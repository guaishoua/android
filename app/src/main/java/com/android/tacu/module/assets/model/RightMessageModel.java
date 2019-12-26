package com.android.tacu.module.assets.model;

import com.google.gson.annotations.*;

import java.io.Serializable;

/**
 * Created by jiazhen on 2018/12/26.
 */
public class RightMessageModel implements Serializable {

    @SerializedName("allProfit")
    public String allProfit;
    @SerializedName("yesterdayProfit")
    public String yesterdayProfit;
    @SerializedName("lockNum")
    public int lockNum;
    @SerializedName("lockAmount")
    public int lockAmount;
}
