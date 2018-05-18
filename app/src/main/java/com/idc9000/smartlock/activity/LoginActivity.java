package com.idc9000.smartlock.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.idc9000.smartlock.HttpCallBack.LoginCallBack;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.bean.RLoginResult;
import com.idc9000.smartlock.cons.IdiyMessage;
import com.idc9000.smartlock.cons.NetworkConst;
import com.idc9000.smartlock.controller.UserController;
import com.idc9000.smartlock.db.UserDao;
import com.idc9000.smartlock.utils.ActivityUtil;
import com.idc9000.smartlock.view.ClearEditText;
import com.socks.library.KLog;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import okhttp3.Call;


/**
 * Created by 赵先生 on 2017/10/9.
 * 登录
 */

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";

    @BindView(R.id.input_login_phone)
    ClearEditText phone_input;
    @BindView(R.id.input_login_password)
    EditText pw_input;
    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.rb_group)
    RadioRealButtonGroup rb_group;
    @BindView(R.id.iv_host)
    ImageView iv_host;
    @BindView(R.id.iv_user)
    ImageView iv_user;
    @BindView(R.id.iv_back)
    ImageView iv_back;

    String userAccount; //用户Id

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initController();
        initUserState();
    }

    @Override
    protected void initController() {
        mController = new UserController(this);
        mController.setIModeChangeListener(this);
    }

    @Override
    protected void handlerMessage(Message msg) {
        super.handlerMessage(msg);
        switch (msg.what) {
            case IdiyMessage.SAVE_USERTODB_RESULT:
                handleSaveUser2Db((Boolean) msg.obj);
                break;
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
            phone_input.setText(userInfo.name);
            pw_input.setText(userInfo.pwd);
        }
    }

    /**
     * 保存用户名密码并加密
     *
     * @param ifSuccess 保存状态
     */
    private void handleSaveUser2Db(boolean ifSuccess) {
        if (ifSuccess) {
            onLoginSuccess();
        } else {
            ToastUtils.showShort("登陆异常");
        }
    }

    /**
     * 选择用户状态
     */
    public void initUserState() {
        //默认为房东登录
        SPUtils.getInstance().put("userType","Admin");

        // 如果数据库表里面有数据 应该读取账号密码出来
        mController.sendAsyncMessage(IdiyMessage.GET_USER_ACTION, 0);

        iv_host.isPressed();
        rb_group.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                showChooseView(position);
            }
        });
    }

    /**
     * 选择角色状态
     *
     * @param position 房主或租客
     */
    private void showChooseView(int position) {
        if (0 == position) {
            iv_user.setVisibility(View.INVISIBLE);
            iv_host.setVisibility(View.VISIBLE);
            SPUtils.getInstance().put("userType","Admin");
        } else if (1 == position) {
            iv_host.setVisibility(View.INVISIBLE);
            iv_user.setVisibility(View.VISIBLE);
            SPUtils.getInstance().put("userType","User");
        }
    }

    /**
     * 回退
     */
    @OnClick(R.id.iv_back)
    public void back() {
        finish();
    }

    /**
     * 点击登陆
     */
    @OnClick(R.id.btn_login)
    public void login() {
        if (!validate()) {
            return;
        }

        btn_login.setEnabled(false);

        String phone = phone_input.getText().toString();
        String passwd = pw_input.getText().toString();
        showProgressDialog(this, "登录中...");

        requestLogin(phone, passwd);
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
                        KLog.e(TAG, e.getLocalizedMessage());
                        onLoginFailed(e.getLocalizedMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        KLog.json(response);
                        handleLoginResult(response);
                    }
                });
    }


    /**
     * 账号密码验证
     *
     * @return 输入格式验证结果
     */
    public boolean validate() {
        boolean valid = true;
        String phone = phone_input.getText().toString();
        String password = pw_input.getText().toString();

        if (ifValueWasEmpty(phone) || phone.length() != 11) {
            phone_input.setError("请输入11位手机号码");
            valid = false;
        } else {
            phone_input.setError(null);
        }

        if (ifValueWasEmpty(password) || password.length() < 6 || password.length() > 10) {
            pw_input.setError("请输入6到10个字符");
            valid = false;
        } else {
            pw_input.setError(null);
        }
        return valid;
    }

    /**
     * 登陆数据处理
     */
    private void handleLoginResult(String response) {
        try {
            RLoginResult rLoginResult = JSON.parseObject(response,RLoginResult.class);


            if (rLoginResult.isSuccess()) {
                // 2.将账号密码保存到数据库 传递一个账号密码
                String name = phone_input.getText().toString();
                String pwd = pw_input.getText().toString();
                mController.sendAsyncMessage(IdiyMessage.SAVE_USERTODB, name, pwd);
                //获取用户id并记录
                userAccount=rLoginResult.getResult().getAccount();
                String pushId= String.valueOf(rLoginResult.getResult().getId());
                SPUtils.getInstance().put("CurrentUserID", userAccount);
                initPush(pushId);
            } else {
                onLoginFailed(rLoginResult.getErrorMsg());
            }
        } catch (Exception e) {
            ToastUtils.showShort("登陆异常！" + e.getLocalizedMessage());
        }
    }

    /**
     * 初始化推送
     * @param pushId
     */
    private void initPush(String pushId) {
        //初始化极光推送sdk
        JPushInterface.setDebugMode(true);//正式版的时候设置false，关闭调试
        JPushInterface.init(this);
        //极光推送 建议添加tag标签，发送消息的之后就可以指定tag标签来发送了
        Set<String> set = new HashSet<>();
        set.add(pushId); //名字任意，可多添加几个
        JPushInterface.setTags(this, set, null);//设置标签
    }


    /**
     * 登陆成功
     */
    public void onLoginSuccess() {
        dismissProgressDialog();
        SPUtils.getInstance().put("isLogin", true);
        ActivityUtils.finishActivity(SplashActivity.class);
        ActivityUtil.start(this, DeviceListActivity.class, true);
    }

    /**
     * 登陆失败
     */
    public void onLoginFailed(String errorMsg) {
        dismissProgressDialog();
        ToastUtils.showShort(errorMsg);
        btn_login.setEnabled(true);
    }
}
