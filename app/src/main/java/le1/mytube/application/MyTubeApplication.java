package le1.mytube.application;

import android.app.Application;

public class MyTubeApplication extends Application {
    public static final String KEY_SONG = "le1.mytube.MusicServiceConstants.key.song";

    public void onCreate() {
        super.onCreate();
        //ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppLifecycleObserver(this));
    }


}