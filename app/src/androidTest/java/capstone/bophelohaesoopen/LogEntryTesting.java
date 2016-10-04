package capstone.bophelohaesoopen;

import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import capstone.bophelohaesoopen.HaesoAPI.Model.LogEntry;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Jacob Ntesang on 9/25/2016.
 * This class check the logEntry object details of the database.
 */

@RunWith(AndroidJUnit4.class)
public class LogEntryTesting {

    private LogEntry logEntry;
    private LogEntry.LogType logEntryType;
    private Date timestamp;
    private  String formatTimeStamp = "";

    /**
     * Object and variables setup.
     */
    @Before
    public void setUp() {
        logEntryType = logEntryType.MEDIA_PLAYER;
        timestamp = new Date();
        logEntry = new LogEntry(logEntryType, "Audio Playing", "recording101",timestamp);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        formatTimeStamp = dateFormat.format(timestamp);
    }

    /**
     * Check if the log entry object was successfully created.
     * If this test fails, then ay other test is bound to fail too.
     */
    @Test
    public void Preconditions() {
        assertNotNull(logEntry);
    }

    /**
     * Confirms all the log entry object representation details.
     */
    @Test
    public void confirmLogEntryDetails() {
        assertEquals("Audio Playing", logEntry.getLoggedAction());
        assertEquals("recording101", logEntry.getFileName());
        assertEquals(LogEntry.LogType.MEDIA_PLAYER, logEntry.getLogEntryType());
        assertEquals(formatTimeStamp, logEntry.getDateTimeString());
    }

    /**
     * Release resources.
     */
    @After
    public void tearDown() {
        logEntry = null;
    }

}
