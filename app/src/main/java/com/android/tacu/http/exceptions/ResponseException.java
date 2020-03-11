package com.android.tacu.http.exceptions;

/**
 * Created by Administrator on 2017/3/21 0021.
 */

public class ResponseException extends Exception {

    public int status;
    //服务器返回的是1
    public int type = 0;
    public String message;

    public ResponseException(Throwable paramThrowable, int paramInt) {
        super(paramThrowable);
        this.type = 0;
        this.status = paramInt;
    }

    public ResponseException(Throwable paramThrowable, int type, int paramInt) {
        super(paramThrowable);
        this.type = type;
        this.status = paramInt;
    }
}
