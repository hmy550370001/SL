package com.idc9000.smartlock.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.gson.Gson;
import com.idc9000.smartlock.HttpCallBack.CommonCallBack;
import com.idc9000.smartlock.HttpCallBack.KeyListCallBack;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.adapter.MyKeyListAdapter;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.bean.KeyInfo;
import com.idc9000.smartlock.bean.RCommonInfo;
import com.idc9000.smartlock.bean.RKeyListResult;
import com.idc9000.smartlock.cons.Constants;
import com.idc9000.smartlock.cons.NetworkConst;
import com.idc9000.smartlock.event.BindingEvent;
import com.idc9000.smartlock.event.CurrentDeviceEvent;
import com.idc9000.smartlock.utils.ActivityUtil;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.socks.library.KLog;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

import static com.chad.library.adapter.base.BaseQuickAdapter.SLIDEIN_LEFT;

/**
 * Created by 赵先生 on 2017/10/27.
 * 门锁钥匙列表
 */

public class KeyListActivity extends BaseActivity {
    private static final String TAG = "KeyListActivity";

    Context context = this;
    @BindView(R.id.top_bar)
    QMUITopBar top_bar;
    @BindView(R.id.list_key)
    RecyclerView list_Key;
    @BindView(R.id.btn_add)
    Button btnAdd;
    @BindView(R.id.btn_delete)
    Button btnDelete;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    String lockId;
    @BindView(R.id.ll_select_all)
    LinearLayout llSelectAll;
    @BindView(R.id.ll_btn_delete)
    LinearLayout llBtnDelete;
    @BindView(R.id.ll_delete)
    LinearLayout llDelete;
    private int mNextRequestPage = 1;
    private static final int PAGE_SIZE = 20;

    private View notDataView;//没有数据
    private View errorView; //加载失败
    int unique=0;
    private MyKeyListAdapter mAdapter;
    boolean deleteAll =false;

    private RecyclerView.LayoutManager mLayoutManager; //    RecyclerView提供的布局管理器

