package com.idc9000.smartlock.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.idc9000.smartlock.R;
import com.idc9000.smartlock.bean.BusinessInfo;

import java.util.ArrayList;

/**
 * Created by 赵先生 on 2017/10/25.
 *
 */

public class BusinessListAdapter extends RecyclerView.Adapter<BusinessListAdapter.ViewHolder> implements View.OnClickListener{

    private ArrayList<BusinessInfo> mData;

    public BusinessListAdapter(ArrayList<BusinessInfo> mData) {
        this.mData = mData;
    }

    public void updateData(ArrayList<BusinessInfo> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_businesslist, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(v);
        //将创建的View注册点击事件
        v.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_name.setText(mData.get(position).business_name);
        holder.tv_com.setText(mData.get(position).business_com);
        holder.tv_tag.setText(mData.get(position).business_tag);
        holder.tv_phone.setText(mData.get(position).business_phone);
        //将数据保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(view,(BusinessInfo) view.getTag());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        TextView tv_com;
        TextView tv_tag;
        TextView tv_phone;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_com= (TextView) itemView.findViewById(R.id.tv_com);
            tv_tag= (TextView) itemView.findViewById(R.id.tv_tag);
            tv_phone= (TextView) itemView.findViewById(R.id.tv_phone);
        }
    }

    /**
     * 实现点击接口
     * view是点击的Item，data是数据
     */
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, BusinessInfo data);
    }

    /**
     * 添加接口和设置Adapter接口的方法
     */
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
