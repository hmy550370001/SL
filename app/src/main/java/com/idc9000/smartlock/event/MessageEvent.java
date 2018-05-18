package com.idc9000.smartlock.event;

/**
 * 事件实体类
 */

public class MessageEvent {

    private String message;

    public MessageEvent(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
