package le1.mytube.mvpPresenters;

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
import le1.mytube.listeners.MusicPlayerCallback;
import le1.mytube.listeners.PlaybackStateListener;
import le1.mytube.mvpModel.database.song.YouTubeSong;
import le1.mytube.mvpViews.MusicPlayerActivity;
import le1.mytube.mvpViews.SearchResultActivity;

public class MusicPlayerPresenter extends AndroidViewModel implements PlaybackStateListener, LifecycleObserver {
    private static final String TAG = ("LE1_" + MusicPlayerPresenter.class.getSimpleName());
    private MusicPlayerCallback listener;
    private SimpleExoPlayerView playerView;
    private List<String> resolutions;


    public MusicPlayerPresenter(Application application) {
        super(application);
        ((MyTubeApplication) getApplication()).getServiceRepo().addListener(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume() {
        if (((MyTubeApplication) getApplication()).getServiceRepo().getCurrentSong() != null && resolutions != null) {
            if (resolutions.size()>0) listener.onInitializeUi(((MyTubeApplication) getApplication()).getServiceRepo().getCurrentSong(), null);
            else listener.onInitializeUi(((MyTubeApplication) getApplication()).getServiceRepo().getCurrentSong(), resolutions);
            listener.onUpdateSeekBar((int) playerView.getPlayer().getCurrentPosition());
        }
    }


    @Override
    public void onPlaying(YouTubeSong currentSong, List<String> resolutions) {
        this.resolutions = resolutions;
        listener.onInitializeUi(currentSong, resolutions);
    }

    @Override
    public void onLoadingStarted() {

    }

    @Override
    public void onLoadingFinished() {

    }

    @Override
    public void onStopped() {
        listener.onCloseActivity();
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
    public void onPositionChanged(long currentTimeInMill) {
        listener.onUpdateSeekBar((int) currentTimeInMill);

    }

    public void playOrPause() {
        ((MyTubeApplication) getApplication()).getServiceRepo().playOrPause();
    }

    public void setListener(MusicPlayerCallback listener) {
        this.listener = listener;
    }

    public void linkPlayerToView(SimpleExoPlayerView playerView) {
        this.playerView = playerView;
        if (playerView.getPlayer() == null)
            ((MyTubeApplication) getApplication()).getServiceRepo().setView(playerView);
    }


    public void seekTo(int progress) {
        ((MyTubeApplication) getApplication()).getServiceRepo().seekTo((long) progress);
    }

    /**
     * activity can be opened either by {@link MusicPlayerActivity}
     * or by Notification
     * <p>
     * if opened by activity start new song, otherwise just open the player in it's current state
     */

    public void startSongIfNecessary(@NonNull Intent intent, @Nullable ComponentName callingActivity) {
        Log.d(TAG, "startSongIfNecessary called with intent=" + intent + " and callingActivity=" + callingActivity);
        if (callingActivity != null) {
            if (callingActivity.getClassName().equals(SearchResultActivity.class.getName())) {
                YouTubeSong youTubeSong = intent.getParcelableExtra(MyTubeApplication.KEY_SONG);
                ((MyTubeApplication) getApplication()).getServiceRepo().prepareStreaming(youTubeSong);
            }
        }
    }

}

