package com.android.tacu.module.market.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TradeHistoryModelTwo implements Serializable{

    @SerializedName("tradeCurrencyId")
    public String tradeCurrencyId;
    @SerializedName("baseCurrencyId")
    public String baseCurrencyId;
    @SerializedName("type")
    public int type;
    @SerializedName("content")
    public ContentBean content;
}
