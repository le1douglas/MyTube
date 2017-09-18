package le1.mytube.services;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;

import le1.mytube.R;
import le1.mytube.notification.musicNotification.MusicNotificationConstants;

/**
 * TODO
 */

public class Player implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
    private static final String TAG = "LE1_" + Player.class.getSimpleName();
    private static final long playbackSpeed = (long) 1.0;

    private Context context;
    private AudioManager audioManager;
    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener;
    private Service service;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder playbackState = new PlaybackStateCompat.Builder();

    private MediaPlayer mediaPlayer;


    public Player(Context context, Service service, AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener, MediaSessionCompat mediaSession) {
        this.context = context;
        audioManager = (AudioManager) service.getApplication().getSystemService(Context.AUDIO_SERVICE);
        this.onAudioFocusChangeListener = onAudioFocusChangeListener;
        this.service = service;
        this.mediaSession = mediaSession;
        playbackState.setActions(
                PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
                        PlaybackStateCompat.ACTION_PLAY_FROM_URI |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_STOP);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    void prepareFromUri(Uri uri) {
        Log.d(TAG, "prepare with uri=" + uri.toString());
        setPlaybackState(PlaybackStateCompat.STATE_BUFFERING);
        try {
            mediaPlayer.setDataSource(service.getApplicationContext(), uri);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void prepareFromId(String id) {
        Log.d(TAG, "prepare with path=" + id);
        setPlaybackState(PlaybackStateCompat.STATE_BUFFERING);
        try {
            mediaPlayer.setDataSource(id);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void playPause() {
        Log.d(TAG, "playPause");
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) pause();
            else play();
        }
    }

    void play() {
        Log.d(TAG, "play");
        //TODO AudioFocusRequest for oreo
        int result = audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mediaSession.setActive(true);
            setPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            service.startForeground(MusicNotificationConstants.ID, buildNotification(context, mediaSession));

            mediaPlayer.start();
        }
    }

    void duck() {
        Log.d(TAG, "duck");
    }

    void pause() {
        Log.d(TAG, "pause");
        setPlaybackState(PlaybackStateCompat.STATE_PAUSED);
        service.startForeground(MusicNotificationConstants.ID, buildNotification(context, mediaSession));
        service.stopForeground(false);

        mediaPlayer.pause();
    }

    void stop() {
        Log.d(TAG, "stop");
        //TODO AudioFocusRequest for oreo
        mediaSession.setActive(false);
        audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        setPlaybackState(PlaybackStateCompat.STATE_STOPPED);

        mediaPlayer.stop();
    }

    void skipToNext(){
        Log.d(TAG, "skipToNext");
    }

    void skipToPrevious(){
        Log.d(TAG, "skipToPrevious");
    }


    void seekTo(long position) {
        Log.d(TAG, "seekTo position=" + String.valueOf(position));
    }

    void onDestroy() {
        setPlaybackState(PlaybackStateCompat.STATE_NONE);
        mediaSession.release();
        mediaPlayer.release();
        Log.d(TAG, "onDestroy");
    }


    private void setPlaybackState(Integer state) {
        if (mediaPlayer != null)
            playbackState.setState(state, mediaPlayer.getCurrentPosition(), playbackSpeed);
        else playbackState.setState(state, 0, playbackSpeed);

        mediaSession.setPlaybackState(playbackState.build());
        Log.d(TAG, "setPlaybackState with state=" + playbackState.build().toString());

    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onCompletion");
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        play();
    }

    private Notification buildNotification(Context context, MediaSessionCompat mediaSession) {

        NotificationCompat.Action action = mediaSession.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING
                ? new NotificationCompat.Action(R.drawable.ic_pause_black_24dp, "pause",
                MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                        PlaybackStateCompat.ACTION_PAUSE))
                : new NotificationCompat.Action(R.drawable.ic_play_arrow_black_24dp, "play",
                MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                        PlaybackStateCompat.ACTION_PLAY));


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder

                .setContentIntent(mediaSession.getController().getSessionActivity())

                // Stop the service when the notification is swiped away
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                        PlaybackStateCompat.ACTION_STOP))
                // Make the transport controls visible on the lockscreen
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                // Add an app icon and set its accent color
                // Be careful about the color
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))

                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_fast_rewind_black_24dp, "rewind",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                                PlaybackStateCompat.ACTION_REWIND)))

                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_skip_previous_black_24dp, "previous",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)))


                // pause/play button
                .addAction(action)

                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_skip_next_black_24dp, "next",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT)))

                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_fast_forward_black_24dp, "fast forward",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                                PlaybackStateCompat.ACTION_FAST_FORWARD)))

                // Take advantage of MediaStyle features
                .setStyle(new NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(1, 2, 3)

                        // Add a cancel button
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                                PlaybackStateCompat.ACTION_STOP)))
                .setShowWhen(false)
                .setContentTitle("setContentTitle")
                .setContentText("setContentText")
                .setSubText("setSubText")
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));

// Display the notification and place the service in the foreground
      return builder.build();
    }
}
