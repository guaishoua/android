package com.android.tacu.module.splash.model;

import com.google.gson.annotations.*;

import java.io.Serializable;

public class TradeWinModel implements Serializable {

    public TradeWinModel(String id, String name, String uid, String tradeAmount, String amount, String reward) {
        this.id = id;
        this.name = name;
        this.uid = uid;
        this.tradeAmount = tradeAmount;
        this.amount = amount;
        this.reward = reward;
    }

    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("uid")
    public String uid;
    @SerializedName("tradeAmount")
    public String tradeAmount;
    @SerializedName("amount")
    public String amount;
    @SerializedName("reward")
    public String reward;
}
