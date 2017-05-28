package le1.mytube;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.SparseArray;
import android.widget.RemoteViews;

import java.io.IOException;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;


public class MusicService extends Service {

    public static MediaPlayer player;
    static NotificationManager mNotificationManager;
    static RemoteViews remoteView;
    static Notification notification;
    AudioManager.OnAudioFocusChangeListener afChangeListener;
    AudioManager audioManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

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
                player.start();
                remoteView.setTextViewText(R.id.btn1, "playing");
                mNotificationManager.notify(666, notification);
            }
        });

        afChangeListener =
                new AudioManager.OnAudioFocusChangeListener() {
                    public void onAudioFocusChange(int focusChange) {
                        if (player != null) {
                            if (focusChange == AudioManager.AUDIOFOCUS_LOSS && !MainActivity.modalitaPorno) {
                                player.pause();
                                remoteView.setTextViewText(R.id.btn1, "paused");
                                mNotificationManager.notify(666, notification);
                            }
                        }
                    }
                };



        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopForeground(false);
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
        mNotificationManager.notify(666, notification);

        new YouTubeExtractor(context) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                if (ytFiles != null) {
                    int itag = 140;
                    String downloadUrl = ytFiles.get(itag).getUrl();
                    System.out.println(downloadUrl);
                    try {
                        player.setDataSource(downloadUrl);
                        player.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.extract("http://youtube.com/watch?v=" + videoId, false, false);


    }


    @Override
    public void onDestroy() {
        this.audioManager.abandonAudioFocus(afChangeListener);
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
