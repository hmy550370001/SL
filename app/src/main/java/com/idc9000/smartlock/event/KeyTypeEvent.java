package com.idc9000.smartlock.event;

/**
 * 钥匙类型
 */

public class KeyTypeEvent {

    private String type;

    public KeyTypeEvent(String type){
        this.type = type;
    }

    public String getMessage(){
        return type;
    }
}
