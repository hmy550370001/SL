package com.idc9000.smartlock.bean;

import java.util.List;

/**
 * 物业信息
 */

public class RProperty {

    /**
     * result : {"keys":[{"startTime":"2017/12/20","id":20,"phone":"18722378112","username":"李四","hirePrice":1,"hireType":"月","endTime":"2017/12/21"}],"lock":{"id":7,"eleNumber":123,"remark":null,"waterPrice":0,"battery":null,"name":"5","gasPrice":0,"gasNumber":123,"elePrice":0,"waterNumber":123}}
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
        /**
         * keys : [{"startTime":"2017/12/20","id":20,"phone":"18722378112","username":"李四","hirePrice":1,"hireType":"月","endTime":"2017/12/21"}]
         * lock : {"id":7,"eleNumber":123,"remark":null,"waterPrice":0,"battery":null,"name":"5","gasPrice":0,"gasNumber":123,"elePrice":0,"waterNumber":123}
         */

        private LockBean lock;
        private List<KeysBean> keys;

        public LockBean getLock() {
            return lock;
        }

        public void setLock(LockBean lock) {
            this.lock = lock;
        }

        public List<KeysBean> getKeys() {
            return keys;
        }

        public void setKeys(List<KeysBean> keys) {
            this.keys = keys;
        }

        public static class LockBean {
            /**
             * id : 7
             * eleNumber : 123
             * remark : null
             * waterPrice : 0
             * battery : null
             * name : 5
             * gasPrice : 0
             * gasNumber : 123
             * elePrice : 0
             * waterNumber : 123
             */

            private int id;
            private int eleNumber;
            private Object remark;
            private int waterPrice;
            private Object battery;
            private String name;
            private int gasPrice;
            private int gasNumber;
            private int elePrice;
            private int waterNumber;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getEleNumber() {
                return eleNumber;
            }

            public void setEleNumber(int eleNumber) {
                this.eleNumber = eleNumber;
            }

            public Object getRemark() {
                return remark;
            }

            public void setRemark(Object remark) {
                this.remark = remark;
            }

            public int getWaterPrice() {
                return waterPrice;
            }

            public void setWaterPrice(int waterPrice) {
                this.waterPrice = waterPrice;
            }

            public Object getBattery() {
                return battery;
            }

            public void setBattery(Object battery) {
                this.battery = battery;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getGasPrice() {
                return gasPrice;
            }

            public void setGasPrice(int gasPrice) {
                this.gasPrice = gasPrice;
            }

            public int getGasNumber() {
                return gasNumber;
            }

            public void setGasNumber(int gasNumber) {
                this.gasNumber = gasNumber;
            }

            public int getElePrice() {
                return elePrice;
            }

            public void setElePrice(int elePrice) {
                this.elePrice = elePrice;
            }

            public int getWaterNumber() {
                return waterNumber;
            }

            public void setWaterNumber(int waterNumber) {
                this.waterNumber = waterNumber;
            }
        }

        public static class KeysBean {
            /**
             * startTime : 2017/12/20
             * id : 20
             * phone : 18722378112
             * username : 李四
             * hirePrice : 1
             * hireType : 月
             * endTime : 2017/12/21
             */

            private String startTime;
            private int id;
            private String phone;
            private String username;
            private int hirePrice;
            private String hireType;
            private String endTime;

            public String getStartTime() {
                return startTime;
            }

            public void setStartTime(String startTime) {
                this.startTime = startTime;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public int getHirePrice() {
                return hirePrice;
            }

            public void setHirePrice(int hirePrice) {
                this.hirePrice = hirePrice;
            }

            public String getHireType() {
                return hireType;
            }

            public void setHireType(String hireType) {
                this.hireType = hireType;
            }

            public String getEndTime() {
                return endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
            }
        }
    }
}
