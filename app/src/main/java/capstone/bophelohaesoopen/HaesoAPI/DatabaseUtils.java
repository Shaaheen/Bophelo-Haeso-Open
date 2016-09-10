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

    // Adding new log entry
    public void addLog(LogEntry logEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, String.valueOf(logEntry.getLogEntryType())); // LogEntry Name
        values.put(KEY_ACTION, logEntry.getLoggedAction()); // LogEntry Phone Number
        // Inserting Row
        db.insert(TABLE_Video, null, values);
        db.close(); // Closing database connection
    }

    // Getting All LogEntrys
    public List<LogEntry> getAllLogEntrys() {
        List<LogEntry> LogEntryList = new ArrayList<LogEntry>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_Video;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                LogEntry LogEntry = new LogEntry(
                        capstone.bophelohaesoopen.HaesoAPI.LogEntry.LogType.valueOf(cursor.getString(1))
                        , cursor.getString(2));
                // Adding log to list
                LogEntryList.add(LogEntry);
            } while (cursor.moveToNext());
        }
        db.close();
        // return contact list
        return LogEntryList;
    }
}
