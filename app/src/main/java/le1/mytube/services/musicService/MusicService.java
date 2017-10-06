package le1.mytube.services.musicService;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import le1.mytube.MyTubeApplication;
import le1.mytube.R;
import le1.mytube.listeners.PlaybackStateCallback;
import le1.mytube.mvpModel.database.song.YouTubeSong;
import le1.mytube.notification.musicNotification.MusicNotification;


public class MusicService extends MediaBrowserServiceCompat implements AudioManager.OnAudioFocusChangeListener {
    private static final String TAG = ("LE1_" + MusicService.class.getSimpleName());
    private static final int BUTTON_RECEIVER_REQUEST_CODE = 33;
    private static final long PLAYBACK_SPEED_NORMAL = 1;

    private static MediaSessionCompat mediaSession;
    private static AudioManager audioManager;

    private PlaybackStateCompat.Builder playbackState = new PlaybackStateCompat.Builder();

    private static Context context;
    private static Service service;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mediaSession = new MediaSessionCompat(this, TAG);
        mediaSession.setCallback(new MediaSessionCallback());
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);
        mediaSession.setMediaButtonReceiver(PendingIntent.getBroadcast(this, BUTTON_RECEIVER_REQUEST_CODE, mediaButtonIntent, 0));
        setSessionToken(mediaSession.getSessionToken());

        playbackState.setActions(
                PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_STOP);


        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        context = this.getApplicationContext();
        service = this;
    }


    public boolean requestAudioFocus() {
        return audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public void abandonAudioFocus() {
        audioManager.abandonAudioFocus(this);
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot(getString(R.string.app_name), null);

    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(null);
    }

    public void setMetadata(YouTubeSong youTubeSong, PlaybackStateCallback callback) {
        MediaMetadataCompat.Builder metadata = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, youTubeSong.getTitle())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, youTubeSong.getId())
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, youTubeSong.getId())
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ART, BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));

        if (youTubeSong.getImage() != null)
            metadata.putString(MediaMetadataCompat.METADATA_KEY_ART_URI, youTubeSong.getImage().toString());
        if (youTubeSong.getDuration() != null)
            metadata.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, youTubeSong.getDuration());

        mediaSession.setMetadata(metadata.build());
        callback.onMetadataChanged(metadata.build());
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        Log.d(TAG, "onAudioFocusChange with focusChange=" + focusChange);
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                ((MyTubeApplication) getApplication()).getServiceRepo().pause();
                return;
            case AudioManager.AUDIOFOCUS_LOSS:
                Toast.makeText(this, "AUDIOFOCUS_LOST", Toast.LENGTH_SHORT).show();
                ((MyTubeApplication) getApplication()).getServiceRepo().pause();
                return;
            case AudioManager.AUDIOFOCUS_GAIN:
                //if (ServiceController.getInstance(this).getPlaybackState()== PlaybackStateCompat.STATE_PAUSED)
                ((MyTubeApplication) getApplication()).getServiceRepo().play();
                return;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                ((MyTubeApplication) getApplication()).getServiceRepo().duck();
                return;
            default:
                break;
        }
    }

    public void setMediaSessionActive(boolean active) {
        mediaSession.setActive(active);
    }

    public void setConnectedToNoisyReceiver(boolean connect) {
        BroadcastReceiver noisyReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ((MyTubeApplication) getApplication()).getServiceRepo().pause();
            }
        };

        if (connect) {
            context.registerReceiver(noisyReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
        } else {
            try {
                context.unregisterReceiver(noisyReceiver);
            } catch (Exception ignored) {
            }
        }
    }

    public void setPlaybackState(int state, long playerCurrentPosition) {
        Log.d(TAG, "setPlaybackState with state=" + playbackState.build().toString());

        playbackState.setState(state, playerCurrentPosition, PLAYBACK_SPEED_NORMAL);
        mediaSession.setPlaybackState(playbackState.build());
        MusicNotification.updateNotification(context, service, mediaSession, state);
    }

    public int getPlaybackState() {
        return mediaSession.getController().getPlaybackState().getState();
    }

    private class MediaSessionCallback extends MediaSessionCompat.Callback {

        @Override
        public void onPause() {
            super.onPause();
            ((MyTubeApplication) getApplication()).getServiceRepo().pause();
        }

        @Override
        public void onPlay() {
            super.onPlay();
            ((MyTubeApplication) getApplication()).getServiceRepo().play();

        }

        @Override
        public void onStop() {
            super.onStop();
            ((MyTubeApplication) getApplication()).getServiceRepo().stop();

        }
    }
}
