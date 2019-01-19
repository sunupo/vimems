package com.vimems.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.vimems.R;
import com.vimems.mainactivity.AdminMainActivity;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import util.BaseActivity;

/**
 * author sunupo
 * date 2019/1/19
 * description 用户登录activity
 */


public class LoginActivity extends BaseActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button login;
    private String username;
    private String password;
    private final String address="www.vimems.com";
    private boolean loginFlag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername=(EditText)findViewById(R.id.username);
        editTextPassword=(EditText)findViewById(R.id.password);
        login=(Button)findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                username=editTextUsername.getText().toString();
                password=editTextPassword.getText().toString();
                sendRequestWithOkHttp(address,username,password);
                if(loginFlag=true){
                    Intent intent=new Intent(LoginActivity.this, AdminMainActivity.class);
                    startActivity(intent);


                }
            }
        });


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

