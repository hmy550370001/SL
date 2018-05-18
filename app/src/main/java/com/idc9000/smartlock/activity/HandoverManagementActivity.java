package com.idc9000.smartlock.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.idc9000.smartlock.HttpCallBack.CommonCallBack;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.bean.RResult;
import com.idc9000.smartlock.cons.NetworkConst;
import com.idc9000.smartlock.event.BindingEvent;
import com.idc9000.smartlock.event.CurrentDeviceEvent;
import com.idc9000.smartlock.utils.ActivityUtil;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by 赵先生 on 2017/10/30.
 * 智能锁移交管理
 */

public class HandoverManagementActivity extends BaseActivity {
    @BindView(R.id.title_bar)
    QMUITopBar titleBar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.tv_pw)
    TextView tvPw;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.lock_pw)
    TextView lockPw;
    @BindView(R.id.et_keyAdmin)
    EditText etKeyAdmin;
    @BindView(R.id.btn_unBundling)
    QMUIRoundButton btnUnBundling;


    private static final String TAG = "HandoverManagementActiv";
    Context context=this;

    String lockID;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handovermanagement);
        EventBus.getDefault().register(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        initTopBar();
    }

    @Override
    protected void initTopBar() {
        super.initTopBar();
        titleBar.setTitle("智能门锁移交管理");
        titleBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @OnClick(R.id.btn_unBundling)
    public void onClick() {
        try {
            checkAccount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onShowMessageEvent(CurrentDeviceEvent currentDeviceEvent) {
        lockID=currentDeviceEvent.getDeviceId();
    }

    private void checkAccount() {
        OkHttpUtils
                .post()
                .url(NetworkConst.HandoverManagement)
                .addParams("phone", etPhone.getText().toString())
                .addParams("password", etPassword.getText().toString())
                .addParams("keyAdmin", etKeyAdmin.getText().toString())
                .addParams("lockId", lockID)
                .build()
                .execute(new CommonCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort(e.getLocalizedMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        RResult rResult = JSON.parseObject(response, RResult.class);
                        if (rResult.isSuccess()){
                            EventBus.getDefault().postSticky(new BindingEvent("UnBingDing",null));
                            ActivityUtils.startActivity(SearchDeviceActivity.class);
                            finish();
                        }else {
                            ToastUtils.showShort(rResult.getErrorMsg());
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //反注册EventBus
        EventBus.getDefault().unregister(this);
    }
}
