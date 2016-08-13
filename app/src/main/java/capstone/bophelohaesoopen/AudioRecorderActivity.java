package capstone.bophelohaesoopen;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AudioRecorderActivity extends AppCompatActivity
{
    FloatingActionButton stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);

        initialize();
    }

    private void initialize()
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
    }

    public void stopButtonClick()
    {

    }
}