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
import com.idc9000.smartlock.adapter.MyMessageAdapter;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.bean.RMessage;
import com.idc9000.smartlock.cons.NetworkConst;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.socks.library.KLog;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

import static com.chad.library.adapter.base.BaseQuickAdapter.ALPHAIN;
import static com.chad.library.adapter.base.BaseQuickAdapter.SCALEIN;
import static com.chad.library.adapter.base.BaseQuickAdapter.SLIDEIN_BOTTOM;
import static com.chad.library.adapter.base.BaseQuickAdapter.SLIDEIN_LEFT;

/**
 * Created by 赵先生 on 2017/10/30.
 */

public class MessageCenterActivity extends BaseActivity {
    private static final String TAG = "MessageCenterActivity";
    Context context=this;
    @BindView(R.id.topbar)
    QMUITopBar topbar;
    @BindView(R.id.list_message)
    RecyclerView listMessage;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    MyMessageAdapter myMessageAdapter;
    private int mNextRequestPage = 1;
    private static final int PAGE_SIZE = 10;

    ArrayList<RMessage.ResultBean.InfosBean> infosBeans;
    
    private View notDataView;//没有数据
    private View errorView; //加载失败
     
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagecenter);
        initView();
        initAdapter();
        initData();
    }

    @Override
    protected void initData() {
        super.initData();
        //刷新
        refresh();
    }

    @Override
    protected void initView() {
        super.initView();
        initTopBar();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        // 设置布局管理器
        listMessage.setLayoutManager(mLayoutManager);
    }

    @Override
    protected void initTopBar() {
        super.initTopBar();
        topbar.setTitle("消息中心");
        topbar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 初始化适配器
     * 添加头部轮播图 底部留白
     * 上拉加载 下拉刷新
     */
    private void initAdapter() {
        myMessageAdapter = new MyMessageAdapter(R.layout.item_message, null);
        myMessageAdapter.openLoadAnimation(SCALEIN);
       
        //boot 留白
        View boot = getLayoutInflater().inflate(R.layout.devicelist_booter, (ViewGroup) listMessage.getParent(), false);
        myMessageAdapter.addFooterView(boot);
        
        //下拉刷新  上拉加载更多
        initRefreshLayout();
        // 设置adapter
        listMessage.setAdapter(myMessageAdapter);

        notDataView = getLayoutInflater().inflate(R.layout.empty_view, (ViewGroup) listMessage.getParent(), false);
        notDataView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        errorView = getLayoutInflater().inflate(R.layout.error_view, (ViewGroup) listMessage.getParent(), false);
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
        myMessageAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        });
    }


    /**
     * 刷新
     */
    private void refresh() {
        mNextRequestPage = 1;
        myMessageAdapter.setEnableLoadMore(false);//这里的作`   用是防止下拉刷新的时候还可以上拉加载

        OkHttpUtils
                .post()
                .url(NetworkConst.info)
                .addParams("currectPage", String.valueOf(mNextRequestPage))
                .addParams("pageSize", String.valueOf(PAGE_SIZE))
                .build()
                .execute(new CommonCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort(e.getLocalizedMessage());
                        myMessageAdapter.setEnableLoadMore(true);
                        swipeRefreshLayout.setRefreshing(false);
                        myMessageAdapter.setEmptyView(errorView);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        KLog.e(TAG, "onResponse: "+response);
                        Gson gson = new Gson();
                        try {
                            RMessage rMessage = gson.fromJson(response, RMessage.class);
                            if (rMessage.isSuccess()) {
                                infosBeans = (ArrayList<RMessage.ResultBean.InfosBean>) rMessage.getResult().getInfos();
                                setData(true, infosBeans);
                                myMessageAdapter.setEnableLoadMore(true);
                                swipeRefreshLayout.setRefreshing(false);
                                if (infosBeans.size()==0){
                                    myMessageAdapter.setEmptyView(notDataView);
                                }
                            } else {
                                ToastUtils.showShort(rMessage.getErrorMsg());
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
            myMessageAdapter.setNewData(data);
        } else {
            if (size > 0) {
                myMessageAdapter.addData(data);
            }
        }
        if (size < PAGE_SIZE) {
            //第一页如果不够一页就不显示没有更多数据布局
            myMessageAdapter.loadMoreEnd(isRefresh);
        } else {
            myMessageAdapter.loadMoreComplete();
        }
    }


    /**
     * 加载更多
     */
    private void loadMore() {

        OkHttpUtils
                .post()
                .url(NetworkConst.info)
                .addParams("currectPage", String.valueOf(mNextRequestPage))
                .addParams("pageSize", String.valueOf(PAGE_SIZE))
                .build()
                .execute(new DeviceListCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort(e.getLocalizedMessage());
                        myMessageAdapter.loadMoreFail();
                        myMessageAdapter.setEmptyView(errorView);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        RMessage rMessage = gson.fromJson(response, RMessage.class);
                        if (rMessage.isSuccess()){
                            infosBeans = (ArrayList<RMessage.ResultBean.InfosBean>) rMessage.getResult().getInfos();
                            setData(false, infosBeans);
                            if (infosBeans.size()==0){
                                myMessageAdapter.setEmptyView(notDataView);
                            }
                        }else {
                            ToastUtils.showShort(rMessage.getErrorMsg());
                            myMessageAdapter.loadMoreFail();
                        }
                    }
                });
    }


}
