package le1.mytube.mvpModel;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

import le1.mytube.YouTubeSong;

import static le1.mytube.mvpUtils.DatabaseConstants.FLD_END;
import static le1.mytube.mvpUtils.DatabaseConstants.FLD_ID;
import static le1.mytube.mvpUtils.DatabaseConstants.FLD_INDEX;
import static le1.mytube.mvpUtils.DatabaseConstants.FLD_PATH;
import static le1.mytube.mvpUtils.DatabaseConstants.FLD_START;
import static le1.mytube.mvpUtils.DatabaseConstants.FLD_TITLE;
import static le1.mytube.mvpUtils.DatabaseConstants.TB_NAME;
import static le1.mytube.mvpUtils.SharedPreferencesConstants.keyAudiofocus;

public class Repository implements DatabaseInterface, SharedPreferencesInterface {

    private SQLiteDatabase database;
    private SharedPreferences preferences;

    public Repository(Context context) {
       openDatabase(context);
       preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void openDatabase(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        //if there is not one it will go to the OnCreate of databaseHelper (?)
        database = databaseHelper.getWritableDatabase();
    }

    @Override
    public void closeDatabase() {
        database.close();
    }


    private ContentValues generateSongCV(YouTubeSong youTubeSong) {
        ContentValues values = new ContentValues();
        values.put(FLD_TITLE, youTubeSong.getTitle());
        values.put(FLD_ID, youTubeSong.getId());
        values.put(FLD_PATH, youTubeSong.getPath());
        values.put(FLD_START, youTubeSong.getStart());
        values.put(FLD_END, youTubeSong.getEnd());
        return values;
    }

    @Override
    public void addSong(YouTubeSong youTubeSong) {
        try {
            ContentValues initialValues = generateSongCV(youTubeSong);
            database.insertOrThrow(TB_NAME, null, initialValues);
        } catch (Exception e) {
            //TODO see if arrives here
        }
    }

    @Override
    public void updateSong(YouTubeSong oldYoutubeSong, YouTubeSong newYoutubeSong) {
        //TODO
    }

    @Override
    public void deleteSong(YouTubeSong youTubeSong) {
        //TODO add file delete in presenter
        database.delete(TB_NAME, FLD_ID + "='" + youTubeSong.getId() + "'", null);
    }

    @Override
    public void deleteAllSongs() {
        //TODO clear mainactivity list in presenter
        //TODO change Object i to String i
        for (Object i : getAllPlaylistsName()) {
            if (i.equals(TB_NAME)) {
                database.execSQL("delete from " + TB_NAME);
            } else {
                Log.d("DBoperation", "deleting " + i);
                database.execSQL("drop table `" + i + "`");
            }
        }
        database.execSQL("update sqlite_sequence set seq=0 where name='" + TB_NAME + "'");
    }

    @Override
    public void createPlaylist(String playlistName) {
        String query = "create table if not exists `" + playlistName + "`(" +
                FLD_INDEX + " integer primary key autoincrement, " +
                FLD_ID + " text);";
        database.execSQL(query);
    }

    @Override
    public void deletePlaylist(String playlistName) {
        String query = "drop table if exists `" + playlistName + "`";
        database.execSQL(query);
    }

    @Override
    public void addSongToPlaylist(YouTubeSong youTubeSong, String playlistName) {
        //TODO changed query. see if it works
        database.execSQL("insert into `" + playlistName + "` (" + FLD_ID + ")  values ('" + youTubeSong.getId() + "')");
    }

    @Override
    public void removeSongFromPlaylist(YouTubeSong youTubeSong, String playlistName) {
        //TODO see if it works
        database.execSQL("delete from `" + playlistName + "` where " + FLD_ID + "='" + youTubeSong.getId() + "'");
    }

    @Override
    public ArrayList<YouTubeSong> getSongsInPlaylist(String playlistName) {
        try {
            String table = TB_NAME + ",`" + playlistName + "`";
            String[] columns = new String[]{TB_NAME + "." + FLD_TITLE, TB_NAME + "." + FLD_ID, TB_NAME + "." + FLD_PATH, TB_NAME + "." + FLD_START, TB_NAME + "." + FLD_END};
            String where = TB_NAME + "." + FLD_ID + "=`" + playlistName + "`." + FLD_ID;

            Cursor cursor = database.query(table, columns, where, null, null, null, null);

            ArrayList<YouTubeSong> arrayList = new ArrayList<>();
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        arrayList.add(new YouTubeSong(cursor.getString(cursor.getColumnIndex(FLD_TITLE)),
                                cursor.getString(cursor.getColumnIndex(FLD_ID)),
                                cursor.getString(cursor.getColumnIndex(FLD_PATH)),
                                cursor.getInt(cursor.getColumnIndex(FLD_START)),
                                cursor.getInt(cursor.getColumnIndex(FLD_END))));
                    } while (cursor.moveToNext());

                }
                cursor.close();
            }

