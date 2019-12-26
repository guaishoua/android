package com.android.tacu.utils;

import android.util.Log;

import com.android.tacu.BuildConfig;

/**
 * Created by jiazhen on 2018/4/18.
 */

public class LogUtils {

    public static final boolean showLog = BuildConfig.DEBUG;
    public static final String tag = "tag_chaoex";

    public static void d(String tag, String msg) {
        if (showLog) Log.d(tag, msg);
    }

    public static void d(String msg) {
        if (showLog) Log.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (showLog) Log.i(tag, msg);
    }

    public static void i(String msg) {
        if (showLog) Log.i(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (showLog) Log.v(tag, msg);
    }

    public static void v(String msg) {
        if (showLog) Log.v(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (showLog) Log.w(tag, msg);
    }

    public static void w(String msg) {
        if (showLog) Log.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (showLog) Log.e(tag, msg);
    }

    public static void e(String msg) {
        if (showLog) {
            StackTraceElement invoker = getInvoker();
            showLogCompletion("【" + invoker.getClassName() + ":" + invoker.getMethodName() + ":" + invoker.getLineNumber() + "】" + msg, 4 * 1000);
        }
    }

    private static StackTraceElement getInvoker() {
        return Thread.currentThread().getStackTrace()[4];
    }

    /**
     * 分段打印出较长log文本
     *
     * @param log       原log文本
     * @param showCount 规定每段显示的长度（最好不要超过eclipse限制长度）
     */
    private static void showLogCompletion(String log, int showCount) {
        if (log.length() > showCount) {
            String show = log.substring(0, showCount);
            Log.e(tag, show + "");
            if ((log.length() - showCount) > showCount) {//剩下的文本还是大于规定长度
                String partLog = log.substring(showCount, log.length());
                showLogCompletion(partLog, showCount);
            } else {
                String surplusLog = log.substring(showCount, log.length());
                Log.e(tag, surplusLog + "");
            }
        } else {
            Log.e(tag, log + "");
        }
    }
}
