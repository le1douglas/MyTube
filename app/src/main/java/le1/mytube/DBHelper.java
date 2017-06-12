package le1.mytube;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Leone on 09/06/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mydatabase.db";
    private static final int DB_VERSION = 1;

    public static final String TB_NAME = "OfflineSongs";

    public static final String FLD_INDEX = "_id";
    public static final String FLD_TITLE = "FLD_TITLE";
    public static final String FLD_ID = "FLD_ID";
    public static final String FLD_PATH = "FLD_PATH";
    public static final String FLD_START = "FLD_START";
    public static final String FLD_END = "FLD_END";

    public static SQLiteDatabase database;


    private static final String DB_CREATE = "create table "+ TB_NAME + "(" +
            FLD_INDEX + " integer primary key autoincrement, " +
            FLD_TITLE + " text, " +
            FLD_ID + " text, " +
            FLD_PATH + " text, " +
            FLD_START + " int, " +
            FLD_END + " int);";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d("DBHELPER", DB_CREATE.toUpperCase());
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //Log.d("DBHELPER", DB_CREATE.toUpperCase());
        database.execSQL(DB_CREATE);
    }


    @Override
    public void onUpgrade( SQLiteDatabase database, int oldVersion, int newVersion ) {

        database.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(database);

    }
}
