package le1.mytube.services;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import java.io.IOException;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import le1.mytube.mvpModel.Repo;
import le1.mytube.mvpModel.database.song.YouTubeSong;
import le1.mytube.notification.musicNotification.MusicNotification;
import le1.mytube.notification.musicNotification.MusicNotificationImpl;

import static le1.mytube.services.MusicServiceConstants.ACTION_FAST_FORWARD;
import static le1.mytube.services.MusicServiceConstants.ACTION_NEXT;
import static le1.mytube.services.MusicServiceConstants.ACTION_PAUSE;
import static le1.mytube.services.MusicServiceConstants.ACTION_PLAY;
import static le1.mytube.services.MusicServiceConstants.ACTION_PLAY_PAUSE;
import static le1.mytube.services.MusicServiceConstants.ACTION_PREVIOUS;
import static le1.mytube.services.MusicServiceConstants.ACTION_REWIND;
import static le1.mytube.services.MusicServiceConstants.ACTION_START_LOCAL;
import static le1.mytube.services.MusicServiceConstants.ACTION_START_STREAMING;
import static le1.mytube.services.MusicServiceConstants.ACTION_STOP;
import static le1.mytube.services.MusicServiceConstants.KEY_SONG;


@TargetApi(Build.VERSION_CODES.KITKAT)
@SuppressWarnings("deprecation")
public class MusicService extends Service implements MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {


    static MediaPlayer player;
    static AudioManager audioManager;
    MusicNotification notification;

    Repo repo;
    ComponentName eventReceiver;

    @Override
    public void onCreate() {
        repo = new Repo(this);
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        eventReceiver = new ComponentName(getPackageName(), MusicReceiver.class.getName());
        audioManager.registerMediaButtonEventReceiver(eventReceiver);

        notification = new MusicNotificationImpl();
        notification.buildNotification(this, this);

        super.onCreate();
    }

