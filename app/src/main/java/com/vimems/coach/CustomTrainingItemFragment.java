package com.vimems.coach;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.vimems.R;

import static com.vimems.coach.CustomTrainingFragment.ARG_PAGE;

/*
* 用来替代4个fragment的
* */
public class CustomTrainingItemFragment extends Fragment {

    private int page;

    private TextView itemName;
    LinearLayout lowFrequencyLinearLayout;//用来设置低频视图是否可见
    LinearLayout highFrequencyLinearLayout;//用来设置高频视图是否可见
    RadioButton aRadioButton,bRadioButton,cRadiobutton;//当为康复模式时需要更改radiobutton的显示文字为：放松、镇痛、损伤。

    public static CustomTrainingItemFragment newInstance(int page){

        Bundle bundle=new Bundle();
        bundle.putInt(ARG_PAGE,page);
        CustomTrainingItemFragment customTrainingItemFragment=new CustomTrainingItemFragment();
        customTrainingItemFragment.setArguments(bundle);
        return customTrainingItemFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page=getArguments().getInt(ARG_PAGE);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_custom_training_item,container,false);
        itemName=view.findViewById(R.id.custom_training_options_item_name);
        lowFrequencyLinearLayout=view.findViewById(R.id.low_frequency);
        highFrequencyLinearLayout=view.findViewById(R.id.high_frequency);
        aRadioButton=view.findViewById(R.id.training_item_level_a);
        bRadioButton=view.findViewById(R.id.training_item_level_b);
        cRadiobutton=view.findViewById(R.id.training_item_level_c);
        if(page==3){
            lowFrequencyLinearLayout.setVisibility(View.VISIBLE);
            highFrequencyLinearLayout.setVisibility(View.VISIBLE);
        }
        return view;

    }
}
