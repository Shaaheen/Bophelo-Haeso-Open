package capstone.bophelohaesoopen.HaesoAPI;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.devpaul.bluetoothutillib.SimpleBluetooth;
import com.devpaul.bluetoothutillib.utils.BluetoothUtility;
import com.devpaul.bluetoothutillib.utils.SimpleBluetoothListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

/**
 * Class to act as interface to bluetooth functionality
 */
public class BluetoothUtils {

    public HaesoBTListener haesoBTListener;

    //Bluetooth Scanning and enabling library
    private SmoothBluetooth mSmoothBluetooth;
    private Activity activityUIClass;
    public static final int ENABLE_BT_REQUEST = 1;

    // Dialog to display while scanning for devices
    //MaterialDialog scanningDialog;

    //Connecting and Transferring Bluetooth library
    private SimpleBluetooth simpleBluetooth;
    private static final int SCAN_REQUEST = 119;
    private static final int CHOOSE_SERVER_REQUEST = 120;

    //File data structures and vars for receiving a file
    ByteArrayOutputStream fileBytes;
    int sizeOfFileRec; //Size of File being received
    int currSizeOfRecFile; //stores size of file transferred so far
    double receivingProgress; //percentage of file received so far
    int intialOffset; //Offset to ensure file size message not in resulting file bytes
    String nameOfTransferredFile;
    private static int NUMBER_OF_COLONS = 4;

    public BluetoothUtils( Context context, Activity activity ) {
        mSmoothBluetooth = new SmoothBluetooth(context); //Uses bluetooth library
        activityUIClass = activity; //The activity of the class stored

        if ( !mSmoothBluetooth.isBluetoothEnabled() ){
            enableBT();
        }
        else {
            setupBTUtilities();
        }

    }

    private void setupBTUtilities() {
        mSmoothBluetooth.setListener(mListener);  //for bluetooth events

        //Library to handle connecting and transferring through bluetooth
        simpleBluetooth = new SimpleBluetooth(activityUIClass, activityUIClass);

        currSizeOfRecFile = 0;
        intialOffset = 0;
        nameOfTransferredFile = "Media";

        simpleBluetooth.setSimpleBluetoothListener(simpleBluetoothListener);//Sets BT listeners
        simpleBluetooth.initializeSimpleBluetooth();
        simpleBluetooth.setInputStreamType( BluetoothUtility.InputStreamType.BUFFERED );

        //Initializes device as server in case another device attempts to connect to it
        simpleBluetooth.createBluetoothServerConnection();
    }

    private void enableBT(){
        Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        Toast.makeText(activityUIClass, "Bluetooth not enabled", Toast.LENGTH_SHORT).show();
        //Enable bluetooth pop up
        activityUIClass.startActivityForResult(enableBluetooth, ENABLE_BT_REQUEST);
    }


    /**
     * Initializes scanning and pops up with dialogue of available devices
     */
    public void startScanning(){
        mSmoothBluetooth.doDiscovery();
    }


    public void tryConnection(){
        mSmoothBluetooth.tryConnection();
    }

