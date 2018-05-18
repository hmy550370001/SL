package com.idc9000.smartlock.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.ToastUtils;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.event.CurrentDeviceEvent;
import com.idc9000.smartlock.event.KeyTypeEvent;
import com.idc9000.smartlock.utils.bleutils.BleUtils;
import com.idc9000.smartlock.utils.bleutils.EncryptUtils;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;

import static com.idc9000.smartlock.utils.bleutils.BleUtils.bcd2Str;
import static com.idc9000.smartlock.utils.bleutils.BleUtils.hex2byte;

/**
 * Created by 赵先生 on 2017/11/1.
 * 生成临时密码
 */

public class CreateTemporaryPasswordActivity extends BaseActivity {
    private static final String TAG = "CreateTemporaryPassword";
    @BindView(R.id.top_bar)
    QMUITopBar topBar;
    @BindView(R.id.tv_password)
    QMUIRoundButton tvPassword;
    @BindView(R.id.et_temporary_code)
    EditText etTemporaryCode;
    String deviceId;//锁Id
    String type;//钥匙类型
    @BindView(R.id.btn_create_password)
    QMUIRoundButton btnCreatePassword;

    CurrentDeviceEvent currentDevice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_createtemporarypassword);
        EventBus.getDefault().register(this);
        initData();
        initView();
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @Override
    protected void initView() {
        super.initView();
        initTopBar();
    }

    @Override
    protected void initTopBar() {
        super.initTopBar();
        topBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onShowMessageEvent(CurrentDeviceEvent currentDeviceEvent) {
        currentDevice=new CurrentDeviceEvent();
        currentDevice=currentDeviceEvent;
        KLog.e(TAG, "onShowMessageEvent: "+currentDevice.toString() );
        topBar.setTitle(currentDevice.getName()+"  生成临时密码");
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true) // ThreadMode is optional here
    public void onMessage(KeyTypeEvent event) {
        type = event.getMessage();
    }


    @OnClick(R.id.btn_create_password)
    public void onClick() {
        if (!validate()) {
            return;
        }
       try {
           tvPassword.setText(createTemporaryPassword());
       }catch (Exception e){
           e.printStackTrace();
       }


    }

    private boolean validate() {
        boolean valid = true;
        String password = etTemporaryCode.getText().toString();

        if (ifValueWasEmpty(password) || password.length()!=8) {
            etTemporaryCode.setError("请输入8位数");
            valid = false;
        } else {
            etTemporaryCode.setError(null);
        }
        return valid;
    }

    /**
     * 生成临时密码
     * @return
     */
    private String createTemporaryPassword() {
        byte [] temp = EncryptUtils.encrypt(etTemporaryCode.getText().toString().getBytes(),EncryptUtils.hexStringToBytes(currentDevice.getKeyDev()));
        byte [] a =hex2byte(temp);
        return BleUtils.byteToString(a);
    }
}