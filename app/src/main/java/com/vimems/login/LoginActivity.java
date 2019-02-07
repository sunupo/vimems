package com.vimems.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.vimems.R;
import com.vimems.bean.Admin;
import com.vimems.bean.Coach;
import com.vimems.bean.Member;
import com.vimems.mainactivity.AdminMainActivity;
import com.vimems.mainactivity.CoachMainActivity;

import org.litepal.LitePal;

import java.util.ArrayList;
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
    private SharedPreferences sp=null;

    private boolean firstBootstrap=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = this.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);


     /*   InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);*/

        setContentView(R.layout.activity_login);

        firstBootstrap=sp.getBoolean("firstBootstrap",true);
        if(firstBootstrap){
            //第一次安装时，初始化Admin、Coach、Member数据库表，初始化InitBean.adminArrayList等
            LitePal.getDatabase();
            InitBean.initLitePalTable();
            Toast.makeText(this,"database has been initialed ",Toast.LENGTH_SHORT).show();

            SharedPreferences.Editor editor = sp.edit();
            //新建一个Editor对象
            editor.putBoolean("firstBootstrap",false);
            //提交数据
            editor.commit();
        }else{
            //数据库已经创建成功，初始化列表
            InitBean.adminArrayList= (ArrayList<Admin>) LitePal.findAll(Admin.class);
            InitBean.coachArrayList=(ArrayList<Coach>)LitePal.findAll(Coach.class);
            InitBean.memberArrayList=(ArrayList<Member>)LitePal.findAll(Member.class);
        }

        editTextUsername=findViewById(R.id.username);
        editTextPassword=findViewById(R.id.password);
        savePassword=findViewById(R.id.save_password);

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

        Toast.makeText(this,"用户名为："+LitePal.find(Admin.class,Integer.parseInt(username.substring(username.length()-1,username.length()))+1).getAdminName(),Toast.LENGTH_SHORT).show();


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
//        Toast.makeText(this,"localCoachLogin"+LitePal.findAll(Coach.class).size(),Toast.LENGTH_SHORT).show();
        Toast.makeText(this,"用户名为："+LitePal.find(Coach.class,Integer.parseInt(username.substring(username.length()-1,username.length()))+1).getCoachName()+"总coach数目"+InitBean.coachArrayList.size(),Toast.LENGTH_SHORT).show();

        Iterator<Coach> coachIterator = InitBean.coachArrayList.iterator();
        String tempName;
        String tempPassword;
        Coach tempCoach;
        while (coachIterator.hasNext()) {
            tempCoach = coachIterator.next();
            tempName = tempCoach.getCoachLoginName();
            tempPassword = tempCoach.getLoginPassword();
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

