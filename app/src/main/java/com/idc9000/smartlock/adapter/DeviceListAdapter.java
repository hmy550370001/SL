package com.idc9000.smartlock.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.event.CurrentDeviceEvent;

import java.util.List;

/**
 *设备列表适配器
 */

public class DeviceListAdapter extends BaseQuickAdapter<CurrentDeviceEvent, BaseViewHolder> {

    public DeviceListAdapter(int layoutResId, @Nullable List<CurrentDeviceEvent> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CurrentDeviceEvent item) {
        helper.setText(R.id.device_address,item.getName());
        if (item.getRemains()!=null){
            helper.setText(R.id.device_remaining_lease,"剩余租期："+item.getRemains());
        }else {
            helper.setText(R.id.device_remaining_lease,"尚未出租");
        }
        helper.setText(R.id.device_remaining_capacity,"剩余电量："+item.getBattery()+"%");
    }
}
