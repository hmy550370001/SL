package com.idc9000.smartlock.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleIndicateCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.cons.Constants;
import com.idc9000.smartlock.event.CurrentDeviceEvent;
import com.idc9000.smartlock.event.ProspectiveValidationResult;
import com.idc9000.smartlock.event.UpdateLeaseEvent;
import com.idc9000.smartlock.utils.ActivityUtil;
import com.idc9000.smartlock.utils.bleutils.BleUtils;
import com.idc9000.smartlock.utils.bleutils.EncryptUtils;
import com.idc9000.smartlock.utils.bleutils.HexUtil;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import at.grabner.circleprogress.CircleProgressView;
import butterknife.BindView;

/**
 * 租户更新租期
 */

public class UserUpdateLeaseActivity extends BaseActivity {
    private static final String TAG = "UserUpdateLeaseActivity";
    Context context = this;
    @BindView(R.id.top_bar)
    QMUITopBar topBar;
    @BindView(R.id.circleView)
    CircleProgressView circleView;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.et_lockname)
    EditText etLockname;

    BleDevice mbleDevice;//绑定的蓝牙设备
    int proLen = 0;//进度
    Timer timer = new Timer();//计时器
    boolean verificationSuccess;//绑定结果
    String action = "ProspectiveValidation";
    byte[] keyAdmin;
    String deviceId;

    UpdateLeaseEvent updateLeaseEvent;
    CurrentDeviceEvent currentDeviceEvent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binddevice);
        EventBus.getDefault().register(this);
        initView();
        initData();
        initBleNotify();
        sendPreVerification();
    }

    /**
     * 绑定事件
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getBleDevice(BleDevice bleDevice) {
        mbleDevice = bleDevice;
    }

    private void sendPreVerification() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //开始进度条
                timer.schedule(task, 1000, 100);       // timeTask
                try {
                    Write(BleUtils.prospectiveValidation(SPUtils.getInstance().getString("CurrentUserID"), 0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500);
    }


    /**
     * 处理写入时间返回结果
     */

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

                            //写入住时间
                            case "InputTime":
                                resolveInputTime(result);
                                break;
                        }
                    }
                });
    }

    private void resolveInputTime(String result) {
        try {
            if (BleUtils.ins7Return(result)){
                tvTip.setText("写入时间成功！");
                verificationSuccess=true;
            }else {
                ToastUtils.showShort("写入时间失败！");
                tvTip.setText("写入时间失败！");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取设备信息
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void InitCurrentDeviceEvent(CurrentDeviceEvent event) {
        if (Constants.UserState.equals("User")){
            currentDeviceEvent=new CurrentDeviceEvent();
            currentDeviceEvent=event;
            KLog.e(TAG, "UserCurrentDevice: "+currentDeviceEvent.toString() );
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
                tvTip.setText("验证身份成功！");
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        input_time();
                    }
                }, 500);
                action = "InputTime";
            } else {
                tvTip.setText("验证身份失败！");
                finishAddDevice();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    public void onMessage(UpdateLeaseEvent event) {
       updateLeaseEvent =new UpdateLeaseEvent();
       updateLeaseEvent=event;
        KLog.e(TAG, "onMessage: "+updateLeaseEvent.toString() );
    }
    /**
     * 写入入住离店时间
     */
    private void input_time() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = null;
        Date endDate=null;

        try {
            startDate = simpleDateFormat.parse(currentDeviceEvent.getStartTime());
            endDate = simpleDateFormat.parse(currentDeviceEvent.getEndTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        KLog.e(TAG, "input_time: "+ Arrays.toString(BleUtils.str2Bcd(currentDeviceEvent.getLockNo())) + Arrays.toString(EncryptUtils.dateToBytes4(startDate)) + Arrays.toString(EncryptUtils.dateToBytes4(endDate)) + Arrays.toString(EncryptUtils.str2Bcd(currentDeviceEvent.getKeyAdmin())));
        Write(BleUtils.ins7(BleUtils.str2Bcd(currentDeviceEvent.getLockNo()), EncryptUtils.dateToBytes4(startDate), EncryptUtils.dateToBytes4(endDate),currentDeviceEvent.getKeyAdmin().getBytes()));

        action = "InputTime";
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 500);
            action = "Validation";
        }else {
            tvTip.setText("预验证失败！");
            finishAddDevice();
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
        tvTip.setText("    注：更改租期需要一定时间，请您稍候。");
    }

    @Override
    protected void initTopBar() {
        super.initTopBar();
        topBar.setTitle("更改租期");
        topBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
                                timer.cancel();
                                finishAddDevice();
                            }

                        } else {
                            if (proLen == 50) {
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

        new Handler().postDelayed(new Runnable() {
            public void run() {
                //跳转界面
                ActivityUtil.start(context, DeviceListActivity.class, true);
            }
        }, 2000);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //反注册EventBus
        EventBus.getDefault().unregister(this);
    }
}
