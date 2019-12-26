package com.android.tacu.module.transaction.model;

import com.google.gson.annotations.*;

import java.io.Serializable;

/**
 * Created by jiazhen on 2018/10/18.
 */
public class DealDetailsModel implements Serializable {
    /**
     * sellFeeCurrencyName:null
     * buyFeeCurrencyName:null
     * tradeTime : 2018-09-26 10:59:08
     * tradeNum : 1
     * tradePrice : 225
     * tradeAmount : 225
     * fee : 0.225
     */

    @SerializedName("sellFeeCurrencyName")
    public String sellFeeCurrencyName;
    @SerializedName("buyFeeCurrencyName")
    public String buyFeeCurrencyName;
    @SerializedName("tradeTime")
    public String tradeTime;
    @SerializedName("tradeNum")
    public double tradeNum;
    @SerializedName("tradePrice")
    public double tradePrice;
    @SerializedName("tradeAmount")
    public double tradeAmount;
    @SerializedName("fee")
    public double fee;
}
