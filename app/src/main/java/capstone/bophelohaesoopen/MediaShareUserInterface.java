package capstone.bophelohaesoopen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import capstone.bophelohaesoopen.HaesoAPI.Controller.BluetoothUtils;
import capstone.bophelohaesoopen.HaesoAPI.Controller.BluetoothListener;
import capstone.bophelohaesoopen.HaesoAPI.Model.Media;
import capstone.bophelohaesoopen.R;

/**
 * Handles connecting to BT device and sending off media files
 */
public class MediaShareUserInterface
{
    public BluetoothUtils bluetoothUtils;

    public enum State {IDLE, SENDING, SCANNING, FAILED, SENT, RECEIVING, CANCELLED, RECEIVED};

    public State state = State.IDLE;
    private boolean sendMedia;
    private Media mediaToSend;

    ProgressDialog indeterminatePD;
    ProgressDialog determinatePD;

    // Dialog to display while scanning for devices
    private MaterialDialog scanningDialog;
    private Activity activityM;

    public MediaShareUserInterface(final Context ctx, final Activity activity)
    {
        mediaToSend = null;
        determinatePD = null;
        sendMedia = false;
        bluetoothUtils = new BluetoothUtils(ctx, activity);
        activityM = activity;

        bluetoothUtils.bluetoothListener = new BluetoothListener()
        {
            @Override
            public void onStartScan()
            {
                //Dialog displaying scanning
                indeterminatePD = new ProgressDialog(activityM);
                indeterminatePD.setMessage("Scanning for devices");
                indeterminatePD.setCancelable(false);
                indeterminatePD.setIndeterminate(true);
                indeterminatePD.show();
                state = State.SCANNING;
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
                    state = State.FAILED;
                    Toast.makeText(ctx, "No devices found", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Create pop up dialogue that displays a list of all the found devices
                //All are clickable
                final MaterialDialog dialog = new MaterialDialog.Builder(activityM)
                        .title("Devices")
                        .canceledOnTouchOutside(false)
                        .dismissListener(new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface)
                            {
                                state = State.CANCELLED;
                            }
                        })
                        .cancelable(true)
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
                state = State.IDLE;
                //if want to send after connection
                if (sendMedia)
                {
                    try
                    {
                        Thread.sleep(700); //Sleeps to wait for bluetooth to set up
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
                state = State.FAILED;
                //sendMedia = false;
            }

            @Override

            public void onStartReceiving()
            {
                state = State.RECEIVING;
                //Toast.makeText(ctx, "Receving file...", Toast.LENGTH_SHORT).show();
                //Toast.makeText(activity, "Receiving", Toast.LENGTH_SHORT).show();
                String receivingDialogTitle = activityM.getResources().getString(R.string.receiving_dialog_title);
                createProgressDialogue(receivingDialogTitle);
            }

            @Override
            public void onStartSending()
            {
                state = State.SENDING;
                createProgressDialogue("Sending file...");
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
                    state = State.RECEIVED;
                }

                Log.v("BT", "Received " + progress + "% ");
                determinatePD.setProgress((int) progress);
            }

            @Override
            public void onSendingProgress(String progress) {
                Log.v("BT","Sending " + progress + "%");

                int p = Integer.valueOf(progress);
                if(p >= 99)
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
     * Creates a new Dialog to show progress if does not exist already
     */
    private void createProgressDialogue(String dialogueText){
        if (determinatePD == null){
            determinatePD = new ProgressDialog(activityM);
            determinatePD.setCancelable(false);
            determinatePD.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            determinatePD.setIndeterminate(false);
        }
        determinatePD.setTitle(dialogueText);
        determinatePD.setProgress(0);
        determinatePD.setMax(100);
        determinatePD.setButton(ProgressDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                indeterminatePD.dismiss();

                // Cancel whatever operation is being performed
                state = State.CANCELLED;
                bluetoothUtils.end();
            }
        });
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

    public void end(){
        mediaToSend = null;
        determinatePD = null;
        sendMedia = false;
        bluetoothUtils.end();
    }
}
