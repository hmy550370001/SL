package com.idc9000.smartlock.HttpCallBack;

import com.alibaba.fastjson.JSON;
import com.idc9000.smartlock.bean.RResult;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

/**
 * 请求注册的回调
 */
public abstract class RegisterCallBack extends Callback<RResult>
{
    @Override
    public RResult parseNetworkResponse(Response response, int id) throws Exception {
        String jsonStr = response.body().string();
        return JSON.parseObject(jsonStr, RResult.class);
    }
}