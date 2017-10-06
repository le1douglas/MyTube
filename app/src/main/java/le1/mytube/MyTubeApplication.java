package le1.mytube;

import android.app.Application;

import le1.mytube.services.musicService.ServiceRepo;
import le1.mytube.services.musicService.ServiceRepoImpl;

public class MyTubeApplication extends Application {
    public static final String KEY_SONG = "le1.mytube.MusicServiceConstants.key.song";

    private ServiceRepo serviceRepo;
    public void onCreate() {
        super.onCreate();
        serviceRepo = new ServiceRepoImpl(this);
    }

    public ServiceRepo getServiceRepo() {
        return serviceRepo;
    }
}