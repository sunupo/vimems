package com.vimems.Ble.AppBluetoothGatt;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.aware.Characteristics;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.vimems.Ble.DeviceBluetoothGattServer.ParaProfile;
import com.vimems.R;
import com.vimems.bean.CustomMusclePara;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import util.BaseActivity;

import static com.vimems.coach.CustomTrainingItemFragment.CUSTOM_MUSCLE_PARA_LIST;
import static com.vimems.coach.CustomTrainingItemFragment.deviceState;
import static util.Constants.EXTRAS_DEVICE_ADDRESS;
import static util.Constants.EXTRAS_DEVICE_NAME;


/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
/**
 * author sunupo
 * date 2019/1/24 16:49
 * description
 *
 */

/**
 * DeviceControlActivity的onCreate()中通过
 * Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
 * bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
 * 绑定了服务，进去
 * ServiceConnection的onServiceConnected()方法,得到BluetoothService的句柄mBluetoothLeService
 * mBluetoothLeService调用BluetoothService的connect(mDeviceAddress)方法
 * 在connect中，根据address得到BluetoothDevice对象device
 * 通过mBluetoothGatt = device.connectGatt(this, false, mGattCallback);得到BluetoothGatt对象mBluetoothGatt
 * mBluetoothGatt的回调接口mGattCallback
 * 首先进入GattCallback.onConnectionStateChange()方法，
 *      A、连接成功执行mBluetoothGatt.discoverServices())，该类的广播接收器收到广播，执行相应操作
 *      B、失败执行broadcastUpdate(intentAction); intentAction = ACTION_GATT_DISCONNECTED;
 *假设连接成功，进入GattCallback.onServicesDiscovered()方法,
 *      A、未发现服务，可以再日志中输出调试信息
 *      B、发现存在服务，执行broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);该类的广播接收器收到广播，
 *      执行displayGattServices(mBluetoothLeService.getSupportedGattServices());把结果显示在列表中
 */
/**
 * 在Activity里面通过sevice对象，调用writeCharacteristic方法，mBluetoothLeService.writeCharateristic(temp);
 * 在service里面，真正的调用Gatt对象的writeCharateristic方法，
 * 在service中有个回调函数，接受服务端的sendResponse数据，发出一条广播
 * Activity中有一个BroadcastReceiver接受这条广播
 *
 */

