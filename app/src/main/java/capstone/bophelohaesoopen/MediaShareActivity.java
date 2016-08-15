package capstone.bophelohaesoopen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ProgressBar;

import capstone.bophelohaesoopen.HaesoAPI.BluetoothUtils;
import capstone.bophelohaesoopen.HaesoAPI.Video;

public class MediaShareActivity extends AppCompatActivity
{
    AppCompatButton scanButton;
    AppCompatButton sendButton;

    BluetoothUtils bluetoothUtils;

    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_share);

        initializeViews();

        bluetoothUtils = new BluetoothUtils(this, MediaShareActivity.this);
    }

    private void initializeViews()
    {
        scanButton = (AppCompatButton)findViewById(R.id.scanButton);
        sendButton = (AppCompatButton)findViewById(R.id.sendButton);

        //region Button OnClickListeners
        scanButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                scanButtonClick();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sendButtonClick();
            }
        });

        //endregion

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
    }

    private void sendButtonClick()
    {
        Video video = new Video("Wookie_video", "/video_sample.mp4");
        bluetoothUtils.sendMediaFile(video);
    }

    private void scanButtonClick()
    {
        bluetoothUtils.startScanning();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // For switching on Bluetooth
        bluetoothUtils.handleActivityResult(requestCode, resultCode, data);
    }

    public void showProgressBar()
    {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar()
    {
        progressBar.setVisibility(View.VISIBLE);
    }
}
