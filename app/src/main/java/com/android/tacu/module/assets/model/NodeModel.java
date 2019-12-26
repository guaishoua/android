package com.android.tacu.module.assets.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jiazhen on 2018/7/7.
 */

public class NodeModel implements Serializable {

    @SerializedName("profitTime")
    public String profitTime;//收益时间
    @SerializedName("numNodes")
    public String numNodes;//可申请节点数
    @SerializedName("unLockNodeCount")
    public String unLockNodeCount;//可解锁节点数
    @SerializedName("nodeProfit")
    public String nodeProfit;//节点收益
    @SerializedName("cashAmount")
    public String cashAmount;//可用余额
    @SerializedName("allNode")
    public String allNode;//全网节点总数
}
