/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vimems.mainactivity;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.vimems.Ble.AppBluetoothGatt.SampleGattAttributes;
import com.vimems.Ble.DeviceBluetoothGattServer.ParaProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.vimems.Ble.DeviceBluetoothGattServer.ParaProfile.uuidList;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;//这样得到mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
    private int mConnectionState = STATE_DISCONNECTED;//这个变量没被其他地方使用到，声明为static过后应该可以被Activity获取
    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);

    //Activity有一个动态广播接收下面这些数据
//    源码中的常量
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String ACTION_DATA_WRITE_SUCCESS=
            "com.example.bluetooth.le.ACTION_DATA_WRITE_SUCCESS";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    //多个蓝牙连接状态的广播
    public static final int DEFAULT_MAX_DEVICE_AVAILABLE=8;
    private static int maxDeviceNum;//根据地址列表大小动态初始化该值
    public static final int MUSCLE_CHARACTERISTIC_NUM=10;//肌肉总数目10块
    public static  String[] ACTION_GATT_CONNECTED_ARRAY;
    public static String[] ACTION_GATT_DISCONNECTED_ARRAY;
    public static String[] ACTION_GATT_SERVICES_DISCOVERED_ARRAY;
    public static String[][] ACTION_DATA_AVAILABLE_ARRAY;
    public static String[][] ACTION_DATA_WRITE_SUCCESS_ARRAY;
    public static String[][] EXTRA_DATA_ARRAY;
//    static {
//        for (int i = 0; i < MAX_DEVICE_NAME; i++) {
//            ACTION_GATT_CONNECTED_ARRAY[i]= "com.example.bluetooth.le.ACTION_GATT_CONNECTED_ARRAY["+i+"]";
//            ACTION_GATT_DISCONNECTED_ARRAY[i]="com.example.bluetooth.le.ACTION_GATT_DISCONNECTED_ARRAY["+i+"]";
//            ACTION_GATT_SERVICES_DISCOVERED_ARRAY[i]="com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED_ARRAY["+i+"]";
//        }
//        for (int i = 0; i < MAX_DEVICE_NAME; i++) {
//            for (int j = 0; j < MUSCLE_CHARACTERISTIC_NUM; j++) {
//                ACTION_DATA_AVAILABLE_ARRAY[i][j]="com.example.bluetooth.le.ACTION_DATA_AVAILABLE_ARRAY["+i+"]";
//                ACTION_DATA_WRITE_SUCCESS_ARRAY[i][j]="com.example.bluetooth.le.ACTION_DATA_WRITE_SUCCESS_ARRAY["+i+"]["+j+"]";
//                EXTRA_DATA_ARRAY[i][j]="com.example.bluetooth.le.EXTRA_DATA_ARRAY["+i+"]["+j+"]";
//
//            }
//        }
//    }

    //多人模式下，记录activity传递过来的BluetoothDevice和BluetoothGatt
    //注意BluetoothGatt是在服务里面得到的，仔细思考一下是否需要在activity使用

    private List<String> bluetoothDeviceAddressList=new ArrayList<>();
    private List<BluetoothDevice> bluetoothDeviceList=new ArrayList<>();//这个变量未被访问read
    private List<BluetoothGatt> bluetoothGattList=new ArrayList<>();
