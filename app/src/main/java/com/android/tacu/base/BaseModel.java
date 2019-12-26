package com.android.tacu.base;

/**
 * 用于辅助解析
 * Created by jiazhen on 2017/3/21 0021.
 */

import com.google.gson.annotations.SerializedName;

public class BaseModel<T> {

    @SerializedName("attachment")
    public T attachment;

    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;
}
