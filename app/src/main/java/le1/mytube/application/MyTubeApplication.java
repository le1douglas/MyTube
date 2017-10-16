package le1.mytube.application;

import android.app.ActivityManager;
import android.app.Application;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.widget.Toast;

import le1.mytube.services.musicService.MusicService;
import le1.mytube.services.musicService.ServiceRepo;
import le1.mytube.services.musicService.ServiceRepoImpl;

public class MyTubeApplication extends Application {
    public static final String KEY_SONG = "le1.mytube.MusicServiceConstants.key.song";

    private ServiceRepo serviceRepo;

    public void onCreate() {
        Toast.makeText(this, "ApplicationOnCreate", Toast.LENGTH_SHORT).show();
        super.onCreate();
        serviceRepo = new ServiceRepoImpl(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppLifecycleObserver(this));
    }

    public ServiceRepo getServiceRepo() {
        return serviceRepo;
    }

    public static boolean isMusicServiceRunning(Context context) {
        Context c = context.getApplicationContext();
        ActivityManager manager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MusicService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}