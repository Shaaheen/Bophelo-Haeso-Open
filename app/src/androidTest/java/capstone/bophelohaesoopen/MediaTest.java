package capstone.bophelohaesoopen;

import android.media.MediaRecorder;
import android.os.Environment;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import capstone.bophelohaesoopen.HaesoAPI.AudioRecorder;
import capstone.bophelohaesoopen.HaesoAPI.Model.Media;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;


/**
 * Created by Jacob Ntesang on 9/2/2016.
 * This class unit tests the media production.
 */

@RunWith(AndroidJUnit4.class)
public class MediaTest {

    AudioRecorder myAudio;
    Media media;
    String name, outputFile;

    //This method will be executed before each test in the class, so as to execute some preconditions necessary for the test.
    @Before
    public void setUp() {
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/chw_workout.3gp";
        name =  "video";
        myAudio =  new AudioRecorder(outputFile);
        media = new Media(name, outputFile);
        myAudio.prepareForRecording();
    }


    //If this fails, then every test in this class is expected to fail. The information in this method must be true before any test.
    @Test
    public void preConditions(){
        assertNotNull(myAudio);
        assertNotNull(media);
    }

    @Test
    public void audioRecordingTest() throws IOException{
        myAudio.startRecording();
        //While the app is recording, the mediaRecorder instance is not null;
        assertNotNull(myAudio.myAudioRecorder);
    }

    //Confirm the media name and the output filePath.
    @Test
    public void confirmMediaDetails() {
        assertEquals(media.getName(), "workout");
        assertEquals(media.getFilePath(), outputFile);
    }

    //Release all the resources used in this class.
    @After
    public void tearDown() {
        myAudio = null;
    }
}
