package com.android.tacu.module.auction.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AuctionLogsListModel implements Serializable {

    @SerializedName("total")
    public Integer total;
    @SerializedName("list")
    public List<AuctionLogsModel> list = new ArrayList<>();
}
