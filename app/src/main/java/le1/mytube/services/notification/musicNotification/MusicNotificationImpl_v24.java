package le1.mytube.services.notification.musicNotification;

import android.app.Notification;
import android.app.Service;
import android.content.Context;

import le1.mytube.mvpModel.songs.YouTubeSong;

/**
 * Created by Leone on 29/08/17.
 */

public class MusicNotificationImpl_v24 extends MusicNotification{


   /* private static void setNotificationState(NotificationState state) {
        switch (state) {
            case LOADING:
                remoteView.setTextViewText(R.id.btn1, "loading");
                remoteView.setTextViewText(R.id.title, "PLACEHOLDER TITLE");
                remoteView.setBoolean(R.id.btn1, "setEnabled", false);
                break;
            case PLAYING:
                remoteView.setBoolean(R.id.btn1, "setEnabled", true);
                remoteView.setTextViewText(R.id.btn1, "playing");
                break;
            case PAUSED:
                remoteView.setTextViewText(R.id.btn1, "paused");
                break;
            case ERROR:
                remoteView.setTextViewText(R.id.btn1, "error");
                remoteView.setTextViewText(R.id.title, "Something went wrong");
                remoteView.setBoolean(R.id.btn1, "setEnabled", false);
                break;
        }
        //TODO check if necessary
        mNotificationManager.notify(666, notification);

    }*/

    /*private Notification.Action generateAction(int icon, String title, String intentAction) {
        Intent intent = new Intent(getApplicationContext(), MusicService_v21.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        Icon ic = Icon.createWithResource(MusicService_v21.this, icon);
        return new Notification.Action.Builder(ic, title, pendingIntent).build();
    }

    private void buildNotification(Notification.Action action, boolean foreground) {
        Notification.MediaStyle style = new Notification.MediaStyle();
        Intent intent = new Intent(getApplicationContext(), MusicService_v21.class);
        intent.setAction(ACTION_STOP);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Media Title")
                .setContentText("Media Artist")
                .setDeleteIntent(pendingIntent)
                .setStyle(style);
        builder.addAction(generateAction(android.R.drawable.ic_media_previous, "Previous", ACTION_PREVIOUS));
        builder.addAction(generateAction(android.R.drawable.ic_media_rew, "Rewind", ACTION_REWIND));
        builder.addAction(action);
        builder.addAction(generateAction(android.R.drawable.ic_media_ff, "Fast Foward", ACTION_FAST_FORWARD));
        builder.addAction(generateAction(android.R.drawable.ic_media_next, "Next", ACTION_NEXT));
        style.setShowActionsInCompactView(1, 2, 3, 4, 0);
        Notification not = builder.build();
        if (foreground) {
            startForeground(1, not);
        } else {
            stopForeground(false);

            notificationManager.notify(1, builder.build());
        }

    }*/


    @Override
    public void buildNotification(Context context, Service service) {

    }

    @Override
    public Notification getNotification() {
        return null;
    }

    @Override
    public void setPlaying(YouTubeSong youTubeSong) {

    }

    @Override
    public void setLoading() {

    }

    @Override
    public void setPaused() {

    }

    @Override
    public void setError() {

    }
}
