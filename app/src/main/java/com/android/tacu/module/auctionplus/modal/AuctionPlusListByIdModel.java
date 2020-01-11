package com.android.tacu.module.auctionplus.modal;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiazhen on 2019/6/5.
 */
public class AuctionPlusListByIdModel implements Serializable {

    @SerializedName("arr")
    public ArrModel arr;
    @SerializedName("time")
    public long time;
    @SerializedName("info")
    public AuctionPlusModel info;

    public static class ArrModel implements Serializable{
        @SerializedName("total")
        public long total;
        @SerializedName("list")
        public List<AuctionPlusOfferPriceModel> list = new ArrayList<>();
    }
}
