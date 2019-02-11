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
    private int mPage;

    private RadioGroup radioGroup;
    private CustomTrainingGainMuscleFragment customTrainingGainMuscleFragment;
    private CustomTrainingLoseFatFragment customTrainingLoseFatFragment;
    private CustomTrainingShapeFragment customTrainingShapeFragment;
    private  CustomTrainingRecoveryFragment customTrainingRecoveryFragment;

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
        mPage=getArguments().getInt(ARG_PAGE);


        customTrainingGainMuscleFragment=new CustomTrainingGainMuscleFragment();
        customTrainingLoseFatFragment=new CustomTrainingLoseFatFragment();
        customTrainingShapeFragment=new CustomTrainingShapeFragment();
        customTrainingRecoveryFragment=new CustomTrainingRecoveryFragment();




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
    private void replaceFragment(int checkedId){

        FragmentManager fragmentManager=getChildFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        switch (checkedId){
            case R.id.custom_training_options_gain_muscle:
                fragmentTransaction.replace(R.id.custom_training_options_fragment,customTrainingGainMuscleFragment);
                break;
            case R.id.custom_training_options_lose_fat:
                fragmentTransaction.replace(R.id.custom_training_options_fragment,customTrainingLoseFatFragment);
                break;
            case R.id.custom_training_options_shape:
                fragmentTransaction.replace(R.id.custom_training_options_fragment,customTrainingShapeFragment);
                break;
            case R.id.custom_training_options_recovery:
                fragmentTransaction.replace(R.id.custom_training_options_fragment,customTrainingRecoveryFragment);
                break;
            default:
                break;
        }
        fragmentTransaction.commit();
    }
    private void replaceFragmentItem(int checkedId){

        FragmentManager fragmentManager=getChildFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        switch (checkedId){
            case R.id.custom_training_options_gain_muscle:
                fragmentTransaction.replace(R.id.custom_training_options_fragment,CustomTrainingItemFragment.newInstance(mPage));
                break;
            case R.id.custom_training_options_lose_fat:
                fragmentTransaction.replace(R.id.custom_training_options_fragment,CustomTrainingItemFragment.newInstance(mPage));
                break;
            case R.id.custom_training_options_shape:
                fragmentTransaction.replace(R.id.custom_training_options_fragment,CustomTrainingItemFragment.newInstance(mPage));
                break;
            case R.id.custom_training_options_recovery:
                fragmentTransaction.replace(R.id.custom_training_options_fragment,CustomTrainingItemFragment.newInstance(mPage));
                break;
            default:
                break;
        }
        fragmentTransaction.commit();
    }
}
