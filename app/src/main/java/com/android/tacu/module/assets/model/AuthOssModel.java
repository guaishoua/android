package com.android.tacu.module.assets.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jiazhen on 2018/7/23.
 */

public class AuthOssModel implements Serializable {

    @SerializedName("SecurityToken")
    public String SecurityToken;
    @SerializedName("bucket")
    public String bucket;
    @SerializedName("AccessKeyId")
    public String AccessKeyId;
    @SerializedName("AccessKeySecret")
    public String AccessKeySecret;
    @SerializedName("Expiration")
    public String Expiration;
}
