package com.idc9000.smartlock.bean;

/**
 * Created by Administrator on 2018/1/11.
 */

public class RScanDevice {

    /**
     * deviceName : SmartLock
     * mac : 00:A0:50:00:05:08
     */

    private String deviceName;
    private String mac;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
