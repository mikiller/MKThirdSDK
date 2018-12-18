package com.mikiller.sdklib.qqsdk

/**
 * Created by Mikiller on 2018/12/18.
 */
data class QQLoginResp(var ret: Int) {
    var openid: String = "";
    var access_token: String = "";
    var pay_token: String = "";
    var expires_in: Long = 0;
    var pf: String = "";
    var pfkey: String = "";
    var msg: String = "";
    var login_cost: Long = 0;
    var query_authority_cost: Long = 0;
    var authority_cost: Long = 0;
    var expires_time: Long = 0;
}