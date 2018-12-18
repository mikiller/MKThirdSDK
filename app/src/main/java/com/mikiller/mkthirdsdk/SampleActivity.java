package com.mikiller.mkthirdsdk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mikiller.sdklib.SdkListener;
import com.mikiller.sdklib.SdkWrapper;
import com.mikiller.sdklib.ThirdUserInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mikiller on 2018/12/17.
 */

public class SampleActivity extends AppCompatActivity {
    private Button btnlogin, btnshare, btnuserinfo, btnlogout;
    private SdkWrapper sdkWrapper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        btnlogin = findViewById(R.id.btnlogin);
        btnshare = findViewById(R.id.btnshare);
        btnuserinfo = findViewById(R.id.btnuserinfo);
        btnlogout = findViewById(R.id.btnlogout);

        sdkWrapper = SdkWrapper.getInstance();
        sdkWrapper.setRedirectUrl("https://zoen13.github.io/mi-intro/");
        sdkWrapper.init(this, new SdkListener() {
            @Override
            public void onLoginSuccess(int platform) {
                sdkWrapper.getUserInfo(SampleActivity.this, platform);
            }

            @Override
            public void onLoginFailed(int code, String msg) {

            }

            @Override
            public void onShareSuccess() {

            }

            @Override
            public void onShareFailed(String msg) {

            }

            @Override
            public void onGetUserInfo(ThirdUserInfo userInfo) {
                Log.e("act", "userinfo: " + userInfo.toString());
            }

            @Override
            public void onLogout() {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(int code, String msg) {
                Log.e("act", "code: " + code + ", msg: " + msg);
            }
        }, SdkWrapper.QQ, SdkWrapper.WEIBO);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sdkWrapper.login(SampleActivity.this, SdkWrapper.QQ, "all");
                sdkWrapper.login(SampleActivity.this, SdkWrapper.WEIBO);
            }
        });

        btnshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> args = new HashMap<>();
                args.put(SdkWrapper.KEY_TYPE, String.valueOf(SdkWrapper.WEB));
                //args.put(SdkWrapper.KEY_IMAGE, "http://p4.so.qhmsg.com/bdr/200_200_/t01ecef0bcb75d98099.jpg");
                sdkWrapper.share(SdkWrapper.WEIBO, "http://www.baidu.com", "hello", "lalalla", args);
            }
        });

        btnuserinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sdkWrapper.getUserInfo(SampleActivity.this, SdkWrapper.WEIBO);
            }
        });

        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sdkWrapper.logout(SampleActivity.this, SdkWrapper.WEIBO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        sdkWrapper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        sdkWrapper.onNewIntent(intent);
    }
}
