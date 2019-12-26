package com.android.tacu.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import com.android.tacu.base.MyApplication;

import java.util.ArrayList;


public class PackageUtils {

    public static final String META_NAME = "UMENG_CHANNEL";

    /**
     * 渠道名
     */
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiKey;
    }

    /**
     * 版本NAME值
     */
    public static String getVersion() {
        String packageName = MyApplication.getInstance().getPackageName();
        PackageInfo versionInfo;
        String version = null;
        try {
            versionInfo = MyApplication.getInstance().getPackageManager().getPackageInfo(packageName, 0);
            version = versionInfo.versionName;
        } catch (NameNotFoundException e) {
            version = "1.0";
            e.printStackTrace();
        }
        return version;
    }

    //截取版本号
    public static Boolean splitVersionNum(String newVersion, String oldVersion) {
        //拿到最新的版本号 截取成三个数字
        String[] splitNumNew = newVersion.split("\\.");
        //拿到现在的版本号 截取成三个数字
        String[] splitNumOld = oldVersion.split("\\.");
        ArrayList<Integer> splitNew = new ArrayList();
        ArrayList<Integer> splitOld = new ArrayList();
        //将字符串转成数字
        for (String strToNumNew : splitNumNew) {
            splitNew.add(Integer.parseInt(strToNumNew));
        }

        for (String strToNumOld : splitNumOld) {
            splitOld.add(Integer.parseInt(strToNumOld));
        }
        return isNeedUpdate(splitNew, splitOld);
    }

    private static Boolean isNeedUpdate(ArrayList<Integer> splitNew, ArrayList<Integer> splitOld) {
        //首先比较第一位 如果第一位相同 就比较第二位  来不及写递归 有点脏
        if ((splitNew.size() == 2)) {
            splitNew.add(2, 0);
        }
        if ((splitOld.size() == 2)) {
            splitOld.add(2, 0);
        }

        if (splitNew.get(0) > splitOld.get(0)) {
            return true;
        }
        //不会出现现在版本号超前的情况
        if (splitNew.get(0).equals(splitOld.get(0))) {
            //第一位相同开始比较第二位
            if (splitNew.get(1) > splitOld.get(1)) {
                return true;
            } else if (splitNew.get(1).equals(splitOld.get(1))) {
                //开始比较第三位
                if (splitNew.get(2).equals(splitOld.get(2))) {
                    return false;
                } else if (splitNew.get(2) > splitOld.get(2)) {
                    return true;
                }
            }
        }
        return false;
    }
}
