package com.android.tacu.module.assets.model;

import com.google.gson.annotations.*;

import java.io.Serializable;

/**
 * Created by jiazhen on 2018/12/26.
 */
public class UnclaimedModel implements Serializable {


    /**
     * id : 854
     * customerUuid : e9e61a3cb3c84f3dbf6c680b586b6a97
     * amount : 8.84813554550487E7
     * reward : 96.07658412913491
     * status : 1
     * ds : 2018-12-26
     * createTime : 2018-12-26 10:29:10
     * updateTime : 2018-12-26 10:29:10
     * chexPrice : 2.09866169
     * rewardAll : 0
     */

    @SerializedName("id")
    public int id;
    @SerializedName("customerUuid")
    public String customerUuid;
    @SerializedName("amount")
    public String amount;//总资产
    @SerializedName("reward")
    public String reward;//利息
    @SerializedName("status")
    public int status;//状态 0.不可领取 1。待领取 2.已领取
    @SerializedName("ds")
    public String ds;
    @SerializedName("createTime")
    public String createTime;
    @SerializedName("updateTime")
    public String updateTime;
    @SerializedName("chexPrice")
    public String chexPrice;//chex价格
    @SerializedName("rewardAll")
    public String rewardAll;//历史领息
}
