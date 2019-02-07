package com.vimems.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vimems.Adapter.MemberAdapter;
import com.vimems.R;
import com.vimems.bean.Coach;
import com.vimems.bean.Member;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Iterator;

import util.BaseActivity;
import util.InitBean;

public class CoachDetailActivity extends BaseActivity {
    private TextView coachName;
    private TextView coachGender;
    private TextView coachBirthday;
    private  TextView coachID;
    private RadioGroup coachRank;
    private RecyclerView recyclerView;
    private ArrayList<Member> coachMemberList=new ArrayList<>();


    private  String coach_Name;
    private int coach_id;

    private  Coach coach;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_detail);
        Intent intent=getIntent();
        coach_Name=intent.getStringExtra("coachName");

        coachName= findViewById(R.id.coach_name);
        coachGender=findViewById(R.id.coach_gender);
        coachBirthday=findViewById(R.id.coach_birthday);
        coachID=findViewById(R.id.coach_ID);
        coachRank=findViewById(R.id.radio_group_coach_rank_display);


        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView=findViewById(R.id.coach_member_recycler_view);
        recyclerView.setLayoutManager(linearLayoutManager);
        MemberAdapter memberAdapter=new MemberAdapter(initCoachMemberList(getCoachID(InitBean.coachArrayList,coach_Name)));
        recyclerView.setAdapter(memberAdapter);

        coach_id=intent.getIntExtra("coachID",1);
        coach=LitePal.where("coachID = ?",coach_id+"").find(Coach.class).get(0);
        Toast.makeText(this,coach.toString(),Toast.LENGTH_LONG).show();

        coachName.setText(coach.getCoachName());
        coachGender.setText(coach.getGender());
        coachBirthday.setText(coach.getBirthdate().toString());
        coachID.setText(coach.getCoachID()+"");
        coachRank.check((coach.getCoachRank().equals("A"))?R.id.coach_rank_display_a:(coach.getCoachRank().equals("B")?R.id.coach_rank_display_b:R.id.coach_rank_display_c));
    }
    private int getCoachID(ArrayList<Coach> coachArrayList, String coachName){
        Iterator<Coach> coachIterator=coachArrayList.iterator();
        Coach coach;
        while(coachIterator.hasNext()){
            coach=coachIterator.next();
            if(coach.getCoachName().equals(coachName)){
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
}
