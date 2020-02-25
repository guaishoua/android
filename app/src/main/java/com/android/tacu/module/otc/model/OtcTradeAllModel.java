package com.android.tacu.module.otc.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OtcTradeAllModel implements Serializable {

    @SerializedName("info")
    public OtcMarketInfoModel infoModel;

    @SerializedName("order")
    public OtcTradeModel tradeModel;
}
