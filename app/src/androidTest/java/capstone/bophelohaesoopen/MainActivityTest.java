package capstone.bophelohaesoopen;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.CardView;
import android.widget.Button;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Jacob Ntesang on 9/1/2016.
 * This class tests that al the components expected in our MainActivity are there when the activity is run.
 */



@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    Button recordAudio, viewAudio, capturePicture, viewPicture;
    CardView shareMediaBar;
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
        viewAudio = (Button) mActivity.findViewById(R.id.audioGalleryButton);
        viewPicture = (Button) mActivity.findViewById(R.id.picturesButton);

        assertNotNull(recordAudio);
        assertNotNull(capturePicture);
        assertNotNull(viewAudio);
        assertNotNull(viewPicture);
    }

    // Test the presence of the share media bar.
    public void testShareMediaBarPresence() {
        //Get the shareMediaBar on the current activity.
        shareMediaBar = (CardView) mActivity.findViewById(R.id.shareMediaBar);
        assertNotNull(shareMediaBar);
    }

    // Tests the change text on the shareMediaBar when it is clicked.
    public void testShareMediaBarTextChange() {
        TextView shareText = (TextView) mActivity.findViewById(R.id.shareText);

        //First check the shareText on the bar before it is clicked.
        assertEquals("Share", shareText,toString());

        //perform button click.
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // click recordings button and open audioGalleryActivity.
                shareMediaBar.performClick();
            }
        });

        //The text should change from "Share" to "Cancel" after the bar is clicked.
        assertEquals("Cancel", shareText,toString());
    }



    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}
