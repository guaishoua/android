package com.android.tacu.module.auction.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CityModel implements Serializable {
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("code")
    public String code;
    @SerializedName("provinceCode")
    public String provinceCode;
}
