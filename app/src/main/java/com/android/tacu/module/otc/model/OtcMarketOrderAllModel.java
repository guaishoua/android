package com.android.tacu.module.otc.model;

import com.google.gson.annotations.*;

import java.io.Serializable;

public class OtcMarketOrderAllModel implements Serializable {

    @SerializedName("info")
    public OtcMarketInfoModel infoModel;

    @SerializedName("order")
    public OtcMarketOrderModel orderModel;
}
