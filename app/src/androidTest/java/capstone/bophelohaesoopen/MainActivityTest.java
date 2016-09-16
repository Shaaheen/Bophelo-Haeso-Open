package capstone.bophelohaesoopen;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Button;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by Jacob Ntesang on 9/1/2016.
 * This class tests that al the components expected in our MainActivity are there when the activity is run.
 */



@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    Button recordAudio, viewAudio, capturePicture, viewPicture;
    private MainActivity mActivity;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityRule.getActivity();
    }


    //Check that all the UI buttons are present.
    @Test
    public void testButtons() {
        recordAudio = (Button) mActivity.findViewById(R.id.recordAudioButton);
        capturePicture = (Button) mActivity.findViewById(R.id.takePictureButton);
        viewAudio = (Button) mActivity.findViewById(R.id.recordingsButton);
        viewPicture = (Button) mActivity.findViewById(R.id.picturesButton);

        assertNotNull(recordAudio);
        assertNotNull(capturePicture);
        assertNotNull(viewAudio);
        assertNotNull(viewPicture);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}
