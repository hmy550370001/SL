package com.idc9000.smartlock.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.idc9000.smartlock.HttpCallBack.LoginCallBack;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.bean.RLoginResult;
import com.idc9000.smartlock.bean.RResult;
import com.idc9000.smartlock.cons.IdiyMessage;
import com.idc9000.smartlock.cons.NetworkConst;
import com.idc9000.smartlock.controller.UserController;
import com.idc9000.smartlock.db.UserDao;
import com.idc9000.smartlock.utils.ActivityUtil;
import com.socks.library.KLog;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;

/**
 * Created by 赵先生 on 2017/10/11.
 * 启动页
 */

public class SplashActivity extends BaseActivity{
    private static final String TAG = "SplashActivity";
    private Context context=this;

    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.btn_signup)
    Button btn_signup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //判断是否登录
        if (SPUtils.getInstance().getBoolean("isLogin")){
            KLog.e(TAG, "onCreate: isLogin");
            initController();
        }
    }


    @Override
    protected void initController() {
        mController = new UserController(this);
        mController.setIModeChangeListener(this);
        mController.sendAsyncMessage(IdiyMessage.GET_USER_ACTION, 0);
    }


    @Override
    protected void handlerMessage(Message msg) {
        super.handlerMessage(msg);
        switch (msg.what) {
            case IdiyMessage.GET_USER_ACTION_RESULT:
                handlerGetUser(msg.obj);
                break;
        }
    }

    /**
     * 获取上次登录的用户名密码
     *
     * @param c userInfo
     */
    private void handlerGetUser(Object c) {
        if (c != null) {
            UserDao.UserInfo userInfo = (UserDao.UserInfo) c;
            requestLogin(userInfo.name, userInfo.pwd);
        }
    }

    /**
     * 登录
     */
    @OnClick(R.id.btn_login)
    public void gotoLogin(){
        ActivityUtil.start(this,LoginActivity.class,false);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * 注册
     */
    @OnClick(R.id.btn_signup)
    public void gotoSignup(){
        ActivityUtil.start(this,SignUpActivity.class,false);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * 请求登录
     *
     * @param phone  手机号
     * @param passwd 密码
     */
    private void requestLogin(String phone, String passwd) {
        OkHttpUtils
                .post()
                .url(NetworkConst.LOGIN_URL)
                .addParams("phone", phone)
                .addParams("password", passwd)
                .build()
                .execute(new LoginCallBack() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort("登陆异常！" + e.getLocalizedMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        handleLoginResult(response);
                    }
                });
    }

    /**
     * 登陆数据处理
     */
    private void handleLoginResult(String response) {
            RLoginResult rLoginResult = JSON.parseObject(response,RLoginResult.class);

            if (rLoginResult.isSuccess()) {
                //获取用户id并记录
                String userAccount=rLoginResult.getResult().getAccount();
                String pushId= String.valueOf(rLoginResult.getResult().getId());
                SPUtils.getInstance().put("CurrentUserID", userAccount);
                KLog.e(TAG, "pushId: "+pushId);
                //初始化sdk
                JPushInterface.setDebugMode(true);//正式版的时候设置false，关闭调试
                JPushInterface.init(this);
                //极光推送 建议添加tag标签，发送消息的之后就可以指定tag标签来发送了
                Set<String> set = new HashSet<>();
                set.add(pushId); //名字任意，可多添加几个
                JPushInterface.setTags(this, set, null);//设置标签
                ActivityUtil.start(this,DeviceListActivity.class,true);
            } else {
                ToastUtils.showShort(rLoginResult.getErrorMsg());
            }
    }

}
