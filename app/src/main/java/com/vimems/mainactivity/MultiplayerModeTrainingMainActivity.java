package com.vimems.mainactivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.vimems.Adapter.MultiplayerGattDeviceAdapter;
import com.vimems.Adapter.MultiplayerMemberAdapter;
import com.vimems.R;
import com.vimems.bean.Coach;
import com.vimems.bean.CustomMusclePara;
import com.vimems.bean.Member;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import util.BaseActivity;
import util.InitBean;

import static util.Constants.COACH_LOGIN_NAME;
import static util.Constants.REQUEST_ENABLE_BT;
import static util.Constants.SCAN_PERIOD;

public class MultiplayerModeTrainingMainActivity extends BaseActivity {

    private final String TAG="MultiplayerModeTrainingMainActivity";

    /*viewpager+tabLayout begin */

    /*viewpager+tabLayout begin */

    private String coachLoginName;
    private Coach coach;
    private ArrayList<Member> coachMemberList=new ArrayList<>();
    private RecyclerView memberRecyclerView;
    private RecyclerView deviceRecyclerView;
    private ArrayList<BluetoothDevice> bluetoothDeviceArrayList=new ArrayList<>();
    private Handler mHandler;
    public static boolean mScanning;
    private BluetoothAdapter mBluetoothAdapter;
    private MultiplayerGattDeviceAdapter gattDeviceAdapter;
    private MultiplayerMemberAdapter memberAdapter;

    private Button connectSelectedDevice;
    private BluetoothLeService mBluetoothLeService;
    private ToggleButton displayDeviceToogle;

    private boolean[] deviceConnectedState;


//    设备全部连接，和全部断开比较好判断绑定服务，解绑服务
// TODO: 2/22/2019    如果是单人选择连接断开，
    public static boolean serviceBindFlag=false;//serviceBindFlag=true，服务已绑定，serviceBindFlag=false，服务未绑定
    public static int selectedBluetoothDeviceSize=0;//已选设备数目大小,用在按下返回键，判断是否需要弹出断开设备提示

    public static List<BluetoothDevice> selectedBluetoothDeviceList=new ArrayList<>();//设备列表
    public static List<String> selectedBluetoothDeviceAddressList=new ArrayList<>();//设备地址列表

    private List<BluetoothGatt> bluetoothGattList=new ArrayList<>();//一个BluetoothGatt结构管理一个device，未使用到该变量，是通过disconnect(i),来间接控制的

    private ArrayList<ArrayList<ArrayList<BluetoothGattCharacteristic>>> allDeviceCharacteristic;//所有设备的characteristic;三维，【设备数，每一个设备的服务数目，每个服务的characteristic数目】

    private Map<Integer,String> multiplayerMemberBindDeviceMap=new HashMap<>();//会员member与绑定设备devicemap

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            serviceBindFlag=true;

            mBluetoothLeService = ((com.vimems.mainactivity.BluetoothLeService.LocalBinder) service).getService();
            Log.d(TAG, "onServiceConnected: mBluetoothLeService初始化成功");
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.initBleServiceThreePrivateFunction(selectedBluetoothDeviceAddressList);
            for (int i = 0; i < selectedBluetoothDeviceAddressList.size(); i++) {
                mBluetoothLeService.connect(selectedBluetoothDeviceAddressList.get(i),i);
            }
            Log.d(TAG, "onServiceConnected: selectedBluetoothDeviceAddressList.size()"
                    +selectedBluetoothDeviceAddressList.size());

//            initBleServiceThreePrivateFunction里面初始化了actionString之后，才能makeGattUpdateIntentFilter
            registerReceiver(bleServiceGattUpdateReceiver,makeGattUpdateIntentFilter());

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            serviceBindFlag=false;

