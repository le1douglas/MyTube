package le1.mytube.mvpModel;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import le1.mytube.application.MyTubeApplication;
import le1.mytube.listeners.PlaybackStateCompositeListener;
import le1.mytube.listeners.PlaybackStateListener;
import le1.mytube.mvpModel.database.song.YouTubeSong;
import le1.mytube.services.musicService.MusicService;
import le1.mytube.services.musicService.PlayerManager;


/**
 * Use this class for any interaction with media playback,
 * e.g media control, user friendly callbacks, retrieving of playback information
 */
public class MusicControl {
    private static final String TAG = "LE1_MusicControl";
    private Context context;

    /**
     * A collection of all the {@link PlaybackStateListener} subscribed
     */
    private PlaybackStateCompositeListener compositeListener;

    /**
     * Used to connect with {@link MusicService}
     */
    private MediaBrowserCompat mediaBrowserCompat;

    /**
     * Used to actually control playback,
     * usable only after connecting with {@link #mediaBrowserCompat}
     */
    private MediaControllerCompat mediaController;

    /**
     * Intent used to start and stop {@link MusicService}
     */
    private Intent musicServiceIntent;

    /**
     * Forward any change in playback to {@link #compositeListener}
     */
    private MediaControllerCompat.Callback mediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            switch (state.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    compositeListener.onPlaying();
                    break;
                case PlaybackStateCompat.STATE_BUFFERING:
                    compositeListener.onLoading();
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                    compositeListener.onPaused();
                    break;
                case PlaybackStateCompat.STATE_STOPPED:
                    compositeListener.onStopped();
                    break;
                case PlaybackStateCompat.STATE_ERROR:
                    compositeListener.onError(state.getErrorMessage().toString());
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
            compositeListener.onMetadataLoaded(metadata);
        }
    };

    /**
     * Callback of {@link #mediaBrowserCompat}
     */
    private MediaBrowserCompat.ConnectionCallback connectionCallback = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            super.onConnected();
            try {
                Log.d(TAG, "onConnected: success");
                mediaController = new MediaControllerCompat(context, mediaBrowserCompat.getSessionToken());
                mediaController.registerCallback(mediaControllerCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    };


    /**
     * Don't use this constructor directly.
     * Use {@link MyTubeApplication#getMusicControl()} instead
     *
     * @param context Application context
     */
    public MusicControl(Context context) {
        musicServiceIntent = new Intent(context.getApplicationContext(), MusicService.class);
        compositeListener = new PlaybackStateCompositeListener();
        mediaBrowserCompat = new MediaBrowserCompat(context,
                new ComponentName(context.getApplicationContext(), MusicService.class), connectionCallback, null);
        this.context = context;
    }

    /**
     * @param playerView the view in which to display media
     */
    public void setPlayerView(SimpleExoPlayerView playerView) {
        PlayerManager.getInstance(context).setPlayerView(playerView);
    }

    /**
     * Add a listener that will react to playback events
     *
     * @param playbackListener The listener to add
     */
    public void addListener(PlaybackStateListener playbackListener) {
        compositeListener.addListener(playbackListener);
    }

    /**
     * Connects to {@link MusicService}
     */
    public void connect() {
        if (!mediaBrowserCompat.isConnected()) {
            context.startService(musicServiceIntent);
            mediaBrowserCompat.connect();
        }
    }

    /**
     * Close connection and perform all the clean up.
     * {@link MediaControllerCompat#unregisterCallback(MediaControllerCompat.Callback)}
     * it's just a ui callback so it's the first to be eliminated
     * {@link MediaBrowserCompat#disconnect()} disconnects from {@link MusicService}
     * finally, {@link Context#stopService(Intent)} kills the service
     */
    public void disconnect() {
        mediaController.unregisterCallback(mediaControllerCallback);
        mediaBrowserCompat.disconnect();
        context.stopService(musicServiceIntent);

    }

    /**
     * @return true if connected to {@link MusicService}, false otherwise
     */
    public boolean isConnected() {
        return mediaBrowserCompat.isConnected();
    }


    /**
     * Prepare playback, load media and then start playing (as if called with {@link #play()})
     * @param youTubeSong The song to be played. Note that only the id is actually required
     */
    public void prepareAndPlay(YouTubeSong youTubeSong) {
        mediaController.getTransportControls().prepareFromMediaId(youTubeSong.getId(), null);
    }

    /**
     * Start playback.
     * Only works if song was prepared with {@link #prepareAndPlay(YouTubeSong)}
     */
    public void play() {
        mediaController.getTransportControls().play();
    }

    /**
     * Pause playback.
     * Only works if song was prepared with {@link #prepareAndPlay(YouTubeSong)}
     */
    public void pause() {
        mediaController.getTransportControls().pause();
    }

    /**
     * Stops playback.
     * Can be called at any time.
     * To resume playback, {@link #prepareAndPlay(YouTubeSong)} must be called
     */
    public void stop() {
        mediaController.getTransportControls().stop();
    }


    /**
     * @return the current playback position in milliseconds
     */
    public int getCurrentPosition() {
        return PlayerManager.getInstance(context).getCurrentPosition() * 1000;
    }

    /**
     * @return the current {@link MediaMetadataCompat} of the current playback.
     * May change at any moment
     */
    public MediaMetadataCompat getMetadata() {
        return mediaController.getMetadata();
    }


    /**
     * @return The current {@link PlaybackStateCompat} state
     */
    public int getPlaybackState() {
        return mediaController.getPlaybackState().getState();
    }

    /**
     * Seek to a new position in time
     *
     * @param progress Number of milliseconds where to start playback from
     */
    public void seekTo(int progress) {
        mediaController.getTransportControls().seekTo(progress / 1000);
    }

    /**
     * Toggle between playing and paused state
     */
    public void playOrPause() {
        if (mediaController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING)
            mediaController.getTransportControls().pause();
        else
            mediaController.getTransportControls().play();
    }
}
