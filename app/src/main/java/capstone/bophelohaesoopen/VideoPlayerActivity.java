package capstone.bophelohaesoopen;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

import capstone.bophelohaesoopen.HaesoAPI.Controller.MediaPlayer;
import capstone.bophelohaesoopen.HaesoAPI.Model.Video;

/**
 * Activity for video playback - allows, play_black, pause_black, resume functionality
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

    TextView counter;

    FloatingActionButton stopButton;
    FloatingActionButton playPauseButton;

    RelativeLayout videoControls;

    float videoControlsY;

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
    Runnable seekbarUpdater;
    Handler seekBarUpdateHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i("BHO", "ON CREATE");
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
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

        video = new Video(vidName, vidPath);
        paused = false;

        mediaPlayer = new MediaPlayer("BHO");
        videoControlsVisible = true;

        // Ensures the video display is loaded before the video tries to play_black
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
        seekbarUpdater = new Runnable()
        {
            @Override
            public void run()
            {
                if(mediaPlayer.isPrepared())
                {
                    isPlaying = mediaPlayer.isMediaPlaying();
                    int position = mediaPlayer.getCurrentPlayPosition();

                    final int duration = mediaPlayer.getVideoDuration();
                    updateCounter(position);

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
        seekBarUpdateHandler.postDelayed(seekbarUpdater, 0);
    }

    private void initializeViews()
    {
        videoView = (SurfaceView) findViewById(R.id.videoView);
        videoView.setOnTouchListener(this);
        playPauseButton = (FloatingActionButton)findViewById(R.id.playPauseButton);
        stopButton = (FloatingActionButton)findViewById(R.id.stopButton);

        counter = (TextView)findViewById(R.id.counter);

        videoControls = (RelativeLayout)findViewById(R.id.videoControls);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if(fromUser)
                {
                    seekBarUpdateHandler.removeCallbacks(seekbarUpdater);
                    mediaPlayer.seekToPosition(progress);
                    seekBarUpdateHandler.postDelayed(seekbarUpdater, 0);
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

    // region SurfaceView overrides
    /**
     * Launches when video display is done loading
     * Will start to play_black video when video holder is done loading
     * @param holder - The video holder display
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        if(paused)
        {
            resumeVideo();
        }
        else
        {
            playVideo(screenHeight, screenWidth);
        }
        videoControlsY = videoControls.getY();
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

    // region Click Handlers

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
            seekBarUpdateHandler.removeCallbacks(seekbarUpdater);
            pauseVideo();
            playPauseButton.setImageResource(R.drawable.play);
        }
        else if(paused && playing)
        {
            Log.i("BHO", "PAUSED AND PLAYING");
            resumeVideo();
            seekBarUpdateHandler.postDelayed(seekbarUpdater, 0);
            playPauseButton.setImageResource(R.drawable.pause);
        }
        else if(!paused && !playing)
        {
            Log.i("BHO", "NOT PAUSED AND NOT PLAYING");
            seekBarUpdateHandler.removeCallbacks(seekbarUpdater);
            screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
            playVideo(screenWidth, screenHeight);
            playPauseButton.setImageResource(R.drawable.pause);
        }
    }

    //endregion

    //region Utility methods

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

    private void updateCounter(long time)
    {
        time = time/1000;
        int minutes = (int)time / 60;

        int seconds = (int)time % 60;

        String minuteText = "";
        if(minutes < 10)
        {
            minuteText = "0"+minutes;
        }
        else
        {
            minuteText = ""+minutes;
        }

        String secondsText = "";
        if(seconds < 10)
        {
            secondsText = "0"+seconds;
        }
        else
        {
            secondsText = ""+seconds;
        }

        String timeText = minuteText+":"+secondsText;
        counter.setText(timeText);
    }

    // endregion

    // region Video operations

    private void playVideo(int width, int height)
    {
        playing = true;
        paused = false;
        try
        {
            mediaPlayer.playVideo(video, videoView, width, height); //Plays video on given view
        }
        catch (IOException e)
        {
            e.printStackTrace();
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
        seekBarUpdateHandler.removeCallbacks(seekbarUpdater);
        mediaPlayer.stopMedia();
        mediaPlayer.release();
        playing = false;
    }

    //endregion

    //region Activity overrides

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        Log.i("BHO", "ON CONFIGURATION CHANGED");
        super.onConfigurationChanged(newConfig);
        videoControls.invalidate();

//        recreate();

//        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
//        {
//            videoControls.setY(videoControlsY);
//        }

    }

    @Override
    protected void onRestart()
    {
        Log.i("BHO", "ON RESTART");
        super.onRestart();
    }

    @Override
    protected void onPause()
    {
        Log.i("BHO", "ON PAUSE");
        super.onPause();
        if(!mediaPlayer.mediaPlayerNull())
        {
            pauseVideo();
        }
    }

    @Override
    protected void onResume()
    {
        Log.i("BHO", "ON RESUME");
        super.onResume();
        if(!mediaPlayer.mediaPlayerNull())
        {
            resumeVideo();
        }
    }

    @Override
    protected void onStop()
    {
        Log.i("BHO", "ON STOP");
        super.onStop();
        if(!mediaPlayer.mediaPlayerNull())
        {
            stopVideo();
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        stopVideo();
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }
    // endregion

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