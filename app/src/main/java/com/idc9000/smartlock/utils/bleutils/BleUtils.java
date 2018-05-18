package com.idc9000.smartlock.utils.bleutils;

import com.idc9000.smartlock.cons.Constants;
import com.idc9000.smartlock.event.ProspectiveValidationResult;
import com.socks.library.KLog;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;

/**
 * 账户类型3种，00——管理员账户、01——用户、02——租户
 * KeyAdmin：超级管理员（用户设定）
 * KeyUser    固定密钥
 * KeyDev    为出厂固定一锁一密
 */

public class BleUtils {

    private static final String TAG = "Ble";

    public static byte[] DefaultKeyAdmin = {0x12, 0x34, 0x56, 0x78, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
    public static byte[] account;


    /**
     * 预验证指令
     * 手机向锁发送指令数据：账号（7byte）+账户类型（1byte）
     * 账户类型3种，00——管理员账户、01——用户、02——租户
     * 指令0Xa1
     */
    public static String prospectiveValidation(String id, int type) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        // 账号(7字节)
        account = HexUtil.hexStringToBytes(id);
        byteBuffer.put(account);

        byte[] accountType = new byte[0];
        if (type == 0) {
            accountType = new byte[]{0x00};
        } else if (type == 1) {
            accountType = new byte[]{0x01};
        } else if (type == 2) {
            accountType = new byte[]{0x02};
        }
        byteBuffer.put(accountType);
        String dataHexStr = EncryptUtils.bytesToHexString(byteBuffer.array());
        KLog.e(TAG, "预验证指令: " + BluetoothEncryptUtils.encryptHexStr((byte) 0xa1, dataHexStr));
        return BluetoothEncryptUtils.encryptHexStr((byte) 0xa1, dataHexStr);
    }

    /**
     * 预验证指令返回数据
     * 锁号4字节+密文a（8字节：des（KeyDev，账号（低4byte）+随机数4字节））
     * KeyDev	: 	 初始密钥 锁号（4字节）+0x64 0x65 0x76 0x65
     */
    public static ProspectiveValidationResult receiveProspectiveValidation(String value) {

        //打印解密信息并获取数据
        byte[] data = printDesData(value);

        ByteBuffer buffer = ByteBuffer.wrap(data);
        //数据前4字节 为锁号
        byte[] lockNo = new byte[4];

        buffer.get(lockNo);

        byte[] c = {0x64, 0x65, 0x76, 0x65};
        byte[] keyDev = addBytes(lockNo, c);

        KLog.e(TAG, "keyDev" + bcd2Str(keyDev));

        byte[] dst = new byte[data.length - 4];
        //除去锁号就是密文
        buffer.get(dst);
        byte[] n = new byte[0];
        try {
            n = EncryptUtils.decrypt(dst, keyDev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] randomNumber = new byte[]{n[4], n[5], n[6], n[7]};
        KLog.e(TAG, "当前连接设备的锁号 : " + bcd2Str(lockNo) + "随机数 : " + EncryptUtils.bytesToInt(randomNumber));

        return new ProspectiveValidationResult(true, lockNo, randomNumber, keyDev);
    }

    /**
     * 验证指令
     */
    public static String verification(byte[] keyAdmin, byte[] lockNo, byte[] randomNumber) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byte[] mac = EncryptUtils.encrypt(addBytes(lockNo, randomNumber), keyAdmin);
        byteBuffer.put(mac);
        String dataHexStr = EncryptUtils.bytesToHexString(byteBuffer.array());
        String hexStr = BluetoothEncryptUtils.encryptHexStr((byte) 0xa2, dataHexStr);
        KLog.e(TAG, "验证指令: " + hexStr);
        return hexStr;
    }

