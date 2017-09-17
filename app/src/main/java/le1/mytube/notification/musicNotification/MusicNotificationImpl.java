package le1.mytube.notification.musicNotification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import le1.mytube.R;
import le1.mytube.mvpModel.database.song.YouTubeSong;
import le1.mytube.mvpViews.MainActivity;
import le1.mytube.notification.NotificationReceiver;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class MusicNotificationImpl extends MusicNotification {

    private RemoteViews smallRemoteView;
    private RemoteViews bigRemoteView;
    private RemoteViews[] arrayRemoteView = new RemoteViews[2];
    private NotificationManager notificationManager;
    private Notification notification;

    @Override
    public void buildNotification(Context context, Service service) {
        smallRemoteView = new RemoteViews(context.getPackageName(), R.layout.notification_small);
        bigRemoteView = new RemoteViews(context.getPackageName(), R.layout.notification_big);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        arrayRemoteView[0] = smallRemoteView;
        arrayRemoteView[1] = bigRemoteView;

        //play button
        final Intent play = new Intent(context, NotificationReceiver.class);
        play.putExtra(MusicNotificationConstants.KEY_NOTIFICATION, MusicNotificationConstants.EXTRA_PLAY_PAUSE);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        for (RemoteViews remote : arrayRemoteView)
            remote.setOnClickPendingIntent(R.id.action_play_pause, playPendingIntent);

        //previus track button
        final Intent prev = new Intent(context, NotificationReceiver.class);
        prev.putExtra(MusicNotificationConstants.KEY_NOTIFICATION, MusicNotificationConstants.EXTRA_PREV);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(context, 1, prev, PendingIntent.FLAG_UPDATE_CURRENT);
        for (RemoteViews remote : arrayRemoteView)
            remote.setOnClickPendingIntent(R.id.action_prev, prevPendingIntent);

        //next track button
        final Intent next = new Intent(context, NotificationReceiver.class);
        next.putExtra(MusicNotificationConstants.KEY_NOTIFICATION, MusicNotificationConstants.EXTRA_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 2, next, PendingIntent.FLAG_UPDATE_CURRENT);
        for (RemoteViews remote : arrayRemoteView)
            remote.setOnClickPendingIntent(R.id.action_next, nextPendingIntent);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        Bitmap bm = BitmapFactory.decodeResource(Resources.getSystem(), android.R.drawable.ic_lock_lock);
        notification = new NotificationCompat.Builder(context.getApplicationContext())
                .setLargeIcon(bm)
                .setShowWhen(true)
                .setWhen(SystemClock.currentThreadTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(smallRemoteView)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(notificationPendingIntent)
                .build();

        notification.bigContentView = bigRemoteView;
    }

    @Override
    public Notification getNotification() {
        if (notification == null)
            throw new IllegalStateException("getNotification() called before buildNotification()");
        return notification;
    }

    @Override
    public void setPlaying(YouTubeSong youTubeSong) {
        if (notification == null)
            throw new IllegalStateException("setPlaying() called before buildNotification()");
        for (RemoteViews remote : arrayRemoteView) {
            remote.setImageViewResource(R.id.album_image, R.mipmap.ic_launcher);
            remote.setImageViewResource(R.id.action_play_pause, R.drawable.ic_pause_black_24dp);
            remote.setImageViewResource(R.id.action_next, R.drawable.ic_skip_next_black_24dp);
            remote.setImageViewResource(R.id.action_prev, R.drawable.ic_skip_previous_black_24dp);

            remote.setBoolean(R.id.action_play_pause, "setEnabled", true);
            remote.setBoolean(R.id.action_next, "setEnabled", true);
            remote.setBoolean(R.id.action_prev, "setEnabled", true);
            if (youTubeSong != null) {
                remote.setTextViewText(R.id.title, youTubeSong.getTitle());
                remote.setTextViewText(R.id.text, youTubeSong.getId());
                try {
                    remote.setTextViewText(R.id.text2,  youTubeSong.getPath() == null ? "Streaming" : "Local");
                } catch (Exception ignored) {
                    //there is no text2 in smallRemoteView
                }
            }
        }


        notificationManager.notify(MusicNotificationConstants.ID, notification);
    }

    @Override
    public void setLoading() {
        if (notification == null)
            throw new IllegalStateException("setLoading() called before buildNotification()");
        for (RemoteViews remote : arrayRemoteView) {
            remote.setImageViewResource(R.id.action_play_pause, R.drawable.ic_play_arrow_disabled_24dp);
            remote.setImageViewResource(R.id.action_next, R.drawable.ic_skip_next_disabled_24dp);
            remote.setImageViewResource(R.id.action_prev, R.drawable.ic_skip_previous_disabled_24dp);
            remote.setBoolean(R.id.action_play_pause, "setEnabled", false);
            remote.setBoolean(R.id.action_next, "setEnabled", false);
            remote.setBoolean(R.id.action_prev, "setEnabled", false);
            remote.setTextViewText(R.id.title, "Loading");
            remote.setTextViewText(R.id.text, "Loading");
            try {
                remote.setTextViewText(R.id.text2, "Loading");
            } catch (Exception ignored) {
                //there is no text2 in smallRemoteView
            }
        }
        notificationManager.notify(MusicNotificationConstants.ID, notification);
    }

    @Override
    public void setPaused() {
        if (notification == null)
            throw new IllegalStateException("setPaused() called before buildNotification()");

        for (RemoteViews remote : arrayRemoteView)
            remote.setImageViewResource(R.id.action_play_pause, R.drawable.ic_play_arrow_black_24dp);

        notificationManager.notify(MusicNotificationConstants.ID, notification);
    }

    @Override
    public void setError() {
        if (notification == null)
            throw new IllegalStateException("setError() called before buildNotification()");
        for (RemoteViews remote : arrayRemoteView) {
            remote.setImageViewResource(R.id.action_play_pause, R.drawable.ic_play_arrow_disabled_24dp);
            remote.setImageViewResource(R.id.action_prev, R.drawable.ic_skip_previous_disabled_24dp);
            remote.setImageViewResource(R.id.action_next, R.drawable.ic_skip_next_disabled_24dp);
            remote.setImageViewResource(R.id.album_image, R.drawable.ic_error_black_24dp);
            remote.setBoolean(R.id.action_play_pause, "setEnabled", false);
            remote.setBoolean(R.id.action_next, "setEnabled", false);
            remote.setBoolean(R.id.action_prev, "setEnabled", false);
            remote.setTextViewText(R.id.title, "Error");
            remote.setTextViewText(R.id.text, "Error");
            try {
                remote.setTextViewText(R.id.text2, "Error");
            } catch (Exception ignored) {
                //there is no text2 in smallRemoteView
            }
        }
        notificationManager.notify(MusicNotificationConstants.ID, notification);
    }


}
