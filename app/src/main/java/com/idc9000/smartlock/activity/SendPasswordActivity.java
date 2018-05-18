package com.idc9000.smartlock.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.cons.Constants;
import com.idc9000.smartlock.event.BindingEvent;
import com.idc9000.smartlock.utils.ActivityUtil;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 类名: SendPasswordActivity
 * 描述: 录入长密码
 */
public class SendPasswordActivity extends BaseActivity {

    private static final String TAG = "SendPasswordActivity";
    Context context = this;

    @BindView(R.id.top_bar)
    QMUITopBar topBar;
    @BindView(R.id.et_pw)
    EditText etPw;
    @BindView(R.id.btn_sure)
    QMUIRoundButton btnSure;
    @BindView(R.id.cb_tip)
    CheckBox cbTip;

    private String intent="AdminBinding";//蓝牙连接的意图


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendpassword);
        setToolbar();
        EventBus.getDefault().register(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        try {
            switch (intent){
                case "AdminBinding":
                    topBar.setTitle("房东绑定智能锁");
                    break;
                case "UserBinding":
                    topBar.setTitle("租客绑定智能锁");
                    break;
                case "LongPwSetting":
                    topBar.setTitle("更改长密码");
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 绑定事件
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.POSTING)
    public void onBinding(BindingEvent event) {
        intent = event.getBindType();
        KLog.e(TAG, event.getBindType());
    }

    //确认
    @OnClick({R.id.btn_sure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sure:
                if (isEmpty()) return;
                if (cbTip.isChecked()) {
                    startBind();
                } else {
                    ToastUtils.showShort("请同意锁具使用协议！");
                }
                break;
        }
    }

    /**
     * 开始绑定
     */
    private void startBind() {
        if (Constants.UserState.equals("Admin")){
            Constants.AdminPassWord=etPw.getText().toString();
        }else {
            Constants.UserPassWord=etPw.getText().toString();
        }
        switch (intent){
            case "AdminBinding":
                //房东绑定
                ActivityUtil.start(context, BindDeviceActivity.class, false);
                finish();
                break;
            case "UserBinding":
                //租客绑定
                ActivityUtil.start(context, BindDeviceActivity.class, false);
                finish();
                break;
            case "LongPwSetting":
                //长密码设置（修改长密码）
                finish();
                ActivityUtil.start(context, InputOnlyPasswordActivity.class, false);
                break;
        }
    }


    /**
     * 设置标题栏
     */
    private void setToolbar() {
        topBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });
    }

    private void back() {
        //跳转界面
        ActivityUtil.start(context, DeviceListActivity.class, true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }


    private boolean isEmpty() {
        if (TextUtils.isEmpty(etPw.getText().toString())||etPw.getText().toString().length()!=8) {
            Toast.makeText(SendPasswordActivity.this, "请输入8位密码！", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

}
