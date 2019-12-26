package com.android.tacu.module.assets.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaohong on 2018/8/2.
 */

public class NodeListModel implements Serializable{
    @SerializedName("total")
    public int total;
    @SerializedName("list")
    public List<NodeModel> list = new ArrayList<>();

    public static class NodeModel implements Serializable{
        @SerializedName("profitTime")
        public String profitTime;//收益时间
        @SerializedName("lockTime")
        public String lockTime;//发放时间
        @SerializedName("allProfit")
        public double allProfit;//总收益
        @SerializedName("yesterdayProfit")
        public double yesterdayProfit;//昨日收益
        @SerializedName("nodeIds")
        public String nodeIds;
        @SerializedName("allNodeNum")
        public String allNodeNum;//全部节点数
        @SerializedName("lockNodeNum")
        public String lockNodeNum;//锁定节点数
    }
}
