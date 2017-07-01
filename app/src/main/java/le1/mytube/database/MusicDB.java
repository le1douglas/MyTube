package le1.mytube.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import le1.mytube.MainActivity;
import le1.mytube.YouTubeSong;

import static le1.mytube.mvpUtils.DatabaseConstants.FLD_END;
import static le1.mytube.mvpUtils.DatabaseConstants.FLD_ID;
import static le1.mytube.mvpUtils.DatabaseConstants.FLD_INDEX;
import static le1.mytube.mvpUtils.DatabaseConstants.FLD_PATH;
import static le1.mytube.mvpUtils.DatabaseConstants.FLD_START;
import static le1.mytube.mvpUtils.DatabaseConstants.FLD_TITLE;
import static le1.mytube.mvpUtils.DatabaseConstants.TB_NAME;


public class MusicDB {

    private Context context;
    private MusicDBHelper musicDbHelper;
    private SQLiteDatabase database;

    public MusicDB(Context context) {
        this.context = context;
    }

    public MusicDB open() throws SQLException {
        musicDbHelper = new MusicDBHelper(context);
        database = musicDbHelper.getWritableDatabase();
        //database = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory() + "/mydatabase.db", null);

        return this;
    }

    public void close() {
        musicDbHelper.close();
    }

    public void clear() {
        MainActivity.list.clear();
        MainActivity.adapter.notifyDataSetChanged();

        for (Object i : getAllTableNames()) {
            if (i.equals(TB_NAME)) {
                database.execSQL("delete from " + TB_NAME);
            } else {
                Log.d("DBoperation", "deleting " + i);
                database.execSQL("drop table `" + i + "`");
            }
        }
        database.execSQL("update sqlite_sequence set seq=0 where name='" + TB_NAME + "'");
        Toast.makeText(context, "All tables deleted", Toast.LENGTH_SHORT).show();

    }

    private ContentValues generateSongCV(YouTubeSong ytSong) {
        ContentValues values = new ContentValues();
        values.put(FLD_TITLE, ytSong.getTitle());
        values.put(FLD_ID, ytSong.getId());
        values.put(FLD_PATH, ytSong.getPath());
        values.put(FLD_START, ytSong.getStart());
        values.put(FLD_END, ytSong.getEnd());
        return values;
    }


    //integer because it may be null
    public boolean addSong(YouTubeSong ytSong) {
        ContentValues initialValues = generateSongCV(ytSong);
        return database.insertOrThrow(TB_NAME, null, initialValues) > -1;
    }

    //update a contact
    public boolean updateSong(long _id, YouTubeSong ytSong) {
        ContentValues updateValues = generateSongCV(ytSong);
        return database.update(TB_NAME, updateValues, FLD_INDEX + "=" + _id, null) > 0;
    }

    //delete a contact
    public boolean deleteSong(YouTubeSong ytSong) {
        File file = new File(ytSong.getPath());
        return database.delete(TB_NAME, FLD_ID + "='" + ytSong.getId() + "'", null) > 0 && file.delete();

    }


    public void addTable(String tableName) {
        String query = "create table if not exists `" + tableName + "`(" +
                FLD_INDEX + " integer primary key autoincrement, " +
                FLD_ID + " text);";
        Log.d("TABLE", query);
        database.execSQL(query);
    }

    public void deleteTable(String tableName) {
        String query = "drop table if exists `" + tableName + "`";
        database.execSQL(query);
    }

    public void addSongToPlaylist(String tableName, YouTubeSong ytSong) {
        database.execSQL("insert into `" + tableName + "` (id) values ('" + ytSong.getId() + "')");
        //return database.insertOrThrow(tableName, null, generatePlaylistCV(videoID)) > -1;
    }


    public Cursor getAllSongsCursor() {
        Cursor cursor = database.query(TB_NAME, new String[]{FLD_INDEX, FLD_TITLE, FLD_ID, FLD_PATH, FLD_START, FLD_END}, null, null, null, null, null);
        return cursor;
    }

    public ArrayList<YouTubeSong> getAllSongs() {
        Cursor cursor = database.query(TB_NAME, new String[]{FLD_INDEX, FLD_TITLE, FLD_ID, FLD_PATH, FLD_START, FLD_END}, null, null, null, null, null);
        ArrayList<YouTubeSong> arrayList = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    YouTubeSong ytSong= new YouTubeSong(
                            cursor.getString(cursor.getColumnIndex(FLD_TITLE)),
                            cursor.getString(cursor.getColumnIndex(FLD_ID)),
                            cursor.getString(cursor.getColumnIndex(FLD_PATH)),
                            cursor.getInt(cursor.getColumnIndex(FLD_START)),
                            cursor.getInt(cursor.getColumnIndex(FLD_END)));
                    arrayList.add(ytSong);

                } while (cursor.moveToNext());

            }

        }
        cursor.close();
        return arrayList;
    }

    public String getAllSongsString() {
        Cursor cursor = database.query(TB_NAME, new String[]{FLD_INDEX, FLD_TITLE, FLD_ID, FLD_PATH, FLD_START, FLD_END}, null, null, null, null, null);
        StringBuilder sb = new StringBuilder();
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    String singlerow =
                            cursor.getString(cursor.getColumnIndex(FLD_INDEX)) + "| " +
                                    cursor.getString(cursor.getColumnIndex(FLD_TITLE)) + ", " +
                                    cursor.getString(cursor.getColumnIndex(FLD_ID)) + ", " +
                                    cursor.getString(cursor.getColumnIndex(FLD_PATH)) + ", " +
                                    cursor.getString(cursor.getColumnIndex(FLD_START)) + ", " +
                                    cursor.getString(cursor.getColumnIndex(FLD_END))
                                    + System.getProperty("line.separator");

                    sb.append(singlerow);


                } while (cursor.moveToNext());

            }

        }
        cursor.close();
        return sb.toString();
    }

    public ArrayList getAllTableNames() {
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
            }
            cursor.close();
            return arrayList;
        } catch (SQLiteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getAllSongsInPlaylist(String tableName) {
        try {
            Cursor cursor = database.query(tableName, new String[]{FLD_INDEX, FLD_ID}, null, null, null, null, null);
            StringBuilder sb = new StringBuilder();
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        String singlerow =
                                cursor.getString(cursor.getColumnIndex(FLD_INDEX)) + "| " +
                                        cursor.getString(cursor.getColumnIndex(FLD_ID))
                                        + System.getProperty("line.separator");

                        sb.append(singlerow);

                    } while (cursor.moveToNext());

                }
            }
            cursor.close();
            return sb.toString();
        } catch (SQLiteException e) {
            return tableName + " Probally does not exits";
        }

    }

    //fetch contacts filter by a string
    public YouTubeSong getSongById(String id) {
        YouTubeSong ytSong = null;
        Cursor cursor = database.query(true, TB_NAME, new String[]{
                        FLD_INDEX, FLD_TITLE, FLD_ID, FLD_PATH, FLD_START, FLD_END},
                FLD_ID + " like '%" + id + "%'", null, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    ytSong= new YouTubeSong(
                            cursor.getString(cursor.getColumnIndex(FLD_TITLE)),
                            cursor.getString(cursor.getColumnIndex(FLD_ID)),
                            cursor.getString(cursor.getColumnIndex(FLD_PATH)),
                            cursor.getInt(cursor.getColumnIndex(FLD_START)),
                            cursor.getInt(cursor.getColumnIndex(FLD_END)));

                } while (cursor.moveToNext());

            }

        }
        cursor.close();
        return ytSong;
    }
}
