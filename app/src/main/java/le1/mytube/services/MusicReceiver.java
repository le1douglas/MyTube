package le1.mytube.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Toast;

import le1.mytube.mvpModel.Repo;

public class MusicReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Repo repo= new Repo(context);
        if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
           repo.pauseSong();
        } else if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                Toast.makeText(context, keyEvent.toString(), Toast.LENGTH_SHORT).show();
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                        repo.playOrPauseSong();
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PLAY:
                        repo.playSong();
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PAUSE:
                        repo.pauseSong();
                        break;
                    case KeyEvent.KEYCODE_MEDIA_STOP:
                        repo.stopMusicService();
                        break;
                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        repo.skipToNextSong();
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                       repo.skipToPreviusSong();
                        break;
                    default:
                        break;
                }
            }

        }
    }
}
