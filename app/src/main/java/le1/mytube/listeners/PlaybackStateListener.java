package le1.mytube.listeners;

import java.util.List;

import le1.mytube.mvpModel.database.song.YouTubeSong;

public interface PlaybackStateListener {
    void onLoadingStarted(YouTubeSong currentSong);

    void onPositionChanged(int currentTimeInSec);

    void onPaused();

    void onPlaying(List<YouTubeSong> currentSongs);

    void onStopped();

    void onError(String message);
}