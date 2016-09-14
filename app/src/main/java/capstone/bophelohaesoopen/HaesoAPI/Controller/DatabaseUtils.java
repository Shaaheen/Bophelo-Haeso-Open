package capstone.bophelohaesoopen.HaesoAPI.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import capstone.bophelohaesoopen.HaesoAPI.Model.LogEntry;

/**
 * Created by Shaaheen on 8/8/2016.
 */
public class DatabaseUtils extends SQLiteOpenHelper{

    // Database Version
    private static final int DATABASE_VERSION = 3;
    // Database Name
    private static final String DATABASE_NAME = "Logging_Database";
    // Contacts table name
    private static final String TABLE_LOGGING = "Logs";
    // LogEntrys Table Columns names
    private static final String KEY_ID = "Id";
    private static final String KEY_TYPE = "Type";
    private static final String KEY_ACTION = "Log_Action";
    private static final String KEY_FILENAME = "File_name";
    private static final String KEY_DATETIME = "Date";
    //private static final String KEY_SH_ADDR = “LogEntry_address”;

    //For backend access to logg on setup db
    private static DatabaseUtils database = null;

    public DatabaseUtils(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.v("DB","Setting up DB");
        database = this;
    }

    protected static DatabaseUtils getInstance(){
        return database;
    }

    protected static boolean isDatabaseSetup(){
        return (database!=null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_LOG_TABLE = "CREATE TABLE " + TABLE_LOGGING + "("
        + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TYPE + " TEXT," +
                KEY_ACTION + " TEXT," + KEY_FILENAME + " TEXT," + KEY_DATETIME + " DATETIME" + ")";
        Log.v("DB","Created table");
        sqLiteDatabase.execSQL(CREATE_LOG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGGING);
        // Creating tables again
        Log.v("DB","Refreshed and updated table");
        onCreate(sqLiteDatabase);
    }

    /**
     * Adds new log entry
     * @param logEntry - provide LogEntry object to add
     */
    public void addLog(LogEntry logEntry) {
        Log.v("DB","Adding entry to DB: " + logEntry);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, String.valueOf(logEntry.getLogEntryType()));
        values.put(KEY_ACTION, logEntry.getLoggedAction());
        values.put(KEY_FILENAME, logEntry.getFileName());
        values.put(KEY_DATETIME, logEntry.getDateTimeString());
        // Inserting Row
        db.insert(TABLE_LOGGING, null, values);
        Log.v("DB","Inserted");
        db.close(); // Closing database connection
    }

    /**
     * Gets All LogEntrys in logging database
     */
    public List<LogEntry> getAllLogEntries() {
        Log.v("DB","Getting all Log entries");
        List<LogEntry> LogEntryList = new ArrayList<LogEntry>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_LOGGING;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String dateString = cursor.getString(4);
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date dateTime = null;
                try {
                    dateTime = dateFormat.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                System.out.println(dateTime);
                LogEntry LogEntry = new LogEntry(
                        capstone.bophelohaesoopen.HaesoAPI.Model.LogEntry.LogType.valueOf(cursor.getString(1))
                        , cursor.getString(2),cursor.getString(3), dateTime);
                // Adding log to list
                LogEntryList.add(LogEntry);
            } while (cursor.moveToNext());
        }
        Log.v("DB","Retrieved all Log entries");
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
    public List<LogEntry> getLogEntriesForType(LogEntry.LogType logType) {
        Log.v("DB","Getting log entries for type : "+ logType.name());
        List<LogEntry> LogEntryList = new ArrayList<LogEntry>();
        String nam = logType.name();
        // Select All Query
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_LOGGING, new String[]{KEY_ID,
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
                        capstone.bophelohaesoopen.HaesoAPI.Model.LogEntry.LogType.valueOf(cursor.getString(1))
                        , cursor.getString(2) , cursor.getString(3) , dateTime);
                // Adding log to list
                LogEntryList.add(LogEntry);
            } while (cursor.moveToNext());
        }
        Log.v("DB","Got log entries");
        cursor.close();
        db.close();
        // return contact list
        return LogEntryList;
    }



}
