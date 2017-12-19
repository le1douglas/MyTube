package le1.mytube.domain.services.musicService.managers;

import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import le1.mytube.data.database.youTubeSong.YouTubeSong;

/**
 * Created by leone on 18/12/17.
 */

public class QueueManager {
    private static final String TAG = "LE1_QueueManager";
    private MediaSessionCompat mediaSession;
    private static int currentPosition;

    QueueManager(MediaSessionCompat mediaSession) {
        this.mediaSession = mediaSession;
    }

    public YouTubeSong getSongAtPosition(int position) {
        MediaDescriptionCompat description = mediaSession.getController().getQueue().get(position).getDescription();
        return fromDescriptionToYoutubeSong(description);
    }

    public int getIndexOfSong(YouTubeSong youTubeSong){
        for (int i= 0; i<mediaSession.getController().getQueue().size(); i++){
            String mediaId =mediaSession.getController().getQueue().get(i).getDescription().getMediaId();
            if (mediaId.equals(youTubeSong.getId()))
                return i;
        }
        return  -1;
    }

    public Long addToEnd(MediaDescriptionCompat description) {
        List<MediaSessionCompat.QueueItem> queueList = mediaSession.getController().getQueue();
        if (queueList == null) queueList = new ArrayList<>();
        //if the song is already the last in the queue there is no need to add it again
        if (queueList.size() > 0) {
            MediaSessionCompat.QueueItem lastQueueItem = queueList.get(queueList.size() - 1);
            if (lastQueueItem.getDescription().getMediaId()
                    .equals(description.getMediaId())) {
                Log.w(TAG, "addToEnd: this item is already the last in queue");
                return null;
            }

        }
        long queueId = (long) description.getMediaId().hashCode();
        queueList.add(new MediaSessionCompat.QueueItem(description, queueId));
        mediaSession.setQueue(queueList);
        return queueId;
    }

    public Long addToPosition(MediaDescriptionCompat description, int position) {
        List<MediaSessionCompat.QueueItem> queueList = mediaSession.getController().getQueue();
        if (queueList == null) queueList = new ArrayList<>();
        //if the song is already the same position in the queue there is no need to add it again
        if (queueList.size() > 0) {
            MediaSessionCompat.QueueItem lastQueueItem = queueList.get(position-1);
            if (lastQueueItem.getDescription().getMediaId()
                    .equals(description.getMediaId())) {
                Log.w(TAG, "addToEnd: this item is already the last in queue" + lastQueueItem.getDescription().getTitle());

                return null;
            }

        }

        long queueId = (long) description.getMediaId().hashCode();
        MediaSessionCompat.QueueItem qi = new MediaSessionCompat.QueueItem(description, queueId);
        queueList.add(position, qi);
        Log.d(TAG, "addToPosition: " + qi.toString());
        mediaSession.setQueue(queueList);
        return queueId;
    }

    public List<YouTubeSong> getList() {
        if  (mediaSession.getController().getQueue()==null) return null;
        List<YouTubeSong> youTubeList = new ArrayList<>();
        for (MediaSessionCompat.QueueItem item : mediaSession.getController().getQueue()) {
            Log.d(TAG, "getList: " + item.toString());
            youTubeList.add(fromDescriptionToYoutubeSong(item.getDescription()));
        }
        return youTubeList;
    }

    public static YouTubeSong fromDescriptionToYoutubeSong(MediaDescriptionCompat description){
        return  new YouTubeSong.Builder(description.getMediaId(),
                description.getTitle().toString())
                .build();
    }

    public static MediaDescriptionCompat fromYouTubeSongToDescription(YouTubeSong youTubeSong){
        return new MediaDescriptionCompat.Builder()
                .setTitle(youTubeSong.getTitle())
                .setMediaId(youTubeSong.getId())
                .build();
    }

    public void setCurrentPosition(int position) {
        currentPosition = position;
    }

    public static int getCurrentPosition() {
        return currentPosition;
    }
}
