package capstone.bophelohaesoopen.HaesoAPI;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Shaaheen on 9/10/2016.
 */
public class LogEntry {

    public static enum LogType {VIDEO, RECORDING, BLUETOOTH, PAGE_VISITS}

    private LogType logEntryType;
    private String loggedAction;
    private Date timestamp;

    public LogEntry(LogType logType, String action) {
        this.logEntryType = logType;
        this.loggedAction = action;
        this.timestamp = new Date();
    }

    public LogEntry(LogType logType, String action,Date date) {
        this.logEntryType = logType;
        this.loggedAction = action;
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
}
