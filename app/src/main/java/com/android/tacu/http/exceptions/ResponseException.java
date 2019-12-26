package com.android.tacu.http.exceptions;

/**
 * Created by Administrator on 2017/3/21 0021.
 */

public class ResponseException extends Exception {

    public int status;
    public String message;

    public ResponseException(Throwable paramThrowable, int paramInt) {
        super(paramThrowable);
        this.status = paramInt;
    }
}
