package com.idc9000.smartlock.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/12/4.
 */

public class RKeyListResult {


    /**
     * result : {"keys":[{"id":30,"username":"王五","receiveFlag":"未领取","orderNumber":1,"receiveTime":""}]}
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
        private List<KeysBean> keys;

        public List<KeysBean> getKeys() {
            return keys;
        }

        public void setKeys(List<KeysBean> keys) {
            this.keys = keys;
        }

        public static class KeysBean {
            /**
             * id : 30
             * username : 王五
             * receiveFlag : 未领取
             * orderNumber : 1
             * receiveTime :
             */

            private int id;
            private String username;
            private String receiveFlag;
            private int orderNumber;
            private String receiveTime;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getReceiveFlag() {
                return receiveFlag;
            }

            public void setReceiveFlag(String receiveFlag) {
                this.receiveFlag = receiveFlag;
            }

            public int getOrderNumber() {
                return orderNumber;
            }

            public void setOrderNumber(int orderNumber) {
                this.orderNumber = orderNumber;
            }

            public String getReceiveTime() {
                return receiveTime;
            }

            public void setReceiveTime(String receiveTime) {
                this.receiveTime = receiveTime;
            }
        }
    }
}
