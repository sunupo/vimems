package com.vimems.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.vimems.Adapter.MemberAdapter;
import com.vimems.R;
import com.vimems.bean.Member;

import java.util.ArrayList;

import util.BaseActivity;
import util.InitBean;

public class CoachDetailActivity extends BaseActivity {
    private TextView coachUsername;
    private TextView coachGender;
    private TextView coachBirthday;
    private  TextView coachID;
    private RecyclerView recyclerView;
    private ArrayList<Member> caochMemberList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_detail);

        coachUsername=findViewById(R.id.coach_name);
        coachGender=findViewById(R.id.coach_gender);
        coachBirthday=findViewById(R.id.coach_birthday);
        coachID=findViewById(R.id.coach_ID);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView=findViewById(R.id.coach_member_recycler_view);
        recyclerView.setLayoutManager(linearLayoutManager);
        MemberAdapter memberAdapter=new MemberAdapter(InitBean.memberItemArrayList);
        recyclerView.setAdapter(memberAdapter);
        Intent intent=getIntent();
//        coachUsername.setText(intent.getStringExtra("coachUsername"));
//        coachGender.setText(intent.getStringExtra("caochGender"));
//        coachBirthday.setText(intent.getStringExtra("coachBirthday"));

        coachID.setText(intent.getStringExtra("coachID"));
        Log.d("coachID", intent.getStringExtra("coachID"));
    }
    private void initCoachMemberList(){

    };
}