    /**
     * 验证返回
     *
     * @param value 蓝牙返回值
     */
    public static boolean validateReturn(String value) throws Exception {
        //打印解密信息并获取数据
        byte[] data = printDesData(value);

        ByteBuffer buffer = ByteBuffer.wrap(data);
        byte[] reult = new byte[1];
        //数据第1字节为结果
        buffer.get(reult);
        KLog.e(TAG, "操作结果 : " + EncryptUtils.bytesToHexString(reult));

        byte[] time = new byte[7];
        //接着7字节为锁时钟
        buffer.get(time);
        KLog.e(TAG, "锁时钟 : " + EncryptUtils.bytesToDate7(time));

        byte[] battery = new byte[1];
        buffer.get(battery);
        KLog.e(TAG, "电量 : " + BleUtils.byteToString(battery));
        Constants.battery =BleUtils.byteToString(battery);


        if (EncryptUtils.bytesToHexString(reult).equals("00")) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 指令1        帐号(7byte)
     * {0x00, 0x00, 0x00, 0x00, (byte) 0x9A, 0x00, 0x00};
     *
     * @param KeyAdmin/keyUser 用户自设的密钥
     *                         { 0x12, 0x34, 0x56, 0x78, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF }
     * @param passwordStr      录入的密码
     */
    public static String ins1(String id, byte[] KeyAdmin, String passwordStr) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(15);
        byte[] account = EncryptUtils.hexStringToBytes(id);

        // 账号(7字节)
        byteBuffer.put(account);

//        byte[] password =  { 0x30, 0x30, 0x30, 0x30, 0x30,0x30, 0x30, 0x31 };

        byte[] password = passwordStr.getBytes();
        KLog.e(TAG, "ins1: passWord:" + Arrays.toString(password));

        //密文管理员密钥（8byte)
        byte[] array1 = EncryptUtils.encrypt(password, KeyAdmin);
        byteBuffer.put(array1);

        String dataHexStr = EncryptUtils.bytesToHexString(byteBuffer.array());
        String hexStr = BluetoothEncryptUtils.encryptHexStr((byte) 0x01, dataHexStr);
        KLog.e(TAG, "账号、KeyAdmin 录入指令---绑定指令: " + hexStr);
        return hexStr;
    }

    /**
     * 指令1返回值
     *
     * @param value
     * @throws Exception
     */
    public static boolean ins1Return(String value) throws Exception {
        //打印解密信息并获取数据
        byte[] data = printDesData(value);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        //第一个字节为应答结果
        byte[] lockNo = new byte[1];
        buffer.get(lockNo);
        byte reply = lockNo[0];//和0x00 0x01比较

        if (reply == 0x00) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 指令6  解绑
     *
     * @param keyAdmin/keyuser { 0x12, 0x34, 0x56, 0x78, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF }
     */
    public static String ins6(byte[] keyAdmin) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(12);
        byteBuffer.put(new byte[]{0x12, 0x34, 0x56, 0x78});

        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(new byte[]{0x12, 0x34, 0x56, 0x78});
        buffer.put(new byte[]{0x12, 0x34, 0x56, 0x78});
        byte[] mac = EncryptUtils.encrypt(buffer.array(), keyAdmin);
        byteBuffer.put(mac);

        //数据进行加密生成通信消息
        String dataHexStr = EncryptUtils.bytesToHexString(byteBuffer.array());
        String hexStr = BluetoothEncryptUtils.encryptHexStr((byte) 0x07, dataHexStr);
        KLog.e(TAG, "解绑指令: " + hexStr);
        return hexStr;
    }

