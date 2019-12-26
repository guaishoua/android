package com.android.tacu.module.assets.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by xiaohong on 2018/7/24.
 */

public class ExchangeShowModel implements Serializable{
    @SerializedName("scale")
    public String scale;
    @SerializedName("exchangeAmount")
    public double exchangeAmount;
    @SerializedName("GetAmount")
    public double GetAmount;
}
