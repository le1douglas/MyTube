package le1.mytube.ui.musicPlayer;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import le1.mytube.R;
import le1.mytube.application.MyTubeApplication;
import le1.mytube.mvpModel.database.song.YouTubeSong;


public class MusicPlayerActivity extends AppCompatActivity implements MusicPlayerContract.View{
    private static final String TAG = ("LE1_" + MusicPlayerActivity.class.getSimpleName());
    private SimpleExoPlayerView playerView;
    private MusicPlayerPresenter presenter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_music_player);

        playerView = findViewById(R.id.exo_player);
        presenter = ViewModelProviders.of(this).get(MusicPlayerPresenter.class);
        presenter.setContractView(this);

        YouTubeSong youTubeSong = getIntent().getParcelableExtra(MyTubeApplication.KEY_SONG);
        boolean shouldStart = getIntent().getBooleanExtra(MyTubeApplication.KEY_SHOULD_PLAY, false);
        if (shouldStart) {
            if (youTubeSong==null)
                Toast.makeText(this, "youTubeSongIsNull", Toast.LENGTH_SHORT).show();
            presenter.startSongIfItsDifferent(youTubeSong);
        }

    }

    private void makeFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    @Override
    protected void onResume() {
        super.onResume();
        presenter.linkPlayerToView(playerView);
    }
}