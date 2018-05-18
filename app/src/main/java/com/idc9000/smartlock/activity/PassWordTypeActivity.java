package com.idc9000.smartlock.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleIndicateCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.cons.Constants;
import com.idc9000.smartlock.event.BindingEvent;
import com.idc9000.smartlock.event.CurrentDeviceEvent;
import com.idc9000.smartlock.event.ProspectiveValidationResult;
import com.idc9000.smartlock.utils.ActivityUtil;
import com.idc9000.smartlock.utils.bleutils.BleUtils;
import com.idc9000.smartlock.utils.bleutils.HexUtil;
import com.idc9000.smartlock.view.WaveView;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/1/9.
 */

public class PassWordTypeActivity extends BaseActivity {
    Context context = this;
    private static final String TAG = "PassWordTypeActivity";
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.wave_view)
    WaveView mWaveView;
    @BindView(R.id.iv_lock)
    ImageView ivLock;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    String action = "ProspectiveValidation";
    CurrentDeviceEvent currentDevice;
    String intent;
    BleDevice mbleDevice;//绑定的蓝牙设备
    byte safeMode;//安全模式
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputpassword);
        EventBus.getDefault().register(this);
        initData();
        initBleNotify();
        sendPreVerification();
    }
    /**
     * 当前连接的蓝牙设备
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getBleDevice(BleDevice bleDevice) {
        mbleDevice = bleDevice;
        KLog.e(TAG, "当前连接的蓝牙设备: " + mbleDevice.getName());
    }

    private void initBleNotify() {
        // TODO 接收数据的监听
        BleManager.getInstance().indicate(
                mbleDevice,
                Constants.uuid_service,
                Constants.UUID_NOTIFY,
                new BleIndicateCallback() {
                    @Override
                    public void onIndicateSuccess() {
                        KLog.e(TAG, "打开通知操作成功 ");
                    }

                    @Override
                    public void onIndicateFailure(BleException exception) {
                        KLog.e(TAG, "打开通知操作失败");
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        //返回的结果
                        String result = HexUtil.bytesToHexString(data);
                        KLog.e(TAG, "onReceiver: " + action);
                        switch (action) {
                            //预验证返回
                            case "ProspectiveValidation":
                                resolveProspectiveValidation(result);
                                break;

                            //验证返回
                            case "Validation":
                                resolveValidation(result);
                                break;

                            //设置方式
                            case "setType":
                                resolvedeleteKey(result);
                                break;
                        }
                    }
                });
    }

    @Override
    protected void initView() {
        super.initView();
        initTopBar();
        tvTitle.setText("设置开门密码方式");
        tvTip.setText("请保持您的手机处于当前页面等待设置完成");
        mWaveView.setStyle(Paint.Style.FILL);
        mWaveView.setColor(Color.parseColor("#f4ffe2"));
        mWaveView.setInterpolator(new LinearOutSlowInInterpolator());
        mWaveView.start();
    }
    /**
     * 绑定事件
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onBinding(BindingEvent event) {
        intent = event.getBindType();
    }
    /**
     * 获取设备信息
     *
     * @param currentDeviceEvent
     */
    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    public void onShowMessageEvent(CurrentDeviceEvent currentDeviceEvent) {
        currentDevice = new CurrentDeviceEvent();
        currentDevice = currentDeviceEvent;
        KLog.e(TAG, "currentDeviceEvent: " + currentDevice.toString());
    }

    /**
     * 预验证
     */
    private void sendPreVerification() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                try {
                    Write(BleUtils.prospectiveValidation(Constants.CurrentUserID, 0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500);
    }

    /**
     * 处理预验证返回结果
     *
     * @param result
     */
    private void resolveProspectiveValidation(String result) {
        final ProspectiveValidationResult prospectiveValidationEvent = BleUtils.receiveProspectiveValidation(result);
        KLog.e(TAG, "预验证返回结果: " + prospectiveValidationEvent.toString());
        if (prospectiveValidationEvent.isSucesss()) {
            //预验证成功 发送验证请求
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    try {
                        KLog.e(TAG, "run: 发送房主验证请求");
                        //发送房主验证请求
                        Write(BleUtils.verification(currentDevice.getKeyAdmin().getBytes(), prospectiveValidationEvent.getLockNo(), prospectiveValidationEvent.getRandomNumber()));
                        action = "Validation";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 1000);
        }
    }

    /**
     * 处理验证返回结果
     *
     * @param result
     */
    private void resolveValidation(String result) {
        try {
            if (BleUtils.validateReturn(result)) {
                if (currentDevice.getSafeMode()==0){
                    safeMode=0x00;
                }else {
                    safeMode=0x01;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (intent.equals("LongPw")){
                            KLog.e(TAG, "LongPw");
                            Write(BleUtils.ins4(currentDevice.getKeyAdmin().getBytes(),safeMode, (byte)0x00));
                        }else {
                            KLog.e(TAG, "shortPw");
                            Write(BleUtils.ins4(currentDevice.getKeyAdmin().getBytes(),safeMode, (byte)0x01));
                        }
                    }
                }, 500);
                action = "setType";
            } else {
                ToastUtils.showShort("验证失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 处理锁具删除钥匙的结果
     *
     * @param result
     */
    private void resolvedeleteKey(String result) {
        try {
            if (BleUtils.ins10Return(result)) {
                setFinish("设置成功！");
            } else {
                setFinish("设置失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_back)
    public void onClick() {
        finish();
    }
    /**
     * 关闭当前页
     */
    private void finishAddDevice() {
        //跳转界面
        ActivityUtil.start(context, DeviceListActivity.class, true);
    }
    /**
     * 写入数据
     */
    public void Write(final String value) {
        BleManager.getInstance().write(
                mbleDevice,
                Constants.uuid_service,
                Constants.UUID_WRITE,
                HexUtil.hexStringToBytes(value),
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess() {
                        KLog.e(TAG, "发送数据到设备成功: " + value);
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        KLog.e(TAG, "发送数据到设备失败: " + value);
                    }
                });
    }



    /**
     * 删除成功
     */
    private void setFinish(String msg) {
        tvTip.setText(msg);
        mWaveView.stop();
        ToastUtils.showShort(msg);
        finishAddDevice();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
