package le1.mytube.mvpViews;

import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import le1.mytube.MyTubeApplication;
import le1.mytube.R;
import le1.mytube.listeners.PlaybackStateCallback;
import le1.mytube.mvpModel.database.song.YouTubeSong;


public class MusicPlayerActivity extends AppCompatActivity implements PlaybackStateCallback, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = ("LE1_" + MusicPlayerActivity.class.getSimpleName());
    SimpleExoPlayerView playerView;
    TextView titleView;
    YouTubeSong youTubeSong;
    SeekBar seekBar;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_music_player2);
        Log.d(TAG, " onCreate");
        this.playerView = (SimpleExoPlayerView) findViewById(R.id.exo_player);
        this.titleView = (TextView) findViewById(R.id.title);
        seekBar = (SeekBar) findViewById(R.id.progressBar);
        seekBar.setOnSeekBarChangeListener(this);
        this.youTubeSong = getIntent().getParcelableExtra(MyTubeApplication.KEY_SONG);

        if (youTubeSong!=null) {
        ((MyTubeApplication) getApplication()).getServiceRepo().setCallback(this);
        ((MyTubeApplication) getApplication()).getServiceRepo().prepareStreaming(this.youTubeSong);}

        if (playerView.getPlayer()==null) ((MyTubeApplication) getApplication()).getServiceRepo().setView(playerView);

    }

    @Override
    public void onPlaying() {
    }

    @Override
    public void onLoadingStarted(ExoPlayer exoPlayer) {

    }

    @Override
    public void onLoadingFinished() {

    }

    @Override
    public void onStopped() {
    }

    @Override
    public void onError(String message) {
    }

    @Override
    public void onPaused() {
    }

    @Override
    public void onMetadataChanged(MediaMetadataCompat mediaMetadata) {
        this.titleView.setText(mediaMetadata.getDescription().getTitle().toString());
        seekBar.setMax((int) mediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
    }

    @Override
    public void onPositionChanged(long currentTimeInMill) {
        seekBar.setProgress((int) currentTimeInMill);
    }

    public void rewButton(View view) {
        Toast.makeText(this, "WIP", Toast.LENGTH_SHORT).show();
    }

    public void backButton(View view) {
        Toast.makeText(this, "WIP", Toast.LENGTH_SHORT).show();
    }

    public void forButton(View view) {
        Toast.makeText(this, "WIP", Toast.LENGTH_SHORT).show();
    }

    public void nextButton(View view) {
        Toast.makeText(this, "WIP", Toast.LENGTH_SHORT).show();
    }

    public void playpauseButton(View view) {
        ((MyTubeApplication) getApplication()).getServiceRepo().playOrPause();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        ((MyTubeApplication) getApplication()).getServiceRepo().seekTo((long)seekBar.getProgress());
    }
}