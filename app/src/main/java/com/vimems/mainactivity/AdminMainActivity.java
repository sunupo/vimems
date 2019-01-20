package com.vimems.mainactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.vimems.Adapter.CoachAdapter;
import com.vimems.R;
import com.vimems.admin.AddCoach;

import util.BaseActivity;
import util.InitBean;

public class AdminMainActivity extends BaseActivity {

    private Button addCoach;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        if(InitBean.isInit==false){
            InitBean.initCoachItemList();
            InitBean.initMemberItemList();
            InitBean.initAdminList();
            InitBean.isInit=true;
        }

        RecyclerView recyclerView=findViewById(R.id.coach_recycler_view);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        CoachAdapter coachAdapter=new CoachAdapter(InitBean.coachItemArrayList);
        recyclerView.setAdapter(coachAdapter);
        addCoach=findViewById(R.id.add_coach);
        addCoach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminMainActivity.this, AddCoach.class);
                startActivity(intent);
                Toast.makeText(v.getContext(),"添加教练",Toast.LENGTH_LONG).show();
            }
        });
    }

}
