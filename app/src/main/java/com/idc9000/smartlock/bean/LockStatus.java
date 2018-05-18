package com.idc9000.smartlock.bean;

/**
 * Created by 赵先生 on 2017/11/7.
 */

public class LockStatus {

    byte bat;//电量
    long time;//时间
    byte mark;//标志
    byte fmark;//防打扰标志

    public LockStatus(byte bat, long time, byte mark, byte fmark) {
        this.bat = bat;
        this.time = time;
        this.mark = mark;
        this.fmark = fmark;
    }

    public byte getBat() {
        return bat;
    }

    public void setBat(byte bat) {
        this.bat = bat;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public byte getMark() {
        return mark;
    }

    public void setMark(byte mark) {
        this.mark = mark;
    }

    public byte getFmark() {
        return fmark;
    }

    public void setFmark(byte fmark) {
        this.fmark = fmark;
    }

    @Override
    public String toString() {
        return "LockStatus{" +
                "bat=" + bat +
                ", time=" + time +
                ", mark=" + mark +
                ", fmark=" + fmark +
                '}';
    }
}
