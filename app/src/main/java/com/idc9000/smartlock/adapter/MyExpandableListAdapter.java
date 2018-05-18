package com.idc9000.smartlock.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.activity.CreateKeyPasswordActivity;
import com.idc9000.smartlock.activity.SearchDeviceActivity;
import com.idc9000.smartlock.activity.UpdateLeaseActivity;
import com.idc9000.smartlock.bean.RProperty;
import com.idc9000.smartlock.cons.Constants;
import com.idc9000.smartlock.event.BindingEvent;
import com.idc9000.smartlock.event.UpdateLeaseEvent;
import com.idc9000.smartlock.utils.ActivityUtil;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by 赵先生 on 2017/10/27.
 */

public class MyExpandableListAdapter  extends BaseExpandableListAdapter {

    private Activity mContext;
    private RProperty rProperty;

    public MyExpandableListAdapter(Activity mContext, RProperty rProperty) {
        this.mContext = mContext;
        this.rProperty=rProperty;
    }

    //        获取分组的个数
    @Override
    public int getGroupCount() {
        return rProperty.getResult().getKeys().size();
    }
    //        获取指定分组中的子选项的个数
    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }
    //        获取指定的分组数据
    @Override
    public Object getGroup(int groupPosition) {
        return rProperty.getResult().getKeys().get(groupPosition);
    }
    //        获取指定分组中的指定子选项数据
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return rProperty.getResult().getKeys().get(groupPosition);
    }
    //        获取指定分组的ID, 这个ID必须是唯一的
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    //        获取子选项的ID, 这个ID必须是唯一的
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    //        分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们。
    @Override
    public boolean hasStableIds() {
        return true;
    }
    //        获取显示指定分组的视图
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.property_payment_header, parent, false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            groupViewHolder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);

            groupViewHolder.iv_check= (ImageView) convertView.findViewById(R.id.next);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        if (isExpanded){
            groupViewHolder.iv_check.setBackgroundResource(R.mipmap.heshsang);
        }else {
            groupViewHolder.iv_check.setBackgroundResource(R.mipmap.zhanka);
        }
        groupViewHolder.tv_name.setText(rProperty.getResult().getKeys().get(groupPosition).getUsername());
        groupViewHolder.tv_phone.setText(rProperty.getResult().getKeys().get(groupPosition).getPhone());

        return convertView;
    }
    //        获取显示指定分组中的指定子选项的视图
    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.property_payment_content, parent, false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.tv_rent = (TextView) convertView.findViewById(R.id.tv_rent);
            childViewHolder.tv_start_time=(TextView) convertView.findViewById(R.id.tv_start_time);
            childViewHolder.tv_end_time=(TextView) convertView.findViewById(R.id.tv_end_time);
            childViewHolder.btn_update=(QMUIRoundButton) convertView.findViewById(R.id.btn_update);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        childViewHolder.tv_rent.setText(rProperty.getResult().getKeys().get(groupPosition).getHirePrice()+"元/"+rProperty.getResult().getKeys().get(groupPosition).getHireType());
        childViewHolder.tv_start_time.setText(rProperty.getResult().getKeys().get(groupPosition).getStartTime());
        childViewHolder.tv_end_time.setText(rProperty.getResult().getKeys().get(groupPosition).getEndTime());
        childViewHolder.btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id=String.valueOf(rProperty.getResult().getKeys().get(groupPosition).getId());
                String userName= rProperty.getResult().getKeys().get(groupPosition).getUsername();
                String phone=rProperty.getResult().getKeys().get(groupPosition).getPhone();
                String EndTime=rProperty.getResult().getKeys().get(groupPosition).getEndTime();
                String StartTime=rProperty.getResult().getKeys().get(groupPosition).getStartTime();

                EventBus.getDefault().postSticky(new UpdateLeaseEvent(id,userName,phone,StartTime,EndTime));

                if (Constants.UserState.equals("Admin")){
                    ActivityUtil.start(mContext, UpdateLeaseActivity .class,false);
                }else {
                    EventBus.getDefault().postSticky(new BindingEvent("UserUpdateLease",null));
                    ActivityUtils.startActivity(SearchDeviceActivity.class);
                }

            }
        });
        return convertView;
    }
    //        指定位置上的子元素是否可选中
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class GroupViewHolder {
        TextView tv_name;
        TextView tv_phone;
        ImageView iv_profle;
        ImageView iv_check;
    }
    static class ChildViewHolder {
        TextView tv_rent;
        TextView tv_start_time;
        TextView tv_end_time;
        QMUIRoundButton btn_update;
    }
}
