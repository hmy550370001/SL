package com.idc9000.smartlock.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/12/4.
 */

public class RDeviceResult {


    /**
     * result : {"locks":[{"keyAdmin":"12345678","remains":null,"remark":null,"keyNumber":"12345678","safeMode":0,"mac":null,"endTime":null,"startTime":null,"id":42,"receiveFlag":0,"deviceName":null,"name":"测试二维码","battery":"85","keyUser":"982AF96A534680EC","keyDev":"1234567864657665"},{"keyAdmin":"12345678","remains":"0小时","remark":null,"keyNumber":"12345678","safeMode":0,"mac":null,"endTime":null,"startTime":null,"id":41,"receiveFlag":0,"deviceName":null,"name":"自动连接测试","battery":"85","keyUser":"982AF96A534680EC","keyDev":"1234567864657665"},{"keyAdmin":"12345678","remains":null,"remark":null,"keyNumber":"12345678","safeMode":0,"mac":null,"endTime":null,"startTime":null,"id":40,"receiveFlag":0,"deviceName":null,"name":"天优测试锁1","battery":"85","keyUser":"982AF96A534680EC","keyDev":"1234567864657665"},{"keyAdmin":"12345678","remains":null,"remark":null,"keyNumber":"12345678","safeMode":0,"mac":null,"endTime":null,"startTime":null,"id":37,"receiveFlag":0,"deviceName":null,"name":"整体测试","battery":"85","keyUser":"982AF96A534680EC","keyDev":"1234567864657665"},{"keyAdmin":"12345678","remains":"13小时","remark":null,"keyNumber":"12345678","safeMode":0,"mac":null,"endTime":null,"startTime":null,"id":35,"receiveFlag":0,"deviceName":null,"name":"测时间","battery":"85","keyUser":"982AF96A534680EC","keyDev":"1234567864657665"},{"keyAdmin":"12345678","remains":null,"remark":null,"keyNumber":"12345678","safeMode":0,"mac":null,"endTime":null,"startTime":null,"id":34,"receiveFlag":0,"deviceName":null,"name":"解绑测试","battery":"85","keyUser":"982AF96A534680EC","keyDev":"1234567864657665"},{"keyAdmin":"12345678","remains":null,"remark":null,"keyNumber":"12345678","safeMode":0,"mac":null,"endTime":null,"startTime":null,"id":33,"receiveFlag":0,"deviceName":null,"name":"12345678","battery":"85","keyUser":"982AF96A534680EC","keyDev":"1234567864657665"},{"keyAdmin":"12345678","remains":null,"remark":null,"keyNumber":"12345678","safeMode":0,"mac":null,"endTime":null,"startTime":null,"id":32,"receiveFlag":0,"deviceName":null,"name":"123456","battery":"85","keyUser":"982AF96A534680EC","keyDev":"1234567864657665"},{"keyAdmin":"12345678","remains":null,"remark":null,"keyNumber":"12345678","safeMode":0,"mac":null,"endTime":null,"startTime":null,"id":27,"receiveFlag":0,"deviceName":null,"name":"aa","battery":"85","keyUser":"982AF96A534680EC","keyDev":"1234567864657665"},{"keyAdmin":"12345678","remains":null,"remark":null,"keyNumber":"12345678","safeMode":0,"mac":null,"endTime":null,"startTime":null,"id":25,"receiveFlag":0,"deviceName":null,"name":"zz","battery":"85","keyUser":"982AF96A534680EC","keyDev":"1234567864657665"}]}
     * errorMsg :
     * success : true
     */

    private ResultBean result;
    private String errorMsg;
    private boolean success;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static class ResultBean {
        private List<LocksBean> locks;

        public List<LocksBean> getLocks() {
            return locks;
        }

        public void setLocks(List<LocksBean> locks) {
            this.locks = locks;
        }

        public static class LocksBean {
            /**
             * keyAdmin : 12345678
             * remains : null
             * remark : null
             * keyNumber : 12345678
             * safeMode : 0
             * mac : null
             * endTime : null
             * startTime : null
             * id : 42
             * receiveFlag : 0
             * deviceName : null
             * name : 测试二维码
             * battery : 85
             * keyUser : 982AF96A534680EC
             * keyDev : 1234567864657665
             */

            private String keyAdmin;
            private Object remains;
            private Object remark;
            private String keyNumber;
            private int safeMode;
            private Object mac;
            private Object endTime;
            private Object startTime;
            private int id;
            private int receiveFlag;
            private Object deviceName;
            private String name;
            private String battery;
            private String keyUser;
            private String keyDev;

            public String getKeyAdmin() {
                return keyAdmin;
            }

            public void setKeyAdmin(String keyAdmin) {
                this.keyAdmin = keyAdmin;
            }

            public Object getRemains() {
                return remains;
            }

            public void setRemains(Object remains) {
                this.remains = remains;
            }

            public Object getRemark() {
                return remark;
            }

            public void setRemark(Object remark) {
                this.remark = remark;
            }

            public String getKeyNumber() {
                return keyNumber;
            }

            public void setKeyNumber(String keyNumber) {
                this.keyNumber = keyNumber;
            }

            public int getSafeMode() {
                return safeMode;
            }

            public void setSafeMode(int safeMode) {
                this.safeMode = safeMode;
            }

            public Object getMac() {
                return mac;
            }

            public void setMac(Object mac) {
                this.mac = mac;
            }

            public Object getEndTime() {
                return endTime;
            }

            public void setEndTime(Object endTime) {
                this.endTime = endTime;
            }

            public Object getStartTime() {
                return startTime;
            }

            public void setStartTime(Object startTime) {
                this.startTime = startTime;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getReceiveFlag() {
                return receiveFlag;
            }

            public void setReceiveFlag(int receiveFlag) {
                this.receiveFlag = receiveFlag;
            }

            public Object getDeviceName() {
                return deviceName;
            }

            public void setDeviceName(Object deviceName) {
                this.deviceName = deviceName;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getBattery() {
                return battery;
            }

            public void setBattery(String battery) {
                this.battery = battery;
            }

            public String getKeyUser() {
                return keyUser;
            }

            public void setKeyUser(String keyUser) {
                this.keyUser = keyUser;
            }

            public String getKeyDev() {
                return keyDev;
            }

            public void setKeyDev(String keyDev) {
                this.keyDev = keyDev;
            }
        }
    }
}
