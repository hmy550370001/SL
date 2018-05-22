package com.idc9000.smartlock.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;

import butterknife.BindView;

public class AddHomeActivity extends BaseActivity {
    @BindView(R.id.top_bar_left)
    ImageView topBarLeft;
    @BindView(R.id.top_bar_text)
    TextView topBarText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_home);
        initView();
    }

    @Override
    protected void initView() {

    }
}
