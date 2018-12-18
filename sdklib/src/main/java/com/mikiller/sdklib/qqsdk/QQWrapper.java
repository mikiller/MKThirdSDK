package com.mikiller.sdklib.qqsdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.mikiller.sdklib.BaseWrapper;
import com.mikiller.sdklib.SdkWrapper;
import com.mikiller.sdklib.ThirdUserInfo;
import com.tencent.connect.UserInfo;
import com.tencent.connect.share.QQShare;
import com.tencent.open.utils.HttpUtils;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Map;

/**
 * Created by Mikiller on 2018/12/17.
 */

public class QQWrapper extends BaseWrapper {
    private Tencent mTencent;
    private abstract class BaseUIListener implements IUiListener{
        @Override
        public void onError(UiError uiError) {
            if(listener != null){
                listener.onLoginFailed(uiError.errorCode, uiError.errorMessage + ", " + uiError.errorDetail);
            }
        }

        @Override
        public void onCancel() {
            if(listener != null)
                listener.onCancel();
        }
    }

    private IRequestListener requestListener = new IRequestListener() {

        @Override
        public void onComplete(JSONObject jsonObject) {

        }

        @Override
        public void onIOException(IOException e) {

        }

        @Override
        public void onMalformedURLException(MalformedURLException e) {

        }

        @Override
        public void onJSONException(JSONException e) {

        }

        @Override
        public void onConnectTimeoutException(ConnectTimeoutException e) {

        }

        @Override
        public void onSocketTimeoutException(SocketTimeoutException e) {

        }

        @Override
        public void onNetworkUnavailableException(HttpUtils.NetworkUnavailableException e) {

        }

        @Override
        public void onHttpStatusException(HttpUtils.HttpStatusException e) {

        }

        @Override
        public void onUnknowException(Exception e) {

        }
    };

    public QQWrapper() {
    }

    @Override
    public boolean init(Context context, String... appId) {
        if (super.init(context, appId)) {
            mTencent = Tencent.createInstance(appId[0], context);
            return mTencent != null;
        }
        return false;
    }

    @Override
    public void login(Context activity, String...scope) {
        if (activity == null || mTencent == null)
            return;
        if (!mTencent.isSessionValid()) {
            mTencent.login((Activity) activity, "all", new BaseUIListener() {
                @Override
                public void onError(UiError uiError) {
                    if(listener != null)
                        listener.onLoginFailed(uiError.errorCode, uiError.errorMessage + ", " + uiError.errorDetail);
                }

                @Override
                public void onComplete(Object o) {
                    Log.e(TAG, "login : " + o.toString());
                    QQLoginResp resp = new Gson().fromJson(o.toString(), QQLoginResp.class);
                    mTencent.setAccessToken(resp.getAccess_token(), String.valueOf(resp.getExpires_in()));
                    mTencent.setOpenId(resp.getOpenid());
                    if(listener != null){
                        listener.onLoginSuccess(SdkWrapper.QQ);
                    }
                }
            });
        }
    }

    @Override
    public void logout(Context activity) {
        if (mTencent != null) {
            mTencent.logout(activity);
            if(listener != null)
                listener.onLogout();
        }else if(listener != null){
            listener.onCancel();
        }
    }

    @Override
    public void share(Context context, int shareType, String url, String title, String content, Map<String, String> args) {
        Bundle bundle = new Bundle();
        if (mTencent != null && args != null) {
            switch (shareType) {
                case QQShare.SHARE_TO_QQ_TYPE_DEFAULT:
                    bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
                    bundle.putString(QQShare.SHARE_TO_QQ_TITLE, title);
                    bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, content);
                    bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, args.get(SdkWrapper.KEY_IMAGE));
                    break;
                case QQShare.SHARE_TO_QQ_TYPE_IMAGE:
                    bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, args.get(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL));
                    break;
                case QQShare.SHARE_TO_QQ_TYPE_AUDIO:
                    bundle.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, args.get(QQShare.SHARE_TO_QQ_AUDIO_URL));
                    break;
                case QQShare.SHARE_TO_QQ_TYPE_APP:
                    bundle.putString(QQShare.SHARE_TO_QQ_ARK_INFO, args.get(QQShare.SHARE_TO_QQ_ARK_INFO));
                    break;
            }

            bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, args.get(QQShare.SHARE_TO_QQ_APP_NAME));
            bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, shareType);
            bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, Integer.parseInt(TextUtils.isEmpty(args.get(QQShare.SHARE_TO_QQ_EXT_INT)) ? "0" : args.get(QQShare.SHARE_TO_QQ_EXT_INT)));
            mTencent.shareToQQ((Activity) context, bundle, new BaseUIListener() {
                @Override
                public void onComplete(Object o) {
                    Log.e(TAG, "share : " + o.toString());
                    if(listener != null)
                        listener.onShareSuccess();
                }

                @Override
                public void onError(UiError uiError) {
                    if(listener != null)
                        listener.onShareFailed(uiError.errorMessage + ", " + uiError.errorDetail);
                }
            });
        }else if(listener != null){
            listener.onShareFailed("分享失败");
        }
    }

    public void getUserInfo(Context context){
        UserInfo userInfo = new UserInfo(context, mTencent.getQQToken());
        userInfo.getUserInfo(new BaseUIListener() {
            @Override
            public void onComplete(Object o) {
                Log.e(TAG, "user iniof: " + o);
                JSONObject jsonObj = (JSONObject) o;
                try {
                    ThirdUserInfo info = new ThirdUserInfo(jsonObj.getString("nickname"));
                    info.setUid(mTencent.getOpenId());
                    info.setBirthday(jsonObj.getString("year"));
                    info.setGender(jsonObj.getString("gender"));
                    info.setIconUrl(jsonObj.getString("figureurl"));
                    if(listener != null){
                        listener.onGetUserInfo(info);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, new BaseUIListener() {
            @Override
            public void onComplete(Object o) {
                Log.e(TAG, "activity result: " + o.toString());
            }
        });
    }
}
