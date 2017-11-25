package le1.mytube.ui.musicPlayer;

import android.support.annotation.NonNull;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import le1.mytube.base.BaseContract;

public interface MusicPlayerContract extends BaseContract {

    interface View extends BaseContract.View {
        void onSetPlayerView(SimpleExoPlayerView exoPlayerView);
    }

    interface ViewModel extends BaseContract.ViewModel {
        @Override
        void setContractView(BaseContract.View contractView);

        void linkPlayerToView(@NonNull SimpleExoPlayerView playerView);
    }
}