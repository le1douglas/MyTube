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
import android.os.Build;
import android.os.IBinder;
import android.util.SparseArray;
import android.widget.RemoteViews;

import java.io.IOException;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

import static le1.mytube.MainActivity.modalitaPorno;


public class MusicService extends Service {

    public static MediaPlayer player;
    static NotificationManager mNotificationManager;
    static RemoteViews remoteView;
    static Notification notification;
    static AudioManager.OnAudioFocusChangeListener afChangeListener;
    static AudioManager audioManager;
    ComponentName componentName;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        componentName = new ComponentName(this, MusicReceiver.class);
        audioManager.registerMediaButtonEventReceiver(componentName);


        Intent notificationIntent = new Intent(MusicService.this, SearchActivity.class);
        notificationIntent.putExtra("FROM", "notification");
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(MusicService.this, 0, notificationIntent, 0);

        remoteView = new RemoteViews(MusicService.this.getPackageName(), R.layout.notificationview);

        final Intent play = new Intent(MusicService.this, NotificationClickHandler.class);
        play.putExtra("NOT", "0");
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(MusicService.this, 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.btn1, playPendingIntent);
        remoteView.setTextViewText(R.id.btn1, "test");

        if (Build.VERSION.SDK_INT >= 24) {
            notification = new Notification.Builder(getApplicationContext())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setCustomContentView(remoteView)
                    .setStyle(new Notification.DecoratedMediaCustomViewStyle())
                    .setColor(12121212)
                    .setContentIntent(notificationPendingIntent)
                    .build();
        } else {
            notification = new Notification.Builder(getApplicationContext())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContent(remoteView)
                    .setContentIntent(notificationPendingIntent)
                    .build();
        }

        startForeground(666, notification);

        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                audioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                remoteView.setBoolean(R.id.btn1, "setEnabled", true);
                player.start();
                remoteView.setTextViewText(R.id.btn1, "playing");
                mNotificationManager.notify(666, notification);
            }
        });


        afChangeListener =
                new AudioManager.OnAudioFocusChangeListener() {
                    public void onAudioFocusChange(int focusChange) {
                        if (player != null&&!modalitaPorno) {
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
                };


        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                MusicService.this.stopSelf();
            }
        });
        mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        MusicService.startSong(intent.getStringExtra("videoId"), intent.getStringExtra("title"), this);

        return super.onStartCommand(intent, flags, startId);

    }

    public static void startSong(String videoId, String videoTitle, Context context) {

        if (player.isPlaying()) player.stop();
        player.reset();
        remoteView.setTextViewText(R.id.btn1, "loading");
        remoteView.setTextViewText(R.id.title, videoTitle);
        remoteView.setBoolean(R.id.btn1, "setEnabled", false);
        mNotificationManager.notify(666, notification);

        new YouTubeExtractor(context) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                if (ytFiles != null) {
                    int itag = 140;
                    String downloadUrl = ytFiles.get(itag).getUrl();
                    System.out.println(downloadUrl);
                    try {
                        player.reset();
                        player.setDataSource(downloadUrl);
                        player.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.extract("http://youtube.com/watch?v=" + videoId, false, false);


    }

    public static void pauseSong(boolean abandonAudioFocus) {
        if (player != null && player.isPlaying()) {
            if (abandonAudioFocus) {
                audioManager.abandonAudioFocus(afChangeListener);
            }
            player.pause();
            remoteView.setTextViewText(R.id.btn1, "paused");
            mNotificationManager.notify(666, notification);

        }
    }

    public static void playSong(boolean gainAudioFocus) {
        if (player != null) {
            if (gainAudioFocus) {
                audioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            }
            player.start();
            player.setVolume(1f, 1f);
            remoteView.setTextViewText(R.id.btn1, "playing");
            mNotificationManager.notify(666, notification);
        }

    }


    @Override
    public void onDestroy() {
        audioManager.unregisterMediaButtonEventReceiver(componentName);
        audioManager.abandonAudioFocus(afChangeListener);
        player.stop();
        player.reset();
        player.release();
        stopForeground(false);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
