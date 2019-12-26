package com.android.tacu.module.assets.model;

import com.google.gson.annotations.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiazhen on 2018/12/26.
 */
public class LockChexListModel implements Serializable {

    @SerializedName("total")
    public long total;
    @SerializedName("totalPage")
    public int totalPage;
    @SerializedName("list")
    public List<LockModel> list = new ArrayList<>();

    public static class LockModel implements Serializable {
        @SerializedName("status")
        public int status;//状态  1:仓锁中  2:已结清
        @SerializedName("num")
        public String num;//锁仓数量
        @SerializedName("profit")
        public String profit;//预计收益
        @SerializedName("historyProfit")
        public String historyProfit;//历史收益
        @SerializedName("createTime")
        public String createTime;//锁定时间
        @SerializedName("lockTime")
        public String lockTime;//到期时间
        @SerializedName("rate")
        public String rate;//-汇率
        @SerializedName("dateVal")
        public int dateVal;//时间值 1:6个月 2:12个月
    }
}
