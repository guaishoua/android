package com.android.tacu.http.network;

public class ApiStatus {

    //请求成功
    public static final int SUCCESS_HTTPS = 200;
    //网络连接其他错误
    public static final int ERROR_UNKNOWN = 1000;
    //其他错误
    public static final int ERROR_PARSE = 1001;
    //网络连接错误
    public static final int ERROR_NETWORD = 1002;
    //Http错误
    public static final int ERROR_HTTP = 1003;
    //SSL错误
    public static final int ERROR_SSL = 1004;
    //超时错误
    public static final int ERROR_TIMEOUT = 1005;
    //多账号登录被顶了
    public static final int ERROR_TOKEN = 9999;
}
