package le1.mytube.services.musicService;

import android.support.annotation.NonNull;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import le1.mytube.listeners.PlaybackStateCallback;
import le1.mytube.mvpModel.database.song.YouTubeSong;

/**
 * Created by leone on 05/10/17.
 */

public interface ServiceRepo {

    void prepareStreaming(@NonNull YouTubeSong youTubeSong);

    void prepareLocal(@NonNull YouTubeSong youTubeSong);

    void play();

    void pause();

    void playOrPause();

    void stop();

    void duck();

    void seekTo(long position);

    void setCallback(PlaybackStateCallback playbackStateCallback);

    void setView(SimpleExoPlayerView exoPlayerView);
}
