package capstone.bophelohaesoopen;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.CardView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Jacob Ntesang on 9/18/2016.
 * This class tests the CameraActivity UI when the picture button is clicked from the main screen.
 */

@RunWith(AndroidJUnit4.class)
public class PictureTakingUITest {

    CardView takePictureButton;
    Activity mActivity;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityRule.getActivity();
        takePictureButton = (CardView) mActivity.findViewById(R.id.takePictureButton);
    }

    // This makes sure that the our current screen has a record_black button that we need clicked.
    @Test
    public void preConditions() {
        assertNotNull(takePictureButton);
    }

    @Test
    public void clickTakePictureButton_opensCameraUI() {
        // Click the takePictureButton button.
        onView(withId(R.id.takePictureButton)).perform(click());

        // Check if the camera_black screen is displayed by checking the screen title and
        // making sure it has the capture button and that the button is clickable.
        onView(withId(R.id.capture)).check(matches(isDisplayed()));
        onView(withId(R.id.capture)).check(matches(isClickable()));

    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}
