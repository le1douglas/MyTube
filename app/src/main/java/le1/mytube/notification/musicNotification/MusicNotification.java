package le1.mytube.notification.musicNotification;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat.Builder;
import android.support.v7.app.NotificationCompat.MediaStyle;
import android.util.Log;

import le1.mytube.R;

public class MusicNotification {
    public static final int ID = 666;
    private static final String TAG = ("LE1_" + MusicNotification.class.getSimpleName());
    private static Action fastForwardAction = null;
    private static Action playPauseAction = null;
    private static Action rewindAction = null;
    private static Action skipToNextAction = null;
    private static Action skipToPreviousAction = null;
    private static int state;

    public static Builder updateNotification(Context context, MediaSessionCompat mediaSession, int state) {
        Log.d(TAG, "buildNotification called with state=" + String.valueOf(state));
        MusicNotification.state = state;
        Builder builder = new Builder(context);
        MediaDescriptionCompat description = null;
        if (mediaSession.getController().getMetadata() != null) {
            description = mediaSession.getController().getMetadata().getDescription();
        }
        switch (state) {
            case 2:
                setButtonsEnabled(true, context);
                builder.setContentTitle(description.getTitle())
                        .setContentText(description.getSubtitle())
                        .setSubText(description.getMediaId())
                        .setLargeIcon(description.getIconBitmap());
                break;
            case 3:
                setButtonsEnabled(true, context);
                builder.setContentTitle(description.getTitle())
                        .setContentText(description.getSubtitle())
                        .setSubText(description.getMediaId())
                        .setLargeIcon(description.getIconBitmap());
                break;
            case 6:
                setButtonsEnabled(false, context);
                builder.setContentTitle("loading")
                        .setContentText("loading")
                        .setSubText("loading")
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
                break;
            case 7:
                setButtonsEnabled(false, context);
                builder.setContentTitle("loading")
                        .setContentText("loading")
                        .setSubText("loading")
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
                break;
            default:
                throw new UnsupportedOperationException("unsupported state for music notification");
        }
        builder.
                setContentIntent(mediaSession.getController()
                        .getSessionActivity())
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, 1))
                .setVisibility(1).setSmallIcon(R.drawable.ic_notification_icon)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                .setShowWhen(false).addAction(rewindAction).addAction(skipToPreviousAction)
                .addAction(playPauseAction).addAction(skipToNextAction).addAction(fastForwardAction)
                .setStyle(new MediaStyle().setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(1, 2, 3).setShowCancelButton(true)
                        .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, 1)));
        return builder;
    }

    private static void setButtonsEnabled(boolean enabled, Context context) {
        Log.d(TAG, "setButtonsEnabled called with state=" + String.valueOf(state));
        if (enabled) {
            Log.d(TAG, "enabling buttons");
            if (state == 3) {
                playPauseAction = new Action(R.drawable.ic_pause_black_24dp, "play", MediaButtonReceiver.buildMediaButtonPendingIntent(context, 512));
            } else {
                playPauseAction = new Action(R.drawable.ic_play_arrow_black_24dp, "pause", MediaButtonReceiver.buildMediaButtonPendingIntent(context, 512));
            }
            skipToPreviousAction = new Action(R.drawable.ic_skip_previous_black_24dp, "previous", MediaButtonReceiver.buildMediaButtonPendingIntent(context, 16));
            skipToNextAction = new Action(R.drawable.ic_skip_next_black_24dp, "next", MediaButtonReceiver.buildMediaButtonPendingIntent(context, 32));
            rewindAction = new Action(R.drawable.ic_fast_rewind_black_24dp, "rewind", MediaButtonReceiver.buildMediaButtonPendingIntent(context, 8));
            fastForwardAction = new Action(R.drawable.ic_fast_forward_black_24dp, "fast forward", MediaButtonReceiver.buildMediaButtonPendingIntent(context, 64));
            return;
        }
        Log.d(TAG, "disabling buttons");
        playPauseAction = new Action(R.drawable.ic_play_arrow_disabled_24dp, "play", null);
        skipToPreviousAction = new Action(R.drawable.ic_skip_previous_disabled_24dp, "previous", null);
        skipToNextAction = new Action(R.drawable.ic_skip_next_disabled_24dp, "next", null);
        rewindAction = new Action(R.drawable.ic_fast_rewind_disabled_24dp, "rewind", null);
        fastForwardAction = new Action(R.drawable.ic_fast_forward_disabled_24dp, "fast forward", null);
    }
}