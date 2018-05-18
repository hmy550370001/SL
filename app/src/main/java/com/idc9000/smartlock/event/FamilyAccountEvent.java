package com.idc9000.smartlock.event;

/**
 * 事件实体类
 */

public class FamilyAccountEvent {

    private String message;

    public FamilyAccountEvent(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
