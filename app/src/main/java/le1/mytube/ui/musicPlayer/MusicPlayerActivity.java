package le1.mytube.ui.musicPlayer;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.SeekBar;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import java.util.List;

import le1.mytube.PlayerOverlayView;
import le1.mytube.R;
import le1.mytube.mvpModel.database.song.YouTubeSong;


public class MusicPlayerActivity extends LifecycleActivity implements MusicPlayerContract.View, SeekBar.OnSeekBarChangeListener, LifecycleOwner, AdapterView.OnItemSelectedListener {
    private static final String TAG = ("LE1_" + MusicPlayerActivity.class.getSimpleName());
    SimpleExoPlayerView playerView;
    PlayerOverlayView overlay;
    MusicPlayerPresenter presenter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_music_player);

        playerView = findViewById(R.id.exo_player);
        overlay = findViewById(R.id.overlay);


        presenter = ViewModelProviders.of(this).get(MusicPlayerPresenter.class);
        presenter.setContractView(this);
        presenter.linkPlayerToView(playerView);
        getLifecycle().addObserver(presenter);


        overlay.setSpinnerOnItemSelected(this);
        overlay.setOnSeekBarChangeListener(this);
        overlay.setButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.playOrPause();

            }
        });

        //activity just created, instead of rotated
        if (savedInstanceState == null) {
            presenter.startSongIfNecessary(getIntent(), getCallingActivity());
        }
    }

    private void makeFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    @Override
    public void onUpdateSeekBar(int position) {
        overlay.setProgress(position);
    }

    @Override
    public void onResolutionAvailable(@NonNull List<YouTubeSong> youTubeSongs) {
        overlay.setSpinnerContent(youTubeSongs);
    }

    @Override
    public void onInitializeUi(YouTubeSong youTubeSong) {
        overlay.setTitle(youTubeSong.getTitle());
        overlay.setMaxProgress(youTubeSong.getDuration());
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}