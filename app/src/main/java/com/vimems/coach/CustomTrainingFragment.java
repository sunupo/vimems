package com.vimems.coach;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.vimems.R;

public class CustomTrainingFragment extends Fragment {
    public static final String ARG_PAGE="ARG_PAGE";
    public static final String TRAINING_ITEM="TRAINING_ITEM";
    public static final String MEMBER_ID="MEMBER_ID";
    private int mPage;
    private int memberID;
    public static final int GAIN_MUSCLE=1;
    public static final int LOSE_FAT=2;
    public static final int SHAPE=3;
    public static final int RECOVERY=4;


    private RadioGroup radioGroup;
    private CustomTrainingItemFragment customTrainingItemFragment;

    //使用newInstance的方式，或者在CustomTrainingFragmentPageAdapter中添加一个列表
//    fragments=new ArrayList<>();
//		//将提前写好三个Fragment添加到集合中
//        fragments.add(new FirstFragment());
//        fragments.add(new SecondFragment());
//        fragments.add(new ThirdFragment());
//    在适配器的构造方法中传入参数fragmentManage、fragments
//    在适配器的getItem方法中return fragments.get(position)
    public static CustomTrainingFragment newInstance(int page){

        Bundle bundle=new Bundle();
        bundle.putInt(ARG_PAGE,page);
        CustomTrainingFragment customTrainingFragment=new CustomTrainingFragment();
        customTrainingFragment.setArguments(bundle);
        return customTrainingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle;
        bundle=getArguments();
        mPage=bundle.getInt(ARG_PAGE);
        memberID=bundle.getInt(MEMBER_ID);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_custom_training,container,false);
        radioGroup=view.findViewById(R.id.custom_training_options_radio_group);
//        replaceFragment(radioGroup.getCheckedRadioButtonId());
        replaceFragmentItem(radioGroup.getCheckedRadioButtonId());

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                replaceFragmentItem(checkedId);
            }
        });
        return view;
    }

    private void replaceFragmentItem(int checkedId){

        FragmentManager fragmentManager=getChildFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        Bundle bundle1,bundle2,bundle3,bundle4;
        CustomTrainingItemFragment customTrainingItemFragment1,customTrainingItemFragment2,customTrainingItemFragment3,customTrainingItemFragment4;
        switch (checkedId){
            case R.id.custom_training_options_gain_muscle:
                customTrainingItemFragment1=new CustomTrainingItemFragment();
                bundle1=new Bundle();
                bundle1.putInt(ARG_PAGE,mPage);
                bundle1.putInt(TRAINING_ITEM,GAIN_MUSCLE);
                bundle1.putInt(MEMBER_ID,memberID);
                customTrainingItemFragment1.setArguments(bundle1);
                fragmentTransaction.replace(R.id.custom_training_options_fragment,customTrainingItemFragment1);                break;
            case R.id.custom_training_options_lose_fat:
                customTrainingItemFragment2=new CustomTrainingItemFragment();
                bundle2=new Bundle();
                bundle2.putInt(ARG_PAGE,mPage);
                bundle2.putInt(TRAINING_ITEM,LOSE_FAT);
                bundle2.putInt(MEMBER_ID,memberID);
                customTrainingItemFragment2.setArguments(bundle2);
                fragmentTransaction.replace(R.id.custom_training_options_fragment,customTrainingItemFragment2);                 break;
            case R.id.custom_training_options_shape:
                customTrainingItemFragment3=new CustomTrainingItemFragment();
                bundle3=new Bundle();
                bundle3.putInt(ARG_PAGE,mPage);
                bundle3.putInt(TRAINING_ITEM,SHAPE);
                bundle3.putInt(MEMBER_ID,memberID);
                customTrainingItemFragment3.setArguments(bundle3);
                fragmentTransaction.replace(R.id.custom_training_options_fragment,customTrainingItemFragment3);                 break;
            case R.id.custom_training_options_recovery:
                customTrainingItemFragment4=new CustomTrainingItemFragment();
                bundle4=new Bundle();
                bundle4.putInt(ARG_PAGE,mPage);
                bundle4.putInt(TRAINING_ITEM,RECOVERY);
                bundle4.putInt(MEMBER_ID,memberID);
                customTrainingItemFragment4.setArguments(bundle4);
                fragmentTransaction.replace(R.id.custom_training_options_fragment,customTrainingItemFragment4);
                break;
            default:
                break;
        }
        fragmentTransaction.commit();
    }
}
