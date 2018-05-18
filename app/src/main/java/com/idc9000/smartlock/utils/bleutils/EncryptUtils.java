package com.idc9000.smartlock.utils.bleutils;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class EncryptUtils {

    /**
     * XOR 异或算法
     * @param buf
     * @param length
     * @return
     */
    public static byte XOR(byte[] buf, int length) {
        byte j, value = 0;
        for (j = 0; j < length; j++) {
            value ^= buf[j];
        }
        return value;
    }
    
    /**
     * 字节数组转INT
     * @param buffer
     * @return
     */
    public static int bytesToInt(byte[] buffer) {
        return  buffer[3] & 0xFF |
                (buffer[2] & 0xFF) << 8 |
                (buffer[1] & 0xFF) << 16 |
                (buffer[0] & 0xFF) << 24;
    }
    
    /**
     * INT转字节数组
     * @param value
     * @return
     */
    public static byte[] intToBytes(int value) {
        return new byte[] {
            (byte) ((value >> 24) & 0xFF),
            (byte) ((value >> 16) & 0xFF),
            (byte) ((value >> 8) & 0xFF),
            (byte) (value & 0xFF)
        };
    }
    
    /**
     * LONG转字节数组
     * @param values
     * @return
     */
    public static byte[] longToBytes(long values) {
        byte[] buffer = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = 64 - (i + 1) * 8;
            buffer[i] = (byte) ((values >> offset) & 0xff);
        }
        return buffer;
    }

    /**
     * 字节数组转LONG
     * @param buffer
     * @return
     */
    public static long bytesToLong(byte[] buffer) {
        long values = 0;
        for (int i = 0; i < 8; i++) {
            values <<= 8;
            values |= (buffer[i] & 0xff);
        }
        return values;
    }
    
    /**
     * 日期转字节数组(7个字节)
     * @return
     */
    public static byte[] dateToBytes7(Date date) {
        /*int year = calendar.get(Calendar.YEAR);
        String yearString = String.valueOf(year);
        int yearFir = Integer.parseInt(yearString.substring(0,2));
        int yearLast = Integer.parseInt(yearString.substring(2));
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        ByteBuffer byteBuffer = ByteBuffer.allocate(7);
        byteBuffer.put(new byte[] {(byte) yearFir,(byte) yearLast,(byte) month,(byte) day,(byte) hour, (byte) minute,0x00});
        return byteBuffer.array();*/
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = simpleDateFormat.format(date);
        return str2Bcd(dateString);
    }
    /**
     * 字节数组转日期(7个字节)
     * @param bytes
     * @return
     * @throws ParseException 
     */
    public static Date bytesToDate7(byte[] bytes) throws ParseException {
        /*ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        int yearFir = byteBuffer.get();
        int yearLast = byteBuffer.get();
        int month = byteBuffer.get();
        int day = byteBuffer.get();
        int hour = byteBuffer.get();
        int minute = byteBuffer.get();
        int year = Integer.valueOf(String.valueOf(yearFir)+String.valueOf(yearLast));
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar;*/
        String dateString = bcd2Str(bytes);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = simpleDateFormat.parse(dateString);
        return date;
    }
    
    /**
     * 日期转字节数组(4位)
     * @return
     */
    public static byte[] dateToBytes4(Date date) {
        /*int year = calendar.get(Calendar.YEAR);
        String yearString = String.valueOf(year);
        int yearFir = Integer.parseInt(yearString.substring(0,2));
        int yearLast = Integer.parseInt(yearString.substring(2));
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        ByteBuffer byteBuffer = ByteBuffer.allocate(4   );
        byteBuffer.put(new byte[] {(byte) yearFir,(byte) yearLast,(byte) month,(byte) day});
        return byteBuffer.array();*/
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHH");
        String dateString = simpleDateFormat.format(date);
        return str2Bcd(dateString);
    }
    
    
    /**
     * 字节数组转日期(4位)
     * @param bytes
     * @return
     * @throws ParseException 
     */
    public static Date bytesToDate4(byte[] bytes) throws ParseException {
        /*ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        int yearFir = byteBuffer.get();
        int yearLast = byteBuffer.get();
        int month = byteBuffer.get();
        int day = byteBuffer.get();
        int year = Integer.valueOf(String.valueOf(yearFir)+String.valueOf(yearLast));
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, day);*/
        String dateString = bcd2Str(bytes);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = simpleDateFormat.parse(dateString);
        return date;
    }
    
    /**
     * 日期转字节数组(6位)
     * @return
     */
    public static byte[] dateToBytes6(Date date) {
        /*int year = calendar.get(Calendar.YEAR);
        String yearString = String.valueOf(year);
        int yearLast = Integer.parseInt(yearString.substring(2));
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        ByteBuffer byteBuffer = ByteBuffer.allocate(6);
        byteBuffer.put(new byte[] {(byte) yearLast,(byte) month,(byte) day,(byte) hour, (byte) minute,0x00});*/
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss");
        String dateString = simpleDateFormat.format(date);
        return str2Bcd(dateString);
    }
    /**
     * 字节数组转日期(6位)
     * @param bytes
     * @return
     * @throws ParseException 
     */
    public static Date bytesToDate6(byte[] bytes) throws ParseException {
        /*ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        int yearLast = byteBuffer.get();
        int month = byteBuffer.get();
        int day = byteBuffer.get();
        int hour = byteBuffer.get();
        int minute = byteBuffer.get();
        int year = Integer.valueOf("20"+String.valueOf(yearLast));
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);*/
        String dateString = bcd2Str(bytes);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss");
        Date date = simpleDateFormat.parse(dateString);
        return date;
    }
    /**
     * DES加密算法
     * @param dataBytes
     * @param keyBytes
     * @return
     */
    public static byte[] encrypt(byte[] dataBytes, byte[] keyBytes) {
        try {
         // DES算法要求有一个可信任的随机数源
            SecureRandom random = new SecureRandom();
            // 创建一个DESKeySpec对象
            DESKeySpec desKey = new DESKeySpec(keyBytes);
            // 创建一个密匙工厂
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // 将DESKeySpec对象转换成SecretKey对象
            SecretKey securekey = keyFactory.generateSecret(desKey);
            // Cipher对象实际完成解密操作
            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            // 现在，获取数据并加密
            // 正式执行加密操作
            return cipher.doFinal(dataBytes);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DES解密算法
     * @param dataBytes
     * @param keyBytes
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] dataBytes, byte[] keyBytes) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom random = new SecureRandom();
        // 创建一个DESKeySpec对象
        DESKeySpec desKey = new DESKeySpec(keyBytes);
        // 创建一个密匙工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // 将DESKeySpec对象转换成SecretKey对象
        SecretKey securekey = keyFactory.generateSecret(desKey);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);
        // 真正开始解密操作
        return cipher.doFinal(dataBytes);
    }

    /**
     * 字节数组转十六进制字符串
     * @param buf
     * @return
     */
    public static String bytesToHexString(byte[] buf) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (buf == null || buf.length <= 0) {
            return null;
        }
        for (int i = 0; i < buf.length; i++) {
            int v = buf[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }

    /**
     * 十六进制字符串转字节数组
     * @param hexString
     * @return
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    
    public static void main(String[] args) {
        byte [] a = hexStringToBytes("12345678");
        System.out.println(a);
    }
    
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
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
}
