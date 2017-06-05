package le1.mytube;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import static le1.mytube.MusicService.afChangeListener;
import static le1.mytube.MusicService.audioManager;
import static le1.mytube.MusicService.notification;
import static le1.mytube.MusicService.pauseSong;
import static le1.mytube.MusicService.playSong;
import static le1.mytube.MusicService.player;
import static le1.mytube.MusicService.remoteView;


public class NotificationClickHandler extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (intent.getStringExtra("NOT") == null) Log.i("NOTIFICATION", "NULL");

        else if (intent.getStringExtra("NOT").equals("0")) {
            Log.i("NOTIFICATION", "0");
            if (player.isPlaying()) {
                pauseSong(true);
            }else {
                playSong(true);
            }
        }
    }
}

