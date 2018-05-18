package com.idc9000.smartlock.cons;

import com.clj.fastble.data.BleDevice;

/**
 * Created by 赵先生 on 2017/10/9.
 */

public class Constants {
    //当前用户手机号
    public static String CurrentUserPhone;
    //用户状态
    public static String UserState;
    //当前用户Id
    public static String CurrentUserID;
    //房主绑定设备时的输入的密码
    public static String AdminPassWord;
    //租户绑定设备时的输入的密码
    public static String UserPassWord;
    //当前设备的电量
    public static String battery;

    public static String uuid_service ="55e23323-5b3d-4cbe-b159-add467479c82";
    public static String UUID_NOTIFY ="5ee407a8-65f0-44fc-ad69-8db38c8e8c22";
    public static String UUID_WRITE = "5ee407a8-65f0-44fc-ad69-8db38c8e8c22";
    public static String Mac="";
    public static String deviceName="";
    public static BleDevice bleDevice;

    //Mob短信验证码
    public static String Mob_Appkey="2190472881729";
    public static String MobApp_Secret="2cd2ca8033bbb4c0f1fc613b6d2401c0";
}
