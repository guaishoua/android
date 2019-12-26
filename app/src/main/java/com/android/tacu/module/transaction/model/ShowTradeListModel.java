package com.android.tacu.module.transaction.model;


import com.android.tacu.R;
import com.android.tacu.base.MyApplication;
import com.google.gson.annotations.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ================================
 *
 * @作者: jiazhen
 * @创建日期:2017/8/10
 * @版本 1.0
 * @描述: ================================
 */

public class ShowTradeListModel implements Serializable {

    /**
     * total : 220
     * list : [{"currencyNameEn":"DCT","remainNum":"0.0000","orderNo":"15354580875964552353031103855138","tradeAmount":"0.00048400","fee":"0.0055","num":"11.0000","tradeNum":"11.0000","baseCurrencyId":1,"baseCurrencyName":"Bitcoin","baseCurrencyNameEn":"BTC","buyOrSell":1,"tradeTime":"2018-08-28 20:08:08","currencyName":"DECENT","price":"0.00004400","tradePrice":"0.00004400","status":2},{"currencyNameEn":"DCT","remainNum":"0.0000","orderNo":"15354580876684562353191103813563","tradeAmount":"0.00048400","fee":"0.00000024","num":"11.0000","tradeNum":"11.0000","baseCurrencyId":1,"baseCurrencyName":"Bitcoin","baseCurrencyNameEn":"BTC","buyOrSell":2,"tradeTime":"2018-08-28 20:08:08","currencyName":"DECENT","price":"0.00004400","tradePrice":"0.00004400","status":2}]
     */

    @SerializedName("total")
    public int total;
    @SerializedName("list")
    public List<ListBean> list = new ArrayList<>();

    public static class ListBean implements Serializable{
        /**
         * currencyNameEn : DCT
         * remainNum : 0.0000
         * orderNo : 15354580875964552353031103855138
         * tradeAmount : 0.00048400
         * fee : 0.0055
         * num : 11.0000
         * tradeNum : 11.0000
         * baseCurrencyId : 1
         * baseCurrencyName : Bitcoin
         * baseCurrencyNameEn : BTC
         * buyOrSell : 1
         * tradeTime : 2018-08-28 20:08:08
         * currencyName : DECENT
         * price : 0.00004400
         * tradePrice : 0.00004400
         * status : 2
         */

        /*|tradeTime |成交时间 |string |
          |sellFeeCurrencyName |手续费类型（如果该字段为null 则用原来的） |string |
          |buyFeeCurrencyName |手续费类型（如果该字段为null 则用原来的） |string |
          |tradePrice |成交价格  |decimal |
          |tradeNum |成交数量 |decimal|
          |tradeAmount |成交金额 |decimal |
          |baseCurrencyId |基础币种 |int|
          |baseCurrencyName |基础币名称 |string |
          |baseCurrencyNameEn |基础币简称 |string|
          |buyOrSell | 买卖方向 |int|
          |currencyName |数字货币全称 |string |
          |currencyNameEn|数字货币简称  |string|
          |fee  |交易费用 |decimal |
          |num |委托数量  |decimal |
          |orderNo |委托单号 |string |
          |status |委托单状态 |int|*/

        @SerializedName("sellFeeCurrencyName")
        public String sellFeeCurrencyName;
        @SerializedName("buyFeeCurrencyName")
        public String buyFeeCurrencyName;
        @SerializedName("currencyNameEn")
        public String currencyNameEn;
        @SerializedName("remainNum")
        public String remainNum;
        @SerializedName("orderNo")
        public String orderNo;
        @SerializedName("tradeAmount")
        public String tradeAmount;
        @SerializedName("fee")
        public String fee;
        @SerializedName("num")
        public String num;
        @SerializedName("tradeNum")
        public String tradeNum;
        @SerializedName("baseCurrencyId")
        public int baseCurrencyId;
        @SerializedName("baseCurrencyName")
        public String baseCurrencyName;
        @SerializedName("baseCurrencyNameEn")
        public String baseCurrencyNameEn;
        @SerializedName("buyOrSell")
        public int buyOrSell;
        @SerializedName("tradeTime")
        public String tradeTime;
        @SerializedName("currencyName")
        public String currencyName;
        @SerializedName("price")
        public String price;
        @SerializedName("tradePrice")
        public String tradePrice;
        @SerializedName("status")
        public int status;

        public String getBuyOrSell() {
            switch (buyOrSell) {
                case 1:
                    return MyApplication.getInstance().getResources().getString(R.string.buy);
                case 2:
                    return MyApplication.getInstance().getResources().getString(R.string.sell);
            }
            return "";
        }

        public int getTextColor() {
            switch (buyOrSell) {
                case 1:
                    return R.color.color_riseup;
                case 2:
                    return R.color.color_risedown;
            }
            return 0;
        }
    }
}
