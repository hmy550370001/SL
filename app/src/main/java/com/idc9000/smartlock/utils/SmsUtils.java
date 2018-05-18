package com.idc9000.smartlock.utils;

import android.app.Application;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.blankj.utilcode.util.SnackbarUtils;
import com.idc9000.smartlock.listener.IModeSmsListener;
import com.socks.library.KLog;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static android.R.attr.data;
import static cn.smssdk.utils.a.d;
import static cn.smssdk.utils.a.e;

/**
 * Created by 赵先生 on 2017/9/25.
 * 注册 验证码
 */

public class SmsUtils extends Application {
    public  static boolean SmsSuccess=false;
    private static final String TAG = "SmsUtils";
    private static SmsUtils smsUtils;
    private  EventHandler eventHandler;
    /**
     *定义一个变量储存数据
     */
    private  IModeSmsListener listener;

    /**
     *提供公共的方法,并且初始化接口类型的数据
     */
    public void setListener( IModeSmsListener listener){
        this.listener =  listener;
    }


    public static SmsUtils getInstance(){
        if(smsUtils == null){
            smsUtils = new SmsUtils();
        }
        return smsUtils;
    }

    //请求验证码
    public static void sendMsg(String country, String phoneNumber) {
        KLog.e(TAG, "sendMsg: "+phoneNumber);
        SMSSDK.getVerificationCode(country, phoneNumber);
    }
    //提交验证码
    public static void  submitVerificationCode(String country, String phone, String code){
        SMSSDK.submitVerificationCode(country, phone, code);
    }
    //初始化
    public void initSms(){
        KLog.e(TAG, "initSms: ");
        // 创建EventHandler对象

        // 注册监听器
        eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eventHandler);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;

            KLog.e(TAG, "handleMessage: "+ msg.toString());
            if (listener!=null) {
                listener.onModeChanged(event,result,data);
            }

            SMSSDK.unregisterEventHandler(eventHandler);

        }

    };

}