    /**
     * 指令6返回值
     *
     * @param value
     * @throws Exception
     */
    public static boolean ins6Return(String value) throws Exception {
        //打印解密信息并获取数据
        byte[] data = printDesData(value);

        ByteBuffer buffer = ByteBuffer.wrap(data);
        //第一个字节为应答结果
        byte[] resultArray = new byte[1];
        buffer.get(resultArray);
        byte result = resultArray[0];//应答结果

        if (result == 0x00) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * @param keyAdmin
     * @param keyNumber
     * @param order  组内编号
     * @param userOrder 用户组编号
     * @param orderNumber 顺序编号
     * @return
     */
    public static String ins3(byte[] keyAdmin,byte [] keyNumber, byte order, byte userOrder,
                            byte orderNumber) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(14);
        //保留
        byte [] remain = {0x00,0x00,0x00};
        byteBuffer.put(remain);
        byteBuffer.put(order);
        byteBuffer.put(userOrder);
        byteBuffer.put(orderNumber);// 和下面的0x01一起变

        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(remain);
        buffer.put(order);
        buffer.put(userOrder);
        buffer.put(orderNumber);
        buffer.put(new byte[] { 0x5A, (byte) 0xA5 });

        byte[] mac = EncryptUtils.encrypt(buffer.array(), keyAdmin);
//        byte[] mac = EncryptUtils.encrypt(keyAdmin, buffer.array());
        byteBuffer.put(mac);

        String dataHexStr = EncryptUtils.bytesToHexString(byteBuffer.array());

        // 数据进行加密生成通信消息
        String hexStr = BluetoothEncryptUtils.encryptHexStr((byte) 0x03,
                dataHexStr);
        KLog.e(TAG, "密码指纹设置: "+hexStr);
        return hexStr;
    }

    /**
     * 指令3  指纹、密码录入指令 返回值
     *
     * @param value
     * @throws Exception
     */
    public static boolean ins3Return(String value) throws Exception {
        //打印解密信息并获取数据
        byte[] data = printDesData(value);

        ByteBuffer buffer = ByteBuffer.wrap(data);
        //第一个字节为应答结果
        byte[] resultArray = new byte[1];
        buffer.get(resultArray);
        byte result = resultArray[0];//应答结果

        //第二个字节为顺序编号
        byte[] orderArray = new byte[1];
        buffer.get(orderArray);
        byte order = orderArray[0];//顺序编号

        if (result == 0x00) {
            return true;
        } else {
            return false;
        }

    }


    /**
     * 指令7  写入时间
     *
     * @param lockNumber     锁号4字节  { 0x12, 0x34, 0x56, 0x78 }
     * @param startTimeBytes 开始时间
     *                       byte[] startTimeBytes = EncryptUtils.calendarToBytes4(calendar);
     * @param endTimeBytes   结束时间
     * @param keyAdmin
     */
    public static String ins7(byte[] lockNumber, byte[] startTimeBytes, byte[] endTimeBytes, byte[] keyAdmin) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.put(lockNumber);
        byteBuffer.put(startTimeBytes);
        byteBuffer.put(endTimeBytes);

        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(lockNumber);
        buffer.put(endTimeBytes);
        //计算加密mac值
        byte[] mac = EncryptUtils.encrypt(buffer.array(), keyAdmin);
        ByteBuffer macBuffer = ByteBuffer.wrap(mac);
        byte[] macArr = new byte[4];
        macBuffer.get(macArr, 0, 4);
        byteBuffer.put(macArr);

        String dataHexStr = EncryptUtils.bytesToHexString(byteBuffer.array());

        // 数据进行加密生成通信消息
        String hexStr = BluetoothEncryptUtils.encryptHexStr((byte) 0x08,
                dataHexStr);
        KLog.e(TAG, "写入时间 ins7: " + hexStr);

