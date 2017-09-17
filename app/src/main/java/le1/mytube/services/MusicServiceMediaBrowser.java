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
import android.util.Log;

import java.util.List;

import le1.mytube.R;

public class MusicServiceMediaBrowser extends MediaBrowserServiceCompat implements AudioManager.OnAudioFocusChangeListener{
    private static final String TAG = "LE1_"+MusicServiceMediaBrowser.class.getSimpleName();
    private static final int MEDIA_BUTTON_REQUEST_CODE = 33;

    private MediaSessionCompat mediaSession;
    private Player player;

    private BroadcastReceiver noisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            player.pause();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

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

        player= new Player(this, this, mediaSession);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        unregisterReceiver(noisyReceiver);
        player.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        Log.d(TAG, "onAudioFocusChange with focusChange=" + focusChange);
        switch (focusChange){
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
            player.prepare(uri);
        }


        @Override
        public void onPrepareFromMediaId(String mediaId, Bundle extras) {
            super.onPrepareFromMediaId(mediaId, extras);
            Log.d(TAG, "onPrepareFromMediaId with id=" + mediaId);
            player.prepare(mediaId);
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
    }


    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        Log.d("service", "onGetRoot");
        return new BrowserRoot(getString(R.string.app_name), null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.d("service", "onLoadChildren");
        result.sendResult(null);
    }

}
