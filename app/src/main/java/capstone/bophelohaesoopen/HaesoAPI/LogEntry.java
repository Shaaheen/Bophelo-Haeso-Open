package capstone.bophelohaesoopen.HaesoAPI;

import java.security.Timestamp;

/**
 * Created by Shaaheen on 9/10/2016.
 */
public class LogEntry {

    public static enum LogType {VIDEO, RECORDING, BLUETOOTH, PAGE_VISITS}

    LogType logEntryType;
    String loggedAction;
    //Timestamp

    public LogEntry(LogType logType, String action) {
        this.logEntryType = logType;
        this.loggedAction = action;
    }
}