        return hexStr;
    }

    /**
     * 指令7返回值
     *
     * @param value
     * @throws Exception
     */
    public static boolean ins7Return(String value) throws Exception {
        //打印解密信息并获取数据
        byte[] data = printDesData(value);

        ByteBuffer buffer = ByteBuffer.wrap(data);
        //第一个字节为应答结果
        byte[] resultArray = new byte[1];
        buffer.get(resultArray);
        byte result = resultArray[0];//应答结果

        if (result == 0x00) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 生成 KeyUser
     *
     * @param keyNumber
     * @param keyAdmin
     * @return
     */
    public static String keyUser(String keyNumber, String keyAdmin) {

        byte[] keyNumberByte = EncryptUtils.hexStringToBytes(keyNumber);
        System.out.println("keyNumberByte: " + Arrays.toString(keyNumberByte));
        byte[] keyAdminByte = keyAdmin.getBytes();

        byte[] d = {(byte) 0xa1, (byte) 0xa2, (byte) 0xa3, (byte) 0xa4};
        byte[] f = addBytes(keyNumberByte, d); ///锁号（4字节）+0xa1 0xa2 0xa3 0xa4)
        System.out.println("keyAdminByte: " + Arrays.toString(keyAdminByte));
        byte[] keyUser = EncryptUtils.encrypt(f, keyAdminByte);
        String hexString = EncryptUtils.bytesToHexString(keyUser);
        System.out.println("keyUser: " + EncryptUtils.bytesToHexString(keyUser));

        return hexString;
    }


    public static byte[] addBytes(byte[] data1, byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;
    }


    /**
     * @功能: BCD码转为10进制串(阿拉伯数据)
     * @参数: BCD码
     * @结果: 10进制串
     */
    public static String bcd2Str(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
                .toString().substring(1) : temp.toString();
    }

    /**
     * @功能: 10进制串转为BCD码
     * @参数: 10进制串
     * @结果: BCD码
     */
    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }
        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }
        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;
        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }
            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    /**
     * 指令9
     *
     * @param keyAdmin
     * @param keyNumber 锁号
     * @param password  密码
     * @param userType  账户类型
     * @param userOrder 分组编号
     */
    public static String ins9(byte[] keyAdmin, byte[] keyNumber, byte[] password, byte userType, byte userOrder) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(14);
        byteBuffer.put(keyNumber);
        byteBuffer.put(password);
        byteBuffer.put(userType);
        byteBuffer.put(userOrder);

        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(keyNumber);
        buffer.put(password);

        byte[] mac = EncryptUtils.encrypt(buffer.array(), keyAdmin);

        ByteBuffer macBuffer = ByteBuffer.wrap(mac);
        byte[] macArr = new byte[4];
        macBuffer.get(macArr);
        byteBuffer.put(macArr);

        String dataHexStr = EncryptUtils.bytesToHexString(byteBuffer.array());

        //数据进行加密生成通信消息
        String hexStr = BluetoothEncryptUtils.encryptHexStr((byte) 0x0A, dataHexStr);
        KLog.e(TAG, "密码设置【0x0A】 : " + hexStr);
        return hexStr;
    }

    /**
     * 指令9返回值
     *
     * @param value
     * @throws Exception
     */
    public static boolean ins9Return(String value) throws Exception {
        //打印解密信息并获取数据
        byte[] data = printDesData(value);

        ByteBuffer buffer = ByteBuffer.wrap(data);
        //第一个字节为应答结果
        byte[] resultArray = new byte[1];
        buffer.get(resultArray);
        byte result = resultArray[0];//应答结果

        if (result == 0x00) {
            return true;
        } else {
            return false;
        }

    }

    public static byte[] hex2byte(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) ((((int) b[i]) & 0xff) % 9);
        }
        return b;
    }

    /**
     * 指令10  删除钥匙（长密码）
     * @param keyNumber 锁号
     * @param orderNumber 组编号
     * @param type 账户类型
     */
    public static String ins10(byte orderNumber,byte type,byte[] keyNumber,byte[] keyAdmin) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(6);
        byteBuffer.put(orderNumber);
        byteBuffer.put(type);

        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(keyNumber);
        buffer.put(keyNumber);

        byte[] mac = EncryptUtils.encrypt(buffer.array(), keyAdmin);
        ByteBuffer macBuffer = ByteBuffer.wrap(mac);
        byte[] macArr = new byte[4];
        macBuffer.get(macArr);
        byteBuffer.put(macArr);
        String dataHexStr = EncryptUtils.bytesToHexString(byteBuffer.array());

        // 数据进行加密生成通信消息
        String hexStr = BluetoothEncryptUtils.encryptHexStr((byte) 0x0D,
                dataHexStr);
        KLog.e(TAG, "ins10: "+hexStr);
        return hexStr;
    }

    /**
     * 删除钥匙返回
     *
     * @param value
     * @throws Exception
     */
    public static boolean ins10Return(String value) throws Exception {
        // 打印解密信息并获取数据
        byte[] data = printDesData(value);

        ByteBuffer buffer = ByteBuffer.wrap(data);
        // 第一个字节为应答结果
        byte[] resultArray = new byte[1];
        buffer.get(resultArray);
        byte result = resultArray[0];// 应答结果
        if (result == 0x00) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 接收锁返回值  打印解密信息
     */
    private static byte[] printDesData(String value) {
        byte[] array = EncryptUtils.hexStringToBytes(value);
        ByteBuffer byteBuffer = ByteBuffer.wrap(array);
        //帧头
        byte header = byteBuffer.get();
        //长度
        byte len = byteBuffer.get();
        //指令
        byte cmd = byteBuffer.get();
        byte[] data = new byte[len - 1 - 1 - 1 - 1];
        //数据
        byteBuffer.get(data, 0, len - 1 - 1 - 1 - 1);
        byte xor = byteBuffer.get();
        KLog.e(TAG, "printDesData: "+ Arrays.toString(data));
        return data;
    }

    public static String byteToString(byte[] b) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            sBuffer.append(b[i] + "");
        }
        return sBuffer.toString();
    }

    /**
     * 指令10
     * @param orderNumber 组编号
     * @param figureNumber 指纹编号
     * @param type 账户类型
     * @param keyNumber 锁号
     */
    public static String ins11(byte orderNumber, byte figureNumber, byte type, byte[] keyNumber, byte[] keyAdmin) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(7);
        byteBuffer.put(orderNumber);
        byteBuffer.put(figureNumber);
        byteBuffer.put(type);

        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(keyNumber);
        buffer.put(keyNumber);

        byte[] mac = EncryptUtils.encrypt(buffer.array(), keyAdmin);
        ByteBuffer macBuffer = ByteBuffer.wrap(mac);
        byte[] macArr = new byte[4];
        macBuffer.get(macArr);
        byteBuffer.put(macArr);
        String dataHexStr = EncryptUtils.bytesToHexString(byteBuffer.array());

        // 数据进行加密生成通信消息
        String hexStr = BluetoothEncryptUtils.encryptHexStr((byte) 0x0C,
                dataHexStr);
        KLog.e(TAG, "删除指纹: "+hexStr );
        return hexStr;
    }

    /**
     * 设置密码类型
     * @param keyAdmin
     * @param mark
     * @param type
     */
    public static String ins4(byte[] keyAdmin, byte mark, byte type) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(12);
        byte[] time = EncryptUtils.dateToBytes6(new Date());
        byteBuffer.put(time);
        // 防打扰标志
        byteBuffer.put(mark);
        // 验证类型
        byteBuffer.put(type);

        // des算法算mac值
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(time);
        buffer.put(mark);
        buffer.put(type);

        byte[] mac = EncryptUtils.encrypt(buffer.array(),keyAdmin);
        ByteBuffer macBuffer = ByteBuffer.wrap(mac);
        byte[] macArr = new byte[4];
        macBuffer.get(macArr);
        byteBuffer.put(macArr);
        String dataHexStr = EncryptUtils.bytesToHexString(byteBuffer.array());

        // 数据进行加密生成通信消息
        String hexStr = BluetoothEncryptUtils.encryptHexStr((byte) 0x05, dataHexStr);
        KLog.e(TAG, "设置密码类型: "+hexStr );
        return hexStr;
    }
}
