package com.android.tacu.module.Auction.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AreaModel implements Serializable {

    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("code")
    public String code;
    @SerializedName("cityCode")
    public String cityCode;
}
