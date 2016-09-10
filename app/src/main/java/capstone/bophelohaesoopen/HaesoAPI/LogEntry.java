package capstone.bophelohaesoopen.HaesoAPI;

import java.security.Timestamp;

/**
 * Created by Shaaheen on 9/10/2016.
 */
public class LogEntry {

    public static enum LogType {VIDEO, RECORDING, BLUETOOTH, PAGE_VISITS}

    private LogType logEntryType;
    private String loggedAction;
    //Timestamp

    public LogEntry(LogType logType, String action) {
        this.logEntryType = logType;
        this.loggedAction = action;
    }

    public LogType getLogEntryType() {
        return logEntryType;
    }

    public String getLoggedAction() {
        return loggedAction;
    }
}
