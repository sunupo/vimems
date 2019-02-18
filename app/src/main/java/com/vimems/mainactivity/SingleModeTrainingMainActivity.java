package com.vimems.mainactivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.wifi.aware.Characteristics;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.vimems.Adapter. TrainingMainGattDeviceAdapter;
import com.vimems.Ble.AppBluetoothGatt.BluetoothLeService;
import com.vimems.Ble.AppBluetoothGatt.DeviceControlActivity;
import com.vimems.Ble.AppBluetoothGatt.SampleGattAttributes;
import com.vimems.Ble.DeviceBluetoothGattServer.ParaProfile;
import com.vimems.R;
import com.vimems.bean.CustomMusclePara;
import com.vimems.coach.MemberDetailActivity;

import org.litepal.LitePal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import util.BaseActivity;

import static com.vimems.Ble.AppBluetoothGatt.BluetoothLeService.Write_Characteristic_Callback_Success;
import static com.vimems.coach.CustomTrainingItemFragment.CUSTOM_MUSCLE_PARA_LIST;
import static com.vimems.coach.CustomTrainingItemFragment.checkBoxIntegerMap;
import static com.vimems.coach.CustomTrainingItemFragment.deviceState;
import static java.lang.Thread.sleep;
import static util.Constants.EXTRAS_DEVICE_ADDRESS;
import static util.Constants.EXTRAS_DEVICE_NAME;
import static util.Constants.REQUEST_ENABLE_BT;
import static util.Constants.SCAN_PERIOD;

/**
 *  onCreate()   中通过
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

public class SingleModeTrainingMainActivity extends BaseActivity {
    private final static String TAG = SingleModeTrainingMainActivity.class.getSimpleName();

    private BluetoothAdapter mBluetoothAdapter;
    private TextView mConnectionState;
    private TextView mDataField;

    private String mDeviceName;
    public static String mDeviceAddress;//暴露给 TrainingMainGattDeviceAdapter，点击绑定的时候，更改mac地址

    private ExpandableListView mGattServicesList;

    private BluetoothLeService mBluetoothLeService;//自定义service

    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<>();

    private boolean mConnected = false;

    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private Bundle muscleParaBundle;
    private static List<CustomMusclePara> customMuscleParaList;
    private Handler handler;
    private final String FINAL_CHARACTERISTIC="FINAL_CHARACTERISTIC";
    private final String FINAL_K="FINAL_K";

    public static RecyclerView scanResultDeviceRecyclerView;
    private LinearLayoutManager recyclerViewlinearLayoutManager;
    private  TrainingMainGattDeviceAdapter gattDeviceAdapter;
    private ArrayList<BluetoothDevice> bluetoothDeviceArrayList=new ArrayList<>();
    private static boolean mScanning;
    private Handler mHandler;
    public static TextView trainingPageDeviceName;
    public static TextView defaultDeviceAddress;
    private Button scanDeviceTrigger;
    private final String STARTUP_SCAN="扫描";
    private final String SHUTDOWN_SCAN="停止";
    private RadioGroup connectStateRadioGroup;


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
//            deviceState.check(R.id.bind_member_device_connect_state);

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
                //状态显示为已连接
                connectStateRadioGroup.check(R.id.device_state_connected);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                //状态显示为未连接
                connectStateRadioGroup.check(R.id.device_state_disconnected);
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());

                setMuscleParaCharacteristics();

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA).toString());
                Toast.makeText(SingleModeTrainingMainActivity.this,"read is success",Toast.LENGTH_SHORT).show();
            }else if (BluetoothLeService.ACTION_DATA_WRITE_SUCCESS.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA).toString() );
                Toast.makeText(SingleModeTrainingMainActivity.this,"write is success",Toast.LENGTH_SHORT).show();
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
                                    byte[] b=new byte[11];
                                    b[0]=0;b[1]=2;b[2]=3;b[3]=8;b[4]=12;b[5]=2;b[6]=3;b[7]=8;
                                    b[8]=12;b[9]=2;b[10]=3;
                                    BluetoothGattCharacteristic temp;
                                    temp=characteristic;
                                    temp.setValue(b);
                                    mBluetoothLeService.writeCharacteristic(temp);
                                    Toast.makeText(SingleModeTrainingMainActivity.this,"b[0]="+b[0]+"\n"+"b[1]="+b[1]+"\n"
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

    /*
    *
    * */
    private void handlerHandleMessageRun(final BluetoothGattCharacteristic finalBluetoothGattCharacteristic,
                                         final int finalK){

//        while(!Write_Characteristic_Callback_Success){}//上一次回调是否成功，不成功继续等待
//        Write_Characteristic_Callback_Success=false;
        mBluetoothLeService.writeCharacteristic(finalBluetoothGattCharacteristic);
        Log.d(TAG+"        writeMuscleCharacteristic   ", ""+customMuscleParaList.get(finalK).getMuscleID());
//        用延时函数代替
        try {
            sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_mode_training_main);

        //通过BluetoothManager初始化BluetoothAdapter
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        scanResultDeviceRecyclerView = findViewById(R.id.single_mode_training_main_activity_device_recycler_view);
        recyclerViewlinearLayoutManager = new LinearLayoutManager(this);
        scanResultDeviceRecyclerView.setLayoutManager(recyclerViewlinearLayoutManager);
        gattDeviceAdapter = new TrainingMainGattDeviceAdapter(bluetoothDeviceArrayList);
        scanResultDeviceRecyclerView.setAdapter(gattDeviceAdapter);

        defaultDeviceAddress=findViewById(R.id.bind_member_default_device_address);
        trainingPageDeviceName= findViewById(R.id.bind_member_custom_device_name);
        scanDeviceTrigger=findViewById(R.id.scan_bluetooth_trigger);
        connectStateRadioGroup=findViewById(R.id.bind_member_device_connect_state);
        scanDeviceTrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (scanDeviceTrigger.getText().toString()){
                    case STARTUP_SCAN:
                        bluetoothDeviceArrayList.clear();
                        scanDeviceTrigger.setText(SHUTDOWN_SCAN);
                        scanLeDevice(true);
                        break;
                    case SHUTDOWN_SCAN:
                        scanDeviceTrigger.setText(STARTUP_SCAN);
                        scanLeDevice(false);
                        break;
                }
            }
        });

        mHandler=new Handler();

        scanLeDevice(true);

