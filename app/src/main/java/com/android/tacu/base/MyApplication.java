package com.android.tacu.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.android.tacu.service.AppIntentService;
import com.android.tacu.utils.LanguageUtils;
import com.android.tacu.utils.lock.LockUtils;

import java.util.Locale;

/**
 * Created by weiyushuai on 2017/4/14.
 */

public class MyApplication extends Application {
    /**
     * 全局Context，方便引用
     */
    private static MyApplication application;
    private static Locale userLocale;

    public static MyApplication getInstance() {
        return application;
    }

    public static Locale getUserLocale() {
        return userLocale;
    }

    public static void setUserLocale(Locale userLocale) {
        MyApplication.userLocale = userLocale;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        //手势和指纹密码的信息
        LockUtils.init();
        //語言
        userLocale = LanguageUtils.getUserLocale(this);
        //一些第三方放在这里
        AppIntentService.start(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
