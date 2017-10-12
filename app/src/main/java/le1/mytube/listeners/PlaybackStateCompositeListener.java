package le1.mytube.listeners;

import java.util.ArrayList;
import java.util.List;

import le1.mytube.mvpModel.database.song.YouTubeSong;

/**
 * Created by leone on 12/10/17.
 */

public class PlaybackStateCompositeListener implements PlaybackStateListener {
    private List<PlaybackStateListener> playbackStateListeners = new ArrayList<>();

    public void addListener(PlaybackStateListener playbackStateListener){
        playbackStateListeners.add(playbackStateListener);
    }
    public void removeListener(PlaybackStateListener playbackStateListener){
        playbackStateListeners.remove(playbackStateListener);
    }

    @Override
    public void onLoadingStarted() {
        for (PlaybackStateListener l: playbackStateListeners){
            l.onLoadingStarted();
        }
    }

    @Override
    public void onLoadingFinished() {
        for (PlaybackStateListener l: playbackStateListeners){
            l.onLoadingFinished();
        }
    }

    @Override
    public void onPositionChanged(long currentTimeInMill) {
        for (PlaybackStateListener l: playbackStateListeners){
            l.onPositionChanged(currentTimeInMill);
        }
    }

    @Override
    public void onPaused() {
        for (PlaybackStateListener l: playbackStateListeners){
            l.onPaused();
        }
    }

    @Override
    public void onPlaying(YouTubeSong currentSong) {
        for (PlaybackStateListener l: playbackStateListeners){
            l.onPlaying(currentSong);
        }
    }

    @Override
    public void onStopped() {
        for (PlaybackStateListener l: playbackStateListeners){
            l.onStopped();
        }
    }

    @Override
    public void onError(String message) {
        for (PlaybackStateListener l: playbackStateListeners){
            l.onError(message);
        }
    }
}
