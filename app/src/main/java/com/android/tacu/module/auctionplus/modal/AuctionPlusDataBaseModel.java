package com.android.tacu.module.auctionplus.modal;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiazhen on 2019/4/28.
 */
public class AuctionPlusDataBaseModel implements Serializable {

    @SerializedName("data")
    public List<AuctionPlusDataModel> data = new ArrayList<>();
    @SerializedName("time")
    public long time;
}
