package com.android.tacu.module.transaction.model;

import com.google.gson.annotations.*;

import java.io.Serializable;

/**
 * Created by jiazhen on 2018/10/27.
 */
public class UserAccountModel implements Serializable {


    /**
     * tradeCurrencyId : 1
     * baseCurrencyId : 63
     * baseCoinBalance : 500000000
     * tradeCoinBalance : 500000000
     * authLevel : 2
     */

    @SerializedName("tradeCurrencyId")
    public int tradeCurrencyId;
    @SerializedName("baseCurrencyId")
    public int baseCurrencyId;
    @SerializedName("baseCoinBalance")
    public double baseCoinBalance;
    @SerializedName("tradeCoinBalance")
    public double tradeCoinBalance;
    @SerializedName("authLevel")
    public int authLevel;
}
