package le1.mytube.domain.application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;

import le1.mytube.domain.services.musicService.MusicService;

/**
 * A {@link LifecycleObserver} that listens to changes in the whole app
 */
public class AppLifecycleObserver implements LifecycleObserver {
    private static final String TAG = "AppLifecycleObserver";
    private Context context;

    AppLifecycleObserver(Context context) {
        this.context = context;
    }

    /**
     * Called at launch and every time the app goes from the recents screen to foreground.
     * Connects to the {@link MusicService} as soon as possible
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume() {
        MyTubeApplication.setAppOpen(true);
        ((MyTubeApplication) context.getApplicationContext()).getMusicControl().connect();
    }

}
