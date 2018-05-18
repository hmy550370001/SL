package com.idc9000.smartlock.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.bean.RLockRecord;
import com.idc9000.smartlock.bean.RMessage;

import java.util.List;

/**
 * Created by Administrator on 2017/12/26.
 */

public class MyLockRecordAdapter extends BaseQuickAdapter<RLockRecord.ResultBean.LocklogsBean, BaseViewHolder> {


    public MyLockRecordAdapter(int layoutResId, @Nullable List<RLockRecord.ResultBean.LocklogsBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RLockRecord.ResultBean.LocklogsBean item) {
        helper.setText(R.id.tv_left_content, item.getContent());
        helper.setText(R.id.tv_left_name, item.getUsername() );
        helper.setText(R.id.time, item.getTime());
    }
}
