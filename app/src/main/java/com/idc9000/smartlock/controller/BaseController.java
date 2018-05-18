package com.idc9000.smartlock.controller;

import android.content.Context;

import com.idc9000.smartlock.listener.IModeChangeListener;

/**
 *
 */

public abstract class BaseController {
    protected Context mContext;
    protected IModeChangeListener mListener;

    public BaseController(Context c) {
        mContext=c;
    }

    public void setIModeChangeListener(IModeChangeListener listener) {
        mListener=listener;
    }

    /**
     * 发送网络请求
     * @param action 用来区别多个请求
     * @param values  请求的数据
     */
    public void sendAsyncMessage(final int action, final Object... values){
        new Thread(){
            @Override
            public void run() {
                super.run();
                handleMessage(action,values);
            }
        }.start();

    }

    /**
     * 子类处理具体需求
     * @param action
     * @param values
     */
    protected abstract void handleMessage(int action,Object... values);
}
