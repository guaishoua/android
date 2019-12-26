package com.android.tacu.utils;

import android.text.TextUtils;
import android.util.Base64;

import com.android.tacu.BuildConfig;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * ================================
 *
 * @作者: 魏玉帅
 * @创建日期:2017/6/15
 * @版本 1.0
 * @描述: ================================
 */

public class Md5Utils {

    /**
     * MD5加密
     *
     * @param value
     * @return
     */
    public static String md5Encode(String value) {
        if (TextUtils.isEmpty(value)) {
            return "";
        }
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            byte[] result = digest.digest(value.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                int data = (b & 0xff);
                String str = Integer.toHexString(data);
                if (str.length() == 1) {
                    sb.append("0");
                }
                sb.append(str);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * BASE64 解密
     * encodeWord：加密后的文字/比如密码
     */
    public static String setDecrypt(String encodeWord) {
        if (TextUtils.isEmpty(encodeWord)) {
            return "";
        }
        String decode = "";
        try {
            decode = new String(Base64.decode(encodeWord, Base64.NO_WRAP), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decode;
    }

    public static final String encryptSmall(String s) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        try {
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;

            for (int i = 0; i < j; ++i) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 15];
                str[k++] = hexDigits[byte0 & 15];
            }

            return new String(str);
        } catch (Exception var10) {
            return null;
        }
    }

    public static final String encrypt(String s) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        try {
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;

            for (int i = 0; i < j; ++i) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 15];
                str[k++] = hexDigits[byte0 & 15];
            }

            return new String(str);
        } catch (Exception var10) {
            return null;
        }
    }

    /**
     * 加密登录密码
     *
     * @param pwd
     * @return
     */
    public static final String encryptPwd(String pwd) {
        String afterFormat = null;
        String afterEncrypt = null;

        try {
            afterFormat = pwd + BuildConfig.SALT_LOGIN_PWD;
            afterEncrypt = encrypt(afterFormat);
        } catch (Exception var4) {
        }

        return afterEncrypt;
    }

    /**
     * 加密交易密码
     *
     * @param pwd
     * @param uid
     * @return
     */
    public static final String encryptFdPwd(String pwd, int uid) {
        String afterFormat = null;
        String afterEncrypt = null;

        try {
            afterFormat = pwd + BuildConfig.SALT_TRADE_PWD + uid;
            afterEncrypt = encrypt(afterFormat);
        } catch (Exception var5) {
        }

        return afterEncrypt;
    }

    /**
     * OTC的加密 AES加密
     */
    public static String AESEncrypt(String words) {
        try {
            String sKey = "1234123412ABCDEF";//key
            String ivParameter = "ABCDEF1234123412";//偏移量

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            byte[] raw = sKey.getBytes("UTF-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes("UTF-8"));//使用CBC模式，需要一个向量iv，可增加加密算法的强度
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(words.getBytes("UTF-8"));
            //两种输入模式 1.Base64 2.Hex （这里采用Hex）
            //return Base64.encodeToString(encrypted, Base64.DEFAULT);
            return bytesToHexString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * byte数组转HexString
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }
}
