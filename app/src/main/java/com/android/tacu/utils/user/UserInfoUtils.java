package com.android.tacu.utils.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.android.tacu.api.Constant;
import com.android.tacu.base.MyApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 这里存放用户的个人信息
 * Created by jiazhen on 2017/3/31.
 */

public class UserInfoUtils {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private static volatile UserInfoUtils instance;
    private static String SP_FILE_NAME = "tacu";

    public static UserInfoUtils getInstance() {
        if (instance == null) {
            synchronized (UserInfoUtils.class) {
                if (instance == null) {
                    instance = new UserInfoUtils(SP_FILE_NAME);
                }
            }
        }
        return instance;
    }

    private UserInfoUtils(String file) {
        sp = MyApplication.getInstance().getSharedPreferences(file, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    /**
     * desc:保存对象
     *
     * @param key
     * @param obj 要保存的对象，只能保存实现了serializable的对象
     *            modified:
     */
    public void saveObject(Object obj, String key) {
        try {
            // 保存对象
            //先将序列化结果写到byte缓存中，其实就分配一个内存空间
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            //将对象序列化写入byte缓存
            os.writeObject(obj);
            //将序列化的数据转为16进制保存
            String bytesToHexString = bytesToHexString(bos.toByteArray());
            //保存该16进制数组
            editor.putString(key, bytesToHexString);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("", "保存obj失败");
        }
    }

    /**
     * desc:将数组转为16进制
     *
     * @param bArray
     * @return modified:
     */
    public String bytesToHexString(byte[] bArray) {
        if (bArray == null) {
            return null;
        }
        if (bArray.length == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * desc:获取保存的Object对象
     *
     * @return modified:
     */
    public Object readObject(String key) {
        try {
            if (sp.contains(key)) {
                String string = sp.getString(key, "");
                if (TextUtils.isEmpty(string)) {
                    return null;
                } else {
                    //将16进制的数据转为数组，准备反序列化
                    byte[] stringToBytes = StringToBytes(string);
                    ByteArrayInputStream bis = new ByteArrayInputStream(stringToBytes);
                    ObjectInputStream is = new ObjectInputStream(bis);
                    //返回反序列化得到的对象
                    Object readObject = is.readObject();
                    return readObject;
                }
            }
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //所有异常返回null
        return null;
    }

    /**
     * desc:将16进制的数据转为数组
     * <p>创建人：聂旭阳 , 2014-5-25 上午11:08:33</p>
     *
     * @param data
     * @return modified:
     */
    public static byte[] StringToBytes(String data) {
        String hexString = data.toUpperCase().trim();
        if (hexString.length() % 2 != 0) {
            return null;
        }
        byte[] retData = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i++) {
            int int_ch;  // 两位16进制数转化后的10进制数
            char hex_char1 = hexString.charAt(i); ////两位16进制数中的第一位(高位*16)
            int int_ch3;
            if (hex_char1 >= '0' && hex_char1 <= '9')
                int_ch3 = (hex_char1 - 48) * 16;   //// 0 的Ascll - 48
            else if (hex_char1 >= 'A' && hex_char1 <= 'F')
                int_ch3 = (hex_char1 - 55) * 16; //// A 的Ascll - 65
            else
                return null;
            i++;
            char hex_char2 = hexString.charAt(i); ///两位16进制数中的第二位(低位)
            int int_ch4;
            if (hex_char2 >= '0' && hex_char2 <= '9')
                int_ch4 = (hex_char2 - 48); //// 0 的Ascll - 48
            else if (hex_char2 >= 'A' && hex_char2 <= 'F')
                int_ch4 = hex_char2 - 55; //// A 的Ascll - 65
            else
                return null;
            int_ch = int_ch3 + int_ch4;
            retData[i / 2] = (byte) int_ch;//将转化后的数放入Byte里
        }
        return retData;
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public Object get(String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param key
     * @return
     */
    public boolean contains(String key) {
        return sp.contains(key);
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param object
     */
    public void put(String key, Object object) {
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException
                    | InvocationTargetException
                    | IllegalAccessException e) {
            }
            editor.commit();
        }
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }

    public void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

    /*-----------项目使用相关----------*/

    /**
     * 是否登录
     * 判断token 和 uid
     */
    public boolean getLogin() {
        if (!TextUtils.isEmpty(getToken()) && getUserUid() != 0) {
            return true;
        }
        return false;
    }

    /**
     * @param uid uid
     */
    public void setUserUid(Integer uid) {
        if (uid == null)
            uid = 0;
        editor.putInt("uid", uid);
        editor.commit();
    }

    public int getUserUid() {
        return sp.getInt("uid", 0);
    }

    /**
     * @param token token
     */
    public void setToken(String token) {
        editor.putString("token", token);
        editor.commit();
    }

    public String getToken() {
        return sp.getString("token", "");
    }

    /**
     * first是否第一次运行软件
     *
     * @param first
     */
    public void setFirst(boolean first) {
        editor.putBoolean("first", first);
        editor.commit();
    }

    public boolean getFirst() {
        return sp.getBoolean("first", true);
    }

    /**
     * 切换语言
     */
    public void setLanguage(String language) {
        editor.putString("language_3", language);
        editor.commit();
    }

    public String getLanguage() {
        return sp.getString("language_3", Constant.ZH_TW);
    }

    /**
     * 切换法币汇率
     */
    public void setConvertMoney(String convertMoney) {
        editor.putString("convertMoney", convertMoney);
        editor.commit();
    }

    public String getConvertMoney() {
        return sp.getString("convertMoney", Constant.CNY);
    }

    /**
     * 是否设置交易密码
     *
     * @param isValidatePass
     */
    public void setValidatePass(boolean isValidatePass) {
        editor.putBoolean("isValidatePass", isValidatePass);
        editor.commit();
    }

    public boolean getValidatePass() {
        return sp.getBoolean("isValidatePass", false);
    }

    /**
     * 是否设置GA
     *
     * @param status
     */
    public void setGaStatus(String status) {
        editor.putString("status", status);
        editor.commit();
    }

    public String getGaStatus() {
        return sp.getString("status", "");
    }

    /**
     * 是否每次输入交易密码
     *
     * @param isVisibility
     */
    public void setPwdVisibility(boolean isVisibility) {
        editor.putBoolean("isVisibility", isVisibility);
        editor.commit();
    }

    public boolean getPwdVisibility() {
        return sp.getBoolean("isVisibility", false);
    }

    /**
     * @param account 隐藏过中间部分的账号
     */
    public void setAccount(String account) {
        editor.putString("account", account);
        editor.commit();
    }

    public String getAccount() {
        return sp.getString("account", "");
    }

    /**
     * @param phone 手机号
     */
    public void setPhone(String phone) {
        editor.putString("phone", phone);
        editor.commit();
    }

    public String getPhone() {
        return sp.getString("phone", "");
    }

    /**
     * 认证等级 (kyc1 kyc2 kyc3)
     */
    public void setAuth(Integer auth) {
        if (auth == null) {
            auth = 0;
        }
        editor.putInt("Auth", auth);
        editor.commit();
    }

    public int getAuth() {
        return sp.getInt("Auth", 0);
    }

    /**
     * 用户认证状态 (已认证 未通过 之类的)
     *
     * @param isAuthSenior
     */
    public void setIsAuthSenior(Integer isAuthSenior) {
        if (isAuthSenior == null)
            isAuthSenior = 0;
        editor.putInt("isAuthSenior", isAuthSenior);
        editor.commit();
    }

    public int getIsAuthSenior() {
        return sp.getInt("isAuthSenior", 0);
    }

    /**
     * @param isPhone 手机绑定状态
     */
    public void setPhoneStatus(boolean isPhone) {
        editor.putBoolean("isPhone", isPhone);
        editor.commit();
    }

    public boolean getPhoneStatus() {
        return sp.getBoolean("isPhone", false);
    }

    /**
     * @param email 邮箱
     */
    public void setEmail(String email) {
        editor.putString("email", email);
        editor.commit();
    }

    public String getEmail() {
        return sp.getString("email", "");
    }

    /**
     * @param isEmail 邮箱绑定状态
     */
    public void setEmailStatus(boolean isEmail) {
        editor.putBoolean("isEmail", isEmail);
        editor.commit();
    }

    public boolean getEmailStatus() {
        return sp.getBoolean("isEmail", false);
    }

    /**
     * @param code code
     */
    public void saveOldCode(String code) {
        editor.putString("code", code);
        editor.commit();
    }

    public String getSaveCode() {
        return sp.getString("code", "");
    }

    /**
     * @param oldAccount code
     */
    public void saveOldAccount(String oldAccount) {
        editor.putString("oldAccount", oldAccount);
        editor.commit();
    }

    public String getSaveAccount() {
        return sp.getString("oldAccount", "");
    }

    /**
     * 是否隐藏资产
     */
    public void setAssetShowStatus(boolean isShow) {
        editor.putBoolean("assetShowStatus", isShow);
        editor.commit();
    }

    public boolean getAssetShowStatus() {
        return sp.getBoolean("assetShowStatus", true);
    }

    /**
     * 登录成功后直接保存用户填写的
     * 退出登录不清理
     *
     * @param accountString
     */
    public void setAccountString(String accountString) {
        editor.putString("accountString", accountString);
        editor.commit();
    }

    public String getAccountString() {
        return sp.getString("accountString", "");
    }

    /**
     * 昵称
     */
    public void setNickName(String nickName) {
        editor.putString("nickName", nickName);
        editor.commit();
    }

    public String getNickName() {
        return sp.getString("nickName", "");
    }

    /**
     * 头像
     */
    public void setHeadImg(String headImg) {
        editor.putString("headImg", headImg);
        editor.commit();
    }

    public String getHeadImg() {
        return sp.getString("headImg", "");
    }

    /**
     * 是否绑定支付信息
     */
    public void setIsPayInfo(boolean isPayInfo) {
        editor.putBoolean("isPayInfo", isPayInfo);
        editor.commit();
    }

    public boolean getIsPayInfo() {
        return sp.getBoolean("isPayInfo", false);
    }

    /**
     * 用户KYC的真实姓名
     */
    public void setKYCName(String kycName) {
        editor.putString("kycName", kycName);
        editor.commit();
    }

    public String getKYCName() {
        return sp.getString("kycName", "");
    }

    /**
     * 是否是会员身份
     * 0不是vip 1月度会员(30天) 2年度会员(12月) 3连续包年
     */
    public void setVip(Integer vipStatus) {
        if (vipStatus == null) {
            vipStatus = 0;
        }
        editor.putInt("vipStatus", vipStatus);
        editor.commit();
    }

    public int getVip() {
        return sp.getInt("vipStatus", 0);
    }

    /**
     * 申请商户状态 0未申请(默认) 1待审核 2审核成功 3审核失败
     *
     */
    public void setApplyMerchantStatus(Integer applyMerchantStatus) {
        if (applyMerchantStatus == null) {
            applyMerchantStatus = 0;
        }
        editor.putInt("applyMerchantStatus", applyMerchantStatus);
        editor.commit();
    }

    public int getApplyMerchantStatus() {
        return sp.getInt("applyMerchantStatus", 0);
    }

    /**
     * //申请认证商户状态 0未申请(默认) 1待审核 2审核成功 3审核失败
     *
     */
    public void setApplyAuthMerchantStatus(Integer applyAuthMerchantStatus) {
        if (applyAuthMerchantStatus == null) {
            applyAuthMerchantStatus = 0;
        }
        editor.putInt("applyAuthMerchantStatus", applyAuthMerchantStatus);
        editor.commit();
    }

    public int getApplyAuthMerchantStatus() {
        return sp.getInt("applyAuthMerchantStatus", 0);
    }
}
