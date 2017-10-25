package le1.mytube.services.musicService;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import le1.mytube.R;
import le1.mytube.listeners.PlaybackStateCompositeListener;
import le1.mytube.listeners.PlaybackStateListener;
import le1.mytube.mvpModel.database.song.YouTubeSong;

public class ServiceRepoImpl implements ServiceRepo {
    private static final String TAG = ("LE1_" + ServiceRepoImpl.class.getSimpleName());

    private MediaBrowserCompat mediaBrowser;
    private MusicService service;
    private Application context;


    private SimpleExoPlayer player;

    private PlaybackStateCompositeListener listener = new PlaybackStateCompositeListener();

    private List<YouTubeSong> currentSongs = new ArrayList<>();
    private boolean postProgressUpdate = false;

    private class ConnectionCallback extends MediaBrowserCompat.ConnectionCallback {
        @Override
        public void onConnected() {
            super.onConnected();
            Log.d(TAG, "onConnected");
            init();
        }

        @Override
        public void onConnectionFailed() {
            super.onConnectionFailed();
            Log.e(TAG, "onConnectionFailed");
        }

        @Override
        public void onConnectionSuspended() {
            super.onConnectionSuspended();
            Log.e(TAG, "onConnectionSuspended");
        }
    }

    public ServiceRepoImpl(@NonNull Application application) {
        context = application;
    }

