package com.idc9000.smartlock.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ToastUtils;
import com.idc9000.smartlock.HttpCallBack.CommonCallBack;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.adapter.MyExpandableListAdapter;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.bean.RProperty;
import com.idc9000.smartlock.cons.Constants;
import com.idc9000.smartlock.cons.NetworkConst;
import com.idc9000.smartlock.event.CurrentDeviceEvent;
import com.idc9000.smartlock.event.InputPropertyEvent;
import com.idc9000.smartlock.utils.ActivityUtil;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.socks.library.KLog;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by 赵先生 on 2017/10/26.
 * 物业缴费
 */

public class PropertyPayMent extends BaseActivity {
    private static final String TAG = "PropertyPayMent";
    Context context = this;

    @BindView(R.id.title_bar)
    QMUITopBar titleBar;

    @BindView(R.id.progressbar)
    RoundCornerProgressBar progressbar;

    @BindView(R.id.lv_property_payment)
    ExpandableListView lvPropertyPayment;

    String lockId;
    @BindView(R.id.input)
    Button input;
    @BindView(R.id.btn_electricity_meter)
    Button btnElectricityMeter;
    @BindView(R.id.gas)
    Button gas;
    @BindView(R.id.water_meter)
    Button waterMeter;
    @BindView(R.id.tv_power)
    TextView tvPower;

    RProperty rProperty;
    @BindView(R.id.iv_nodata)
    ImageView ivNodata;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propertypayment);
        EventBus.getDefault().register(this);
        initView();
    }


    @Override
    protected void initView() {
        super.initView();
        initTopBar();
        initProgressBar();
        if (!Constants.UserState.equals("Admin")){
            input.setText("查看详情");
        }
    }

    @Override
    protected void initData() {
        super.initData();

        OkHttpUtils
                .post()
                .url(NetworkConst.PropertyPayMent)
                .addParams("lockId", lockId)
                .addParams("currectPage", "10")
                .addParams("pageSize", "1")
                .build()
                .execute(new CommonCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort("网络异常：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        KLog.e(TAG, "PropertyPayMent  onResponse: " + response);
                        rProperty = JSON.parseObject(response, RProperty.class);
                        if (rProperty.isSuccess()) {
                            try {
                                initAdpter();
                                btnElectricityMeter.setText("" + rProperty.getResult().getLock().getEleNumber() + "kwh");
                                gas.setText("" + rProperty.getResult().getLock().getGasNumber() + "方");
                                waterMeter.setText(String.valueOf(rProperty.getResult().getLock().getWaterNumber()) + "吨");
                                tvPower.setText("智能锁剩余电量："+rProperty.getResult().getLock().getBattery()+"%");
                                progressbar.setProgress(Integer.parseInt(String.valueOf(rProperty.getResult().getLock().getBattery())));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            ToastUtils.showShort(rProperty.getErrorMsg());
                        }
                    }
                });

    }

    private void initAdpter() {
        if (rProperty.getResult().getKeys().size() == 0) {
            ivNodata.setVisibility(View.VISIBLE);
        }
        lvPropertyPayment.setAdapter(new MyExpandableListAdapter(this, rProperty));
    }

    private void initProgressBar() {
        progressbar.setProgressBackgroundColor(getResources().getColor(R.color.white));
        progressbar.setProgressColor(getResources().getColor(R.color.progress));
        progressbar.setMax(100);
        progressbar.setProgress(0);
    }

    @Override
    protected void initTopBar() {
        super.initTopBar();
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
        titleBar.setTitle(currentDeviceEvent.getName() + "  物业缴费");
        lockId = currentDeviceEvent.getDeviceId();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @OnClick(R.id.input)
    public void onClick() {
        EventBus.getDefault().postSticky(new InputPropertyEvent(lockId, String.valueOf(rProperty.getResult().getLock().getEleNumber()), String.valueOf(rProperty.getResult().getLock().getGasNumber()), String.valueOf(rProperty.getResult().getLock().getWaterNumber()), String.valueOf(rProperty.getResult().getLock().getRemark())));
        ActivityUtil.start(context, InputPropertyActivity.class, false);
    }
}
