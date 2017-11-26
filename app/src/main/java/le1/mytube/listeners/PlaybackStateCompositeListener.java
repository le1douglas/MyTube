package le1.mytube.listeners;

import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of every {@link PlaybackStateListener}.
 * A composite listener is necessary if more than one listener wants to subscribe to a listener
 *
 * @see PlaybackStateListener
 */
public class PlaybackStateCompositeListener implements PlaybackStateListener {

    private List<PlaybackStateListener> playbackStateListeners = new ArrayList<>();

    /**
     * Add a listener to notify
     * @param playbackStateListener the listener that wants to subscribe
     */
    public void addListener(PlaybackStateListener playbackStateListener){
        playbackStateListeners.add(playbackStateListener);
    }
    public void removeListener(PlaybackStateListener playbackStateListener){
        playbackStateListeners.remove(playbackStateListener);
    }

    @Override
    public void onMetadataLoaded(MediaMetadataCompat metadata) {
        for (PlaybackStateListener l: playbackStateListeners){
            l.onMetadataLoaded(metadata);
        }
    }

    @Override
    public void onLoading() {
        for (PlaybackStateListener l: playbackStateListeners){
            l.onLoading();
        }
    }

    @Override
    public void onPaused() {
        for (PlaybackStateListener l: playbackStateListeners){
            l.onPaused();
        }
    }

    @Override
    public void onPlaying() {
        for (PlaybackStateListener l: playbackStateListeners){
            l.onPlaying();
        }
    }

    @Override
    public void onStopped() {
        for (PlaybackStateListener l: playbackStateListeners){
            l.onStopped();
        }
    }

    @Override
    public void onError(String error) {
        for (PlaybackStateListener l: playbackStateListeners){
            l.onError(error);
        }
    }
}