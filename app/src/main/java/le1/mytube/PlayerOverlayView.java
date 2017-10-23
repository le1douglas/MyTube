package le1.mytube;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import le1.mytube.application.MyTubeApplication;
import le1.mytube.listeners.PlaybackStateListener;
import le1.mytube.mvpModel.database.song.YouTubeSong;

public class PlayerOverlayView extends RelativeLayout implements PlaybackStateListener {
    private Spinner spinner;
    private TextView titleView;
    private SeekBar seekbar;
    private ImageButton playPause;
    private TextView duration;
    private TextView current;

    private OnClickListener clientListener;
    private ArrayAdapter<String> spinnerAdapter;

    private TypedArray a;
    private ViewState state;
    private Context context;

    private enum ViewState {
        CONTROLS_HIDDEN,
        CONTROLS_VISIBLE
    }

    @Override
    public void onLoadingStarted() {

    }

    @Override
    public void onLoadingFinished() {

    }

    @Override
    public void onPositionChanged(int currentTimeInSec) {
        current.setText(formatSeconds(currentTimeInSec));
    }

    @Override
    public void onPaused() {
        setState(ViewState.CONTROLS_VISIBLE);

    }

    @Override
    public void onPlaying(List<YouTubeSong> currentSongs) {
        setState(ViewState.CONTROLS_VISIBLE);

    }

    @Override
    public void onStopped() {
        setState(ViewState.CONTROLS_VISIBLE);
    }

    @Override
    public void onError(String message) {
        setState(ViewState.CONTROLS_VISIBLE);

    }

    public void setSpinnerOnItemSelected(Spinner.OnItemSelectedListener listener) {
        spinner.setOnItemSelectedListener(listener);
    }

    public void setSpinnerContent(List<YouTubeSong> spinnerContent) {
        spinnerAdapter.clear();
        for (YouTubeSong yts :
                spinnerContent) {

            StringBuilder sb = new StringBuilder();
            sb.append("(")
                    .append(yts.getFormat().getItag())
                    .append(") ");
            if (yts.getFormat().getHeight() == -1) {
                sb.append("[Audio Only] ")
                        .append("NULL")
                        .append(" | ")
                        .append(yts.getFormat().getAudioBitrate())
                        .append(" kbit/s");
            } else if (yts.getFormat().getAudioBitrate() == -1) {
                sb.append("[Video Only] ")
                        .append(yts.getFormat().getHeight())
                        .append("p | ")
                        .append("NO AUDIO");
            } else {
                sb.append("[Audio Video] ")
                        .append(yts.getFormat().getHeight())
                        .append("p | ")
                        .append(yts.getFormat().getAudioBitrate())
                        .append(" kbit/s");

            }

            sb.append(" | ")
                    .append(yts.getFormat().getExt());
            spinnerAdapter.add(sb.toString());
        }
        spinnerAdapter.notifyDataSetChanged();
    }


    void init(Context context, AttributeSet attributeSet, int defStyleAttr) {
        this.context = context;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.player_overlay, this);

        titleView = view.findViewById(R.id.title);
        seekbar = view.findViewById(R.id.seekBar);
        playPause = view.findViewById(R.id.playpause);
        spinner = view.findViewById(R.id.spinner);
        duration = view.findViewById(R.id.duration);
        current = view.findViewById(R.id.current);

        spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);

        spinner.setAdapter(spinnerAdapter);


        playPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clientListener.onClick(v);
            }
        });

        setState(ViewState.CONTROLS_VISIBLE);
        ((MyTubeApplication) context.getApplicationContext()).getServiceRepo().addListener(this);

        if (attributeSet != null) {
            a = context.getTheme().obtainStyledAttributes(
                    attributeSet,
                    R.styleable.PlayerOverlayView,
                    defStyleAttr, 0);
        }
        String title = a.getString(R.styleable.PlayerOverlayView_title);
        titleView.setText(title);
        int color = a.getColor(R.styleable.PlayerOverlayView_titleTextColor, defStyleAttr);
        titleView.setTextColor(color);
        a.recycle();
    }

    public PlayerOverlayView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public PlayerOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public PlayerOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("PlayerView", "ontouchEvent with state=" + state);
        if (state == ViewState.CONTROLS_VISIBLE) {
            setState(ViewState.CONTROLS_HIDDEN);
        } else if (state == ViewState.CONTROLS_HIDDEN) {
            setState(ViewState.CONTROLS_VISIBLE);
        }
        return super.onTouchEvent(event);

    }

    public void setTitle(@NonNull String title) {
        titleView.setText(title);
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener) {
        seekbar.setOnSeekBarChangeListener(listener);
    }

    public void setMaxProgress(int max) {
        seekbar.setMax(max);
        duration.setText(formatSeconds(max));
    }

    public void setProgress(int progress) {
        seekbar.setProgress(progress);
    }

    public void setButtonOnClickListener(OnClickListener onClick) {
        clientListener = onClick;
        playPause.setOnClickListener(onClick);
    }

    private String formatSeconds(int time) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return String.format(context.getResources().getConfiguration().getLocales().get(0), "%02d:%02d", time / 60, time % 60);
        } else{
            //noinspection deprecation
            return String.format(context.getResources().getConfiguration().locale, "%02d:%02d", time / 60, time % 60);
        }
    }

    private void setState(ViewState state) {
        switch (state) {
            case CONTROLS_HIDDEN:
                this.state = ViewState.CONTROLS_HIDDEN;
                titleView.setVisibility(GONE);
                seekbar.setVisibility(GONE);
                playPause.setVisibility(GONE);
                spinner.setVisibility(GONE);
                duration.setVisibility(GONE);
                current.setVisibility(GONE);
                break;
            case CONTROLS_VISIBLE:
                this.state = ViewState.CONTROLS_VISIBLE;
                titleView.setVisibility(VISIBLE);
                seekbar.setVisibility(VISIBLE);
                playPause.setVisibility(VISIBLE);
                spinner.setVisibility(VISIBLE);
                duration.setVisibility(VISIBLE);
                current.setVisibility(VISIBLE);
                if (((MyTubeApplication) context.getApplicationContext()).getServiceRepo().getPlaybackState() == PlaybackStateCompat.STATE_PLAYING) {
                    playPause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.exo_controls_pause));
                } else {
                    playPause.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.exo_controls_play));
                }
                break;
        }
    }

}
