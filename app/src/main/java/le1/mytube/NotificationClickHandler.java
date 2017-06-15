package le1.mytube;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static le1.mytube.MusicService.pauseSong;
import static le1.mytube.MusicService.playSong;
import static le1.mytube.MusicService.player;


public class NotificationClickHandler extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if (intent.getStringExtra("NOT") != null) {
            switch (intent.getStringExtra("NOT")) {
                case "play":
                    if (player.isPlaying()) {
                        pauseSong(true);
                    } else {
                        playSong(true);
                    }
                    break;
                case "stop":
                    context.stopService(new Intent(context, MusicService.class));
                    break;

            }
        }
    }
}

