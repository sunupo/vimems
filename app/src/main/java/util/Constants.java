package util;

import android.os.ParcelUuid;

public class Constants {
    /*public static final ParcelUuid Service_UUID = ParcelUuid
            .fromString("0000ae8f-0000-1000-8000-00805f9b34fb");*/
    public static final ParcelUuid Service_UUID = ParcelUuid
            .fromString("0000b81d-0000-1000-8000-00805f9b34fb");

    public static final int REQUEST_ENABLE_BT = 1;

    public  static  boolean connectable=true;
    public  static  int timeoutMillis=0;

    public static final String[] TRAINING_MODE = {"自定义训练","课程训练","VIP训练"};

}
