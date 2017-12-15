package le1.mytube.listeners;

import le1.mytube.mvpModel.MusicControl;
import le1.mytube.mvpModel.database.song.YouTubeSong;


/**
 * User friendly listener of the player changes in state.
 * Remember to call {@link MusicControl#addListener(PlaybackStateListener)} when you use this listener
 */
public interface PlaybackStateListener {

    /**
     * Called when the metadata loads, can be called multiple times.
     *
     * @param youTubeSong the current {@link YouTubeSong} encapsulating all of the metadata
     *
     * @see MusicControl#getCurrentSong()
     */
    void onMetadataLoaded(YouTubeSong youTubeSong);

    /**
     * Called when the player starts loading either after
     * {@link MusicControl#prepareAndPlay(le1.mytube.mvpModel.database.song.YouTubeSong)} or {@link MusicControl#seekTo(int)}
     */
    void onLoading();


    /**
     * Called when the player starts playing
     *
     * @see MusicControl#play()
     */
    void onPlaying();

    /**
     * Called when player pauses, usually after {@link #onPlaying()}
     *
     * @see MusicControl#pause()
     */
    void onPaused();


    /**
     * Called when player finishes playing a media and has no other media in queue
     *
     * @see MusicControl#stop()
     */
    void onStopped();


    /**
     * Called if something goes wrong during playback
     *
     * @param error A user friendly error message
     */
    void onError(String error);
}
