package com.android.tacu.http.network;

import android.text.TextUtils;

import com.android.tacu.http.factory.SSLSocketGenFactory;
import com.android.tacu.http.httpssafe.CustomTrustManager;
import com.android.tacu.http.httpssafe.NoSafeHostnameVerifier;
import com.android.tacu.http.intercept.CommonHeaderInterceptor;
import com.android.tacu.http.intercept.LoggingInterceptor;
import com.android.tacu.utils.LogUtils;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by jiazhen on 2018/8/8.
 */

public class Retrofit2Mgr {

    private static final int CONN_TIME_OUT = 15;
    private static final int READ_TIME_OUT = 15;
    private static final int WRITE_TIME_OUT = 15;
    private static volatile Retrofit2Mgr mRtf2Mgr;
    private static HashMap<String, Retrofit> mRtfContainer;
    private Retrofit.Builder mRtfBuilder;

    /**
     * 如果需要校验cer证书的话就替换
     * sslSocketFactory(SSLSocketGenFactory.genSSLSocketCerFactory(certificateInputStream), new CustomTrustManager())
     * hostnameVerifier(new SafeHostnameVerifier())
     */
    private Retrofit2Mgr() {
        OkHttpClient.Builder localBuilder = new OkHttpClient().newBuilder().
                connectTimeout(CONN_TIME_OUT, TimeUnit.SECONDS).
                readTimeout(READ_TIME_OUT, TimeUnit.SECONDS).
                writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS).
                addInterceptor(new CommonHeaderInterceptor()).
                addInterceptor(new LoggingInterceptor()).//打印请求的参数
                addInterceptor(loggingInterceptor).//打印请求成功的回调
                sslSocketFactory(SSLSocketGenFactory.genSSLSocketFactory(), new CustomTrustManager()).
                hostnameVerifier(new NoSafeHostnameVerifier()).
                retryOnConnectionFailure(true);
        this.mRtfBuilder = new Retrofit.Builder().client(localBuilder.build()).
                addConverterFactory(ScalarsConverterFactory.create()).
                addConverterFactory(GsonConverterFactory.create()).
                addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        mRtfContainer = new HashMap();
    }

    public static Retrofit2Mgr getInstance() {
        if (mRtf2Mgr == null) {
            synchronized (Retrofit2Mgr.class) {
                if (mRtf2Mgr == null) {
                    mRtf2Mgr = new Retrofit2Mgr();
                }
            }
        }
        return mRtf2Mgr;
    }

    public Retrofit getRetrofit(String paramString) {
        Retrofit localRetrofit = this.mRtfContainer.get(paramString);
        if (localRetrofit == null) {
            localRetrofit = this.mRtfBuilder.baseUrl(paramString).build();
            mRtfContainer.put(paramString, localRetrofit);
        }
        return localRetrofit;
    }

    private HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {

        @Override
        public void log(String message) {
            if (TextUtils.isEmpty(message)) return;
            String s = message.substring(0, 1);
            //如果收到想响应是json才打印
            if ("{".equals(s) || "[".equals(s)) {
                LogUtils.i("收到响应: " + message);
            }
        }
    }).setLevel(HttpLoggingInterceptor.Level.BODY);
}
