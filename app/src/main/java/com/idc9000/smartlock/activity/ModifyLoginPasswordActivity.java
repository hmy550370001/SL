package com.idc9000.smartlock.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.ToastUtils;
import com.idc9000.smartlock.HttpCallBack.RegisterCallBack;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.bean.RResult;
import com.idc9000.smartlock.cons.Constants;
import com.idc9000.smartlock.cons.NetworkConst;
import com.idc9000.smartlock.listener.IModeSmsListener;
import com.idc9000.smartlock.utils.SmsUtils;
import com.idc9000.smartlock.view.CountDownButton;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.socks.library.KLog;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cn.smssdk.SMSSDK;
import okhttp3.Call;

/**
 * 修改登录密码
 */

public class ModifyLoginPasswordActivity extends BaseActivity implements IModeSmsListener {
    private static final String TAG = "ModifyLoginPasswordActi";
    Context context;
    @BindView(R.id.top_bar)
    QMUITopBar topBar;
    @BindView(R.id.btn_code)
    CountDownButton btn_code;

    @BindView(R.id.et_inputSignCode)
    EditText etInputSignCode;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_confirm_password)
    EditText etConfirmPassword;
    @BindView(R.id.btn_confirm)
    QMUIRoundButton btnConfirm;

    boolean SmsSuccess = false;  //验证码是否验证成功

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pw);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        topBar.setTitle("修改登录密码");
        topBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @OnClick({R.id.btn_code, R.id.btn_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_code:
                getSms();
                break;
            case R.id.btn_confirm:
                modifyPassWord();
                break;
        }
    }

    public void getSms() {
        KLog.e(TAG, "getSms: "+Constants.CurrentUserPhone );
        btn_code.startCount();
        SmsUtils.getInstance().setListener(this);
        SmsUtils.getInstance().initSms();
        SmsUtils.sendMsg("86", Constants.CurrentUserPhone);
    }

    /**
     * 输入格式验证
     *
     * @return 输入格式验证结果
     */
    public boolean validate() {
        boolean valid = true;

        String code = etInputSignCode.getText().toString();
        String password = etPassword.getText().toString();
        String ConfirmPassword = etConfirmPassword.getText().toString();


        if (ifValueWasEmpty(password) || password.length() < 8) {
            etPassword.setError("请输入8位密码！");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        if (!password.equals(ConfirmPassword)) {
            etConfirmPassword.setError("两次密码输入不一致！");
            valid = false;
        } else {
            etConfirmPassword.setError(null);
        }

        if (ifValueWasEmpty(code)) {
            etInputSignCode.setError("请输入验证码！");
            valid = false;
        } else {
            etInputSignCode.setError(null);
        }

        return valid;
    }


    /**
     * 更改密码
     */
    public void modifyPassWord() {
        Log.d(TAG, "modify");

        if (!validate()) {
            return;
        }

        showProgressDialog(this, "更改中...");

        String code = etInputSignCode.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword =etConfirmPassword.getText().toString();

        //确认验证码
        SmsUtils.submitVerificationCode("86", Constants.CurrentUserPhone, code);

        if (SmsSuccess) {
            requestModify(password, confirmPassword);
        } else {
            dismissProgressDialog();
            ToastUtils.showShort("验证码错误，请重新输入！");
        }
    }

    /**
     * 发送注册请求
     *
     * @param password    密码
     * @param confirmPwd  确认密码
     */
    private void requestModify(String password, String confirmPwd) {
        OkHttpUtils
                .post()
                .url(NetworkConst.updatePwd)
                .addParams("password", password)
                .addParams("confirmPwd", confirmPwd)
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
     * 请求更改的结果
     *
     * @param resultBean 更改结果
     */
    private void handleSignupResult(RResult resultBean) {
        ToastUtils.showShort(resultBean.isSuccess() ? "更改成功!" : resultBean.getErrorMsg());
        finish();
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
        }
    }
}
