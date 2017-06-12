package le1.mytube;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import static le1.mytube.DBHelper.FLD_END;
import static le1.mytube.DBHelper.FLD_ID;
import static le1.mytube.DBHelper.FLD_INDEX;
import static le1.mytube.DBHelper.FLD_PATH;
import static le1.mytube.DBHelper.FLD_START;
import static le1.mytube.DBHelper.FLD_TITLE;
import static le1.mytube.DBHelper.TB_NAME;
import static le1.mytube.DBHelper.database;

/**
 * Created by Leone on 09/06/17.
 */

public class MusicDB {

    private Context context;
    private DBHelper dbHelper;


    public MusicDB(Context context) {
        this.context = context;
    }

    public MusicDB open() throws SQLException {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        database= SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory()+"/mydatabase.db", null);


        return this;
    }

    public void close() {
        dbHelper.close();
    }

    private ContentValues generateCV(String title, String videoID, String path, int startTime, int endTime ) {
        ContentValues values = new ContentValues();
        values.put( FLD_TITLE, title );
        values.put( FLD_ID, videoID );
        values.put( FLD_PATH, path );
        values.put( FLD_START, startTime );
        values.put( FLD_END, endTime );
        return values;
    }

    //create a contact
    public long addSong(String title, String videoID, String path, int startTime,int endTime ) {
        ContentValues initialValues = generateCV(title, videoID, path, startTime, endTime);
        return database.insertOrThrow(TB_NAME, null, initialValues);
    }

    //update a contact
    public boolean updateSong(long _id, String title, String videoID, String path, int startTime,int endTime ) {
        ContentValues updateValues = generateCV(title, videoID, path, startTime, endTime);
        return database.update(TB_NAME, updateValues, FLD_INDEX + "=" + _id, null) > 0;
    }

    //delete a contact
    public boolean deleteSong(long _id) {
        return database.delete(TB_NAME, FLD_ID + "=" + _id, null) > 0;
    }

    //fetch all contacts
    public Cursor fetchAllSongs() {
        return database.query(TB_NAME, new String[] { FLD_INDEX, FLD_TITLE, FLD_ID, FLD_PATH, FLD_START, FLD_END}, null, null, null, null, null);
    }

    public void clear(){
        database.execSQL("delete from sqlite_sequence where name='"+TB_NAME+"'");
        database.execSQL("delete from "+ TB_NAME);
    }

    //fetch contacts filter by a string
    public Cursor fetchContactsByFilter(int fieldToBeFiltered, String filter) {
        String fldToFilter;

        switch (fieldToBeFiltered){
            case 0: fldToFilter= FLD_INDEX;
                break;
            case 1: fldToFilter=FLD_TITLE;
                break;
            case 2: fldToFilter=FLD_ID;
                break;
            case 3: fldToFilter=FLD_PATH;
                break;
            case 4: fldToFilter=FLD_START;
                break;
            case 5: fldToFilter=FLD_END;
                break;
            default:fldToFilter=FLD_INDEX;
        }
        Cursor mCursor = database.query(true, TB_NAME, new String[] {
                        FLD_INDEX, FLD_TITLE, FLD_ID, FLD_PATH, FLD_START, FLD_END },
                fldToFilter + " like '%"+ filter + "%'", null, null, null, null, null);

        return mCursor;
    }}
