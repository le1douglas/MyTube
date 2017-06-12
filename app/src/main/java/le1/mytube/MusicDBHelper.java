package le1.mytube;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Leone on 09/06/17.
 */

public class MusicDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mydatabase.db";
    private static final int DB_VERSION = 1;

    public static final String TB_NAME = "Offline";

    public static final String FLD_INDEX = "_id";
    public static final String FLD_TITLE = "TITLE";
    public static final String FLD_ID = "ID";
    public static final String FLD_PATH = "PATH";
    public static final String FLD_START = "START";
    public static final String FLD_END = "END";



    private static final String DB_CREATE = "create table "+ TB_NAME + "(" +
            FLD_INDEX + " integer primary key autoincrement, " +
            FLD_TITLE + " text, " +
            FLD_ID + " text, " +
            FLD_PATH + " text, " +
            FLD_START + " int, " +
            FLD_END + " int);";


    public MusicDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
    }


    @Override
    public void onUpgrade( SQLiteDatabase database, int oldVersion, int newVersion ) {

        database.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(database);

    }
}
