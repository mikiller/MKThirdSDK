package com.mikiller.sdklib.weibosdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.mikiller.sdklib.BaseWrapper;
import com.mikiller.sdklib.SdkWrapper;
import com.mikiller.sdklib.ThirdUserInfo;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.BaseMediaObject;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MultiImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoSourceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Mikiller on 2018/12/18.
 */

public class WeiboWrapper extends BaseWrapper {
    private SsoHandler ssoHandler;
    private WbShareHandler shareHandler;

    @Override
    public boolean init(Context context, String... args) {
        if (super.init(context, args) && args.length == 3) {
            WbSdk.install(context, new AuthInfo(context, args[0], args[1], args[2]));
            ssoHandler = new SsoHandler((Activity) context);
            shareHandler = new WbShareHandler((Activity) context);
            shareHandler.registerApp();
            return true;
        }
        return false;
    }

    @Override
    public void login(final Context context, String... args) {
        ssoHandler.authorize(new WbAuthListener() {
            @Override
            public void onSuccess(Oauth2AccessToken oauth2AccessToken) {
                if (oauth2AccessToken.isSessionValid()) {
                    AccessTokenKeeper.writeAccessToken(context, oauth2AccessToken);
                    if (listener != null) {
                        listener.onLoginSuccess(SdkWrapper.WEIBO);
                    }
                }
            }

            @Override
            public void cancel() {
                if (listener != null)
                    listener.onCancel();
            }

            @Override
            public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
                if (listener != null) {
                    listener.onLoginFailed(Integer.parseInt(wbConnectErrorMessage.getErrorCode()), wbConnectErrorMessage.getErrorMessage());
                }
            }
        });
    }

    @Override
    public void logout(Context context) {
        AccessTokenKeeper.clear(context);
        if (listener != null)
            listener.onLogout();
    }

    @Override
    public void share(Context context, int shareType, String url, String title, String content, Map<String, String> args) {
        if (shareHandler != null && args != null) {
            WeiboMultiMessage wbMsg = new WeiboMultiMessage();
            switch (shareType) {
                case SdkWrapper.WEB:

                    wbMsg.mediaObject = getWebObject(url, title, content, args);

                    break;
                case SdkWrapper.TXT:
                    wbMsg.textObject = getTxtObject(url, title, content);
                    break;
                case SdkWrapper.IMAGE:
                    wbMsg.imageObject = getImgObject(args);
                    break;
                case SdkWrapper.VIDE0:
                    wbMsg.videoSourceObject = getVideoObject(args);
                    break;
                default:
                    break;
            }
            shareHandler.shareMessage(wbMsg, false);
        } else if (listener != null)
            listener.onShareFailed("分享失败");
    }

    private BaseMediaObject getWebObject(String url, String title, String content, Map<String, String> args) {
        WebpageObject webObj = new WebpageObject();
        webObj.identify = String.valueOf(System.currentTimeMillis());
        webObj.title = title;
        webObj.description = content;
        webObj.actionUrl = url;
        webObj.defaultText = content;
        if (args.get(SdkWrapper.KEY_IMAGE) != null) {
            webObj.setThumbImage(BitmapFactory.decodeFile(args.get(SdkWrapper.KEY_IMAGE)));
        }
        return webObj;
    }

    private TextObject getTxtObject(String url, String title, String content) {
        TextObject txtObj = new TextObject();
        txtObj.text = content;
        txtObj.title = title;
        txtObj.actionUrl = url;
        return txtObj;
    }

    private ImageObject getImgObject(Map<String, String> args) {
        ImageObject imgObj = new ImageObject();
        imgObj.setImageObject(BitmapFactory.decodeFile(args.get(SdkWrapper.KEY_IMAGE)));
        return imgObj;
    }

    private VideoSourceObject getVideoObject(Map<String, String> args) {
        VideoSourceObject videoObj = new VideoSourceObject();
        videoObj.videoPath = Uri.fromFile(new File(args.get(SdkWrapper.KEY_VIDEO)));
        return videoObj;
    }

    public void getUserInfo(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Oauth2AccessToken auth = AccessTokenKeeper.readAccessToken(context);
                String param = ("access_token=" + auth.getToken() + "&uid=" + auth.getUid());
                InputStream is = null;
                HttpURLConnection connection = null;
                try {
                    URL url = new URL("https://api.weibo.com/2/users/show.json?" + param);
                    Log.e(TAG, "url: " + url.toString());
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoOutput(false);
                    connection.setDoInput(true);
                    connection.setInstanceFollowRedirects(true);
                    connection.setUseCaches(false);
                    connection.setRequestProperty("Content-type", "application/json");
                    connection.connect();
                    if (connection.getResponseCode() == 200) {
                        is = connection.getInputStream();
                        StringBuffer strBuff = new StringBuffer();
                        byte[] buff = new byte[1024];
                        int len = -1;
                        while ((len = is.read(buff)) != -1) {
                            strBuff.append(new String(buff, 0, len, "utf-8"));
                        }

                        JSONObject jsonObject = new JSONObject(strBuff.toString());
                        ThirdUserInfo userInfo = new ThirdUserInfo(jsonObject.getString("name"));
                        userInfo.setIconUrl(jsonObject.getString("profile_image_url"));
                        userInfo.setGender("m".equals(jsonObject.getString("gender")) ? "男" : "女");
                        userInfo.setUid(auth.getUid());
                        if (listener != null) {
                            listener.onGetUserInfo(userInfo);
                        }
                    } else if (listener != null) {
                        listener.onError(connection.getResponseCode(), connection.getResponseMessage());
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    connection.disconnect();
                }
            }
        }).start();
    }

    @Override
    public void onActivityResult(int resquestCode, int resultCode, Intent data) {
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(resquestCode, resultCode, data);
        }
    }

    public void onNewIntent(Intent intent) {
        if (shareHandler != null)
            shareHandler.doResultIntent(intent, new WbShareCallback() {
                @Override
                public void onWbShareSuccess() {
                    if (listener != null) {
                        listener.onShareSuccess();
                    }
                }

                @Override
                public void onWbShareCancel() {
                    if (listener != null)
                        listener.onCancel();
                }

                @Override
                public void onWbShareFail() {
                    if (listener != null)
                        listener.onShareFailed("微博分享失败");
                }
            });
    }
}
