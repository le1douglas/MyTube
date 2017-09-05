package le1.mytube.services.notification;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import le1.mytube.mvpModel.Repo;
import le1.mytube.services.notification.musicNotification.MusicNotificationConstants;


public class NotificationReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Repo repo = new Repo(context);
        if (intent.getStringExtra(MusicNotificationConstants.KEY_NOTIFICATION) != null) {
            switch (intent.getStringExtra(MusicNotificationConstants.KEY_NOTIFICATION)) {
                case MusicNotificationConstants.EXTRA_PLAY_PAUSE:
                    repo.playOrPauseSong();
                    break;
                case MusicNotificationConstants.EXTRA_STOP:
                    repo.stopMusicService();
                    break;
                case MusicNotificationConstants.EXTRA_PREV:
                    repo.skipToPreviusSong();
                    break;
                case MusicNotificationConstants.EXTRA_NEXT:
                    repo.skipToNextSong();
                    break;
                default:
                    break;
            }
        }
    }
}

