package le1.mytube.listeners;

import android.support.v4.media.MediaMetadataCompat;

import com.google.android.exoplayer2.ExoPlayer;

public interface PlaybackStateCallback {
    void onLoading(ExoPlayer exoPlayer);

    void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat);

    void onPaused();

    void onPlaying();

    void onStopped();
}