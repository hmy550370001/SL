package com.idc9000.smartlock.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.idc9000.smartlock.R;
import com.idc9000.smartlock.adapter.BusinessListAdapter;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.bean.BusinessInfo;
import com.qmuiteam.qmui.widget.QMUITopBar;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by 赵先生 on 2017/11/9.
 * 商家详情
 */

public class BusinessDetailListActivity extends BaseActivity {
    private static final String TAG = "BusinessDetailList";
    Context context = this;

    @BindView(R.id.topbar)
    QMUITopBar topbar;
    @BindView(R.id.list_business)
    RecyclerView listBusiness;

    private BusinessListAdapter mAdapter;

    private RecyclerView.LayoutManager mLayoutManager; //    RecyclerView提供的布局管理器

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_businessdetaillist);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        initData();
        initTopBar();
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new BusinessListAdapter(getData());
        // 设置布局管理器
        listBusiness.setLayoutManager(mLayoutManager);
        // 设置adapter
        listBusiness.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initTopBar() {
        super.initTopBar();
        topbar.setTitle("商家详情");
        topbar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 假数据
     * @return
     */
    private ArrayList<BusinessInfo> getData() {
        ArrayList<BusinessInfo> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            BusinessInfo businessinfo = new BusinessInfo();
            businessinfo.business_name = "家庭别墅保洁";
            businessinfo.business_com = "中恒(天津)清洗服务有限公司";
            businessinfo.business_tag = "放心企业";
            businessinfo.business_phone ="15509876754";
            data.add(i, businessinfo);
        }
        return data;
    }
}
