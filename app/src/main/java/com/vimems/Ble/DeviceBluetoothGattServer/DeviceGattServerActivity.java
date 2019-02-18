package com.vimems.Ble.DeviceBluetoothGattServer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.vimems.R;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import util.BaseActivity;

import static com.vimems.Ble.DeviceBluetoothGattServer.ParaProfile.MUSCLE1_CHARACTERISTIC;
import static com.vimems.Ble.DeviceBluetoothGattServer.ParaProfile.MUSCLE2_CHARACTERISTIC;
import static com.vimems.Ble.DeviceBluetoothGattServer.ParaProfile.MUSCLE3_CHARACTERISTIC;
import static com.vimems.Ble.DeviceBluetoothGattServer.ParaProfile.MUSCLE4_CHARACTERISTIC;
import static com.vimems.Ble.DeviceBluetoothGattServer.ParaProfile.uuidList;
import static com.vimems.coach.CustomTrainingItemFragment.checkBoxIntegerMap;
import static java.lang.Thread.sleep;

public class DeviceGattServerActivity extends BaseActivity {
    private static final String TAG=DeviceGattServerActivity.class.getSimpleName();
    private TextView muscle1ParaTextView,muscle2ParaTextView,muscle3ParaTextView,muscle4ParaTextView
            ,muscle5ParaTextView,muscle6ParaTextView,muscle7ParaTextView,muscle8ParaTextView
            ,muscle9ParaTextView,muscle10ParaTextView;
    private BluetoothManager mBluetoothManager;
    private BluetoothGattServer mBluetoothGattServer;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    //记录蓝牙设备，发出电量信息通知
    public Set<BluetoothDevice> bluetoothDeviceSet=new HashSet<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_gattt_server);
        muscle1ParaTextView=findViewById(R.id.muscle1_para_textview);
        muscle2ParaTextView=findViewById(R.id.muscle2_para_textview);
        muscle3ParaTextView=findViewById(R.id.muscle3_para_textview);
        muscle4ParaTextView=findViewById(R.id.muscle4_para_textview);
        muscle5ParaTextView=findViewById(R.id.muscle5_para_textview);
        muscle6ParaTextView=findViewById(R.id.muscle6_para_textview);
        muscle7ParaTextView=findViewById(R.id.muscle7_para_textview);
        muscle8ParaTextView=findViewById(R.id.muscle8_para_textview);
        muscle9ParaTextView=findViewById(R.id.muscle9_para_textview);
        muscle10ParaTextView=findViewById(R.id.muscle10_para_textview);


        //当前活动可见时，保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //检查是否支持蓝牙
        mBluetoothManager=(BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter=mBluetoothManager.getAdapter();
        if(mBluetoothAdapter==null){
            Log.w(TAG,"当前设备不支持蓝牙");
            finish();
        }
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            Log.w(TAG, "onCreate: 当前设备不支持Bluetooth LE");
            finish();
        }

        //注册一个广播接收器，当蓝牙开/关状态发生改变时，收到ACTION_STATE_CHANGED发出的广播
        IntentFilter intentFilter=new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBluetoothReceiver,intentFilter);

        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "Bluetooth is currently disabled...enabling");
            mBluetoothAdapter.enable();
        } else {
            Log.d(TAG, "Bluetooth enabled...starting services");
            startAdvertising();
            startServer();
        }
    }
    private BroadcastReceiver mBluetoothReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);

            switch (state) {
                case BluetoothAdapter.STATE_ON:
                    startAdvertising();
                    startServer();
                    break;
                case BluetoothAdapter.STATE_OFF:
                    stopServer();
                    stopAdvertising();
                    break;
                default:
                    // Do nothing
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter=new IntentFilter("ACTION_PARA_CHANGE");
        registerReceiver(paraChangeReceiver,filter);
    }
    private BroadcastReceiver paraChangeReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()){
                case "ACTION_PARA_CHANGE":
                    notifyRegisteredDevices(ParaProfile.getPara());
                    break;
                default:
                    break;
            }

            muscle1ParaTextView.setText("paraChangeReceiver=接收到广播"+new Date());
        }
    };

    //可以服务端更改数值，然后通知中心设备
    private void notifyRegisteredDevices(byte[] bytes) {
        if (bluetoothDeviceSet.isEmpty()) {
            Log.i(TAG, "No subscribers registered");
            return;
        }
        Log.i(TAG, "Sending update to " + bluetoothDeviceSet.size() + " subscribers");
        for (BluetoothDevice device : bluetoothDeviceSet) {
            BluetoothGattCharacteristic muscleParaCharacteristic = mBluetoothGattServer
                    .getService(ParaProfile.PARA_SERVICE)
                    .getCharacteristic(MUSCLE1_CHARACTERISTIC);
            muscleParaCharacteristic.setValue(bytes);
            mBluetoothGattServer.notifyCharacteristicChanged(device, muscleParaCharacteristic, false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(paraChangeReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();
        if (bluetoothAdapter.isEnabled()) {
            stopServer();
            stopAdvertising();
        }
        unregisterReceiver(mBluetoothReceiver);

    }

    private void startAdvertising() {
        BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();
        mBluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        if (mBluetoothLeAdvertiser == null) {
            Log.w(TAG, "Failed to create advertiser");
            return;
        }

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build();

        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(false)
                .addServiceUuid(new ParcelUuid(ParaProfile.PARA_SERVICE))
                .build();

        mBluetoothLeAdvertiser
                .startAdvertising(settings, data, mAdvertiseCallback);
    }

    private void stopAdvertising() {

        if (mBluetoothLeAdvertiser == null)
            return;
        mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
    }

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.i(TAG, "LE Advertise Started.");
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.w(TAG, "LE Advertise Failed: "+errorCode);
        }
    };



    private void startServer() {
        mBluetoothGattServer = mBluetoothManager.openGattServer(this, mGattServerCallback);
        if (mBluetoothGattServer == null) {
            Log.w(TAG, "Unable to create GATT server");
            return;
        }

        mBluetoothGattServer.addService(ParaProfile.createParaService());

        // Initialize the local UI
        muscle1ParaTextView.setText("接受参数");
    }

    private void stopServer() {
        if (mBluetoothGattServer == null) return;

        mBluetoothGattServer.close();
    }



    private BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "BluetoothDevice CONNECTED: " + device);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "BluetoothDevice DISCONNECTED: " + device);
                //Remove device from any active subscriptions
                bluetoothDeviceSet.remove(device);
            }
        }

        @Override
        public void onCharacteristicWriteRequest(final BluetoothDevice device, final int requestId,
                                                 final BluetoothGattCharacteristic characteristic,
                                                 boolean preparedWrite, boolean responseNeeded,
                                                 int offset, final byte[] value) {
            Log.d(TAG, "onCharacteristicWriteRequest: "+characteristic.getUuid().toString());
            // TODO: 2/16/2019 接收10个characteristic 设置一个map<MUSCLE1_CHARACTERISTIC,muscle1ParaTextView>,减少代码
            Map<UUID,TextView> map =initMuscleParaUuidTextViewMap();
            for (int i = 0; i <map.size() ; i++) {
                decideWhichMuscle(uuidList.get(i),characteristic,value,device,requestId,map.get(uuidList.get(i)));
            }
//            decideWhichMuscle(MUSCLE1_CHARACTERISTIC,characteristic,value,device,requestId,muscle1ParaTextView);
        }

        //判断是哪一个muscle对应的uuid
        private void decideWhichMuscle(final UUID uuid,BluetoothGattCharacteristic characteristic,byte[] value,
                                       BluetoothDevice device,int requestId,final TextView textView){
            if(uuid.equals(characteristic.getUuid())){
                characteristic.setValue(value);
                Log.d(TAG, "查看CHARACTERISTIC"+getValueToString(value));
                mBluetoothGattServer.sendResponse(device,requestId,BluetoothGatt.GATT_SUCCESS,0,characteristic.getValue());
                final String str=getCustomString(value);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(str);
//                        try {
//                            sleep(200);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                        Log.d(TAG, "Characteristic uuid: "+uuid.toString());
                        Toast.makeText(DeviceGattServerActivity.this,"成功接受参数",Toast.LENGTH_SHORT);
                    }
                });
            }
        }

        private String getCustomString(byte[] value){
            String string="肌肉位置="+value[0] +"\n"
                    +"会员id="+value[1]+"\n"
                    +"ModeCode="+value[2]+"\n"
                    +"ModuleCode="+value[3]+"\n"
                    +"等级="+value[4]+"\n"
                    +"低频="+(value[5]+value[6]*100)+"\n"
                    +"高频="+(value[7]+value[8]*100)+"\n"
                    +"脉宽="+value[9]+"\n"
                    +"脉冲时间="+value[10]+"\n"
                    +"间歇时间="+value[11]+"\n"
                    +"强度="+value[12]+"\n";
            return string;
        }
        private String getValueToString(byte[] value){
            StringBuffer strValue=new StringBuffer();
            for (int i = 0; i <value.length ; i++) {
                if(i%13==0) strValue.append("\n");
                strValue.append(value[i]+",");

            }
            return strValue.toString();
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset,
                                                BluetoothGattCharacteristic characteristic) {
            if(MUSCLE1_CHARACTERISTIC.equals(device.getUuids())){
                Log.i(TAG, "onCharacteristicReadRequest: read MUSCLE1_CHARACTERISTIC");
                mBluetoothGattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0,
                        characteristic.getValue()
                        );
            }
        }
    };
    private Map<UUID,TextView> initMuscleParaUuidTextViewMap(){
        Map<UUID,TextView> muscleParaUuidTextViewMap=new HashMap<>();
        muscleParaUuidTextViewMap.put(ParaProfile.MUSCLE1_CHARACTERISTIC,muscle1ParaTextView);
        muscleParaUuidTextViewMap.put(ParaProfile.MUSCLE2_CHARACTERISTIC,muscle2ParaTextView);
        muscleParaUuidTextViewMap.put(ParaProfile.MUSCLE3_CHARACTERISTIC,muscle3ParaTextView);
        muscleParaUuidTextViewMap.put(ParaProfile.MUSCLE4_CHARACTERISTIC,muscle4ParaTextView);
        muscleParaUuidTextViewMap.put(ParaProfile.MUSCLE5_CHARACTERISTIC,muscle5ParaTextView);
        muscleParaUuidTextViewMap.put(ParaProfile.MUSCLE6_CHARACTERISTIC,muscle6ParaTextView);
        muscleParaUuidTextViewMap.put(ParaProfile.MUSCLE7_CHARACTERISTIC,muscle7ParaTextView);
        muscleParaUuidTextViewMap.put(ParaProfile.MUSCLE8_CHARACTERISTIC,muscle8ParaTextView);
        muscleParaUuidTextViewMap.put(ParaProfile.MUSCLE9_CHARACTERISTIC,muscle9ParaTextView);
        muscleParaUuidTextViewMap.put(ParaProfile.MUSCLE10_CHARACTERISTIC,muscle10ParaTextView);
        return muscleParaUuidTextViewMap;
    }

}
