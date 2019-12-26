package com.android.tacu.module.transaction.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RecordModel implements Serializable {
    @SerializedName("tradeCurrencyId")
    public int tradeCurrencyId;
    @SerializedName("baseCurrencyId")
    public int baseCurrencyId;
    @SerializedName("entrustScale")
    public double entrustScale;
    @SerializedName("buy")
    public List<BuyBean> buy;
    @SerializedName("sell")
    public List<SellBean> sell;

    public static class BuyBean {
        @SerializedName("current")
        public double current;
        @SerializedName("number")
        public double number;

        //用于深度数据展示不参与json解析
        public double buyEntrustScale;
        public double buyEntrustNumber;
    }

    public static class SellBean {
        /**
         * current : 18.0
         * number : 9.0
         */
        @SerializedName("current")
        public double current;
        @SerializedName("number")
        public double number;

        //用于深度数据展示不参与json解析
        public double sellEntrustScale;
        public double sellEntrustNumber;
    }
}
