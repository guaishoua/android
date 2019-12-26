package com.android.tacu.module.market.model;

import android.annotation.SuppressLint;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by jiazhen on 2019/3/5.
 */
public class MarketNewModel implements Serializable {

    @SerializedName("name")
    public String name;
    @SerializedName("name_en")
    public String name_en;
    @SerializedName("sort_type")
    public Integer sort_type;//自动排序规则单选：1.价格正序 2.价格倒序 3.涨跌幅正序 4.涨跌幅倒序 5.交易币首字母正序 6.交易币首字母倒序 7.24小时成交量正序 8.24小时成交量倒序

    @SerializedName("info")
    public List<InfoBean> baseCoinList = new ArrayList<>();
    @SerializedName("tradeCoinsFixed")//固定顺序
    public List<TradeCoinsBean> tradeCoinsFixedList = new ArrayList<>();
    @SerializedName("tradeCoinsAuto")//需要移动端根据sort_type这个字段排序的
    public List<TradeCoinsBean> tradeCoinsAutoList = new ArrayList<>();

    //tradeCoinsFixedList和tradeCoinsAutoList的合并
    @Expose(deserialize = false)
    public List<TradeCoinsBean> tradeCoinsList = new ArrayList<>();

    public static class TradeCoinsBean implements Serializable {
        @SerializedName("coinCode")
        public int coinCode;
        @SerializedName("currencyId")
        public int currencyId;
        @SerializedName("currencyName")
        public String currencyName;
        @SerializedName("currencyNameEn")
        public String currencyNameEn;
        @SerializedName("baseCurrencyId")
        public int baseCurrencyId;
        @SerializedName("baseCurrencyNameEn")
        public String baseCurrencyNameEn;
        @SerializedName("currentAmount")
        public double currentAmount;
        @SerializedName("volume")
        public double volume;
        @SerializedName("icoUrl")
        public String icoUrl;
        @SerializedName("pointNum")
        public int pointNum;
        @SerializedName("pointPrice")
        public int pointPrice;
        @SerializedName("rmbScale")
        public double rmbScale;
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
        @SerializedName("amount")
        public double amount;
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
        @SerializedName("hours24TrendList")
        public List<List<String>> hours24TrendList = new ArrayList<>();

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TradeCoinsBean bean = (TradeCoinsBean) o;
            return Double.compare(bean.currentAmount, currentAmount) == 0 &&
                    Double.compare(bean.changeRate, changeRate) == 0 &&
                    Double.compare(bean.volume, volume) == 0 &&
                    pointNum == bean.pointNum &&
                    pointPrice == bean.pointPrice &&
                    Double.compare(bean.rmbScale, rmbScale) == 0;
        }

        @SuppressLint("NewApi")
        @Override
        public int hashCode() {
            return Objects.hash(currentAmount, changeRate, volume, pointNum, pointPrice, rmbScale);
        }
    }

    public static class InfoBean implements Serializable {
        @SerializedName("currencyId")
        public int currencyId;
        @SerializedName("currencyNameEn")
        public String currencyNameEn;
        @SerializedName("baseExchangeAmount")
        public double baseExchangeAmount;//当前币相对于 汇率币的 比值
    }
}
