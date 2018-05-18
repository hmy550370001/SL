package com.idc9000.smartlock.cons;

/**
 * 网络请求接口
 */

public class NetworkConst {
    /**
     * URL
     */
//    private static final String BASE_URL="http://10.10.10.105:8085";
//    private static final String BASE_URL="http://10.10.10.105:8085";
    public static final String BASE_URL="http://103.248.102.5:10091/i9-lock-api";
    /**
     * 注册
     */
    public static final String LOGIN_URL=BASE_URL+"/user/login";
    /**
     * 登录
     */
    public static final String REGISTER_URL=BASE_URL+"/user/regist";
    /**
     * 添加锁具
     */
    public static final String ADDLOCK_URL=BASE_URL+"/lock/save";
    /**
     * 房东设备列表
     */
    public static final String ADMINDEVICELIST_URL=BASE_URL+"/lock/landlordKey";
    /**
     * 租客设备列表
     */
    public static final String USERDEVICELIST_URL=BASE_URL+"/lock/authorizeList";
    /**
     * 添加钥匙
     */
    public static final String ADDLOCKKEY_URL=BASE_URL+"/lockKey/add";
    /**
     * 钥匙列表
     */
    public static final String LOCKKEYLIST=BASE_URL+"/lockKey/list";
    /**
     * 门锁日志
     */
    public static final String LockRecord=BASE_URL+"/locklog/list";
    /**
     *  物业缴费
     */
    public static final String PropertyPayMent=BASE_URL+"/lock/price";
    /**
     * 移交锁验证
     */
    public static final String HandoverManagement=BASE_URL+"/lock/releaseValidate";
    /**
     *  移交锁
     */
    public static final String release=BASE_URL+"/lock/release";
    /**
     * 获取可用密码顺序编号
     */
    public static final String getUsefulOrderNumber=BASE_URL+"/password/getUsefulOrderNumber";
    /**
     * 更改密码
     */
    public static final String updatePwd =BASE_URL+"/user/updatePwd";
    /**
     * 个人中心
     */
    public static final String index =BASE_URL+"/user/index";
    /**
     * 更改亲情号
     */
    public static final String updateFamilyPhone =BASE_URL+"/user/updateFamilyPhone";
    /**
     * 更新锁
     */
    public static final String update=BASE_URL+"/lock/update";
    /**
     * 更新钥匙
     */
    public static final String updatelockKey  =BASE_URL+"/lockKey/update";
    /**
     * 更新租期
     */
    public static final String updateEndTime  =BASE_URL+"/lockKey/updateEndTime";
    /**
     * 消息中心
     */
    public static final String info  =BASE_URL+"/info/list";
    /**
     * 删除钥匙
     */
    public static final String deleteKey  =BASE_URL+"/lockKey/delete";
    /**
     * 删除全部钥匙
     */
    public static final String deleteAllKey  =BASE_URL+"/lockKey/deleteAll";
    /**
     * 密码列表
     */
    public static final String passwordlist  =BASE_URL+"/password/list";
    /**
     * 添加密码
     */
    public static final String addpassword  =BASE_URL+"/password/add";
    /**
     * 获取用户组编号
     */
    public static final String getUserOrderNumber  =BASE_URL+"/lockKey/getUserOrderNumber";
    /**
     * 删除密码
     */
    public static final String deletePassWord  =BASE_URL+"/password/delete";
}
