package com.vimems.coach;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.widget.TableLayout;

import com.vimems.Adapter.CustomTrainingFragmentPageAdapter;
import com.vimems.R;

import java.util.ArrayList;
import java.util.List;

import util.BaseActivity;

public class MemberDetailActivity extends BaseActivity {

    private CustomTrainingFragmentPageAdapter pageAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    
    List<Fragment> fragmentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);

        // TODO: 2/10/2019 暂时用一个fragment代替
        fragmentList=new ArrayList<>();
        fragmentList.add(CustomTrainingFragment.newInstance(1));
        fragmentList.add(new VideoTrainingFragment());
        fragmentList.add(CustomTrainingFragment.newInstance(3));
        FragmentManager fragmentManager=getSupportFragmentManager();
        pageAdapter=new CustomTrainingFragmentPageAdapter(fragmentManager,fragmentList);
        viewPager=findViewById(R.id.training_mode_viewpager);
        tabLayout=findViewById(R.id.training_mode_tab);
        viewPager.setAdapter(pageAdapter);
        //让TabLayout与viewpager产生联动
        tabLayout.setupWithViewPager(viewPager);
    }
}
