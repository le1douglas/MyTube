package le1.mytube.ui.musicPlayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import le1.mytube.R;
import le1.mytube.application.MyTubeApplication;
import le1.mytube.listeners.PlaybackStateListener;
import le1.mytube.mvpModel.MusicControl;
import le1.mytube.mvpModel.database.song.YouTubeSong;

/**
 * A {@link View} that mimics the player overlay of youtube.
 * This view does not contain th player, it's just an overlay
 */
public class PlayerOverlayView extends RelativeLayout implements PlaybackStateListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "LE1_PlayerOverlayView";


    private TextView titleView;
    private TextView currentTimeView;
    private TextView totalTimeView;
    private ProgressBar loadingIcon;
    private SeekBar seekbar;
    private ImageButton playPauseButton;
    private Button retryButton;

    private MusicControl musicControl;
    private boolean isUiVisible;

    final Handler autoHideHandler = new Handler();
    Runnable autoHideRunnable = new Runnable() {
        @Override
        public void run() {
            hideUi();
        }

    };
    private static final int autoHideMs = 2500;

    private SharedPreferences sharedPref;
    private static final String SHARED_PREF_NAME = "sharedPref_PlayerOverlayView";
    private static final String SHARED_PREF_IS_UI_VISIBLE_KEY = "sharedPref_isUiVisible";


    public PlayerOverlayView(Context context) {
        this(context, null, 0);
    }

    public PlayerOverlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Inflates the {@link R.layout#player_overlay} layout used for the ui.
     * Starts an {@link Handler} that updates the {@link #seekbar} every second.
     */
    public PlayerOverlayView(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        sharedPref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        musicControl = ((MyTubeApplication) context.getApplicationContext()).getMusicControl();
        musicControl.addListener(this);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.player_overlay, this);

        titleView = view.findViewById(R.id.title);
        loadingIcon = view.findViewById(R.id.loading_icon);
        currentTimeView = view.findViewById(R.id.current_time);
        totalTimeView = view.findViewById(R.id.total_time);
        seekbar = view.findViewById(R.id.seek_bar);
        retryButton= view.findViewById(R.id.retry_button);
        playPauseButton = view.findViewById(R.id.play_pause);

        seekbar.setOnSeekBarChangeListener(this);

        playPauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicControl.getPlaybackState() == PlaybackStateCompat.STATE_STOPPED) {
                    retryButton.callOnClick();
                } else {
                    musicControl.playOrPause();
                }
            }
        });

        retryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                YouTubeSong youTubeSong = new YouTubeSong.Builder(
                        musicControl.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID),
                        null)
                        .build();

                musicControl.prepareAndPlay(youTubeSong);
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if (isAttachedToWindow()) {
                            handler.postDelayed(this, 1000);
                            seekbar.setProgress(musicControl.getCurrentPosition());
                            currentTimeView.setText(formatMilliseconds(musicControl.getCurrentPosition()));
                        }
                    }
                }, 1000);
    }

    /**
     * Update and show the ui to reflect a {@link PlaybackStateCompat} state.
     * Automatically calls {@link #hideUi()} after {@link #autoHideMs} milliseconds
     *
     * @param playbackState one of {@link PlaybackStateCompat#STATE_BUFFERING}
     *                      {@link PlaybackStateCompat#STATE_PLAYING}
     *                      {@link PlaybackStateCompat#STATE_PAUSED}
     *                      {@link PlaybackStateCompat#STATE_STOPPED}
     *                      {@link PlaybackStateCompat#STATE_ERROR}
     * @param metadata      the metadata used to build the ui
     */
    public void updateUi(int playbackState, MediaMetadataCompat metadata) {
        isUiVisible = true;

        if (metadata != null) {
            titleView.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
            seekbar.setMax((int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
            seekbar.setProgress(musicControl.getCurrentPosition());
            currentTimeView.setText(formatMilliseconds(musicControl.getCurrentPosition()));
            totalTimeView.setText(formatMilliseconds((int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)));

        }
        switch (playbackState) {
            case PlaybackStateCompat.STATE_BUFFERING:
                playPauseButton.setVisibility(GONE);
                loadingIcon.setVisibility(VISIBLE);
                titleView.setVisibility(VISIBLE);
                seekbar.setVisibility(VISIBLE);
                currentTimeView.setVisibility(VISIBLE);
                totalTimeView.setVisibility(VISIBLE);
                retryButton.setVisibility(GONE);

                if (titleView.getText().toString().equals("")) titleView.setText("Loading");
                seekbar.setActivated(true);
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                playPauseButton.setVisibility(VISIBLE);
                loadingIcon.setVisibility(GONE);
                titleView.setVisibility(VISIBLE);
                seekbar.setVisibility(VISIBLE);
                currentTimeView.setVisibility(VISIBLE);
                totalTimeView.setVisibility(VISIBLE);
                retryButton.setVisibility(GONE);

                // If the handler it's already started, stop it and restart it,
                // so that the runnable it's called only after the last call to this method
                autoHideHandler.removeCallbacks(autoHideRunnable);
                autoHideHandler.postDelayed(autoHideRunnable, autoHideMs);

                seekbar.setActivated(true);
                playPauseButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.exo_controls_pause));
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                playPauseButton.setVisibility(VISIBLE);
                loadingIcon.setVisibility(GONE);
                titleView.setVisibility(VISIBLE);
                seekbar.setVisibility(VISIBLE);
                currentTimeView.setVisibility(VISIBLE);
                totalTimeView.setVisibility(VISIBLE);
                retryButton.setVisibility(GONE);

                seekbar.setActivated(true);
                playPauseButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.exo_controls_play));

                break;
            case PlaybackStateCompat.STATE_STOPPED:
                playPauseButton.setVisibility(VISIBLE);
                loadingIcon.setVisibility(GONE);
                titleView.setVisibility(GONE);
                seekbar.setVisibility(GONE);
                currentTimeView.setVisibility(GONE);
                totalTimeView.setVisibility(GONE);
                retryButton.setVisibility(GONE);

                playPauseButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.exo_controls_play));
                seekbar.setActivated(false);
                break;
            case PlaybackStateCompat.STATE_ERROR:
                playPauseButton.setVisibility(GONE);
                loadingIcon.setVisibility(GONE);
                titleView.setVisibility(VISIBLE);
                seekbar.setVisibility(GONE);
                currentTimeView.setVisibility(GONE);
                totalTimeView.setVisibility(GONE);
                retryButton.setVisibility(VISIBLE);

                titleView.setText("Error");
                seekbar.setActivated(false);
                break;
            default:
                break;

        }
    }

    /**
     * Hides all the ui controls for a more immersive experience.
     * Ui can be made visible again with {@link #updateUi(int, MediaMetadataCompat)}
     */
    public void hideUi() {
        isUiVisible = false;
        playPauseButton.setVisibility(GONE);
        loadingIcon.setVisibility(GONE);
        titleView.setVisibility(GONE);
        seekbar.setVisibility(GONE);
        currentTimeView.setVisibility(GONE);
        totalTimeView.setVisibility(GONE);
        retryButton.setVisibility(GONE);
    }


    /**
     * Restores the {@link #seekbar} and the correct view state
     * as soon as this view is visible.
     *
     * @see View#onAttachedToWindow()
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isUiVisible = sharedPref.getBoolean(SHARED_PREF_IS_UI_VISIBLE_KEY, true);
        if (musicControl.isConnected()) {
            if (isUiVisible)
                updateUi(musicControl.getPlaybackState(), musicControl.getMetadata());
            else hideUi();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(SHARED_PREF_IS_UI_VISIBLE_KEY, isUiVisible);
        editor.commit();
    }

    /**
     * Called every time the user touches the view and no other sub-view catches the
     * {@link MotionEvent} (for example, if a button is touched, this method is not called)
     * Toggles the visibility of the ui
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isUiVisible) hideUi();
        else updateUi(musicControl.getPlaybackState(), musicControl.getMetadata());
        return super.onTouchEvent(event);
    }

    /**
     * Converts milliseconds to a human readable string formatted as mm:ss
     *
     * @param time the number of milliseconds to convert
     * @return a human readable timestamp
     */
    private String formatMilliseconds(int time) {
        time = time / 1000;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return String.format(this.getResources().getConfiguration().getLocales().get(0),
                    "%02d:%02d", time / 60, time % 60);
        } else {
            //noinspection deprecation
            return String.format(this.getResources().getConfiguration().locale,
                    "%02d:%02d", time / 60, time % 60);
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    /**
     * Calls {@link MusicControl#seekTo(int)} only when user releases the finger
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d(TAG, "onStopTrackingTouch: " + seekbar.getProgress());
        musicControl.seekTo(seekbar.getProgress());
    }


    /**
     * @see PlaybackStateListener#onMetadataLoaded(MediaMetadataCompat)
     */
    @Override
    public void onMetadataLoaded(MediaMetadataCompat metadata) {
        updateUi(musicControl.getPlaybackState(), metadata);
    }

    /**
     * For every playback action,
     * calls {@link #updateUi(int, MediaMetadataCompat)} with the appropriate {@link PlaybackStateCompat} state.
     */
    @Override
    public void onLoading() {
        updateUi(PlaybackStateCompat.STATE_BUFFERING, musicControl.getMetadata());
    }

    @Override
    public void onPaused() {
        updateUi(PlaybackStateCompat.STATE_PAUSED, musicControl.getMetadata());
    }

    @Override
    public void onPlaying() {
        updateUi(PlaybackStateCompat.STATE_PLAYING, musicControl.getMetadata());
    }

    @Override
    public void onStopped() {
        updateUi(PlaybackStateCompat.STATE_STOPPED, musicControl.getMetadata());
    }

    @Override
    public void onError(String error) {
        updateUi(PlaybackStateCompat.STATE_ERROR, musicControl.getMetadata());
    }

}

