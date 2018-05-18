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

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleIndicateCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.idc9000.smartlock.HttpCallBack.CommonCallBack;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.bean.RUserOder;
import com.idc9000.smartlock.cons.Constants;
import com.idc9000.smartlock.cons.NetworkConst;
import com.idc9000.smartlock.event.CurrentDeviceEvent;
import com.idc9000.smartlock.event.ProspectiveValidationResult;
import com.idc9000.smartlock.utils.ActivityUtil;
import com.idc9000.smartlock.utils.bleutils.BleUtils;
import com.idc9000.smartlock.utils.bleutils.HexUtil;
import com.idc9000.smartlock.view.WaveView;
import com.socks.library.KLog;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 长密码录入
 */

public class InputOnlyPasswordActivity extends BaseActivity {
    private static final String TAG = "InputOnlyPassword";
    Context context = this;


    CurrentDeviceEvent currentDevice;
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.wave_view)
    WaveView mWaveView;
    @BindView(R.id.iv_lock)
    ImageView ivLock;
    @BindView(R.id.tv_tip)
    TextView tvTip;

    int orderNumber;
    String action = "ProspectiveValidation";
    BleDevice mbleDevice;//绑定的蓝牙设备


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputpassword);
        initView();
        EventBus.getDefault().register(this);
        initData();
        initBleNotify();
        sendPreVerification();
    }

    //蓝牙通知
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
                        String result = HexUtil.bytesToHexString(data);
                        KLog.e(TAG, "通知数据" + result);
                        switch (action) {
                            //预验证返回
                            case "ProspectiveValidation":
                                resolveProspectiveValidation(result);
                                break;

                            //验证返回
                            case "Validation":
                                resolveValidation(result);
                                break;

                            //更改密码返回
                            case "ChangePassword":
                                resolveChangePassword(result);
                                break;
                        }
                    }
                });
    }

    /**
     * 预验证
     */
    private void sendPreVerification() {
        new Handler().postDelayed(new Runnable() {
            public void run() {

                try {
                    if (Constants.UserState.equals("Admin")) {
                        KLog.e(TAG, "房东预验证: ");
                        Write(BleUtils.prospectiveValidation(Constants.CurrentUserID, 0));
                    } else {
                        KLog.e(TAG, "租户预验证: ");
                        Write(BleUtils.prospectiveValidation(Constants.CurrentUserID, 2));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500);
    }

    /**
     * 更改密码结果
     * @param result
     */
    private void resolveChangePassword(String result) {
        try {
            if (BleUtils.ins9Return(result)) {

                ToastUtils.showShort("录入成功！");
                ivLock.setImageResource(R.mipmap.lockdrawfinish);
                tvTip.setText("设置成功!");
                mWaveView.stop();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finishAddDevice();
                    }
                }, 1000);

            } else {
                ToastUtils.showShort("输入密码失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 设备信息上传成功
     */
    private void finishAddDevice() {
        ActivityUtils.finishActivity(SendPasswordActivity.class);
        ActivityUtils.finishActivity(SearchDeviceActivity.class);
        //跳转界面
        ActivityUtil.start(context, DeviceListActivity.class, true);
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
     * 处理预验证返回结果
     *
     * @param result
     */
    private void resolveProspectiveValidation(String result) {
        final ProspectiveValidationResult prospectiveValidationEvent = BleUtils.receiveProspectiveValidation(result);
        KLog.e(TAG, "onReceiver 预验证返回结果: " + prospectiveValidationEvent.toString());
        if (prospectiveValidationEvent.isSucesss()) {

            //预验证成功 发送验证请求
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    try {
                        if (Constants.UserState.equals("Admin")) {
                            KLog.e(TAG, "run: 发送房主验证请求" + Arrays.toString("87654321".getBytes()));
                            Write(BleUtils.verification(currentDevice.getKeyAdmin().getBytes(), prospectiveValidationEvent.getLockNo(), prospectiveValidationEvent.getRandomNumber()));
                        } else {
                            //发送租客验证请求
                            Write(BleUtils.verification(BleUtils.str2Bcd(currentDevice.getKeyUser()), prospectiveValidationEvent.getLockNo(), prospectiveValidationEvent.getRandomNumber()));
                        }
                        action = "Validation";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 1000);
        }
    }

    /**
     * 当前连接的蓝牙设备
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getBleDevice(BleDevice bleDevice) {
        mbleDevice = bleDevice;
        KLog.e(TAG, "当前连接的蓝牙设备: " + mbleDevice.getName());
    }

    /**
     * 处理验证返回结果
     *
     * @param result
     */
    private void resolveValidation(String result) {
        try {
            if (BleUtils.validateReturn(result)) {
                //发送写入请求
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Constants.UserState.equals("Admin")){
                            //房主更改KeyAdmin
                            KLog.e(TAG, "房主旧的KeyAdmin"+currentDevice.getKeyAdmin());
                            KLog.e(TAG, "getLockNo "+currentDevice.getLockNo() );
                            KLog.e(TAG, "run: "+currentDevice.getLockNo() );
                            KLog.e(TAG, "房主输入的KeyAdmin"+Constants.AdminPassWord+"字节数组："+ Arrays.toString(Constants.AdminPassWord.getBytes()));
                            Write(BleUtils.ins9(currentDevice.getKeyAdmin().getBytes(), BleUtils.str2Bcd(currentDevice.getLockNo()),BleUtils.str2Bcd(Constants.AdminPassWord),(byte)0x00,(byte)orderNumber));
                        }else {
                            KLog.e(TAG, "租户更改KeyUser: "+currentDevice.toString());
                            KLog.e(TAG, "用户输入的KeyUser"+Constants.UserPassWord);
                            Write(BleUtils.ins9(BleUtils.str2Bcd(currentDevice.getKeyUser()),BleUtils.str2Bcd(currentDevice.getLockNo()),BleUtils.str2Bcd(Constants.UserPassWord),(byte)0x02,(byte)orderNumber));
                            currentDevice.setKeyUser(Constants.UserPassWord);
                        }
                        action = "ChangePassword";
                    }
                }, 500);
            } else {
                ToastUtils.showShort("验证失败！此设备已绑定");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initView() {
        super.initView();
        initTopBar();

        mWaveView.setStyle(Paint.Style.FILL);
        mWaveView.setColor(Color.parseColor("#f4ffe2"));
        mWaveView.setInterpolator(new LinearOutSlowInInterpolator());
        mWaveView.start();
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


    @OnClick(R.id.btn_back)
    public void onClick() {
        finish();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
