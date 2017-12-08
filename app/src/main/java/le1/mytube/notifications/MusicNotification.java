package le1.mytube.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.app.NotificationCompat.MediaStyle;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.PlaybackStateCompat;

import le1.mytube.R;
import le1.mytube.services.musicService.MediaSessionManager;
import le1.mytube.services.musicService.MusicService;
import le1.mytube.ui.musicPlayer.MusicPlayerActivity;


/**
 * The {@link NotificationCompat} of the {@link MusicService}.
 * It also handles the {@link Service#startForeground(int, Notification)}
 * and {@link Service#stopForeground(boolean)}
 */
public class MusicNotification {
    private static final int NOTIFICATION_ID = 666;
    private static final String CHANNEL_ID = "music";
    private static final String TAG = ("LE1_" + MusicNotification.class.getSimpleName());

    /**
     * Build and Update the notification based on the playback state
     * Should be called after every {@link MediaSessionManager#setPlaybackState(int, long, String)} call.
     *
     * @param service      The music service tied to the notification
     * @param mediaSession The {@link MediaSessionManager} tied to the music service
     */
    public static void updateNotification(Service service, MediaSessionManager mediaSession) {
        int state = mediaSession.getPlaybackState();
        Context context = service.getApplicationContext();

        Bitmap image = null;
        String title = null;
        String artist = null;
        String mediaId = null;
        if (mediaSession.getMetadata() != null) {
            image = mediaSession.getMetadata().getBitmap(MediaMetadataCompat.METADATA_KEY_ART);
            if (image == null) image = BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.ic_launcher);

            title = mediaSession.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_TITLE);
            if (title == null) {
                title = "error getting title";
            }

            artist = mediaSession.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
            if (artist == null) {
                artist = "error getting artist";
            }

            mediaId = mediaSession.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
            if (mediaId == null) {
                mediaId = "error getting id";
            }
        }


        switch (state) {
            case PlaybackStateCompat.STATE_PAUSED:
                //first we update the notification...
                service.startForeground(NOTIFICATION_ID,
                        getPlaybackButtonsNotificationBuilder(context, mediaSession)
                                .setLargeIcon(image)
                                .setContentTitle(title)
                                .setContentText(artist)
                                .setSubText(mediaId)
                                .build()
                );
                //...and then we make it dismissible
                service.stopForeground(false);
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                service.startForeground(NOTIFICATION_ID,
                        getPlaybackButtonsNotificationBuilder(context, mediaSession)
                                .setLargeIcon(image)
                                .setContentTitle(title)
                                .setContentText(artist)
                                .setSubText(mediaId)
                                .build());
                break;
            case PlaybackStateCompat.STATE_BUFFERING:
                service.startForeground(NOTIFICATION_ID,
                        getCancelButtonNotificationBuilder(context, mediaSession)
                                .setLargeIcon(image)
                                .setContentTitle("loading")
                                .setContentText("loading")
                                .setSubText("loading")
                                .build());
                break;
            case PlaybackStateCompat.STATE_ERROR:
                //first we update the notification...
                service.startForeground(NOTIFICATION_ID,
                        getCancelButtonNotificationBuilder(context, mediaSession)
                        .setLargeIcon(image)
                        .setContentTitle(mediaSession.getErrorMessage())
                        .setContentText("error")
                        .setSubText("error")
                        .build());
                //...and then we make it dismissible
                service.stopForeground(false);
                break;
            case PlaybackStateCompat.STATE_NONE:
                service.stopForeground(true);
                break;
            case PlaybackStateCompat.STATE_STOPPED:
                service.stopForeground(true);
                break;
            default:
                break;
        }
    }

    /**
     * @param context Application {@link Context}
     * @param mediaSession The {@link MediaSessionManager} used to build the notification
     * @return A {@link NotificationCompat.Builder} of a notification with all the playback controls
     */
    private static Builder getPlaybackButtonsNotificationBuilder(Context context, MediaSessionManager mediaSession) {
        Action skipToPreviousAction = new Action(R.drawable.ic_skip_previous_black_24dp, "previous", MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));
        Action skipToNextAction = new Action(R.drawable.ic_skip_next_black_24dp, "next", MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT));
        Action rewindAction = new Action(R.drawable.ic_fast_rewind_black_24dp, "rewind", MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_REWIND));
        Action fastForwardAction = new Action(R.drawable.ic_fast_forward_black_24dp, "fast forward", MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_FAST_FORWARD));
        Action playPauseAction;
        if (mediaSession.getPlaybackState() == PlaybackStateCompat.STATE_PLAYING) {
            playPauseAction = new Action(R.drawable.ic_pause_black_24dp, "playing", MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PLAY_PAUSE));
        } else {
            playPauseAction = new Action(R.drawable.ic_play_arrow_black_24dp, "paused", MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PLAY_PAUSE));
        }

        return getCommonNotificationBuilder(context)
                .addAction(skipToPreviousAction)
                .addAction(rewindAction)
                .addAction(playPauseAction)
                .addAction(fastForwardAction)
                .addAction(skipToNextAction)
                .setStyle(getCommonMediaStyle(context, mediaSession)
                        .setShowActionsInCompactView(0, 2, 4));

    }

    /**
     * @param context Application {@link Context}
     * @param mediaSession The {@link MediaSessionManager} used to build the notification
     * @return A {@link NotificationCompat.Builder} of a notification with only a button to dismiss it
     */
    private static Builder getCancelButtonNotificationBuilder(Context context, MediaSessionManager mediaSession) {
        Action cancelAction = new Action(R.drawable.ic_clear_black_24dp, "cancel", MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP));

        return getCommonNotificationBuilder(context)
                .addAction(cancelAction)
                .setStyle(getCommonMediaStyle(context, mediaSession)
                        .setShowActionsInCompactView(0));

    }

    /**
     * Do not use this method directly,
     * instead use {@link #getCancelButtonNotificationBuilder(Context, MediaSessionManager)}
     * or {@link #getPlaybackButtonsNotificationBuilder(Context, MediaSessionManager)}
     * @param context Application {@link Context}
     * @return A potentially incomplete {@link NotificationCompat.Builder} used by every notification type
     */
    private static Builder getCommonNotificationBuilder(Context context) {
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MusicPlayerActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent cancelIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP);

        return new Builder(context, CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setDeleteIntent(cancelIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                .setShowWhen(false);
    }

    /**
     * Do not use this method directly,
     * instead use {@link #getCancelButtonNotificationBuilder(Context, MediaSessionManager)}
     * or {@link #getPlaybackButtonsNotificationBuilder(Context, MediaSessionManager)}
     * @param context Application {@link Context}
     * @return A potentially incomplete {@link MediaStyle} used by every notification type
     */
    private static MediaStyle getCommonMediaStyle(Context context, MediaSessionManager mediaSession) {
        PendingIntent cancelIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP);
        return new MediaStyle()
                .setMediaSession(mediaSession
                        .getToken())
                .setShowCancelButton(true)
                .setCancelButtonIntent(cancelIntent);
    }
}