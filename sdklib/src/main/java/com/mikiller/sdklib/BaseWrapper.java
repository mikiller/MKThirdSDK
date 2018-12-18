package com.mikiller.sdklib;

import android.content.Context;
import android.content.Intent;

import java.util.Map;

/**
 * Created by Mikiller on 2018/12/17.
 */

public abstract class BaseWrapper {
    protected final String TAG = this.getClass().getSimpleName();
    protected SdkListener listener;
    protected void setSdkListener(SdkListener listener){
        this.listener = listener;
    }
    protected boolean init(Context context, String... args){
        if (context == null || args == null)
            return false;
        return true;
    }
    protected abstract void login(Context context, String... args);
    protected abstract void logout(Context context);
    protected abstract void share(Context context, int shareType, String url, String title, String content, Map<String, String> args);
    public void onActivityResult(int requestCode, int resultCode, Intent data){}
}
