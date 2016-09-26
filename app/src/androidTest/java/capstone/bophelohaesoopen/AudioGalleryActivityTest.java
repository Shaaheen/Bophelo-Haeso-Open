package capstone.bophelohaesoopen;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.CardView;
import android.widget.Button;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Jacob Ntesang on 9/16/2016.
 * This class will test the start of the AudioGalleryActivity by the MainActivity using recordings_black button.
 */

@RunWith(AndroidJUnit4.class)
public class AudioGalleryActivityTest {
    CardView recordingsButton;
    Activity mActivity, audioGalleryActivity;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityRule.getActivity();
        recordingsButton = (CardView) mActivity.findViewById(R.id.recordingsButton);
    }

    // This fails, the test fails.
    @Test
    public void Preconditions() {
        assertNotNull(recordingsButton);
        assertTrue(recordingsButton.isClickable());
    }

    @Test
    public void clickRecordingButton_startsAudioGalleryActivity(){
        // register next activity that need to be monitored.
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(AudioGalleryActivity.class.getName(), null, false);

        //perform button click.
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // click recordings_black button and open audioGalleryActivity.
                recordingsButton.performClick();
            }
        });

        //Watch for the timeout
        //example values 5000 if in ms, or 5 if it's in seconds.
        audioGalleryActivity = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5000);

        // audioGalleryActivity is opened and captured.
        //Check that the shareMediaBar and the audio.recyclerView is present.
        assertNotNull(audioGalleryActivity);
        onView(withId(R.id.shareMediaBar)).check(matches(isDisplayed()));
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
        audioGalleryActivity.finish();
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}

