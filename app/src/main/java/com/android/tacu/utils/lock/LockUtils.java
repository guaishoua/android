package com.android.tacu.utils.lock;

import android.text.TextUtils;

import com.android.tacu.db.DaoManager;
import com.android.tacu.db.model.LockModel;
import com.android.tacu.module.lock.model.LockSettingModel;
import com.android.tacu.utils.user.UserInfoUtils;
import com.google.gson.Gson;

/**
 * Created by jiazhen on 2018/8/28.
 */
public class LockUtils {

    //手势密码和指纹密码唤起的时间间隔（10秒）
    public static final int LOCK_TIME = 10 * 1000;
    //保存Activity最近一次调用onPause()的系统时间
    private static long lockTime = 0;

    private static UserInfoUtils spUtil = UserInfoUtils.getInstance();
    private static Gson gson = new Gson();

    //有手势密码和指纹密码的信息
    private static LockSettingModel lockSetting;

    public static void init() {
        if (spUtil.getLogin()) {
            LockModel lockModel = DaoManager.getLockModelUtils().selectLockModel(spUtil.getUserUid());
            if (lockModel != null && !TextUtils.isEmpty(lockModel.getValString())) {
                lockSetting = new Gson().fromJson(lockModel.getValString(), LockSettingModel.class);
            } else {
                lockSetting = new LockSettingModel();
            }
        } else {
            lockSetting = new LockSettingModel();
        }
    }

    /**
     * 手势的信息
     */
    public static void addGesture(String gesture) {
        if (spUtil.getLogin() && lockSetting != null) {
            lockSetting.setGesture(gesture);
            addSetting();
        }
    }

    /**
     * 指纹的信息
     */
    public static void addFinger(boolean isFinger) {
        if (spUtil.getLogin() && lockSetting != null) {
            lockSetting.setFinger(isFinger);
            addSetting();
        }
    }

    private static void addSetting() {
        DaoManager.getLockModelUtils().insertOrReplaceLockModel(spUtil.getUserUid(), gson.toJson(lockSetting));
    }

    /**
     * 退出登录清除信息
     */
    public static void clearSetting() {
        lockSetting = new LockSettingModel();
    }

    public static LockSettingModel getLockSetting() {
        if (lockSetting == null) {
            lockSetting = new LockSettingModel();
        }
        return lockSetting;
    }

    public static void setLockTime(long time) {
        lockTime = time;
    }

    public static long getLockTime() {
        return lockTime;
    }
}