//        handler=new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                Bundle bundle;
//                bundle=msg.getData();
//                final BluetoothGattCharacteristic finalBluetoothGattCharacteristic=bundle.getParcelable(FINAL_CHARACTERISTIC);
//                final int finalK=bundle.getInt(FINAL_K);
//                switch (msg.what){
//                    case 1:
//                        Thread t1=new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                handlerHandleMessageRun(finalBluetoothGattCharacteristic,finalK);
//                            }
//                        });
//                        t1.start();
//                        try {
//                            t1.join();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        break;
//                    case 2:
//                        Thread t2=new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                handlerHandleMessageRun(finalBluetoothGattCharacteristic,finalK);
//                            }
//                        });
//                        t2.start();
//                        try {
//                            t2.join();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        break;
//
//                        default:
//                            break;
//                }
//            }
//        };
//        Toast.makeText(SingleModeTrainingMainActivity.this,"99",Toast.LENGTH_SHORT);
        final Intent intent = getIntent();
//        Set<String> intentCategoriesSet=intent.getCategories();
//        if(intentCategoriesSet.contains(CUSTOM_TRAINING_ITEM_FRAGMENT_INTENT_CATEGORY)){
//            mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
//            mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
//
//            // TODO: 2/14/2019 获取参数列表的bundle
//            //获取从MemberDetailActivity的CustomTrainingItemFragment（设置参数的Fragment）传递过来的训练时肌肉参数
//            muscleParaBundle=intent.getExtras();
//            customMuscleParaList= (List<CustomMusclePara>) muscleParaBundle.getSerializable(CUSTOM_MUSCLE_PARA_LIST);
//        }else{
//            mDeviceName=deviceDefaultCustomNameMap.get(memberIDDeviceMap.get(intent.getIntExtra(MEMBER_ID,1)));
//            mDeviceAddress=memberIDDeviceMap.get(intent.getIntExtra(MEMBER_ID,1)).getAddress();
////            customMuscleParaList=LitePal.where().find(CustomMusclePara.class);
//        }
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // TODO: 2/14/2019 获取参数列表的bundle
        //获取从MemberDetailActivity的CustomTrainingItemFragment（设置参数的Fragment）传递过来的训练时肌肉参数
        muscleParaBundle=intent.getExtras();
        customMuscleParaList= (List<CustomMusclePara>) muscleParaBundle.getSerializable(CUSTOM_MUSCLE_PARA_LIST);


        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        trainingPageDeviceName.setText(mDeviceName);
        defaultDeviceAddress.setText(mDeviceAddress);
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

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    if (bluetoothDeviceArrayList.size()!=0){
//                        customDeviceName.setText(bluetoothDeviceArrayList.get(0).getName());
//                        deviceAddress.setText(bluetoothDeviceArrayList.get(0).getAddress());
                        Toast.makeText(SingleModeTrainingMainActivity.this,"扫描结束！"+"\n"
                                +"设备数目="+bluetoothDeviceArrayList.size(),Toast.LENGTH_SHORT).show();
                    }
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();//optionMenu需要被重新create
    }
    BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!bluetoothDeviceArrayList.contains(device)) {
                                bluetoothDeviceArrayList.add(device);
                                gattDeviceAdapter.notifyDataSetChanged();//如果适配器的内容改变时需要刷新每个Item的内容。
                                scanResultDeviceRecyclerView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            };



    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }

        //确保蓝牙已打开，否则弹出警示框dialog询问用户是否开启蓝牙；
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 如果用户选择取消启动蓝牙
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);

        scanLeDevice(false);

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


    /*
     * 在切换连接断开状态时，几秒过后再点击连接会出现无法连接情况
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                if(!mBluetoothLeService.connect(mDeviceAddress)){
                    if (!mBluetoothLeService.initialize()) {
                        Log.e(TAG, "Unable to initialize Bluetooth");
                        finish();
                    }
                    if(!mBluetoothLeService.connect(mDeviceAddress)){
                        Log.e(TAG, "重连失败");
                    }
                }
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
    private static Message[] messages=new Message[10];
    static {
        for (int i = 0; i < messages.length; i++) {
            messages[i]=new Message();
        }
    }
    private void putHandlerMessage(final BluetoothGattCharacteristic finalBluetoothGattCharacteristic,
                                   final int finalK,final int muscleID){

        messages[muscleID-1]=new Message();
        messages[muscleID-1].what=muscleID;
        Bundle bundle=new Bundle();
        bundle.putParcelable("FINAL_CHARACTERISTIC",finalBluetoothGattCharacteristic);
        bundle.putInt("FINAL_K",finalK);
        messages[muscleID-1].setData(bundle);
        handler.sendMessage(messages[muscleID-1]);

    }
    public void setMuscleParaCharacteristics(){

        Thread t1,t2,t3,t4,t5,t6,t7,t8,t9,t10;
        Set<Thread> threadSet=new HashSet<>();
        BluetoothGattCharacteristic bluetoothGattCharacteristic;
        CustomMusclePara customMusclePara;
        byte[] b;
        for (int i = 0; i < mGattCharacteristics.size(); i++) {
            for (int j = 0; j < mGattCharacteristics.get(i).size(); j++) {
                bluetoothGattCharacteristic=mGattCharacteristics.get(i).get(j);
                // TODO: 2/14/2019写入13个characteristic 逻辑好像不对
                for(int k=0;k<customMuscleParaList.size();k++){
                    customMusclePara=customMuscleParaList.get(k);
                    b=setConcreteMusclePara(customMusclePara);
                    switch (customMuscleParaList.get(k).getMuscleID()){
                        case 1:
                            if(bluetoothGattCharacteristic.getUuid().equals(ParaProfile.MUSCLE1_CHARACTERISTIC)){
                                if(b.length>0){
                                    bluetoothGattCharacteristic.setValue(b);
                                    final BluetoothGattCharacteristic finalBluetoothGattCharacteristic = bluetoothGattCharacteristic;
                                    final int finalK = k;
                                    t1 =new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("t1", "is running: ");
                                            handlerHandleMessageRun(finalBluetoothGattCharacteristic,finalK);
                                            Log.d("t1", "is  over ");
                                        }
                                    });
                                    threadSet.add(t1);
//                                    t1.start();
//                                    try {
//                                        t1.join();
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                        Log.d("join() exception", "1");
//                                    }
                                }
                            }
                            break;
                        case 2:
                            if(bluetoothGattCharacteristic.getUuid().equals(ParaProfile.MUSCLE2_CHARACTERISTIC)){
                                 if(b.length>0){
                                    bluetoothGattCharacteristic.setValue(b);
                                    final BluetoothGattCharacteristic finalBluetoothGattCharacteristic = bluetoothGattCharacteristic;
                                    final int finalK = k;
                                    t2=new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("t2", "is running: ");
                                            handlerHandleMessageRun(finalBluetoothGattCharacteristic,finalK);
                                            Log.d("t2", "is over: ");

                                        }
                                    });
                                     threadSet.add(t2);

//                                    t2.start();
//                                    try {
//                                        t2.join();
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                        Log.d("join() exception", "2");
//                                    }
                                }
                            }
                            break;
                        case 3:
                            if(bluetoothGattCharacteristic.getUuid().equals(ParaProfile.MUSCLE3_CHARACTERISTIC)){
                                if(b.length>0){
                                    bluetoothGattCharacteristic.setValue(b);
                                    final BluetoothGattCharacteristic finalBluetoothGattCharacteristic = bluetoothGattCharacteristic;
                                    final int finalK = k;

                                    t3=new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("t3", "is running: ");
                                            handlerHandleMessageRun(finalBluetoothGattCharacteristic,finalK);
                                            Log.d("t3", "is over: ");
                                        }
                                    });
                                    threadSet.add(t3);
//                                    t3.start();
//                                    try {
//                                        t3.join();
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                        Log.d("join() exception", "3");
//                                    }
                                }
                            }
                            break;
                        case 4:
                            if(bluetoothGattCharacteristic.getUuid().equals(ParaProfile.MUSCLE4_CHARACTERISTIC)){
                                if(b.length>0){
                                    bluetoothGattCharacteristic.setValue(b);
                                    final BluetoothGattCharacteristic finalBluetoothGattCharacteristic = bluetoothGattCharacteristic;
                                    final int finalK = k;

                                    t4=new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("t4", "is running: ");
                                            handlerHandleMessageRun(finalBluetoothGattCharacteristic,finalK);
                                            Log.d("t4", "is over: ");
                                        }
                                    });
                                    threadSet.add(t4);
//                                    t4.setPriority(4);
//                                    t4.start();
//                                    try {
//                                        t4.join();
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                        Log.d("join() exception", "4");
//                                    }
                                }
                            }
                            break;
                        case 5:
                            if(bluetoothGattCharacteristic.getUuid().equals(ParaProfile.MUSCLE5_CHARACTERISTIC)){
                                if(b.length>0){
                                    bluetoothGattCharacteristic.setValue(b);
                                    final BluetoothGattCharacteristic finalBluetoothGattCharacteristic = bluetoothGattCharacteristic;
                                    final int finalK = k;

                                    t5=new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("t5", "is running: ");
                                            handlerHandleMessageRun(finalBluetoothGattCharacteristic,finalK);
                                            Log.d("t5", "is over: ");
                                        }
                                    });
                                    threadSet.add(t5);
//                                    t5.setPriority(5);
//                                    t5.start();
//                                    try {
//                                        t5.join();
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                        Log.d("join() exception", "5");
//                                    }
                                }
                            }
                            break;
                        case 6:
                            if(bluetoothGattCharacteristic.getUuid().equals(ParaProfile.MUSCLE6_CHARACTERISTIC)){
                                if(b.length>0){
                                    bluetoothGattCharacteristic.setValue(b);
                                    final BluetoothGattCharacteristic finalBluetoothGattCharacteristic = bluetoothGattCharacteristic;
                                    final int finalK = k;

                                    t6=new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("t6", "is running: ");
                                            handlerHandleMessageRun(finalBluetoothGattCharacteristic,finalK);
                                            Log.d("t6", "is over: ");
                                        }
                                    });
                                    threadSet.add(t6);
//                                    t6.setPriority(6);
//                                    t6.start();
//                                    try {
//                                        t6.join();
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                        Log.d("join() exception", "6");
//                                    }
                                }
                            }
                            break;
                        case 7:
                            if(bluetoothGattCharacteristic.getUuid().equals(ParaProfile.MUSCLE7_CHARACTERISTIC)){
                                if(b.length>0){
                                    bluetoothGattCharacteristic.setValue(b);
                                    final BluetoothGattCharacteristic finalBluetoothGattCharacteristic = bluetoothGattCharacteristic;
                                    final int finalK = k;

                                    t7=new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("t7", "is running: ");
                                            handlerHandleMessageRun(finalBluetoothGattCharacteristic,finalK);
                                            Log.d("t7", "is over: ");
                                        }
                                    });
                                    threadSet.add(t7);
//                                    t7.setPriority(7);
//                                    t7.start();
//                                    try {
//                                        t7.join();
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                        Log.d("join() exception", "7");
//                                    }
                                }
                            }
                            break;
                        case 8:
                            if(bluetoothGattCharacteristic.getUuid().equals(ParaProfile.MUSCLE8_CHARACTERISTIC)){
                                if(b.length>0){
                                    bluetoothGattCharacteristic.setValue(b);
                                    final BluetoothGattCharacteristic finalBluetoothGattCharacteristic = bluetoothGattCharacteristic;
                                    final int finalK = k;
                                    t8=new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("t8", "is running: ");
                                            handlerHandleMessageRun(finalBluetoothGattCharacteristic,finalK);
                                            Log.d("t8", "is over: ");
                                        }
                                    });
                                    threadSet.add(t8);
//                                    t8.setPriority(8);
//                                    t8.start();
//                                    try {
//                                        t8.join();
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                        Log.d("join() exception", "8");
//                                    }
                                }
                            }
                            break;
                        case 9:
                            if(bluetoothGattCharacteristic.getUuid().equals(ParaProfile.MUSCLE9_CHARACTERISTIC)){
                                if(b.length>0){
                                    bluetoothGattCharacteristic.setValue(b);
                                    final BluetoothGattCharacteristic finalBluetoothGattCharacteristic = bluetoothGattCharacteristic;
                                    final int finalK = k;

                                    t9=new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("t9", "is running: ");
                                            handlerHandleMessageRun(finalBluetoothGattCharacteristic,finalK);
                                            Log.d("t9", "is over: ");
                                        }
                                    });
                                    threadSet.add(t9);
//                                    t9.setPriority(9);
//                                    t9.start();
//                                    try {
//                                        t9.join();
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                        Log.d("join() exception", "9");
//                                    }
                                }
                            }
                            break;
                        case 10:
                            if(bluetoothGattCharacteristic.getUuid().equals(ParaProfile.MUSCLE10_CHARACTERISTIC)){
                                if(b.length>0){
                                    bluetoothGattCharacteristic.setValue(b);
                                    final BluetoothGattCharacteristic finalBluetoothGattCharacteristic = bluetoothGattCharacteristic;
                                    final int finalK = k;
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                    putHandlerMessage(finalBluetoothGattCharacteristic,finalK,10);
//                                        }
//                                    });
                                    t10=new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("t10", "is running: ");
                                            handlerHandleMessageRun(finalBluetoothGattCharacteristic,finalK);
                                            Log.d("t10", "is over: ");
                                        }
                                    });
                                    threadSet.add(t10);
//                                    t10.setPriority(10);
//                                    t10.start();
//                                    try {
//                                        t10.join();
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                        Log.d("join() exception", "10");
//                                    }
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        Log.d("threadSet.size()", "= "+threadSet.size());

        Iterator<Thread> iterator=threadSet.iterator();
        Thread tempThread;
        while(iterator.hasNext()){

            tempThread=iterator.next();
            Log.d("tempThread.start()", ""+tempThread.getName());
            /*
            * 这一段特别重要，必须先start在join，顺序执行
            * */
            tempThread.start();

            try {
                tempThread.join();
                Log.d("tempThread.join()", ""+tempThread.getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
    /*
    * 给每一块肌肉对应的characteristic写入数据
    * */
    private byte[] setConcreteMusclePara(CustomMusclePara customMusclePara){

        byte[] b=new byte[13];

        b[0]= (byte) customMusclePara.getMuscleID();
        b[1]= (byte) customMusclePara.getMemberID();
        b[2]=(byte)customMusclePara.getTrainingModeCode();
        b[3]=(byte)customMusclePara.getTrainingModuleCode();
        b[4]=(byte)customMusclePara.getTrainingModuleLevel();
        //low 0-150, high 2000-10000
        b[5]=(byte)(customMusclePara.getLowFrequency()%100);//lowFrequency 十位数和个位数
        b[6]=(byte)(customMusclePara.getLowFrequency()/100);//lowFrequency 百位数0或者1
        b[7]=(byte)(customMusclePara.getHighFrequency()%100);//highFrequency 十位数和个位数
        b[8]=(byte)(customMusclePara.getHighFrequency()/100);//highFrequency 高位

        b[9]=(byte)customMusclePara.getPulseWidth();
        b[10]=(byte)customMusclePara.getPulsePeriod();
        b[11]=(byte)customMusclePara.getIntermittentPeriod();
        b[12]=(byte)customMusclePara.getIntensity();
        return b;

    }

}

