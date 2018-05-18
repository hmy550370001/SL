package com.idc9000.smartlock.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.idc9000.smartlock.base.BaseActivity;


/**
 * Created by 赵先生 on 2017/10/9.
 */

public class ActivityUtil {
    /**
     * 跳转
     * @param context
     * @param clazz
     * @param ifFinishSelf 是否关闭当前activity
     */
    public static void start(Context context, Class<? extends BaseActivity> clazz, boolean ifFinishSelf){
        Intent intent =new Intent(context,clazz);
        context.startActivity(intent);
        if (ifFinishSelf){
            ((Activity)context).finish();
        }
    }
}