//    private BluetoothGattCallback[] bluetoothGattCallbacks;
    private List<BluetoothGattCallback> bluetoothGattCallbackList=new ArrayList<>();
    // TODO: 2/19/2019 应该需要在绑定服务过后，先根据设备数目，把状态初始化为 STATE_DISCONNECTED
    //直接初始化数组大小为8，因为最多只能连接7-8个gattserver设备，这个变量没被其他地方使用到，声明为static过后应该可以被Activity获取
    private int[] mConnectionStates={STATE_DISCONNECTED,STATE_DISCONNECTED,STATE_DISCONNECTED,STATE_DISCONNECTED
            ,STATE_DISCONNECTED,STATE_DISCONNECTED,STATE_DISCONNECTED,STATE_DISCONNECTED};//    private int mConnectionState = STATE_DISCONNECTED;



    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    /**
     * 在后面会用
     * BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);//这种方法过时了，新的用法好像是用ScanResultAdapter来得到device
     * mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
     */
    //本机是一个client，去监听GATT服务器发送的数据，gattserver应该要声明BluetoothGattServer和BluetoothGattServerCallback

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                /**
                 * Gatt发现服务mBluetoothGatt.discoverServices());
                 */
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

            if(status==BluetoothGatt.GATT_SUCCESS){
                broadcastUpdate(ACTION_DATA_WRITE_SUCCESS, characteristic);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        //Callback triggered as a result of a remote characteristic notification.
        //需要先设置mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    //发送广播到字符串制定的action
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();//The properties contain a bit mask of property flags indicating the features of this characteristic.
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            }
        }
        sendBroadcast(intent);
    }

    /**
     * 多人模式下，服务广播数据
     * 因为，broadcastUpdateForMultidevice需要提取characteristic中的数据到intent中，
     * 而因为有多个设备，需要用到EXTRA_DATA_ARRAY来区分是哪个设备的数据
     * final int i,int j,用来构建EXTRA_DATA_ARRAY[i][j]，标记第i个设备的第j个characteristic的数据
     */
    private void broadcastUpdate(final String action,final BluetoothGattCharacteristic characteristic,
                                 final int i,int j) {
        final Intent intent = new Intent(action);

        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for(byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            intent.putExtra(EXTRA_DATA_ARRAY[i][j], new String(data) + "\n" + stringBuilder.toString());
        }
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        for (int i = 0; i < bluetoothDeviceAddressList.size(); i++) {
            Log.d(TAG, "onUnbind: i="+i);
            Log.d(TAG, "onUnbind: bluetoothDeviceAddressList.size()="+bluetoothDeviceAddressList.size());
            close(i);
        }
        Log.d(TAG, "onUnbind: super.onUnbind(intent);");
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

//    TODO: 2/19/2019 不应该这样，就应该一个一个地址的传进来，分多次调用，方便区分哪一个设备connect是否成功；放在一个函数里只有用一个数组来区分
//   原计划用一个connectNew一次连接多个设备
//    public boolean connectNew(List<String> addressList) {
//
//        if (mBluetoothAdapter == null ||addressList.size()==0) {
//            Log.w(TAG, "BluetoothAdapter not initialized  or addressList.size()==0");
//            return false;
//        }
//
//        // Previously connected device.  Try to reconnect.
//        for (int i = 0; i < bluetoothDeviceAddressList.size(); i++) {
//            if(bluetoothDeviceAddressList.get(i) != null && addressList.get(i).equals(bluetoothDeviceAddressList.get(i))
//                    &&bluetoothGattList.get(i) != null){
//                Log.d(TAG, "Trying to use  existing bluetoothGattList for connection.");
//                if(bluetoothGattList.get(i).connect()){
//                    mConnectionState = STATE_CONNECTING;
//                    mConnectionStates[i] = STATE_CONNECTING;
//                    return true;
//                }else{
//                    return false;
//                }
//            }
//        }
//        //检查MAC地址是否全部为大写
//        for (int i = 0; i < addressList.size(); i++) {
//            if(!BluetoothAdapter.checkBluetoothAddress(addressList.get(i)) ){
//                return false;
//            }else{
//                bluetoothDeviceList.add(mBluetoothAdapter.getRemoteDevice(addressList.get(i)));
//                if(bluetoothDeviceList.get(i)==null){
//                    Log.w(TAG, addressList.get(i)+"Device not found.  Unable to connect.");
//                    return false;
//                }else{
//                    bluetoothGattList.add(bluetoothDeviceList.get(i).connectGatt(this, false, new BluetoothGattCallback() {
//                        @Override
//                        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//                            super.onCharacteristicWrite(gatt, characteristic, status);
//                        }
//                    }));
//                    Log.d(TAG, "bluetoothDeviceList.get("+i+")Trying to create a new connection.");
//                    bluetoothDeviceAddressList.add(bluetoothDeviceList.get(i).getAddress());
//                    mConnectionStates[i]=STATE_CONNECTING;
//                }
//            }
//        }
//        return true;
/////*
////        // We want to directly connect to the device, so we are setting the autoConnect
////        // parameter to false.
//////        Whether to directly connect to the remote device (false)
//////        or to automatically connect as soon as the remote
//////        device becomes available (true).
////        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
////        Log.d(TAG, "Trying to create a new connection.");
////        mBluetoothDeviceAddress = address;
////        mConnectionState = STATE_CONNECTING;
////        return true;*/
//    }

    /**
     * 检查所有设备地址英文字母是否都为大写
     */
    public boolean checkAddressAll(List<String> addressList) {

//        检查BluetoothAdapter是否初始化，地址列表是否为空
        if (mBluetoothAdapter == null || addressList.size() == 0) {
            Log.w(TAG, "BluetoothAdapter not initialized  or addressList.size()==0");
            return false;
        }
//        检查所有设备地址英文字母是否都为大写
        for (int i = 0; i < addressList.size(); i++) {
            if (!BluetoothAdapter.checkBluetoothAddress(addressList.get(i))) {
                return false;
            }
        }
        return true;
    }
    public void initBleServiceThreePrivateFunction(List<String> bluetoothDeviceAddressList){

        maxDeviceNum=initBluetoothDeviceAddressList(bluetoothDeviceAddressList);//顺序1，初始化服务的私有变量bluetoothDeviceAddressList
        initActionString(maxDeviceNum);//顺序2，初始化广播的动作常量
        initGattCallbackList(maxDeviceNum);//顺序3，初始化BluetoothGattCallback回调接口
    }

    /**
     * 初始化bluetoothDeviceAddressList，并返回列表大小
     * 需在connect(final String address,int indexOfDevice)之前调用
     */
    private int initBluetoothDeviceAddressList(List<String> bluetoothDeviceAddressList){
        this.bluetoothDeviceAddressList=bluetoothDeviceAddressList;
        return bluetoothDeviceAddressList.size();
    }
    /**
     * 根据bluetoothDeviceAddressList大小初始化广播变量
     * 在initBluetoothDeviceAddressList得到了maxDeviceNum；
     */
    private void initActionString(int maxDeviceNum){
        ACTION_GATT_CONNECTED_ARRAY=new String[maxDeviceNum];
        ACTION_GATT_DISCONNECTED_ARRAY=new String[maxDeviceNum];
        ACTION_GATT_SERVICES_DISCOVERED_ARRAY=new String[maxDeviceNum];
        ACTION_DATA_AVAILABLE_ARRAY=new String[maxDeviceNum][MUSCLE_CHARACTERISTIC_NUM];
        ACTION_DATA_WRITE_SUCCESS_ARRAY=new String[maxDeviceNum][MUSCLE_CHARACTERISTIC_NUM];
        EXTRA_DATA_ARRAY=new String[maxDeviceNum][MUSCLE_CHARACTERISTIC_NUM];

        for (int i = 0; i < maxDeviceNum; i++) {
            ACTION_GATT_CONNECTED_ARRAY[i]= "com.example.bluetooth.le.ACTION_GATT_CONNECTED_ARRAY["+i+"]";
            ACTION_GATT_DISCONNECTED_ARRAY[i]="com.example.bluetooth.le.ACTION_GATT_DISCONNECTED_ARRAY["+i+"]";
            ACTION_GATT_SERVICES_DISCOVERED_ARRAY[i]="com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED_ARRAY["+i+"]";
        }
        for (int i = 0; i < maxDeviceNum; i++) {
            for (int j = 0; j < MUSCLE_CHARACTERISTIC_NUM; j++) {
                ACTION_DATA_AVAILABLE_ARRAY[i][j]="com.example.bluetooth.le.ACTION_DATA_AVAILABLE_ARRAY["+i+"]";
                ACTION_DATA_WRITE_SUCCESS_ARRAY[i][j]="com.example.bluetooth.le.ACTION_DATA_WRITE_SUCCESS_ARRAY["+i+"]["+j+"]";
                EXTRA_DATA_ARRAY[i][j]="com.example.bluetooth.le.EXTRA_DATA_ARRAY["+i+"]["+j+"]";

            }
        }
    }
    /**
     * 根据传过来的deviceAddressList大小，创建对应数目的BluetoothGattCallback对象
     * 在initBluetoothDeviceAddressList之后调用
     */
    private void initGattCallbackList(int maxDeviceNum){
        for (int i = 0; i < maxDeviceNum; i++) {
            Log.d(TAG, "initGattCallbackList: bluetoothDeviceAddressList.size()="+bluetoothDeviceAddressList.size());
//            bluetoothGattCallbacks=new BluetoothGattCallback[bluetoothDeviceAddressList.size()];
            final int finalI = i;
            BluetoothGattCallback bluetoothGattCallback=new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);
                    String intentAction;
                    if (newState == BluetoothProfile.STATE_CONNECTED) {//连接成功
                        intentAction = ACTION_GATT_CONNECTED_ARRAY[finalI];
                        mConnectionStates[finalI] = STATE_CONNECTED;
                        broadcastUpdate(intentAction);
                        Log.i(TAG, "Connected to GATT server index="+ finalI);
                        bluetoothGattList.get(finalI).discoverServices();//Gatt发现服务mBluetoothGatt.discoverServices());
                        Log.i(TAG, "Attempting to start service discovery:index="+finalI );
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        intentAction = ACTION_GATT_DISCONNECTED_ARRAY[finalI];
                        mConnectionStates[finalI] = STATE_DISCONNECTED;
                        Log.i(TAG, "Disconnected from GATT server.index="+finalI);
                        broadcastUpdate(intentAction);
                    }
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED_ARRAY[finalI]);
                    } else {
                        Log.w(TAG, "BluetoothDevice"+finalI+"onServicesDiscovered received: " + status);
                    }
                }

                @Override
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicRead(gatt, characteristic, status);
                    for (int j = 0; j < uuidList.size(); j++) {
                        if(characteristic.getUuid().equals(uuidList.get(j))){
                            if(status==BluetoothGatt.GATT_SUCCESS){
                                broadcastUpdate(ACTION_DATA_AVAILABLE_ARRAY[finalI][j], characteristic,finalI,j);
                            }
                        }
                    }
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicWrite(gatt, characteristic, status);
                    // TODO: 2/20/2019  具体到每一个gattserver的每一个characteristic，需要用一个二维数组
                    for (int j = 0; j < uuidList.size(); j++) {
                        if(characteristic.getUuid().equals(uuidList.get(j))){
                            if(status==BluetoothGatt.GATT_SUCCESS){
                                broadcastUpdate(ACTION_DATA_WRITE_SUCCESS_ARRAY[finalI][j], characteristic,finalI,j);
                            }
                        }
                    }
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    super.onCharacteristicChanged(gatt, characteristic);
                    for (int j = 0; j < uuidList.size(); j++) {
                        if(characteristic.getUuid().equals(uuidList.get(j))){
                            broadcastUpdate(ACTION_DATA_AVAILABLE_ARRAY[finalI][j], characteristic,finalI,j);
                        }
                    }
                }
            };
            bluetoothGattCallbackList.add(bluetoothGattCallback);
        }
        Log.d(TAG, "initGattCallbackList: bluetoothGattCallbacks.length="+bluetoothGattCallbackList.size());
    }
    /**
     * address 设备地址
     * indexOfDevice 记录设备序号
     *
     * MultiplayerModeTrainingMainActivity需要：
     * 先调用initBluetoothDeviceAddressList(),初始化this.bluetoothDeviceAddressList
     * 再调用initGattCallbackList();//在initBluetoothDeviceAddressList之后调用
     * 最后调用connect(final String address,int indexOfDevice)
     */
    public boolean connect(final String address,int indexOfDevice) {

        Log.d(TAG, "connect: "+"address="+address+"indexOfDevice="+indexOfDevice);

        if(!checkAddressAll(bluetoothDeviceAddressList)){
            return false;
        }
        // Previously connected device.  Try to reconnect.
//        以前连接过设备，尝试重连
        Log.d(TAG, "connect: bluetoothDeviceAddressList.get(indexOfDevice)"+bluetoothDeviceAddressList.get(indexOfDevice));
        if (bluetoothDeviceAddressList.get(indexOfDevice) != null && address.equals(bluetoothDeviceAddressList.get(indexOfDevice))
                && bluetoothGattList.size() > indexOfDevice && bluetoothGattList.get(indexOfDevice) != null){
//            为什么用bluetoothGattList.size() > indexOfDevice && bluetoothGattList.get(indexOfDevice) != null来判断
//            加入indexOfDevice=0，第一次连接时bluetoothGattList.get(indexOfDevice)并不存在，bluetoothGattList.size()=0，indexOfDevice=0
//            用bluetoothGattList.size() > indexOfDevice判断为false，就不会再判断 bluetoothGattList.get(indexOfDevice) != null
//            如果不加bluetoothGattList.size() > indexOfDevice，直接判断bluetoothGattList.get(indexOfDevice)!= null会报错，因为不存在下标为indexOfDevice的对象
            Log.d(TAG, "Trying to use an existing bluetoothGattList.get("+ indexOfDevice +")for connection.");
            if(bluetoothGattList.get(indexOfDevice).connect()){
                mConnectionStates[indexOfDevice]=STATE_CONNECTING;
                Log.d(TAG, "BluetoothDevice "+indexOfDevice+" STATE_CONNECTING ");
//                return true://return true可能会失败
            }else {
                return false;
            }
        }
//        把顺序为indexOfDevice的设备添加到列表bluetoothDeviceList
        final BluetoothDevice bluetoothDevice=mBluetoothAdapter.getRemoteDevice(address);
//        bluetoothDeviceList.set(indexOfDevice,bluetoothDevice);
        bluetoothDeviceList.add(bluetoothDevice);

        if(bluetoothDevice==null){
            Log.w(TAG, "bluetoothDevice"+indexOfDevice+"Device not found.  Unable to connect.");
            return false;
        }
        BluetoothGatt bluetoothGatt=bluetoothDevice.connectGatt(this,false,bluetoothGattCallbackList.get(indexOfDevice));
        Log.d(TAG, "bluetoothGatt"+indexOfDevice+"Trying to create a new connection.");
        //        bluetoothGattList.set(indexOfDevice,bluetoothGatt);
        bluetoothGattList.add(bluetoothGatt);
//        bluetoothDeviceAddressList.add(address);/已经用initBluetoothDeviceAddressList()初始化过了，再使用add会出错/
        mConnectionStates[indexOfDevice] = STATE_CONNECTING;
        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    /**
     * 调用mBluetoothGatt.connect()
     * */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");

            if (mBluetoothGatt.connect()) {//调用mBluetoothGatt.connect()

                mConnectionState = STATE_CONNECTING;
                Log.d(TAG, "STATE_CONNECTING.");
                //如果断开重连失败的话。把这儿注释掉,好像mBluetoothGatt.connect()返回值就算为true，重连接也一直失败
                //因为如果mBluetoothGatt.connect()返回值为false，就进入了return false，
//                这儿不能返回true，不知道为什么，mBluetoothGatt.connect()的值为true才进入的该方法，
//                却实际上未连接上，我这儿跳出了这个返回，进入到后面的mBluetoothAdapter.getRemoteDevice(address);
//               可能原因： 多次扫描蓝牙，在华为荣耀，魅族M3 NOTE 中有的机型，会发现多次断开–扫描–断开–扫描… 会扫描不到设备，
//                          此时需要在断开连接后，不能立即扫描，而是要先停止扫描后，过2秒再扫描才能扫描到设备。
//                return true;
            } else {
                return false;
            }
        }
        //检查MAC地址是否全部为大写
        if(!BluetoothAdapter.checkBluetoothAddress(address) ){
            return false;
        }
        /*
         * 被去掉了final修饰符
         * */
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
//        Whether to directly connect to the remote device (false)
//        or to automatically connect as soon as the remote
//        device becomes available (true).
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    /**
     * 这是官方源码中关闭一个BluetoothGatt的方法
     * 源码原来只有一个BluetoothGatt，管理一个设备
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }
    /**
     * 根据设备序号关闭对应的GattCharacteristic
     */
    public void disconnect(int indexOfDevice){
        if (mBluetoothAdapter == null || bluetoothGattList.get(indexOfDevice)== null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bluetoothGattList.get(indexOfDevice).disconnect();
        Log.d(TAG, "disconnect:"+indexOfDevice);
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }
    public void close(int indexOfDevice){
        Log.d(TAG, "close: indexOfDevice="+indexOfDevice);
        if (bluetoothGattList.get(indexOfDevice) == null) {
            return;
        }
        bluetoothGattList.get(indexOfDevice).close();
        bluetoothGattList.set(indexOfDevice,null);
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "When readCharacteristic BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic,int indexOfDevice) {
        if (mBluetoothAdapter == null || bluetoothGattList.get(indexOfDevice) == null) {
            Log.w(TAG, "When device="+indexOfDevice+"readCharacteristic BluetoothAdapter not initialized");
            return;
        }
        bluetoothGattList.get(indexOfDevice).readCharacteristic(characteristic);
    }

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic){
        if(mBluetoothAdapter==null||mBluetoothGatt==null){
            Log.w(TAG,"When writeCharacteristic BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.writeCharacteristic(characteristic);
    }
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic,int indexOfDevice) {
        if(mBluetoothAdapter==null||bluetoothGattList.get(indexOfDevice) == null){
            Log.w(TAG,"When device="+indexOfDevice+"writeCharacteristic BluetoothAdapter not initialized");
            return;
        }
        bluetoothGattList.get(indexOfDevice).writeCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Heart Rate Measurement.
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,int indexOfDevice,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || bluetoothGattList.get(indexOfDevice) == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bluetoothGattList.get(indexOfDevice).setCharacteristicNotification(characteristic, enabled);

    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
    public List<BluetoothGattService> getSupportedGattServices(int indexOfDevice) {
        if (bluetoothGattList.get(indexOfDevice)  == null) return null;

        return bluetoothGattList.get(indexOfDevice) .getServices();
    }
}