    /**
     * Sends a media file to a connected Bluetooth device
     * Breaks file into bytes and sends byte array to device
     * @param mediaFile - Media object of file want to send
     */
    public void sendMediaFile(Media mediaFile){
        //verifyStoragePermissions(activityUIClass);
        File file = new File(Environment.getExternalStorageDirectory() + mediaFile.filePath);
        System.out.println(file.getName());
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); //To accumulate file bytes
        FileInputStream fis = null;
        byte[] mediaBytes = new byte[0]; //Initialise bytes for media file
        try {
            //Gets bytes from file - 1024 bytes at a time
            fis = new FileInputStream(file);
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf))) {
                baos.write(buf, 0, n);
            }

            mediaBytes = baos.toByteArray(); //Accumalating bytes

        } catch (IOException e) {
            e.printStackTrace();
        }
        //Must specify the file size to receiver so receiver knows how many bytes to expect
        String sendFileSize = "file_size:" + baos.size() + ":file_name:" + mediaFile.getFileName() + ":";
        simpleBluetooth.sendData(sendFileSize);

        Log.w("Sending size", baos.size() +"" );
        Log.w("Byte size of string",sendFileSize.getBytes().length + "");

        simpleBluetooth.sendData(mediaBytes); //Send file as bytes
    }

    public void createServer(){
        simpleBluetooth.createBluetoothServerConnection();
    }

    public  void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ENABLE_BT_REQUEST) {
            if(resultCode == activityUIClass.RESULT_OK) {
                Log.v("BT","BT ENABLED NOW");
                setupBTUtilities();
                //mSmoothBluetooth.tryConnection();
            }
        }
    }
    private SimpleBluetoothListener simpleBluetoothListener = new SimpleBluetoothListener() {
        /**
         * Gets data that has been transferred to Device
         * DATA ONLY COMES IN BYTE ARRAYS OF SIZE 1024 (byte[1024])
         * Hence byte arrays are accumalated up until file size and then exported into file
         * @param bytes
         * @param data
         */
        @Override
        public void onBluetoothDataReceived(byte[] bytes, String data) {
            super.onBluetoothDataReceived(bytes, data);

            //Log.w("SIMPLEBT", "Data received");
            //Log.w("Byte array length ",bytes.length+"");
            if (data != null){

                //Initial message received indicates the file size so we know
                // when to stop reading in bytes that are being received
                if (data.contains("file_size:")){
                    Toast.makeText(activityUIClass, "Receving File...", Toast.LENGTH_SHORT).show();
                    haesoBTListener.onStartReceiving();
                    receivingProgress = 0.0;
                    fileBytes = new ByteArrayOutputStream();
                    String[] splitFileInfo = data.split(":");
                    sizeOfFileRec = Integer.parseInt( splitFileInfo[1] );
                    nameOfTransferredFile = splitFileInfo[3];
                    Log.w("Size of File",sizeOfFileRec + "");
                    intialOffset = (splitFileInfo[0] + splitFileInfo[1] + splitFileInfo[2] + splitFileInfo[3]).getBytes().length + NUMBER_OF_COLONS;
                    Log.w( "Byte size ", intialOffset + "" );

                    currSizeOfRecFile = 0;
                }
                FileOutputStream out = null;
                try {

                    //Writes bytes just received into accumalating array of bytes
                    //Will accumulate all bytes received and export all bytes into one file
                    fileBytes.write( bytes , intialOffset, bytes.length - intialOffset);
                    currSizeOfRecFile += bytes.length ;
                    currSizeOfRecFile -= intialOffset;
                    double currProg = ( (currSizeOfRecFile+0.0) /(sizeOfFileRec+0.0) );
                    currProg = currProg *100;
                    if (currProg - receivingProgress > 1.0){
                        receivingProgress = currProg;
                        haesoBTListener.onReceivingProgress(receivingProgress);
                    }
                    //Log.v("BLUETOOTH","Gets HERE");
                    intialOffset = 0;

                    //Stop taking in bytes and export file
                    if ( currSizeOfRecFile >= sizeOfFileRec ){
                        out =  new FileOutputStream( Environment.getExternalStorageDirectory() + "/" + nameOfTransferredFile );
                        out.write( fileBytes.toByteArray() );
                        out.close();
                        Log.w( "Rec File","Done receiving file" );
                        Log.w( "File Size","File size: " + fileBytes.toByteArray().length + " Diff is: " + (fileBytes.toByteArray().length - sizeOfFileRec)  );
                        Toast.makeText( activityUIClass, "Received File.", Toast.LENGTH_SHORT ).show();

                        //Reset all variables for next file
                        fileBytes = null;
                        sizeOfFileRec = 0;
                        currSizeOfRecFile = 0;
                        intialOffset = 0;
                        nameOfTransferredFile = "Media";
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(activityUIClass, "BT Bytes not received", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onDeviceConnected(BluetoothDevice device) {
            super.onDeviceConnected(device);
            Toast.makeText(activityUIClass, "Simple BT Connected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDeviceDisconnected(BluetoothDevice device) {
            super.onDeviceDisconnected(device);
            Toast.makeText(activityUIClass, "Simple BT Disconnected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDiscoveryStarted() {
            super.onDiscoveryStarted();
        }

        @Override
        public void onDiscoveryFinished() {
            super.onDiscoveryFinished();
        }

        @Override
        public void onDevicePaired(BluetoothDevice device) {
            super.onDevicePaired(device);
            Toast.makeText(activityUIClass, "Simple BT paired", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDeviceUnpaired(BluetoothDevice device) {
            super.onDeviceUnpaired(device);
        }
    };

    private SmoothBluetooth.Listener mListener = new SmoothBluetooth.Listener() {
        @Override
        public void onBluetoothNotSupported() {
            Toast.makeText(activityUIClass, "Bluetooth not found", Toast.LENGTH_SHORT).show();
            activityUIClass.finish();
        }

        @Override
        public void onBluetoothNotEnabled() {
        }

        @Override
        public void onConnecting(Device device) {
        }

        @Override
        public void onConnected(Device device) {
        }

        @Override
        public void onDisconnected() {
        }

        @Override
        public void onConnectionFailed(Device device) {
        }

        @Override
        public void onDiscoveryStarted() {
            Toast.makeText(activityUIClass, "Searching", Toast.LENGTH_SHORT).show();
            haesoBTListener.onStartScan();
        }

        @Override
        public void onDiscoveryFinished() {
            haesoBTListener.onStopScan();
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
                btDevices.add ( new BTDevice(deviceList.get(i)) );
            }

            haesoBTListener.onBTDevicesFound(btDevices);

        }

        @Override
        public void onDataReceived(int data) {
        }

    };

    public void connectToAddress(String macAddr){
        simpleBluetooth.connectToBluetoothServer(macAddr);
    }

    //Class that adds a toString method to Device class
    public class BTDevice extends Device{

        public BTDevice(Device device) {
            super(device.getName(),device.getAddress(),device.isPaired());
        }

        public String toString(){
            return getName();
        }
    }
}



