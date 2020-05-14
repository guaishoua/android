package com.android.tacu.module.Auction.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProvinceModel implements Serializable {

    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("code")
    public String code;
    @SerializedName("status")
    public Integer status;
}
