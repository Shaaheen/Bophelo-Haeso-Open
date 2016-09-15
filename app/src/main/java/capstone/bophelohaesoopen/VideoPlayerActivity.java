package capstone.bophelohaesoopen;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import java.io.IOException;

import capstone.bophelohaesoopen.HaesoAPI.Controller.MediaPlayer;
import capstone.bophelohaesoopen.HaesoAPI.Model.Video;

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

    int screenWidth;
    int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);



        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        else
        {
            Log.i("APP", "Action bar null");
        }

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

    private void playVideo(int width, int height)
    {
        playing = true;
        try
        {
            mediaPlayer.playMedia(video, videoView, width, height); //Plays video on given view
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

        mediaPlayer.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(android.media.MediaPlayer mediaPlayer)
            {
                // so it fits on the screen
                int videoWidth = mediaPlayer.getVideoWidth();
                int videoHeight = mediaPlayer.getVideoHeight();
                Log.i("BHO", "Video height = "+videoHeight);
                Log.i("BHO", "Video width = "+videoWidth);

                float videoProportion = (float) videoWidth / (float) videoHeight;

                float screenProportion = (float) screenWidth / (float) screenHeight;
                android.view.ViewGroup.LayoutParams lp = videoView.getLayoutParams();

                if (videoProportion > screenProportion)
                {
                    lp.width = screenWidth;
                    lp.height = (int) ((float) screenWidth / videoProportion);
                }
                else
                {
                    lp.width = (int) (videoProportion * (float) screenHeight);
                    lp.height = screenHeight;
                }
                videoView.setLayoutParams(lp);

            }
        });
//
        playVideo(screenHeight, screenWidth);
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
            playVideo(screenWidth, screenHeight);
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

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }
}