    private void init() {
        Toast.makeText(context, "start service", Toast.LENGTH_SHORT).show();
        service = new MusicService();
        player = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter())));
        player.addListener(new PlayerListener());

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                if (postProgressUpdate) {
                    //convert from milliseconds to second
                    listener.onPositionChanged((int) player.getCurrentPosition() / 1000);
                }
            }
        }, 1000);
    }

    @Override
    public void startService() {
        context.startService(new Intent(context, MusicService.class));
        mediaBrowser = new MediaBrowserCompat(context, new ComponentName(context, MusicService.class), new ConnectionCallback(), null);
        mediaBrowser.connect();
    }

    @Override
    public void stopService() {
        if (getPlaybackState() != PlaybackStateCompat.STATE_STOPPED) stop();
        Toast.makeText(context, "stopService", Toast.LENGTH_SHORT).show();
        service.setPlaybackState(PlaybackStateCompat.STATE_NONE, player.getCurrentPosition());
        player.release();
        context.stopService(new Intent(context.getApplicationContext(), MusicService.class));
        this.service = null;
        mediaBrowser.disconnect();
    }


    @Override
    public void prepareStreaming(@NonNull final YouTubeSong youTubeSong) {
        Log.d(TAG, "prepareStreaming");
        currentSongs.clear();
        service.setPlaybackState(PlaybackStateCompat.STATE_BUFFERING, player.getCurrentPosition());
        new YouTubeExtractor(context) {
            @Override
            protected void onExtractionComplete(SparseArray<YtFile> itags, VideoMeta videoMeta) {
                if (videoMeta.isLiveStream()) {
                    Toast.makeText(context, "live streaming are not supported yet", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (itags != null) {
                    Log.d(TAG, "onExtractionComplete");
                    loadImage(youTubeSong);

                    for (int i = 0; i < itags.size(); i++) {
                        int key = itags.keyAt(i);
                        Log.d(TAG, "(" + i + ")itag at " + key + " = " + (itags.get(key)).getUrl());

                        //one of the youtube song that will arrive to the le1.mytube.ui
                        YouTubeSong yts2add= new YouTubeSong.Builder(videoMeta.getVideoId(), videoMeta.getTitle())
                                .duration((int) videoMeta.getVideoLength())
                                .format((itags.get(key)).getFormat())
                                .imageUri(Uri.parse(videoMeta.getHqImageUrl()))
                                .streamingUri(Uri.parse(itags.get(key).getUrl()))
                                .build();

                        currentSongs.add(yts2add);
                    }

                    Uri audioUri = Uri.parse((itags.get(140)).getUrl());
                    Uri videoUri = Uri.parse((itags.get(134)).getUrl());
                    player.prepare(buildMediaSource(videoUri, audioUri));
                    service.updateMetadata(youTubeSong);
                    play();

                } else {
                    listener.onError("ytfile are null");
                }
            }
        }.extract("http://youtube.com/watch?v=" + youTubeSong.getId(), true, true);
    }

    private void loadImage(final YouTubeSong youTubeSong) {
        if (youTubeSong.getImageUri() == null) {
            Log.w(TAG, "loadImage called without valid uri");
            return;
        }
        Picasso.with(context).load(youTubeSong.getImageUri()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                youTubeSong.setImageBitmap(bitmap);
                service.updateMetadata(youTubeSong);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
    }

    private MediaSource buildMediaSource(Uri videoUri, Uri audioUri) {
        String userAgent = Util.getUserAgent(context, context.getString(R.string.app_name));
        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, userAgent);
        MediaSource audioSource = new ExtractorMediaSource(audioUri, dataSourceFactory, extractorsFactory, null, null);
        MediaSource videoSource = new ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null);
        return new MergingMediaSource(audioSource, videoSource);

    }

    private void getMostAppropriateResolution(List<YouTubeSong> youTubeSongList) {
        for (YouTubeSong yts : youTubeSongList) {
            //TODO
        }
    }

    @Override
    public void prepareLocal(@NonNull YouTubeSong youTubeSong) {

    }

    private void prepareUri(Uri videoUri) {
        String userAgent = Util.getUserAgent(context, context.getString(R.string.app_name));
        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, userAgent);
        MediaSource videoSource = new ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null);
        player.prepare(videoSource);
        play();
    }


    @Override
    public void play() {
        Log.d(TAG, "play");
        if (service.requestAudioFocus()) {
            service.setPlaybackState(PlaybackStateCompat.STATE_PLAYING, player.getCurrentPosition());
            Log.d(TAG, "requestAudioFocus = true");
            player.setVolume(1.0f);
            player.setPlayWhenReady(true);
            service.setMediaSessionActive(true);
            service.setConnectedToNoisyReceiver(true);
            postProgressUpdate = true;
        } else {
            Toast.makeText(this.context, "audio focus not granted", Toast.LENGTH_SHORT).show();
            pause();

        }

    }

    @Override
    public void pause() {
        Log.d(TAG, "pause");
        service.setPlaybackState(PlaybackStateCompat.STATE_PAUSED, player.getCurrentPosition());
        player.setPlayWhenReady(false);
        service.setConnectedToNoisyReceiver(false);
        postProgressUpdate = false;
    }

    @Override
    public void playOrPause() {
        Log.d(TAG, "playOrPause");
        if (service.getPlaybackState() == PlaybackStateCompat.STATE_PLAYING)
            pause();
        else play();
    }

    @Override
    public void stop() {
        Log.d(TAG, "stop");
        player.setPlayWhenReady(false);
        service.setConnectedToNoisyReceiver(false);
        postProgressUpdate = false;
        currentSongs = null;
        service.setPlaybackState(PlaybackStateCompat.STATE_STOPPED, player.getCurrentPosition());
        player.stop();
        service.setMediaSessionActive(false);
        service.abandonAudioFocus();
        listener.onStopped();
    }

    @Override
    public void duck() {
        Log.d(TAG, "duck");
        player.setVolume(0.5f);
    }

    @Override
    public void seekTo(int position) {
        //convert from seconds to milliseconds
        Log.d(TAG, "seekTo pos=" + position * 1000);
        player.seekTo(position * 1000);
    }

    @Override
    public void addListener(PlaybackStateListener playbackStateListener) {
        listener.addListener(playbackStateListener);
    }

    @Override
    public void setView(SimpleExoPlayerView exoPlayerView) {
        exoPlayerView.setPlayer(player);

    }

    @Override
    public int getPlaybackState() {
        return service.getPlaybackState();
    }

    @Override
    public int getPlaybackPosition() {
        return (int) player.getCurrentPosition() / 1000;
    }

    @Nullable
    public List<YouTubeSong> getCurrentSongs() {
        return currentSongs;
    }

    private class PlayerListener implements Player.EventListener {

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            if (isLoading) listener.onLoadingStarted();
            else listener.onLoadingFinished();
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case Player.STATE_READY:
                    if (playWhenReady) {
                        listener.onPlaying(currentSongs);
                    } else {
                        listener.onPaused();
                    }
                    break;
                case Player.STATE_IDLE:
                    break;
                case Player.STATE_ENDED:
                    service.setPlaybackState(PlaybackStateCompat.STATE_NONE, player.getCurrentPosition());
                    listener.onStopped();
                    currentSongs.clear();
                    break;
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            listener.onError(error.getMessage());
        }

        @Override
        public void onPositionDiscontinuity() {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }
    }
}
