package le1.mytube.mvpModel.database;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import le1.mytube.mvpModel.database.song.YouTubeSong;
import le1.mytube.mvpModel.database.song.YouTubeSongDao;

@android.arch.persistence.room.Database(entities = {YouTubeSong.class}, version = DatabaseConstants.DB_VERSION)
@TypeConverters({Converters.class})
public abstract class Database extends RoomDatabase{
    private static Database INSTANCE;

    public static Database getDatabase(Context context) {
        if (INSTANCE == null) {
            //TODO change .allowMainThreadQueries() so it doesn't allow it
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), Database.class, DatabaseConstants.DB_NAME)
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public abstract YouTubeSongDao youTubeSongDao();

}

