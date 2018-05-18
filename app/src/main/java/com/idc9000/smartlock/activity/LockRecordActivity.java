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

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.idc9000.smartlock.HttpCallBack.CommonCallBack;
import com.idc9000.smartlock.HttpCallBack.DeviceListCallBack;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.adapter.LockRecordAdapter;
import com.idc9000.smartlock.adapter.MyLockRecordAdapter;
import com.idc9000.smartlock.adapter.MyMessageAdapter;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.bean.RLockRecord;
import com.idc9000.smartlock.bean.RMessage;
import com.idc9000.smartlock.bean.RResult;
import com.idc9000.smartlock.bean.RecordInfo;
import com.idc9000.smartlock.bean.RecordItemInfo;
import com.idc9000.smartlock.cons.NetworkConst;
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
import butterknife.ButterKnife;
import okhttp3.Call;

import static com.chad.library.adapter.base.BaseQuickAdapter.SCALEIN;

/**
 * Created by 赵先生 on 2017/10/25.
 * 门锁日志
 */

public class LockRecordActivity extends BaseActivity {
    private static final String TAG = "LockRecordActivity";
    Context context=this;
    @BindView(R.id.title_bar)
    QMUITopBar titleBar;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.list_lock_record)
    RecyclerView listLockRecord;
    MyLockRecordAdapter myLockRecordAdapter;

    String lockId;
    private View notDataView;//没有数据
    private View errorView; //加载失败
    private int mNextRequestPage = 1;
    private static final int PAGE_SIZE = 10;

    ArrayList<RLockRecord.ResultBean.LocklogsBean> locklogsBeans;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockrecord);
        EventBus.getDefault().register(this);
        initView();
        initAdapter();
        initData();
    }

    /**
     * 初始化适配器
     * 添加头部轮播图 底部留白
     * 上拉加载 下拉刷新
     */
    private void initAdapter() {
        myLockRecordAdapter= new MyLockRecordAdapter(R.layout.item_record_left, null);
        myLockRecordAdapter.openLoadAnimation(SCALEIN);

        //boot 留白
        View boot = getLayoutInflater().inflate(R.layout.devicelist_booter, (ViewGroup) listLockRecord.getParent(), false);
        myLockRecordAdapter.addFooterView(boot);

        //下拉刷新  上拉加载更多
        initRefreshLayout();
        // 设置adapter
        listLockRecord.setAdapter(myLockRecordAdapter);

        notDataView = getLayoutInflater().inflate(R.layout.empty_view, (ViewGroup) listLockRecord.getParent(), false);
        notDataView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        errorView = getLayoutInflater().inflate(R.layout.error_view, (ViewGroup) listLockRecord.getParent(), false);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
    }

    /**
     * 下拉刷新
     * 上拉加载更多
     */
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
        myLockRecordAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
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
                .url(NetworkConst.LockRecord)
                .addParams("lockId", lockId)
                .addParams("currectPage", "1")
                .addParams("pageSize", "10")
                .build()
                .execute(new DeviceListCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort(e.getLocalizedMessage());
                        myLockRecordAdapter.loadMoreFail();
                        myLockRecordAdapter.setEmptyView(errorView);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        RLockRecord rLockRecord = gson.fromJson(response, RLockRecord.class);
                        if (rLockRecord.isSuccess()){
                            locklogsBeans = (ArrayList<RLockRecord.ResultBean.LocklogsBean>) rLockRecord.getResult().getLocklogs();
                            KLog.e(TAG, "onResponse: "+locklogsBeans.toString() );
                            setData(false, locklogsBeans);
                            if (locklogsBeans.size()==0){
                                myLockRecordAdapter.setEmptyView(notDataView);
                            }
                        }else {
                            ToastUtils.showShort(rLockRecord.getErrorMsg());
                            myLockRecordAdapter.loadMoreFail();
                        }
                    }
                });
    }
    /**
     * 刷新
     */
    private void refresh() {
        mNextRequestPage = 1;
        myLockRecordAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载

        OkHttpUtils
                .post()
                .url(NetworkConst.LockRecord)
                .addParams("lockId", lockId)
                .addParams("currectPage", "1")
                .addParams("pageSize", "10")
                .build()
                .execute(new CommonCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort(e.getLocalizedMessage());
                        myLockRecordAdapter.setEnableLoadMore(true);
                        swipeRefreshLayout.setRefreshing(false);
                        myLockRecordAdapter.setEmptyView(errorView);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        KLog.e(TAG, "onResponse: "+response);
                        Gson gson = new Gson();
                        try {
                            RLockRecord rLockRecord = gson.fromJson(response, RLockRecord.class);
                            if (rLockRecord.isSuccess()) {
                                locklogsBeans = (ArrayList<RLockRecord.ResultBean.LocklogsBean>) rLockRecord.getResult().getLocklogs();
                                setData(true, locklogsBeans);
                                myLockRecordAdapter.setEnableLoadMore(true);
                                swipeRefreshLayout.setRefreshing(false);
                                if (locklogsBeans.size()==0){
                                    myLockRecordAdapter.setEmptyView(notDataView);
                                }
                            } else {
                                ToastUtils.showShort(rLockRecord.getErrorMsg());
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 填充数据
     * @param isRefresh 刷新
     * @param data 数据
     */
    private void setData(boolean isRefresh, List data) {
        mNextRequestPage++;
        final int size = data == null ? 0 : data.size();
        if (isRefresh) {
            myLockRecordAdapter.setNewData(data);
        } else {
            if (size > 0) {
                myLockRecordAdapter.addData(data);
            }
        }
        if (size < PAGE_SIZE) {
            //第一页如果不够一页就不显示没有更多数据布局
            myLockRecordAdapter.loadMoreEnd(isRefresh);
        } else {
            myLockRecordAdapter.loadMoreComplete();
        }
    }

    @Override
    protected void initData() {
        super.initData();
        refresh();
    }

    @Override
    protected void initView() {
        super.initView();
        initTopBar();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        // 设置布局管理器
        listLockRecord.setLayoutManager(mLayoutManager);
    }

    @Override
    protected void initTopBar() {
        super.initTopBar();
        titleBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        titleBar.addRightImageButton(R.mipmap.set, R.id.set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtil.start(context, LockSetting.class,false);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onShowMessageEvent(CurrentDeviceEvent currentDeviceEvent) {
        titleBar.setTitle(currentDeviceEvent.getName()+"  门锁日志");
        lockId=currentDeviceEvent.getDeviceId();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
