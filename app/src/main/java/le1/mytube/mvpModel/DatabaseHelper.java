package le1.mytube.mvpModel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static le1.mytube.mvpUtils.DatabaseConstants.DB_NAME;
import static le1.mytube.mvpUtils.DatabaseConstants.DB_VERSION;
import static le1.mytube.mvpUtils.DatabaseConstants.FLD_END;
import static le1.mytube.mvpUtils.DatabaseConstants.FLD_ID;
import static le1.mytube.mvpUtils.DatabaseConstants.FLD_INDEX;
import static le1.mytube.mvpUtils.DatabaseConstants.FLD_PATH;
import static le1.mytube.mvpUtils.DatabaseConstants.FLD_START;
import static le1.mytube.mvpUtils.DatabaseConstants.FLD_TITLE;
import static le1.mytube.mvpUtils.DatabaseConstants.TB_NAME;

class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_CREATE = "create table " + TB_NAME + "(" +
            FLD_INDEX + " integer primary key autoincrement, " +
            FLD_TITLE + " text, " +
            FLD_ID + " text, " +
            FLD_PATH + " text, " +
            FLD_START + " int, " +
            FLD_END + " int);";


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        database.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(database);

    }


}
