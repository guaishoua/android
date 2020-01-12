package com.android.tacu.utils.lock;


import android.text.TextUtils;

import com.android.tacu.db.DaoManager;
import com.android.tacu.db.model.LockNewModel;
import com.android.tacu.utils.user.UserInfoUtils;

/**
 * Created by jiazhen on 2018/8/28.
 */
public class LockUtils {

    private static UserInfoUtils spUtil = UserInfoUtils.getInstance();

    /**
     * 数据库添加账号密码
     */
    public static void addAccountAndPwd(String account, String pwd) {
        DaoManager.getLockNewModelUtils().insertOrReplaceLockNewModel(account, pwd);
    }

    /**
     * 添加手势的信息
     */
    public static void addGesture(String gesture) {
        if (spUtil.getLogin() && !TextUtils.isEmpty(spUtil.getAccountString())) {
            DaoManager.getLockNewModelUtils().updateLockNewModel(spUtil.getAccountString(), gesture, false);
        }
    }

    /**
     * 添加指纹的信息
     */
    public static void addFinger(boolean isFinger) {
        if (spUtil.getLogin() && !TextUtils.isEmpty(spUtil.getAccountString())) {
            DaoManager.getLockNewModelUtils().updateLockNewModel(spUtil.getAccountString(), "", isFinger);
        }
    }

    /**
     * 获取手势信息
     */
    public static String getGesture() {
        if (spUtil.getLogin() && !TextUtils.isEmpty(spUtil.getAccountString())) {
            return DaoManager.getLockNewModelUtils().getGestureString(spUtil.getAccountString());
        }
        return "";
    }

    /**
     * 获取指纹是否开启
     */
    public static boolean getIsFinger() {
        if (spUtil.getLogin() && !TextUtils.isEmpty(spUtil.getAccountString())) {
            return DaoManager.getLockNewModelUtils().getIsFinger(spUtil.getAccountString());
        }
        return false;
    }

    public static LockNewModel getLockNewModel() {
        if (spUtil.getLogin() && !TextUtils.isEmpty(spUtil.getAccountString())) {
            return DaoManager.getLockNewModelUtils().getLockNewModel(spUtil.getAccountString());
        }
        return null;
    }
}
