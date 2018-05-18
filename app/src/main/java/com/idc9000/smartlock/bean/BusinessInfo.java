package com.idc9000.smartlock.bean;

/**
 * Created by 赵先生 on 2017/10/25.
 */

public class BusinessInfo {

    public String business_name; //标题
    public String business_com; //公司名
    public String business_tag; //公司标签
    public String business_phone; //手机号

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getBusiness_com() {
        return business_com;
    }

    public void setBusiness_com(String business_com) {
        this.business_com = business_com;
    }

    public String getBusiness_tag() {
        return business_tag;
    }

    public void setBusiness_tag(String business_tag) {
        this.business_tag = business_tag;
    }

    public String getBusiness_phone() {
        return business_phone;
    }

    public void setBusiness_phone(String business_phone) {
        this.business_phone = business_phone;
    }

    @Override
    public String toString() {
        return "BusinessInfo{" +
                "business_name='" + business_name + '\'' +
                ", business_com='" + business_com + '\'' +
                ", business_tag='" + business_tag + '\'' +
                ", business_phone='" + business_phone + '\'' +
                '}';
    }
}
