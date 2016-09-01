package capstone.bophelohaesoopen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import capstone.bophelohaesoopen.HaesoAPI.BluetoothUtils;
import capstone.bophelohaesoopen.HaesoAPI.HaesoBTListener;
import capstone.bophelohaesoopen.HaesoAPI.Video;

/**
 * Activity for sharing selected media files with other devices via Bluetooth
 */

public class MediaShareActivity extends AppCompatActivity
{
    AppCompatButton scanButton;
    AppCompatButton sendButton;

    BluetoothUtils bluetoothUtils;

    ProgressBar progressBar;

    // Dialog to display while scanning for devices
    MaterialDialog scanningDialog;
    MediaShareActivity activityM;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_share);

        initializeViews();
        activityM = this;

        bluetoothUtils = new BluetoothUtils(this, MediaShareActivity.this);

        bluetoothUtils.haesoBTListener = new HaesoBTListener() {
            @Override
            public void onStartScan() {
                Toast.makeText(getApplicationContext(), "SCANNNINNINININ", Toast.LENGTH_SHORT).show();
                scanningDialog = new MaterialDialog.Builder(activityM)
                        .title("Devices")
                        .items("Scanning . . .")
                        .show();
            }

            @Override
            public void onStopScan() {
                scanningDialog.hide();
            }

            @Override
            public void onBTDevicesFound(final List<BluetoothUtils.BTDevice> btDevices) {

                if (btDevices == null || btDevices.isEmpty()){
                    return;
                }
                //Create pop up dialogue that displays a list of all the found devices
                //All are clickable
                final MaterialDialog dialog = new MaterialDialog.Builder(activityM)
                        .title("Devices")
                        .items(btDevices)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                Toast.makeText(getApplicationContext(), "Device No. " + which + " : " + text + " selected ", Toast.LENGTH_SHORT).show();
                                //Connects to selected device
                                //simpleBluetooth.connectToBluetoothServer(deviceList.get(which).getAddress());
                                bluetoothUtils.connectToAddress(btDevices.get(which).getAddress());
                            }
                        })
                        .show();

                dialog.show();
            }

            @Override
            public void onConnected() {

            }

            @Override
            public void onDisconnected() {

            }

            @Override
            public void onStartReceiving() {
                Toast.makeText(getApplicationContext(), "RecEIVING", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReceivingProgress(double progress) {
                Log.v("BT","Received " + progress + "% ");
            }

        };
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
        // Parameters hard-coded for the sake of the prototype
        Video video = new Video("Sample_video", "/video_sample.mp4");
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

    // Future use
    public void showProgressBar()
    {
        progressBar.setVisibility(View.VISIBLE);
    }

    // Future use
    public void hideProgressBar()
    {
        progressBar.setVisibility(View.INVISIBLE);
    }
}
