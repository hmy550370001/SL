package com.idc9000.smartlock;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idc9000.smartlock.activity.BusinessServicesActivity;
import com.idc9000.smartlock.activity.KeyListActivity;
import com.idc9000.smartlock.activity.LockRecordActivity;
import com.idc9000.smartlock.activity.LockSetting;
import com.idc9000.smartlock.activity.PropertyPayMent;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.event.CurrentDeviceEvent;
import com.idc9000.smartlock.utils.ActivityUtil;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.zhy.autolayout.AutoLinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 锁具详情页
 */

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    Context context = this;

    @BindView(R.id.title_bar)
    QMUITopBar titleBar;

    @BindView(R.id.lock_record)
    LinearLayout lockRecord;
    @BindView(R.id.property_pay)
    LinearLayout propertyPay;
    @BindView(R.id.lock_key)
    LinearLayout lockKey;
    @BindView(R.id.cleaning)
    AutoLinearLayout cleaning;
    @BindView(R.id.repair)
    AutoLinearLayout repair;
    @BindView(R.id.realty_service)
    AutoLinearLayout realtyService;
    @BindView(R.id.tv_lockName)
    TextView tvLockName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        initTopBar();
    }

    public void test() {


    }

    @Override
    protected void initTopBar() {
        super.initTopBar();
        titleBar.setTitle("锁具详情");
        titleBar.getBackground().setAlpha(00);
        titleBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        titleBar.addRightImageButton(R.mipmap.set, R.id.set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtil.start(context, LockSetting.class, false);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onShowMessageEvent(CurrentDeviceEvent currentDeviceEvent) {
        tvLockName.setText(currentDeviceEvent.getName());
    }

    @OnClick({R.id.lock_record, R.id.property_pay, R.id.lock_key, R.id.cleaning, R.id.repair, R.id.realty_service})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lock_record:
                //门锁日志
                ActivityUtil.start(context, LockRecordActivity.class, false);
                break;
            case R.id.property_pay:
                //物业缴费
                ActivityUtil.start(context, PropertyPayMent.class, false);
                break;
            case R.id.lock_key:
                //门锁钥匙列表
                ActivityUtil.start(context, KeyListActivity.class, false);
                break;
            case R.id.cleaning:
                //商家服务
                ActivityUtil.start(context, BusinessServicesActivity.class, false);
                break;
            case R.id.repair:
                //商家服务
                ActivityUtil.start(context, BusinessServicesActivity.class, false);
                break;
            case R.id.realty_service:
                //商家服务
                ActivityUtil.start(context, BusinessServicesActivity.class, false);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
