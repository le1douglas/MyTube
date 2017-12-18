package le1.mytube.domain.services.musicService.managers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;

/**
 * Catches media button actions, mainly headphones buttons and similar
 */
public class MediaButtonManager {
    private final MediaSessionCompat mediaSession;

     MediaButtonManager(Context context, MediaSessionCompat mediaSession){
        this.mediaSession = mediaSession;
        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(context, MediaButtonReceiver.class);
        PendingIntent mediaButtonPendingIntent = PendingIntent.getBroadcast
                (context, 0, mediaButtonIntent, 0);
        this.mediaSession.setMediaButtonReceiver(mediaButtonPendingIntent);
    }

    public void handleIntent(Intent intent){
        MediaButtonReceiver.handleIntent(mediaSession, intent);
    }
}
