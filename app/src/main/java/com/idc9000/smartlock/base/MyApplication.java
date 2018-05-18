package com.idc9000.smartlock.base;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
import com.idc9000.smartlock.cons.Constants;
import com.mob.MobSDK;
import com.socks.library.KLog;
import com.tencent.bugly.crashreport.CrashReport;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;

/**
 * @author Mr.Zhao
 * 应用初始化
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //第三方utils集合 初始化
        Utils.init(this);

        //mob短信验证  注册你的AppKey和AppSecret
        MobSDK.init(this, Constants.Mob_Appkey, Constants.MobApp_Secret);

        //bugly
        CrashReport.initCrashReport(getApplicationContext());

        //Okhttp
        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);

        //二维码
        ZXingLibrary.initDisplayOpinion(this);
    }
}