    private void log(String message){
        Log.d("LE1_DEBUG SERVICE" , message);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log("onStartCommand");
        if (intent != null && intent.getAction() != null) {
            log("intent not null");
            String action = intent.getAction();
            String[] songInfo = intent.getStringArrayExtra(KEY_SONG);
            if (songInfo != null) {
                YouTubeSong youTubeSong = new YouTubeSong.Builder(songInfo[0], songInfo[1])
                        .path(songInfo[2])
                        .startTime(Integer.parseInt(songInfo[3]))
                        .endTime(Integer.parseInt(songInfo[4])).build();

                switch (action) {
                    case ACTION_START_LOCAL:
                        log("ACTION_START_LOCAL");
                        startLocalSong(youTubeSong);
                        break;
                    case ACTION_START_STREAMING:
                        log("ACTION_START_STREAMING");
                        startStreamingSong(youTubeSong);
                        break;
                }
            }

            switch (action) {
                case ACTION_PLAY_PAUSE:
                    log("ACTION_PLAY_PAUSE");
                    if (player != null && player.isPlaying()) pauseSong(repo.getAudioFocus());
                    else playSong(repo.getAudioFocus(), null);
                    break;
                case ACTION_PLAY:
                    log("ACTION_PLAY");
                    playSong(repo.getAudioFocus(), null);
                    break;
                case ACTION_PAUSE:
                    log("ACTION_PAUSE");
                    pauseSong(repo.getAudioFocus());
                    break;
                case ACTION_REWIND:
                    log("ACTION_REWIND");
                    player.seekTo(5000);
                    break;
                case ACTION_FAST_FORWARD:
                    log("ACTION_FAST_FORWARD");
                    player.seekTo(10000);
                    break;
                case ACTION_NEXT:
                    log("ACTION_NEXT");
                    break;
                case ACTION_PREVIOUS:
                    log("ACTION_PREVIOUS");
                    player.seekTo(0);
                    break;
                case ACTION_STOP:
                    log("ACTION_STOP");
                    this.stopSelf();
                    break;
                default:
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private synchronized void startLocalSong(YouTubeSong youTubeSong) {
        log("startLocalSong");
        //REQUIRED path
        notification.setLoading();
        if (player.isPlaying()) player.stop();
        player.reset();
        Toast.makeText(this, "Local file", Toast.LENGTH_SHORT).show();

        try {
            player.setDataSource(youTubeSong.getPath());
            player.prepare();
            playSong(repo.getAudioFocus(), youTubeSong);
            log("playSong called");
        } catch (IOException e) {
            notification.setError();
            e.printStackTrace();
        }
    }


    boolean extracting = false;

    private synchronized void startStreamingSong(final YouTubeSong youTubeSong) {
        log("startStreamingSong");
        //REQUIRED id
        if (player.isPlaying()) player.stop();
        player.reset();
        notification.setLoading();
        YouTubeExtractor extractor = new YouTubeExtractor(this.getApplicationContext()) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                extracting = false;
                if (ytFiles != null) {
                    try {
                        log("onExtractionComplete");
                        String downloadUrl = ytFiles.get(140).getUrl();
                        System.out.println(downloadUrl);
                        player.setDataSource(downloadUrl);
                        player.prepare();
                        playSong(repo.getAudioFocus(), youTubeSong);
                        log("playSong called");
                    } catch (Exception e) {
                        notification.setError();
                        e.printStackTrace();
                    }

                }
            }
        };

        if (!extracting) {
            extracting = true;
            extractor.extract("http://youtube.com/watch?v=" + youTubeSong.getId(), false, false);
        } else {
            Log.w("MUSIC SERVICE", "already extracting something else");
        }
    }

    public void playSong(Boolean handleAudioFocus, YouTubeSong youTubeSong) {
        log("playSong");
        if (player != null) {
            log("audiofocus = " + String.valueOf(handleAudioFocus));
            if (handleAudioFocus) {
                audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            }
            if (!notification.isVisible(this)) {
                log("start notification");
                notification.start(this, notification.getNotification());
            }
            notification.setPlaying(youTubeSong);
            player.start();
            player.setOnCompletionListener(this);
            player.setVolume(1f, 1f);
        } else {notification.setError();
            log("mediaplayer is null");}

    }

    public void pauseSong(Boolean handleAudioFocus) {
        log("pauseSong");
        if (player != null) {
            if (player.isPlaying()) {
                log("audiofocus = " + String.valueOf(handleAudioFocus));
                if (handleAudioFocus) {
                    audioManager.abandonAudioFocus(this);
                }
                player.pause();
                notification.setDismissable();
                notification.setPaused();
            }
        } else {notification.setError();
            log("mediaplayer is null");
        }

    }


    public void duckSong() {
        log("duckSong");
        if (player != null) {
            if (player.isPlaying()) {
                player.setVolume(0.2f, 0.2f);
            }
        } else {notification.setError();
            log("mediaplayer is null");
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        log("onCompletion");
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        log("onDestroy");
        if (notification.isVisible(this)) notification.stop();
        audioManager.abandonAudioFocus(this);
        audioManager.unregisterMediaButtonEventReceiver(eventReceiver);
        player.release();
        super.onDestroy();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        log("onAudioFocusChange");
        if (player != null && repo.getAudioFocus()) {
            switch (focusChange) {
                case (AudioManager.AUDIOFOCUS_GAIN):
                    log("AUDIOFOCUS_GAIN");
                    playSong(repo.getAudioFocus(),null);
                    break;
                case (AudioManager.AUDIOFOCUS_LOSS):
                    log("AUDIOFOCUS_LOSS");
                    pauseSong(true);
                    break;
                case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                    log("AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                    duckSong();
                    break;
                case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
                    log("AUDIOFOCUS_LOSS_TRANSIENT");
                    pauseSong(false);
                    break;
            }

        }

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (!player.isPlaying()) this.stopSelf();
        log("onTaskRemoved");
    }

}
