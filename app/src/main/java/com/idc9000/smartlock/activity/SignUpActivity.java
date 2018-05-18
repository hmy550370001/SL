package com.idc9000.smartlock.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.blankj.utilcode.util.ToastUtils;
import com.idc9000.smartlock.HttpCallBack.RegisterCallBack;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.bean.RResult;
import com.idc9000.smartlock.cons.NetworkConst;
import com.idc9000.smartlock.listener.IModeSmsListener;
import com.idc9000.smartlock.utils.ActivityUtil;
import com.idc9000.smartlock.utils.SmsUtils;
import com.idc9000.smartlock.view.ClearEditText;
import com.idc9000.smartlock.view.CountDownButton;
import com.idc9000.smartlock.view.PasswdEditText;
import com.socks.library.KLog;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cn.smssdk.SMSSDK;
import okhttp3.Call;


/**
 * 注册页
 */

public class SignUpActivity extends BaseActivity implements IModeSmsListener {
    private static final String TAG = "SignUpActivity";

    boolean SmsSuccess = false;  //验证码是否验证成功

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.input_sign_phone)
    ClearEditText inputSignPhone;
    @BindView(R.id.input_sign_code)
    EditText inputSignCode;
    @BindView(R.id.input_sign_password)
    PasswdEditText inputSignPassword;
    @BindView(R.id.btn_signup)
    Button btnSignup;
    @BindView(R.id.btn_code)
    CountDownButton btn_code;
    @BindView(R.id.input_sign_name)
    EditText inputSignName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }


    /**
     * 获取验证码
     */
    @OnClick(R.id.btn_code)
    public void getSms() {
        String phone = inputSignPhone.getText().toString();
        if (ifValueWasEmpty(phone) || phone.length() != 11) {
            inputSignPhone.setError("请输入11位手机号码！");
        } else {
            btn_code.startCount();
            SmsUtils.getInstance().setListener(this);
            SmsUtils.getInstance().initSms();
            SmsUtils.sendMsg("86", phone);
        }
    }

    /**
     * 取消
     */
    @OnClick(R.id.iv_back)
    public void back() {
        finish();
    }

    /**
     * 注册
     */
    @OnClick(R.id.btn_signup)
    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            return;
        }

        showProgressDialog(this, "注册中...");
        String name = inputSignName.getText().toString();   //昵称
        String phone = inputSignPhone.getText().toString(); //手机号
        String code = inputSignCode.getText().toString();//验证码
        String password = inputSignPassword.getText().toString();//密码

        //确认验证码  此处使用Mob短信验证码
        SmsUtils.submitVerificationCode("86", phone, code);

        if (SmsSuccess) {
            //验证码验证成功 请求注册
            requestRegister(name,phone, password);
        } else {
            dismissProgressDialog();
            ToastUtils.showShort("验证码错误，请重新输入！");
        }
    }

    /**
     * 发送注册请求
     *
     * @param phone    手机号
     * @param password 密码
     */
    private void requestRegister(String name ,String phone, String password) {
        OkHttpUtils
                .post()
                .url(NetworkConst.REGISTER_URL)
                .addParams("name",name)
                .addParams("phone", phone)
                .addParams("password", password)
                .build()
                .execute(new RegisterCallBack() {
                    @Override
                    public void onResponse(RResult response, int id) {
                        KLog.e(TAG, "onResponse: " + response.toString());
                        dismissProgressDialog();
                        handleSignupResult(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dismissProgressDialog();
                        ToastUtils.showShort(e.getLocalizedMessage());
                        KLog.e(TAG, "onError: " + e.getLocalizedMessage());
                    }

                });
    }

    /**
     * 请求注册的结果
     *
     * @param resultBean 注册结果
     */
    private void handleSignupResult(RResult resultBean) {
        ToastUtils.showShort(resultBean.isSuccess() ? "注册成功!" : resultBean.getErrorMsg());
        if (resultBean.isSuccess()) {
            //注册成功 跳转到登录页
            ActivityUtil.start(this, LoginActivity.class, true);
        }
    }

    /**
     * 输入格式验证
     *
     * @return 输入格式验证结果
     */
    public boolean validate() {
        boolean valid = true;
        String name = inputSignName.getText().toString();
        String phone = inputSignPhone.getText().toString();
        String code = inputSignCode.getText().toString();
        String password = inputSignPassword.getText().toString();
        if (ifValueWasEmpty(name) || name.length() < 4) {
            valid = false;
        } else {
            inputSignName.setError(null);
        }
        if (ifValueWasEmpty(phone) || phone.length() < 11) {
            inputSignPhone.setError("请输入11位手机号码！");
            valid = false;
        } else {
            inputSignPhone.setError(null);
        }

        if (ifValueWasEmpty(password) || password.length() < 6 || password.length() > 11) {
            inputSignPassword.setError("请输入6至11位密码！");
            valid = false;
        } else {
            inputSignPassword.setError(null);
        }

        if (ifValueWasEmpty(code)) {
            inputSignCode.setError("请输入验证码！");
            valid = false;
        } else {
            inputSignCode.setError(null);
        }

        return valid;
    }


    /**
     * 验证码结果
     *
     * @param event  请求事件
     * @param result 结果
     * @param data   数据
     */
    @Override
    public void onModeChanged(int event, int result, Object data) {
        if (result == SMSSDK.RESULT_COMPLETE) {
            // 短信注册成功后，返回MainActivity,然后提示新好友
            if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                KLog.e(TAG, "handleMessage: 提交验证码成功");
            } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                KLog.e(TAG, "handleMessage: 发送验证码成功");
                SmsSuccess = true;
            } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {// 返回支持发送验证码的国家列表
                KLog.e(TAG, "handleMessage: 获取国家列表成功");
            }

        } else {
            ((Throwable) data).printStackTrace();
            ToastUtils.showShort(((Throwable) data).getLocalizedMessage());
        }
    }
}
