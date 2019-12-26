package com.android.tacu.module.assets.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by xiaohong on 2018/7/23.
 */

public class ExchangeModel implements Serializable{
    @SerializedName("buyPrice")
    public double buyPrice;
    @SerializedName("sellPrice")
    public double sellPrice;
}
