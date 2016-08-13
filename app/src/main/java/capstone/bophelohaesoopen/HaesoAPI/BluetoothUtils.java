package capstone.bophelohaesoopen.HaesoAPI;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.LinkedList;
import java.util.List;

import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

/**
 * Created by Shaaheen on 8/8/2016.
 * Class to act as interface to bluetooth functionality
 */
public class BluetoothUtils {
    private SmoothBluetooth mSmoothBluetooth;
    private Activity activityUIClass;
    public static final int ENABLE_BT_REQUEST = 1;


    public BluetoothUtils( Context context, Activity activity ) {
        mSmoothBluetooth = new SmoothBluetooth(context); //Uses bluetooth library
        mSmoothBluetooth.setListener(mListener);  //for bluetooth events
        activityUIClass = activity; //The activity of the class stored
    }

    public void startScanning(){
        mSmoothBluetooth.doDiscovery();
    }

    public  void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ENABLE_BT_REQUEST) {

            if(resultCode == activityUIClass.RESULT_OK) {

                mSmoothBluetooth.tryConnection();

            }

        }

    }

    private SmoothBluetooth.Listener mListener = new SmoothBluetooth.Listener() {
        @Override
        public void onBluetoothNotSupported() {
            Toast.makeText(activityUIClass, "Bluetooth not found", Toast.LENGTH_SHORT).show();
            activityUIClass.finish();
        }

        @Override
        public void onBluetoothNotEnabled() {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            Toast.makeText(activityUIClass, "Bluetooth not enabled", Toast.LENGTH_SHORT).show();
            activityUIClass.startActivityForResult(enableBluetooth, ENABLE_BT_REQUEST);
        }

        @Override
        public void onConnecting(Device device) {
            Toast.makeText(activityUIClass, "Connecting to " + device.getName(), Toast.LENGTH_SHORT).show();
//            mStateTv.setText("Connecting to");
//            mDeviceTv.setText(device.getName());
        }

        @Override
        public void onConnected(Device device) {
            Toast.makeText(activityUIClass, "Connected", Toast.LENGTH_SHORT).show();
//            mStateTv.setText("Connected to");
//            mDeviceTv.setText(device.getName());
//            mConnectionLayout.setVisibility(View.GONE);
//            mDisconnectButton.setVisibility(View.VISIBLE);
        }

        @Override
        public void onDisconnected() {
            Toast.makeText(activityUIClass, "Disconnected", Toast.LENGTH_SHORT).show();
//            mStateTv.setText("Disconnected");
//            mDeviceTv.setText("");
//            mDisconnectButton.setVisibility(View.GONE);
//            mConnectionLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onConnectionFailed(Device device) {
//            mStateTv.setText("Disconnected");
//            mDeviceTv.setText("");
            Toast.makeText(activityUIClass, "Failed to connect to " + device.getName(), Toast.LENGTH_SHORT).show();
            if (device.isPaired()) {
                mSmoothBluetooth.doDiscovery();
            }
        }

        @Override
        public void onDiscoveryStarted() {
            Toast.makeText(activityUIClass, "Searching", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDiscoveryFinished() {
            Toast.makeText(activityUIClass, "Done Searching", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNoDevicesFound() {
            Toast.makeText(activityUIClass, "No devices found", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDevicesFound(final List<Device> deviceList,
                                   final SmoothBluetooth.ConnectionCallback connectionCallback) {

            Toast.makeText(activityUIClass, "Device(s) found", Toast.LENGTH_SHORT).show();


            //Adds BT device(Device class with toString) to a list to display to user and connect
            final List<BTDevice> btDevices = new LinkedList<>();
            System.out.println("DEVICES FOUND");
            for (int i = 0; i < deviceList.size(); i++) {
                System.out.println(deviceList.get(i).getName());
                btDevices.add (new BTDevice(deviceList.get(i)) );
            }

            //Create pop up dialogue that displays a list of all the found devices
            //All are clickable
            final MaterialDialog dialog = new MaterialDialog.Builder(activityUIClass)
                    .title("Devices")
                    .items(btDevices)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            Toast.makeText(activityUIClass, "Device No. " + which + " : " + text + " selected", Toast.LENGTH_SHORT).show();
                            //connectionCallback.connectTo( deviceList.get(which) );
                        }
                    })
                    .show();

            dialog.show();

        }

        @Override
        public void onDataReceived(int data) {

        }

    };
}

class BTDevice extends Device{

    public BTDevice(String name, String address, boolean paired) {
        super(name, address, paired);
    }

    public BTDevice(Device device) {
        super(device.getName(),device.getAddress(),device.isPaired());
    }

    public String toString(){
        return getName();
    }
}
