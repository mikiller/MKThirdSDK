package com.mikiller.sdklib;

/**
 * Created by Mikiller on 2018/12/18.
 */

public interface SdkListener {
    void onLoginSuccess(int platform);
    void onLoginFailed(int code, String msg);
    void onShareSuccess();
    void onShareFailed(String msg);
    void onGetUserInfo(ThirdUserInfo userInfo);
    void onLogout();
    void onCancel();
    void onError(int code, String msg);
}
