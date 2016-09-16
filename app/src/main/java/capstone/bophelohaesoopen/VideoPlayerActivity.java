package capstone.bophelohaesoopen;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import java.io.IOException;

import capstone.bophelohaesoopen.HaesoAPI.Controller.MediaPlayer;
import capstone.bophelohaesoopen.HaesoAPI.Model.Video;

/**
 * Activity for video playback - allows, play, pause, resume functionality
 * Starts playing video automatically when activity is started
 */

public class VideoPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnTouchListener
{
    SurfaceView videoView;
    String vidName;
    String vidPath;

    MediaPlayer mediaPlayer;
    Video video;

    SeekBar seekBar;

    FloatingActionButton stopButton;
    FloatingActionButton playPauseButton;

    RelativeLayout videoControls;

    boolean videoControlsVisible = true;
    private static int ANIMATION_DURATION = 600;
    private static int CONTROLS_VISIBLE_DURATION = 4000;
    private static int UPDATE_INTERVAL = 300;

    // Used for Intent parameters
    public static String VIDEO_NAME = "VIDEO_NAME";
    public static String VIDEO_FILE_PATH = "VIDEO_FILE_PATH";

    boolean playing = true;
    boolean paused = false;
    boolean isPlaying = true;

    int screenWidth;
    int screenHeight;

    Handler handler;
    Runnable runnable;
    Runnable updateSeekbar;
    Handler seekBarUpdateHandler;

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
        paused = false;

        mediaPlayer = new MediaPlayer("BHO");
        videoControlsVisible = true;

        // Ensures the video display is loaded before the video tries to play
        SurfaceHolder holder = videoView.getHolder();
        holder.addCallback(this);

        handler = new Handler();
        runnable = new Runnable()
        {
            @Override
            public void run()
            {
                if(!paused)
                {
                    hideControls();
                }
            }
        };
        handler.postDelayed(runnable, CONTROLS_VISIBLE_DURATION);


        seekBarUpdateHandler = new Handler();
        updateSeekbar = new Runnable()
        {
            @Override
            public void run()
            {
                Log.i("BHO", "Update runnable called");
                if(mediaPlayer.isPrepared())
                {
                    isPlaying = mediaPlayer.isMediaPlaying();
                    int position = mediaPlayer.getCurrentPlayPosition();

                    final int duration = mediaPlayer.getVideoDuration();
                    seekBar.setMax(duration);

                    if(position > duration)
                    {
                        position = duration;
                    }

                    seekBar.setProgress(position);
                }
                if(!paused && isPlaying)
                {
                    seekBarUpdateHandler.postDelayed(this, UPDATE_INTERVAL);
                }

            }
        };
        seekBarUpdateHandler.postDelayed(updateSeekbar, 0);

    }

    private void initializeViews()
    {
        videoView = (SurfaceView) findViewById(R.id.videoView);
        videoView.setOnTouchListener(this);
        playPauseButton = (FloatingActionButton)findViewById(R.id.playPauseButton);
        stopButton = (FloatingActionButton)findViewById(R.id.stopButton);

        videoControls = (RelativeLayout)findViewById(R.id.videoControls);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
//        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if(fromUser)
                {
                    seekBarUpdateHandler.removeCallbacks(updateSeekbar);
//                    int videoDuration = mediaPlayer.getVideoDuration();
//                    float percentage = progress/100f;
//                    int newPosition = (int)(percentage * videoDuration);
                    mediaPlayer.seekToPosition(progress);
                    seekBarUpdateHandler.postDelayed(updateSeekbar, 0);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

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
        paused = false;
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

    private void hideControls()
    {
        float yPos = videoControls.getY();
        float yDelta = videoControls.getHeight();

        ValueAnimator anim = ValueAnimator.ofFloat(yPos, yPos + yDelta);
        anim.setDuration(ANIMATION_DURATION);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                videoControls.setY((Float) valueAnimator.getAnimatedValue());
            }
        });
        anim.start();
        videoControlsVisible = false;
    }

    private void showControls()
    {
        float yPos = videoControls.getY();
        float yDelta = videoControls.getHeight();

        ValueAnimator anim = ValueAnimator.ofFloat(yPos, yPos - yDelta);
        anim.setDuration(ANIMATION_DURATION);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                videoControls.setY((Float) valueAnimator.getAnimatedValue());
            }
        });
        anim.start();
        videoControlsVisible = true;
    }

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
            Log.i("BHO", "PLAYING AND NOT PAUSED");
            seekBarUpdateHandler.removeCallbacks(updateSeekbar);
            pauseVideo();
            playPauseButton.setImageResource(R.drawable.play);
        }
        else if(paused && playing)
        {
            Log.i("BHO", "PAUSED AND PLAYING");
            resumeVideo();
            seekBarUpdateHandler.postDelayed(updateSeekbar, 0);
            playPauseButton.setImageResource(R.drawable.pause);
        }
        else if(!paused && !playing)
        {
            Log.i("BHO", "NOT PAUSED AND NOT PLAYING");
            seekBarUpdateHandler.removeCallbacks(updateSeekbar);
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
        seekBarUpdateHandler.removeCallbacks(updateSeekbar);
        mediaPlayer.stopMedia();
        playing = false;
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        switch(motionEvent.getAction())
        {
            case MotionEvent.ACTION_DOWN:

                // Stop any running Runnable
                handler.removeCallbacks(runnable);

                if(videoControlsVisible)
                {
                    hideControls();
                }
                else
                {
                    showControls();
                    handler.postDelayed(runnable, CONTROLS_VISIBLE_DURATION);
                }

        }
        return true;
    }
}