    ArrayList<RKeyListResult.ResultBean.KeysBean> keysBeans;
    ArrayList<KeyInfo> data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keylist);
        EventBus.getDefault().register(this);
        initView();
        initAdapter();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    /**
     * 发起网络请求 读取已配对设备列表
     */
    @Override
    protected void initData() {
        super.initData();
        refresh();
    }

    /**
     * 初始化适配器
     * 上拉加载 下拉刷新
     */
    private void initAdapter() {
        mAdapter = new MyKeyListAdapter(R.layout.item_keylist, null,false);
        mAdapter.openLoadAnimation(SLIDEIN_LEFT);

        //下拉刷新  上拉加载更多
        initRefreshLayout();
        // 设置adapter
        list_Key.setAdapter(mAdapter);

        notDataView = getLayoutInflater().inflate(R.layout.empty_view, (ViewGroup) list_Key.getParent(), false);
        notDataView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        errorView = getLayoutInflater().inflate(R.layout.error_view, (ViewGroup) list_Key.getParent(), false);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
    }

    private void initRefreshLayout() {
        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        swipeRefreshLayout.setRefreshing(true);
        //上拉加载更多
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        });
    }

    /**
     * 加载更多
     */
    private void loadMore() {
        OkHttpUtils
                .post()
                .url(NetworkConst.LOCKKEYLIST)
                .addParams("lockId", lockId)
                .addParams("currectPage", String.valueOf(mNextRequestPage))
                .addParams("pageSize", String.valueOf(PAGE_SIZE))
                .build()
                .execute(new KeyListCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort(e.getLocalizedMessage());
                        mAdapter.loadMoreFail();
                        mAdapter.setEmptyView(errorView);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        KLog.e(TAG, "KeyList: "+response );
                        Gson gson = new Gson();
                        RKeyListResult rKeyListResult = gson.fromJson(response, RKeyListResult.class);

                        if (rKeyListResult.isSuccess()) {
                            keysBeans = (ArrayList<RKeyListResult.ResultBean.KeysBean>) rKeyListResult.getResult().getKeys();
                            setData(false, getData(keysBeans));
                            if (keysBeans.size() == 0) {
                                mAdapter.setEmptyView(notDataView);
                            }
                        } else {
                            ToastUtils.showShort(rKeyListResult.getErrorMsg());
                            mAdapter.loadMoreFail();
                        }
                    }
                });
    }

    /**
     * 刷新
     */
    private void refresh() {
        mNextRequestPage = 1;
        mAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载

        OkHttpUtils
                .post()
                .url(NetworkConst.LOCKKEYLIST)
                .addParams("lockId", lockId)
                .addParams("currectPage", String.valueOf(mNextRequestPage))
                .addParams("pageSize", String.valueOf(PAGE_SIZE))
                .build()
                .execute(new KeyListCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort(e.getLocalizedMessage());
                        mAdapter.setEnableLoadMore(true);
                        swipeRefreshLayout.setRefreshing(false);
                        mAdapter.setEmptyView(errorView);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        KLog.e(TAG, "onResponse: " + response);
                        Gson gson = new Gson();
                        try {
                            RKeyListResult rKeyListResult = gson.fromJson(response, RKeyListResult.class);
                            if (rKeyListResult.isSuccess()) {
                                keysBeans = (ArrayList<RKeyListResult.ResultBean.KeysBean>) rKeyListResult.getResult().getKeys();
                                setData(true, getData(keysBeans));
                                mAdapter.setEnableLoadMore(true);
                                swipeRefreshLayout.setRefreshing(false);
                                if (keysBeans.size() == 0) {
                                    mAdapter.setEmptyView(notDataView);
                                }
                            } else {
                                ToastUtils.showShort(rKeyListResult.getErrorMsg());
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected void initView() {
        super.initView();
        initTopBar();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        // 设置布局管理器
        list_Key.setLayoutManager(mLayoutManager);
        if (Constants.UserState.equals("User")){
            btnAdd.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        }
    }

    private ArrayList<KeyInfo> getData(List<RKeyListResult.ResultBean.KeysBean> keysBeans) {
        data = new ArrayList<>();
        for (int i = 0; i < keysBeans.size(); i++) {
            KeyInfo keyInfo = new KeyInfo();
            keyInfo.setId(String.valueOf(keysBeans.get(i).getId()));
            keyInfo.setTime(keysBeans.get(i).getReceiveTime());
            keyInfo.setName(keysBeans.get(i).getUsername());
            keyInfo.setState(keysBeans.get(i).getReceiveFlag());
            keyInfo.setOrderNumber(keysBeans.get(i).getOrderNumber());
            data.add(i, keyInfo);
        }

        return data;
    }

    @Override
    protected void initTopBar() {
        super.initTopBar();
        top_bar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        top_bar.addRightImageButton(R.mipmap.set, R.id.set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtil.start(context, LockSetting.class, false);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onShowMessageEvent(CurrentDeviceEvent messageEvent) {
        top_bar.setTitle(messageEvent.getName() + "  门锁钥匙");
        lockId = messageEvent.getDeviceId();
    }

    @OnClick({R.id.btn_add, R.id.btn_delete,R.id.ll_btn_delete,R.id.ll_select_all})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                ActivityUtil.start(context, SelectKeyTypeActivity.class, false);
                break;
            case R.id.btn_delete:
                deleteKey();
                break;
            case R.id.ll_btn_delete:
                confirmDeleteKey();
                break;
            case R.id.ll_select_all:
                selectAll();
                break;
        }
    }

    /**
     * 删除钥匙
     */
    private void deleteKey() {
        if(data.size()!=0){
            btnAdd.setVisibility(View.INVISIBLE);
            btnDelete.setVisibility(View.INVISIBLE);
            llDelete.setVisibility(View.VISIBLE);
            mAdapter.setDeleteState(true);
            swipeRefreshLayout.setEnabled(false);
            refresh();
            list_Key.addOnItemTouchListener(new OnItemClickListener() {
                @Override
                public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                    if (data.get(position).isSelect){
                        data.get(position).setSelect(false);
                        unique=unique-1;
                    }else {
                        if (unique==0){
                            data.get(position).setSelect(true);
                            unique=unique+1;
                        }else {
                            ToastUtils.showShort("单选！");
                        }
                    }
                    mAdapter.notifyItemChanged(position,data.get(position));
                }
            });
        }else {
            ToastUtils.showShort("暂无数据，无法删除！");
        }
    }

    /**
     * 确认删除
     */
    private void confirmDeleteKey() {
        if (deleteAll){
            //删除全部钥匙
            EventBus.getDefault().postSticky(new BindingEvent("DeleteAllKey",null));
            ActivityUtils.startActivity(SearchDeviceActivity.class);
        }else {
            //删除单个钥匙
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).isSelect){
                    KeyInfo keyInfo =data.get(i);
                    EventBus.getDefault().postSticky(new BindingEvent("DeleteKey",null,keyInfo.getId()));
                    EventBus.getDefault().postSticky(keyInfo);
                    ActivityUtils.startActivity(SearchDeviceActivity.class);
                }
            }
        }
    }

    /**
     * 全选
     */
    private void selectAll() {
        if (data.get(0).isSelect) {
            for (int i = 0; i < data.size(); i++) {
                data.get(i).setSelect(false);
                mAdapter.notifyItemChanged(i, data.get(i));
            }
        } else {
            for (int i = 0; i < data.size(); i++) {
                data.get(i).setSelect(true);
                mAdapter.notifyItemChanged(i, data.get(i));
            }
        }
        deleteAll=true;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 填充数据
     *
     * @param isRefresh 刷新
     * @param data      数据
     */
    private void setData(boolean isRefresh, List data) {
        mNextRequestPage++;
        final int size = data == null ? 0 : data.size();
        if (isRefresh) {
            mAdapter.setNewData(data);
        } else {
            if (size > 0) {
                mAdapter.addData(data);
            }
        }
        if (size < PAGE_SIZE) {
            //第一页如果不够一页就不显示没有更多数据布局
            mAdapter.loadMoreEnd(isRefresh);
        } else {
            mAdapter.loadMoreComplete();
        }
    }
}
