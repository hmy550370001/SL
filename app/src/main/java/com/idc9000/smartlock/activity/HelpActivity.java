package com.idc9000.smartlock.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by 赵先生 on 2017/10/30.
 * 帮助与反馈
 */

public class HelpActivity extends BaseActivity {
    private static final String TAG = "HelpActivity";
    @BindView(R.id.top_bar_left)
    ImageView topBarLeft;
    @BindView(R.id.top_bar_text)
    TextView topBarText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
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
        topBarText.setText("帮助与反馈");
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
