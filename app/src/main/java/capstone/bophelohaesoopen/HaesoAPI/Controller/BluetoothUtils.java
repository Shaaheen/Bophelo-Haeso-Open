package capstone.bophelohaesoopen.HaesoAPI.Controller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

import capstone.bophelohaesoopen.HaesoAPI.Model.LogEntry;
import capstone.bophelohaesoopen.HaesoAPI.Model.Media;
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
    private static final int ENABLE_BT_REQUEST = 1;

    //Connecting and Transferring Bluetooth library
    private SimpleBluetooth simpleBluetooth;

    //File data structures and vars for receiving a file
    private ByteArrayOutputStream fileBytes;
    private int sizeOfFileRec; //Size of File being received
    private int currSizeOfRecFile; //stores size of file transferred so far
    private double receivingProgress; //percentage of file received so far
    private int intialOffset; //Offset to ensure file size message not in resulting file bytes
    private String nameOfTransferredFile;
    private static int NUMBER_OF_COLONS = 4;
    private String connectedMacAddr;

    /**
     * Bluetooth Utility to handle sending and receiving via bluetooth
     * @param context
     * @param activity
     */
    public BluetoothUtils( Context context, Activity activity ) {
        mSmoothBluetooth = new SmoothBluetooth(context); //Uses bluetooth library
        activityUIClass = activity; //The activity of the class stored
        connectedMacAddr =null;

        if ( !mSmoothBluetooth.isBluetoothEnabled() ){
            enableBT();
        }
        else {
            setupBTUtilities();
        }

    }

    /**
     * Initialises all BT variables
     */
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

    /**
     * Connects to device with given mac address via Bluetooth
     * @param macAddr - string of a desired device's mac address
     */
    public void connectToAddress(String macAddr){
        if (connectedMacAddr!=null && macAddr.equals(connectedMacAddr) ){
            bluetoothListener.onConnected();
        }
        else{
            simpleBluetooth.connectToBluetoothServer(macAddr);
            connectedMacAddr = macAddr;
        }
    }

    /**
     * Initializes scanning and pops up with dialogue of available devices
     */
    public void startScanning(){
        mSmoothBluetooth.doDiscovery();

        //Log to database
        LogEntry logEntry = new LogEntry(LogEntry.LogType.BLUETOOTH,"Start Sharing",null);
        if ( DatabaseUtils.isDatabaseSetup() ){
            DatabaseUtils.getInstance().addLog(logEntry);
        }
    }


    /**
     * Sends a media file to a connected Bluetooth device
     * Breaks file into bytes and sends byte array to device
     * @param mediaFile - Media object of file want to send
     */
    public void sendMediaFile(Media mediaFile){
        bluetoothListener.onStartSending(); //launch event saying sending has started

        File file = new File(mediaFile.getFilePath());
        Log.w("BT",mediaFile.getFileName());

        ByteArrayOutputStream baos = new ByteArrayOutputStream(); //To accumulate file bytes
        FileInputStream fis = null;
        byte[] mediaBytes = new byte[0]; //Initialise bytes for media file
        try {
            //Gets bytes from media file - 1024 bytes at a time
            fis = new FileInputStream(file);
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf))) {
                baos.write(buf, 0, n);
            }

            mediaBytes = baos.toByteArray(); //Accumulating bytes

        } catch (IOException e) {
            e.printStackTrace();
        }
        //Must specify the file size to receiver so receiver knows how many bytes to expect
        String sendFileSize = "file_size:" + baos.size() + ":file_name:" + mediaFile.getFileName() + ":";
        simpleBluetooth.sendData(sendFileSize);

        Log.w("Sending size", baos.size() +"" );
        Log.w("Byte size of string",sendFileSize.getBytes().length + "");

        //Log to database
        LogEntry logEntry = new LogEntry(LogEntry.LogType.BLUETOOTH,"Share Media",mediaFile.getFileName());
        if ( DatabaseUtils.isDatabaseSetup() ){
            DatabaseUtils.getInstance().addLog(logEntry);
        }

        //Convert to byte array to Byte object array to be able to send as param to Async task
        Byte[] byteObjects = new Byte[mediaBytes.length];

        int i=0;
        // Associating Byte array values with bytes. (byte[] to Byte[])
        for(byte b: mediaBytes)
            byteObjects[i++] = b;  // Autoboxing.

        //simpleBluetooth.sendData(mediaBytes);
        new SendMediaTask().execute(byteObjects); //Send file as bytes on new thread
    }

    /**
     * Sends a progress measure back to sender device indicating current progress on
     * receiving file
     * @param progress percentage of progress made so far
     */
    private void sendProgressToOtherDevice(int progress){
        //Add leading zeros as need string to be exact length (Max length is 100)
        String prog = "_Progress_;" +("000" + progress).substring( ("" + progress).length() );
        simpleBluetooth.sendData(prog);
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
         * @param bytes - bytes of received data
         * @param data - String data received
         */
        @Override
        public void onBluetoothDataReceived(byte[] bytes, String data) {
            super.onBluetoothDataReceived(bytes, data);
            if (data != null){

                //if the data received is for progress monitoring i.e Sending progres
                if (data.contains("_Progress_;")){
                    String[] splitte = data.split(";");
                    String progress = splitte[1]; //Gets progress number -percentage
                    progress = Integer.valueOf(progress).toString(); //Removes leading zeros
                    bluetoothListener.onSendingProgress(progress); //Report new Sending progress
                }
                else{
                    //Initial message received indicates the file size so we know
                    // when to stop reading in bytes that are being received
                    if (data.contains("file_size:")){
                        bluetoothListener.onStartReceiving(); //launch event - Receving started
                        receivingProgress = 0.0;

                        fileBytes = new ByteArrayOutputStream();

                        //Separates details from found information
                        String[] splitFileInfo = data.split(":");
                        sizeOfFileRec = Integer.parseInt( splitFileInfo[1] );
                        nameOfTransferredFile = splitFileInfo[3];

                        Log.w("Size of File",sizeOfFileRec + "");

                        //Set offset to know what bytes to skip
                        intialOffset = (splitFileInfo[0] + splitFileInfo[1] + splitFileInfo[2] + splitFileInfo[3]).getBytes().length + NUMBER_OF_COLONS;

                        Log.w( "Byte size ", intialOffset + "" );
                        currSizeOfRecFile = 0; //New file receiving


                    }

                    FileOutputStream out = null;
                    try {

                        //Writes bytes just received into accumalating array of bytes
                        //Will accumulate all bytes received and export all bytes into one file
                        fileBytes.write( bytes , intialOffset, bytes.length - intialOffset);
                        currSizeOfRecFile += bytes.length ;
                        currSizeOfRecFile -= intialOffset;
                        intialOffset = 0;

                        //To track progress,
                        // Checks received file size so far against expected file size
                        double currProg = ( (currSizeOfRecFile+0.0) /(sizeOfFileRec+0.0) );
                        currProg = currProg *100;
                        //Launch event if another 1% has been received
                        if (currProg - receivingProgress >= 1.0 || currProg == 100.0){
                            Log.w("BT","Progress found and sending: " + currProg);
                            receivingProgress = currProg;
                            bluetoothListener.onReceivingProgress(receivingProgress);
                            sendProgressToOtherDevice((int) receivingProgress); //Notify other device
                        }


                        //Stop taking in bytes and export file
                        if ( currSizeOfRecFile >= sizeOfFileRec ){
                            out =  new FileOutputStream( Environment.getExternalStorageDirectory() + "/" + nameOfTransferredFile );
                            out.write( fileBytes.toByteArray() );
                            out.close();

                            Log.w( "Rec File","Done receiving file" );
                            Log.w( "File Size","File size: " + fileBytes.toByteArray().length + " Diff is: " + (fileBytes.toByteArray().length - sizeOfFileRec)  );

                            //Log to database
                            LogEntry logEntry = new LogEntry(LogEntry.LogType.BLUETOOTH,"Received Media",nameOfTransferredFile);
                            if ( DatabaseUtils.isDatabaseSetup() ){
                                DatabaseUtils.getInstance().addLog(logEntry);
                            }

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
            }
            else{
                Log.v("BT","BT bytes not received");
            }
        }

        @Override
        public void onDeviceConnected(BluetoothDevice device) {
            super.onDeviceConnected(device);
            bluetoothListener.onConnected(); //launch event indicating new connection
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

    /**
     * Takes in bytes and sends to connected device on new thread
     */
    private class SendMediaTask extends AsyncTask< Byte, Integer, Long>
    {
        @Override
        protected Long doInBackground(Byte... bytes) {
            Log.w("BT","Send Task called " + bytes.length);
            int j=0;
            byte[] mediaBytes = new byte[bytes.length];
            // Unboxing byte values. (Byte[] to byte[])
            for(Byte b: bytes)
                mediaBytes[j++] = b.byteValue();

            simpleBluetooth.sendData( mediaBytes);
            Log.w("BT","Sent with Task: " + mediaBytes.length);
            return null;
        }
    }
}



