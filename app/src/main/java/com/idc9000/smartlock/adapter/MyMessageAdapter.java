package com.idc9000.smartlock.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.bean.RMessage;

import java.util.List;

/**
 * Created by Administrator on 2017/12/26.
 */

public class MyMessageAdapter extends BaseQuickAdapter<RMessage.ResultBean.InfosBean, BaseViewHolder> {


    public MyMessageAdapter(int layoutResId, @Nullable List<RMessage.ResultBean.InfosBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RMessage.ResultBean.InfosBean item) {
        helper.setText(R.id.time,item.getCreateTime());
        helper.setText(R.id.content,item.getContent());
    }
}