            mBluetoothLeService = null;
            Log.d(TAG, "onServiceDisconnected: unBindService successful");
        }
    };

    private final BroadcastReceiver bleServiceGattUpdateReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            for (int i = 0; i < BluetoothLeService.ACTION_GATT_CONNECTED_ARRAY.length; i++) {
                if(BluetoothLeService.ACTION_GATT_CONNECTED_ARRAY[i].equals(action)){
                    // TODO: 2/21/2019 根据列表大小创建一个大小相同的state数组，记录当前设备连接状态
                    deviceConnectedState[i]=true;
                    // TODO: 2/21/2019 界面上添加显示控件，显示设备已连接
                    setDeviceConnectionState(i,R.string.connected);
                }else if (BluetoothLeService.ACTION_GATT_DISCONNECTED_ARRAY[i].equals(action)){
                    deviceConnectedState[i]=false;
                    // TODO: 2/21/2019 界面上添加显示控件，显示设备已断开连接
                    setDeviceConnectionState(i,R.string.disconnected);
                }else if (BluetoothLeService.ACTION_GATT_DISCONNECTED_ARRAY[i].equals(action)){
                    // TODO: 2/21/2019 得到设备所有的服务和characteristic
                    allDeviceCharacteristic.add(getDeviceGattServicesCharacteristic(i));
                }else if (BluetoothLeService.ACTION_DATA_AVAILABLE_ARRAY[i].equals(action)){
                    // TODO: 2/21/2019 根据情况，决定是否显示 EXTRA_DATA_ARRAY[i][j]
                }
            }
        }
    };


    /**
     * 更新设备i的连接状态
     */
    private void setDeviceConnectionState(int indexOfDevice,int resourceID){

    }
    /**
     * 得到某一个设备的service
     */
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> getDeviceGattServicesCharacteristic(int indexOfDevice){
        List<BluetoothGattService> gattServices=mBluetoothLeService.getSupportedGattServices(indexOfDevice);
        if(gattServices==null){
            return null;
        }
        //记录当前设备所有服务的characteristic，在写入characteristic的时候通过遍历该变量值，从而来写入
        ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics=new ArrayList<>();

        for(BluetoothGattService gattService:gattServices){
            ArrayList<BluetoothGattCharacteristic> gattCharacteristics=
                    (ArrayList<BluetoothGattCharacteristic>) gattService.getCharacteristics();
            mGattCharacteristics.add(gattCharacteristics);
        }

        return mGattCharacteristics;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_mode_training_main);

        allDeviceCharacteristic=new ArrayList<ArrayList<ArrayList<BluetoothGattCharacteristic>>>();

        checkBluetooth();
        Intent intent=getIntent();
        coachLoginName=intent.getStringExtra(COACH_LOGIN_NAME);
        coach= LitePal.where(COACH_LOGIN_NAME+"=?",coachLoginName).find(Coach.class).get(0);

        initRecyclerView();
        connectSelectedDevice=findViewById(R.id.connect_selected_device);
        displayDeviceToogle=findViewById(R.id.display_device_toggle);

        final Intent gattServiceIntent = new Intent(this, com.vimems.mainactivity.BluetoothLeService.class);


        connectSelectedDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedBluetoothDeviceList.size()>2){//控制最大连接数目,应该放在adapter的点击事件来判断
                    Toast.makeText(v.getContext(),"超出最大连接数",Toast.LENGTH_SHORT).show();
                    return;
                }else if(selectedBluetoothDeviceList.size()==0){
                    Toast.makeText(v.getContext(),"请选择设备",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(connectSelectedDevice.getText().toString().equals(getResources().getString(R.string.connect_all_selected_device))){
                    // TODO: 2/21/2019 初始化设备连接状态数组
                    deviceConnectedState=new boolean[selectedBluetoothDeviceList.size()];
                    for (int i = 0; i < selectedBluetoothDeviceList.size(); i++) {
                        deviceConnectedState[i]=false;
                        if(!selectedBluetoothDeviceAddressList.contains(selectedBluetoothDeviceList.get(i).getAddress())){
                            selectedBluetoothDeviceAddressList.add(selectedBluetoothDeviceList.get(i).getAddress());
                            Log.d(TAG, "onClick: selectedBluetoothDeviceList.get("+i+").getAddress()="
                                    +selectedBluetoothDeviceList.get(i).getAddress());
                        }
                    }
                    // TODO: 2/19/2019  绑定服务
                    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
                    connectSelectedDevice.setText(R.string.disconnect_all_selected_device);
                    displayDeviceToogle.setChecked(true);
                }else if(connectSelectedDevice.getText().toString().equals(getResources().getString(R.string.disconnect_all_selected_device))){
                    for (int i = 0; i < selectedBluetoothDeviceAddressList.size(); i++) {
                        mBluetoothLeService.disconnect(i);
                    }
//                    当没有点击断开连接时，也可能由于蓝牙自己的问题断开了连接，然后serviceBindFlag=false;
//                    这种情况下，则是在点击返回键的时候，通过判断serviceBindFlag=false就会解绑服务；
                    unbindService(mServiceConnection);
                    connectSelectedDevice.setText(R.string.connect_all_selected_device);
                }
            }
        });
        displayDeviceToogle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    deviceRecyclerView.setVisibility(View.GONE);
                }else{
                    deviceRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
        mHandler=new Handler();
        scanLeDevice(true);
    }

    /**
     * 断开第i个设备
     * 间接控制BluetoothBleService的bluetoothGattList.get(indexOfDevice).disconnect();
     * 通过每个设备自己的按钮来控制，
     */
    public void disconnect(int i){
        if(mBluetoothLeService==null){
            Log.d(TAG, "disconnect error: mBluetoothLeService==null");
        }
        mBluetoothLeService.disconnect(i);
    }
    @Override
    protected void onResume() {
        super.onResume();
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
        scanLeDevice(false);
        Log.d(TAG, "onPause: "+"scanLeDevice(false);");

        // TODO: 2/22/2019 pause 状态是否清空列表，应该为否
//        selectedBluetoothDeviceList.clear();
//        selectedBluetoothDeviceAddressList.clear();
//        Log.d(TAG, "onPause: "+"selectedBluetoothDeviceList.clear();\n" +
//                "selectedBluetoothDeviceAddressList.clear();\n");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            unregisterReceiver(bleServiceGattUpdateReceiver);

        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "onDestroy: 广播未注册");
        }

        //清空列表，不然下次进入重新添加设备时，列表大小（indexOfDevice的最大值）大于实际添加设备数目大小，onUnBind根据close(indexOfDevice)来循环关闭是会出现错误，
        selectedBluetoothDeviceList.clear();
        selectedBluetoothDeviceAddressList.clear();

