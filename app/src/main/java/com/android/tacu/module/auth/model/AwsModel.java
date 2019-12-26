package com.android.tacu.module.auth.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jiazhen on 2018/12/5.
 */
public class AwsModel implements Serializable {

    @SerializedName("bucket")
    public String bucket;
    @SerializedName("id")
    public String id;
    @SerializedName("key")
    public String key;
}
