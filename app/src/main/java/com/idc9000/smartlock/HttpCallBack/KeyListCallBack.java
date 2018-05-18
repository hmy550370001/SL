package com.idc9000.smartlock.HttpCallBack;

import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;


/**
 * 钥匙列表
 */

public abstract class KeyListCallBack extends Callback<String> {
    @Override
    public String parseNetworkResponse(Response response, int id) throws Exception {
        String jsonStr = response.body().string();
        return  jsonStr;
    }
}
