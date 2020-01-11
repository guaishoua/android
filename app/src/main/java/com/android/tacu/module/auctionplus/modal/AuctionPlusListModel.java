package com.android.tacu.module.auctionplus.modal;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiazhen on 2019/4/19.
 */
public class AuctionPlusListModel implements Serializable {

    @SerializedName("total")
    public long total;
    @SerializedName("time")
    public long time;
    @SerializedName("list")
    public List<AuctionPlusModel> list = new ArrayList<>();
}
