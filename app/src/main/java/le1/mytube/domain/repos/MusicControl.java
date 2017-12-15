package le1.mytube.domain.repos;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import le1.mytube.data.database.youTubeSong.YouTubeSong;
import le1.mytube.domain.application.MyTubeApplication;
import le1.mytube.domain.listeners.PlaybackStateCompositeListener;
import le1.mytube.domain.listeners.PlaybackStateListener;
import le1.mytube.domain.services.musicService.MusicService;
import le1.mytube.domain.services.musicService.PlayerManager;


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
                    if (state.getErrorMessage() == null)
                        compositeListener.onError("An error occurred");
                    else
                        compositeListener.onError(state.getErrorMessage().toString());
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
            compositeListener.onMetadataLoaded(getCurrentSong());
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
     * Remove a listener subscribed with {@link #addListener(PlaybackStateListener)}
     *
     * @param playbackListener The listener to add
     */
    public void removeListener(PlaybackStateListener playbackListener) {
        compositeListener.removeListener(playbackListener);
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
     *
     * @param youTubeSong The song to be played. Note that only the id is actually required
     */
    public void prepareAndPlay(YouTubeSong youTubeSong) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(MusicService.YOUTUBE_SONG_KEY, youTubeSong);
        mediaController.getTransportControls().prepareFromMediaId(youTubeSong.getId(), bundle);
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
        return (int) mediaController.getPlaybackState().getPosition();
    }

    /**
     * @return the current song as a {@link YouTubeSong} object,
     * may change at any time with changes in metadata
     */
    public YouTubeSong getCurrentSong() {
        return MusicService.getCurrentOrLastSong();
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
        mediaController.getTransportControls().seekTo(progress);
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
