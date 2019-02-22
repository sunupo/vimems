package com.vimems.mainactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.vimems.R;

import util.BaseActivity;

public class SingleMultiplyaerModeSelecteActivity extends BaseActivity {

    private Button singleModeButton,multiplayerModeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_multiplyaer_mode_select);
        final Intent intent=getIntent();

        singleModeButton=findViewById(R.id.single_mode);
        multiplayerModeButton=findViewById(R.id.multiplayer_mode);

        singleModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(SingleMultiplyaerModeSelecteActivity.this,CoachMainActivity.class);
                intent1.putExtra("coachLoginName",intent.getStringExtra("coachLoginName"));
                startActivity(intent1);
            }
        });
        multiplayerModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(SingleMultiplyaerModeSelecteActivity.this,MultiplayerModeTrainingMainActivity.class);
                intent1.putExtra("coachLoginName",intent.getStringExtra("coachLoginName"));
                startActivity(intent1);
            }
        });
    }
}
