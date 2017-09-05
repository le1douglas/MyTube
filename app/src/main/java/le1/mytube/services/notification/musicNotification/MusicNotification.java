package le1.mytube.services.notification.musicNotification;

/**
 * Created by Leone on 29/08/17.
 */


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import le1.mytube.mvpModel.songs.YouTubeSong;
import le1.mytube.mvpViews.MainActivity;

import static le1.mytube.services.notification.musicNotification.MusicNotificationConstants.ID;

public abstract class MusicNotification {

    private Service service;


    abstract public void buildNotification(Context context, Service service);

    abstract public Notification getNotification();

    public void start(Service service, Notification notification) {
        this.service = service;
        service.startForeground(ID, notification);
    }

    abstract public void setPlaying(YouTubeSong youTubeSong);

    abstract public void setLoading();

    abstract public void setPaused();

    abstract public void setError();

    public void setDismissable() {
        service.stopForeground(false);
    }

    public void stop(){
        service.stopForeground(true);
    }

    public boolean isVisible(Context context) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent test = PendingIntent.getActivity(context, ID, notificationIntent, PendingIntent.FLAG_NO_CREATE);
        return test != null;
    }

}