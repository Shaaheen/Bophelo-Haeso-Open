package capstone.bophelohaesoopen.HaesoAPI;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

import com.devpaul.bluetoothutillib.SimpleBluetooth;
import com.devpaul.bluetoothutillib.handlers.BluetoothHandler;
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

    public BluetoothListener bluetoothListener;

    //Bluetooth Scanning and enabling library
    private SmoothBluetooth mSmoothBluetooth;
    private Activity activityUIClass;
    public static final int ENABLE_BT_REQUEST = 1;

    //Connecting and Transferring Bluetooth library
    private SimpleBluetooth simpleBluetooth;

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
        simpleBluetooth = new SimpleBluetooth(activityUIClass, activityUIClass,mHandler);

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
        //Enable bluetooth pop up
        activityUIClass.startActivityForResult(enableBluetooth, ENABLE_BT_REQUEST);
    }

    public void connectToAddress(String macAddr){
        simpleBluetooth.connectToBluetoothServer(macAddr);
    }

    /**
     * Initializes scanning and pops up with dialogue of available devices
     */
    public void startScanning(){
        mSmoothBluetooth.doDiscovery();
    }


    /**
     * Sends a media file to a connected Bluetooth device
     * Breaks file into bytes and sends byte array to device
     * @param mediaFile - Media object of file want to send
     */
    public void sendMediaFile(Media mediaFile){
        //verifyStoragePermissions(activityUIClass);
        File file = new File(mediaFile.getFilePath());
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

    public  void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ENABLE_BT_REQUEST) {
            if(resultCode == activityUIClass.RESULT_OK) {
                Log.v("BT","BT ENABLED NOW");
                setupBTUtilities();
                //mSmoothBluetooth.tryConnection();
            }
        }
    }

    //Handler for bluetooth data receiving
    private BluetoothHandler mHandler = new BluetoothHandler() {
        public void handleMessage(Message message) {
            switch(message.what) {
                case 121:
                    byte[] readBuf = (byte[])((byte[])message.obj);
                    String readMessage = new String(readBuf);
                    if(readBuf != null && readBuf.length > 0 && simpleBluetoothListener != null) {
                        simpleBluetoothListener.onBluetoothDataReceived(readBuf, readMessage);
                    }
                    break;
            }

        }
    };

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
                    bluetoothListener.onStartReceiving();
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
                        bluetoothListener.onReceivingProgress(receivingProgress);
                    }
                    intialOffset = 0;

                    //Stop taking in bytes and export file
                    if ( currSizeOfRecFile >= sizeOfFileRec ){
                        out =  new FileOutputStream( Environment.getExternalStorageDirectory() + "/" + nameOfTransferredFile );
                        out.write( fileBytes.toByteArray() );
                        out.close();
                        Log.w( "Rec File","Done receiving file" );
                        Log.w( "File Size","File size: " + fileBytes.toByteArray().length + " Diff is: " + (fileBytes.toByteArray().length - sizeOfFileRec)  );

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
                Log.v("BT","BT bytes not received");
            }
        }

        @Override
        public void onDeviceConnected(BluetoothDevice device) {
            super.onDeviceConnected(device);
            bluetoothListener.onConnected();
            Log.v("BT","Connected");
        }

        @Override
        public void onDeviceDisconnected(BluetoothDevice device) {
            super.onDeviceDisconnected(device);
            bluetoothListener.onDisconnected();
            Log.v("BT","Disconnected");
        }

        //Not used in implementation - only called for interface
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
        }
        @Override
        public void onDeviceUnpaired(BluetoothDevice device) {
            super.onDeviceUnpaired(device);
        }
    };

    //Bluetooth listener used for scanning for devices
    private SmoothBluetooth.Listener mListener = new SmoothBluetooth.Listener() {
        @Override
        public void onDiscoveryStarted() {
            bluetoothListener.onStartScan();
        }

        @Override
        public void onDiscoveryFinished() {
            bluetoothListener.onStopScan();
        }

        @Override
        public void onNoDevicesFound() {
            bluetoothListener.onBTDevicesFound(null);
        }

        @Override
        public void onDevicesFound(final List<Device> deviceList,
                                   final SmoothBluetooth.ConnectionCallback connectionCallback) {

            //Adds BT device(Device class with toString) to a list to display to user and connect
            final List<BTDevice> btDevices = new LinkedList<>();
            System.out.println("DEVICES FOUND");
            for (int i = 0; i < deviceList.size(); i++) {
                System.out.println(deviceList.get(i).getName());
                btDevices.add ( new BTDevice(deviceList.get(i)) );
            }

            bluetoothListener.onBTDevicesFound(btDevices);

        }

        //Not used - Only implemented for interface
        @Override
        public void onDataReceived(int data) {
        }
        @Override
        public void onBluetoothNotSupported() {
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

    };


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



