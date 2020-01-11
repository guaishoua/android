package com.android.tacu.module.auctionplus.modal;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 出价
 * Created by jiazhen on 2019/4/22.
 */
public class AuctionPlusOfferPriceModel implements Serializable {

    @SerializedName("uid")
    public String uid;
    @SerializedName("fee")
    public String fee;
    @SerializedName("bonus")
    public String bonus;
    @SerializedName("bonusName")
    public String bonusName;
    @SerializedName("location")
    public String location;
    @SerializedName("locationEn")
    public String locationEn;
    @SerializedName("locationKo")
    public String locationKo;
}
