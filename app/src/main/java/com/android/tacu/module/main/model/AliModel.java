package com.android.tacu.module.main.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AliModel implements Serializable {

    @SerializedName("token")
    public String token;
    @SerializedName("url")
    public String url;
}
