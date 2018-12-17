package com.mikiller.mkthirdsdk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mikiller.sdklib.SdkWrapper;
import com.tencent.connect.share.QQShare;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mikiller on 2018/12/17.
 */

public class SampleActivity extends AppCompatActivity {
    private Button btnlogin, btnshare;
    private SdkWrapper sdkWrapper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        btnlogin = findViewById(R.id.btnlogin);
        btnshare = findViewById(R.id.btnshare);

        sdkWrapper = SdkWrapper.getInstance();
        sdkWrapper.init(this, SdkWrapper.QQ);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sdkWrapper.login(SampleActivity.this, SdkWrapper.QQ, "all");
            }
        });

        btnshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> args = new HashMap<>();
                args.put(QQShare.SHARE_TO_QQ_KEY_TYPE, String.valueOf(QQShare.SHARE_TO_QQ_TYPE_DEFAULT));
                args.put(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://p4.so.qhmsg.com/bdr/200_200_/t01ecef0bcb75d98099.jpg");
                sdkWrapper.share(SdkWrapper.QQ, "http://www.baidu.com", "hello", "lalalla", args);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        sdkWrapper.onActivityResult(requestCode, resultCode, data);
    }
}
