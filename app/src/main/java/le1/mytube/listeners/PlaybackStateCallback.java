package le1.mytube.listeners;

import android.support.v4.media.MediaMetadataCompat;

import com.google.android.exoplayer2.ExoPlayer;

public interface PlaybackStateCallback {
    void onLoadingStarted(ExoPlayer exoPlayer);

    void onLoadingFinished();

    void onMetadataChanged(MediaMetadataCompat mediaMetadataCompat);

    void onPositionChanged(long currentTimeinMill);

    void onPaused();

    void onPlaying();

    void onStopped();
}