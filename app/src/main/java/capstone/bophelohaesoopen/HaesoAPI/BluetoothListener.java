package capstone.bophelohaesoopen.HaesoAPI;

import android.bluetooth.BluetoothAdapter;

import java.util.List;

public interface BluetoothListener
{
    void onStartScan();
    void onStopScan();
    void onBTDevicesFound(List<BluetoothUtils.BTDevice> btDevices);
    void onConnected();
    void onDisconnected();
    void onStartReceiving();
    void onReceivingProgress(double progress);
}
