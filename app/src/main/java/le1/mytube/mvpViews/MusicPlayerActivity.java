package le1.mytube.mvpViews;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import le1.mytube.R;
import le1.mytube.mvpModel.Repo;
import le1.mytube.mvpModel.database.song.YouTubeSong;
import le1.mytube.services.MusicServiceConstants;
import le1.mytube.services.MusicServiceMediaBrowser;


public class MusicPlayerActivity extends AppCompatActivity{
    private static final String TAG = "LE1_"+MusicPlayerActivity.class.getSimpleName();

    Repo repo= new Repo(this);
    MediaControllerCompat mediaController;
    MediaBrowserCompat mediaBrowser;
    YouTubeSong youTubeSong;

    MediaBrowserCompat.ConnectionCallback mConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            Log.d(TAG, "onConnected: session token " + mediaBrowser.getSessionToken());
            try {
                mediaController = new MediaControllerCompat(MusicPlayerActivity.this, mediaBrowser.getSessionToken());
                MediaControllerCompat.setMediaController(MusicPlayerActivity.this, mediaController);
                Log.d(TAG, mediaController.toString());
                repo.startSong(mediaController,youTubeSong);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConnectionFailed() {
            Log.e(TAG, "onConnectionFailed");
        }

        @Override
        public void onConnectionSuspended() {
            Log.d(TAG, "onConnectionSuspended");

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player2);
        Log.d(TAG, " onCreate");

        youTubeSong= getIntent().getParcelableExtra(MusicServiceConstants.KEY_SONG);

        mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MusicServiceMediaBrowser.class),
                mConnectionCallback, null);


    }

    @Override
    protected void onStart() {
        super.onStart();
        mediaBrowser.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaBrowser.disconnect();
    }

    public void pause(View view) {
        repo.playOrPauseSong(mediaController);
    }

}
