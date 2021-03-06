package com.android.tacu.utils.user;

import android.text.TextUtils;

import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseModel;
import com.android.tacu.module.payinfo.model.PayInfoModel;
import com.android.tacu.module.login.model.LoginModel;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.SPUtils;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFUserInfo;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

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
            MobclickAgent.onProfileSignIn(String.valueOf(spUtil.getUserUid()));
        }
    }

    public static void login(BaseModel<LoginModel> model, String accountString) {
        if (model != null && model.attachment != null) {
            spUtil.setUserUid(model.attachment.uid);
            spUtil.setToken(model.attachment.token);
            spUtil.setAccountString(accountString);
            MobclickAgent.onProfileSignIn(String.valueOf(spUtil.getUserUid()));
        }
    }

    public static void loginUnicorn(OwnCenterModel model) {
        YSFUserInfo userInfo = new YSFUserInfo();
        if (model != null) {
            userInfo.userId = String.valueOf(model.uid);
            userInfo.data = CommonUtils.userInfoData(model.nickname, model.phone, model.email, CommonUtils.getHead(model.headImg)).toJSONString();
            Unicorn.setUserInfo(userInfo);
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
            //Unicorn.logout();
        }
    }

    /**
     * 设置个人信息数据
     */
    public static void setPersonInfo(OwnCenterModel model) {
        if (model != null) {
            //是否开启谷歌认证
            spUtil.setGaStatus(model.isValidateGoogle);

            //是否每次都需要输入交易密码
            if (TextUtils.equals(model.fdPwdOrderEnabled, "1")) {
                spUtil.setPwdVisibility(true);
            } else if (TextUtils.equals(model.fdPwdOrderEnabled, "2")) {
                spUtil.setPwdVisibility(false);
            }

            //个人信息
            if (!TextUtils.isEmpty(model.phone)) {
                spUtil.setPhoneStatus(true);
            } else {
                spUtil.setPhoneStatus(false);
            }
            if (!TextUtils.isEmpty(model.email)) {
                spUtil.setEmailStatus(true);
            } else {
                spUtil.setEmailStatus(false);
            }
            if (!TextUtils.isEmpty(model.phone) || !TextUtils.isEmpty(model.email)) {
                if (!TextUtils.isEmpty(model.phone)) {
                    spUtil.setAccount(model.phone);
                } else if (!TextUtils.isEmpty(model.email)) {
                    spUtil.setAccount(model.email);
                }
            }

            spUtil.setPhone(model.phone);
            spUtil.setEmail(model.email);
            spUtil.setAuth(model.isAuth);
            spUtil.setIsAuthSenior(model.isAuthSenior);
            spUtil.setIsAuthVideo(model.isAuthVideo);
            spUtil.setValidatePass(model.getIsValidatePass());
            spUtil.setNickName(model.nickname);
            spUtil.setHeadImg(model.headImg);
            spUtil.setKYCName(model.name);
            spUtil.setVip(model.vip);
            spUtil.setDisclaimer(model.disclaimer);
            spUtil.setApplyMerchantStatus(model.applyMerchantStatus);
            spUtil.setApplyAuthMerchantStatus(model.applyAuthMerchantStatus);
        }
    }

    public static void setPeoplePayInfo(List<PayInfoModel> list) {
        if (list != null && list.size() > 0) {
            spUtil.setIsPayInfo(true);
        } else {
            spUtil.setIsPayInfo(false);
        }
    }

    /**
     * 退出登录清理数据
     */
    public static void clearUserInfo() {
        spUtil.setVip(0);
        spUtil.setAuth(0);
        spUtil.setUserUid(0);
        spUtil.setDisclaimer(0);
        spUtil.setIsAuthSenior(0);
        spUtil.setApplyMerchantStatus(0);
        spUtil.setApplyAuthMerchantStatus(0);
        spUtil.setToken("");
        spUtil.setPhone("");
        spUtil.setEmail("");
        spUtil.setHeadImg("");
        spUtil.setKYCName("");
        spUtil.setAccount("");
        spUtil.setNickName("");
        spUtil.saveOldCode("");
        spUtil.setGaStatus("");
        spUtil.saveOldAccount("");
        spUtil.setIsPayInfo(false);
        spUtil.setPhoneStatus(false);
        spUtil.setEmailStatus(false);
        spUtil.setValidatePass(false);
        spUtil.setPwdVisibility(false);
        spUtil.setAssetShowStatus(true);
        SPUtils.getInstance().put(Constant.SELFCOIN_LIST, "");
    }
}
