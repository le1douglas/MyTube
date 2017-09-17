package le1.mytube.notification.musicNotification;

import android.app.Notification;
import android.app.Service;
import android.content.Context;

import le1.mytube.mvpModel.database.song.YouTubeSong;

/**
 * Created by Leone on 29/08/17.
 */

public class MusicNotificationImpl_v24 extends MusicNotification{

    @Override
    public void buildNotification(Context context, Service service) {

    }

    @Override
    public Notification getNotification() {
        return null;
    }

    @Override
    public void setPlaying(YouTubeSong youTubeSong) {

    }

    @Override
    public void setLoading() {

    }

    @Override
    public void setPaused() {

    }

    @Override
    public void setError() {

    }
}
