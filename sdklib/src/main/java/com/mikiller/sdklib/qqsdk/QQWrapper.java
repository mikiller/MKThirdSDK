package com.mikiller.sdklib.qqsdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.mikiller.sdklib.BaseWrapper;
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

public class QQWrapper implements BaseWrapper {
    private final String TAG = QQWrapper.class.getSimpleName();
    private Tencent mTencent;
    private IUiListener uiListener = new IUiListener() {

        @Override
        public void onComplete(Object o) {

        }

        @Override
        public void onError(UiError uiError) {

        }

        @Override
        public void onCancel() {

        }
    };

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
        if (context == null || appId == null)
            return false;
        mTencent = Tencent.createInstance(appId[0], context);
        return mTencent != null;
    }

    @Override
    public void login(Context activity, String...scope) {
        if (activity == null || mTencent == null)
            return;
        if (!mTencent.isSessionValid()) {
            mTencent.login((Activity) activity, "all", uiListener);
        }
    }

    @Override
    public void logout(Context activity) {
        if (mTencent != null)
            mTencent.logout(activity);
    }

    @Override
    public void share(Context context, String url, String title, String content, Map<String, String> args) {
        Bundle bundle = new Bundle();
        if (mTencent != null && args != null) {
            String shareType = args.get(QQShare.SHARE_TO_QQ_KEY_TYPE);
            if (TextUtils.isEmpty(shareType)) {
                Toast.makeText(context, "QQ分享类型错误", Toast.LENGTH_SHORT).show();
                return;
            }
            switch (Integer.parseInt(shareType)) {
                case QQShare.SHARE_TO_QQ_TYPE_DEFAULT:
                    bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
                    bundle.putString(QQShare.SHARE_TO_QQ_TITLE, title);
                    bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, content);
                    bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, args.get(QQShare.SHARE_TO_QQ_IMAGE_URL));
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
            bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, Integer.parseInt(shareType));
            bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, Integer.parseInt(TextUtils.isEmpty(args.get(QQShare.SHARE_TO_QQ_EXT_INT)) ? "0" : args.get(QQShare.SHARE_TO_QQ_EXT_INT)));
            mTencent.shareToQQ((Activity) context, bundle, uiListener);
        }
    }

    public void getUserInfo(Context context){
        UserInfo userInfo = new UserInfo(context, mTencent.getQQToken());
        userInfo.getUserInfo(uiListener);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, uiListener);
    }
}
