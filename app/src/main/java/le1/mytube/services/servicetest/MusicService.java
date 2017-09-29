package le1.mytube.services.servicetest;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat.MediaItem;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.MediaSessionCompat.Callback;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import le1.mytube.R;
import le1.mytube.mvpModel.database.song.YouTubeSong;
import le1.mytube.notification.musicNotification.MusicNotification;
import le1.mytube.services.MusicServiceConstants;

public class MusicService extends MediaBrowserServiceCompat implements OnAudioFocusChangeListener {
    private static final String TAG = ("LE1_" + MusicService.class.getSimpleName());
    public static MediaSessionCompat mediaSession;
    NotificationManager mNotificationManager;
    private BroadcastReceiver noisyReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (ServiceController.getInstance(MusicService.this).getPlaybackState() == PlaybackStateCompat.STATE_PLAYING) {
                ServiceController.getInstance(MusicService.this).pause();
            }
        }
    };



    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mediaSession = new MediaSessionCompat(this, TAG);
        setSessionToken(mediaSession.getSessionToken());
        mediaSession.setCallback(new MediaSessionCallback());
        mediaSession.setFlags(3);
        Intent mediaButtonIntent = new Intent("android.intent.action.MEDIA_BUTTON");
        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);
        mediaSession.setMediaButtonReceiver(PendingIntent.getBroadcast(this, 33, mediaButtonIntent, 0));
        Log.d(TAG, mediaSession.toString());
        Log.d(TAG, "onCreate finished");
        this.mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        MediaButtonReceiver.handleIntent(mediaSession, intent);
        return super.onStartCommand(intent, flags, startId);
    }

    public void updateNotification(Context context, MediaSessionCompat mediaSession, int state) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(MusicNotification.ID, MusicNotification.updateNotification(context, mediaSession, state).build());
    }

    public void deleteNotification(boolean removeNotification) {
        this.mNotificationManager.cancel(MusicNotification.ID);
    }

    public void setConnectedToNoisyReciever(Context context, boolean connected) {
        if (connected) {
            context.registerReceiver(this.noisyReceiver, new IntentFilter("android.media.AUDIO_BECOMING_NOISY"));
            return;
        }
        context.unregisterReceiver(this.noisyReceiver);
    }

    public boolean requestAudiofocus(Context context) {
        if (((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).requestAudioFocus(this, 3, 1) == 1) {
            return true;
        }
        return false;
    }

    public void abandonAudioFocus(Context context) {
        ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).abandonAudioFocus(this);
    }

    public void onAudioFocusChange(int focusChange) {
        Log.d(TAG, "onAudioFocusChange with focusChange=" + focusChange);
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                ServiceController.getInstance(this).pause();
                return;
            case AudioManager.AUDIOFOCUS_LOSS:
                Toast.makeText(this, "AUDIOFOCUS_LOSS", Toast.LENGTH_SHORT).show();
                ServiceController.getInstance(this).pause();
                return;
            case AudioManager.AUDIOFOCUS_GAIN:
                //if (ServiceController.getInstance(this).getPlaybackState()== PlaybackStateCompat.STATE_PAUSED)
                ServiceController.getInstance(this).play();
                return;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                //duck
                return;
            default:
                return;
        }
    }


    private final class MediaSessionCallback extends Callback {
        public void onPrepareFromUri(Uri uri, Bundle extras) {
            super.onPrepareFromUri(uri, extras);
            Log.d(MusicService.TAG, "onPrepareFromUri");
            extras.setClassLoader(YouTubeSong.class.getClassLoader());
            ServiceController.getInstance(MusicService.this).prepareForStreaming((YouTubeSong) extras.getParcelable(MusicServiceConstants.KEY_SONG));
        }

        public void onPrepareFromMediaId(String mediaId, Bundle extras) {
            super.onPrepareFromMediaId(mediaId, extras);
            Log.d(MusicService.TAG, "onPrepareFromMediaId with id=" + mediaId);
            extras.setClassLoader(YouTubeSong.class.getClassLoader());
            ServiceController.getInstance(MusicService.this).prepareForLocal((YouTubeSong) extras.getParcelable(MusicServiceConstants.KEY_SONG));
        }

        public void onPlay() {
            super.onPlay();
            Log.d(MusicService.TAG, "play");
            ServiceController.getInstance(MusicService.this).play();
        }

        public void onSeekTo(long position) {
            super.onSeekTo(position);
            Log.d(MusicService.TAG, "onSeekTo:" + position);
        }

        public void onPause() {
            super.onPause();
            Log.d(MusicService.TAG, "pause");
            ServiceController.getInstance(MusicService.this).pause();
        }

        public void onStop() {
            super.onStop();
            Log.d(MusicService.TAG, "stop");
            ServiceController.getInstance(MusicService.this).stop();
        }

        public void onSkipToNext() {
            super.onSkipToNext();
            Log.d(MusicService.TAG, "onSkipToNext");
        }

        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            Log.d(MusicService.TAG, "onSkipToPrevious");
        }

        public void onRewind() {
            super.onRewind();
            Log.d(MusicService.TAG, "onRewind");
        }

        public void onFastForward() {
            super.onFastForward();
            Log.d(MusicService.TAG, "onFastForward");
        }
    }

    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        Log.d(TAG, "onGetRoot");
        return new BrowserRoot(getString(R.string.app_name), null);
    }

    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaItem>> result) {
        Log.d(TAG, "onLoadChildren");
        result.sendResult(null);
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        Toast.makeText(this, "service destroyed", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}
