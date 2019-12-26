package com.android.tacu.module.assets.model;

import com.android.tacu.R;
import com.android.tacu.base.MyApplication;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaohong on 2018/8/3.
 */

public class NodeProfitModel implements Serializable{
    @SerializedName("total")
    public int total;
    @SerializedName("list")
    public List<NodeTotalModel> list = new ArrayList<>();

    public static class NodeTotalModel implements Serializable {
        @SerializedName("currencyNodeProfitId")
        public String currencyNodeProfitId;
        @SerializedName("putProfitTime")
        public String putProfitTime;//奖励发放时间
        @SerializedName("profitNodeNum")
        public String profitNodeNum;//发放奖励节点数
        @SerializedName("oneProfit")
        public double oneProfit;//单节点收益
        @SerializedName("allProfit")
        public double allProfit;//总收益
        @SerializedName("state")
        public String state;//节点状态  1.已发放   2.未发放

        public String nodeState() {
            switch (state) {
                case "1":
                    return MyApplication.getInstance().getResources().getString(R.string.node_distributed);
                case "2":
                    return MyApplication.getInstance().getResources().getString(R.string.node_no);
            }
            return "";
        }
    }
}
