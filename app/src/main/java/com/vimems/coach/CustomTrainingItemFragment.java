package com.vimems.coach;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vimems.Ble.AppBluetoothGatt.DeviceControlActivity;
import com.vimems.Ble.AppBluetoothGatt.GattPeripheralDetailActivity;
import com.vimems.R;
import com.vimems.bean.CustomMusclePara;
import com.vimems.mainactivity.SingleModeTrainingMainActivity;

import org.litepal.LitePal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.vimems.coach.CustomTrainingFragment.ARG_PAGE;
import static com.vimems.coach.CustomTrainingFragment.MEMBER_ID;
import static com.vimems.coach.CustomTrainingFragment.RECOVERY;
import static com.vimems.coach.CustomTrainingFragment.TRAINING_ITEM;
import static com.vimems.coach.MemberDetailActivity.deviceDefaultCustomNameMap;
import static com.vimems.coach.MemberDetailActivity.mBluetoothAdapter;
import static com.vimems.coach.MemberDetailActivity.memberIDDeviceMap;

/*
* 用来替代4个fragment的
* */
public class CustomTrainingItemFragment extends Fragment {

    public static final String CUSTOM_MUSCLE_PARA_LIST ="CUSTOM_MUSCLE_PARA_LIST" ;
    private int page;//page=trainingModeCode，就是自定义训练1、vip训练3
    private int trainingModuleCode;
    private int memberID;

    private TextView itemName;
    private LinearLayout lowFrequencyLinearLayout;//用来设置低频视图是否可见
    private LinearLayout highFrequencyLinearLayout;//用来设置高频视图是否可见
    private RadioGroup trainingItemLevelRadioGroup;
    private RadioButton aRadioButton,bRadioButton,cRadiobutton;//当为康复模式时需要更改radiobutton的显示文字为：放松、镇痛、损伤。

    private TextView lowFrequency,highFrequency,pulseWidth,pulsePeriod,intermittent,intensity;
    private Button lowFrequencyDecrease,lowFrequencyIncrease,
            highFrequencyDecrease,highFrequencyIncrease,
            pulseWidthDecrease,pulseWidthIncrease,
            pulsePeriodDecrease,pulsePeriodIncrease,
            intermittentDecrease,intermittentIncrease,
            intensityDecrease,intensityIncrease;

    private CheckBox shiftMuscleItemCheckedStateAll;
    private CheckBox xiongdaji,fuji,xiefangji,beikuoji,
            musclea,muscleb,musclec,muscled,musclee,musclef;
    private Button savePara,trainingStartup;

    private CustomMusclePara customMusclePara;

    private List<CheckBox> checkBoxList;
    private Set<CheckBox> checkedBoxSet=new HashSet<>();//状态为checked的checkbox列表

    public static Map<CheckBox,Integer> checkBoxIntegerMap;//每一个checkbox对应一个整数id
    public static Map<Integer,Integer> radioButtonIdIntegerMap;//每一个radiobutton对应一个整数id

    public static EditText customDeviceName;
    public static TextView deviceAddress;
    public static RadioGroup deviceState;
    private Button alterDeviceName;

    private SharedPreferences sharedPreferences;
    private  boolean flag;

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
        Bundle bundle=getArguments();
        page=bundle.getInt(ARG_PAGE);//page对应trainingModeCode，1自定义训练，3VIP训练
        trainingModuleCode=bundle.getInt(TRAINING_ITEM);
        memberID=bundle.getInt(MEMBER_ID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_custom_training_item,container,false);

