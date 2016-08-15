package capstone.bophelohaesoopen;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import capstone.bophelohaesoopen.HaesoAPI.BluetoothUtils;
import capstone.bophelohaesoopen.HaesoAPI.Video;

public class BluetoothActivity extends AppCompatActivity {

    Button scanButton;
    Button pairedButton;
    Button sendButton;
    Button serverButton;
    BluetoothUtils bluetoothUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //Call BluetoothUtils to assist in bluetooth functionality
        bluetoothUtils = new BluetoothUtils( getApplicationContext() , BluetoothActivity.this );

        scanButton = (Button) findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothUtils.startScanning();
            }
        });

        pairedButton = (Button) findViewById(R.id.pairedButton);
        pairedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothUtils.tryConnection();
            }
        });

        sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Video video = new Video("Wookie_Video","/VID-20160305-WA0012.mp4");
                bluetoothUtils.sendMediaFile(video);
            }
        });

        serverButton = (Button) findViewById(R.id.BTServer);
        serverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothUtils.createServer();
            }
        });
    }

    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bluetoothUtils.handleActivityResult(requestCode,resultCode,data); //For switching on BT

    }

}
