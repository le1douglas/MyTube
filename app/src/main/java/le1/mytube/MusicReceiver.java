package le1.mytube;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Toast;

import static le1.mytube.MusicService.pauseSong;
import static le1.mytube.MusicService.playSong;
import static le1.mytube.MusicService.player;

public class MusicReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {

            pauseSong(true);

        } else if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    if (player.isPlaying()) {
                        pauseSong(true);
                    } else {
                        playSong(true);
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    playSong(true);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    pauseSong(true);
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    context.stopService(new Intent(context, MusicService.class));
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    //TODO
                    Toast.makeText(context, "KEYCODE_MEDIA_NEXT", Toast.LENGTH_SHORT).show();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    Toast.makeText(context, "KEYCODE_MEDIA_PREVIOUS", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }

        }
    }
}
