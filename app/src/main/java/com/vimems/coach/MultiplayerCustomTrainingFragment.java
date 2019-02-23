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
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.vimems.R;

import static util.Constants.ARG_PAGE;
import static util.Constants.MEMBER_ID;
import static util.Constants.TRAINING_ITEM;
import static util.Constants.TRAINING_MODE_CODE;


public class MultiplayerCustomTrainingFragment extends Fragment {

    private int trainingModeCode;
    private int memberID;
    public static final int GAIN_MUSCLE=1;
    public static final int LOSE_FAT=2;
    public static final int SHAPE=3;
    public static final int RECOVERY=4;

    private RadioGroup moduleRadioGroup;
    private RadioGroup trainingItemLevelRadioGroup;
    private RadioButton aRadioButton,bRadioButton,cRadiobutton;//当为康复模式时需要更改radiobutton的显示文字为：放松、镇痛、损伤。
    private TextView lowFrequency,highFrequency,pulseWidth,pulsePeriod,intermittent,intensity;
    private Button lowFrequencyDecrease,lowFrequencyIncrease,
            highFrequencyDecrease,highFrequencyIncrease,
            pulseWidthDecrease,pulseWidthIncrease,
            pulsePeriodDecrease,pulsePeriodIncrease,
            intermittentDecrease,intermittentIncrease,
            intensityDecrease,intensityIncrease;

    private CustomTrainingItemFragment customTrainingItemFragment;

    public static MultiplayerCustomTrainingFragment newInstance(int trainingModeCode){

        Bundle bundle=new Bundle();
//        bundle.putInt(TRAINING_MODE_CODE,trainingModeCode);
        MultiplayerCustomTrainingFragment multiplayerCustomTrainingFragment=new MultiplayerCustomTrainingFragment();
        multiplayerCustomTrainingFragment.setArguments(bundle);
        return multiplayerCustomTrainingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle;
        bundle=getArguments();
        trainingModeCode=bundle.getInt(TRAINING_MODE_CODE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_multiplayer_custom_training,container,false);
        moduleRadioGroup=view.findViewById(R.id.custom_training_module_radio_group);

//        replaceFragmentItem(moduleRadioGroup.getCheckedRadioButtonId());

        moduleRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                replaceFragmentItem(checkedId);
            }
        });
        return view;
    }
//    private void replaceFragmentItem(int checkedId){
//
//        FragmentManager fragmentManager=getChildFragmentManager();
//        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
//        Bundle bundle1,bundle2,bundle3,bundle4;
//        CustomTrainingItemFragment customTrainingItemFragment1,customTrainingItemFragment2,customTrainingItemFragment3,customTrainingItemFragment4;
//        switch (checkedId){
//            case R.id.custom_training_options_gain_muscle:
//                customTrainingItemFragment1=new CustomTrainingItemFragment();
//                bundle1=new Bundle();
//                bundle1.putInt(ARG_PAGE,mPage);
//                bundle1.putInt(TRAINING_ITEM,GAIN_MUSCLE);
//                bundle1.putInt(MEMBER_ID,memberID);
//                customTrainingItemFragment1.setArguments(bundle1);
//                fragmentTransaction.replace(R.id.custom_training_options_fragment,customTrainingItemFragment1);                break;
//            case R.id.custom_training_options_lose_fat:
//                customTrainingItemFragment2=new CustomTrainingItemFragment();
//                bundle2=new Bundle();
//                bundle2.putInt(ARG_PAGE,mPage);
//                bundle2.putInt(TRAINING_ITEM,LOSE_FAT);
//                bundle2.putInt(MEMBER_ID,memberID);
//                customTrainingItemFragment2.setArguments(bundle2);
//                fragmentTransaction.replace(R.id.custom_training_options_fragment,customTrainingItemFragment2);                 break;
//            case R.id.custom_training_options_shape:
//                customTrainingItemFragment3=new CustomTrainingItemFragment();
//                bundle3=new Bundle();
//                bundle3.putInt(ARG_PAGE,mPage);
//                bundle3.putInt(TRAINING_ITEM,SHAPE);
//                bundle3.putInt(MEMBER_ID,memberID);
//                customTrainingItemFragment3.setArguments(bundle3);
//                fragmentTransaction.replace(R.id.custom_training_options_fragment,customTrainingItemFragment3);                 break;
//            case R.id.custom_training_options_recovery:
//                customTrainingItemFragment4=new CustomTrainingItemFragment();
//                bundle4=new Bundle();
//                bundle4.putInt(ARG_PAGE,mPage);
//                bundle4.putInt(TRAINING_ITEM,RECOVERY);
//                bundle4.putInt(MEMBER_ID,memberID);
//                customTrainingItemFragment4.setArguments(bundle4);
//                fragmentTransaction.replace(R.id.custom_training_options_fragment,customTrainingItemFragment4);
//                break;
//            default:
//                break;
//        }
//        fragmentTransaction.commit();
//    }
}
