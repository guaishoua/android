package com.android.tacu.module.auctionplus.modal;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jiazhen on 2019/6/5.
 */
public class AuctionPayStatusModel implements Serializable {

    //列表的数据
    @SerializedName("auctionId")
    public String auctionId;
    @SerializedName("status")
    public int status;//1=待支付 2=已支付 3=已过期
}
