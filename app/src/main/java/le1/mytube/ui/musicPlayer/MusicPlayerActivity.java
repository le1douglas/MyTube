package le1.mytube.ui.musicPlayer;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import le1.mytube.R;


public class MusicPlayerActivity extends AppCompatActivity implements MusicPlayerContract.View{
    private static final String TAG = ("LE1_" + MusicPlayerActivity.class.getSimpleName());
    private SimpleExoPlayerView playerView;
    private MusicPlayerPresenter presenter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_music_player);

        playerView = (SimpleExoPlayerView) findViewById(R.id.exo_player);
        presenter = ViewModelProviders.of(this).get(MusicPlayerPresenter.class);
        presenter.setContractView(this);

    }

    private void makeFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    @Override
    public void onSetPlayerView(SimpleExoPlayerView exoPlayerView) {
        presenter.linkPlayerToView(exoPlayerView);
    }
}