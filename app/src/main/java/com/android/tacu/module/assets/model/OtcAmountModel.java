package com.android.tacu.module.assets.model;

import com.google.gson.annotations.*;

import java.io.Serializable;

public class OtcAmountModel implements Serializable {

    @SerializedName("id")
    public String id;
    @SerializedName("uuid")
    public String uuid;
    @SerializedName("uid")
    public Integer uid;
    @SerializedName("currencyId")
    public Integer currencyId;
    @SerializedName("currencyName")
    public String currencyName;
    @SerializedName("amount")
    public String amount;//总额
    @SerializedName("cashAmount")
    public String cashAmount;//可用
    @SerializedName("freezeAmount")
    public String freezeAmount;//冻结
    @SerializedName("bondFreezeAmount")
    public String bondFreezeAmount;//提币冻结
    @SerializedName("createTime")
    public String createTime;
    @SerializedName("updateTime")
    public String updateTime;
}
