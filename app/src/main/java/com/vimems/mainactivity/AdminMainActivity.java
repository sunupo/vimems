package com.vimems.mainactivity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.vimems.Adapter.CoachAdapter;
import com.vimems.R;

import util.BaseActivity;
import util.InitBean;

public class AdminMainActivity extends BaseActivity {

    private Button addCoach;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        InitBean.initCoachItemList();
        InitBean.initMemberItemList();
        RecyclerView recyclerView=findViewById(R.id.coach_recycler_view);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        CoachAdapter coachAdapter=new CoachAdapter(InitBean.coachItemArrayList);
        recyclerView.setAdapter(coachAdapter);
        addCoach=findViewById(R.id.add_coach);
        addCoach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),InitBean.getCoachMemberNum(2,InitBean.memberArrayList)+"个",Toast.LENGTH_LONG).show();
                Log.d("coachid=",InitBean.getCoachMemberNum(2,InitBean.memberArrayList)+""+InitBean.memberArrayList.size());
            }
        });
    }

}
