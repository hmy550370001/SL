package com.idc9000.smartlock.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.bean.KeyInfo;

import java.util.List;

/**
 * 钥匙列表适配器
 */

public class MyKeyListAdapter extends BaseQuickAdapter<KeyInfo, BaseViewHolder> {

    Boolean isDeleteState;

    public MyKeyListAdapter(int layoutResId, @Nullable List<KeyInfo> data, Boolean isDeleteState) {
        super(layoutResId, data);
        this.isDeleteState = isDeleteState;
    }

    public Boolean getDeleteState() {
        return isDeleteState;
    }

    public void setDeleteState(Boolean deleteState) {
        isDeleteState = deleteState;
    }

    @Override
    protected void convert(BaseViewHolder helper, KeyInfo item) {
        helper.setText(R.id.time,item.getTime());
        helper.setText(R.id.name,item.getName());
        helper.setText(R.id.state,item.getState());
        if (isDeleteState){
            helper.getView(R.id.check_box).setVisibility(View.VISIBLE);
            if (item.isSelect){
                helper.getView(R.id.check_box).setBackgroundResource(R.mipmap.choose_selected);
            }else {
                helper.getView(R.id.check_box).setBackgroundResource(R.mipmap.choose_default);
            }
        }
    }

}
