package com.idc9000.smartlock.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.cons.Constants;
import com.idc9000.smartlock.event.BindingEvent;
import com.idc9000.smartlock.utils.ActivityUtil;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.zhy.autolayout.AutoRelativeLayout;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by 赵先生 on 2017/10/30.
 * 门锁设置
 */

public class LockSetting extends BaseActivity {
    Context context = this;
    @BindView(R.id.top_bar)
    QMUITopBar titleBar;
    @BindView(R.id.rl_code)
    AutoRelativeLayout rlCode;
    @BindView(R.id.rl_turn_over)
    AutoRelativeLayout rlTurnOver;
    @BindView(R.id.rl_remind)
    AutoRelativeLayout rlRemind;
    @BindView(R.id.rl_long_pw)
    AutoRelativeLayout rlLongPw;
    @BindView(R.id.next_turn_over)
    ImageView nextTurnOver;
    @BindView(R.id.portrait_remind)
    ImageView portraitRemind;
    @BindView(R.id.next_remind)
    ImageView nextRemind;
    @BindView(R.id.portrait_turn_over)
    ImageView portraitTurnOver;
    @BindView(R.id.tb_long)
    ImageView tbLong;
    @BindView(R.id.rl_long_type)
    AutoRelativeLayout rlLongType;
    @BindView(R.id.portrait_code)
    ImageView portraitCode;
    @BindView(R.id.tb_short)
    ImageView tbShort;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
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
        titleBar.setTitle("门锁设置");
        titleBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @OnClick({R.id.rl_code, R.id.rl_turn_over, R.id.rl_remind, R.id.rl_long_pw,R.id.tb_long,R.id.tb_short})
    public void onClick(View view) {
        switch (view.getId()) {
            // 密码指纹设置
            case R.id.rl_code:
                ActivityUtil.start(this, PasswordSettingActivity.class, false);
                break;
                //智能锁移交管理
            case R.id.rl_turn_over:
                if (Constants.UserState.equals("Admin")) {
                    ActivityUtil.start(this, HandoverManagementActivity.class, false);
                } else {
                    ToastUtils.showShort("租户无法主动解除绑定！");
                }
                break;
                //警报长鸣
            case R.id.rl_remind:
                ActivityUtil.start(this, LockRemindActivity.class, false);
                break;
            case R.id.rl_long_pw:
                //租户绑定设备
                EventBus.getDefault().postSticky(new BindingEvent("LongPwSetting", null));
                ActivityUtils.startActivity(SearchDeviceActivity.class);
                break;
            case R.id.tb_long:
                tbLong.setBackground(getResources().getDrawable(R.mipmap.choose_selected));
                tbShort.setBackground(getResources().getDrawable(R.mipmap.choose_default));
                EventBus.getDefault().postSticky(new BindingEvent("LongPw","1"));
                ActivityUtils.startActivity(SearchDeviceActivity.class);
                break;
            case R.id.tb_short:
                tbLong.setBackground(getResources().getDrawable(R.mipmap.choose_default));
                tbShort.setBackground(getResources().getDrawable(R.mipmap.choose_selected));
                EventBus.getDefault().postSticky(new BindingEvent("ShortPw","2"));
                ActivityUtils.startActivity(SearchDeviceActivity.class);
                finish();
                break;
        }
    }
}
