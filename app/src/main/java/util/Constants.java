package util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    /*public static final ParcelUuid Service_UUID = ParcelUuid
            .fromString("0000ae8f-0000-1000-8000-00805f9b34fb");*/
    public static final ParcelUuid Service_UUID = ParcelUuid
            .fromString("0000b81d-0000-1000-8000-00805f9b34fb");

    public static final int REQUEST_ENABLE_BT = 1;

    public  static  boolean connectable=true;
    public  static  int timeoutMillis=0;

    public static final String[] TRAINING_MODE = {"自定义训练","课程训练","VIP训练"};

    //设置多少毫秒ms后停止扫描
    public static final int SCAN_PERIOD = 5000;

    public static String CUSTOM_TRAINING_ITEM_FRAGMENT_INTENT_CATEGORY="CUSTOM_TRAINING_ITEM_FRAGMENT_INTENT_CATEGORY";
    public static final String ARG_PAGE="ARG_PAGE";
    public static final String TRAINING_ITEM="TRAINING_ITEM";
    public static final String MEMBER_ID="MEMBER_ID";

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

}
