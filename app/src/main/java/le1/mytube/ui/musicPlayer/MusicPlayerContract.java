package le1.mytube.ui.musicPlayer;

import android.support.annotation.NonNull;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import le1.mytube.base.BaseContract;
import le1.mytube.mvpModel.database.song.YouTubeSong;

public interface MusicPlayerContract extends BaseContract {

    interface View extends BaseContract.View {
    }

    interface ViewModel extends BaseContract.ViewModel {
        @Override
        void setContractView(BaseContract.View contractView);

        void linkPlayerToView(@NonNull SimpleExoPlayerView playerView);

        /**
         * Starts song if it's not already in the player
         * @param youTubeSong The {@link YouTubeSong} to play
         * @return true if song is being started, false otherwise
         */
        boolean startSongIfItsDifferent(YouTubeSong youTubeSong);
    }
}