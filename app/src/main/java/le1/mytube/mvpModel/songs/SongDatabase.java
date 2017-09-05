package le1.mytube.mvpModel.songs;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {YouTubeSong.class}, version = SongDatabaseConstants.DB_VERSION)
public abstract class SongDatabase extends RoomDatabase{
    private static SongDatabase INSTANCE;

    public static SongDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            //TODO change it so it doesn't allow it
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), SongDatabase.class, SongDatabaseConstants.DB_NAME)
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public abstract YouTubeSongDao youTubeSongDao();

}

