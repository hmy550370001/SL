package com.idc9000.smartlock.event;

/**
 * 绑定设备事件
 */

public class BindingEvent {

    String BindType;
    String PassWord;

    String value;


    public BindingEvent(String bindType, String passWord, String value) {
        BindType = bindType;
        PassWord = passWord;
        this.value = value;
    }

    public BindingEvent(String bindType, String passWord) {
        BindType = bindType;
        PassWord = passWord;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getBindType() {
        return BindType;
    }

    public void setBindType(String bindType) {
        BindType = bindType;
    }

    public String getPassWord() {
        return PassWord;
    }

    public void setPassWord(String passWord) {
        PassWord = passWord;
    }

    @Override
    public String toString() {
        return "BindingEvent{" +
                "BindType='" + BindType + '\'' +
                ", PassWord='" + PassWord + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
