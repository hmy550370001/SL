package com.idc9000.smartlock.bean;

/**
 * Created by 赵先生 on 2018/1/5.
 */

public class RUserOder {

    /**
     * result : {"userOrder":1}
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
         * userOrder : 1
         */

        private int userOrder;

        public int getUserOrder() {
            return userOrder;
        }

        public void setUserOrder(int userOrder) {
            this.userOrder = userOrder;
        }
    }
}
