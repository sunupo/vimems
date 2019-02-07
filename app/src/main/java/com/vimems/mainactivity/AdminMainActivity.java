package com.vimems.mainactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vimems.Adapter.CoachAdapter;
import com.vimems.R;
import com.vimems.admin.AddCoach;
import com.vimems.bean.Admin;
import com.vimems.bean.Coach;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.Iterator;

import util.BaseActivity;
import util.InitBean;

public class AdminMainActivity extends BaseActivity {

    private Button addCoach;
    private  RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private  String adminLoginName;
    private  int admin_id;
    private  ArrayList<Coach> adminCoachArrayList=new ArrayList<>();

    private Admin admin;

    private TextView adminID;
    private TextView adminName;
    private TextView loginName;
    private TextView adminGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        Intent intent=getIntent();
        adminLoginName=intent.getStringExtra("adminLoginName");

        adminID=findViewById(R.id.admin_ID);
        adminName=findViewById(R.id.admin_name);
        loginName=findViewById(R.id.admin_login_name);
        adminGender=findViewById(R.id.admin_gender);

        //查询数据库，得到当前登录的admin对象
        //id从1开始，数据库中的adminID从0开始

        admin= LitePal.where("loginName = ?",adminLoginName).find(Admin.class).get(0);
        Log.e("TAG_ADMIN", "onCreate: "+admin.toString() );

        adminID.setText(admin.getAdminID()+"");//参数直接使用admin.getAdminID()报错
        adminName.setText(admin.getAdminName());
        loginName.setText(admin.getLoginName());
        adminGender.setText(admin.getGender());



        Log.d("adminLoginName", "onCreate:adminLoginName "+adminLoginName);

        recyclerView=findViewById(R.id.coach_recycler_view);
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        CoachAdapter coachAdapter=new CoachAdapter(initCoachArrayList(getAdminID(InitBean.adminArrayList,adminLoginName)));
        recyclerView.setAdapter(coachAdapter);
        addCoach=findViewById(R.id.add_coach_button);
        addCoach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminMainActivity.this, AddCoach.class);
                Bundle bundle=new Bundle();
                bundle.putInt("adminID",admin.getAdminID());
                bundle.putInt("coachID",LitePal.count(Coach.class));
                intent.putExtras(bundle);
                startActivity(intent);
                Toast.makeText(v.getContext(),"添加教练",Toast.LENGTH_LONG).show();
            }
        });
    }
    private int getAdminID(ArrayList<Admin> adminArrayList, String adminLoinName){
        Iterator<Admin> adminIterator=adminArrayList.iterator();
        Admin admin;
        while(adminIterator.hasNext()){
            admin=adminIterator.next();
            if(admin.getLoginName().equals(adminLoinName)){
                admin_id=admin.getAdminID();
                Log.d("admin_id", "onCreate:admin_id "+admin_id);
                return admin_id;
            }
        }
        return 0;
    }
    public ArrayList<Coach> initCoachArrayList(int admin_id){


        Iterator<Coach> iterator=InitBean.coachArrayList.iterator();
        Coach coach;
        while(iterator.hasNext()){
            coach=iterator.next();
            if(coach.getAdminID()==admin_id){
                adminCoachArrayList.add(coach);
            }
        }
        return  adminCoachArrayList;
    }
}
