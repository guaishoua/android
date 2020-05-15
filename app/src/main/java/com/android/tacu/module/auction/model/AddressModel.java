package com.android.tacu.module.auction.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AddressModel implements Serializable {
    @SerializedName("id")
    public String id;
    @SerializedName("consignee")
    public String consignee;
    @SerializedName("phone")
    public String phone;
    @SerializedName("provinceCode")
    public String provinceCode;
    @SerializedName("provinceName")
    public String provinceName;
    @SerializedName("cityCode")
    public String cityCode;
    @SerializedName("cityName")
    public String cityName;
    @SerializedName("areaCode")
    public String areaCode;
    @SerializedName("areaName")
    public String areaName;
    @SerializedName("detailedAddress")
    public String detailedAddress;
    @SerializedName("status")
    public Integer status;
}
