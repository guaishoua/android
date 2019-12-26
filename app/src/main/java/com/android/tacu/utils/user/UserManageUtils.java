package com.android.tacu.utils.user;

import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseModel;
import com.android.tacu.module.login.model.LoginModel;
import com.android.tacu.utils.lock.LockUtils;
import com.android.tacu.utils.SPUtils;

/**
 * ================================
 *
 * @作者: 魏玉帅
 * @创建日期:2017/6/30
 * @版本 1.0
 * @描述: ================================
 */

public class UserManageUtils {

    //2秒中之内只能退出一次 防止多次退出
    private static final int MIN_HAPPEN_DELAY_TIME = 2000;
    private static long lastHappenTime;
    private static UserInfoUtils spUtil = UserInfoUtils.getInstance();

    /**
     * 登录
     */
    public static void login(BaseModel<LoginModel> model) {
        if (model != null && model.attachment != null) {
            spUtil.setUserUid(model.attachment.uid);
            spUtil.setToken(model.attachment.token);
            LockUtils.init();
        }
    }

    /**
     * 退出登录
     */
    public static void logout() {
        long curHappenTime = System.currentTimeMillis();
        if ((curHappenTime - lastHappenTime) >= MIN_HAPPEN_DELAY_TIME) {
            lastHappenTime = curHappenTime;
            clearUserInfo();
        }
    }

    /**
     * 退出登录清理数据
     */
    public static void clearUserInfo() {
        spUtil.setAuth(0);
        spUtil.setUserUid(0);
        spUtil.setIsAuthSenior(0);
        spUtil.setActivityTime(0);
        spUtil.setFirstRecharge(0);
        spUtil.setToken("");
        spUtil.setPhone("");
        spUtil.setEmail("");
        spUtil.setAccount("");
        spUtil.saveOldCode("");
        spUtil.setGaStatus("");
        spUtil.setAgentTime("");
        spUtil.saveOldAccount("");
        spUtil.setActivityWord1("");
        spUtil.setActivityWord2("");
        spUtil.setActivityWord3("");
        spUtil.setPhoneStatus(false);
        spUtil.setEmailStatus(false);
        spUtil.setValidatePass(false);
        spUtil.setPwdVisibility(false);
        LockUtils.clearSetting();
        SPUtils.getInstance().put(Constant.SELFCOIN_LIST, "");
    }
}
