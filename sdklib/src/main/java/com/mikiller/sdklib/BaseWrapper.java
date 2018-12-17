package com.mikiller.sdklib;

import android.content.Context;

import java.util.Map;

/**
 * Created by Mikiller on 2018/12/17.
 */

public interface BaseWrapper {
    boolean init(Context context, String... args);
    void login(Context context, String... args);
    void logout(Context context);
    void share(Context context, String url, String title, String content, Map<String, String> args);
}
