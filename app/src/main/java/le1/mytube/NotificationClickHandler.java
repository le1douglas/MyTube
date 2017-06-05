package le1.mytube;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static le1.mytube.MusicService.pauseSong;
import static le1.mytube.MusicService.playSong;
import static le1.mytube.MusicService.player;



public class NotificationClickHandler extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
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

