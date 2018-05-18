package com.idc9000.smartlock.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.bean.RPassWordResult;
import com.idc9000.smartlock.event.BindingEvent;
import com.idc9000.smartlock.event.CurrentDeviceEvent;
import com.idc9000.smartlock.utils.ActivityUtil;
import com.pkmmte.view.CircularImageView;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by 赵先生 on 2017/10/30.
 * 新添锁具密码
 */

public class AddLockPasswordActivity extends BaseActivity {
    @BindView(R.id.title_bar)
    QMUITopBar titleBar;
    @BindView(R.id.btn_add)
    QMUIRoundButton btnAdd;

    private static final String TAG = "AddLockPasswordActivity";
    Context context = this;
    @BindView(R.id.iv_head)
    CircularImageView ivHead;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.btn_rg)
    RadioGroup btnRg;

    int type = 3;
    @BindView(R.id.rb_default)
    RadioButton rbDefault;
    @BindView(R.id.cb_only_pw)
    RadioButton cbOnlyPw;
    @BindView(R.id.cb_only_finger)
    RadioButton cbOnlyFinger;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addlockpassword);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        initTopBar();
        rbDefault.setChecked(true);
    }

    @Override
    protected void initTopBar() {
        super.initTopBar();
        titleBar.setTitle("新添密码/指纹");
        titleBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @OnClick(R.id.btn_add)
    public void onClick() {
        KLog.e(TAG, "type:"+type);
        switch (type) {
            case 1:
                //只录入密码
                EventBus.getDefault().postSticky(new BindingEvent("InputOnlyPassword", null));
                break;
            case 2:
                //只录入指纹
                EventBus.getDefault().postSticky(new BindingEvent("InputOnlyFingerprint", null));
                break;
            case 3:
                //录入指纹和密码
                EventBus.getDefault().postSticky(new BindingEvent("InputPassword", null));
                break;
        }
        ActivityUtils.startActivity(SearchDeviceActivity.class);
        finish();
    }

    @OnClick({R.id.cb_only_pw, R.id.cb_only_finger, R.id.rb_default})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cb_only_pw:
                type = 1;
                break;
            case R.id.cb_only_finger:
                type = 2;
                break;
            case R.id.rb_default:
                type = 3;
                break;
        }
    }
}
