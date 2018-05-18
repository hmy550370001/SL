package com.idc9000.smartlock.bean;

/**
 * Created by 赵先生 on 2017/10/25.
 */

public class KeyInfo {
    public String id;
    public boolean isDeleteState;//是否删除状态
    public boolean isSelect; //选中状态
    public String time; //时间
    public String name; //姓名
    public int orderNumber;//组编号
    public String state; //状态

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public boolean isDeleteState() {
        return isDeleteState;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDeleteState(boolean deleteState) {
        isDeleteState = deleteState;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "KeyInfo{" +
                "id='" + id + '\'' +
                ", isDeleteState=" + isDeleteState +
                ", isSelect=" + isSelect +
                ", time='" + time + '\'' +
                ", name='" + name + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
