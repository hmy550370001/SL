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
 * 指纹、密码录入
 */

public class InputPasswordActivity extends BaseActivity {
    private static final String TAG = "Ble";
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
    BleDevice mbleDevice;//绑定的蓝牙设备
    String action = "ProspectiveValidation";
    int userOrder;//用户组编号
    int order;//组内编号
    int type;//组合类型

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
                        String result = HexUtil.bytesToHexString(data);
                        KLog.e(TAG, "通知数据" + result);
                        KLog.e(TAG, "收到的事件: " + action);
                        switch (action) {
                            //预验证返回
                            case "ProspectiveValidation":
                                resolveProspectiveValidation(result);
                                break;
                            //验证返回
                            case "Validation":
                                resolveValidation(result);
                                break;
                            //处理第一次录入结果
                            case "InPutPassword_First":
                                resolveInPutPassword_First(result);
                                break;
                            //处理第二次录入结果
                            case "InPutPassword_Second":
                                resolveInPutPassword_Second(result);
                                break;
                            //处理第三次录入
                            case "InPutPassword_Third":
                                resolveInPutPassword_Third(result);
                                break;
                            //处理第四次录入
                            case "InPutPassword_Fourth":
                                resolveInPutPassword_Fourth(result);
                                break;
                        }
                    }
                });
    }

    /**
     * 绑定事件
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onBinding(BindingEvent event) {
        KLog.e(TAG, "onBinding:  "+event.getBindType());
        switch (event.getBindType()){
            case "InputOnlyPassword":
                type=0;
                break;
            case "InputOnlyFingerprint":
                type=1;
                break;
            case "InputPassword":
                type=2;
                break;
        }
        KLog.e(TAG, event.getBindType());
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onShowMessageEvent(RPassWordResult.ResultBean.PasswordsBean passwordsBean) {
        KLog.e(TAG, "组编号："+passwordsBean.getUserNumber()+"组内编号："+passwordsBean.getOrderNumber());
        order=passwordsBean.getOrderNumber();
        userOrder=passwordsBean.getUserNumber();
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
     * 处理第四次录入结果
     *
     * @param result
     */
    private void resolveInPutPassword_Fourth(String result) {
        try {
            if (BleUtils.ins3Return(result)) {
                KLog.e(TAG, "设置成功！ ");
                success();
                upLoadData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void success() {
        ToastUtils.showShort("录入成功！");
        KLog.e(TAG, "resolveInPutPassword_Fourth:  录入成功！");
        ivLock.setImageResource(R.mipmap.lockdrawfinish);
        tvTip.setText("设置成功!");
        mWaveView.stop();
    }

    private void upLoadData() {
        KLog.e(TAG, "upLoadData: "+String.valueOf(currentDevice.getDeviceId()+"name:"+"123456"+"type:"+String.valueOf(type)+"orderNumber"+String.valueOf(order)));
        OkHttpUtils
                .post()
                .url(NetworkConst.addpassword)
                .addParams("lockId", String.valueOf(currentDevice.getDeviceId()))
                .addParams("name", "123456")
                .addParams("type", String.valueOf(type))
                .addParams("orderNumber", String.valueOf(order))
                .build()
                .execute(new CommonCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort(e.getLocalizedMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        KLog.e(TAG, "onResponse: "+response);
                        RCommonInfo rCommonInfo = JSON.parseObject(response,RCommonInfo.class);
                        if (rCommonInfo.isSuccess()){
                            finishAddDevice();
                        }else {
                            ToastUtils.showLong(rCommonInfo.getErrorMsg());
                            finishAddDevice();
                        }
                    }
                });
    }


    /**
     * 设备信息上传成功
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

    /**
     * 处理第三次录入结果
     *
     * @param result
     */
    private void resolveInPutPassword_Third(String result) {
        try {
            if (BleUtils.ins3Return(result)) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if (Constants.UserState.equals("Admin")) {
                            switch (type){
                                case 1:
                                    KLog.e(TAG, "房东单独录入指纹: 第二次录入：");
                                    Write(BleUtils.ins3(currentDevice.getKeyAdmin().getBytes(), BleUtils.str2Bcd(currentDevice.getLockNo()),(byte)order, (byte) userOrder, (byte) 0x04));
                                    action = "InPutPassword_Fourth";
                                    break;
                                case 2:
                                    KLog.e(TAG, "房东录入指纹密码！: 第四次录入：");
                                    Write(BleUtils.ins3(currentDevice.getKeyAdmin().getBytes(), BleUtils.str2Bcd(currentDevice.getLockNo()),(byte)order, (byte) userOrder, (byte) 0x04));
                                    action = "InPutPassword_Fourth";
                                    break;

                            }
                        } else {
                            switch (type){
                                case 1:
                                    KLog.e(TAG, "单独录入指纹: 第二次录入：");
                                    Write(BleUtils.ins3(HexUtil.hexStringToBytes(currentDevice.getKeyUser()), BleUtils.str2Bcd(currentDevice.getLockNo()), (byte)order,(byte) userOrder, (byte) 0x04));
                                    action = "InPutPassword_Fourth";
                                    break;
                                case 2:
                                    KLog.e(TAG, "录入指纹密码！: 第四次录入：");
                                    Write(BleUtils.ins3(HexUtil.hexStringToBytes(currentDevice.getKeyUser()), BleUtils.str2Bcd(currentDevice.getLockNo()), (byte)order,(byte) userOrder, (byte) 0x04));
                                    action = "InPutPassword_Fourth";
                                    break;

                            }
                        }
                   }
                }, 2000);
            }else {
                ToastUtils.showShort("录入指纹失败！");
                finishAddDevice();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理第二次录入结果
     *
     * @param result
     */
    private void resolveInPutPassword_Second(String result) {
        try {
            if (BleUtils.ins3Return(result)) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if (Constants.UserState.equals("Admin")) {
                            switch (type){
                                case 1:
                                    KLog.e(TAG, "房东单独录入指纹: 第一次录入：");
                                    Write(BleUtils.ins3(currentDevice.getKeyAdmin().getBytes(), BleUtils.str2Bcd(currentDevice.getLockNo()),(byte)order, (byte) userOrder, (byte) 0x03));
                                    break;
                                case 2:
                                    KLog.e(TAG, "房东录入指纹密码: 第三次录入：");
                                    Write(BleUtils.ins3(currentDevice.getKeyAdmin().getBytes(), BleUtils.str2Bcd(currentDevice.getLockNo()),(byte)order, (byte) userOrder, (byte) 0x03));
                                    break;
                                case 0:
                                    KLog.e(TAG, "房东单独录入密码成功！");
                                    success();
                                    upLoadData();
                                    break;
                            }
                        } else {
                            switch (type){
                                case 1:
                                    KLog.e(TAG, "租客单独录入指纹: 第一次录入：");
                                    Write(BleUtils.ins3(HexUtil.hexStringToBytes(currentDevice.getKeyUser()), BleUtils.str2Bcd(currentDevice.getLockNo()), (byte)order,(byte) userOrder, (byte) 0x03));
                                    break;
                                case 2:
                                    KLog.e(TAG, "租客录入指纹密码: 第三次录入：");
                                    Write(BleUtils.ins3(HexUtil.hexStringToBytes(currentDevice.getKeyUser()), BleUtils.str2Bcd(currentDevice.getLockNo()), (byte)order,(byte) userOrder, (byte) 0x03));
                                    break;
                                case 0:
                                    KLog.e(TAG, "租户单独录入密码成功！");
                                    success();
                                    upLoadData();
                                    break;
                            }
                        }
                    }
                }, 1000);
            }
            action = "InPutPassword_Third";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理第第一次录入结果
     *
     * @param result
     */
    private void resolveInPutPassword_First(String result) {
        try {
            if (BleUtils.ins3Return(result)) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        KLog.e(TAG, "resolveInPutPassword_First: 第二次录入：");
                        if (Constants.UserState.equals("Admin")) {
                            //第二次录入
                            Write(BleUtils.ins3(currentDevice.getKeyAdmin().getBytes(), BleUtils.str2Bcd(currentDevice.getLockNo()),(byte)order, (byte) userOrder, (byte) 0x02));
                        } else {
                            Write(BleUtils.ins3(HexUtil.hexStringToBytes(currentDevice.getKeyUser()), BleUtils.str2Bcd(currentDevice.getLockNo()),(byte)order, (byte) userOrder, (byte) 0x02));
                        }
                    }

                }, 1000);
                action = "InPutPassword_Second";
            }else {
                ToastUtils.showLong("录入失败！");
                finishAddDevice();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取设备信息
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
                            //发送房主验证请求
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
        }else {
            ToastUtils.showLong("预验证失败！");
            finishAddDevice();
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
                //发送写入请求
                if (Constants.UserState.equals("Admin")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (type==1){
                                KLog.e(TAG, "run 房主请求录入指纹");
                                Write(BleUtils.ins3(currentDevice.getKeyAdmin().getBytes(), BleUtils.str2Bcd(currentDevice.getLockNo()),(byte)order, (byte) userOrder, (byte) 0x03));
                                action = "InPutPassword_Third";
                            }else {
                                KLog.e(TAG, "run 房主请求录入密码");
                                Write(BleUtils.ins3(currentDevice.getKeyAdmin().getBytes(), BleUtils.str2Bcd(currentDevice.getLockNo()),(byte)order, (byte) userOrder, (byte) 0x01));
                                action = "InPutPassword_First";
                            }
                        }
                    }, 1000);

                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (type==1){
                                KLog.e(TAG, "run 租户请求录入指纹");
                                Write(BleUtils.ins3(HexUtil.hexStringToBytes(currentDevice.getKeyUser()), BleUtils.str2Bcd(currentDevice.getLockNo()),(byte)order, (byte) userOrder, (byte) 0x03));
                                action = "InPutPassword_Third";
                            }else {
                                KLog.e(TAG, "run 租户请求录入密码");
                                Write(BleUtils.ins3(HexUtil.hexStringToBytes(currentDevice.getKeyUser()), BleUtils.str2Bcd(currentDevice.getLockNo()),(byte)order, (byte) userOrder, (byte) 0x01));
                                action = "InPutPassword_First";
                            }
                        }
                    }, 1000);
                }

            } else {
                ToastUtils.showShort("验证失败！");
                finishAddDevice();
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
