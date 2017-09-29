package le1.mytube.mvpViews;

import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import le1.mytube.R;
import le1.mytube.listeners.PlaybackStateCallback;
import le1.mytube.mvpModel.database.song.YouTubeSong;
import le1.mytube.services.MusicServiceConstants;
import le1.mytube.services.servicetest.ServiceController;

public class MusicPlayerActivity extends AppCompatActivity implements PlaybackStateCallback {
    private static final String TAG = ("LE1_" + MusicPlayerActivity.class.getSimpleName());
    SimpleExoPlayerView playerView;
    TextView titleView;
    YouTubeSong youTubeSong;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_music_player2);
        Log.d(TAG, " onCreate");
        this.playerView = (SimpleExoPlayerView) findViewById(R.id.exo_player);
        this.titleView = (TextView) findViewById(R.id.title);
        this.youTubeSong = getIntent().getParcelableExtra(MusicServiceConstants.KEY_SONG);
        ServiceController.getInstance(this).setCallback(this);
        ServiceController.getInstance(this).prepareForStreaming(this.youTubeSong);
    }

    public void onPlaying() {
    }

    public void onLoading(ExoPlayer exoPlayer) {
        this.playerView.setPlayer((SimpleExoPlayer) exoPlayer);
    }

    public void onStopped() {
    }

    public void onPaused() {
    }

    public void onMetadataChanged(MediaMetadataCompat mediaMetadata) {
        this.titleView.setText(mediaMetadata.getDescription().getTitle().toString() + "  |" + String.valueOf(mediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)));
    }
}