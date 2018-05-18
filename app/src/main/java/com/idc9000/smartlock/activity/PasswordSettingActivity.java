package com.idc9000.smartlock.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.idc9000.smartlock.HttpCallBack.CommonCallBack;
import com.idc9000.smartlock.HttpCallBack.DeviceListCallBack;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.adapter.PasswordListAdapter;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.bean.KeyInfo;
import com.idc9000.smartlock.bean.RCommonInfo;
import com.idc9000.smartlock.bean.RPassWordResult;
import com.idc9000.smartlock.cons.NetworkConst;
import com.idc9000.smartlock.event.BindingEvent;
import com.idc9000.smartlock.event.CurrentDeviceEvent;
import com.idc9000.smartlock.utils.ActivityUtil;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.socks.library.KLog;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

import static com.chad.library.adapter.base.BaseQuickAdapter.SCALEIN;

/**
 * Created by 赵先生 on 2017/10/30.
 * 密码指纹设置
 */

public class PasswordSettingActivity extends BaseActivity {
    private static final String TAG = "PasswordSettingActivity";
    Context context = this;
    @BindView(R.id.top_bar)
    QMUITopBar titleBar;
    @BindView(R.id.password_list)
    RecyclerView passwordList;
    PasswordListAdapter mAdapter;
    List<RPassWordResult.ResultBean.PasswordsBean> passwords;
    String lockId;
    int unique = 0;
    boolean clickLongType = false;
    @BindView(R.id.ll_btn_delete)
    AutoLinearLayout llBtnDelete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwordsetting);
        EventBus.getDefault().register(this);
        initView();
        getListData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onShowMessageEvent(CurrentDeviceEvent currentDeviceEvent) {
        lockId = currentDeviceEvent.getDeviceId();
        KLog.e(TAG, "lockId: " + lockId);
    }

    private void getListData() {
        OkHttpUtils
                .post()
                .url(NetworkConst.passwordlist)
                .addParams("lockId", String.valueOf(lockId))
                .build()
                .execute(new DeviceListCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort(e.getLocalizedMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        KLog.json(response);
                        RPassWordResult rPassWordResult = JSON.parseObject(response, RPassWordResult.class);
                        if (rPassWordResult.isSuccess()) {
                            passwords = rPassWordResult.getResult().getPasswords();
                            try {
                                initAdapter();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            ToastUtils.showShort(rPassWordResult.getErrorMsg());
                        }
                    }
                });
    }

    private void initAdapter() {
        mAdapter = new PasswordListAdapter(R.layout.item_passwordlist, passwords, false);
        mAdapter.openLoadAnimation(SCALEIN);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!clickLongType) {
                    EventBus.getDefault().postSticky(passwords.get(position));
                    ActivityUtil.start(context, AddLockPasswordActivity.class, false);
                }
            }
        });
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                clickLongType = true;
                deleteKey();
                return false;
            }
        });
        passwordList.setAdapter(mAdapter);


    }

    @Override
    protected void initView() {
        super.initView();
        initTopBar();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        // 设置布局管理器
        passwordList.setLayoutManager(mLayoutManager);
    }

    @Override
    protected void initTopBar() {
        super.initTopBar();
        titleBar.setTitle("锁具密码指纹设置");
        titleBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 删除钥匙
     */
    private void deleteKey() {
        llBtnDelete.setVisibility(View.VISIBLE);
        mAdapter.setDeleteState(true);
        mAdapter.notifyDataSetChanged();
        passwordList.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (passwords.get(position).isSelect) {
                    passwords.get(position).setSelect(false);
                    unique = unique - 1;
                } else {
                    if (unique == 0) {
                        passwords.get(position).setSelect(true);
                        unique = unique + 1;
                    } else {
                        ToastUtils.showShort("只可全选或单选！");
                    }
                }
                mAdapter.notifyItemChanged(position, passwords.get(position));
            }
        });
    }

    @OnClick(R.id.ll_btn_delete)
    public void onClick() {
        //删除单个钥匙
        for (int i = 0; i < passwords.size(); i++) {
            if (passwords.get(i).isSelect){
                RPassWordResult.ResultBean.PasswordsBean passwordsBean =passwords.get(i);
                EventBus.getDefault().postSticky(new BindingEvent("DeleteFinger",null,String.valueOf(passwordsBean.getOrderNumber())));
                EventBus.getDefault().postSticky(passwordsBean);

                ActivityUtils.startActivity(SearchDeviceActivity.class);
            }
        }
    }

}
