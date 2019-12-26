package com.android.tacu.module.transaction.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jiazhen on 2019/3/14.
 */
public class FeeModel implements Serializable {

    @SerializedName("currencyId")
    public int currencyId;
    @SerializedName("baseCurrencyId")
    public int baseCurrencyId;
    @SerializedName("buyType")
    public int buyType;//买手续费类型  1固定额度  2百分比
    @SerializedName("buyFee")
    public double buyFee;
    @SerializedName("sellType")
    public int sellType;
    @SerializedName("sellFee")
    public double sellFee;
}
