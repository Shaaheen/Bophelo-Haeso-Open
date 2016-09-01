package capstone.bophelohaesoopen;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import capstone.bophelohaesoopen.HaesoAPI.BluetoothUtils;
import capstone.bophelohaesoopen.HaesoAPI.HaesoBTListener;
import capstone.bophelohaesoopen.HaesoAPI.Media;

/**
 * Handles connecting to BT device and sending off media files
 */
public class MediaShareUtils {

    private BluetoothUtils bluetoothUtils;

    private boolean sendMedia;
    private Media toSendMedia;

    // Dialog to display while scanning for devices
    private MaterialDialog scanningDialog;
    private Activity activityM;

    public MediaShareUtils(final Context ctx, Activity activity) {
        toSendMedia = null;
        sendMedia = false;
        bluetoothUtils = new BluetoothUtils(ctx, activity);
        activityM = activity;

        bluetoothUtils.haesoBTListener = new HaesoBTListener() {
            @Override
            public void onStartScan() {
                //Toast.makeText(ctx, "SCANNNINNINININ", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ctx,"No devices found",Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(ctx,"Devices found",Toast.LENGTH_SHORT).show();
                //Create pop up dialogue that displays a list of all the found devices
                //All are clickable
                final MaterialDialog dialog = new MaterialDialog.Builder(activityM)
                        .title("Devices")
                        .items(btDevices)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
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
            public void onConnected() {
                Toast.makeText(ctx,"Connected, Send : " + sendMedia,Toast.LENGTH_SHORT).show();
                if (sendMedia){
                    bluetoothUtils.sendMediaFile(toSendMedia);
                    sendMedia = false;
                }
            }

            @Override
            public void onDisconnected() {
                Toast.makeText(ctx,"Disconnected",Toast.LENGTH_SHORT).show();
                //sendMedia = false;
            }

            @Override
            public void onStartReceiving() {
                Toast.makeText(ctx, "Receving file...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReceivingProgress(double progress) {
                Log.v("BT","Received " + progress + "% ");
                if (progress>=99.0){
                    sendMedia = false;
                    Toast.makeText(ctx, "Received file", Toast.LENGTH_SHORT).show();
                }
            }

        };

        //scanForDevices();
    }

    public void scanForDevices(){
        bluetoothUtils.startScanning();
    }

    public void sendMedia(Media media){
        this.toSendMedia = media;
        //bluetoothUtils.sendMediaFile(toSendMedia);
        sendMedia = true;
        scanForDevices();
    }


}
