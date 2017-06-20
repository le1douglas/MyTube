package le1.mytube;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import static le1.mytube.MusicDBHelper.FLD_END;
import static le1.mytube.MusicDBHelper.FLD_ID;
import static le1.mytube.MusicDBHelper.FLD_INDEX;
import static le1.mytube.MusicDBHelper.FLD_PATH;
import static le1.mytube.MusicDBHelper.FLD_START;
import static le1.mytube.MusicDBHelper.FLD_TITLE;
import static le1.mytube.MusicDBHelper.TB_NAME;

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

        for (Object i: getAllTableNames()) {
            if (i.equals(TB_NAME)) {
                database.execSQL("delete from " + TB_NAME);
            } else {
                Log.d("DBoperation", "deleting " + i);
                database.execSQL("drop table `" + i+ "`");
            }
        }
        database.execSQL("update sqlite_sequence set seq=0 where name='" + TB_NAME + "'");
        Toast.makeText(context, "All tables deleted", Toast.LENGTH_SHORT).show();

    }

    private ContentValues generateSongCV(String title, String videoID, String path, int startTime, int endTime) {
        ContentValues values = new ContentValues();
        values.put(FLD_TITLE, title);
        values.put(FLD_ID, videoID);
        values.put(FLD_PATH, path);
        values.put(FLD_START, startTime);
        values.put(FLD_END, endTime);
        return values;
    }

    private ContentValues generatePlaylistCV(String videoID) {
        ContentValues values = new ContentValues();
        values.put(FLD_ID, videoID);
        return values;
    }

    //integer because it may be null
    public boolean addSong(String title, String videoID, String path, Integer startTime, Integer endTime) {
        if (startTime == null) {
            startTime = 0;
        }
        if (endTime == null) {
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(path);
            endTime = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        }

        if (startTime < endTime) {

            ContentValues initialValues = generateSongCV(title, videoID, path, startTime, endTime);
            return database.insertOrThrow(TB_NAME, null, initialValues) > -1;
        } else {
            Log.e("MusicDB.addSong", "end time must be greater than start time");
            return false;
        }
    }

    //update a contact
    public boolean updateSong(long _id, String title, String videoID, String path, int startTime, int endTime) {
        ContentValues updateValues = generateSongCV(title, videoID, path, startTime, endTime);
        return database.update(TB_NAME, updateValues, FLD_INDEX + "=" + _id, null) > 0;
    }

    //delete a contact
    public boolean deleteSong(String fieldToBeFiltered, String filter) {
        return database.delete(TB_NAME, fieldToBeFiltered + "='" + filter + "'", null) > 0;
    }


    public void addTable(String tableName) {
        String query = "create table if not exists `" + tableName + "`(" +
                FLD_INDEX + " integer primary key autoincrement, " +
                FLD_ID + " text);";
        Log.d("TABLE", query);
        database.execSQL(query);
    }

    public void deleteTable(String tableName) {
        String query = "drop table if exists `" + tableName+"`";
        database.execSQL(query);
    }

    public void addSongToPlaylist(String tableName, String videoID) {
        database.execSQL("insert into `" + tableName+ "` (id) values ('"+videoID+"')");
        //return database.insertOrThrow(tableName, null, generatePlaylistCV(videoID)) > -1;
    }


    //fetch all contacts
    public String getAllSongs() {
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
    public String getSongsByFilter(String fieldToBeFiltered, String filter) {

        Cursor cursor = database.query(true, TB_NAME, new String[]{
                        FLD_INDEX, FLD_TITLE, FLD_ID, FLD_PATH, FLD_START, FLD_END},
                fieldToBeFiltered + " like '%" + filter + "%'", null, null, null, null, null);

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
                                    cursor.getString(cursor.getColumnIndex(FLD_END)) + "."
                                    + System.getProperty("line.separator");

                    sb.append(singlerow);

                } while (cursor.moveToNext());

            }

        }
        cursor.close();
        return sb.toString();
    }
}
