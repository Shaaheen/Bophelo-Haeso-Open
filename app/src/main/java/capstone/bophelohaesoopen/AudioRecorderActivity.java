package capstone.bophelohaesoopen;

import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import capstone.bophelohaesoopen.HaesoAPI.AudioRecorder;

/**
 * Activity for recording audio
 * Starts recording audio automatically when activity is started
 */

public class AudioRecorderActivity extends AppCompatActivity
{
    FloatingActionButton stopButton;
    ImageView recordingIndicator;
    TextView timeView;

    Handler handler;
    Runnable runnable;

    AudioRecorder audioRecorder;

    boolean indicatorVisible = true;
    boolean recordingStopped = false;

    private static int ANIM_DURATION = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);
        initializeViews();

        String outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";

        audioRecorder = new AudioRecorder();
        audioRecorder.prepareForRecording();
        try
        {
            audioRecorder.startRecording();
            animateIndicator();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void initializeViews()
    {
        stopButton = (FloatingActionButton)findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                stopButtonClick();
            }
        });

        timeView = (TextView)findViewById(R.id.timeView);

        recordingIndicator = (ImageView)findViewById(R.id.recordingIndicator);
    }

    private void stopButtonClick()
    {
        try
        {
            audioRecorder.stopRecording();
        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
        }
        showToast();

        // Close the activity
        finish();
    }

    /**
     * Animates blinking indicator when recording is active
     */
    private void animateIndicator()
    {
        final long startTime = System.currentTimeMillis();
        handler = new Handler();
        runnable = new Runnable()
        {
            @Override
            public void run()
            {
                long currentTime = System.currentTimeMillis();
                long elapsedTime = currentTime - startTime;

                elapsedTime = elapsedTime/1000;

                updateTimeView(elapsedTime);

                if (indicatorVisible)
                {
                    recordingIndicator.setVisibility(View.INVISIBLE);
                    indicatorVisible = false;
                } else
                {
                    recordingIndicator.setVisibility(View.VISIBLE);
                    indicatorVisible = true;
                }
                if (!recordingStopped)
                {
                    handler.postDelayed(this, ANIM_DURATION);
                }

            }
        };
        handler.postDelayed(runnable, ANIM_DURATION);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        audioRecorder.stopRecording();
        recordingStopped = true;
        showToast();


        // Close the activity
        finish();
    }

    /**
     * Updates the time counter TextView with the time given
     * @param time - Time value to be used
     */
    private void updateTimeView(long time)
    {
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
        timeView.setText(timeText);
    }

    private void showToast()
    {
        handler.removeCallbacks(runnable);
        Toast toast = Toast.makeText(this, "Recording saved as \"recording.3gp\"", Toast.LENGTH_SHORT);
        toast.show();
    }
}
