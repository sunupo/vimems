package com.vimems.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.vimems.R;
import com.vimems.bean.Admin;
import com.vimems.bean.Coach;
import com.vimems.mainactivity.AdminMainActivity;
import com.vimems.mainactivity.CoachMainActivity;

import java.util.Iterator;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import util.BaseActivity;
import util.InitBean;

/**
 * author sunupo
 * date 2019/1/19
 * description 用户登录activity
 */


public class LoginActivity extends BaseActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button login;
    private CheckBox savePassword;
    private String username;
    private String password;
    private final String address="www.vimems.com";
    private boolean loginFlag=false;
    /*    1、根据Context获取SharedPreferences对象　　2、利用edit()方法获取Editor对象。　　3、通过Editor对象存储key-value键值对数据。　  4、通过commit()方法提交数据。 */
    private SharedPreferences sp=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(InitBean.isInit==false){
            InitBean.initCoachItemList();
            InitBean.initMemberItemList();
            InitBean.initAdminList();
            InitBean.isInit=true;
        }

        editTextUsername=findViewById(R.id.username);
        editTextPassword=findViewById(R.id.password);
        savePassword=findViewById(R.id.save_password);

        sp = this.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        username = sp.getString("USERNAME", ""); //获取sp里面存储的数据
        password = sp.getString("PASSWORD","");
        if(sp.getBoolean("ISCHECKED",false)){
            savePassword.setChecked(true);
        }else{
            savePassword.setChecked(false);
        }
        editTextUsername.setText(username);//将sp中存储的username写入EditeText
        editTextPassword.setText(password);//将sp中存储的password写入EditeText


        login=findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                username=editTextUsername.getText().toString();
                password=editTextPassword.getText().toString();
                //sendRequestWithOkHttp(address,username,password);
               if(username.contains("adminLoginName")){
                   localAdminLogin(username,password);
               }else if(username.contains("coachLoginName")){
                   localCoachLogin(username,password);
               }else{
                   Toast.makeText(LoginActivity.this, "用户名密码错误！", Toast.LENGTH_LONG).show();
               }
            }
        });

    }
    //本地管理员登陆
    private void localAdminLogin(String username,String password){
        loginFlag=false;
        Iterator<Admin> adminIterator=InitBean.adminArrayList.iterator();
        String tempName;
        String tempPassword;
        Admin tempAdmin;
        while(adminIterator.hasNext()){
            tempAdmin=adminIterator.next();
            tempName=tempAdmin.getLoginName();
            tempPassword=tempAdmin.getAdminPassword();
            if(tempName.equals(username) && tempPassword.equals(password)){
                loginFlag=true;
                break;
            }
        }
        if (loginFlag) {
            if (savePassword.isChecked()) {
                SharedPreferences.Editor editor = sp.edit();
                //新建一个Editor对象来存储键值对用户名和密码
                editor.putString("USERNAME", editTextUsername.getText().toString());
                editor.putString("PASSWORD", editTextPassword.getText().toString());
                editor.putBoolean("ISCHECKED", savePassword.isChecked());
                //提交数据
                editor.commit();
            } else {
                sp.edit().remove("PASSWORD").commit();
                sp.edit().remove("ISCHECKED").commit();
            }
            Toast.makeText(LoginActivity.this, "登陆成功！", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(LoginActivity.this, AdminMainActivity.class);
            intent.putExtra("adminLoginName",username);
            LoginActivity.this.startActivity(intent);
        }else{
            Toast.makeText(LoginActivity.this, "用户名密码错误！", Toast.LENGTH_LONG).show();
        }

    }
    //本地教练登录
    private void localCoachLogin(String username,String password) {
        loginFlag=false;

        Iterator<Coach> coachIterator = InitBean.coachArrayList.iterator();
        String tempName;
        String tempPassword;
        Coach tempCoach;
        while (coachIterator.hasNext()) {
            tempCoach = coachIterator.next();
            tempName = tempCoach.getCoachLoginName();
            tempPassword = tempCoach.getLoginPWD();
            if (tempName.equals(username) && tempPassword.equals(password)) {
                loginFlag = true;
                break;
            }
        }
        if (loginFlag) {
            if (savePassword.isChecked()) {
                SharedPreferences.Editor editor = sp.edit();
                //新建一个Editor对象来存储键值对用户名和密码
                editor.putString("USERNAME", editTextUsername.getText().toString());
                editor.putString("PASSWORD", editTextPassword.getText().toString());
                editor.putBoolean("ISCHECKED", savePassword.isChecked());
                //提交数据
                editor.commit();
            } else {
                sp.edit().remove("PASSWORD").commit();
                sp.edit().remove("ISCHECKED").commit();
            }
            Toast.makeText(LoginActivity.this, "登陆成功！", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(LoginActivity.this, CoachMainActivity.class);
            intent.putExtra("coachLoginName",username);
            LoginActivity.this.startActivity(intent);
        }else{
            Toast.makeText(LoginActivity.this, "用户名密码错误！", Toast.LENGTH_LONG).show();
        }
    }
    /**
     * author sunupo
     * date 2019/1/19
     * description 向服务器发送okHttp请求，
     */
    private void sendRequestWithOkHttp(final String address,String username,String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            // 指定访问的服务器地址
                            .url(address)
                            .build();
                    //服务器返回json数据
                    Response response = client.newCall(request).execute();
                    String responseJsonData = response.body().string();
                    loginFlag=parseJSON(responseJsonData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private boolean parseJSON(String jsonData){
        //todo 解析服务器返回的json数据，判断是否为合法用户
        return true;
    };
    private void showResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 在这里进行UI操作，将结果显示到界面上
                //XXX.setText(response);
            }
        });
    }
}

