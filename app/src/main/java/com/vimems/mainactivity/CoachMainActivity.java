package com.vimems.mainactivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vimems.Adapter.MemberAdapter;
import com.vimems.Ble.AppBluetoothGatt.GattMainActivity;
import com.vimems.Ble.BleScannerAdvertiserActivity;
import com.vimems.Ble.DeviceBluetoothGattServer.DeviceGattServerActivity;
import com.vimems.R;
import com.vimems.bean.Coach;
import com.vimems.bean.Member;

import java.util.ArrayList;
import java.util.Iterator;

import util.BaseActivity;
import util.InitBean;

public class CoachMainActivity extends BaseActivity implements View.OnClickListener {

    private TextView coachName;
    private TextView coachGender;
    private TextView coachBirthday;
    private  TextView coachID;
    private RecyclerView recyclerView;
    private ArrayList<Member> coachMemberList=new ArrayList<>();

    private Button scanBleDeviceButton;

    private Button asGattServer;
    private  Button asGattCentral;

    private  String coachLoginname;
    private  int coach_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_main);

        Intent intent=getIntent();
        coachLoginname=intent.getStringExtra("coachLoginName");

        coachName= findViewById(R.id.coach_name);
        coachGender=findViewById(R.id.coach_gender);
        coachBirthday=findViewById(R.id.coach_birthday);
        coachID=findViewById(R.id.coach_ID);
        scanBleDeviceButton=findViewById(R.id.scan_ble_device);
        scanBleDeviceButton.setOnClickListener(this);

        asGattServer=findViewById(R.id.set_gatt_server_periphery);
        asGattServer.setOnClickListener(this);
        asGattCentral=findViewById(R.id.as_gatt_central);
        asGattCentral.setOnClickListener(this);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView=findViewById(R.id.coach_member_recycler_view);
        recyclerView.setLayoutManager(linearLayoutManager);
        MemberAdapter memberAdapter=new MemberAdapter(initCoachMemberList(getCoachID(InitBean.coachArrayList,coachLoginname)));
        recyclerView.setAdapter(memberAdapter);


//        coachUsername.setText(intent.getStringExtra("coachUsername"));
//        coachGender.setText(intent.getStringExtra("caochGender"));
//        coachBirthday.setText(intent.getStringExtra("coachBirthday"));


        coachID.setText(intent.getStringExtra("coachID"));
    }

    private int getCoachID(ArrayList<Coach> coachArrayList,String coachLoginname){
        Iterator<Coach> coachIterator=coachArrayList.iterator();
        Coach coach;
        while(coachIterator.hasNext()){
            coach=coachIterator.next();
            if(coach.getCoachLoginName().equals(coachLoginname)){
                coach_id=coach.getCoachID();
                return coach_id;

            }
        }
        return 0;
    }
    private ArrayList<Member> initCoachMemberList(int coach_id){
        Iterator<Member> iterator=InitBean.memberArrayList.iterator();
        Member member;
        while(iterator.hasNext()){
            member=iterator.next();
            if(member.getCoachID()==coach_id){
                coachMemberList.add(member);
            }
        }
        return  coachMemberList;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.scan_ble_device:
                Intent intent=new Intent(this, BleScannerAdvertiserActivity.class);
                v.getContext().startActivity(intent);
                break;
            case  R.id.set_gatt_server_periphery:
                Intent gattServerIntent=new Intent(this, DeviceGattServerActivity.class);
                v.getContext().startActivity(gattServerIntent);
                break;
            case  R.id.as_gatt_central:
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},1);
                }else {
                    Intent gattMainActivityIntent=new Intent(this, GattMainActivity.class);
                    v.getContext().startActivity(gattMainActivityIntent);
                }

                break;
        }
    }
}
