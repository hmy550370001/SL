package com.idc9000.smartlock.event;

/**
 * Created by Administrator on 2017/12/20.
 */

public class InputPropertyEvent {
    String lockId;
    String eleNumber;
    String gasNumber;
    String waterNumber;
    String remark;


    public InputPropertyEvent(String lockId, String eleNumber, String gasNumber, String waterNumber, String remark) {
        this.lockId = lockId;
        this.eleNumber = eleNumber;
        this.gasNumber = gasNumber;
        this.waterNumber = waterNumber;
        this.remark = remark;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    public InputPropertyEvent(String eleNumber, String gasNumber, String waterNumber, String remark) {
        this.eleNumber = eleNumber;
        this.gasNumber = gasNumber;
        this.waterNumber = waterNumber;
        this.remark = remark;
    }

    public String getEleNumber() {
        return eleNumber;
    }

    public void setEleNumber(String eleNumber) {
        this.eleNumber = eleNumber;
    }

    public String getGasNumber() {
        return gasNumber;
    }

    public void setGasNumber(String gasNumber) {
        this.gasNumber = gasNumber;
    }

    public String getWaterNumber() {
        return waterNumber;
    }

    public void setWaterNumber(String waterNumber) {
        this.waterNumber = waterNumber;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "InputPropertyEvent{" +
                "lockId='" + lockId + '\'' +
                ", eleNumber='" + eleNumber + '\'' +
                ", gasNumber='" + gasNumber + '\'' +
                ", waterNumber='" + waterNumber + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
