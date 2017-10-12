package le1.mytube.mvpViews;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import le1.mytube.MyTubeApplication;
import le1.mytube.PlayerOverlayView;
import le1.mytube.R;
import le1.mytube.listeners.MusicPlayerCallback;
import le1.mytube.mvpModel.database.song.YouTubeSong;
import le1.mytube.mvpPresenters.MusicPlayerPresenter;


public class MusicPlayerActivity extends LifecycleActivity implements SeekBar.OnSeekBarChangeListener, MusicPlayerCallback, LifecycleOwner{
    private static final String TAG = ("LE1_" + MusicPlayerActivity.class.getSimpleName());
    SimpleExoPlayerView playerView;
    YouTubeSong youTubeSong;
    PlayerOverlayView overlay;
    MusicPlayerPresenter presenter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreenIfLandscape();
        setContentView(R.layout.activity_music_player);
        playerView = findViewById(R.id.exo_player);
        overlay = findViewById(R.id.overlay);

        presenter = ViewModelProviders.of(this).get(MusicPlayerPresenter.class);
        presenter.setListener(this);
        this.getLifecycle().addObserver(presenter);

        overlay.setOnSeekBarChangeListener(this);
        overlay.setButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.playOrPause();

            }
        });

        if (getIntent()!=null)   Toast.makeText(this, "new Intent", Toast.LENGTH_SHORT).show();
        youTubeSong = getIntent().getParcelableExtra(MyTubeApplication.KEY_SONG);

        presenter.linkPlayerToView(playerView);
        presenter.startSong(youTubeSong);


    }

    private void makeFullScreenIfLandscape() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        presenter.seekTo(seekBar.getProgress());
    }

    @Override
    public void onUpdateSeekBar(int position) {
        overlay.setProgress(position);
    }

    @Override
    public void onInitializeUi(YouTubeSong youTubeSong) {
        overlay.setTitle(youTubeSong.getTitle());
        overlay.setMaxProgress(youTubeSong.getDuration());
    }

}