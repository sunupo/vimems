package com.vimems.admin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.vimems.R;
import com.vimems.bean.Coach;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Date;

import util.BaseActivity;
import util.InitBean;

public class AddCoach extends BaseActivity implements DatePickerDialog.OnDateSetListener {
    private Coach tempCoach;

    private TextView coachID;
    private TextView adminID;
    private TextView imageID;
    private  EditText coachName;
    private  EditText coachLoginName;
    private  EditText loginPassword;
    private  RadioGroup gender;
    private  TextView birthdate;
    private RadioGroup coachRank;

    private DatePickerDialog datePickerDialog;
    private int year,month,dayOfMonth;

    private Button addCoachSubmit;

    private String genderStr="male";
    private String rankStr="C";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_coach);

        coachID=findViewById(R.id.add_coach_ID);
        adminID=findViewById(R.id.add_admin_ID);
        imageID=findViewById(R.id.add_image_ID);
        coachName=findViewById(R.id.add_coach_name);
        coachLoginName=findViewById(R.id.add_coach_login_name);
        loginPassword=findViewById(R.id.add_coach_login_password);
        gender=findViewById(R.id.group_add_coach_gender);
        birthdate=findViewById(R.id.add_coach_birthday);
        coachRank=findViewById(R.id.group_add_coach_rank);
        addCoachSubmit=findViewById(R.id.add_coach_submit);

        Intent intent=getIntent();
        final Bundle bundle=intent.getExtras();
        coachID.setText(bundle.getInt("coachID")+"");
        adminID.setText(bundle.getInt("adminID")+"");

        birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.add_coach_gender_male:
                        genderStr="male";
                        break;
                    case R.id.add_coach_gender_female:
                        genderStr="female";
                        break;
                        default:
                            genderStr="male";
                            break;

                }
            }
        });
        coachRank.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.add_coach_rank_one:
                        rankStr="A";
                        break;
                    case R.id.add_coach_rank_two:
                        rankStr="B";
                        break;
                    case R.id.add_coach_rank_three:
                        rankStr="C";
                        break;
                        default:rankStr="C";break;
                }
            }
        });

        addCoachSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Coach coach=new Coach();
                coach.setCoachID(bundle.getInt("coachID"));
                coach.setImageID(R.drawable.ic_launcher_background);
                coach.setAdminID(bundle.getInt("adminID"));
                coach.setCoachName(coachName.getText().toString());
                coach.setCoachLoginName(coachLoginName.getText().toString());
                coach.setLoginPassword(loginPassword.getText().toString());
                coach.setGender(genderStr);
                coach.setBirthdate(new Date());
                coach.setCoachRank(rankStr);
                Toast.makeText(AddCoach.this, "保存成功？"+coach.save(), Toast.LENGTH_SHORT).show();
                //coach.save();
                InitBean.coachArrayList=(ArrayList<Coach>)LitePal.findAll(Coach.class);
            }
            /*
            * coaches[i]=new Coach();
            coaches[i].setCoachID(i);
            coaches[i].setAdminID(i/3);
            coaches[i].setImageID(R.drawable.ic_launcher_foreground);
            coaches[i].setCoachName("coachName"+i);
            coaches[i].setCoachLoginName("coachLoginName"+i);
            coaches[i].setLoginPassword("123456");
            coaches[i].setGender((i%2==0)?"male":"female");
            coaches[i].setBirthdate(new Date());
            coaches[i].setCoachRank((i%3==0)?"A":(i%3==1)?"B":"C");
            coaches[i].save();*/
        });



        datePickerDialog=new DatePickerDialog(this,this,2019,0,1);
        datePickerDialog.setIcon(R.drawable.ic_launcher);
        datePickerDialog.setTitle("选择日期");
        datePickerDialog.setMessage("选择出生日期");
        datePickerDialog.setCancelable(false);

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        this.year=year;
        this.month=month;
        this.dayOfMonth=dayOfMonth;
        birthdate.setText(year+"年"+(month+1)+"月"+dayOfMonth+"日");
    }
}
