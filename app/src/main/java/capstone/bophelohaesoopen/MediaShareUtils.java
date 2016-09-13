package capstone.bophelohaesoopen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import capstone.bophelohaesoopen.HaesoAPI.BluetoothUtils;
import capstone.bophelohaesoopen.HaesoAPI.BluetoothListener;
import capstone.bophelohaesoopen.HaesoAPI.Media;

/**
 * Handles connecting to BT device and sending off media files
 */
public class MediaShareUtils
{
    public BluetoothUtils bluetoothUtils;

    private boolean sendMedia;
    private Media mediaToSend;

    ProgressDialog indeterminatePD;
    ProgressDialog determinatePD;

    // Dialog to display while scanning for devices
    private MaterialDialog scanningDialog;
    private Activity activityM;

    public MediaShareUtils(final Context ctx, final Activity activity)
    {
        mediaToSend = null;
        sendMedia = false;
        bluetoothUtils = new BluetoothUtils(ctx, activity);
        activityM = activity;

        bluetoothUtils.bluetoothListener = new BluetoothListener()
        {
            @Override
            public void onStartScan()
            {
                indeterminatePD = new ProgressDialog(activityM);
                indeterminatePD.setMessage("Scanning for devices");
                indeterminatePD.setCancelable(false);
                indeterminatePD.setIndeterminate(true);
                indeterminatePD.show();
            }

            @Override
            public void onStopScan()
            {
//                scanningDialog.hide();
                indeterminatePD.dismiss();
            }


            @Override
            public void onBTDevicesFound(final List<BluetoothUtils.BTDevice> btDevices)
            {

                if (btDevices == null || btDevices.isEmpty())
                {
                    Toast.makeText(ctx, "No devices found", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(ctx, "Devices found", Toast.LENGTH_SHORT).show();
                //Create pop up dialogue that displays a list of all the found devices
                //All are clickable
                final MaterialDialog dialog = new MaterialDialog.Builder(activityM)
                        .title("Devices")
                        .items(btDevices)
                        .itemsCallback(new MaterialDialog.ListCallback()
                        {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text)
                            {
                                Toast.makeText(ctx, "Device No. " + which + " : " + text + " selected ", Toast.LENGTH_SHORT).show();
                                //Connects to selected device
                                //simpleBluetooth.connectToBluetoothServer(deviceList.get(which).getAddress());
                                bluetoothUtils.connectToAddress(btDevices.get(which).getAddress());


                            }
                        })
                        .show();

                dialog.show();
            }

            @Override
            public void onConnected()
            {
                Toast.makeText(ctx, "Connected, Send : " + sendMedia, Toast.LENGTH_SHORT).show();
                if (sendMedia)
                {
                    try
                    {
                        Thread.sleep(700);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    bluetoothUtils.sendMediaFile(mediaToSend);
                    sendMedia = false;
                }
            }

            @Override
            public void onDisconnected()
            {
                Toast.makeText(ctx, "Disconnected", Toast.LENGTH_SHORT).show();
                //sendMedia = false;
            }

            @Override

            public void onStartReceiving()
            {
                Toast.makeText(ctx, "Receving file...", Toast.LENGTH_SHORT).show();
                //Toast.makeText(activity, "Receiving", Toast.LENGTH_SHORT).show();
                String receivingDialogTitle = activityM.getResources().getString(R.string.receiving_dialog_title);
                createProgressDialogue(receivingDialogTitle);
            }

            @Override
            public void onStartSending() {
                createProgressDialogue("Sending File...");
            }

            @Override
            public void onReceivingProgress(double progress)
            {
                Log.v("BT", "Received " + progress + "% ");
                if (progress >= 99.0)
                {
                    sendMedia = false;
                    determinatePD.dismiss();
                    Toast.makeText(ctx, "Received file", Toast.LENGTH_SHORT).show();
                }

                Log.v("BT", "Received " + progress + "% ");
                determinatePD.setProgress((int) progress);
            }

            @Override
            public void onSendingProgress(String progress) {
                Log.v("BT","Sending " + progress + "%");

                int p = Integer.valueOf(progress);
                if(p == 100)
                {
                    determinatePD.dismiss();
                    Log.v("BT","Done Sending :" + progress + "%");
                }
                else
                {
                    determinatePD.setProgress(p);
                }
            }

        };

    }

    /**
     *
     */
    private void createProgressDialogue(String dialogueText){
        determinatePD = new ProgressDialog(activityM);
        determinatePD.setTitle(dialogueText);
        determinatePD.setCancelable(false);
        determinatePD.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        determinatePD.setIndeterminate(false);
        determinatePD.setProgress(0);
        determinatePD.setMax(100);
        determinatePD.show();
    }

    /**
     * Scans for devices
     */
    private void scanForDevices()
    {
        bluetoothUtils.startScanning();
    }

    /**
     * Sets the media file that will be sent scans for devices
     * When device connected, then listener will catch connection event and send media that was
     * set in this method
     *
     * @param media media file to be sent via Bluetooth
     */
    public void sendMedia(Media media)
    {
        this.mediaToSend = media;
        sendMedia = true;
        scanForDevices();
    }

}
