package le1.mytube.services.servicetest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.media.session.PlaybackStateCompat.Builder;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player.EventListener;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection.Factory;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import le1.mytube.MyTubeApplication;
import le1.mytube.R;
import le1.mytube.listeners.PlaybackStateCallback;
import le1.mytube.mvpModel.database.song.YouTubeSong;

public class ServiceController {
    private static ServiceController INSTANCE = null;
    private static final long PLAYBACK_SPEED = 1;
    private static final String TAG = ("LE1_" + ServiceController.class.getSimpleName());
    private PlaybackStateCallback callback;
    private MediaBrowserCompat.ConnectionCallback connectionCallback = new ConnectionCallback();
    private Context context;
    private MediaBrowserCompat mediaBrowser;
    private MediaSessionCompat mediaSession;
    private Builder playbackState = new Builder();
    private SimpleExoPlayer player;
    private EventListener playerListener = new PlayerListener();
    private MusicService service;
    private YouTubeSong songPreparing;

    private class ConnectionCallback extends MediaBrowserCompat.ConnectionCallback {

        public void onConnected() {
            Log.d(ServiceController.TAG, "onConnected: session token " + ServiceController.this.mediaBrowser.getSessionToken());
            ServiceController.this.startService();
            ServiceController.this.mediaSession = MusicService.mediaSession;
            Log.d(ServiceController.TAG, "mediasession=" + ServiceController.this.mediaSession.toString());
            ServiceController.this.player = ExoPlayerFactory.newSimpleInstance(ServiceController.this.context, new DefaultTrackSelector(new Factory(new DefaultBandwidthMeter())));
            ServiceController.this.player.addListener(ServiceController.this.playerListener);
            ServiceController.this.playbackState.setActions(9855);
            ServiceController.this.service = new MusicService();
        }

        public void onConnectionFailed() {
            Log.e(ServiceController.TAG, "onConnectionFailed");
        }

        public void onConnectionSuspended() {
            Log.d(ServiceController.TAG, "onConnectionSuspended");
        }
    }

    private class PlayerListener implements EventListener {

        public void onTimelineChanged(Timeline timeline, Object manifest) {
        }

        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        }

        public void onLoadingChanged(boolean isLoading) {
        }

        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == 3 && ServiceController.this.songPreparing != null) {
                ServiceController.this.setMetadata(ServiceController.this.songPreparing);
                ServiceController.this.songPreparing = null;
            }
        }

        public void onRepeatModeChanged(int repeatMode) {
        }

        public void onPlayerError(ExoPlaybackException error) {
        }

        public void onPositionDiscontinuity() {
        }

        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        }
    }

    private ServiceController(Context context) {
        this.context = context;
        this.mediaBrowser = new MediaBrowserCompat(context, new ComponentName(context, MusicService.class), this.connectionCallback, null);
        this.mediaBrowser.connect();
    }

    public static ServiceController getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ServiceController(context);
        }
        return INSTANCE;
    }

    private void startService() {
        this.context.startService(new Intent(this.context.getApplicationContext(), MusicService.class));
    }

    private void stopService() {
        Log.d(TAG, "getPlayer");
        this.player.release();
        this.mediaSession.release();
        this.context.stopService(new Intent(this.context.getApplicationContext(), MusicService.class));
        this.service = null;
        this.mediaBrowser.disconnect();
    }

    public void prepareForStreaming(final YouTubeSong youTubeSong) {
        setPlaybackState(PlaybackStateCompat.STATE_BUFFERING);
        new YouTubeExtractor(this.context) {
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                if (ytFiles != null) {
                    Log.d(ServiceController.TAG, "onExtractionComplete");
                    Uri audioUri = Uri.parse(( ytFiles.get(140)).getUrl());
                    //TODO check if 136 is right
                    Uri videoUri = Uri.parse((ytFiles.get(134)).getUrl());
                    DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(ServiceController.this.context, MyTubeApplication.getUserAgent());
                    MediaSource audioSource = new ExtractorMediaSource(audioUri, dataSourceFactory, extractorsFactory, null, null);
                    MediaSource videoSource = new ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null);
                    MediaSource compositeSource = new MergingMediaSource(audioSource, videoSource);
                    setMetadata(youTubeSong);
                    player.prepare(compositeSource);
                    songPreparing = youTubeSong;
                    play();
                }
            }
        }.extract("http://youtube.com/watch?v=" + youTubeSong.getId(), false, false);
        this.callback.onLoading(this.player);
    }

    public void prepareForLocal(YouTubeSong youTubeSong) {
        setPlaybackState(PlaybackStateCompat.STATE_BUFFERING);
    }

    public void play() {
        if (this.service.requestAudiofocus(this.context)) {
            setPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            this.player.setPlayWhenReady(true);
            this.mediaSession.setActive(true);
            this.service.setConnectedToNoisyReciever(this.context, true);
            this.callback.onPlaying();
            return;
        }
        Toast.makeText(this.context, "audiofocus not granted", Toast.LENGTH_SHORT).show();
        pause();
    }

    public void pause() {
        setPlaybackState(PlaybackStateCompat.STATE_PAUSED);
        this.player.setPlayWhenReady(false);
        this.service.setConnectedToNoisyReciever(this.context, false);
        this.callback.onPaused();
    }

    public void stop() {
        pause();
        setPlaybackState(PlaybackStateCompat.STATE_STOPPED);
        this.player.stop();
        this.mediaSession.setActive(false);
        this.service.abandonAudioFocus(this.context);
        this.service.deleteNotification(true);
        this.callback.onStopped();
        stopService();
    }

    public int getPlaybackState() {
        return this.mediaSession.getController().getPlaybackState().getState();
    }

    private void setPlaybackState(Integer state) {
        Log.d(TAG, "setPlaybackState with state=" + this.playbackState.build().toString());
        if (this.player != null) {
            this.playbackState.setState(state, this.player.getCurrentPosition(), 1.0f);
        } else {
            this.playbackState.setState(state, 0, PLAYBACK_SPEED);
        }
        this.mediaSession.setPlaybackState(this.playbackState.build());
        this.service.updateNotification(this.context, this.mediaSession, state);
    }

    private void setMetadata(YouTubeSong youTubeSong) {
        MediaMetadataCompat.Builder metadata = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, youTubeSong.getTitle())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, youTubeSong.getId())
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, youTubeSong.getId())
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ART, BitmapFactory.decodeResource(this.context.getResources(), R.mipmap.ic_launcher))
               // .putString(MediaMetadataCompat.METADATA_KEY_ART_URI, youTubeSong.getImage().toString())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, this.player.getDuration());
        this.mediaSession.setMetadata(metadata.build());
        this.callback.onMetadataChanged(metadata.build());
    }

    public void setCallback(PlaybackStateCallback callback) {
        this.callback = callback;
    }
}