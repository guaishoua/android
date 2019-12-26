package com.android.tacu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.android.tacu.api.Constant;
import com.android.tacu.base.MyApplication;
import com.android.tacu.utils.user.UserInfoUtils;
import com.google.gson.Gson;

import java.util.Locale;

/**
 * ================================
 *
 * @作者: 魏玉帅
 * @创建日期:2017/9/20
 * @版本 1.0
 * @描述: ================================
 */

public class LanguageUtils {

    //简体
    public static final Locale SIMPLIFIED_CHINESE = Locale.SIMPLIFIED_CHINESE;
    //繁体中文
    public static final Locale TRADITIONAL_CHINESE = Locale.TRADITIONAL_CHINESE;
    //英文
    public static final Locale LOCALE_ENGLISH = Locale.US;

    /**
     * 保存SharedPreferences的文件名
     */
    private static final String LOCALE_FILE = "LOCALE_FILE_3";
    /**
     * 保存Locale的key
     */
    private static final String LOCALE_KEY = "LOCALE_KEY_3";

    /**
     * 获取用户设置的Locale
     *
     * @param pContext Context
     * @return Locale
     */
    public static Locale getUserLocale(Context pContext) {
        SharedPreferences spLocale = pContext.getSharedPreferences(LOCALE_FILE, Context.MODE_PRIVATE);
        String localeJson = spLocale.getString(LOCALE_KEY, "");
        if (TextUtils.isEmpty(localeJson)) {
            UserInfoUtils mSpUtil = UserInfoUtils.getInstance();
            Locale currLocale = getCurrentLocale();
            String langu = currLocale.getLanguage();
            if (TextUtils.equals(langu, "zh")) {
                String country = currLocale.getCountry();
                if (TextUtils.equals(country, "TW")) {
                    localeJson = localeToJson(TRADITIONAL_CHINESE);
                    mSpUtil.setLanguage(Constant.ZH_TW);
                } else {
                    localeJson = localeToJson(SIMPLIFIED_CHINESE);
                    mSpUtil.setLanguage(Constant.ZH_CN);
                }
            }  else {
                localeJson = localeToJson(LOCALE_ENGLISH);
                mSpUtil.setLanguage(Constant.EN_US);
            }
        }
        return jsonToLocale(localeJson);
    }

    /**
     * 获取当前的Locale
     *
     * @return Locale
     */
    public static Locale getCurrentLocale() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //7.0有多语言设置获取顶部的语言
            locale = MyApplication.getInstance().getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = MyApplication.getInstance().getResources().getConfiguration().locale;
        }
        return locale;
    }

    /**
     * 保存用户设置的Locale
     *
     * @param pContext    Context
     * @param pUserLocale Locale
     */
    public static void saveUserLocale(Context pContext, Locale pUserLocale) {
        SharedPreferences spLocal = pContext.getSharedPreferences(LOCALE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = spLocal.edit();
        String localeJson = localeToJson(pUserLocale);
        edit.putString(LOCALE_KEY, localeJson);
        edit.apply();
        MyApplication.setUserLocale(pUserLocale);
    }

    /**
     * Locale转成json
     *
     * @param pUserLocale UserLocale
     * @return json String
     */
    private static String localeToJson(Locale pUserLocale) {
        return new Gson().toJson(pUserLocale);
    }

    /**
     * json转成Locale
     *
     * @param pLocaleJson LocaleJson
     * @return Locale
     */
    private static Locale jsonToLocale(String pLocaleJson) {
        return new Gson().fromJson(pLocaleJson, Locale.class);
    }

    /**
     * 更新Locale
     *
     * @param pNewUserLocale New User Locale
     */
    public static void updateLocale(Locale pNewUserLocale) {
        if (needUpdateLocale(pNewUserLocale)) {
            Configuration configuration = MyApplication.getInstance().getResources().getConfiguration();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLocale(pNewUserLocale);
            } else {
                configuration.locale = pNewUserLocale;
            }
            DisplayMetrics displayMetrics = MyApplication.getInstance().getResources().getDisplayMetrics();
            MyApplication.getInstance().getResources().updateConfiguration(configuration, displayMetrics);
            Locale.setDefault(configuration.locale);
            saveUserLocale(MyApplication.getInstance(), pNewUserLocale);
        }
    }

    /**
     * 判断需不需要更新
     *
     * @param pNewUserLocale New User Locale
     * @return true / false
     */
    public static boolean needUpdateLocale(Locale pNewUserLocale) {
        return pNewUserLocale != null && !getCurrentLocale().equals(pNewUserLocale);
    }
}
