package le1.mytube;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Created by leone on 06/10/17.
 */

public class MediaReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (event == null) {
                Log.w("MEDIARECIEVER", "event is null");
            }
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                dispatchKeyEvent(context.getApplicationContext(), event);
            }
        }
    }


    public void dispatchKeyEvent(Context application, KeyEvent event) {
        int keyCode = event.getKeyCode();
        final boolean uniqueDown = event.getRepeatCount() == 0
                && event.getAction() == KeyEvent.ACTION_DOWN;
        if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                || keyCode == KeyEvent.KEYCODE_SPACE) {
            if (uniqueDown) {
                ((MyTubeApplication) application).getServiceRepo().playOrPause();
            }
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
            if (uniqueDown && !(((MyTubeApplication) application).getServiceRepo().getPlaybackState()
                    == PlaybackStateCompat.STATE_PLAYING)) {
                ((MyTubeApplication) application).getServiceRepo().play();
            }
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
            if (uniqueDown && (((MyTubeApplication) application).getServiceRepo().getPlaybackState()
                    == PlaybackStateCompat.STATE_PLAYING)) {
                ((MyTubeApplication) application).getServiceRepo().pause();

            }
        }else  if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP){
            Log.d("MEDIARECIEVER", "KEYCODE_MEDIA_STOP");

            ((MyTubeApplication) application).getServiceRepo().stop();
        }
    }

}
