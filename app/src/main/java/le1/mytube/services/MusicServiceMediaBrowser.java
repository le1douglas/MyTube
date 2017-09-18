package le1.mytube.services;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import le1.mytube.R;
import le1.mytube.mvpModel.Repo;

public class MusicServiceMediaBrowser extends MediaBrowserServiceCompat implements AudioManager.OnAudioFocusChangeListener {
    private static final String TAG = "LE1_" + MusicServiceMediaBrowser.class.getSimpleName();
    private static final int MEDIA_BUTTON_REQUEST_CODE = 33;

    private MediaSessionCompat mediaSession;
    private Player player;

    private BroadcastReceiver noisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            player.pause();
        }
    };

    Repo repo;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        repo = new Repo(this);

        mediaSession = new MediaSessionCompat(this, TAG);
        setSessionToken(mediaSession.getSessionToken());
        mediaSession.setCallback(new MediaSessionCallback());
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, MEDIA_BUTTON_REQUEST_CODE, mediaButtonIntent, 0);
        mediaSession.setMediaButtonReceiver(pendingIntent);

        IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(noisyReceiver, filter);

        player = new Player(this, this, this, mediaSession);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
//        if (intent != null) {
//            Log.d(TAG, "intent action=" + intent.getAction());
//            switch (intent.getAction()) {
//                case MusicServiceConstants.ACTION_START_STREAMING:
//                    player.prepareFromUri(Uri.parse(intent.getStringExtra("Uri")));
//                    break;
//                case MusicServiceConstants.ACTION_START_LOCAL:
//                    player.prepareFromId(intent.getStringExtra("Id"));
//                    break;
//                case MusicServiceConstants.ACTION_PAUSE:
//                    player.pause();
//                    break;
//                case MusicServiceConstants.ACTION_PLAY:
//                    player.play();
//                    break;
//                case MusicServiceConstants.ACTION_STOP:
//                    player.stop();
//                    break;
//                case MusicServiceConstants.ACTION_PLAY_PAUSE:
//                    player.playPause();
//                    break;
//            }
//
//        }

        MediaButtonReceiver.handleIntent(mediaSession, intent);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onAudioFocusChange(int focusChange) {
        Log.d(TAG, "onAudioFocusChange with focusChange=" + focusChange);
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                player.play();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                player.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                player.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                player.duck();
                break;
        }
    }

    private final class MediaSessionCallback extends MediaSessionCompat.Callback {

        @Override
        public void onPrepareFromUri(Uri uri, Bundle extras) {
            super.onPrepareFromUri(uri, extras);
            Log.d(TAG, "onPrepareFromUri with uri=" + uri.toString());
            player.prepareFromUri(uri);
        }


        @Override
        public void onPrepareFromMediaId(String mediaId, Bundle extras) {
            super.onPrepareFromMediaId(mediaId, extras);
            Log.d(TAG, "onPrepareFromMediaId with id=" + mediaId);
            player.prepareFromId(mediaId);
        }

        @Override
        public void onPlay() {
            super.onPlay();
            Log.d(TAG, "play");
            player.play();
        }

        @Override
        public void onSeekTo(long position) {
            super.onSeekTo(position);
            Log.d(TAG, "onSeekTo:" + position);
            player.seekTo(position);
        }

        @Override
        public void onPause() {
            super.onPause();
            Log.d(TAG, "pause");
            player.pause();
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.d(TAG, "stop");
            player.stop();
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            Log.d(TAG, "onSkipToNext");
            player.skipToNext();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            Log.d(TAG, "onSkipToPrevious");
            player.skipToPrevious();
        }
    }


    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        Log.d(TAG, "onGetRoot");
        return new BrowserRoot(getString(R.string.app_name), null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.d(TAG, "onLoadChildren");
        result.sendResult(null);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(TAG, "onTaskRemoved");
        Toast.makeText(this, "onTaskRemoved", Toast.LENGTH_SHORT).show();
        stopSelf();

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
        if (mediaSession.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_STOPPED) {
            unregisterReceiver(noisyReceiver);
            player.stop();
            player.onDestroy();
        }
        super.onDestroy();
    }

}
