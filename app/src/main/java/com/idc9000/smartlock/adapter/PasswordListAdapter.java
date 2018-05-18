package com.idc9000.smartlock.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.bean.KeyInfo;
import com.idc9000.smartlock.bean.PasswordInfo;
import com.idc9000.smartlock.bean.RMessage;
import com.idc9000.smartlock.bean.RPassWordResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 赵先生 on 2017/10/25.
 */

public class PasswordListAdapter extends BaseQuickAdapter<RPassWordResult.ResultBean.PasswordsBean, BaseViewHolder> {
    Boolean isDeleteState;

    public PasswordListAdapter(int layoutResId, @Nullable List<RPassWordResult.ResultBean.PasswordsBean> data,Boolean isDeleteState) {
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
    protected void convert(BaseViewHolder helper, RPassWordResult.ResultBean.PasswordsBean item) {
        helper.setText(R.id.id,""+(item.getOrderNumber()+1));
        helper.setText(R.id.name,""+item.getName());
        helper.setText(R.id.type,item.getType());
        if (isDeleteState){
            helper.getView(R.id.check_box).setVisibility(View.VISIBLE);
            helper.getView(R.id.id).setVisibility(View.INVISIBLE);
            if (item.isSelect){
                helper.getView(R.id.check_box).setBackgroundResource(R.mipmap.choose_selected);
            }else {
                helper.getView(R.id.check_box).setBackgroundResource(R.mipmap.choose_default);
            }
        }
    }
}
