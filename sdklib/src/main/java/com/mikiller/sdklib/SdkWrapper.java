package com.mikiller.sdklib;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.mikiller.sdklib.qqsdk.QQWrapper;
import com.mikiller.sdklib.weibosdk.WeiboWrapper;
import com.tencent.connect.share.QQShare;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mikiller on 2018/12/17.
 */

public class SdkWrapper {
    private final String TAG = SdkWrapper.class.getSimpleName();
    public final static int WEIXIN = 1, QQ = 2, WEIBO = 3;
    public final static int WEB = 1, TXT = 4, IMAGE = 5, AUDIO = 2, VIDE0 = 3, APK = 6;
    public final static String KEY_TYPE = "KEYTYPE", KEY_IMAGE = "KEYIMAGE", KEY_AUDIO = "KEYAUDIO", KEY_VIDEO = "KEYVIDEO", KEY_APK = "KEYAPK", KEY_EXT = "KEYEXT";
    private Context mContext;
    private Map<Integer, BaseWrapper> wrappers = new HashMap<>();
    private String redirectUrl = "";
    private int currentPlatform;
    private SdkWrapper(){}

    private static class WrapperFactory{
        private static SdkWrapper instance = new SdkWrapper();
    }

    public static SdkWrapper getInstance(){
        return WrapperFactory.instance;
    }

    public void setRedirectUrl(String url){
        redirectUrl = url;
    }

    public boolean init(Context context, SdkListener listener, int... platforms){
        mContext = context;
        boolean rst = false;
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            for(int platform : platforms){
                BaseWrapper wrapper = null;
                rst = false;
                if(platform == WEIXIN){

                }else if(platform == QQ){
                    wrapper = new QQWrapper();
                    rst = wrapper.init(context, String.valueOf(appInfo.metaData.getInt("qqAppId")));
                }else if(platform == WEIBO){
                    wrapper = new WeiboWrapper();
                    rst = wrapper.init(context, String.valueOf(appInfo.metaData.getInt("weiboAppId")), redirectUrl, "");
                }
                wrapper.setSdkListener(listener);
                wrappers.put(platform, wrapper);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }finally {
            return rst;
        }
    }

    public void login(Context context, int platform, String... args){
        currentPlatform = platform;
        if(wrappers.get(platform) != null){
            wrappers.get(platform).login(context, args);
        }
    }

    public void logout(Context context, int platform){
        if(wrappers.get(platform) != null){
            wrappers.get(platform).logout(context);
        }
    }

    public void share(int platform, String url, String title, String content, Map<String, String> args){
        if(args == null)
            return;
        currentPlatform = platform;
        switch (platform){
            case WEIXIN:
                break;
            case QQ:
                QQWrapper wrapper = (QQWrapper) wrappers.get(platform);
                wrapper.share(mContext, getQQShareType(args.get(SdkWrapper.KEY_TYPE)), url, title, content, args);
                break;
            case WEIBO:
                wrappers.get(platform).share(mContext, Integer.parseInt(args.get(SdkWrapper.KEY_TYPE)), url, title, content, args);
                break;
        }
    }

    private int getQQShareType(String type){
        int shareType = QQShare.SHARE_TO_QQ_TYPE_DEFAULT;
        if(TextUtils.isEmpty(type))
            return shareType;
        switch (Integer.parseInt(type)){
            case SdkWrapper.WEB:
                shareType = QQShare.SHARE_TO_QQ_TYPE_DEFAULT;
                break;
            case SdkWrapper.IMAGE:
                shareType = QQShare.SHARE_TO_QQ_TYPE_IMAGE;
                break;
            case SdkWrapper.AUDIO:
                shareType = QQShare.SHARE_TO_QQ_TYPE_AUDIO;
                break;
            case SdkWrapper.APK:
                shareType = QQShare.SHARE_TO_QQ_TYPE_APP;
                break;
        }
        return shareType;
    }

    public void getUserInfo(Context context, int platform){
        switch (platform){
            case WEIXIN:
                break;
            case QQ:
                ((QQWrapper) wrappers.get(platform)).getUserInfo(context);
                break;
            case WEIBO:
                ((WeiboWrapper) wrappers.get(platform)).getUserInfo(context);
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(wrappers.get(currentPlatform) != null)
        wrappers.get(currentPlatform).onActivityResult(requestCode, resultCode, data);
    }

    public void onNewIntent(Intent intent){
        ((WeiboWrapper) wrappers.get(WEIBO)).onNewIntent(intent);
    }
}
