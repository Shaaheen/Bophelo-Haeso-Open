package capstone.bophelohaesoopen;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

import capstone.bophelohaesoopen.HaesoAPI.Controller.MediaPlayer;
import capstone.bophelohaesoopen.HaesoAPI.Model.Audio;

/**
 * Activity for playing audio files
 * NB: Not implemented in the prototype
 */

public class AudioPlayerActivity extends AppCompatActivity
{
    SeekBar seekBar;

    FloatingActionButton stopButton;
    FloatingActionButton playPauseButton;

    TextView counter;

    RelativeLayout videoControls;

    MediaPlayer mediaPlayer;
    Audio audio;

    public static String AUDIO_NAME = "RECORDING_NAME";
    public static String AUDIO_FILE_PATH = "RECORDING_FILE_PATH";
    private static int UPDATE_INTERVAL = 300;

    boolean playing = false;
    boolean paused = false;
    boolean isPlaying = true;

    String recName;
    String recPath;

    Runnable updateSeekbar;
    Handler seekBarUpdateHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Gets name and path of video from previous activity
        Intent intent = getIntent();
        recName = intent.getStringExtra(AUDIO_NAME); // Uses these keys to get values
        recPath = intent.getStringExtra(AUDIO_FILE_PATH);

        audio = new Audio(recName, recPath);
        paused = false;

        mediaPlayer = new MediaPlayer("BHO");
        playAudio();
        initializeViews();

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

                    updateCounter(position);
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
        playPauseButton = (FloatingActionButton)findViewById(R.id.playPauseButton);
        stopButton = (FloatingActionButton)findViewById(R.id.stopButton);

        videoControls = (RelativeLayout)findViewById(R.id.videoControls);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if(fromUser)
                {
                    seekBarUpdateHandler.removeCallbacks(updateSeekbar);
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

        counter = (TextView)findViewById(R.id.counter);

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

    private void stopButtonClick()
    {
        playing = false;
        stopAudio();

        // Close Activity
        finish();
    }

    private void playPauseButtonClick()
    {
        if(playing && !paused)
        {
            Log.i("BHO", "PLAYING AND NOT PAUSED");
            seekBarUpdateHandler.removeCallbacks(updateSeekbar);
            pauseAudio();
            playPauseButton.setImageResource(R.drawable.play_black);
        }
        else if(paused && playing)
        {
            Log.i("BHO", "PAUSED AND PLAYING");
            resumeAudio();
            seekBarUpdateHandler.postDelayed(updateSeekbar, 0);
            playPauseButton.setImageResource(R.drawable.pause_black);
        }
        else if(!paused && !playing)
        {
            Log.i("BHO", "NOT PAUSED AND NOT PLAYING");
            seekBarUpdateHandler.removeCallbacks(updateSeekbar);
            playAudio();
            playPauseButton.setImageResource(R.drawable.pause_black);
        }

    }

    private void playAudio()
    {
        playing = true;
        paused = false;
        try
        {
            mediaPlayer.playAudio(audio);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void pauseAudio()
    {
        if(mediaPlayer.isMediaPlaying())
        {
            mediaPlayer.pauseMedia();
            paused = true;
        }
        else
        {
            Log.i("BHO", "Media not playing");
        }

    }

    private void resumeAudio()
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

    private void stopAudio()
    {
        seekBarUpdateHandler.removeCallbacks(updateSeekbar);
        mediaPlayer.stopMedia();
        mediaPlayer.release();
        playing = false;
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

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed()
    {
        stopAudio();
        super.onBackPressed();
    }

}
