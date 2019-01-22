package com.vimems.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vimems.R;
import com.vimems.bean.Coach;

import util.BaseActivity;

public class AddCoach extends BaseActivity {
    private Coach tempCoach;

    private TextView coachID;
    private TextView adminID;
    private TextView imageID;
    private  EditText coachName;
    private  EditText coachLoginName;
    private  EditText loginPassword;
    private  EditText gender;
    private  TextView birthdate;
    private  EditText coachRank;

    private Button addCoachSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_coach);

        coachID=findViewById(R.id.add_coach_ID);

        adminID=findViewById(R.id.add_admin_ID);
        addCoachSubmit=findViewById(R.id.add_coach_submit);
    }
}
