package com.mikiller.sdklib;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.mikiller.sdklib.qqsdk.QQWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mikiller on 2018/12/17.
 */

public class SdkWrapper {
    private final String TAG = SdkWrapper.class.getSimpleName();
    public final static int WEIXIN = 1, QQ = 2, WEIBO = 3;
    private Context mContext;
    private Map<Integer, BaseWrapper> wrapers = new HashMap<>();
    private SdkWrapper(){}

    private static class WrapperFactory{
        private static SdkWrapper instance = new SdkWrapper();
    }

    public static SdkWrapper getInstance(){
        return WrapperFactory.instance;
    }

    public void initAll(Context context){
        for(int platform = WEIXIN; platform < WEIBO; platform++){
            init(context, platform);
        }
    }

    public boolean init(Context context, int... platforms){
        mContext = context;
        boolean rst = false;
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            for(int platform : platforms){
                BaseWrapper wrapper = null;
                if(platform == WEIXIN){

                }else if(platform == QQ){
                    wrapper = new QQWrapper();
                    rst = wrapper.init(context, String.valueOf(appInfo.metaData.getInt("qqAppId")));
                }else if(platform == WEIBO){

                }
                wrapers.put(platform, wrapper);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }finally {
            return rst;
        }
    }

    public void login(Context context, int platform, String... args){
        if(wrapers.get(platform) != null){
            wrapers.get(platform).login(context, args);
        }
    }

    public void logout(Context context, int platform){
        if(wrapers.get(platform) != null){
            wrapers.get(platform).logout(context);
        }
    }

    public void share(int platform, String url, String title, String content, Map<String, String> args){
        switch (platform){
            case WEIXIN:
                break;
            case QQ:
                QQWrapper wrapper = (QQWrapper) wrapers.get(platform);
                wrapper.share(mContext, url, title, content, args);
                break;
            case WEIBO:
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        ((QQWrapper)wrapers.get(QQ)).onActivityResult(requestCode, resultCode, data);
    }
}
