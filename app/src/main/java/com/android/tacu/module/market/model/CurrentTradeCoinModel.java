package com.android.tacu.module.market.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CurrentTradeCoinModel implements Serializable {

    @SerializedName("currentTradeCoin")
    public CurrentTradeCoinBean currentTradeCoin;
    @SerializedName("currentTradeCoinRealTime")
    public List<CurrentTradeCoinRealTimeBean> currentTradeCoinRealTime;

    public static class CurrentTradeCoinBean implements Serializable {
        @SerializedName("coinCode")
        public int coinCode;
        @SerializedName("baseCurrencyId")
        public int baseCurrencyId;
        @SerializedName("baseCurrencyNameEn")
        public String baseCurrencyNameEn;
        @SerializedName("currentAmount")
        public double currentAmount;
        @SerializedName("highPrice")
        public double highPrice;
        @SerializedName("lowPrice")
        public double lowPrice;
        @SerializedName("openPrice")
        public double openPrice;
        @SerializedName("closePrice")
        public double closePrice;
        @SerializedName("yesterClosePirce")
        public double yesterClosePirce;
        @SerializedName("changeRate")
        public double changeRate;
        @SerializedName("changeAmount")
        public double changeAmount;
        @SerializedName("volume")
        public double volume;
        @SerializedName("amount")
        public double amount;
        @SerializedName("currencyId")
        public int currencyId;
        @SerializedName("currencyName")
        public String currencyName;
        @SerializedName("currencyNameEn")
        public String currencyNameEn;
        @SerializedName("pointNum")
        public int pointNum;
        @SerializedName("pointPrice")
        public int pointPrice;
        @SerializedName("previousPrice")
        public double previousPrice;
        @SerializedName("buyFee")
        public double buyFee;
        @SerializedName("sellFee")
        public double sellFee;
        @SerializedName("amountLowLimit")
        public double amountLowLimit;
        @SerializedName("amountHighLimit")
        public double amountHighLimit;
        @SerializedName("entrustPriceMin")
        public double entrustPriceMin;
        @SerializedName("entrustPriceMax")
        public double entrustPriceMax;
        @SerializedName("relationStatus")
        public int relationStatus;
        @SerializedName("entrustScale")
        public double entrustScale;
        @SerializedName("rmbScale")
        public double rmbScale;
        @SerializedName("kycBuy")
        public KycBuyBean kycBuy;
        @SerializedName("kycSell")
        public KycSellBean kycSell;

        public static class KycBuyBean implements Serializable {
            @SerializedName("feeType")
            public int feeType;
            @SerializedName("buyOrSell")
            public int buyOrSell;
            @SerializedName("feeKyc1")
            public double feeKyc1;
            @SerializedName("feeKyc2")
            public double feeKyc2;
            @SerializedName("feeKyc3")
            public double feeKyc3;
        }

        public static class KycSellBean implements Serializable {
            @SerializedName("feeType")
            public int feeType;
            @SerializedName("buyOrSell")
            public int buyOrSell;
            @SerializedName("feeKyc1")
            public double feeKyc1;
            @SerializedName("feeKyc2")
            public double feeKyc2;
            @SerializedName("feeKyc3")
            public double feeKyc3;
        }
    }

    public static class CurrentTradeCoinRealTimeBean implements Serializable {
        @SerializedName("id")
        public int id;
        @SerializedName("coinCode")
        public String coinCode;
        @SerializedName("time")
        public long time;
        @SerializedName("current")
        public double current;
        @SerializedName("avgPrice")
        public double avgPrice;
        @SerializedName("volume")
        public double volume;
        @SerializedName("amount")
        public double amount;
        @SerializedName("changeRate")
        public double changeRate;
        @SerializedName("changeAmount")
        public double changeAmount;
        @SerializedName("createTime")
        public String createTime;
        @SerializedName("baseCurrencyId")
        public String baseCurrencyId;
    }
}
