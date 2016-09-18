package capstone.bophelohaesoopen;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.widget.CardView;
import android.widget.Button;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Jacob Ntesang on 9/16/2016.
 * This class will test the start of the PictureGalleryActivity by the MainActivity using pictures button.
 */
public class PictureGalleryActivityTest {
    CardView picturesButton;
    Activity mActivity, pictureGalleryActivity;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityRule.getActivity();
        picturesButton = (CardView) mActivity.findViewById(R.id.picturesButton);
    }

    @Test
    public void clickRecordingButton_startsPictureGlaleryActivity(){
        // register next activity that need to be monitored.
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(PictureGalleryActivity.class.getName(), null, false);

        //perform button click.
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // click recordings button and open audioGalleryActivity.
                picturesButton.performClick();
            }
        });

        //Watch for the timeout
        //example values 5000 if in ms, or 5 if it's in seconds.
        pictureGalleryActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);
        // audioGalleryActivity is opened and captured.
        assertNotNull(pictureGalleryActivity);
        pictureGalleryActivity.finish();
    }
    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}
