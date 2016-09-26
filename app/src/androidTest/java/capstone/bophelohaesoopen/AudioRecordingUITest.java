package capstone.bophelohaesoopen;

import android.app.Activity;
import android.os.SystemClock;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.CardView;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by Jacob Ntesang on 9/18/2016.
 * This class tests the UI of the Audio Recorder screen when the record_black button is clicked.
 */

@RunWith(AndroidJUnit4.class)
public class AudioRecordingUITest {
    CardView recordButton;
    Activity mActivity;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityRule.getActivity();
        recordButton = (CardView) mActivity.findViewById(R.id.recordAudioButton);
    }

    // This makes sure that the our current screen has a record_black button that we need clicked.
    @Test
    public void preConditions() {
        assertNotNull(recordButton);
    }

    @Test
    public void clickRecordButton_opensAudioRecordingUI() {
        // Click the record_black button.
        onView(withId(R.id.recordAudioButton)).perform(click());

        // Check if the recording_black screen is displayed by checking the screen title and
        // making sure it has all the components expected.
        SystemClock.sleep(1000); // This is to make the app wait until timeView is loaded.
        onView(withId(R.id.timeView)).check(matches(isDisplayed()));
        onView(withId(R.id.recordingIndicator)).check(matches(isDisplayed()));
        onView(withId(R.id.stopButton)).check(matches(isDisplayed()));

        // stop_black the recording_black.
        onView(withId(R.id.stopButton)).perform(click());
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}
