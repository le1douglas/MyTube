package le1.mytube.ui.musicPlayer;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.ComponentName;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import java.util.List;

import le1.mytube.application.MyTubeApplication;
import le1.mytube.base.BaseContract;
import le1.mytube.listeners.PlaybackStateListener;
import le1.mytube.mvpModel.database.song.YouTubeSong;
import le1.mytube.ui.searchResult.SearchResultActivity;

public class MusicPlayerPresenter extends AndroidViewModel implements MusicPlayerContract.ViewModel, PlaybackStateListener, LifecycleObserver {
    private static final String TAG = ("LE1_" + MusicPlayerPresenter.class.getSimpleName());
    private MusicPlayerContract.View contractView;

    public MusicPlayerPresenter(Application application) {
        super(application);
        ((MyTubeApplication) application).getServiceRepo().addListener(this);
    }

    /**
     *  getCurrentSongs() may be empty but not null, because it's initialized along with
     *  the service
     */
    @SuppressWarnings("ConstantConditions")
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume() {
        if (((MyTubeApplication) getApplication()).getServiceRepo().getCurrentSongs().size()>0) {
            contractView.onResolutionAvailable(((MyTubeApplication) getApplication()).getServiceRepo().getCurrentSongs());
            contractView.onUpdateSeekBar(((MyTubeApplication) getApplication()).getServiceRepo().getPlaybackPosition());
        }
    }


    @Override
    public void onPlaying(List<YouTubeSong> currentSongs) {
        contractView.onResolutionAvailable(currentSongs);
    }

    @Override
    public void onPreparing(YouTubeSong currentSong) {
        //only some metadata is availble at ths point
        contractView.onInitializeUi(currentSong);
    }

    @Override
    public void onLoading(YouTubeSong currentSong) {
        contractView.onInitializeUi(currentSong);
    }


    @Override
    public void onStopped() {
    }

    @Override
    public void onError(String message) {
        if (message == null || message.equals("")) message = "an error occured";
        Toast.makeText(this.getApplication(), message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onPaused() {

    }

    @Override
    public void onPositionChanged(int currentTimeInSec) {
        contractView.onUpdateSeekBar(currentTimeInSec);
    }



    @Override
    public void playOrPause() {
        ((MyTubeApplication) getApplication()).getServiceRepo().playOrPause();
    }

    @Override
    public void seekTo(int progress) {
        ((MyTubeApplication) getApplication()).getServiceRepo().seekTo(progress);
    }

    /**
     * activity can be opened either by {@link MusicPlayerActivity}
     * or by Notification
     * <p>
     * if opened by activity start new song, otherwise just open the player in it's current state
     */

    @Override
    public void startSongIfNecessary(@NonNull Intent intent, @Nullable ComponentName callingActivity) {
        Log.d(TAG, "startSongIfNecessary called with intent=" + intent + " and callingActivity=" + callingActivity);
        if (callingActivity != null) {
            if (callingActivity.getClassName().equals(SearchResultActivity.class.getName())) {
                YouTubeSong youTubeSong = intent.getParcelableExtra(MyTubeApplication.KEY_SONG);
                ((MyTubeApplication) getApplication()).getServiceRepo().prepareStreaming(youTubeSong);
            }
        }
    }

    @Override
    public void linkPlayerToView(SimpleExoPlayerView playerView) {
        if (playerView.getPlayer() == null)
            ((MyTubeApplication) getApplication()).getServiceRepo().setView(playerView);
    }

    @Override
    public void setContractView(BaseContract.View contractView) {
        this.contractView = (MusicPlayerContract.View) contractView;
    }

}

