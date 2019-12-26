package com.android.tacu.module.assets.model;

import com.google.gson.annotations.*;

import java.io.Serializable;

/**
 * Created by jiazhen on 2018/12/26.
 */
public class RewardCoinAccountModel implements Serializable {

    @SerializedName("amount")
    public double amount;
    @SerializedName("amountDot")
    public int amountDot;
}
