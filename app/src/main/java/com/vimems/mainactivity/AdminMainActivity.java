package com.vimems.mainactivity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.vimems.Adapter.CoachAdapter;
import com.vimems.AdapterItem.CoachItem;
import com.vimems.R;

import java.util.ArrayList;

import util.BaseActivity;

public class AdminMainActivity extends BaseActivity {
    private ArrayList<CoachItem> coachItemArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        initCoachItemList();
        RecyclerView recyclerView=findViewById(R.id.coach_recycler_view);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        CoachAdapter coachAdapter=new CoachAdapter(coachItemArrayList);
        recyclerView.setAdapter(coachAdapter);
    }
    private void initCoachItemList(){
        for(int i=0;i<2;i++){
            CoachItem zhangsan=new CoachItem("zhangsan",R.drawable.ic_launcher_background);
            coachItemArrayList.add(zhangsan);
            CoachItem lisi=new CoachItem("lisi",R.drawable.ic_launcher_background);
            coachItemArrayList.add(lisi);
            CoachItem wangwu=new CoachItem("wangwu",R.drawable.ic_launcher_background);
            coachItemArrayList.add(wangwu);
            CoachItem zhao=new CoachItem("zhao",R.drawable.ic_launcher_background);
            coachItemArrayList.add(zhao);
        }
    }
}
