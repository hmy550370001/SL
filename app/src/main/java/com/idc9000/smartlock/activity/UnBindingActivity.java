package com.idc9000.smartlock.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
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
import com.idc9000.smartlock.cons.Constants;
import com.idc9000.smartlock.cons.NetworkConst;
import com.idc9000.smartlock.event.CurrentDeviceEvent;
import com.idc9000.smartlock.event.ProspectiveValidationResult;
import com.idc9000.smartlock.utils.ActivityUtil;
import com.idc9000.smartlock.utils.bleutils.BleUtils;
import com.idc9000.smartlock.utils.bleutils.HexUtil;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.socks.library.KLog;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import at.grabner.circleprogress.CircleProgressView;
import butterknife.BindView;
import okhttp3.Call;

/**
 * 解绑界面
 */

public class UnBindingActivity extends BaseActivity {
    private static final String TAG = "UnBindingActivity";
    Context context = this;
    @BindView(R.id.top_bar)
    QMUITopBar topBar;
    @BindView(R.id.circleView)
    CircleProgressView circleView;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.et_lockname)
    EditText etLockname;


    int proLen = 0;//进度
    Timer timer = new Timer();//计时器
    boolean verificationSuccess;//绑定结果
    BleDevice mbleDevice;//绑定的蓝牙设备

    String action = "ProspectiveValidation";
    byte[] keyAdmin;
    String deviceId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binddevice);
        EventBus.getDefault().register(this);
        initView();
        initBleNotify();
        sendPreVerification();
    }

    private void sendPreVerification() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                try {
                    //开始进度条
                    timer.schedule(task, 1000, 100);       // timeTask
                    Write(BleUtils.prospectiveValidation(SPUtils.getInstance().getString("CurrentUserID"), 0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500);
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

                            //解绑返回
                            case "UnBind":
                                resolveUnBind(result);
                        }
                    }
                });
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
     * 处理解绑结果
     *
     * @param result
     */
    private void resolveUnBind(String result) {
        try {
            if (BleUtils.ins6Return(result)) {
                verificationSuccess = true;
                KLog.e(TAG, "resolveUnBind: 解绑成功" );
                ToastUtils.showLong("解绑成功！");
            } else {
                verificationSuccess=false;
                KLog.e(TAG, "resolveUnBind: 解绑失败" );
                ToastUtils.showLong("解绑失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                    public void run() {
                        //发送解绑请求
                        Write(BleUtils.ins6(keyAdmin));
                        action = "UnBind";
                    }
                }, 500);
            } else {
                verificationSuccess=false;
                ToastUtils.showShort("验证失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理预验证返回结果
     *
     * @param result
     */
    private void resolveProspectiveValidation(String result) {
        final ProspectiveValidationResult prospectiveValidationEvent = BleUtils.receiveProspectiveValidation(result);
        KLog.e(TAG, "onReceiver: " + prospectiveValidationEvent.toString());
        if (prospectiveValidationEvent.isSucesss()) {
            //预验证成功 发送验证请求
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    try {
                        //发送验证请求
                        KLog.e(TAG, "预验证成功 发送验证请求 verification: keyAdmin:" + Arrays.toString(keyAdmin));
                        Write(BleUtils.verification(keyAdmin, prospectiveValidationEvent.getLockNo(), prospectiveValidationEvent.getRandomNumber()));
                        action = "Validation";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 500);
        }else {
            verificationSuccess=false;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onShowUnBindingEvent(CurrentDeviceEvent currentDeviceEvent) {
        keyAdmin = currentDeviceEvent.getKeyAdmin().getBytes();
        deviceId = currentDeviceEvent.getDeviceId();
    }


    @Override
    protected void initView() {
        super.initView();
        initTopBar();
        tvTip.setText("    注：解除绑定需要一定时间，请您稍候。");
    }

    @Override
    protected void initTopBar() {
        super.initTopBar();
        topBar.setTitle("设备解绑");
        topBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 进度条倒计时
     */
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            // UI thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initPro();
                }

                private void initPro() {
                    proLen++;
                    try {
                        circleView.setSeekModeEnabled(false);
                        circleView.setValue(proLen);

                        if (verificationSuccess) {
                            if (proLen == 100) {
                                KLog.e(TAG, "releaseLock ");
                                timer.cancel();
                                releaseLock();
                            }
                        }else {
                            if (proLen == 90) {
                                ToastUtils.showShort("认证失败!");
                                timer.cancel();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


    /**
     * 设备信息上传成功
     */
    private void finishAddDevice() {
        BleManager.getInstance().disconnect(Constants.bleDevice);
        if (!BleManager.getInstance().isConnected(Constants.bleDevice)){
            Constants.bleDevice=null;
        }
        //跳转界面
        ActivityUtil.start(context, DeviceListActivity.class, true);
    }

    /**
     * 请求更改解绑状态
     */
    private void releaseLock() {
        OkHttpUtils
                .post()
                .url(NetworkConst.release)
                .addParams("lockId", deviceId)
                .build()
                .execute(new CommonCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort("网络异常：" + e.getLocalizedMessage());

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        RCommonInfo rCommonInfo = JSON.parseObject(response, RCommonInfo.class);
                        if (rCommonInfo.isSuccess()) {
                            finishAddDevice();
                        } else {
                            ToastUtils.showShort(rCommonInfo.getErrorMsg());
                        }
                    }
                });

    }

    /**
     * 蓝牙写入数据
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        //反注册EventBus
        EventBus.getDefault().unregister(this);
    }
}
