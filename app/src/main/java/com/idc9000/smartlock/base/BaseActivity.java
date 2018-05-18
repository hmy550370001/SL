package com.idc9000.smartlock.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Window;

import com.idc9000.smartlock.controller.BaseController;
import com.idc9000.smartlock.listener.IModeChangeListener;
import com.pgyersdk.activity.FeedbackActivity;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by 赵先生 on 2017/10/9.
 * activity 基类
 */

public abstract class BaseActivity extends FragmentActivity implements IModeChangeListener {
    //进度弹出框
    private QMUITipDialog tipDialog;
    //Controller
    protected BaseController mController;
    //ButterKnife 解绑
    protected Unbinder mUnbinder;

    protected void initController(){

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        injectView();
    }
    /**
     * 界面初始化
     */
    protected void initView(){

    }
    /**
     * 数据初始化
     */
    protected void initData() {

    }

    /**
     * 初始化标题栏
     */
    protected void initTopBar(){

    }

    /**
     * 接收异步消息
     */
    protected Handler mHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // TODO Auto-generated method stub
            handlerMessage(msg);
            return false;
        }
    });


    protected void handlerMessage(Message msg){

    }

    /**
     * 收到消息后修改UI
     * @param action 返回处理不同的action
     * @param values 返回的数据
     */
    @Override
    public void onModeChanged(int action, Object... values) {
        mHandler.obtainMessage(action,values[0]).sendToTarget();
    }

    /**
     * 绑定ButterKnife 必须在setContView()后
     */
    public void injectView(){
        mUnbinder = ButterKnife.bind(this);
    }

    /**
     * 输入框判空
     */
    protected boolean ifValueWasEmpty(String... values) {
        for (String value : values) {
            if (TextUtils.isEmpty(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 显示等待进度条
     * @param message msg
     */
    protected void showProgressDialog(Context context,String message) {
        tipDialog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(message)
                .create();
        tipDialog.show();
    }

    protected void changeProgressDialog(String message){
        tipDialog.setTitle(message);
    }

    /**
     *
     */
    protected void showSuccessDialog(Context context,String message) {
        tipDialog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                .setTipWord(message)
                .create();
        tipDialog.show();
    }

    /**
     * 隐藏等待进度条
     */
    protected void dismissProgressDialog(){
        tipDialog.dismiss();
    }

    /**
     * activity注销时 ButterKnife 解绑
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        //解绑 ButterKnife
        if (this.mUnbinder != null) {
            this.mUnbinder.unbind();
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // 自定义摇一摇的灵敏度，默认为950，数值越小灵敏度越高。
        PgyFeedbackShakeManager.setShakingThreshold(800);
        // 以Activity的形式打开，这种情况下必须在AndroidManifest.xml配置FeedbackActivity
        // 打开沉浸式,默认为false
         FeedbackActivity.setBarImmersive(true);
        //相当于使用Dialog的方式；
        PgyFeedbackShakeManager.register(this, true);

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        PgyFeedbackShakeManager.unregister();
    }
}
