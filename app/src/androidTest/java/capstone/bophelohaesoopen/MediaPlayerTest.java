package capstone.bophelohaesoopen;

import android.os.Environment;
import android.support.test.runner.AndroidJUnit4;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import capstone.bophelohaesoopen.HaesoAPI.Media;
import capstone.bophelohaesoopen.HaesoAPI.MediaPlayer;

/**
 * Created by Jacob Ntesang on 9/2/2016.
 * This class will perform instrumentation test on MediaPlayer.
 */

@RunWith(AndroidJUnit4.class)
public class MediaPlayerTest {

    MediaPlayer mediaPlayer;
    String nameOfTheApp, filePath;
    Media media;
    SurfaceView videoView;

    //This method will be executed before each test in the class, so as to execute some preconditions necessary for the test.
    @Before
    public void setUp() {
        filePath = "VIDEO_FILE_PATH";
        nameOfTheApp =  "BopheloHaeso";
        mediaPlayer = new MediaPlayer(nameOfTheApp);
        media = new Media("VIDEO", filePath);

        VideoPlayerActivity vActivity = new VideoPlayerActivity();
        videoView = (SurfaceView) vActivity.findViewById(R.id.videoView);

        // Ensures the video display is loaded before the video tries to play
        SurfaceHolder holder = videoView.getHolder();
        holder.addCallback((SurfaceHolder.Callback) this);
    }//setUp()


    //If this fails, then every test in this class is expected to fail. The information in this method must be true before any test.
    @Test
    public void preConditions(){
        assertNotNull(mediaPlayer);
        assertNotNull(media);
    }//preConditions()

    /**\
     *
     * Check to see that the mediaFile(video in this case) never stops playing unless interrupted by the user or the player has reached the end og the video.
     * And that the media resumes playing from the position it was paused.
     * @throws IOException
     */
    @Test
    public void playMediaTest() throws IOException{
        mediaPlayer.playMedia(media, videoView);
        assertTrue(mediaPlayer.isMediaPlaying());

        //Stop media and resume from the current position, not from the start or any other position.
        mediaPlayer.pauseMedia();
        int currPos = mediaPlayer.getCurrentPosition();
        mediaPlayer.resumeMedia();
        assertFalse(mediaPlayer.getCurrentPosition() == 0);
        assertEquals(currPos, mediaPlayer.getCurrentPosition());
    }//playMediaTest()

    /**
     * Media gets released as soon as the media is stoped.
     */
    @Test
    public void stopMediaTest() {
        mediaPlayer.stopMedia();
        assertNull(mediaPlayer);
    }//stopMediaTest

    /**
     * Confirm the media name and the output filePath.
     */
    @Test
    public void confirmMediaDetails() {
        assertEquals(media.getName(), "VIDEO");
        assertEquals(media.getFilePath(), filePath);
    }//confirmMediaDetails()

    /**
     * Release all the resources used in this class.
     */
    @After
    public void tearDown() {
        media = null;
    }//tearDown()
}
