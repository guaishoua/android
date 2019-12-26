package com.android.tacu.module.assets.model;

import com.google.gson.annotations.*;

import java.io.Serializable;

/**
 * Created by jiazhen on 2018/12/26.
 */
public class LockChexAmountModel implements Serializable {

    @SerializedName("historyProfit")
    public String historyProfit;//已获收益
    @SerializedName("allProfit")
    public String allProfit;//仓锁收益
    @SerializedName("profit")
    public String profit;//预计收益
    @SerializedName("lockNum")
    public int lockNum;//锁定数量
    @SerializedName("rate1")
    public String rate1;//半年利率
    @SerializedName("rate2")
    public String rate2;//一年利率
}
