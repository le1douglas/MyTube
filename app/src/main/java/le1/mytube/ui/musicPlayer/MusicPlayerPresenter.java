package le1.mytube.ui.musicPlayer;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import le1.mytube.application.MyTubeApplication;
import le1.mytube.base.BaseContract;
import le1.mytube.listeners.PlaybackStateListener;
import le1.mytube.mvpModel.MusicControl;
import le1.mytube.mvpModel.database.song.YouTubeSong;

public class MusicPlayerPresenter extends AndroidViewModel implements MusicPlayerContract.ViewModel, PlaybackStateListener {
    private static final String TAG = "LE1_MusicPlayerPres";
    private MusicPlayerContract.View contractView;
    private MusicControl musicControl;

    public MusicPlayerPresenter(Application application) {
        super(application);
        musicControl = ((MyTubeApplication) getApplication()).getMusicControl();
        musicControl.addListener(this);
    }

    @Override
    public void linkPlayerToView(@NonNull SimpleExoPlayerView playerView) {
        if (playerView.getPlayer() == null)
            ((MyTubeApplication) getApplication()).getMusicControl().setPlayerView(playerView);
    }

    @Override
    public boolean startSongIfItsDifferent(YouTubeSong youTubeSong) {
        if (musicControl.getMetadata() == null ||
                !(musicControl.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
                        .equals(youTubeSong.getId()))) {
            musicControl.prepareAndPlay(youTubeSong);
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void setContractView(BaseContract.View contractView) {
        this.contractView = (MusicPlayerContract.View) contractView;
    }

    @Override
    public void onMetadataLoaded(MediaMetadataCompat metadata) {
        Log.d(TAG, "onMetadataLoaded: " + metadata);
    }

    @Override
    public void onLoading() {
        Log.d(TAG, "onLoading: ");
    }

    @Override
    public void onPlaying() {
        Log.d(TAG, "onPlaying: ");
    }

    @Override
    public void onPaused() {
        Log.d(TAG, "onPaused: ");
    }

    @Override
    public void onStopped() {
        Log.d(TAG, "onStopped: ");
    }

    @Override
    public void onError(String error) {
        Log.d(TAG, "onError: " + error);
    }
}

