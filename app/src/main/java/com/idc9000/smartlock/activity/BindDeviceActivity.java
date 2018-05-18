package com.idc9000.smartlock.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.idc9000.smartlock.HttpCallBack.UploadAddLockCallBack;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.bean.RResult;
import com.idc9000.smartlock.bean.RUserOder;
import com.idc9000.smartlock.cons.Constants;
import com.idc9000.smartlock.cons.NetworkConst;
import com.idc9000.smartlock.event.CurrentDeviceEvent;
import com.idc9000.smartlock.event.ProspectiveValidationResult;
import com.idc9000.smartlock.utils.ActivityUtil;
import com.idc9000.smartlock.utils.bleutils.BleUtils;
import com.idc9000.smartlock.utils.bleutils.EncryptUtils;
import com.idc9000.smartlock.utils.bleutils.HexUtil;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.socks.library.KLog;
import com.zhy.http.okhttp.OkHttpUtils;

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
import okhttp3.Call;


/**
 * Created by 赵先生 on 2017/10/17.
 * 智能锁绑定
 */

public class BindDeviceActivity extends BaseActivity {

    private static final String TAG = "BindDeviceActivity";
    Context context = this;

    @BindView(R.id.top_bar)
    QMUITopBar topBar;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.et_lockname)
    EditText et_lockname;
    @BindView(R.id.circleView)
    CircleProgressView circleView;

    int orderNumber;//用户组编号
    int proLen = 0; //进度
    Timer timer = new Timer();  //计时器
    boolean verificationSuccess;    //绑定结果

    String action = "ProspectiveValidation";
    BleDevice mbleDevice;//绑定的蓝牙设备

    CurrentDeviceEvent currentDevice;//当前设备

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binddevice);
        initView();
        EventBus.getDefault().register(this);
        initData();
        setKeyBoardEnter();
        initBleListener();
        sendPreVerification();
    }

    /**
     * 绑定事件
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getBleDevice(BleDevice bleDevice) {
        mbleDevice = bleDevice;
        KLog.i(TAG, "当前连接的蓝牙设备: " + mbleDevice.getName());
    }

    @Override
    protected void initData() {
        super.initData();
        //房东
        if (Constants.UserState.equals("Admin")) {
            currentDevice = new CurrentDeviceEvent();
            currentDevice.setKeyAdmin(Constants.AdminPassWord);
            KLog.i(TAG, "KeyAdmin" + Constants.AdminPassWord);
        } else {
            getUserOrderNumber();
        }
    }

    /**
     * 预验证
     */
    private void sendPreVerification() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //开始进度条
                timer.schedule(task, 1000, 100);       // timeTask
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
     * 获取设备信息
     *
     * @param currentDeviceEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void InitCurrentDeviceEvent(CurrentDeviceEvent currentDeviceEvent) {
        if (Constants.UserState.equals("User")) {
            currentDevice = currentDeviceEvent;
            KLog.i(TAG, "UserCurrentDevice: " + currentDevice.toString());
        }
    }


    /**
     * 蓝牙数据监听
     */
    private void initBleListener() {
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
                        KLog.i(TAG, "通知数据" + result);
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

                            //写入住时间
                            case "InputTime":
                                resolveInputTime(result);
                                break;
                        }
                    }
                });
    }

    /**
     * 处理写入时间返回结果
     *
     * @param result
     */
    private void resolveInputTime(String result) {
        try {
            if (BleUtils.ins7Return(result)) {
                KLog.i(TAG, "写入 入住离开时间成功！");
                verificationSuccess = true;
            } else {
                ToastUtils.showShort("写入 入住离开时间 失败！");
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
        KLog.i(TAG, "预验证返回结果: " + prospectiveValidationEvent.toString());
        if (prospectiveValidationEvent.isSucesss()) {
            //预验证成功 发送验证请求
            new Handler().postDelayed(new Runnable() {
                public void run() {

                    if (Constants.UserState.equals("Admin")) {
                        //发送房主验证请求
                        currentDevice.setLockNo(BleUtils.bcd2Str(prospectiveValidationEvent.getLockNo()));
                        currentDevice.setKeyDev(BleUtils.bcd2Str(prospectiveValidationEvent.getKeyDev()));
                        currentDevice.setKeyUser(BleUtils.keyUser(currentDevice.getLockNo(), Constants.AdminPassWord));
                        KLog.i(TAG, "预验证返回 :" + currentDevice.toString());
                        Write(BleUtils.verification(BleUtils.DefaultKeyAdmin, prospectiveValidationEvent.getLockNo(), prospectiveValidationEvent.getRandomNumber()));
                    } else {
                        //发送租客验证请求
                        KLog.i(TAG, "租客：发送验证请求");
                        Write(BleUtils.verification(BleUtils.str2Bcd(currentDevice.getKeyUser()), prospectiveValidationEvent.getLockNo(), prospectiveValidationEvent.getRandomNumber()));
                    }
                    action = "Validation";
                }
            }, 500);
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Constants.UserState.equals("Admin")) {
                            //房主更改KeyAdmin
                            Write(BleUtils.ins1(SPUtils.getInstance().getString("CurrentUserID"), BleUtils.DefaultKeyAdmin, Constants.AdminPassWord));
                        } else {
                            Write(BleUtils.ins9(BleUtils.str2Bcd(currentDevice.getKeyUser()), BleUtils.str2Bcd(currentDevice.getLockNo()), BleUtils.str2Bcd(Constants.UserPassWord), (byte) 0x02, (byte) orderNumber));
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


    /**
     * 绑定结果
     *
     * @param result
     */
    private void resolveChangePassword(String result) {
        try {
            if (BleUtils.ins9Return(result)) {
                if (Constants.UserState.equals("Admin")) {
                    verificationSuccess = true;
                    KLog.i(TAG, "resolveChangePassword: 输入密码成功！");
                } else {
                    //写入入住离店时间
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            input_time();
                        }
                    }, 500);
                }
            } else {
                ToastUtils.showShort("输入密码失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入入住离店时间
     */
    private void input_time() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = null;
        Date endDate = null;

        try {
            startDate = simpleDateFormat.parse(currentDevice.getStartTime());
            endDate = simpleDateFormat.parse(currentDevice.getEndTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        KLog.i(TAG, "input_time: " + Arrays.toString(BleUtils.str2Bcd(currentDevice.getLockNo())) + Arrays.toString(EncryptUtils.dateToBytes4(startDate)) + Arrays.toString(EncryptUtils.dateToBytes4(endDate)) + Arrays.toString(EncryptUtils.str2Bcd(currentDevice.getKeyAdmin())));
        Write(BleUtils.ins7(BleUtils.str2Bcd(currentDevice.getLockNo()), EncryptUtils.dateToBytes4(startDate), EncryptUtils.dateToBytes4(endDate), currentDevice.getKeyAdmin().getBytes()));

        action = "InputTime";
    }


    /**
     * 设置键盘回车
     */
    private void setKeyBoardEnter() {
        et_lockname.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (KeyEvent.KEYCODE_ENTER == keyCode && KeyEvent.ACTION_DOWN == event.getAction()) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null && imm.isActive()) {
                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                        if (Constants.UserState.equals("Admin")) {
                            upLoadDevice();
                        }
                    }
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 更新租户绑定状态
     */
    private void upLoadUserKey() {
        OkHttpUtils
                .post()
                .url(NetworkConst.updatelockKey)
                .addParams("lockId", currentDevice.getDeviceId())
                .addParams("receiveFlag", "1")
                .build()
                .execute(new UploadAddLockCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort("网络异常：" + e.getLocalizedMessage());
                    }

                    @Override
                    public void onResponse(RResult response, int id) {
                        KLog.e(TAG, "onResponse: " + response.toString());
                        if (response.isSuccess()) {
                            KLog.i(TAG, "添加成功！");
                            finishAddDevice();
                        } else {
                            ToastUtils.showShort(response.getErrorMsg());
                        }
                    }
                });
    }

    /**
     * 上传设备
     */
    private void upLoadDevice() {
        OkHttpUtils
                .post()
                .url(NetworkConst.ADDLOCK_URL)
                .addParams("keyUser", currentDevice.getKeyUser())
                .addParams("keyAdmin", currentDevice.getKeyAdmin())
                .addParams("keyNumber", currentDevice.getLockNo())
                .addParams("keyDev", currentDevice.getKeyDev())
                .addParams("keyDev", currentDevice.getKeyDev())
                .addParams("name", et_lockname.getText().toString())
                .addParams("battery", Constants.battery)
                .addParams("deviceName", Constants.deviceName)
                .addParams("mac", Constants.Mac)
                .build()
                .execute(new UploadAddLockCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort("网络异常：" + e.getLocalizedMessage());
                    }

                    @Override
                    public void onResponse(RResult response, int id) {
                        KLog.i(TAG, "onResponse: " + response.toString());
                        if (response.isSuccess()) {
                            Log.e(TAG, "添加成功！ ");
                            finishAddDevice();
                        } else {
                            ToastUtils.showShort(response.getErrorMsg());
                        }
                    }
                });
    }

    /**
     * 设备信息上传成功
     */
    private void finishAddDevice() {
        //跳转界面
        ActivityUtil.start(context, DeviceListActivity.class, true);
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
                        KLog.i(TAG, "发送数据到设备成功: " + value);
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
                                ToastUtils.showShort("认证成功!");
                                timer.cancel();
                                if (Constants.UserState.equals("Admin")) {
                                    et_lockname.setVisibility(View.VISIBLE);
                                } else {
                                    //更新锁具绑定状态
                                    upLoadUserKey();
                                }
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

    @Override
    protected void initView() {
        super.initView();
        initTopBar();
    }

    @Override
    protected void initTopBar() {
        super.initTopBar();
        topBar.setTitle("智能锁绑定");
        topBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAddDevice();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //反注册EventBus
        EventBus.getDefault().unregister(this);
    }

    /**
     * 获取用户组编号
     */
    private void getUserOrderNumber() {
        OkHttpUtils
                .post()
                .url(NetworkConst.getUserOrderNumber)
                .addParams("lockId", currentDevice.getDeviceId())
                .build()
                .execute(new CommonCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort("网络异常：" + e.getLocalizedMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        KLog.json(response);
                        RUserOder rUserOder = JSON.parseObject(response, RUserOder.class);
                        if (rUserOder.isSuccess()) {
                            orderNumber = rUserOder.getResult().getUserOrder();
                        } else {
                            ToastUtils.showShort(rUserOder.getErrorMsg());
                        }
                    }
                });
    }
}

