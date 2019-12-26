package com.android.tacu.module.assets.model;

import com.google.gson.annotations.*;

import java.io.Serializable;

/**
 * Created by jiazhen on 2018/12/26.
 */
public class CashChexAndRateModel implements Serializable {
    @SerializedName("cashAmount")
    public String cashAmount;
}
