package le1.mytube.domain.services.musicService.managers;


import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import le1.mytube.R;
import le1.mytube.domain.listeners.AudioFocusCallback;

/**
 * Wrapper of {@link SimpleExoPlayer} that exposes the least amount of methods possible.
 * Enum because it is a singleton
 */
public enum PlayerManager {
    INSTANCE;
    private SimpleExoPlayer player;
    DataSource.Factory dataSourceFactory;
    DefaultExtractorsFactory extractorsFactory;

    public void buildPlayer(Context context){
        player = ExoPlayerFactory.newSimpleInstance(context.getApplicationContext(),
                new DefaultTrackSelector(
                        new AdaptiveTrackSelection.Factory(
                                new DefaultBandwidthMeter())
                )
        );
        String userAgent = Util.getUserAgent(context, context.getString(R.string.app_name));
        extractorsFactory = new DefaultExtractorsFactory();
        dataSourceFactory = new DefaultDataSourceFactory(context, userAgent);
    }

    /**
     * Register a listener for player events
     *
     * @param listener The {@link Player.EventListener} to register
     */
    public void addEventListener(Player.EventListener listener) {
        player.addListener(listener);
    }

    /**
     * Unregister a listener for player events
     *
     * @param listener The {@link Player.EventListener} to unregister
     */
    public void removeEventListener(Player.EventListener listener) {
        player.removeListener(listener);
    }

    /**
     * @return {@link #player}'s current position in milliseconds
     */
    public int getCurrentPosition() {
        return (int) player.getCurrentPosition();
    }

    /**
     * Start preparing playback
     *
     * @param audioUri Audio only Uri of track
     * @param videoUri Video only Uri of track
     */
    public void prepare(Uri audioUri, Uri videoUri) {
        MediaSource audioSource = new ExtractorMediaSource(audioUri, dataSourceFactory, extractorsFactory, null, null);
        MediaSource videoSource = new ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null);
        MergingMediaSource combinedSources = new MergingMediaSource(audioSource, videoSource);
        player.prepare(combinedSources, true, true);
    }

    /**
     * Start playback
     *
     * @see Player#setPlayWhenReady(boolean)
     */
    public void play() {
        player.setVolume(1);
        player.setPlayWhenReady(true);
    }

    /**
     * Pause playback
     *
     * @see Player#setPlayWhenReady(boolean)
     */
    public void pause() {
        player.setPlayWhenReady(false);
    }

    /**
     * Stop playback
     *
     * @see Player#stop()
     */
    public void stop() {
        player.stop();
    }

    /**
     * Lower playback volume, usually used with {@link AudioFocusCallback#onAudioFocusLossTransientCanDuck()}
     */
    public void duck() {
        player.setVolume(0.2f);
    }

    /**
     * Seek to a particular point in time
     *
     * @param position in milliseconds
     */
    public void seekTo(int position) {
        player.seekTo(position);
    }

    /**
     * @see Player#release()
     */
    public void destroy() {
        player.release();
    }

    /**
     * Bind this player to a view
     *
     * @param playerView The view to bind to.
     */
    public void setPlayerView(SimpleExoPlayerView playerView) {
        playerView.setPlayer(player);
    }

}

