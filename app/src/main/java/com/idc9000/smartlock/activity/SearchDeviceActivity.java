package com.idc9000.smartlock.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanAndConnectCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.idc9000.smartlock.MainActivity;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.bean.RScanDevice;
import com.idc9000.smartlock.cons.Constants;
import com.idc9000.smartlock.event.BindingEvent;
import com.idc9000.smartlock.event.CurrentDeviceEvent;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.socks.library.KLog;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;


/**
 * Created by 赵先生 on 2017/10/17.
 * 搜索设备并连接
 */

public class SearchDeviceActivity extends BaseActivity {

    private static final String TAG = "SearchDeviceActivity";
    @BindView(R.id.top_bar)
    QMUITopBar topBar;
    private Context context = this;
    private String intent="AdminBinding";

    private RScanDevice rScanDevice; //搜索到的设备

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finddevice);
        EventBus.getDefault().register(this);
        initView();

        if (intent.equals("AdminBinding")){
            //房东第一次绑定需要扫描二维码
            Intent Zxing = new Intent(context, CaptureActivity.class);
            startActivityForResult(Zxing, 100);
        }else {
            checkPermissions();
        }
    }


    /**
     * 绑定事件
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.POSTING)
    public void onBinding(BindingEvent event) {
        intent = event.getBindType();
        KLog.e(TAG, event.getBindType());
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onShowMessageEvent(CurrentDeviceEvent currentDeviceEvent) {
        Constants.Mac=currentDeviceEvent.getMac();
        Constants.deviceName=currentDeviceEvent.getDeviceName();
    }

    //扫描二维码后返回的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    KLog.e(TAG, "onActivityResult: "+result);
                    rScanDevice = JSON.parseObject(result,RScanDevice.class);
                    //设备名
                    Constants.deviceName=rScanDevice.getDeviceName();
                    //MAC地址
                    Constants.Mac=rScanDevice.getMac();
                    //检查蓝牙状态  连接设备
                    checkPermissions();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(context, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }else {
                finish();
            }
        }
    }

    /**
     * 扫描并自动连接
     */
    private void scanAndConnect() {
        BleManager.getInstance().scanAndConnect(new BleScanAndConnectCallback() {
            @Override
            public void onScanStarted(boolean success) {
                KLog.e(TAG, "开始扫描: " + success);
                try {
                    showProgressDialog(context, "正在搜索锁具...");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onScanFinished(BleDevice scanResult) {
                try {
                    dismissProgressDialog();
                }catch (Exception e){
                    e.printStackTrace();
                }
                // 扫描结束，结果即为扫描到的第一个符合扫描规则的BLE设备，如果为空表示未搜索到（主线程）
                if (scanResult==null){
                    ToastUtils.showShort("没有扫描到符合条件的设备");
                    finish();
                }
            }

            @Override
            public void onStartConnect() {
                KLog.e(TAG, "开始连接: ");
                showProgressDialog(context, "开始连接...");
            }

            @Override
            public void onConnectFail(BleException exception) {
                KLog.e(TAG, "连接失败: ");
                try {
                    dismissProgressDialog();
                }catch (Exception e){
                    e.printStackTrace();
                }
                connectDevice();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                // 连接成功，BleDevice即为所连接的BLE设备（主线程）
                KLog.e(TAG, "蓝牙连接成功: " + bleDevice.getName());
                Constants.bleDevice =bleDevice;
                try {
                    dismissProgressDialog();
                }catch (Exception e){
                    e.printStackTrace();
                }
                ToastUtils.showLong("设备已连接！");
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    connectFinish(bleDevice);
                }
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                // 连接断开，isActiveDisConnected是主动断开还是被动断开（主线程）
                if (isActiveDisConnected) {
                    KLog.e(TAG, "蓝牙连接主动断开");
                } else {
                    KLog.e(TAG, "蓝牙连接被动断开");
                    scanAndConnect();
                }
            }
        });
    }

    //连接成功
    private void connectFinish(BleDevice bleDevice) {
        EventBus.getDefault().postSticky(bleDevice);
        switch (intent) {
            case "AdminBinding":
                //房东初次绑定设备
                ActivityUtils.startActivity(SendPasswordActivity.class);
                finish();
                break;
            case "UserBinding":
                //租客初次绑定设备
                ActivityUtils.startActivity(SendPasswordActivity.class);
                finish();
                break;
            case "UnBingDing":
                //房东解绑设备
                ActivityUtils.startActivity(UnBindingActivity.class);
                finish();
                break;
            case "InputPassword":
                //录入指纹和密码
                ActivityUtils.startActivity(InputPasswordActivity.class);
                finish();
                break;
            case "InputOnlyPassword":
                //录入密码
                ActivityUtils.startActivity(InputPasswordActivity.class);
                finish();
                break;
            case "InputOnlyFingerprint":
                //录入指纹
                ActivityUtils.startActivity(InputPasswordActivity.class);
                finish();
                break;
            case "UserUpdateLease":
                //租客更新入住 离开时间
                ActivityUtils.startActivity(UserUpdateLeaseActivity.class);
                finish();
                break;
            case "DeleteKey":
                //删除单个钥匙
                ActivityUtils.startActivity(DeleteKeyActivity.class);
                finish();
                break;
            case "DeleteAllKey":
                //删除所有钥匙
                ActivityUtils.startActivity(DeleteKeyActivity.class);
                finish();
                break;
            case "LongPwSetting":
                //长密码设置（修改长密码）
                ActivityUtils.startActivity(SendPasswordActivity.class);
                finish();
                break;
            case "DeleteFinger":
                //删除指纹
                ActivityUtils.startActivity(DeleteKeyActivity.class);
                finish();
                break;
            case "LongPw":
                //选择长密码状态
                ActivityUtils.startActivity(PassWordTypeActivity.class);
                finish();
                break;
            case "ShortPw":
                //选择指纹密码状态
                ActivityUtils.startActivity(PassWordTypeActivity.class);
                finish();
                break;
        }
    }


    //检查蓝牙状态是否开启
    private void checkPermissions() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            //开启蓝牙
            KLog.e("开启蓝牙");
            bluetoothAdapter.enable();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (null == Constants.bleDevice) {
                        KLog.e(TAG, "蓝牙首次连接");
                        //连接设备
                        connectDevice();
                    } else {
                        if (BleManager.getInstance().isConnected(Constants.bleDevice)) {
                            KLog.e(TAG, "蓝牙已连接 直接操作");
                            connectFinish(Constants.bleDevice);
                        } else {
                            KLog.e(TAG, "蓝牙已断开 重新连接");
                            connectDevice();
                        }
                    }
                }
            }, 1000);
        }else {
            KLog.e("蓝牙已开启");
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (null == Constants.bleDevice) {
                        KLog.e(TAG, "蓝牙首次连接");
                        //连接设备
                        connectDevice();
                    } else {
                        if (BleManager.getInstance().isConnected(Constants.bleDevice)) {
                            KLog.e(TAG, "蓝牙已连接 直接操作");
                            connectFinish(Constants.bleDevice);
                        } else {
                            KLog.e(TAG, "蓝牙已断开 重新连接");
                            connectDevice();
                        }
                    }
                }
            }, 1000);
        }
    }

    /**
     * 连接设备
     */
    private void connectDevice() {
        KLog.e(TAG, "设备deviceName："+Constants.deviceName+"Mac:" +Constants.Mac);
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setMaxConnectCount(7)
                .setOperateTimeout(10000);

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setDeviceName(true, Constants.deviceName)         // 只扫描指定广播名的设备，可选
                .setDeviceMac(Constants.Mac)                  // 只扫描指定mac的设备，可选
                .setAutoConnect(true)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(10000)              // 扫描超时时间，可选，默认10秒；小于等于0表示不限制扫描时间
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);

        scanAndConnect();
    }



    public void initView() {
        topBar.setTitle("连接锁具");
    }

}



