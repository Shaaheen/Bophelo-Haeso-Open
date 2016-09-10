package capstone.bophelohaesoopen.HaesoAPI;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shaaheen on 8/8/2016.
 */
public class DatabaseUtils extends SQLiteOpenHelper{

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "Logging_Database";
    // Contacts table name
    private static final String TABLE_Video = "video_logs";
    // LogEntrys Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_ACTION = "log_action";
    //private static final String KEY_SH_ADDR = “LogEntry_address”;

    public DatabaseUtils(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_VIDEO_TABLE = "CREATE TABLE " + TABLE_Video + "("
        + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TYPE + " TEXT," + KEY_ACTION + " TEXT)";
        sqLiteDatabase.execSQL(CREATE_VIDEO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_Video);
        // Creating tables again
        onCreate(sqLiteDatabase);
    }

    
}