            return arrayList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<YouTubeSong> getAllSongs() {
        try {
            Cursor cursor = database.query(TB_NAME, new String[]{FLD_INDEX, FLD_TITLE, FLD_ID, FLD_PATH, FLD_START, FLD_END}, null, null, null, null, null);
            ArrayList<YouTubeSong> arrayList = new ArrayList<>();
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        //TODO check if it works
                        arrayList.add(
                                new YouTubeSong(
                                        cursor.getString(cursor.getColumnIndex(FLD_TITLE)),
                                        cursor.getString(cursor.getColumnIndex(FLD_ID)),
                                        cursor.getString(cursor.getColumnIndex(FLD_PATH)),
                                        cursor.getInt(cursor.getColumnIndex(FLD_START)),
                                        cursor.getInt(cursor.getColumnIndex(FLD_END))
                                ));


                    } while (cursor.moveToNext());

                }
                cursor.close();
            }
            return arrayList;

        } catch (Exception e) {
            //Does it ever arrives here?
            //TODO test it
            throw new RuntimeException(e);
        }

    }

    @Override
    public ArrayList<String> getAllPlaylistsName() {
        try {
            Cursor cursor = database.rawQuery("select name from sqlite_sequence", null);
            ArrayList<String> arrayList = new ArrayList<>();
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        String singlerow = cursor.getString(cursor.getColumnIndex("name"));
                        arrayList.add(singlerow);

                    } while (cursor.moveToNext());

                }
                cursor.close();
            }

            return arrayList;
        } catch (Exception e) {
            //Does it ever arrives here?
            //TODO test it
            throw new RuntimeException(e);
        }
    }

    @Override
    public YouTubeSong getSongById(String id) {
        try {
            YouTubeSong youTubeSong = null;
            Cursor cursor = database.query(true, TB_NAME, new String[]{
                            FLD_INDEX, FLD_TITLE, FLD_ID, FLD_PATH, FLD_START, FLD_END},
                    FLD_ID + " like '%" + id + "%'", null, null, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    youTubeSong = new YouTubeSong(
                            cursor.getString(cursor.getColumnIndex(FLD_TITLE)),
                            cursor.getString(cursor.getColumnIndex(FLD_ID)),
                            cursor.getString(cursor.getColumnIndex(FLD_PATH)),
                            cursor.getInt(cursor.getColumnIndex(FLD_START)),
                            cursor.getInt(cursor.getColumnIndex(FLD_END)));
                }
                cursor.close();
            }
            return youTubeSong;
        } catch (Exception e) {
            //Does it ever arrives here?
            //TODO test it
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean getHandleAudiofocus() {
        return preferences.getBoolean(keyAudiofocus, true);
    }

    @Override
    public void setHandleAudiofocus(boolean handleAudioFocus) {
        preferences.edit().putBoolean(keyAudiofocus, handleAudioFocus).apply();
    }
}
