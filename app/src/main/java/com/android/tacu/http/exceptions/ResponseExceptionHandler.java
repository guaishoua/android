package com.android.tacu.http.exceptions;

import android.net.ParseException;

import com.android.tacu.R;
import com.android.tacu.http.network.ApiStatus;
import com.android.tacu.base.MyApplication;
import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLHandshakeException;

import retrofit2.HttpException;

/**
 * Created by Administrator on 2017/3/21 0021.
 */

public class ResponseExceptionHandler {

    public static ResponseException handleResponseException(Throwable throwable) {
        ResponseException responseException = null;
        if (throwable instanceof HttpException) {
            responseException = new ResponseException(throwable, ApiStatus.ERROR_HTTP);
            responseException.message = MyApplication.getInstance().getResources().getString(R.string.net_unavailable);
            return responseException;
        } else if (throwable instanceof ServerException) {
            /**
             * 判断token是否失效,失效强制退出登录
             */
            if (((ServerException) throwable).status == ApiStatus.ERROR_TOKEN) {
                responseException = new ResponseException(throwable, ApiStatus.ERROR_TOKEN);
                /**
                 * 状态码不是200 状态提示
                 */
            } else if (((ServerException) throwable).status != ApiStatus.SUCCESS_HTTPS) {
                responseException = new ResponseException(throwable, ApiStatus.ERROR_TOAST);
                String message = ((ServerException) throwable).message;
                responseException.message = message;
            }
            return responseException;
        } else if (!(throwable instanceof JsonParseException) && !(throwable instanceof JSONException) && !(throwable instanceof ParseException)) {
            if (throwable instanceof ConnectException) {
                responseException = new ResponseException(throwable, ApiStatus.ERROR_NETWORD);
                responseException.message = MyApplication.getInstance().getResources().getString(R.string.net_unavailable);
                return responseException;
            } else if (!(throwable instanceof SSLHandshakeException) && !(throwable instanceof CertificateException)) {
                if (throwable instanceof SocketTimeoutException) {
                    responseException = new ResponseException(throwable, ApiStatus.ERROR_TIMEOUT);
                    responseException.message = MyApplication.getInstance().getResources().getString(R.string.net_unavailable);
                    return responseException;
                } else {
                    ResponseException exception = new ResponseException(throwable, ApiStatus.ERROR_UNKNOWN);
                    exception.message = MyApplication.getInstance().getResources().getString(R.string.net_unavailable);
                    return exception;
                }
            } else {
                responseException = new ResponseException(throwable, ApiStatus.ERROR_SSL);
                responseException.message = MyApplication.getInstance().getResources().getString(R.string.net_busy);
                return responseException;
            }
        } else {
            responseException = new ResponseException(throwable, ApiStatus.ERROR_PARSE);
            responseException.message = MyApplication.getInstance().getResources().getString(R.string.net_busy);
            return responseException;
        }
    }
}
