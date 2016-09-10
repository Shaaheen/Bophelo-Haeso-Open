package capstone.bophelohaesoopen.HaesoAPI;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.database.Cursor;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Shaaheen on 8/8/2016.
 */
public class DatabaseUtils extends SQLiteOpenHelper{

    // Database Version
    private static final int DATABASE_VERSION = 2;
    // Database Name
    private static final String DATABASE_NAME = "Logging_Database";
    // Contacts table name
    private static final String TABLE_Video = "video_logs";
    // LogEntrys Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_ACTION = "log_action";
    private static final String KEY_DATETIME = "date";
    //private static final String KEY_SH_ADDR = “LogEntry_address”;

    public DatabaseUtils(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_VIDEO_TABLE = "CREATE TABLE " + TABLE_Video + "("
        + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TYPE + " TEXT," +
                KEY_ACTION + " TEXT," + KEY_DATETIME + " DATETIME" + ")";
        sqLiteDatabase.execSQL(CREATE_VIDEO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_Video);
        // Creating tables again
        onCreate(sqLiteDatabase);
    }

    /**
     * Adds new log entry
     * @param logEntry - provide LogEntry object to add
     */
    public void addLog(LogEntry logEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, String.valueOf(logEntry.getLogEntryType())); // LogEntry Name
        values.put(KEY_ACTION, logEntry.getLoggedAction()); // LogEntry Phone Number
        values.put(KEY_DATETIME, logEntry.getDateTimeString());
        // Inserting Row
        db.insert(TABLE_Video, null, values);
        db.close(); // Closing database connection
    }

    /**
     * Gets All LogEntrys in logging database
     */
    public List<LogEntry> getAllLogEntrys() {
        List<LogEntry> LogEntryList = new ArrayList<LogEntry>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_Video;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String dateString = cursor.getString(3);
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date dateTime = null;
                try {
                    dateTime = dateFormat.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                System.out.println(dateTime);
                LogEntry LogEntry = new LogEntry(
                        capstone.bophelohaesoopen.HaesoAPI.LogEntry.LogType.valueOf(cursor.getString(1))
                        , cursor.getString(2),dateTime);
                // Adding log to list
                LogEntryList.add(LogEntry);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return contact list
        return LogEntryList;
    }

    /**
     * Gets all Log Entries for a SPECIFIED TYPE
     * @param logType - type of log data to retrieve
     *                - Use LogType Enum - LogType.VIDEO, LogType.RECORDING,etc
     * @return List of LogEntry objects that was retrieved from database
     */
    public List<LogEntry> getLogEntrysForType(LogEntry.LogType logType) {
        List<LogEntry> LogEntryList = new ArrayList<LogEntry>();
        String nam = logType.name();
        // Select All Query
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_Video, new String[]{KEY_ID,
                        KEY_TYPE, KEY_ACTION,KEY_DATETIME}, KEY_TYPE + "=?",
                new String[]{logType.name()}, null, null, null, null);

        //Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String dateString = cursor.getString(3);
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date dateTime = null;
                try {
                    dateTime = dateFormat.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                System.out.println(dateTime);

                LogEntry LogEntry = new LogEntry(
                        capstone.bophelohaesoopen.HaesoAPI.LogEntry.LogType.valueOf(cursor.getString(1))
                        , cursor.getString(2),dateTime);
                // Adding log to list
                LogEntryList.add(LogEntry);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return contact list
        return LogEntryList;
    }



}