        allFindViewById(view);
        initCheckBoxList();
        setCheckBoxButtonListener();
        setDecIncButtonOnClickListener();
        initCheckBoxIntegerMap();
        initRadioButtonIdIntegerMap();
        setLFHFVisibility();
        //判断是否保存过自定义参数
        // 是：则显示开始训练按钮
        sharedPreferences=getContext().getSharedPreferences("hasPara",Context.MODE_PRIVATE);
        flag=sharedPreferences.getBoolean(memberID+page+trainingModuleCode+"flag",false);
        if (flag){
            trainingStartup.setVisibility(View.VISIBLE);
        }
        return view;
    }
    /*改变康复模式下等级radiobutton的显示文字和如果是在vip训练模式下，显示低频和高频参数设置*/
    private void setLFHFVisibility(){
        if(page==3){
            lowFrequencyLinearLayout.setVisibility(View.VISIBLE);
            highFrequencyLinearLayout.setVisibility(View.VISIBLE);
        }
        else{
            lowFrequencyLinearLayout.setVisibility(View.GONE);
            highFrequencyLinearLayout.setVisibility(View.GONE);
        }
        if(trainingModuleCode==RECOVERY){
            aRadioButton.setText("放松");
            bRadioButton.setText("镇痛");
            cRadiobutton.setText("损伤");
        }
    }
    private void allFindViewById(View view){

        customDeviceName=view.findViewById(R.id.bind_member_custom_device_name);
        deviceAddress=view.findViewById(R.id.bind_member_default_device_address);
        deviceState=view.findViewById(R.id.bind_member_device_connect_state);
        alterDeviceName=view.findViewById(R.id.alter_bind_member_custom_device_name);

        itemName=view.findViewById(R.id.custom_training_options_item_name);
        lowFrequencyLinearLayout=view.findViewById(R.id.low_frequency);
        highFrequencyLinearLayout=view.findViewById(R.id.high_frequency);
        trainingItemLevelRadioGroup=view.findViewById(R.id.training_item_level_radio_group);
        aRadioButton=view.findViewById(R.id.training_item_level_a);
        bRadioButton=view.findViewById(R.id.training_item_level_b);
        cRadiobutton=view.findViewById(R.id.training_item_level_c);

        lowFrequency=view.findViewById(R.id.low_frequency_textview);
        highFrequency=view.findViewById(R.id.high_frequency_textview);
        pulseWidth=view.findViewById(R.id.pulse_width_textview);
        pulsePeriod=view.findViewById(R.id.pulse_period_textview);
        intermittent=view.findViewById(R.id.intermittent_period_textview);
        intensity=view.findViewById(R.id.training_muscle_intensity_textview);

        shiftMuscleItemCheckedStateAll=view.findViewById(R.id.muscle_item_checked_all);
        xiongdaji=view.findViewById(R.id.muscle_xiongdaji);
        fuji=view.findViewById(R.id.muscle_fuji);
        xiefangji=view.findViewById(R.id.muscle_xiefangji);
        beikuoji=view.findViewById(R.id.muscle_beikuoji);
        musclea=view.findViewById(R.id.muscle_a);
        muscleb=view.findViewById(R.id.muscle_b);
        musclec=view.findViewById(R.id.muscle_c);
        muscled=view.findViewById(R.id.muscle_d);
        musclee=view.findViewById(R.id.muscle_e);
        musclef=view.findViewById(R.id.muscle_f);
        savePara=view.findViewById(R.id.save_muscle_para);
        trainingStartup=view.findViewById(R.id.single_mode_training_startup);

        //decrease increase 按钮绑定
        lowFrequencyDecrease=view.findViewById(R.id.low_frequency_textview_decrease);
        lowFrequencyIncrease=view.findViewById(R.id.low_frequency_textview_increase);
        highFrequencyIncrease=view.findViewById(R.id.high_frequency_textview_increase);
        highFrequencyDecrease=view.findViewById(R.id.high_frequency_textview_decrease);
        pulseWidthIncrease=view.findViewById(R.id.pulse_width_textview_increase);
        pulseWidthDecrease=view.findViewById(R.id.pulse_width_textview_decrease);
        pulsePeriodDecrease=view.findViewById(R.id.pulse_period_textview_decrease);
        pulsePeriodIncrease=view.findViewById(R.id.pulse_period_textview_increase);
        intermittentDecrease=view.findViewById(R.id.intermittent_period_textview_decrease);
        intermittentIncrease=view.findViewById(R.id.intermittent_period_textview_increase);
        intensityDecrease=view.findViewById(R.id.intensity_textview_decrease);
        intensityIncrease=view.findViewById(R.id.intensity_textview_increase);
    }

    /**
     * 其他部位肌肉的全选列表
     */
    private void initCheckBoxList(){
        checkBoxList=new ArrayList<>();
//        checkBoxList.add(xiongdaji);
//        checkBoxList.add(fuji);
//        checkBoxList.add(beikuoji);
//        checkBoxList.add(xiefangji);
        checkBoxList.add(musclea);
        checkBoxList.add(muscleb);
        checkBoxList.add(musclec);
        checkBoxList.add(muscled);
        checkBoxList.add(musclee);
        checkBoxList.add(musclef);
    }

    private void setCheckBoxButtonListener(){
        customDeviceName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        alterDeviceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothDevice bluetoothDevice= memberIDDeviceMap.get(memberID);
                if(alterDeviceName.getText().equals("重命名")){
                    if(bluetoothDevice!=null){
                        customDeviceName.setEnabled(true);
                        alterDeviceName.setText("确认");
                    }
                    else{
                        Log.e("bluetoothDevice==null", "bluetoothDevice==null" );
                    }
                }else if((alterDeviceName.getText().equals("确认"))){
                    deviceDefaultCustomNameMap.put(bluetoothDevice,customDeviceName.getText().toString());
                    Log.d("deviceDefaultCustomNameMap.put(),重命名",
                            "<"+bluetoothDevice.getName()+","+customDeviceName.getText().toString()+">");
                    customDeviceName.setEnabled(false);
                    alterDeviceName.setText("重命名");
                }
            }
        });
        shiftMuscleItemCheckedStateAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    for (CheckBox checkBox:checkBoxList) {
                        checkBox.setChecked(true);
                    }
                }else{
                    for (CheckBox checkBox:checkBoxList) {
                        checkBox.setChecked(false);
                    }
                }
            }
        });
        setFrontBackMuscleCheckedChangeListener(xiongdaji);
        setFrontBackMuscleCheckedChangeListener(fuji);
        setFrontBackMuscleCheckedChangeListener(xiefangji);
        setFrontBackMuscleCheckedChangeListener(beikuoji);
        setOtherMuscleCheckedChangListener(musclea);
        setOtherMuscleCheckedChangListener(muscleb);
        setOtherMuscleCheckedChangListener(musclec);
        setOtherMuscleCheckedChangListener(muscled);
        setOtherMuscleCheckedChangListener(musclee);
        setOtherMuscleCheckedChangListener(musclef);
        savePara.setOnClickListener(new View.OnClickListener() {//把每一个肌肉的训练参数，保存到数据库中
            @Override
            public void onClick(View v) {
                Iterator<CheckBox> checkBoxIterator=checkedBoxSet.iterator();
                Boolean saveState=false;
                //<p>Returns the identifier of the selected radio button in this group.
                //     * Upon empty selection, the returned value is -1.</p>
                if(!checkBoxIterator.hasNext()){
                    Toast.makeText(v.getContext(),"请选择至少一处肌肉",Toast.LENGTH_SHORT).show();
                    return;
                }else if(trainingItemLevelRadioGroup.getCheckedRadioButtonId()==-1){
                    Toast.makeText(v.getContext(),"请选择等级",Toast.LENGTH_SHORT).show();
                    return;
                }
                while(checkBoxIterator.hasNext()){
                    int tempMuscleID;
                    tempMuscleID=checkBoxIntegerMap.get(checkBoxIterator.next());

                    customMusclePara=new CustomMusclePara();
                    customMusclePara.setMuscleID(tempMuscleID);
                    customMusclePara.setMemberID(memberID);
                    customMusclePara.setTrainingModeCode(page);
                    customMusclePara.setTrainingModuleCode(trainingModuleCode);
                    customMusclePara.setTrainingModuleLevel(radioButtonIdIntegerMap.get(trainingItemLevelRadioGroup.getCheckedRadioButtonId()));
                    customMusclePara.setLowFrequency(Integer.parseInt(lowFrequency.getText().toString()));
                    customMusclePara.setHighFrequency(Integer.parseInt(highFrequency.getText().toString()));
                    customMusclePara.setPulseWidth(Integer.parseInt(pulseWidth.getText().toString()));
                    customMusclePara.setPulsePeriod(Integer.parseInt(pulsePeriod.getText().toString()));
                    customMusclePara.setIntermittentPeriod(Integer.parseInt(intermittent.getText().toString()));
                    customMusclePara.setIntensity(Integer.parseInt(intensity.getText().toString()));
                    if(LitePal.where("muscleID = ? and memberID = ? and trainingModeCode = ? and trainingModuleCode = ?",
                            tempMuscleID+"",memberID+"",page+"",trainingModuleCode+"").find(CustomMusclePara.class).size()>0){
                        CustomMusclePara temp;
                        //根据这四个属性（四个主键）查询出来的最多只有一条数据
                        temp=LitePal.where("muscleID = ? and memberID = ? and trainingModeCode = ? and trainingModuleCode = ?",
                                tempMuscleID+"",memberID+"",page+"",trainingModuleCode+"")
                                .find(CustomMusclePara.class).get(0);
                        temp.setTrainingModuleLevel(radioButtonIdIntegerMap.get(trainingItemLevelRadioGroup.getCheckedRadioButtonId()));
                        temp.setLowFrequency(Integer.parseInt(lowFrequency.getText().toString()));
                        temp.setHighFrequency(Integer.parseInt(highFrequency.getText().toString()));
                        temp.setPulseWidth(Integer.parseInt(pulseWidth.getText().toString()));
                        temp.setPulsePeriod(Integer.parseInt(pulsePeriod.getText().toString()));
                        temp.setIntermittentPeriod(Integer.parseInt(intermittent.getText().toString()));
                        temp.setIntensity(Integer.parseInt(intensity.getText().toString()));

                        saveState=temp.save();
                        Log.d("查询到数据，更新数据 --> temp.save()",
                                "muscleID = "+tempMuscleID+"; memberID = "+memberID+ ";trainingModeCode ="+page+"; trainingModuleCode = "+trainingItemLevelRadioGroup+"更新成功？ "
                                        +saveState );
                    }else {
                        saveState=customMusclePara.save();
                        Toast.makeText(v.getContext(), "保存成功？" , Toast.LENGTH_SHORT);
                        Log.d("没有查询到数据，插入数据  --> customMusclePara.save()",
                                "muscleID = "+tempMuscleID+"; memberID = "+memberID+ ";trainingModeCode ="+page+"; trainingModuleCode = "+trainingItemLevelRadioGroup+"保存成功？"
                                        + saveState);
                    }
                }
                Toast.makeText(v.getContext(),"保存成功?"+saveState,Toast.LENGTH_SHORT).show();

                /**
                 * 判断以前是否保存过参数,决定是否显示开始训练按钮
                 */
                if(!sharedPreferences.getBoolean(memberID+page+trainingModuleCode+"flag",false)){
                    SharedPreferences.Editor editor=v.getContext().getSharedPreferences("hasPara",Context.MODE_PRIVATE).edit();
                    editor.putBoolean(memberID+page+trainingModuleCode+"flag",true);
                    editor.commit();
                    trainingStartup.setVisibility(View.VISIBLE);
                }else{
                    trainingStartup.setVisibility(View.VISIBLE);
                }

            }
        });
        trainingStartup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CustomMusclePara> customMuscleParaList=new ArrayList<>();
                initCheckBoxSet();
                Iterator<CheckBox> checkBoxIterator=checkedBoxSet.iterator();
                while(checkBoxIterator.hasNext()){

                    customMusclePara=new CustomMusclePara();
                    customMusclePara.setMuscleID(checkBoxIntegerMap.get(checkBoxIterator.next()));
                    customMusclePara.setMemberID(memberID);
                    customMusclePara.setTrainingModeCode(page);
                    customMusclePara.setTrainingModuleCode(trainingModuleCode);
                    customMusclePara.setTrainingModuleLevel(radioButtonIdIntegerMap.get(trainingItemLevelRadioGroup.getCheckedRadioButtonId()));
                    customMusclePara.setLowFrequency(Integer.parseInt(lowFrequency.getText().toString()));
                    customMusclePara.setHighFrequency(Integer.parseInt(highFrequency.getText().toString()));
                    customMusclePara.setPulseWidth(Integer.parseInt(pulseWidth.getText().toString()));
                    customMusclePara.setPulsePeriod(Integer.parseInt(pulsePeriod.getText().toString()));
                    customMusclePara.setIntermittentPeriod(Integer.parseInt(intermittent.getText().toString()));
                    customMusclePara.setIntensity(Integer.parseInt(intensity.getText().toString()));

                    customMuscleParaList.add(customMusclePara);
                }
                Intent intent=new Intent(v.getContext(),SingleModeTrainingMainActivity.class);
                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, customDeviceName.getText().toString());
                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, deviceAddress.getText().toString());

                Bundle bundle=new Bundle();
                bundle.putSerializable(CUSTOM_MUSCLE_PARA_LIST,(Serializable)customMuscleParaList);
                //bundle.putSerializable("xiongdaji",customMuscleParaList.get(0));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    /**
     * 正面背面肌肉checkbox，checked状态改变的监听器
     * checkedchanglistener
     */
    private void setFrontBackMuscleCheckedChangeListener(CheckBox checkBox){
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.equals(xiongdaji)||buttonView.equals(fuji)){
                    if(isChecked){
                        if(!checkedBoxSet.contains(buttonView)){
                            checkedBoxSet.add((CheckBox) buttonView);
                        }
                        beikuoji.setChecked(false);
                        xiefangji.setChecked(false);
//                        checkBox.setChexked(false);应该是能触发监听事件onCheckedChanged
//                        这儿判断一下，保险起见
//                        if(checkedBoxSet.contains(beikuoji)){
//                            checkedBoxSet.remove(beikuoji);
//                        }
//                        if(checkedBoxSet.contains(xiefangji)){
//                            checkedBoxSet.remove(xiefangji);
//                        }
                    }
                }
                if (buttonView.equals(xiefangji)||buttonView.equals(beikuoji)){
                    if(isChecked){
                        if(!checkedBoxSet.contains(buttonView)){
                            checkedBoxSet.add((CheckBox) buttonView);
                        }
                        xiongdaji.setChecked(false);
                        fuji.setChecked(false);
//                        checkBox.setChexked(false);应该是能触发监听事件onCheckedChanged
//                        这儿判断一下，保险起见
//                        if(checkedBoxSet.contains(xiongdaji)){
//                            checkedBoxSet.remove(xiongdaji);
//                        }
//                        if(checkedBoxSet.contains(fuji)){
//                            checkedBoxSet.remove(fuji);
//                        }
                    }
                }
            }
        });
    }
    private void setOtherMuscleCheckedChangListener(CheckBox checkBox){
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    if(!checkedBoxSet.contains(buttonView)){
//                        checkedBoxSet.add((CheckBox)buttonView);
//                    }
//                }
//                if(!isChecked){
//                    checkedBoxSet.remove(buttonView);
//                }
//                else if(!isChecked && checkedBoxSet.contains(buttonView)){
//                    checkedBoxSet.remove(buttonView);
//                }
            }
        });
    }
    private void setDecIncButtonOnClickListener(){

        /*映射Button和TextView的map*/
        Map<Button,TextView> decreaseButtonTextViewMap=new HashMap<>();
        Map<Button,TextView> increaseButtonTextViewMap=new HashMap<>();

        /*参数最小值与最大值*/
        int[] minValues={0,2000,0,1,0,0};
        int[] maxValue={150,10000,100,30,30,100};

        decreaseButtonTextViewMap.put(lowFrequencyDecrease,lowFrequency);
        decreaseButtonTextViewMap.put(highFrequencyDecrease,highFrequency);
        decreaseButtonTextViewMap.put(pulseWidthDecrease,pulseWidth);
        decreaseButtonTextViewMap.put(pulsePeriodDecrease,pulsePeriod);
        decreaseButtonTextViewMap.put(intermittentDecrease,intermittent);
        decreaseButtonTextViewMap.put(intensityDecrease,intensity);

        increaseButtonTextViewMap.put(lowFrequencyIncrease,lowFrequency);
        increaseButtonTextViewMap.put(highFrequencyIncrease,highFrequency);
        increaseButtonTextViewMap.put(pulseWidthIncrease,pulseWidth);
        increaseButtonTextViewMap.put(pulsePeriodIncrease,pulsePeriod);
        increaseButtonTextViewMap.put(intermittentIncrease,intermittent);
        increaseButtonTextViewMap.put(intensityIncrease,intensity);

        setDecreaseButtonOnClickListener(lowFrequencyDecrease,lowFrequency,minValues[0]);
        setDecreaseButtonOnClickListener(highFrequencyDecrease,highFrequency,minValues[1]);
        setDecreaseButtonOnClickListener(pulseWidthDecrease,pulseWidth,minValues[2]);
        setDecreaseButtonOnClickListener(pulsePeriodDecrease,pulsePeriod,minValues[3]);
        setDecreaseButtonOnClickListener(intermittentDecrease,intermittent,minValues[4]);
        setDecreaseButtonOnClickListener(intensityDecrease,intensity,minValues[5]);

        setIncreaseButtonOnClickListener(lowFrequencyIncrease,lowFrequency,maxValue[0]);
        setIncreaseButtonOnClickListener(highFrequencyIncrease,highFrequency,maxValue[1]);
        setIncreaseButtonOnClickListener(pulseWidthIncrease,pulseWidth,maxValue[2]);
        setIncreaseButtonOnClickListener(pulsePeriodIncrease,pulsePeriod,maxValue[3]);
        setIncreaseButtonOnClickListener(intermittentIncrease,intermittent,maxValue[4]);
        setIncreaseButtonOnClickListener(intensityIncrease,intensity,maxValue[5]);
    }
    private void setDecreaseButtonOnClickListener(Button button, final TextView textView, final int minValue){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentValue=Integer.parseInt(textView.getText().toString());
                if(currentValue>minValue){
                    textView.setText((currentValue-1)+"");
                }else{
                    Toast.makeText(getContext(),"已经是最小值了",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void setIncreaseButtonOnClickListener(Button button, final TextView textView, final int maxValue){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentValue=Integer.parseInt(textView.getText().toString());
                if(currentValue<maxValue){
                    textView.setText((currentValue+1)+"");
                }else{
                    Toast.makeText(getContext(),"已达到最大值",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void initCheckBoxIntegerMap(){
        checkBoxIntegerMap=new HashMap<>();
        checkBoxIntegerMap.put(xiongdaji,1);
        checkBoxIntegerMap.put(fuji,2);
        checkBoxIntegerMap.put(xiefangji,3);
        checkBoxIntegerMap.put(beikuoji,4);
        checkBoxIntegerMap.put(musclea,5);
        checkBoxIntegerMap.put(muscleb,6);
        checkBoxIntegerMap.put(musclec,7);
        checkBoxIntegerMap.put(muscled,8);
        checkBoxIntegerMap.put(musclee,9);
        checkBoxIntegerMap.put(musclef,10);
    }
    private void initRadioButtonIdIntegerMap(){
        radioButtonIdIntegerMap=new HashMap<>();
        radioButtonIdIntegerMap.put(R.id.training_item_level_a,1);
        radioButtonIdIntegerMap.put(R.id.training_item_level_b,2);
        radioButtonIdIntegerMap.put(R.id.training_item_level_c,3);

    }
    private void initCheckBoxSet(){
        checkedBoxSet.clear();
        if(xiongdaji.isChecked()){
            checkedBoxSet.add(xiongdaji);
        }
        if(fuji.isChecked()){
            checkedBoxSet.add(fuji);
        }
        if(xiefangji.isChecked()){
            checkedBoxSet.add(xiefangji);
        }
        if(beikuoji.isChecked()){
            checkedBoxSet.add(beikuoji);
        }
        if(musclea.isChecked()){
            checkedBoxSet.add(musclea);
        }
        if(muscleb.isChecked()){
            checkedBoxSet.add(muscleb);
        }
        if(musclec.isChecked()){
            checkedBoxSet.add(musclec);
        }
        if(muscled.isChecked()){
            checkedBoxSet.add(muscled);
        }
        if(musclee.isChecked()){
            checkedBoxSet.add(musclee);
        }
        if(musclef.isChecked()){
            checkedBoxSet.add(musclef);
        }

    }
}
