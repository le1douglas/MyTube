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
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.IOException;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import le1.mytube.receivers.MusicReceiver;
import le1.mytube.receivers.NotificationReceiver;

import static le1.mytube.MainActivity.modalitaPorno;


public class MusicService extends Service {

    public static MediaPlayer player;
    static NotificationManager mNotificationManager;
    static RemoteViews remoteView;
    static Notification notification;
    static AudioManager.OnAudioFocusChangeListener afChangeListener;
    static AudioManager audioManager;
    ComponentName componentName;
    YouTubeSong youTubeSong;

    @Override
    public void onCreate() {
        createNotification();

        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        componentName = new ComponentName(this, MusicReceiver.class);
        audioManager.registerMediaButtonEventReceiver(componentName);

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
                        if (player != null && !modalitaPorno) {
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

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        MusicService.startSong(new YouTubeSong(intent.getStringExtra("title"), intent.getStringExtra("videoId"), null, null, null), this);

        return super.onStartCommand(intent, flags, startId);

    }

    public static void startSong(YouTubeSong youTubeSong, Context context) {

        if (player.isPlaying()) player.stop();
        player.reset();
        remoteView.setTextViewText(R.id.btn1, "loading");
        remoteView.setTextViewText(R.id.title, youTubeSong.getTitle());
        remoteView.setBoolean(R.id.btn1, "setEnabled", false);
        mNotificationManager.notify(666, notification);

        if (youTubeSong.getPath() == null) {
            Toast.makeText(context, "Streaming", Toast.LENGTH_SHORT).show();

            new YouTubeExtractor(context) {
                @Override
                public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                    if (ytFiles != null) {
                        String downloadUrl = ytFiles.get(140).getUrl();
                        System.out.println(downloadUrl);
                        try {
                            player.setDataSource(downloadUrl);
                            player.prepareAsync();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }.extract("http://youtube.com/watch?v=" + youTubeSong.getId(), false, false);

        } else {
            Toast.makeText(context, "Local file", Toast.LENGTH_SHORT).show();
            try {
                player.setDataSource(youTubeSong.getPath());
                player.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }

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

    public void createNotification() {
        Intent notificationIntent = new Intent(MusicService.this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(MusicService.this, 0, notificationIntent, 0);

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

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        startForeground(666, notification);

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
