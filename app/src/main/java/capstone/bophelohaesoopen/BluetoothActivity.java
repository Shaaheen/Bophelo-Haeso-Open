package capstone.bophelohaesoopen;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

public class BluetoothActivity extends AppCompatActivity {

    Button scanButton;
    private SmoothBluetooth mSmoothBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mSmoothBluetooth = new SmoothBluetooth(this);

        mSmoothBluetooth.setListener(mListener);

        scanButton = (Button) findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmoothBluetooth.doDiscovery();
            }
        });
    }


    private SmoothBluetooth.Listener mListener = new SmoothBluetooth.Listener() {
        @Override
        public void onBluetoothNotSupported() {
            Toast.makeText(BluetoothActivity.this, "Bluetooth not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onBluetoothNotEnabled() {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            Toast.makeText(BluetoothActivity.this, "Bluetooth not enabled", Toast.LENGTH_SHORT).show();
            //startActivityForResult(enableBluetooth, ENABLE_BT__REQUEST);
        }

        @Override
        public void onConnecting(Device device) {
//            mStateTv.setText("Connecting to");
//            mDeviceTv.setText(device.getName());
        }

        @Override
        public void onConnected(Device device) {
//            mStateTv.setText("Connected to");
//            mDeviceTv.setText(device.getName());
//            mConnectionLayout.setVisibility(View.GONE);
//            mDisconnectButton.setVisibility(View.VISIBLE);
        }

        @Override
        public void onDisconnected() {
//            mStateTv.setText("Disconnected");
//            mDeviceTv.setText("");
//            mDisconnectButton.setVisibility(View.GONE);
//            mConnectionLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onConnectionFailed(Device device) {
//            mStateTv.setText("Disconnected");
//            mDeviceTv.setText("");
            Toast.makeText(BluetoothActivity.this, "Failed to connect to " + device.getName(), Toast.LENGTH_SHORT).show();
//            if (device.isPaired()) {
//                mSmoothBluetooth.doDiscovery();
//            }
        }

        @Override
        public void onDiscoveryStarted() {
            Toast.makeText(BluetoothActivity.this, "Searching", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDiscoveryFinished() {
            Toast.makeText(BluetoothActivity.this, "Done Searching", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNoDevicesFound() {
            Toast.makeText(BluetoothActivity.this, "No devices found", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDevicesFound(final List<Device> deviceList,
                                   final SmoothBluetooth.ConnectionCallback connectionCallback) {

            Toast.makeText(BluetoothActivity.this, "Device found", Toast.LENGTH_SHORT).show();
//            final MaterialDialog dialog = new MaterialDialog.Builder(BluetoothActivity.this)
//                    .title("Devices")
//                    .adapter(new DevicesAdapter(BluetoothActivity.this, deviceList), null)
//                    .build();
//
//            ListView listView = (ListView) dialog.getView();
//            if (listView != null) {
//                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        connectionCallback.connectTo(deviceList.get(position));
//                        dialog.dismiss();
//                    }
//
//                });
//            }
//
//            dialog.show();

        }

        @Override
        public void onDataReceived(int data) {

        }

    };

}
