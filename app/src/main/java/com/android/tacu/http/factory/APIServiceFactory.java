package com.android.tacu.http.factory;

import com.android.tacu.http.network.Retrofit2Host;
import com.android.tacu.http.network.Retrofit2Mgr;

/**
 * Created by Administrator on 2017/3/21 0021.
 */

public class APIServiceFactory {

    public static <T> T createAPIService(Retrofit2Host paramRetrofit2Host, Class<T> paramClass) {
        return Retrofit2Mgr.getInstance().getRetrofit(paramRetrofit2Host.getHost()).create(paramClass);
    }
}
