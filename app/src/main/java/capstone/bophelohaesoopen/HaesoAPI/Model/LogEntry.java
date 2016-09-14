package capstone.bophelohaesoopen.HaesoAPI.Model;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Shaaheen on 9/10/2016.
 */
public class LogEntry {

    public static enum LogType {MEDIA_PLAYER, RECORDING, BLUETOOTH, PAGE_VISITS}

    private LogType logEntryType;
    private String loggedAction;
    private Date timestamp;
    private String fileName;

    public LogEntry(LogType logType, String action) {
        this.logEntryType = logType;
        this.loggedAction = action;
        this.fileName = null;
        this.timestamp = new Date();
    }

    public LogEntry(LogType logEntryType, String loggedAction, String fileName) {
        this.logEntryType = logEntryType;
        this.loggedAction = loggedAction;
        this.fileName = fileName;
        this.timestamp = new Date();
    }

    public String getFileName() {
        return fileName;
    }

    public LogEntry(LogType logType, String action,String fileName, Date date) {
        this.logEntryType = logType;
        this.loggedAction = action;
        this.fileName = fileName;
        this.timestamp = date;
    }

    public String getDateTimeString(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dateFormat.format(timestamp);
    }

    public LogType getLogEntryType() {
        return logEntryType;
    }

    public String getLoggedAction() {
        return loggedAction;
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "logEntryType=" + logEntryType +
                ", loggedAction='" + loggedAction + '\'' +
                ", timestamp=" + timestamp +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
