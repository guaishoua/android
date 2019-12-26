package com.android.tacu.http.httpssafe;

import android.text.TextUtils;

import com.android.tacu.BuildConfig;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by jiazhen on 2019/1/31.
 */
public class SafeHostnameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String hostname, SSLSession session) {
        //仅仅校验正式环境的证书
        if (BuildConfig.ISFORMALHOST) {
            //校验hostname是否正确，如果正确则建立连接
            if (TextUtils.equals(hostname, BuildConfig.IP_RELEASE)) {
                return true;
            }
            return false;
        }
        return true;
    }
}
