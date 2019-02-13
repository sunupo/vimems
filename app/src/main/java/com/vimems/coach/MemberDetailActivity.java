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

import static com.vimems.coach.CustomTrainingFragment.ARG_PAGE;

public class MemberDetailActivity extends BaseActivity {

    private CustomTrainingFragmentPageAdapter pageAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private int memberID;
    
    List<Fragment> fragmentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);

        memberID=getIntent().getIntExtra("MEMBER_ID",1);
        initFragmentList();

        FragmentManager fragmentManager=getSupportFragmentManager();
        pageAdapter=new CustomTrainingFragmentPageAdapter(fragmentManager,fragmentList);
        viewPager=findViewById(R.id.training_mode_viewpager);
        tabLayout=findViewById(R.id.training_mode_tab);
        viewPager.setAdapter(pageAdapter);
        //让TabLayout与viewpager产生联动
        tabLayout.setupWithViewPager(viewPager);
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
}
