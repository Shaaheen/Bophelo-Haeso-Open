package capstone.bophelohaesoopen.HaesoAPI.Controller;

import java.util.List;

/**
 * Interface listener that launches events when specific Bluetooth actions/events occur
 */
public interface BluetoothListener
{
    void onStartScan();
    void onStopScan();
    void onBTDevicesFound(final List<BluetoothUtils.BTDevice> btDevices);
    void onConnected();
    void onDisconnected();
    void onStartReceiving();
    void onStartSending();
    void onReceivingProgress(double progress);
    void onSendingProgress(String progress);
}
