package le1.mytube.application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;


class AppLifecycleObserver implements LifecycleObserver {

    private Context context;

    AppLifecycleObserver(Context context) {
        this.context = context;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        if (!MyTubeApplication.isMusicServiceRunning(context)){
            ((MyTubeApplication) context.getApplicationContext()).getServiceRepo().startService();
        }
    }
}
