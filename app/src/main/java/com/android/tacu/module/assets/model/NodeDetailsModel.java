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

public class NodeDetailsModel implements Serializable {
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public String status;
    @SerializedName("attachment")
    public List<DetailsModel> attachment = new ArrayList<>();

    public static class DetailsModel implements Serializable {
        @SerializedName("currencyNodeId")
        public String currencyNodeId;//序号
        @SerializedName("unlockTime")
        public String unlockTime;//解锁时间
        @SerializedName("state")
        public String state;//节点状态   1.锁仓  2.解仓
        @SerializedName("allProfit")
        public double allProfit;//总收益
        @SerializedName("dayNum")
        public String dayNum;//锁仓天数

        public String nodeState() {
            switch (state) {
                case "1":
                    return MyApplication.getInstance().getResources().getString(R.string.node_lock);
                case "2":
                    return MyApplication.getInstance().getResources().getString(R.string.node_unlock2);
            }
            return "";
        }

        public String unLockDate() {
            switch (state) {
                case "1":
                    return "--";
                case "2":
                    return unlockTime;
            }
            return "--";
        }
    }

}
