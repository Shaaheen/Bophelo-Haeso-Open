package capstone.bophelohaesoopen;

import android.Manifest;
import android.content.DialogInterface;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.FloatingActionButton;
import android.app.AlertDialog;
import android.app.AlertDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import capstone.bophelohaesoopen.HaesoAPI.Controller.AudioRecorder;

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

    AlertDialog.Builder alert;

    AudioRecorder audioRecorder;
    boolean indicatorVisible = true;
    boolean recordingStopped = false;

    private static int ANIM_DURATION = 500;
    private Bundle savedInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);

        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.savedInstance = savedInstanceState;

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO))
            {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        2);

            }
            else
            {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,

                        new String[]{Manifest.permission.RECORD_AUDIO},

                        2);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        else
        {
            setUpRecordingActivity();
        }
    }

    @Override

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case 2:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    setUpRecordingActivity();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else
                {
                    Log.w("Rec", "Permission denied");
                    onBackPressed();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;


            // other 'case' lines to check for other
            // permissions this app might request

        }
    }

    private void setUpRecordingActivity()
    {
        // Initialize activity view components
        initializeViews();

        alert = new AlertDialog.Builder(this);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        else
        {
            Log.i("APP", "Action bar null");
        }

        audioRecorder = new AudioRecorder();
        String durationLimit = getResources().getString(R.string.recording_duration_limit_minutes);
        audioRecorder.setRecordingDurationLimit(Integer.parseInt(durationLimit));
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

    /**
     * Initialises view components of the activity
     */
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

        // Close the activity
        finish();
    }

    /**
     * Animates blinking indicator when recording is happening
     */
    private void animateIndicator()
    {
        final long startTime = System.currentTimeMillis();

        // Handler to run the Runnable below every ANIM_DURATION milliseconds
        handler = new Handler();
        // Runnable containing the code to hide/show the recording indicator
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
                    if(!audioRecorder.maxDurationReached)
                    {
                        handler.postDelayed(this, ANIM_DURATION);
                    }
                    else
                    {
                        try
                        {
                            audioRecorder.stopRecording();

                            recordingStopped = true;

                            String recordingLimitMessage = getResources().getString(R.string.recording_duration_limit_message);
                            String recordingLimitTitle = getResources().getString(R.string.recording_duration_limit_title);
                            String okText = getResources().getString(R.string.positive_action_button_text);
                            alert.setTitle(recordingLimitTitle);
                            alert.setMessage(recordingLimitMessage);
                            alert.setPositiveButton(okText, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {
                                    dialogInterface.dismiss();
                                    finish();
                                }
                            });
                            alert.show();
                        }
                        catch (IllegalStateException e)
                        {
                            e.printStackTrace();
                        }

                    }

                }

            }
        };
        handler.postDelayed(runnable, ANIM_DURATION);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if(audioRecorder != null)
        {
            audioRecorder.stopRecording();
            recordingStopped = true;
        }

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

    @Override
    public boolean onSupportNavigateUp()
    {
        super.onBackPressed();
        audioRecorder.stopRecording();
        recordingStopped = true;
        finish();
        return true;
    }
}
