package com.vimems.Ble.DeviceBluetoothGattServer;

/**
 * 每一个周边BluetoothGattServer，包含多个服务Service，每一个Service包含多个特征Characteristic。
 * 1.new一个特征：character = new BluetoothGattCharacteristic(
 *      UUID.fromString(characteristicUUID),
 *      BluetoothGattCharacteristic.PROPERTY_NOTIFY,
 *      BluetoothGattCharacteristic.PERMISSION_READ);
 * 2.new一个服务：service = new BluetoothGattService(UUID.fromString(serviceUUID),
 *      BluetoothGattService.SERVICE_TYPE_PRIMARY);
 * 3.把特征添加到服务：service.addCharacteristic(character);
 * 4.获取BluetoothManager：manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
 * 5.获取/打开周边：BluetoothGattServer server = manager.openGattServer(this,
 * new BluetoothGattServerCallback(){...});
 * 6.把service添加到周边：server.addService(service);
 * 7.开始广播service：Google还没有广播Service的API，等吧！！！！！所以目前我们还不能让一个Android手机作为周边来提供数据。
 */

public class SDS {
}
