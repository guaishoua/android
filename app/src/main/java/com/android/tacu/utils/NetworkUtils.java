package com.android.tacu.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 判断网络状态
 * Created by jiazhen on 2017/3/22.
 */

public class NetworkUtils {

    public static ConnectivityManager getConnManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static boolean isConnected(Context mContext) {
        NetworkInfo networkInfo = getConnManager(mContext).getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }
}
