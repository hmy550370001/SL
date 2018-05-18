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
import com.idc9000.smartlock.bean.KeyInfo;
import com.idc9000.smartlock.bean.RCommonInfo;
import com.idc9000.smartlock.bean.RPassWordResult;
import com.idc9000.smartlock.cons.Constants;
import com.idc9000.smartlock.cons.NetworkConst;
import com.idc9000.smartlock.event.BindingEvent;
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

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 删除钥匙 指纹
 */

public class DeleteKeyActivity extends BaseActivity {
    private static final String TAG = "DeleteKeyActivity";
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
    @BindView(R.id.tv_title)
    TextView tvTitle;
    private String intent;//蓝牙连接的意图
    private String keyID;//钥匙id
    BleDevice mbleDevice;//绑定的蓝牙设备
    String action = "ProspectiveValidation";
    int userOrder;//用户组编号
    int type;//1 删除钥匙  2 删除指纹
    int order;//组内编号
    int passWordId;//密码id
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

                            //删除钥匙
                            case "deleteKey":
                                resolvedeleteKey(result);
                                break;
                        }
                    }
                });
    }

    @Override
    protected void initData() {
        super.initData();
        if (intent.equals("DeleteKey")){
            type=1;
        }else if (intent.equals("DeleteFinger")){
            type=2;
            tvTitle.setText("删除指纹");
            tvTip.setText("请保持您的手机处于当前页面等待删除指纹完成");
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
     * 绑定事件
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onBinding(BindingEvent event) {
        intent = event.getBindType();
    }

    /**
     *
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onKeyInfo(KeyInfo keyInfo) {
        keyID = keyInfo.getId();
        userOrder = keyInfo.getOrderNumber();
        KLog.e(TAG, "keyID: " + keyID + " userOrder:" + userOrder);
    }


    /**
     *
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onPwInfo(RPassWordResult.ResultBean.PasswordsBean passwordsBean) {
        order = passwordsBean.getOrderNumber();
        userOrder=passwordsBean.getUserNumber();
        passWordId=passwordsBean.getId();
        KLog.e(TAG, "指纹密码 组编号: "+userOrder+"组内编号: "+order+"id"+passWordId);
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
     * 处理锁具删除钥匙的结果
     *
     * @param result
     */
    private void resolvedeleteKey(String result) {
        try {
            KLog.e(TAG, "type: "+type);
            if (BleUtils.ins10Return(result)) {
                if(type==1){
                    if (intent.equals("DeleteAllKey")) {
                        deleteAllKey();
                    } else {
                        deleteKey(keyID);
                    }
                }else {
                    deleteFinger();
                }
            } else {
                finishAll("删除失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteFinger() {
        OkHttpUtils
                .post()
                .url(NetworkConst.deletePassWord)
                .addParams("passwordId", String.valueOf(passWordId))
                .build()
                .execute(new CommonCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort(e.getLocalizedMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        KLog.e(TAG, "onResponse: " + response);
                        RCommonInfo rCommonInfo = JSON.parseObject(response, RCommonInfo.class);
                        if (rCommonInfo.isSuccess()) {
                            finishAll("删除成功！");
                        } else {
                            finishAll("删除失败！"+rCommonInfo.getErrorMsg());
                        }

                    }
                });
    }

    /**
     * 删除所有钥匙
     */
    private void deleteAllKey() {
        OkHttpUtils
                .post()
                .url(NetworkConst.deleteAllKey)
                .addParams("lockId", currentDevice.getDeviceId())
                .build()
                .execute(new CommonCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort(e.getLocalizedMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        KLog.e(TAG, "onResponse: " + response);
                        RCommonInfo rCommonInfo = JSON.parseObject(response, RCommonInfo.class);
                        if (rCommonInfo.isSuccess()) {
                            finishAll("删除成功！");
                        } else {
                            finishAll("删除失败！"+rCommonInfo.getErrorMsg());
                        }
                    }
                });
    }

    /**
     * 删除单个钥匙
     *
     * @param keyId
     */
    private void deleteKey(String keyId) {
        OkHttpUtils
                .post()
                .url(NetworkConst.deleteKey)
                .addParams("lockKeyId", keyId)
                .build()
                .execute(new CommonCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort(e.getLocalizedMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        KLog.e(TAG, "onResponse: " + response);
                        RCommonInfo rCommonInfo = JSON.parseObject(response, RCommonInfo.class);
                        if (rCommonInfo.isSuccess()) {
                            finishAll("删除成功！");
                        } else {
                            finishAll("删除失败！"+rCommonInfo.getErrorMsg());
                        }

                    }
                });
    }

    /**
     * 删除成功
     */
    private void finishAll(String msg) {
        tvTip.setText(msg);
        mWaveView.stop();
        finishAddDevice();
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
                        KLog.e(TAG, "run: 发送房主验证请求");
                        //发送房主验证请求
                        Write(BleUtils.verification(currentDevice.getKeyAdmin().getBytes(), prospectiveValidationEvent.getLockNo(), prospectiveValidationEvent.getRandomNumber()));
                        action = "Validation";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 1000);
        }else {
            finishAll("预验证失败！");
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (type==1){
                            if (intent.equals("DeleteAllKey")) {
                                //删除所有钥匙
                                KLog.e(TAG, "run: 删除所有钥匙");
                                Write(BleUtils.ins10((byte) 0xFF,(byte) 0x00,BleUtils.str2Bcd(currentDevice.getLockNo()), currentDevice.getKeyAdmin().getBytes()));
                            } else {
                                //删除一把钥匙
                                Write(BleUtils.ins10((byte)userOrder,(byte) 0x00,BleUtils.str2Bcd(currentDevice.getLockNo()), currentDevice.getKeyAdmin().getBytes()));
                            }
                        }else {
                            KLog.e(TAG, "删除指纹：");
                            if (Constants.UserState.equals("Admin")){
                                Write(BleUtils.ins11((byte)userOrder,(byte)order,(byte)0x00,BleUtils.str2Bcd(currentDevice.getLockNo()), currentDevice.getKeyAdmin().getBytes()));
                            }else {
                                Write(BleUtils.ins11((byte)userOrder,(byte)order,(byte)0x02,BleUtils.str2Bcd(currentDevice.getLockNo()), BleUtils.str2Bcd(currentDevice.getKeyUser())));
                            }
                        }
                    }
                }, 1000);
                action = "deleteKey";
            } else {
                finishAll("验证失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initView() {
        super.initView();
        initTopBar();
        tvTitle.setText("删除钥匙");
        tvTip.setText("请保持您的手机处于当前页面等待删除钥匙完成");
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

    /**
     * 关闭当前页
     */
    private void finishAddDevice() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //跳转界面
                ActivityUtil.start(context, DeviceListActivity.class, true);
            }
        },3000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
