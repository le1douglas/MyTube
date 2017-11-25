package le1.mytube.ui.musicPlayer;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import le1.mytube.base.BaseContract;

public class MusicPlayerPresenter extends AndroidViewModel implements MusicPlayerContract.ViewModel {
    private MusicPlayerContract.View contractView;

    public MusicPlayerPresenter(Application application) {
        super(application);
    }

    @Override
    public void linkPlayerToView(SimpleExoPlayerView playerView) {
        if (playerView.getPlayer() == null);

    }

    @Override
    public void setContractView(BaseContract.View contractView) {
        this.contractView = (MusicPlayerContract.View) contractView;
    }

}

