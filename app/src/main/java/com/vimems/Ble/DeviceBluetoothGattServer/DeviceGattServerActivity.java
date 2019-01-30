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

import java.util.HashSet;
import java.util.Set;

import util.BaseActivity;

public class DeviceGattServerActivity extends BaseActivity {
    private static final String TAG=DeviceGattServerActivity.class.getSimpleName();
    private TextView muscle1ParaTextView;
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
            int para= Integer.parseInt(muscle1ParaTextView.getText().toString());
            muscle1ParaTextView.setText("para= "+(para-1));
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
                    .getCharacteristic(ParaProfile.MUSCLE1_CHARACTERISTIC);
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
        muscle1ParaTextView.setText("100");
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
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId,
                                                 BluetoothGattCharacteristic characteristic,
                                                 boolean preparedWrite, boolean responseNeeded,
                                                 int offset, final byte[] value) {

            if(ParaProfile.MUSCLE1_CHARACTERISTIC.equals(characteristic.getUuid())){
                characteristic.setValue(value);
                Log.i(TAG, "onCharacteristicWriteRequest: Write MUSCLE1_CHARACTERISTIC"+value.toString());
                mBluetoothGattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0,
                        characteristic.getValue()
                        );

                final String str="value[0]="+value[0]+"\n"+"value[1]="+value[1]+"\n"
                        +"value[2]="+value[2]+"\n"+"value[3]="+value[3]+"\n";
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        muscle1ParaTextView.setText(str);
                        Toast.makeText(DeviceGattServerActivity.this,"99",Toast.LENGTH_SHORT);
                    }
                });
            }
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset,
                                                BluetoothGattCharacteristic characteristic) {
            if(ParaProfile.MUSCLE1_CHARACTERISTIC.equals(device.getUuids())){
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

}
