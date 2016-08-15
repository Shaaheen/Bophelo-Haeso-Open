package capstone.bophelohaesoopen;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;

import capstone.bophelohaesoopen.HaesoAPI.MediaPlayer;
import capstone.bophelohaesoopen.HaesoAPI.Video;

/**
 * Activity for video playback - allows, play, pause, resume functionality
 * Starts playing video automatically when activity is started
 */

public class VideoPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback
{
    SurfaceView videoView;
    String vidName;
    String vidPath;

    MediaPlayer mediaPlayer;
    Video video;

    FloatingActionButton stopButton;
    FloatingActionButton playPauseButton;

    // Used for Intent parameters
    public static String VIDEO_NAME = "VIDEO_NAME";
    public static String VIDEO_FILE_PATH = "VIDEO_FILE_PATH";

    boolean playing = true;
    boolean paused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        initializeViews();

        // Gets name and path of video from previous activity
        Intent intent = getIntent();
        vidName = intent.getStringExtra(VIDEO_NAME); // Uses these keys to get values
        vidPath = intent.getStringExtra(VIDEO_FILE_PATH);

        video = new Video( vidName, vidPath );

        mediaPlayer = new MediaPlayer("BHO");

        // Ensures the video display is loaded before the video tries to play
        SurfaceHolder holder = videoView.getHolder();
        holder.addCallback(this);
    }

    private void initializeViews()
    {
        videoView = (SurfaceView) findViewById(R.id.videoView);
        playPauseButton = (FloatingActionButton)findViewById(R.id.playPauseButton);
        stopButton = (FloatingActionButton)findViewById(R.id.stopButton);

        playPauseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                playPauseButtonClick();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                stopButtonClick();
            }
        });
    }

    private void playVideo()
    {
        playing = true;
        try
        {
            mediaPlayer.playMedia( video , videoView ); //Plays video on given view
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // region SurfaceView overrides
    /**
     * Launches when video display is done loading
     * Will start to play video when video holder is done loading
     * @param holder - The video holder display
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        playVideo();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2)
    {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {

    }
    //endregion


    @Override
    public void onBackPressed()
    {
        stopVideo();
        super.onBackPressed();
    }

    private void stopButtonClick()
    {
        playing = false;
        stopVideo();

        // Close Activity
        finish();
    }

    private void playPauseButtonClick()
    {
        if(playing && !paused)
        {
            pauseVideo();
            playPauseButton.setImageResource(R.drawable.play);
        }
        else if(paused && playing)
        {
            resumeVideo();
            playPauseButton.setImageResource(R.drawable.pause);
        }
        else if(!paused && !playing)
        {
            playVideo();
            playPauseButton.setImageResource(R.drawable.pause);
        }

    }

    private void pauseVideo()
    {
        if(mediaPlayer.isMediaPlaying())
        {
            mediaPlayer.pauseMedia();
            paused = true;
        }
    }

    private void resumeVideo()
    {
        try
        {
            mediaPlayer.resumeMedia();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        paused = false;
    }

    private void stopVideo()
    {
        mediaPlayer.stopMedia();
        playing = false;
    }
}
