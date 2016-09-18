package capstone.bophelohaesoopen;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Button;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Jacob Ntesang on 9/16/2016.
 * This class will test the start of the AudioGalleryActivity by the MainActivity using recordings button.
 */

@RunWith(AndroidJUnit4.class)
public class AudioGalleryActivityTest {
    Button recordingsButton;
    Activity mActivity, audioGalleryActivity;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityRule.getActivity();
        recordingsButton = (Button) mActivity.findViewById(R.id.recordingsButton);
    }

    @Test
    public void clickRecordingButton_startsAudioGalleryActivity(){
        // register next activity that need to be monitored.
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(AudioGalleryActivity.class.getName(), null, false);

        //perform button click.
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // click recordings button and open audioGalleryActivity.
                recordingsButton.performClick();
            }
        });

        //Watch for the timeout
        //example values 5000 if in ms, or 5 if it's in seconds.
        audioGalleryActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        // audioGalleryActivity is opened and captured.
        assertNotNull(audioGalleryActivity);
        audioGalleryActivity.finish();
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}

