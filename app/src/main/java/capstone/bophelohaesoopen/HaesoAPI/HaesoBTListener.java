package capstone.bophelohaesoopen.HaesoAPI;

import android.bluetooth.BluetoothAdapter;

import java.util.List;

public interface HaesoBTListener{
    void onStartScan();
    void onStopScan();
    void onBTDevicesFound(List<BluetoothUtils.BTDevice> btDevices);
}
