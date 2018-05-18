package com.idc9000.smartlock.utils.bleutils;

import android.util.Log;

import java.nio.ByteBuffer;

public class BluetoothEncryptUtils {
    private static final String TAG = "BluetoothEncryptUtils";
    /**
     * 解密蓝牙消息十六进制数据
     * @param packHexStr
     * @return
     */
    public static Pair<Byte, String> decryptHexStr(String packHexStr) {
//        String value = "bb10a112345678d66b533874b1160304";
        byte[] array = EncryptUtils.hexStringToBytes(packHexStr);
        ByteBuffer byteBuffer = ByteBuffer.wrap(array);
        byte header = byteBuffer.get();
        byte len = byteBuffer.get();
        byte cmd = byteBuffer.get();
        byte[] data = new byte[len - 1 - 1 - 1 - 1];
        byteBuffer.get(data, 0, len - 1 - 1 - 1 - 1);
        byte xor = byteBuffer.get();

//        System.out.println("-------------DECRYPT-START--------------");
//        System.out.println("解密头 : " + EncryptUtils.bytesToHexString(new byte[] {header}));
//        System.out.println("解密命令 : " + EncryptUtils.bytesToHexString(new byte[] {cmd}));
//        System.out.println("解密长度 : " + EncryptUtils.bytesToHexString(new byte[] {len}));
//        System.out.println("解密数据包 : " + EncryptUtils.bytesToHexString(data));
//        System.out.println("解密XOR : " + EncryptUtils.bytesToHexString(new byte[] {xor}));
//
//        System.out.println("解密十六进制 : " + EncryptUtils.bytesToHexString(byteBuffer.array()));
//        System.out.println("比较十六进制 : " + "bb10a112345678d66b533874b1160304");
        System.out.println("-------------DECRYPT-END--------------");
        Pair<Byte, String> pair = new Pair<Byte, String>(cmd, EncryptUtils.bytesToHexString(data));
        return pair;
    }

    /**
     * 通过ByteBuffer加密十六进制数据
     * @param cmd
     * @param byteBuffer
     * @return
     */
    public static String encryptHexStr(byte cmd, ByteBuffer byteBuffer) {
        return encryptHexStr(cmd, byteBuffer.array());
    }

    /**
     * 通过byte数组加密十六进制数据
     * @param cmd
     * @param dataBytes
     * @return
     */
    public static String encryptHexStr(byte cmd, byte[] dataBytes) {
        String dataHexStr = EncryptUtils.bytesToHexString(dataBytes);
        return encryptHexStr(cmd, dataHexStr);
    }
    
    /**
     * 通过十六进制数据加密
     * @param cmd
     * @param dataHexStr
     * @return
     */
    public static String encryptHexStr(byte cmd, String dataHexStr) {
        byte[] data = EncryptUtils.hexStringToBytes(dataHexStr);
        if (data == null) data = new byte[0];
        byte len = (byte) (1 + 1 + 1 + data.length + 1);
        byte header = (byte) 0xaa;
//        byte cmd = (byte) 0x01;
        ByteBuffer byteBuffer = ByteBuffer.allocate(len);
        
        byteBuffer.put(header);
        byteBuffer.put(len);
        byteBuffer.put(cmd);
        byteBuffer.put(data);
        byte xor = EncryptUtils.XOR(byteBuffer.array(), byteBuffer.array().length);
        byteBuffer.put(xor);

//        Log.e(TAG, "-------------ENCRYPT-START--------------");
//        Log.e(TAG, "encryptHexStr: "+"加密头 : " + EncryptUtils.bytesToHexString(new byte[] {header}));
//        Log.e(TAG, "encryptHexStr: "+"加密命令 : " + EncryptUtils.bytesToHexString(new byte[] {cmd}));
//        Log.e(TAG, "encryptHexStr: "+"加密长度 : " + EncryptUtils.bytesToHexString(new byte[] {len}));
//        Log.e(TAG, "encryptHexStr: "+"加密数据包 : " + EncryptUtils.bytesToHexString(data));
//        Log.e(TAG, "encryptHexStr: "+"加密XOR : " + EncryptUtils.bytesToHexString(new byte[] {xor}));
//
//
        String result = EncryptUtils.bytesToHexString(byteBuffer.array());
//        Log.e(TAG, "encryptHexStr: "+"加密十六进制 : " + result);
//        Log.e(TAG, "-------------ENCRYPT-END--------------");
//
        return result;
    }
}
