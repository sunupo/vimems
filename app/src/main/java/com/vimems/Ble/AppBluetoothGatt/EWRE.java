package com.vimems.Ble.AppBluetoothGatt;

/**
 * 为了拿到中央BluetoothGatt，可要爬山涉水十八弯：
 * 1.先拿到BluetoothManager：bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
 * 2.再拿到BluetoothAdapt：btAdapter = bluetoothManager.getAdapter();
 * 3.开始扫描：btAdapter.startLeScan( BluetoothAdapter.LeScanCallback);
 * 4.从LeScanCallback中得到BluetoothDevice：public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {.....}
 * 5.用BluetoothDevice得到BluetoothGatt：gatt = device.connectGatt(this, true, gattCallback);
 * 终于拿到中央BluetoothGatt了，它有一堆方法（查API吧），调用这些方法，你就可以通过BluetoothGattCallback和周边BluetoothGattServer交互了。
 */

public class EWRE {
}
