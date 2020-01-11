package com.android.tacu.module.auctionplus.modal;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jiazhen on 2019/4/20.
 */
public class AuctionPlusDataModel implements Serializable {

    //列表需要单个刷新的
    @SerializedName("a")
    public String a;//活动id
    @SerializedName("p")
    public String p;//成交价
    @SerializedName("t")
    public long t;//出价倒计时
    @SerializedName("s")
    public int s;//活动状态（1 进行中 2 未开始 3 已结束）
    @SerializedName("n")
    public String n;//每份数量
    @SerializedName("o")
    public String o;//总次数
    @SerializedName("st")
    public String st;//开始时间
    @SerializedName("c")
    public String c;//剩余次数
}
