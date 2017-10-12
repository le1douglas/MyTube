package le1.mytube;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import le1.mytube.services.musicService.ServiceRepo;
import le1.mytube.services.musicService.ServiceRepoImpl;

public class MyTubeApplication extends Application {
    public static final String KEY_SONG = "le1.mytube.MusicServiceConstants.key.song";

    private ServiceRepo serviceRepo;
    public void onCreate() {
        Toast.makeText(this, "ApplicationOncreate", Toast.LENGTH_SHORT).show();
        super.onCreate();
        serviceRepo = new ServiceRepoImpl(this);
    }

    public ServiceRepo getServiceRepo() {
        return serviceRepo;
    }

    public static boolean isMyServiceRunning(Context context,Class<?> serviceClass) {
        Context c = context.getApplicationContext();
        ActivityManager manager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}