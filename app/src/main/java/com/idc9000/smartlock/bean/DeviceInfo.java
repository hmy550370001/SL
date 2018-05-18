package com.idc9000.smartlock.bean;

/**
 * Created by 赵先生 on 2017/10/25.
 */

public class DeviceInfo {

    public String DeviceId;//锁Id
    public String device_icon;//图片地址
    public String device_address; //剩余地址
    public String device_remaining_lease; //剩余租期
    public String device_remaining_capacity; //剩余电量

    public DeviceInfo() {
    }

    public DeviceInfo(String deviceId, String device_icon, String device_address, String device_remaining_lease, String device_remaining_capacity) {
        DeviceId = deviceId;
        this.device_icon = device_icon;
        this.device_address = device_address;
        this.device_remaining_lease = device_remaining_lease;
        this.device_remaining_capacity = device_remaining_capacity;
    }

    public DeviceInfo(String device_icon, String device_address, String device_remaining_lease, String device_remaining_capacity) {
        this.device_icon = device_icon;
        this.device_address = device_address;
        this.device_remaining_lease = device_remaining_lease;
        this.device_remaining_capacity = device_remaining_capacity;
    }

    public DeviceInfo(String device_address, String device_remaining_lease, String device_remaining_capacity) {
        this.device_address = device_address;
        this.device_remaining_lease = device_remaining_lease;
        this.device_remaining_capacity = device_remaining_capacity;
    }


    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }

    public String getDevice_icon() {
        return device_icon;
    }

    public void setDevice_icon(String device_icon) {
        this.device_icon = device_icon;
    }

    public String getDevice_address() {
        return device_address;
    }

    public void setDevice_address(String device_address) {
        this.device_address = device_address;
    }

    public String getDevice_remaining_lease() {
        return device_remaining_lease;
    }

    public void setDevice_remaining_lease(String device_remaining_lease) {
        this.device_remaining_lease = device_remaining_lease;
    }

    public String getDevice_remaining_capacity() {
        return device_remaining_capacity;
    }

    public void setDevice_remaining_capacity(String device_remaining_capacity) {
        this.device_remaining_capacity = device_remaining_capacity;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "DeviceId='" + DeviceId + '\'' +
                ", device_icon='" + device_icon + '\'' +
                ", device_address='" + device_address + '\'' +
                ", device_remaining_lease='" + device_remaining_lease + '\'' +
                ", device_remaining_capacity='" + device_remaining_capacity + '\'' +
                '}';
    }
}
