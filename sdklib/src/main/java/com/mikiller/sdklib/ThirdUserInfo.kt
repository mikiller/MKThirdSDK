package com.mikiller.sdklib

import android.provider.ContactsContract

/**
 * Created by Mikiller on 2018/12/18.
 */
data class ThirdUserInfo(var nickname: String) {
    var gender: String = "";
    var birthday: String = "";
    var iconUrl: String = "";
    var uid: String = "";
}