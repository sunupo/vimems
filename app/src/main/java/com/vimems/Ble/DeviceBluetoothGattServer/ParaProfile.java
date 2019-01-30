package com.vimems.Ble.DeviceBluetoothGattServer;


import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

public class ParaProfile {
    private static final String TAG = ParaProfile.class.getSimpleName();
    //SERVICE
    public static final  UUID PARA_SERVICE=UUID.fromString("00001106-0000-1000-8000-00805F9B34FB");
    //CHARACTERISTIC
    public static final UUID MUSCLE1_CHARACTERISTIC=UUID.fromString("00002a0f-0000-1000-8000-00805f9b34fb");

    public static BluetoothGattService createParaService() {

        BluetoothGattService bluetoothGattService=new BluetoothGattService(PARA_SERVICE,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);

        BluetoothGattCharacteristic muscle1Characteristic=new BluetoothGattCharacteristic(MUSCLE1_CHARACTERISTIC,
                BluetoothGattCharacteristic.PROPERTY_READ|BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_READ|BluetoothGattCharacteristic.PERMISSION_WRITE);
        byte[] b={9};

        muscle1Characteristic.setValue(b);
        bluetoothGattService.addCharacteristic(muscle1Characteristic);


        return bluetoothGattService;
    }
    public  static byte[] getPara(){
        byte[] filed =new byte[10];
        filed[0]=100;
        filed[1]=100;
        filed[2]=100;
        filed[3]=100;
        filed[4]=100;
        filed[5]=100;
        filed[6]=100;
        filed[7]=100;
        filed[8]=100;
        filed[9]=100;
        return filed;
    }


}
