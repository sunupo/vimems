package com.vimems.Ble.DeviceBluetoothGattServer;


import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ParaProfile {
    private static final String TAG = ParaProfile.class.getSimpleName();
    //SERVICE
    public static final  UUID PARA_SERVICE=UUID.fromString("00001106-0000-1000-8000-00805F9B34FB");
    //CHARACTERISTIC
    public static final UUID MUSCLE1_CHARACTERISTIC=UUID.fromString("00002a0f-0000-1000-8000-00805f9b34fb");
    public static final UUID MUSCLE2_CHARACTERISTIC=UUID.fromString("00002a1f-0000-1000-8000-00805f9b34fb");
    public static final UUID MUSCLE3_CHARACTERISTIC=UUID.fromString("00002a2f-0000-1000-8000-00805f9b34fb");
    public static final UUID MUSCLE4_CHARACTERISTIC=UUID.fromString("00002a3f-0000-1000-8000-00805f9b34fb");
    public static final UUID MUSCLE5_CHARACTERISTIC=UUID.fromString("00002a4f-0000-1000-8000-00805f9b34fb");
    public static final UUID MUSCLE6_CHARACTERISTIC=UUID.fromString("00002a5f-0000-1000-8000-00805f9b34fb");
    public static final UUID MUSCLE7_CHARACTERISTIC=UUID.fromString("00002a6f-0000-1000-8000-00805f9b34fb");
    public static final UUID MUSCLE8_CHARACTERISTIC=UUID.fromString("00002a7f-0000-1000-8000-00805f9b34fb");
    public static final UUID MUSCLE9_CHARACTERISTIC=UUID.fromString("00002a8f-0000-1000-8000-00805f9b34fb");
    public static final UUID MUSCLE10_CHARACTERISTIC=UUID.fromString("00002a9f-0000-1000-8000-00805f9b34fb");
    public static List<UUID> uuidList=new ArrayList<>();
    static {
        uuidList.add(MUSCLE1_CHARACTERISTIC);
        uuidList.add(MUSCLE2_CHARACTERISTIC);
        uuidList.add(MUSCLE3_CHARACTERISTIC);
        uuidList.add(MUSCLE4_CHARACTERISTIC);
        uuidList.add(MUSCLE5_CHARACTERISTIC);
        uuidList.add(MUSCLE6_CHARACTERISTIC);
        uuidList.add(MUSCLE7_CHARACTERISTIC);
        uuidList.add(MUSCLE8_CHARACTERISTIC);
        uuidList.add(MUSCLE9_CHARACTERISTIC);
        uuidList.add(MUSCLE10_CHARACTERISTIC);
    }

    public static BluetoothGattService createParaService() {

        BluetoothGattCharacteristic[] muscleBluetoothGattCharacteristics=new BluetoothGattCharacteristic[13];
        BluetoothGattService bluetoothGattService;

        bluetoothGattService=new BluetoothGattService(PARA_SERVICE,BluetoothGattService.SERVICE_TYPE_PRIMARY);
        for (int i = 0; i <uuidList.size() ; i++) {
            muscleBluetoothGattCharacteristics[i]=new BluetoothGattCharacteristic(uuidList.get(i),
                    BluetoothGattCharacteristic.PROPERTY_READ|BluetoothGattCharacteristic.PROPERTY_WRITE,
                    BluetoothGattCharacteristic.PERMISSION_READ|BluetoothGattCharacteristic.PERMISSION_WRITE);
            bluetoothGattService.addCharacteristic(muscleBluetoothGattCharacteristics[i]);
        }
//
//        byte[] b={9};
//
//        muscle1Characteristic1.setValue(b);
//        bluetoothGattService.addCharacteristic(muscle1Characteristic1);
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
