package com.android.tacu.http.httpssafe;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by jiazhen on 2019/1/31.
 */
public class NoSafeHostnameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String hostname, SSLSession session) {
        //信任所有https
        return true;
    }
}
