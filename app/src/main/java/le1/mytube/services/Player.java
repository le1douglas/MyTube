package le1.mytube.services;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

/**
 * TODO
 */

public class Player {
    private static final String TAG = "LE1_" + Player.class.getSimpleName();
    private static final long playbackSpeed = (long) 1.0;

    private AudioManager audioManager;
    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener;
    private Service service;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder playbackState = new PlaybackStateCompat.Builder();

    MediaPlayer mediaPlayer;


    public Player(Service service, AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener, MediaSessionCompat mediaSession) {
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
    }

    void prepare(Uri uri) {
        Log.d(TAG, "prepare with uri=" + uri.toString());
        setPlaybackState(PlaybackStateCompat.STATE_BUFFERING);
    }

    void prepare(String id) {
        Log.d(TAG, "prepare with id=" + id);

        setPlaybackState(PlaybackStateCompat.STATE_BUFFERING);
    }

    void playPause() {
        Log.d(TAG, "playPause");
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) play();
            else pause();
        }
    }

    void play() {
        Log.d(TAG, "play");
        //TODO AudioFocusRequest for oreo
        int result = audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mediaSession.setActive(true);
            setPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            //actually play music
        }
    }

    void duck() {
        Log.d(TAG, "duck");
    }

    void pause() {
        Log.d(TAG, "pause");
        setPlaybackState(PlaybackStateCompat.STATE_PAUSED);
    }

    void stop() {
        Log.d(TAG, "stop");
        //TODO AudioFocusRequest for oreo
        mediaSession.setActive(false);
        audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        setPlaybackState(PlaybackStateCompat.STATE_STOPPED);
    }

    void seekTo(long position) {
        Log.d(TAG, "seekTo position=" + String.valueOf(position));
    }

    void onDestroy() {
        mediaSession.release();
        setPlaybackState(PlaybackStateCompat.STATE_NONE);
        Log.d(TAG, "onDestroy");
    }


    private void setPlaybackState(Integer state) {
        if (mediaPlayer != null)
            playbackState.setState(state, mediaPlayer.getCurrentPosition(), playbackSpeed);
        else playbackState.setState(state, 0, playbackSpeed);

        mediaSession.setPlaybackState(playbackState.build());
        Log.d(TAG, "setPlaybackState with state="+ playbackState.build().toString());

    }
}
