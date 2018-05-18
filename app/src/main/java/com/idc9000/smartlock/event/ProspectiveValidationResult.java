package com.idc9000.smartlock.event;

import java.util.Arrays;

/**
 * 预验证指令返回值
 */

public class ProspectiveValidationResult {

    boolean isSucesss;//预验证是否成功

    byte lockNo[]; //锁号

    byte randomNumber[];//随机数

    byte KeyDev[];

    public ProspectiveValidationResult(boolean isSucesss, byte[] lockNo, byte[] randomNumber, byte[] keyDev) {
        this.isSucesss = isSucesss;
        this.lockNo = lockNo;
        this.randomNumber = randomNumber;
        KeyDev = keyDev;
    }


    public byte[] getKeyDev() {
        return KeyDev;
    }

    public void setKeyDev(byte[] keyDev) {
        KeyDev = keyDev;
    }

    public boolean isSucesss() {
        return isSucesss;
    }

    public void setSucesss(boolean sucesss) {
        isSucesss = sucesss;
    }

    public byte[] getLockNo() {
        return lockNo;
    }

    public void setLockNo(byte[] lockNo) {
        this.lockNo = lockNo;
    }

    public byte[] getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(byte[] randomNumber) {
        this.randomNumber = randomNumber;
    }

    @Override
    public String toString() {
        return "ProspectiveValidationEvent{" +
                "isSucesss=" + isSucesss +
                ", lockNo=" + Arrays.toString(lockNo) +
                ", randomNumber=" + Arrays.toString(randomNumber) +
                ", KeyDev=" + Arrays.toString(KeyDev) +
                '}';
    }
}
