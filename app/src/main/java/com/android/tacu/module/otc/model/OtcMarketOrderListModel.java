package com.android.tacu.module.otc.model;

import com.google.gson.annotations.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OtcMarketOrderListModel implements Serializable {

    @SerializedName("total")
    public Integer total;

    @SerializedName("infoList")
    public List<OtcMarketInfoModel> infoList = new ArrayList<>();

    @SerializedName("list")
    public List<OtcMarketOrderModel> list = new ArrayList<>();
}
