package com.idc9000.smartlock.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.zhy.autolayout.AutoRelativeLayout;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by 赵先生 on 2017/10/31.
 * 门锁提醒
 */

public class LockRemindActivity extends BaseActivity {
    private static final String TAG = "LockRemindActivity";
    Context context = this;
    @BindView(R.id.top_bar)
    QMUITopBar titleBar;
    @BindView(R.id.item_alarm_setting)
    AutoRelativeLayout itemAlarmSetting;
    @BindView(R.id.pw_setting)
    AutoRelativeLayout pwSetting;
    @BindView(R.id.tv_time_setting)
    TextView tvTimeSetting;

    String alarmTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockremind);
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
        titleBar.setTitle("门锁提醒设置");
        titleBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @OnClick({R.id.item_alarm_setting, R.id.pw_setting})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_alarm_setting:
                Intent intent = new Intent(context, ChangeAlarmSettingsActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.pw_setting:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (1 == resultCode) {
            String result = data.getStringExtra("result");//得到新Activity关闭后返回的数据
            tvTimeSetting.setText(result+"分钟");
            alarmTime = result;
        }
    }
}
