package com.idc9000.smartlock.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 商家服务
 */

public class BusinessServicesActivity extends BaseActivity {
    private static final String TAG = "BusinessServicesActivity";
    Context context = this;

    @BindView(R.id.top_bar_left)
    ImageView topBarLeft;
    @BindView(R.id.top_bar_text)
    TextView topBarText;
    @BindView(R.id.btn_jump)
    QMUIRoundButton btnJump;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_businessservices);
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
        topBarText.setText("商家服务");
        topBarLeft.setImageDrawable(getResources().getDrawable(R.mipmap.back));
    }

    @OnClick({R.id.top_bar_left})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.top_bar_left:
                finish();
                break;
        }
    }
}
