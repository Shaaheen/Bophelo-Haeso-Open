package capstone.bophelohaesoopen;

/**
 * Class to display a given video - allows play, pause and seek functionality
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import capstone.bophelohaesoopen.HaesoAPI.MediaPlayer;
import capstone.bophelohaesoopen.HaesoAPI.Video;

public class VideoPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback
{
    SurfaceView videoView;
    String vidName;
    String vidPath;

    public static String VIDEO_NAME = "VIDEO_NAME";
    public static String VIDEO_FILE_PATH = "VIDEO_FILE_PATH";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoView = (SurfaceView) findViewById(R.id.videoView);

        //Gets name and path of video from previous activity
        Intent intent = getIntent();
        vidName = intent.getStringExtra("VideoName"); //Uses these keys to get values
        vidPath = intent.getStringExtra("VideoPath");

        //Ensures the video display is loaded before the video tries to play
        SurfaceHolder holder = videoView.getHolder();
        holder.addCallback(this);

    }

    /**
     * Launches when video display is done loading
     * Will start to play video when video holder is done loading
     * @param holder - The video holder display
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder){
        Video video = new Video( vidName, vidPath );
        MediaPlayer mediaPlayer = new MediaPlayer("BHO");
        try {
            mediaPlayer.playMedia( video , videoView ); //Plays video on given view
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
