package com.android.tacu.http.exceptions;

/**
 * Created by Administrator on 2017/3/21 0021.
 */


public class ServerException extends RuntimeException {

    public int status;
    public String message;

    public ServerException(int status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
}
