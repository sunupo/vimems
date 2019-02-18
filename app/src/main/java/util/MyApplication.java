package util;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.vimems.R;

import org.litepal.LitePal;

import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application {

        private final String TAG=MyApplication.class.getSimpleName();
        @Override
        public void onCreate() {
                super.onCreate();
                Log.d(TAG, "onCreate: ");
                LitePal.initialize(this);

                /*判断是否支持Ble蓝牙和是否打开蓝牙*/
                //检查设备是否支持BLE
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                        Toast.makeText(this,R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
//                System.exit(0);
                }
                // 检查设备是否支持蓝牙
//                if (mBluetoothAdapter == null) {
//                        Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
////                        System.exit(0);
//                        return;
//                }
        }

        @Override
        public void onTerminate() {
                super.onTerminate();
                Log.d(TAG, "onTerminate: ");
        }

        @Override
        public void onLowMemory() {
                super.onLowMemory();
                Log.d(TAG, "onLowMemory: ");
        }
}
