package le1.mytube.data.database.youTubeSong;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by Leone on 18/08/17.
 */
@Dao
public interface YouTubeSongDao{

    @Insert
    void addSongs(YouTubeSong... youTubeSongs);

    @Update
    void updateSong(YouTubeSong... youTubeSongs);

    @Delete
    void deleteSong(YouTubeSong... youTubeSong);

    @Query("DELETE FROM Offline")
    void deleteAllSongs();

    @Query("SELECT * FROM Offline")
    List<YouTubeSong> getAllSongs();

    @Query("SELECT * FROM Offline WHERE ID=:id LIMIT 1")
    YouTubeSong getSongById(String id);

}