//        判断是否绑定过BluetoothLeService，
// 绑定暂时是放在onclick里面，如果未点击onclick就返回上一个界面，就不需要解绑
// onclick绑定过BluetoothLeService，就解绑服务
//        ActivityManager myManager = (ActivityManager) this
//                .getSystemService(Context.ACTIVITY_SERVICE);
//        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
//                .getRunningServices(300);
//        Log.d(TAG, "onDestroy: runningService.size()="+runningService.size());
//        for (int i = 0; i < runningService.size(); i++) {
//            if (runningService.get(i).service.getClassName()
//                    .equals("com.vimems.mainactivity.BluetoothLeService")) {
//                unbindService(mServiceConnection);
//            }
//        }
//        判断是否绑定过BluetoothLeService，
        if(serviceBindFlag){
            unbindService(mServiceConnection);
        }
        mBluetoothLeService = null;
    }

    @Override
    public void onBackPressed() {
        boolean backFlag=true;
        try {
            Thread.sleep(200);//延时，防止当点击了一键连接，设备还没有连接上
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        如果没有添加设备，会显示deviceConnectedState数组为空的错误
//        java.lang.NullPointerException: Attempt to get length of null array
//        if(selectedBluetoothDeviceList!=null){//这样不行，因为没有添加设备并连接，selectedBluetoothDeviceList只是声明了，并未new
//            for (int i = 0; i < deviceConnectedState.length; i++) {
//                if(deviceConnectedState[i]){
//                    backFlag=false;
//                    Toast.makeText(this,"请全部断开设备",Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "onBackPressed: 设备没有全部断开");
//                    break;
//                }
//            }
//        }

        if(connectSelectedDevice.getText().toString().equals(getResources().getString(R.string.disconnect_all_selected_device))){//按钮的值为断开连接，说明已经连接上了设备deviceConnectedState已经创建了
            for (int i = 0; i < deviceConnectedState.length; i++) {
                if(deviceConnectedState[i]){
                    backFlag=false;
                    Toast.makeText(this,"请全部断开设备",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onBackPressed: 设备没有全部断开");
                    break;
                }
            }
        }
        if(backFlag){
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                bluetoothDeviceArrayList.clear();
                // TODO: 2/14/2019  deviceDefaultCustomNameMap是否需要清空
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }
    private void checkBluetooth(){
        /*判断是否支持Ble蓝牙和是否打开蓝牙*/
        //检查设备是否支持BLE
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this,R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        //通过BluetoothManager初始化BluetoothAdapter
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // 检查设备是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }
    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        memberRecyclerView=findViewById(R.id.multiplayer_mode_coach_member_recycler_view);
        memberRecyclerView.setLayoutManager(linearLayoutManager);
        memberAdapter=new MultiplayerMemberAdapter(initCoachMemberList(coach.getCoachID()));
        memberRecyclerView.setAdapter(memberAdapter);

        LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(this);
        deviceRecyclerView=findViewById(R.id.multiplayer_mode_device_scan_result_recycler_view);
        deviceRecyclerView.setLayoutManager(linearLayoutManager1);
        gattDeviceAdapter=new MultiplayerGattDeviceAdapter(bluetoothDeviceArrayList);
        deviceRecyclerView.setAdapter(gattDeviceAdapter);

//        实现MultiplayerGattDeviceAdapter的接口，来设置connectSelectedDevice的不透明度为1
        gattDeviceAdapter.setViewAlphaItem(new MultiplayerGattDeviceAdapter.ViewAlpha() {
            @Override
            public void setConnectDeviceButtonAlpha(float alpha) {
                connectSelectedDevice.setAlpha(alpha);
            }
            public void setConnectDeviceButtonClickable(boolean flag){
                connectSelectedDevice.setClickable(flag);
            }
        });
    }

    /**
     * 初始化（单人模式下保存过肌肉训练参数的）coachMemberList
     * coachMemberList教练名下的学员
     */
    private ArrayList<Member> initCoachMemberList(int coach_id){
        Iterator<Member> iterator=InitBean.memberArrayList.iterator();
        Member member;
        while(iterator.hasNext()){
            member=iterator.next();
            if(member.getCoachID()==coach_id){
//                判断是否为所有的肌肉保存过参数
                boolean flag=true;
                final int minMuscleID=1;//肌肉ID最小值
                final int totalMuscleNum=10;//肌肉总数目
                for (int i=minMuscleID; i < minMuscleID+totalMuscleNum; i++) {
                    //根据会员id和肌肉ID，查询肌肉训练参数，为空则代表未保存
                    if(LitePal.where("memberID = ? and muscleID = ?",member.getMemberID()+"",i+"").find(CustomMusclePara.class).size()==0){
                        flag=false;
                        break;
                    }
                }
                if(flag==true)coachMemberList.add(member);
            }
        }
        return  coachMemberList;
    }
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    清空selectedBluetoothDeviceList，防止扫描结束之前点击添加按钮，把设备添加到列表中
                    selectedBluetoothDeviceList.clear();

                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    if (bluetoothDeviceArrayList.size()!=0){
//                        customDeviceName.setText(bluetoothDeviceArrayList.get(0).getName());
//                        deviceAddress.setText(bluetoothDeviceArrayList.get(0).getAddress());
                        Toast.makeText(MultiplayerModeTrainingMainActivity.this,"扫描结束！"+"\n"
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
                                deviceRecyclerView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            };
    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_WRITE_SUCCESS);

        //给多人模式的广播，添加广播过滤器
        // TODO: 2/21/2019 根据实际添加的设备数目，添加广播过滤器，设置为最大的数目也可以。因为只能最多连接DEFAULT_MAX_DEVICE_AVAILABLE个设备
        for (int i = 0; i < selectedBluetoothDeviceList.size(); i++) {
            Log.d(TAG+"","makeGattUpdateIntentFilter: "+"length="+"");
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED_ARRAY[i]);
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED_ARRAY[i]);
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED_ARRAY[i]);
            for (int j = 0; j < BluetoothLeService.MUSCLE_CHARACTERISTIC_NUM; j++) {
                intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE_ARRAY[i][j]);
                intentFilter.addAction(BluetoothLeService.ACTION_DATA_WRITE_SUCCESS_ARRAY[i][j]);
            }

        }

        return intentFilter;
    }
}
