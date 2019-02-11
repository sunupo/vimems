package com.vimems.Ble;

import android.os.Bundle;
import android.widget.ExpandableListView;

import com.vimems.R;

import util.BaseActivity;

public class BindDeviceActivity extends BaseActivity {
   private ExpandableListView bindDeviceList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_device);

        bindDeviceList=findViewById(R.id.bind_device_detail_expandList);

    }
}
