package util;

import android.app.Notification;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.util.Log;
import android.util.TimingLogger;
import android.widget.Toast;

import com.vimems.Ble.BleScannerActivity;

import java.util.ArrayList;
import java.util.List;

public class BleMananger {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;

    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    private AdvertiseCallback mAdvertiseCallback;
    private  ScanCallback mScanCallBack;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothGatt mBluetoothGatt;
    private boolean isScanning = false;
    private Context context;
    private final String TAG="BleScannerActivity";
    private static final int FOREGROUND_NOTIFICATION_ID = 1;
    private static List<ScanFilter> scanFilterList;
    private static ScanSettings scanSettings;



    public BleMananger(Context context) {
        this.context=context;
        /*
        * JELLY_BEAN_MR1以下的版本使用如下方式获得BluetoothAdapter单例对象。
        * BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        * */
        BluetoothManager blutoothManager=(BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE);
        if(blutoothManager==null){
            Log.e(TAG,"不能初始化BluetoothManager");
            return;
        }
        mBluetoothAdapter=blutoothManager.getAdapter();
        mBluetoothLeScanner=mBluetoothAdapter.getBluetoothLeScanner();

        mBluetoothLeAdvertiser=mBluetoothAdapter.getBluetoothLeAdvertiser();
    }
    private void initScanFilterList(){
        scanFilterList=new ArrayList<>();
        ScanFilter scanFilter1=new ScanFilter.Builder()
                .setServiceUuid(Constants.Service_UUID)
                .build();
        scanFilterList.add(scanFilter1);
    }
    public void initScanSettings(){
        scanSettings=new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT)
                .build();
    }
    private class SampleScanCallback extends ScanCallback{
        public SampleScanCallback() {
            super();
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            byte[] scanRecord=result.getScanRecord().getBytes();
            //todo 把scanRecord数组转换成16进制字符串
            Log.e(TAG, "onScanResult:result.getScanRecord().getBytes().toString()= "+scanRecord.toString() );
            Log.e("TAG","onScanResult :result.getScanRecord()="+result.getScanRecord());
            Log.e("TAG","onScanResult :result.getScanRecord().toString()="+result.getScanRecord().toString());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    }
    public void startLeScan(){
        if (mBluetoothLeScanner == null) {
            return;
        }
        if(isScanning){
            return;
        }
        isScanning=true;

        mScanCallBack= new SampleScanCallback();

        mBluetoothLeScanner.startScan(scanFilterList,scanSettings,mScanCallBack);

    }
    /*在5.0之前扫描BLE设备 使用的方法是 ：
        bluetoothAdapter.startLeScan( leScanCallback);*/
    //或者
    // UUID[] serviceUuids = new UUID[] { UUID.fromString("0000ae8f-0000-1000-8000-00805f9b34fb") };
    // bluetoothAdapter.startLeScan(serviceUuids, leScanCallback);

    /*mBluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback(){
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            //把byte数组转成16进制字符串，方便查看
            Log.e("TAG","scanRecord:"+scanRecord+" rssi:"+rssi);
        }
    });*/
    /**
     * author sunupo
     * date 2019/1/21 22:31
     * description 开始广播
     *
     */
    public void startAdvertisingAction(){
        Log.d(TAG, "Service: Starting Advertising");

        if (mAdvertiseCallback == null) {
            AdvertiseSettings settings = buildAdvertiseSettings();
            AdvertiseData data = buildAdvertiseData();
            mAdvertiseCallback = new SampleAdvertiseCallback();

            if (mBluetoothLeAdvertiser != null) {
                mBluetoothLeAdvertiser.startAdvertising(settings, data,
                        mAdvertiseCallback);
            }
        }
    }

   /**
    * author sunupo
    * date 2019/1/22 9:45
    * description 构造该方法的三个参数startAdvertising(settings, data,mAdvertiseCallback);
    *
    */
    private AdvertiseSettings buildAdvertiseSettings() {
        AdvertiseSettings.Builder settingsBuilder = new AdvertiseSettings.Builder();
        /**
         * 设置广播的模式，低功耗，平衡和低延迟三种模式；
         * 对应  AdvertiseSettings.ADVERTISE_MODE_LOW_POWER  ,ADVERTISE_MODE_BALANCED ,ADVERTISE_MODE_LOW_LATENCY
         * 从左右到右，广播的间隔会越来越短
         */
        settingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER);
        /**
         * 设置是否可以连接。广播分为可连接广播和不可连接广播，
         * 一般不可连接广播应用在iBeacon设备上，这样APP无法连接上iBeacon设备
         */
        settingsBuilder.setConnectable(Constants.connectable);
        /**
         * 设置广播的最长时间，
         * 最大值为常量AdvertiseSettings.LIMITED_ADVERTISING_MAX_MILLIS = 180 * 1000;  180秒
         * 设为0时，代表无时间限制会一直广播
         */
        settingsBuilder.setTimeout(Constants.timeoutMillis);
        /**
         *  setTxPowerLevel(int txPowerLevel);
         * 设置广播的信号强度
         *常量有AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW,ADVERTISE_TX_POWER_LOW,ADVERTISE_TX_POWER_MEDIUM,ADVERTISE_TX_POWER_HIGH
         * 从左到右分别表示强度越来越强.
         * 举例：当设置为ADVERTISE_TX_POWER_ULTRA_LOW时，
         * 手机1和手机2放在一起，手机2扫描到的rssi信号强度为-56左右，
         * 当设置为ADVERTISE_TX_POWER_HIGH  时， 扫描到的信号强度为-33左右，
         * 信号强度越大，表示手机和设备靠的越近
         */

        return settingsBuilder.build();
    }
    public AdvertiseSettings buildAdvertiseSettings(boolean connectable, int timeoutMillis) {
        AdvertiseSettings.Builder mSettingsbuilder = new AdvertiseSettings.Builder();
        mSettingsbuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        mSettingsbuilder.setConnectable(connectable);
        mSettingsbuilder.setTimeout(timeoutMillis);
        AdvertiseSettings mAdvertiseSettings = mSettingsbuilder.build();
        return mAdvertiseSettings;
    }
    /**
     * author sunupo
     * date 2019/1/22 9:46
     * description 构造该方法的三个参数startAdvertising(settings, data,mAdvertiseCallback);
     *
     */

    private AdvertiseData buildAdvertiseData() {

        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
        dataBuilder.addServiceUuid(Constants.Service_UUID);
        dataBuilder.setIncludeDeviceName(true);//Set whether the device name should be included in advertise packet.

        return dataBuilder.build();
    }
    public AdvertiseData buildAdvertiseData(byte[] data) {
        //data ={0x34,0x56};
        AdvertiseData.Builder mDataBuilder = new AdvertiseData.Builder();
        /*
        * byte[] manufacturerSpecificData)
        * Add manufacturer specific data.
        * Please refer to the Bluetooth Assigned Numbers document provided by the Bluetooth SIG  for a list of existing company identifiers.
        * Parameters:
        * manufacturerId int: Manufacturer ID assigned by Bluetooth SIG.
        * manufacturerSpecificData byte: Manufacturer specific data
        * */
        mDataBuilder.addManufacturerData(0x01AC, data);

        /**只添加addManFacturerData方法
         *  假如，扫描得到的数据是 02011A05FFAC013456
         *  分段如下： 02011A  05  FF AC01 3456
         *  02011A这3个字节，02表示后面一段数据长度为2字节，01表示数据类型是flag ,1A就是flag的数据了
         *  05 表示后面的一段数据长度为 5个字节， FF一个字节，AC01 两个字节，3456两个字节，加起来一共5个字节，
         *  FF，是一个数据类型，这是我们通过代码mDataBuilder.addManufacturerData(0x01AC, data); 添加广播数据时候设置的
         *  ManufacturerData 是指设备厂商自定义数据，FF 就是代表下面的数据实体是厂商数据.
         *  第一个参数0x01AC，是厂商id，id长度为2个字节，不足2个字节系统会补0，可以看到log打印出来的是 AC01，
         *  顺序是倒过来的，这点要注意！
         */

        mDataBuilder.addServiceUuid(Constants.Service_UUID);
        /**
        * 当广播数据添加了service_uuid 0000ae8f-0000-1000-8000-00805f9b34fb
        *另一台设备扫描的结果应该就是02011A05FFAC01345603038FAE
         * 分段02 011A 05 FFAC013456 03 038FAE
         * 03 038FAE
         * 第二个03 表示这个数据类型是 server uuid类型，uuid的数据就是AE8F，顺序是倒过来的！
        * */
        /**
         * addServiceUuid代码放在 addManufacturerData 前面,扫描得到的结果一样
         */

        mDataBuilder.addServiceUuid(ParcelUuid.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
        /**
         * 添加多个service_uuid
         *         mDataBuilder.addServiceUuid( ParcelUuid.fromString(
         *         "0000ffe1-0000-1000-8000-00805f9b34fb"));
         * 扫描结果为02011A05FFAC01345605038FAEE1FF
         * 分段02 011A 05 FFAC013456 05 038FAEE1FF
         * 05 038FAEE1FF
         * 可以看到有两个service_uuid AE8F和FFE1,
         */
        //这个ServerUUID 有什么用呢？
        //在扫描BLE设备的时候，注意到这个方法：
        //1.以前通过BluetoothAdapter的startLeScan方法，serviceUuids可以筛选出service_uuid
        //BluetoothAdapter
        //public boolean startLeScan(final UUID[] serviceUuids, final LeScanCallback callback)
        //2.现在的BluetoothLeScanner的startScan方法中有个ScanFilter，可以通过UUID筛选
        // void startScan (List<ScanFilter> filters,
        //                ScanSettings settings,
        //                ScanCallback callback)
        //另一台设备的扫描结果，只会出现有在startLeScan或startScan中声明过的uuid的设备

        mDataBuilder.addServiceData( ParcelUuid.fromString("0000ae8f-0000-1000-8000-00805f9b34fb")
                ,new byte[]{0x64,0x12});
        /**
         * 广播数据可以添加ManufacturerData，还可以添加ServerUUID，还可以添加ServiceData
         * 扫描结果为02011A05FFAC01345605038FAEE1FF05168FAE6412
         * 注意后边部分05 038FAEE1FF 05 168FAE6412
         * 16表示数据类型为 server data， 8FAE表示这个数据的uuid是AE8F， 6412就是数据本体了
         */
        return mDataBuilder.build();
    }
    /**
     * author sunupo
     * date 2019/1/22 9:46
     * description 构造该方法的三个参数startAdvertising(settings, data,mAdvertiseCallback);
     *
     */

    private class SampleAdvertiseCallback extends AdvertiseCallback {

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);

            Log.d(TAG, "Advertising failed");
            Toast.makeText(context,"广播失败 errorCode="+errorCode,Toast.LENGTH_SHORT);

        }

        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
        }
    }

}
