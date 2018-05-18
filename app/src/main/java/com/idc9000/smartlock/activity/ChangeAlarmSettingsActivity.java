package com.idc9000.smartlock.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.view.PickerView;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by 赵先生 on 2017/11/9.
 * 更改报警设置
 */

public class ChangeAlarmSettingsActivity extends BaseActivity {
    private static final String TAG = "ChangeAlarmSetting";
    Context context = this;
    @BindView(R.id.top_bar)
    QMUITopBar topBar;
    @BindView(R.id.time_spinner)
    PickerView timeSpinner;
    @BindView(R.id.btn_submit)
    QMUIRoundButton btnSubmit;

    String time;
    private List<String> data; //

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changealarmsetting);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        initData();
        initTopBar();
        timeSpinner.setData(data);
        timeSpinner.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                    time = text;
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        data = new ArrayList<String>();
        for (int i = 0; i < 11; i++) {
            if (i == 10) {
                data.add(String.valueOf(i));
            } else {
                data.add("0" + i);
            }
        }
    }

    @Override
    protected void initTopBar() {
        super.initTopBar();
        topBar.setTitle("更改报警设置");
        topBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @OnClick(R.id.btn_submit)
    public void onClick() {
        Intent intent = new Intent(context, LockRemindActivity.class);
        if (TextUtils.isEmpty(time)){
            time= String.valueOf(5);
        }
        intent.putExtra("result", time);
        setResult(1, intent);
        finish();
    }
}
