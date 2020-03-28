package com.android.tacu.module.vip.model;

import com.google.gson.annotations.*;

import java.io.Serializable;

public class VipDetailRankModel implements Serializable {

    @SerializedName("id")
    public Integer id;
    @SerializedName("vipName")
    public String vipName;
    @SerializedName("amount")
    public String amount;
    @SerializedName("currentAmount")
    public String currentAmount;
    @SerializedName("createTime")
    public String createTime;
    @SerializedName("updateTime")
    public String updateTime;

    @SerializedName("discountFee")
    public Double discountFee;
}
