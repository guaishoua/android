package com.android.tacu.module.otc.model;

import com.google.gson.annotations.*;

import java.io.Serializable;

public class OtcSelectFeeModel implements Serializable {

    @SerializedName("marketCurrencyId")
    public Integer marketCurrencyId;
    @SerializedName("otcUserType")
    public Integer otcUserType;
    @SerializedName("buyFee")
    public Double buyFee;
    @SerializedName("buyType")
    public Integer buyType;
    @SerializedName("sellFee")
    public Double sellFee;
    @SerializedName("sellType")
    public Integer sellType;
    @SerializedName("createTime")
    public String createTime;
    @SerializedName("updateTime")
    public String updateTime;
}
