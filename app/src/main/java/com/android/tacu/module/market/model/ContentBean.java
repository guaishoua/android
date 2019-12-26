package com.android.tacu.module.market.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ContentBean implements Serializable {

    @SerializedName("time")
    public String time;
    @SerializedName("current")
    public double current;
    @SerializedName("amount")
    public double amount;
    @SerializedName("buyOrSell")
    public int buyOrSell;
}
