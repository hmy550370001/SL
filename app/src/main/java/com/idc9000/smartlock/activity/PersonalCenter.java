package com.idc9000.smartlock.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.clj.fastble.BleManager;
import com.idc9000.smartlock.HttpCallBack.CommonCallBack;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.bean.RUserInfo;
import com.idc9000.smartlock.cons.Constants;
import com.idc9000.smartlock.cons.NetworkConst;
import com.idc9000.smartlock.event.FamilyAccountEvent;
import com.idc9000.smartlock.utils.ActivityUtil;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.socks.library.KLog;
import com.zhy.autolayout.AutoRelativeLayout;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by 赵先生 on 2017/10/30.
 * 个人中心
 */

public class PersonalCenter extends BaseActivity {
    private static final String TAG = "PersonalCenter";
    Context context = this;

    @BindView(R.id.rl_family)
    AutoRelativeLayout rlFamily;
    @BindView(R.id.rl_help)
    AutoRelativeLayout rlHelp;
    @BindView(R.id.rl_check)
    AutoRelativeLayout rlCheck;
    @BindView(R.id.rl_exit)
    AutoRelativeLayout rlExit;
    @BindView(R.id.top_bar_left)
    ImageView topBarLeft;
    @BindView(R.id.top_bar_text)
    TextView topBarText;
    @BindView(R.id.rl_change_pw)
    AutoRelativeLayout rlChangePw;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_family)
    TextView tvFamily;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        initTopBar();
    }

    @Override
    protected void initData() {
        super.initData();
        OkHttpUtils
                .post()
                .url(NetworkConst.index)
                .build()
                .execute(new CommonCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort("网络异常："+e.getLocalizedMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        KLog.e(TAG, "个人中心数据： "+response);
                        RUserInfo rUserInfo = JSON.parseObject(response, RUserInfo.class);
                        if (rUserInfo.isSuccess()){
                            tvPhone.setText(rUserInfo.getResult().getUser().getPhone());
                            tvFamily.setText(rUserInfo.getResult().getUser().getFamilyPhone());
                            Constants.CurrentUserPhone=rUserInfo.getResult().getUser().getPhone();
                        }else {
                            ToastUtils.showShort(rUserInfo.getErrorMsg());
                        }
                    }
                });
    }

    @Override
    protected void initTopBar() {
        super.initTopBar();
        topBarText.setText("个人中心");
        topBarLeft.setImageDrawable(getResources().getDrawable(R.mipmap.back));
    }


    @OnClick({R.id.rl_family, R.id.rl_help, R.id.rl_check, R.id.rl_exit, R.id.top_bar_left, R.id.rl_change_pw})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_family:
                ActivityUtil.start(this, FamilyActivity.class, false);
                break;
            case R.id.rl_help:
                ActivityUtil.start(this, HelpActivity.class, false);
                break;
            case R.id.rl_check:
                showCheckUpdateDialog();
                break;
            case R.id.rl_change_pw:
                ActivityUtil.start(this, ModifyLoginPasswordActivity.class, false);
                break;
            case R.id.rl_exit:
                showMessageExitDialog();
                break;
            case R.id.top_bar_left:
                finish();
                break;
        }
    }

    /**
     * 检查更新
     */
    private void showCheckUpdateDialog() {
        // 版本检测方式2：带更新回调监听
        PgyUpdateManager.register(this,
                "com.idc9000.smartlock.provider_file",
                new UpdateManagerListener() {
                    @Override
                    public void onUpdateAvailable(final String result) {
                        new AlertDialog.Builder(PersonalCenter.this)
                                .setTitle("更新")
                                .setMessage("")
                                .setNegativeButton(
                                        "确定",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                String url;
                                                JSONObject jsonData;
                                                try {
                                                    jsonData = new JSONObject(
                                                            result);
                                                    if ("0".equals(jsonData
                                                            .getString("code"))) {
                                                        JSONObject jsonObject = jsonData
                                                                .getJSONObject("data");
                                                        url = jsonObject
                                                                .getString("downloadURL");

                                                        startDownloadTask(
                                                                PersonalCenter.this,
                                                                url);


                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).show();

                    }

                    @Override
                    public void onNoUpdateAvailable() {
                        new QMUIDialog.MessageDialogBuilder(context)
                                .setTitle("暂无更新")
                                .setMessage("当前版本是最新版！")
                                .addAction("确定", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                });
    }

    /**
     * 确定退出对话框
     */
    private void showMessageExitDialog() {
        new QMUIDialog.MessageDialogBuilder(context)
                .setTitle("退出")
                .setMessage("确定要退出吗？")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        BleManager.getInstance().disconnectAllDevice();
                        BleManager.getInstance().destroy();
                        SPUtils.getInstance().clear();
                        PgyUpdateManager.unregister();
                        ActivityUtils.finishAllActivities();
                    }
                })
                .show();
    }
}
