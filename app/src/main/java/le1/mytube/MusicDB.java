package le1.mytube;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import static le1.mytube.DBHelper.FLD_END;
import static le1.mytube.DBHelper.FLD_ID;
import static le1.mytube.DBHelper.FLD_INDEX;
import static le1.mytube.DBHelper.FLD_PATH;
import static le1.mytube.DBHelper.FLD_START;
import static le1.mytube.DBHelper.FLD_TITLE;
import static le1.mytube.DBHelper.TB_NAME;


/**
 * Created by Leone on 09/06/17.
 */

public class MusicDB {

    private Context context;
    private DBHelper dbHelper;
    private SQLiteDatabase database;

    public MusicDB(Context context) {
        this.context = context;
    }

    public MusicDB open() throws SQLException {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void clear() {
        database.execSQL("delete from sqlite_sequence where name='" + TB_NAME + "'");
        database.execSQL("delete from " + TB_NAME);
    }

    private ContentValues generateCV(String title, String videoID, String path, int startTime, int endTime) {
        ContentValues values = new ContentValues();
        values.put(FLD_TITLE, title);
        values.put(FLD_ID, videoID);
        values.put(FLD_PATH, path);
        values.put(FLD_START, startTime);
        values.put(FLD_END, endTime);
        return values;
    }

    //create a contact
    public long addSong(String title, String videoID, String path, int startTime, int endTime) {
        ContentValues initialValues = generateCV(title, videoID, path, startTime, endTime);
        return database.insertOrThrow(TB_NAME, null, initialValues);
    }

    //update a contact
    public boolean updateSong(long _id, String title, String videoID, String path, int startTime, int endTime) {
        ContentValues updateValues = generateCV(title, videoID, path, startTime, endTime);
        return database.update(TB_NAME, updateValues, FLD_INDEX + "=" + _id, null) > 0;
    }

    //delete a contact
    public boolean deleteSong(long _id) {
        return database.delete(TB_NAME, FLD_ID + "=" + _id, null) > 0;
    }

    //fetch all contacts
    public String getAllSongs() {
        Cursor cursor = database.query(TB_NAME, new String[]{FLD_INDEX, FLD_TITLE, FLD_ID, FLD_PATH, FLD_START, FLD_END}, null, null, null, null, null);
        StringBuilder sb = new StringBuilder();
        if (cursor != null&&cursor.getCount()>0) {
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
        return sb.toString();
    }

    //fetch contacts filter by a string
    public String getSongsByFilter(String fieldToBeFiltered, String filter){

        Cursor cursor = database.query(true, TB_NAME, new String[]{
                        FLD_INDEX, FLD_TITLE, FLD_ID, FLD_PATH, FLD_START, FLD_END},
                fieldToBeFiltered + " like '%" + filter + "%'", null, null, null, null, null);

        StringBuilder sb = new StringBuilder();
        if (cursor != null&&cursor.getCount()>0) {
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

        return sb.toString();
    }
}
