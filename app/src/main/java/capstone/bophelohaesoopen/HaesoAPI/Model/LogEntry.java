package capstone.bophelohaesoopen.HaesoAPI.Model;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Object representation of a log entry in the logging database
 */
public class LogEntry {

    //Types of logs available - for organising data
    public static enum LogType {MEDIA_PLAYER, RECORDING, BLUETOOTH, PAGE_VISITS}

    //Attributes of a log entry in db
    private LogType logEntryType;
    private String loggedAction;
    private Date timestamp;
    private String fileName;

    /**
     * Log object for logging database
     * @param logType - specified type of the log data
     * @param action - action that is to be logged
     */
    public LogEntry(LogType logType, String action) {
        this.logEntryType = logType;
        this.loggedAction = action;
        this.fileName = null;
        this.timestamp = new Date();
    }

    /**
     * Log object for logging database
     * @param logEntryType - specified type of the log data
     * @param loggedAction - action that is to be logged
     * @param fileName - Action associated with specified file
     */
    public LogEntry(LogType logEntryType, String loggedAction, String fileName) {
        this.logEntryType = logEntryType;
        this.loggedAction = loggedAction;
        this.fileName = fileName;
        this.timestamp = new Date();
    }

    /**
     * Log object for logging database
     * @param logType - specified type of the log data
     * @param action - action that is to be logged
     * @param fileName - Action associated with specified file
     * @param date - date of logged action
     */
    public LogEntry(LogType logType, String action,String fileName, Date date) {
        this.logEntryType = logType;
        this.loggedAction = action;
        this.fileName = fileName;
        this.timestamp = date;
    }

    public String getFileName() {
        return fileName;
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

    public String csvFormat(){
        return logEntryType.name() + "," + loggedAction + "," + timestamp + "," + fileName;
    }
}
