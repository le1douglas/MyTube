package le1.mytube.listeners;

import java.util.List;

import le1.mytube.mvpModel.database.song.YouTubeSong;

public interface PlaybackStateListener {

    /**
     * called when the player first start preparing a song
     * @param currentSong current song (only some metadata is available at this point)
     */
    void onPreparing(YouTubeSong currentSong);

    /**
     * called every time it starts loading after {@link #onPreparing(YouTubeSong)}
     * @param currentSong current song (most metadata is available at this point)
     */
    void onLoading(YouTubeSong currentSong);

    /**
     * called every time changes playback position (mainly every second and after {@link le1.mytube.services.musicService.ServiceRepo#seekTo(int)})
     * @param currentTimeInSec current position is seconds
     */
    void onPositionChanged(int currentTimeInSec);

    /**
     * called every time it pauses
     */
    void onPaused();

    /**
     * called every time it starts playing
     * @param currentSongs list of youtube songs at different resolutions (some of them may be only audio or video)
     *                     all of matadata is available at this point
     */
    void onPlaying(List<YouTubeSong> currentSongs);

    /**
     * called at the end of the playback
     */
    void onStopped();

    /**
     * called if any error happens during playback
     */
    void onError(String message);
}