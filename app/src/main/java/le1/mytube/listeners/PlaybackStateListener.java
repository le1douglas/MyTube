package le1.mytube.listeners;

import java.util.List;

import le1.mytube.mvpModel.database.song.YouTubeSong;

public interface PlaybackStateListener {
    void onLoadingStarted();

    void onLoadingFinished();

    void onPositionChanged(long currentTimeInMill);

    void onPaused();

    void onPlaying(YouTubeSong currentSong, List<String> resolutions);

    void onStopped();

    void onError(String message);
}