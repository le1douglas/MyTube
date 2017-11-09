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
    public void onPreparing(YouTubeSong currentSong) {
        for (PlaybackStateListener l: playbackStateListeners){
            l.onPreparing(currentSong);
        }
    }

    @Override
    public void onLoading(YouTubeSong currentSong) {
        for (PlaybackStateListener l: playbackStateListeners){
            l.onLoading(currentSong);
        }
    }


    @Override
    public void onPositionChanged(int currentTimeInSec) {
        for (PlaybackStateListener l: playbackStateListeners){
            l.onPositionChanged(currentTimeInSec);
        }
    }

    @Override
    public void onPaused() {
        for (PlaybackStateListener l: playbackStateListeners){
            l.onPaused();
        }
    }

    @Override
    public void onPlaying(List<YouTubeSong> currentSongs) {
        for (PlaybackStateListener l: playbackStateListeners){
            l.onPlaying(currentSongs);
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
