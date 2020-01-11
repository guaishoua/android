package com.android.tacu.module.auctionplus.modal;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jiazhen on 2019/6/6.
 */
public class AuctionPlusPayInfoModel implements Serializable {

    @SerializedName("gain")
    public String gain;//可获得币种数量
    @SerializedName("pay")
    public String pay;//需要支付usdt数量
    @SerializedName("currency")
    public String currency;//可获得币种名称
}
