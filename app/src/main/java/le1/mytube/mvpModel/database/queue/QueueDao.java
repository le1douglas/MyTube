package le1.mytube.mvpModel.database.queue;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Leone on 09/09/17.
 */

@Dao
public interface QueueDao {

    @Insert
    void addSongs(QueueYouTubeSong... youTubeSongs);

    @Query("SELECT * FROM Queue WHERE position = 0")
    QueueYouTubeSong getNextSong();

    @Query("SELECT * FROM Queue ORDER BY position LIMIT 1")
    QueueYouTubeSong getFirstSong();

    @Query("SELECT * FROM Queue WHERE position = :position")
    QueueYouTubeSong getSongAtPosition(int position);

    @Query("DELETE FROM Queue")
    void deleteQueue();

    @Query("UPDATE Queue SET position = position + 1 WHERE position > :position")
    void incrementByOneAfter(int position);

    @Query("SELECT * FROM Queue ORDER BY position DESC LIMIT 1 ")
    QueueYouTubeSong getLastSong();

    @Query("SELECT * from Queue ORDER BY position")
    List<QueueYouTubeSong> getAllSongs();
}
