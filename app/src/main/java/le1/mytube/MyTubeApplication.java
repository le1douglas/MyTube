package le1.mytube;

import android.app.Application;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection.Factory;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Util;

import le1.mytube.services.servicetest.ServiceController;

public class MyTubeApplication extends Application {
    private static String userAgent;
    private SimpleExoPlayer player;

    public void onCreate() {
        super.onCreate();
        userAgent = Util.getUserAgent(this, getString(R.string.app_name));
        this.player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector(new Factory(new DefaultBandwidthMeter())));
        ServiceController.getInstance(this);
    }

    public static String getUserAgent() {
        return userAgent;
    }

    public SimpleExoPlayer getExoPlayer() {
        return this.player;
    }
}