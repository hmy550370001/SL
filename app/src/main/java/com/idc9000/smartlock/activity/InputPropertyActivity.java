package com.idc9000.smartlock.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ToastUtils;
import com.idc9000.smartlock.HttpCallBack.CommonCallBack;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.bean.RCommonInfo;
import com.idc9000.smartlock.cons.Constants;
import com.idc9000.smartlock.cons.NetworkConst;
import com.idc9000.smartlock.event.InputPropertyEvent;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 房屋物业费用填写
 */

public class InputPropertyActivity extends BaseActivity {
    private static final String TAG = "InputPropertyActivity";
    Context context=this;
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.btn_finish)
    TextView btnFinish;
    @BindView(R.id.et_ammeter)
    EditText etAmmeter;
    @BindView(R.id.et_fire)
    EditText etFire;
    @BindView(R.id.et_water)
    EditText etWater;
    @BindView(R.id.et_note)
    EditText etNote;

   InputPropertyEvent inputProperty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputproperty);
        EventBus.getDefault().register(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        if (Constants.UserState.equals("User")){
            etAmmeter.setEnabled(false);
            etFire.setEnabled(false);
            etWater.setEnabled(false);
            etNote.setEnabled(false);
            btnFinish.setVisibility(View.INVISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onShowMessageEvent(InputPropertyEvent inputPropertyEvent) {
        inputProperty=inputPropertyEvent;
        if (inputProperty.getRemark().equals("null")){
            inputProperty.setRemark(" ");
        }
        try {
            etAmmeter.setText(inputProperty.getEleNumber());
            etFire.setText(inputProperty.getGasNumber());
            etWater.setText(inputProperty.getWaterNumber());
            etNote.setText(inputProperty.getRemark());
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @OnClick(R.id.btn_back)
    public void back() {
        finish();
    }

    @OnClick(R.id.btn_finish)
    public void onClick() {
        inputProperty();
    }

    private void inputProperty() {
        OkHttpUtils
                .post()
                .url(NetworkConst.update)
                .addParams("id", inputProperty.getLockId())
                .addParams("eleNumber", etAmmeter.getText().toString())
                .addParams("gasNumber", etFire.getText().toString())
                .addParams("waterNumber", etWater.getText().toString())
                .addParams("remark", etNote.getText().toString())
                .build()
                .execute(new CommonCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort(e.getLocalizedMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        RCommonInfo rCommonInfo = JSON.parseObject(response,RCommonInfo.class);
                        if (rCommonInfo.isSuccess()){
                            ToastUtils.showShort("更改成功！");
                            finish();
                        }else {
                            ToastUtils.showShort(rCommonInfo.getErrorMsg());
                        }
                    }
                });
    }
}
