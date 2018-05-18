package com.idc9000.smartlock.HttpCallBack;

import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

/**
 * 获取网络数据通用的回调
 */
public abstract class CommonCallBack extends Callback<String>
{
    @Override
    public String parseNetworkResponse(Response response, int id) throws Exception {
        String jsonStr = response.body().string();
        return jsonStr;
    }
}