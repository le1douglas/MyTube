package le1.mytube.notification;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import le1.mytube.mvpModel.Repo;
import le1.mytube.notification.musicNotification.MusicNotificationConstants;


public class NotificationReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Repo repo = new Repo(context);
        //TODO
        if (intent.getStringExtra(MusicNotificationConstants.KEY_NOTIFICATION) != null) {
            switch (intent.getStringExtra(MusicNotificationConstants.KEY_NOTIFICATION)) {
                case MusicNotificationConstants.EXTRA_PLAY_PAUSE:

                    break;
                case MusicNotificationConstants.EXTRA_STOP:

                    break;
                case MusicNotificationConstants.EXTRA_PREV:

                    break;
                case MusicNotificationConstants.EXTRA_NEXT:

                    break;
                default:
                    break;
            }
        }
    }
}

