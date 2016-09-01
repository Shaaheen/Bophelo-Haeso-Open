package capstone.bophelohaesoopen;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.AndroidJUnitRunner;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by ntesang on 9/1/2016.
 */



@RunWith(AndroidJUnit4.class)
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    Button recordAudio, viewAudio, capturePicture, viewPicture;
    private MainActivity mActivity;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = getActivity();
    }

    @Test
    public void testButtons() {
        //Check that all the UI buttons are present.
        recordAudio = (Button) mActivity.findViewById(R.id.recordAudioButton);
        capturePicture = (Button) mActivity.findViewById(R.id.takePictureButton);
        viewAudio = (Button) mActivity.findViewById(R.id.audioGalleryButton);
        viewPicture = (Button) mActivity.findViewById(R.id.picturesButton);

        assertNotNull(recordAudio);
        assertNotNull(capturePicture);
        assertNotNull(viewAudio);
        assertNotNull(viewPicture);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
