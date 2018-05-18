package com.idc9000.smartlock.event;

/**
 * Created by Administrator on 2017/12/20.
 */

public class UpdateLeaseEvent {
    String id;//钥匙id
    String userName;//用户名
    String phone;//账号
    String startTime;
    String endTime;

    public UpdateLeaseEvent() {
    }

    public UpdateLeaseEvent(String id, String userName, String phone, String startTime, String endTime) {
        this.id = id;
        this.userName = userName;
        this.phone = phone;
        this.startTime = startTime;
        this.endTime = endTime;
    }


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "UpdateLeaseEvent{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", phone='" + phone + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
