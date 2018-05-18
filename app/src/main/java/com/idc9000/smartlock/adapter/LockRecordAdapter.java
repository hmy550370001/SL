package com.idc9000.smartlock.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.idc9000.smartlock.R;
import com.idc9000.smartlock.bean.RecordInfo;
import com.idc9000.smartlock.bean.RecordItemInfo;

import java.util.ArrayList;

/**
 * Created by 赵先生 on 2017/10/25.
 */

public class LockRecordAdapter extends RecyclerView.Adapter <LockRecordAdapter.BaseAdapter>{
    private ArrayList<RecordItemInfo> dataList = new ArrayList<>();

    public void replaceAll(ArrayList<RecordItemInfo> list) {
        dataList.clear();
        if (list != null && list.size() > 0) {
            dataList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<RecordItemInfo> list) {
        if (dataList != null && list != null) {
            dataList.addAll(list);
            notifyItemRangeChanged(dataList.size(),list.size());
        }

    }

    @Override
    public LockRecordAdapter.BaseAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case RecordItemInfo.RECORD_LEFT:
                return new RecordLeftHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record_left, parent, false));
            case RecordItemInfo.RECORD_RIGHT:
                return new RecordRightHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record_right, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseAdapter holder, int position) {
        holder.setData(dataList.get(position).object);
    }



    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).type;
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }


    public class BaseAdapter extends RecyclerView.ViewHolder {

        public BaseAdapter(View itemView) {
            super(itemView);
        }

        void setData(Object object) {

        }
    }


    private class RecordLeftHolder extends BaseAdapter {
        private TextView name;
        private TextView content;

        public RecordLeftHolder(View view) {
            super(view);
            name = (TextView) itemView.findViewById(R.id.tv_left_name);
            content = (TextView) itemView.findViewById(R.id.tv_left_content);
        }

        @Override
        void setData(Object object) {
            super.setData(object);
            RecordInfo model = (RecordInfo) object;
            name.setText(model.getName());
            content.setText(model.getContent());
        }
    }

    private class RecordRightHolder extends BaseAdapter {
        private TextView name;
        private TextView content;

        public RecordRightHolder(View view) {
            super(view);
            name = (TextView) itemView.findViewById(R.id.tv_right_name);
            content = (TextView) itemView.findViewById(R.id.tv_right_content);

        }

        @Override
        void setData(Object object) {
            super.setData(object);
            RecordInfo model = (RecordInfo) object;
            name.setText(model.getName());
            content.setText(model.getContent());
        }
    }
}