public class GattPeripheralDetailActivity extends BaseActivity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    private TextView mConnectionState;
    private TextView mDataField;

    private String mDeviceName;
    private String mDeviceAddress;

    private ExpandableListView mGattServicesList;

    private BluetoothLeService mBluetoothLeService;//自定义service

    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

    private boolean mConnected = false;

    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private Bundle muscleParaBundle;
    private static List<CustomMusclePara> customMuscleParaList;

    // Code to manage Service lifecycle.
    //bindService是在onCreate中调用的，随即mServiceConnection的onServiceConnected()方法被调用
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
            deviceState.check(R.id.bind_member_device_connect_state);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
                setMuscleParaCharacteristics();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA).toString());
                Toast.makeText(GattPeripheralDetailActivity.this,"read is success",Toast.LENGTH_SHORT).show();
            }else if (BluetoothLeService.ACTION_DATA_WRITE_SUCCESS.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA).toString() );
                Toast.makeText(GattPeripheralDetailActivity.this,"write is success",Toast.LENGTH_SHORT).show();
            }
        }
    };

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.

    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {

                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);

                        if(characteristic.getUuid().equals(ParaProfile.MUSCLE1_CHARACTERISTIC)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    byte[] b=new byte[4];
                                    b[0]=12;b[1]=2;b[2]=3;b[3]=8;
                                    BluetoothGattCharacteristic temp;
                                    temp=characteristic;
                                    temp.setValue(b);
                                    mBluetoothLeService.writeCharacteristic(temp);
                                    Toast.makeText(GattPeripheralDetailActivity.this,"b[0]="+b[0]+"\n"+"b[1]="+b[1]+"\n"
                                            +"b[2]="+b[2]+"\n"+"b[3]="+b[3]+"\n"+"",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mBluetoothLeService.readCharacteristic(characteristic);
                                }
                            });

                        }
                        /*if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(
                                    characteristic, true);
                        }*/

                        return true;
                    }
                    return false;
                }
            };

    private void clearUI() {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        Toast.makeText(GattPeripheralDetailActivity.this,"99",Toast.LENGTH_SHORT);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // TODO: 2/14/2019 获取参数列表的bundle
        Bundle muscleParaBundle;
        muscleParaBundle=intent.getExtras();
//        customMuscleParaList=new ArrayList<>();
        customMuscleParaList= (List<CustomMusclePara>) muscleParaBundle.getSerializable(CUSTOM_MUSCLE_PARA_LIST);
        

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
        mGattServicesList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                return false;
            }
        });

        mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value);

        //getActionBar().setTitle(mDeviceName);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);

        //new SimpleExpandableListAdapter需要这两个参数gattServiceData、gattCharacteristicData
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        //mGattCharacteristics与gattCharacteristicData大小相同，只是<>类型不一样
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        //得到了多个服务
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            //每个服务对应一个可折叠项，一个可折叠项使用的两个TextView对应的(simple_expandable_list_item_2)
            //一个是服务名字，一个是服务对应的UUID
            gattServiceData.add(currentServiceData);

            //该服务service折叠项对应的二级菜单数据
            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            //该服务service对应的多个Characteristic，gattService.getCharacteristics()返回的就是一个列表
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            //用charas来记录上面的gattCharacteristics对应的一组值
            //并把这一组值charas添加到mGattCharacteristics
            //可能是因为无法直接把List加入add到mGattCharacteristics
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);//用charas来记录上面的gattCharacteristics对应的一组值
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);//并把这一组值charas添加到mGattCharacteristics
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,//LIST_NAME, LIST_UUID在gattServiceData有定义
                android.R.layout.simple_expandable_list_item_2,//android.R.id.text1, android.R.id.text2在这个文件有定义
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mGattServicesList.setAdapter(gattServiceAdapter);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    public void setMuscleParaCharacteristics(){

        BluetoothGattCharacteristic bluetoothGattCharacteristic;
        for (int i = 0; i < mGattCharacteristics.size(); i++) {
            for (int j = 0; j < mGattCharacteristics.get(i).size(); j++) {
                bluetoothGattCharacteristic=mGattCharacteristics.get(i).get(j);
                // TODO: 2/14/2019
                if(bluetoothGattCharacteristic.getUuid().equals(ParaProfile.MUSCLE1_CHARACTERISTIC)){

                    final BluetoothGattCharacteristic finalBluetoothGattCharacteristic = bluetoothGattCharacteristic;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CustomMusclePara customMusclePara;
//                            for (int k = 0; k < customMuscleParaList.size(); k++) {
                                customMusclePara=customMuscleParaList.get(0);
                                byte[] b=new byte[11];
                                b[0]= (byte) customMusclePara.getMuscleID();
                                b[1]= (byte) customMusclePara.getMemberID();
                                b[2]=(byte)customMusclePara.getTrainingModeCode();
                                b[3]=(byte)customMusclePara.getTrainingModuleCode();
                                b[4]=(byte)customMusclePara.getTrainingModuleLevel();
                                b[5]=(byte)customMusclePara.getLowFrequency();
                                b[6]=(byte)customMusclePara.getHighFrequency();
                                b[7]=(byte)customMusclePara.getPulseWidth();
                                b[8]=(byte)customMusclePara.getPulsePeriod();
                                b[9]=(byte)customMusclePara.getIntermittentPeriod();
                                b[10]=(byte)customMusclePara.getIntensity();
                                BluetoothGattCharacteristic temp;
                                temp= finalBluetoothGattCharacteristic;
                                temp.setValue(b);
                                mBluetoothLeService.writeCharacteristic(temp);
                                Toast.makeText(GattPeripheralDetailActivity.this,"b[0]="+b[0]+"\n"+"b[1]="+b[1]+"\n"
                                        +"b[2]="+b[2]+"\n"+"b[3]="+b[3]+"\n"+"b[4]="+b[4]+"\n"+"b[5]="+b[5]+"\n"
                                        +"b[6]="+b[6]+"\n"+"b[7]="+b[7]+"\n"+"b[8]="+b[8]+"\n"+"b[9]="+b[9]+"\n"+"b[10]="+b[10]+"\n"+"",Toast.LENGTH_LONG).show();
//                            }

                        }
                    });
                }
            }
        }
    }
}

