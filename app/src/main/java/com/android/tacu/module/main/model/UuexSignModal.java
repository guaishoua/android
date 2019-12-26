package com.android.tacu.module.main.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UuexSignModal implements Serializable {

    @SerializedName("token")
    public String token;
    @SerializedName("sign")
    public String sign;
    @SerializedName("timestamp")
    public String timestamp;
}
