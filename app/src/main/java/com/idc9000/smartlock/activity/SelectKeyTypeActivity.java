package com.idc9000.smartlock.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.cons.Constants;
import com.idc9000.smartlock.event.CurrentDeviceEvent;
import com.idc9000.smartlock.event.KeyTypeEvent;
import com.idc9000.smartlock.utils.ActivityUtil;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.zhy.autolayout.AutoRelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by 赵先生 on 2017/11/1.
 * 门锁钥匙-选择新建类型
 */

public class SelectKeyTypeActivity extends BaseActivity {
    private static final String TAG = "SelectKeyTypeActivity";
    Context context=this;
    @BindView(R.id.top_bar)
    QMUITopBar topBar;
    @BindView(R.id.item_tenant)
    AutoRelativeLayout itemTenant;
    @BindView(R.id.item_temporary)
    AutoRelativeLayout itemTemporary;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_selectkeytype);
        EventBus.getDefault().register(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        initData();
        initTopBar();
        if(Constants.UserState.equals("User")){
            itemTenant.setVisibility(View.GONE);
        }
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
    public void onShowMessageEvent(CurrentDeviceEvent messageEvent) {
        topBar.setTitle(messageEvent.getName()+"  生成钥匙");
    }

    @OnClick({R.id.item_tenant, R.id.item_temporary})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_tenant:
                EventBus.getDefault().postSticky(new KeyTypeEvent("0"));
                ActivityUtil.start(context,CreateKeyPasswordActivity.class,false);
                break;
            case R.id.item_temporary:
                EventBus.getDefault().postSticky(new KeyTypeEvent("1"));
                ActivityUtil.start(context,CreateTemporaryPasswordActivity.class,false);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
