package le1.mytube.services.musicService;

import android.content.Intent;
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
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import le1.mytube.application.AppLifecycleObserver;
import le1.mytube.application.MyTubeApplication;
import le1.mytube.listeners.AudioFocusCallback;
import le1.mytube.mvpModel.MusicControl;
import le1.mytube.mvpModel.database.song.YouTubeSong;
import le1.mytube.notifications.MusicNotification;

/**
 * The service used for playback.
 * It starts when the app launches in {@link AppLifecycleObserver#onResume()},
 * and stops when no activity is on the recents screen (or in foreground) and no music is playing
 */
public class MusicService extends MediaBrowserServiceCompat {

    private static final String TAG = "LE1_MusicService";

    public static final String YOUTUBE_SONG_KEY = "youtubesong_key";

    private MediaSessionManager mediaSession;
    private MediaButtonManager mediaButtonReceiver;
    private AudioFocusManager audioFocus;
    private PlayerManager player;


    private MusicControl musicControl;


    private static YouTubeSong currentOrLastSong;

    public static YouTubeSong getCurrentOrLastSong() {
        return currentOrLastSong;
    }


    /**
     * Does all the setup and sets the PlaybackState to {@link PlaybackStateCompat#STATE_NONE}
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mediaSession = new MediaSessionManager(this, mediaSessionCallback);
        setSessionToken(mediaSession.getToken());
        mediaSession.setPlaybackState(PlaybackStateCompat.STATE_NONE, -1);
        mediaButtonReceiver = new MediaButtonManager(this, mediaSession);
        audioFocus = new AudioFocusManager(this, audioFocusCallback);
        player = PlayerManager.getInstance(this);
        player.addEventListener(playerListener);
        musicControl = ((MyTubeApplication) getApplication()).getMusicControl();
    }

    /**
     * Calls {@link MediaButtonReceiver#handleIntent(MediaSessionCompat, Intent)}
     * to forward any media buttons clicks to the appropriate {@link MediaSessionCompat.Callback} method
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaButtonReceiver.handleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot("rootId", null);
    }

    @Override
    public void onLoadChildren(@NonNull String rootId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(null);
    }

    /**
     * Callback that actually controls the playback.
     * Every playback command ends here in a way or another.
     * Every {@link MediaSessionManager#setPlaybackState(int, long)} is delegated to {@link #playerListener} as it's when the playback <i>actually</i>
     * starts and not when it <i>should</i> happen
     */
    private MediaSessionCompat.Callback mediaSessionCallback = new MediaSessionCompat.Callback() {

        /**
         * Starts preparing and then starts playing with {@link MusicControl#play()}
         */
        @Override
        public void onPrepareFromMediaId(String youTubeId, Bundle extras) {
            Log.d(TAG, "onPrepareFromMediaId: " + youTubeId);
            super.onPrepareFromMediaId(youTubeId, extras);

            //we want a fresh start if music it's already playing
            if (mediaSession.getPlaybackState() == PlaybackStateCompat.STATE_PLAYING) {
                player.pause();
            }

            extras.setClassLoader(YouTubeSong.class.getClassLoader());
            currentOrLastSong = extras.getParcelable(YOUTUBE_SONG_KEY);


            //we set the playback state to STATE_BUFFERING before extracting the youtube song
            mediaSession.setPlaybackState(PlaybackStateCompat.STATE_BUFFERING, -1);
            mediaSession.setMetadata(currentOrLastSong);
            MusicNotification.updateNotification(MusicService.this, mediaSession);


            new YouTubeExtractor(MusicService.this) {
                @Override
                protected void onExtractionComplete(SparseArray<YtFile> itags, VideoMeta videoMeta) {
                    if (itags != null) {
                        if (videoMeta.isLiveStream()) {
                            Toast.makeText(MusicService.this, "streaming is not supported yet", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.d(TAG, "onExtractionComplete: ");
                        currentOrLastSong.setDuration((int) (videoMeta.getVideoLength() * 1000));
                        currentOrLastSong.setAuthor(videoMeta.getAuthor());
                        currentOrLastSong.setImageUri(Uri.parse(videoMeta.getMaxResImageUrl()));
                        mediaSession.setMetadata(currentOrLastSong);
                        //actually prepare the player
                        player.prepare(Uri.parse(itags.get(140).getUrl()), Uri.parse(itags.get(160).getUrl()));
                        //after preparing start playing
                        Log.d(TAG, "just before play");
                        musicControl.play();
                    } else {
                        mediaSession.setPlaybackState(PlaybackStateCompat.STATE_ERROR, -1);
                        mediaSession.setPlaybackStateErrorMessage(PlaybackStateCompat.ERROR_CODE_APP_ERROR, "itags are null");
                        MusicNotification.updateNotification(MusicService.this, mediaSession);
                        Toast.makeText(MusicService.this, "itags are null", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onExtractionComplete: itags are null");
                    }
                }
            }
                    //actually extract the YouTube song. Method called at object creation
                    .extract("https://www.youtube.com/watch?v=" + youTubeId, true, true);
        }


        @Override
        public void onPause() {
            super.onPause();
            player.pause();
        }

        /**
         * Starts playing the media
         */
        @Override
        public void onPlay() {
            super.onPlay();
            //ask for audio focus. If given, continue on
            if (audioFocus.requestAudioFocus()) {
                mediaSession.setActive();
                player.play();
            }


        }

        /**
         * Stops playback causing {@link Player.EventListener#onPlayerStateChanged(boolean, int)}
         * to be called with {@link Player#STATE_IDLE}
         */
        @Override
        public void onStop() {
            super.onStop();
            player.stop();
            audioFocus.abandonAudioFocus();
            mediaSession.setInactive();

            // sometime the notification is swiped while the app is not in the recent task
            // in this case we want to stop the service
            if (!MyTubeApplication.isAppOpen())
                musicControl.disconnect();

        }

        /**
         * Seeks to a different position in the same media
         * @param pos The position in milliseconds
         */
        @Override
        public void onSeekTo(long pos) {
            Log.d(TAG, "onSeekTo: " + (int) pos);
            super.onSeekTo(pos);
            player.seekTo((int) pos);
        }


        @Override
        public void onRewind() {
            super.onRewind();
            musicControl.seekTo(player.getCurrentPosition() - 10_000);

        }

        @Override
        public void onFastForward() {
            super.onFastForward();
            musicControl.seekTo(player.getCurrentPosition() + 10_000);

        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            musicControl.seekTo(0);
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            Toast.makeText(MusicService.this, "Work in progress", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * Listener of the {@link #player} actions. Gets called when the player <i>actually</i>
     * starts responding not when it <i>should</i>
     */
    private Player.EventListener playerListener = new Player.EventListener() {

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case Player.STATE_IDLE:
                    mediaSession.setPlaybackState(PlaybackStateCompat.STATE_STOPPED, -1);
                    Log.d(TAG, "onPlayerStateChanged: IDLE");
                    break;
                case Player.STATE_READY:
                    if (playWhenReady) {
                        //called when it's playing
                        mediaSession.setPlaybackState(PlaybackStateCompat.STATE_PLAYING, player.getCurrentPosition());
                        Log.d(TAG, "onPlayerStateChanged: PLAYING");
                        break;
                    } else {
                        //called when it's paused
                        mediaSession.setPlaybackState(PlaybackStateCompat.STATE_PAUSED, player.getCurrentPosition());
                        Log.d(TAG, "onPlayerStateChanged: PAUSED");
                        break;
                    }
                case Player.STATE_BUFFERING:
                    mediaSession.setPlaybackState(PlaybackStateCompat.STATE_BUFFERING, player.getCurrentPosition());
                    Log.d(TAG, "onPlayerStateChanged: BUFFERING");
                    break;
                case Player.STATE_ENDED:
                    Log.d(TAG, "onPlayerStateChanged: ENDED");
                    //when the song ends, stop playback
                    //TODO add queue
                    musicControl.stop();
                    break;
            }

            MusicNotification.updateNotification(MusicService.this, mediaSession);
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            mediaSession.setPlaybackState(PlaybackStateCompat.STATE_ERROR, -1);
            mediaSession.setPlaybackStateErrorMessage(PlaybackStateCompat.ERROR_CODE_UNKNOWN_ERROR, "error in player");
            MusicNotification.updateNotification(MusicService.this, mediaSession);
        }

        @Override
        public void onPositionDiscontinuity() {
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }
    };


    private boolean wasPlaying;

    /**
     * Callback of audio focus events
     *
     * @see AudioFocusCallback
     */
    AudioFocusCallback audioFocusCallback = new AudioFocusCallback() {

        @Override
        public void onAudioFocusGain() {
            if (wasPlaying) musicControl.play();
            wasPlaying = false;
        }

        @Override
        public void onAudioFocusLoss() {
            if (musicControl.getPlaybackState()
                    == PlaybackStateCompat.STATE_PLAYING) wasPlaying = true;
            musicControl.pause();
        }

        @Override
        public void onAudioFocusLossTransient() {
            if (musicControl.getPlaybackState()
                    == PlaybackStateCompat.STATE_PLAYING) wasPlaying = true;
            musicControl.pause();
        }

        @Override
        public void onAudioFocusLossTransientCanDuck() {
            if (musicControl.getPlaybackState()
                    == PlaybackStateCompat.STATE_PLAYING) wasPlaying = true;
            player.duck();
        }

        @Override
        public void onAudioFocusBecomingNoisy() {
            musicControl.pause();
        }
    };


    /**
     * Called when the app is swiped from the recents screen
     * Stops the service if user is not listening to music
     *
     * @see MediaBrowserServiceCompat#onTaskRemoved(Intent)
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        MyTubeApplication.setAppOpen(false);

        //if it's playing or loading don't stop the service
        if (!(mediaSession.getPlaybackState() == PlaybackStateCompat.STATE_PLAYING) &&
                !(mediaSession.getPlaybackState() == PlaybackStateCompat.STATE_BUFFERING)) {
            musicControl.disconnect();
        }
    }

    /**
     * Called when the service is being stopped. Calls {@link MusicControl#stop()} to perform
     * all the clean up and then destroys the objects
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
        musicControl.stop();
        mediaSession.destroy();
        player.destroy();
    }


}
