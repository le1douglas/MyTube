package le1.mytube;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.SparseArray;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.IOException;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import le1.mytube.mvpModel.songs.YouTubeSong;
import le1.mytube.mvpViews.MainActivity;
import le1.mytube.services.MusicReceiver;
import le1.mytube.services.NotificationReceiver;

import static le1.mytube.mvpViews.MainActivity.handleAudioFocus;


public class MusicService extends Service implements AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    public static MediaPlayer player;
    static NotificationManager mNotificationManager;
    static RemoteViews remoteView;
    static Notification notification;
    static AudioManager.OnAudioFocusChangeListener afChangeListener;
    static AudioManager audioManager;
    ComponentName componentName;

    @Override
    public void onCreate() {

        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnCompletionListener(this);
        player.setOnPreparedListener(this);

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);


        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        componentName = new ComponentName(this, MusicReceiver.class);
        audioManager.registerMediaButtonEventReceiver(componentName);

        afChangeListener = this;
        createNotification();

        Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onAudioFocusChange(int focusChange) {
        if (player != null && handleAudioFocus) {
            switch (focusChange) {
                case (AudioManager.AUDIOFOCUS_LOSS):
                    pauseSong(true);
                    break;

                case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                    // Lower the volume while ducking.
                    player.setVolume(0.2f, 0.2f);
                    break;
                case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
                    pauseSong(false);
                    break;
                case (AudioManager.AUDIOFOCUS_GAIN):
                    // Return the volume to normal and resume if paused.
                    playSong(true);
                    break;
            }

        }
    }

    public static boolean isMusicPlaying() {
        return player != null && player.isPlaying();
    }

    public static void startLocalSong(Context context, YouTubeSong youTubeSong) {
        if (player.isPlaying()) player.stop();
        player.reset();
        Toast.makeText(context, "Local file", Toast.LENGTH_SHORT).show();
        try {
            player.setDataSource(youTubeSong.getPath());
            player.prepareAsync();
            setNotificationState(NotificationState.LOADING);
        } catch (IOException e) {
            setNotificationState(NotificationState.ERROR);
            e.printStackTrace();
        }
    }

    public static void startStreamingSong(final Context context, YouTubeSong youTubeSong) {
        if (player.isPlaying()) player.stop();
        player.reset();
        Toast.makeText(context, "Streaming", Toast.LENGTH_SHORT).show();
        setNotificationState(NotificationState.LOADING);
        new YouTubeExtractor(context) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                if (ytFiles != null) {
                    String downloadUrl = ytFiles.get(140).getUrl();
                    System.out.println(downloadUrl);
                    try {

                        player.setDataSource(downloadUrl);
                        player.prepareAsync();

                    } catch (Exception e) {
                        setNotificationState(NotificationState.ERROR);
                        e.printStackTrace();
                    }

                }
            }
        }.extract("http://youtube.com/watch?v=" + youTubeSong.getId(), false, false);

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        audioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        player.start();
        setNotificationState(NotificationState.PLAYING);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        MusicService.this.stopSelf();
    }

    public static void playSong(boolean gainAudioFocus) {
        if (player != null) {
            if (gainAudioFocus) {
                audioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            }
            player.start();
            player.setVolume(1f, 1f);
            setNotificationState(NotificationState.PLAYING);
        } else setNotificationState(NotificationState.ERROR);

    }

    public static void pauseSong(boolean abandonAudioFocus) {
        if (player != null && player.isPlaying()) {
            if (abandonAudioFocus) {
                audioManager.abandonAudioFocus(afChangeListener);
            }
            player.pause();
            setNotificationState(NotificationState.PAUSED);
        } else setNotificationState(NotificationState.ERROR);

    }


    private void createNotification() {

        Toast.makeText(this, "creating notif", Toast.LENGTH_SHORT).show();



        remoteView = new RemoteViews(MusicService.this.getPackageName(), R.layout.notification_view);


        //play button
        final Intent play = new Intent(MusicService.this, NotificationReceiver.class);
        play.putExtra("NOT", "play");
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(MusicService.this, 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.btn1, playPendingIntent);
        remoteView.setTextViewText(R.id.btn1, "test");

        //stop button
        final Intent stop = new Intent(MusicService.this, NotificationReceiver.class);
        stop.putExtra("NOT", "stop");
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(MusicService.this, 1, stop, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.btn2, stopPendingIntent);
        remoteView.setTextViewText(R.id.btn2, "Stop");

        Intent notificationIntent = new Intent(MusicService.this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(MusicService.this, 0, notificationIntent, 0);

           notification = new Notification.Builder(getApplicationContext())
                    .setSmallIcon(R.mipmap.ic_launcher)
                 //   .setContent(remoteView)
                    .setContentIntent(notificationPendingIntent)
                    .build();



            startForeground(666, notification);

    }

    private enum NotificationState {
        LOADING,
        PLAYING,
        PAUSED,
        ERROR
    }

    private static void setNotificationState(NotificationState state) {
        switch (state) {
            case LOADING:
                remoteView.setTextViewText(R.id.btn1, "loading");
                remoteView.setTextViewText(R.id.title, "PLACEHOLDER TITLE");
                remoteView.setBoolean(R.id.btn1, "setEnabled", false);
                break;
            case PLAYING:
                remoteView.setBoolean(R.id.btn1, "setEnabled", true);
                remoteView.setTextViewText(R.id.btn1, "playing");
                break;
            case PAUSED:
                remoteView.setTextViewText(R.id.btn1, "paused");
                break;
            case ERROR:
                remoteView.setTextViewText(R.id.btn1, "error");
                remoteView.setTextViewText(R.id.title, "Something went wrong");
                remoteView.setBoolean(R.id.btn1, "setEnabled", false);
                break;
        }
        //TODO check if necessary
        mNotificationManager.notify(666, notification);

    }

    @Override
    public void onDestroy() {
        audioManager.unregisterMediaButtonEventReceiver(componentName);
        audioManager.abandonAudioFocus(afChangeListener);
        player.stop();
        player.reset();
        player.release();
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
