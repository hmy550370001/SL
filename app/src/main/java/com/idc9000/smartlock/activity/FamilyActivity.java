package com.idc9000.smartlock.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ToastUtils;
import com.idc9000.smartlock.HttpCallBack.CommonCallBack;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.bean.RCommonInfo;
import com.idc9000.smartlock.cons.NetworkConst;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.socks.library.KLog;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by 赵先生 on 2017/11/8.
 * 亲情号
 */

public class FamilyActivity extends BaseActivity {
    private static final String TAG = "FamilyActivity";
    Context context = this;


    @BindView(R.id.top_bar)
    QMUITopBar topBar;
    @BindView(R.id.et_family)
    EditText etFamily;
    @BindView(R.id.btn_submit)
    QMUIRoundButton btnSubmit;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        initTopBar();
    }

    @Override
    protected void initTopBar() {
        super.initTopBar();
        topBar.setTitle("亲情号");
        topBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @OnClick(R.id.btn_submit)
    public void onClick() {
        changeFamily();
    }

    /**
     * 请求更改亲情号
     */
    private void changeFamily() {
        OkHttpUtils
                .post()
                .url(NetworkConst.updateFamilyPhone)
                .addParams("phone", etFamily.getText().toString())
                .build()
                .execute(new CommonCallBack() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                       ToastUtils.showShort("网络异常："+e.getLocalizedMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        KLog.e(TAG, "上传亲情号结果: "+response);
                        RCommonInfo commonInfo = JSON.parseObject(response,RCommonInfo.class);
                        if (commonInfo.isSuccess()){
                            changeSuccess();
                        }else {
                            ToastUtils.showShort(commonInfo.getErrorMsg());
                        }
                    }
                });
    }

    /**
     * 修改成功
     */
    private void changeSuccess() {
        //隐藏软键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        finish();
    }
}
