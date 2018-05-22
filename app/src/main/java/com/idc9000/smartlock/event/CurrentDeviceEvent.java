package com.idc9000.smartlock.event;

/**
 * 事件实体类
 */

public class CurrentDeviceEvent {

    public String deviceId;//设备Id
    public String name;//锁名
    public String deviceName;//设备名
    public String battery;//电量
    public String remains;//剩余租期
    public String lockNo;//锁号 设备上的
    public String phone;//账号
    public String keyAdmin; //租户密码
    public String KeyUser;
    public String KeyDev;
    public int receiveFlag;//是否领取
    public String startTime; //开始时间
    public String endTime; //结束时间
    public int safeMode; //安全模式
    public String mac;//mac 地址
    //   public  String

    public CurrentDeviceEvent() {
    }

    public CurrentDeviceEvent(String deviceId, String name, String deviceName, String battery, String remains, String lockNo, String phone, String keyAdmin, String keyUser, String keyDev, int receiveFlag, String startTime, String endTime, int safeMode, String mac) {
        this.deviceId = deviceId;
        this.name = name;
        this.deviceName = deviceName;
        this.battery = battery;
        this.remains = remains;
        this.lockNo = lockNo;
        this.phone = phone;
        this.keyAdmin = keyAdmin;
        KeyUser = keyUser;
        KeyDev = keyDev;
        this.receiveFlag = receiveFlag;
        this.startTime = startTime;
        this.endTime = endTime;
        this.safeMode = safeMode;
        this.mac = mac;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getRemains() {
        return remains;
    }

    public void setRemains(String remains) {
        this.remains = remains;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getLockNo() {
        return lockNo;
    }

    public void setLockNo(String lockNo) {
        this.lockNo = lockNo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getKeyAdmin() {
        return keyAdmin;
    }

    public void setKeyAdmin(String keyAdmin) {
        this.keyAdmin = keyAdmin;
    }

    public String getKeyUser() {
        return KeyUser;
    }

    public void setKeyUser(String keyUser) {
        KeyUser = keyUser;
    }

    public String getKeyDev() {
        return KeyDev;
    }

    public void setKeyDev(String keyDev) {
        KeyDev = keyDev;
    }

    public int getReceiveFlag() {
        return receiveFlag;
    }

    public void setReceiveFlag(int receiveFlag) {
        this.receiveFlag = receiveFlag;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getSafeMode() {
        return safeMode;
    }

    public void setSafeMode(int safeMode) {
        this.safeMode = safeMode;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    @Override
    public String toString() {
        return "CurrentDeviceEvent{" +
                "deviceId='" + deviceId + '\'' +
                ", name='" + name + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", battery='" + battery + '\'' +
                ", remains='" + remains + '\'' +
                ", lockNo='" + lockNo + '\'' +
                ", phone='" + phone + '\'' +
                ", keyAdmin='" + keyAdmin + '\'' +
                ", KeyUser='" + KeyUser + '\'' +
                ", KeyDev='" + KeyDev + '\'' +
                ", receiveFlag=" + receiveFlag +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", safeMode=" + safeMode +
                ", mac='" + mac + '\'' +
                '}';
    }
}
