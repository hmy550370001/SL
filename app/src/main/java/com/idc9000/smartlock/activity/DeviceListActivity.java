package com.idc9000.smartlock.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.gson.Gson;
import com.idc9000.smartlock.HttpCallBack.DeviceListCallBack;
import com.idc9000.smartlock.MainActivity;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.adapter.DeviceListAdapter;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.bean.RDeviceResult;
import com.idc9000.smartlock.cons.Constants;
import com.idc9000.smartlock.cons.NetworkConst;
import com.idc9000.smartlock.event.BindingEvent;
import com.idc9000.smartlock.event.CurrentDeviceEvent;
import com.idc9000.smartlock.event.RefreshData;
import com.idc9000.smartlock.utils.ActivityUtil;
import com.idc9000.smartlock.utils.GlideImageLoader;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.socks.library.KLog;
import com.youth.banner.Banner;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

import static com.chad.library.adapter.base.BaseQuickAdapter.SCALEIN;

/**
 * Created by 赵先生 on 2017/10/17.
 * 设备列表
 */

public class DeviceListActivity extends BaseActivity {
    private static final String TAG = "DeviceListActivity";
    private static final int PAGE_SIZE = 10;
    private static boolean exit = false;//退出
    Context context = this;
    @BindView(R.id.topbar)
    QMUITopBar topbar; //标题
    @BindView(R.id.list_device)
    RecyclerView list_device;  //设备列表
    @BindView(R.id.list_device2)
            RecyclerView list_device2;
    ArrayList<RDeviceResult.ResultBean.LocksBean> locks;  //锁数据
    ArrayList<CurrentDeviceEvent> data;  //设备数据
    @BindView(R.id.btn_add)
    QMUIRoundButton btnAdd;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout; //刷新控件
    DeviceListAdapter mAdapter;
    private int mNextRequestPage = 1;
    private View notDataView;//没有数据
    private View errorView; //加载失败
    private ExitHandler exitHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binddevicelist);
        initView();
        initAdapter();
        initData();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        initTopBar();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        // 设置布局管理器
        list_device.setLayoutManager(mLayoutManager);
        //租户登录隐藏添加设备按钮
        if (SPUtils.getInstance().getString("userType").equals("User")){
            btnAdd.setVisibility(View.GONE);
        }

    }

    /**
     * 收到推送后更新
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void refreshData(RefreshData refreshData) {
      refresh();
    }

    /**
     * 添加设备
     */
    @OnClick(R.id.btn_add)
    public void onClick() {
        EventBus.getDefault().postSticky(new BindingEvent("AdminBinding",null));
        ActivityUtils.startActivity(SearchDeviceActivity.class);
    }

    /**
     * 初始化适配器
     * 添加头部轮播图 底部留白
     * 上拉加载 下拉刷新
     */
    private void initAdapter() {
        mAdapter = new DeviceListAdapter(R.layout.device_list_item, null);
        mAdapter.openLoadAnimation(SCALEIN);
        //header - banner
        View top = getLayoutInflater().inflate(R.layout.devicelist_header, (ViewGroup) list_device.getParent(), false);
        List<Integer> images = new ArrayList<>();
        images.add(R.mipmap.devicelist_bg);
        Banner banner = (Banner) top.findViewById(R.id.banner);
        banner.setImages(images).setImageLoader(new GlideImageLoader()).start();
        mAdapter.addHeaderView(top);
        //boot 空出一部分控件显示按钮
        View boot = getLayoutInflater().inflate(R.layout.devicelist_booter, (ViewGroup) list_device.getParent(), false);
        mAdapter.addFooterView(boot);


        //添加设备点击事件
        list_device.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                try {
                    //当前选中设备
                    CurrentDeviceEvent currentDeviceEvent = (CurrentDeviceEvent) adapter.getData().get(position);
                    EventBus.getDefault().postSticky(currentDeviceEvent);
                    //判断当前用户状态
                    if (Constants.UserState.equals("Admin")){
                        ActivityUtil.start(context, MainActivity.class, false);
                    }else {
                        if (currentDeviceEvent.getReceiveFlag()==0){
                            //租户绑定设备
                            EventBus.getDefault().postSticky(new BindingEvent("UserBinding",null));
                            ActivityUtils.startActivity(SearchDeviceActivity.class);
                        }else if (currentDeviceEvent.getReceiveFlag()==1){
                            ActivityUtil.start(context, MainActivity.class, false);
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        //下拉刷新  上拉加载更多
        initRefreshLayout();
        // 设置adapter
        list_device.setAdapter(mAdapter);

        notDataView = getLayoutInflater().inflate(R.layout.empty_view, (ViewGroup) list_device.getParent(), false);
        notDataView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        errorView = getLayoutInflater().inflate(R.layout.error_view, (ViewGroup) list_device.getParent(), false);
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
                .url(getURL())
                .addParams("currectPage", String.valueOf(mNextRequestPage))
                .addParams("pageSize", String.valueOf(PAGE_SIZE))
                .build()
                .execute(new DeviceListCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort(e.getLocalizedMessage());
                        mAdapter.loadMoreFail();
                        mAdapter.setEmptyView(errorView);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        RDeviceResult rDeviceResult = gson.fromJson(response, RDeviceResult.class);
                        if (rDeviceResult.isSuccess()){
                            locks= (ArrayList<RDeviceResult.ResultBean.LocksBean>) rDeviceResult.getResult().getLocks();
                            setData(false, getData(locks));
                            if (locks.size()==0){
                                mAdapter.setEmptyView(notDataView);
                            }
                        }else {
                            ToastUtils.showShort(rDeviceResult.getErrorMsg());
                            mAdapter.loadMoreFail();
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

    /**
     * 刷新
     */
    private void refresh() {
        mNextRequestPage = 1;
        mAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
        getURL();

        OkHttpUtils
                .post()
                .url(getURL())
                .addParams("currectPage", String.valueOf(mNextRequestPage))
                .addParams("pageSize", String.valueOf(PAGE_SIZE))
                .build()
                .execute(new DeviceListCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort(e.getLocalizedMessage());
                        mAdapter.setEnableLoadMore(true);
                        swipeRefreshLayout.setRefreshing(false);
                        mAdapter.setEmptyView(errorView);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        KLog.json(response);
                        Gson gson = new Gson();
                        try {
                            RDeviceResult rDeviceResult = gson.fromJson(response, RDeviceResult.class);
                            if (rDeviceResult.isSuccess()) {
                                locks = (ArrayList<RDeviceResult.ResultBean.LocksBean>) rDeviceResult.getResult().getLocks();
                                setData(true, getData(locks));
                                mAdapter.setEnableLoadMore(true);
                                swipeRefreshLayout.setRefreshing(false);
                                if (locks.size()==0){
                                    mAdapter.setEmptyView(notDataView);
                                }
                            } else {
                                ToastUtils.showShort(rDeviceResult.getErrorMsg());
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 根据不同角色状态获取URL
     */
    private String getURL() {
        if (Constants.UserState.equals("Admin")){
            return NetworkConst.ADMINDEVICELIST_URL;
        }else {
            return NetworkConst.USERDEVICELIST_URL;
        }
    }

    @Override
    protected void initTopBar() {
        super.initTopBar();
        topbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        topbar.setTitle("常通物联");
        topbar.addLeftImageButton(R.mipmap.my, R.id.my).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtil.start(context, PersonalCenter.class, false);
            }
        });
        topbar.addRightImageButton(R.mipmap.inform, R.id.inform).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtil.start(context, MessageCenterActivity.class, false);
            }
        });
    }

    /**
     * 发起网络请求 读取已配对设备列表
     */
    @Override
    protected void initData() {
        super.initData();
        //当前用户id
        Constants.CurrentUserID=SPUtils.getInstance().getString("CurrentUserID");
        //类型
        Constants.UserState=SPUtils.getInstance().getString("userType");
        KLog.e(TAG, "initData: "+Constants.CurrentUserID+" "+Constants.UserState);
        //刷新
        try {
            refresh();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 列表数据
     */
    private ArrayList<CurrentDeviceEvent> getData(List<RDeviceResult.ResultBean.LocksBean> locks) {
        data = new ArrayList<>();
        for (int i = 0; i <locks.size(); i++) {
            CurrentDeviceEvent currentDeviceEvent = new CurrentDeviceEvent();
            currentDeviceEvent.setKeyAdmin(locks.get(i).getKeyAdmin());
            currentDeviceEvent.setKeyUser(locks.get(i).getKeyUser());
            currentDeviceEvent.setKeyDev(locks.get(i).getKeyDev());
            currentDeviceEvent.setLockNo(locks.get(i).getKeyNumber());
            currentDeviceEvent.setDeviceId(String.valueOf(locks.get(i).getId()));
            currentDeviceEvent.setName(locks.get(i).getName());
            currentDeviceEvent.setRemains((String) locks.get(i).getRemains());
            currentDeviceEvent.setBattery(String.valueOf(locks.get(i).getBattery()));
            currentDeviceEvent.setSafeMode(locks.get(i).getSafeMode());
            currentDeviceEvent.setDeviceName(String.valueOf(locks.get(i).getDeviceName()));
            currentDeviceEvent.setMac(String.valueOf(locks.get(i).getMac()));
            currentDeviceEvent.setReceiveFlag(locks.get(i).getReceiveFlag());
            currentDeviceEvent.setStartTime(String.valueOf(locks.get(i).getStartTime()));
            currentDeviceEvent.setEndTime((String) locks.get(i).getEndTime());
            data.add(i, currentDeviceEvent);
        }
        return data;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getKeyCode()==KeyEvent.KEYCODE_BACK){
            if (!exit){
                exit = true;
                exitHandler=new ExitHandler();
                ToastUtils.showShort("再按一次退出！");
                //发延迟消息2s将exit变为false
                exitHandler.sendEmptyMessageDelayed(1,2000);
                return true;
            }
        }
      return super.onKeyUp(keyCode,event);
    }


    private static class ExitHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                exit=false;
            }
        }
    }
}
