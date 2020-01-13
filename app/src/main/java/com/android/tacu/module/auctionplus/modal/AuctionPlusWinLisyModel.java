package com.android.tacu.module.auctionplus.modal;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiazhen on 2019/6/6.
 */
public class AuctionPlusWinLisyModel implements Serializable {

    @SerializedName("total")
    public long total;
    @SerializedName("list")
    public List<Bean> list = new ArrayList<>();

    public static class Bean implements Serializable {
        
        /**
         * id : 267095
         * auctionId : 225
         * uid : 26093
         * uidObscured : 2***971
         * bonus : 20
         * fee : 10
         * gain : 0.002
         * targetCurrencyId : 3
         * tradePrice : 100
         * location : China
         * locationEn : China
         * locationKo : China
         * createTime : 2019-06-05 10:29:03
         * balance : 0
         * name : null
         * feeName : null
         * bonusName : null
         * gainName : null
         * payName : null
         */

        @SerializedName("id")
        public String id;
        @SerializedName("auctionId")
        public String auctionId;
        @SerializedName("uid")
        public String uid;
        @SerializedName("uidObscured")
        public String uidObscured;
        @SerializedName("bonus")
        public String bonus;
        @SerializedName("fee")
        public String fee;
        @SerializedName("gain")
        public String gain;
        @SerializedName("targetCurrencyId")
        public String targetCurrencyId;
        @SerializedName("tradePrice")
        public String tradePrice;
        @SerializedName("location")
        public String location;
        @SerializedName("locationEn")
        public String locationEn;
        @SerializedName("locationKo")
        public String locationKo;
        @SerializedName("status")
        public int status;// 1待支付 2已支付 3已过期
        @SerializedName("createTime")
        public String createTime;
        @SerializedName("balance")
        public int balance;
        @SerializedName("name")
        public String name;
        @SerializedName("feeName")
        public String feeName;
        @SerializedName("bonusName")
        public String bonusName;
        @SerializedName("gainName")
        public String gainName;
        @SerializedName("payName")
        public String payName;
        @SerializedName("payCurrencyId")
        public Integer payCurrencyId;
        @SerializedName("paymentOverdueTime")
        public String paymentOverdueTime;
    }
}
