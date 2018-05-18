package com.idc9000.smartlock.bean;

/**
 * Created by Administrator on 2017/12/18.
 */

public class RCommonInfo {

    /**
     * result :
     * errorMsg : 一个月之内之内修改一次亲情号
     * success : false
     */

    private String result;
    private String errorMsg;
    private boolean success;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
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
}
