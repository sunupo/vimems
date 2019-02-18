package com.vimems.coach;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.vimems.Adapter.CustomTrainingFragmentPageAdapter;
import com.vimems.Adapter.GattDeviceAdapter;
import com.vimems.R;

import java.util.ArrayList;
import java.util.List;

import util.BaseActivity;


import static util.Constants.ARG_PAGE;
import static util.Constants.REQUEST_ENABLE_BT;
import static util.Constants.SCAN_PERIOD;

public class MemberDetailActivity extends BaseActivity {

    private BluetoothAdapter mBluetoothAdapter;

    public static RecyclerView gattDeviceRecyclerView;
    private LinearLayoutManager recyclerViewlinearLayoutManager;
    private GattDeviceAdapter gattDeviceAdapter;

    private CustomTrainingFragmentPageAdapter pageAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public static int memberID;
    
    List<Fragment> fragmentList;


    private Handler mHandler;
    private ArrayList<BluetoothDevice> bluetoothDeviceArrayList=new ArrayList<>();

    private static boolean mScanning;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);

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

        gattDeviceRecyclerView=findViewById(R.id.member_detail_device_scan_result_recycler_view);
        recyclerViewlinearLayoutManager=new LinearLayoutManager(this);
        gattDeviceRecyclerView.setLayoutManager(recyclerViewlinearLayoutManager);
        gattDeviceAdapter=new GattDeviceAdapter(bluetoothDeviceArrayList);
        gattDeviceRecyclerView.setAdapter(gattDeviceAdapter);


        mHandler=new Handler();

        memberID=getIntent().getIntExtra("MEMBER_ID",1);
        initFragmentList();

        FragmentManager fragmentManager=getSupportFragmentManager();
        pageAdapter=new CustomTrainingFragmentPageAdapter(fragmentManager,fragmentList);
        viewPager=findViewById(R.id.training_mode_viewpager);
        tabLayout=findViewById(R.id.training_mode_tab);
        viewPager.setAdapter(pageAdapter);
        //让TabLayout与viewpager产生联动
        tabLayout.setupWithViewPager(viewPager);

        scanLeDevice(true);
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

    public void initFragmentList(){
        /**
         * viewpager的fragment列表
         */
        fragmentList=new ArrayList<>();
        //因为在viewpager的第一页和第三页使用的是CustomTrainingFragment，所以传入参数1和3来记录位置
        //在CustomTrainingItemFragment中可以通过参数是1，设置低频和高频不显示
        //通过判断参数为3，设置vip训练的高频和低频可以显示

        CustomTrainingFragment customTrainingFragment=new CustomTrainingFragment();
        Bundle customTrainingFragmentBundle=new Bundle();
        customTrainingFragmentBundle.putInt(ARG_PAGE,1);
        customTrainingFragmentBundle.putInt("MEMBER_ID",memberID);
        customTrainingFragment.setArguments(customTrainingFragmentBundle);

        CustomTrainingFragment customTrainingFragmentVip=new CustomTrainingFragment();
        Bundle customTrainingFragmentVipBundle=new Bundle();
        customTrainingFragmentVipBundle.putInt(ARG_PAGE,3);
        customTrainingFragmentVipBundle.putInt("MEMBER_ID",memberID);
        customTrainingFragmentVip.setArguments(customTrainingFragmentVipBundle);

        fragmentList.add(customTrainingFragment);
        fragmentList.add(new VideoTrainingFragment());
        fragmentList.add(customTrainingFragmentVip);
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
                        Toast.makeText(MemberDetailActivity.this,"扫描结束！"+"\n"
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
                                gattDeviceRecyclerView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            };
    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
//        bluetoothDeviceArrayList.clear();
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


}
