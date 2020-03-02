package com.android.tacu.module.transaction.model;


import com.android.tacu.R;
import com.android.tacu.base.MyApplication;
import com.google.gson.annotations.SerializedName;

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

public class ShowOrderListModel implements Serializable {
    /**
     * total : 2
     * list : [{"currencyNameEn":"LTC","remainNum":"0.0000000","orderNo":"1589645235984201023556001235955","dealAmount":"0.0000000","fee":"0.0000000","num":"0.0100000","tradeNum":"0.0000000","baseCurrencyid":1,"baseCurrencyName":"BitCoin","|baseCurrencyNameEn":"BTC","buyOrSell":1,"orderTime":"2018-04-26 14:00:35","currencyName":"Litecoin","price":"10.0000000","averagePrice":"0.0000000","status":4,"tradePerc":"0.1"},{"currencyNameEn":"LTC","remainNum":"0.0000000","orderNo":"1589645235984201023556001235695","dealAmount":"0.0000000","fee":"0.0000000","num":"0.0100000","tradeNum":"0.0000000","baseCurrencyid":1,"baseCurrencyName":"BitCoin","|baseCurrencyNameEn":"BTC","buyOrSell":1,"orderTime":"2018-04-26 14:25:01","currencyName":"Litecoin","price":"10.0000000","averagePrice":"0.0000000","status":4,"tradePerc":"0.1"}]
     */

    @SerializedName("total")
    public int total;
    @SerializedName("list")
    public List<ListBean> list = new ArrayList<>();

    public static class ListBean implements Serializable {

        /**
         * currencyNameEn : LTC
         * remainNum : 0.0000000
         * orderNo : 1589645235984201023556001235955
         * dealAmount : 0.0000000
         * fee : 0.0000000
         * num : 0.0100000
         * tradeNum : 0.0000000
         * baseCurrencyid : 1
         * baseCurrencyName : BitCoin
         * baseCurrencyNameEn : BTC
         * buyOrSell : 1
         * orderTime : 2018-04-26 14:00:35
         * currencyName : Litecoin
         * price : 10.0000000
         * averagePrice : 0.0000000
         * status : 4
         * tradePerc : 0.1
         */

        /**
         * |averagePrice |成交均价  |decimal |
         * |baseCurrencyId |基础币种 |int|
         * |baseCurrencyName |基础币名称 |string |
         * |baseCurrencyNameEn |基础币简称 |string|
         * |buyOrSell | 买卖方向 0=全部方向、1=buy、2=sell|int|
         * |currencyName |数字货币全称 |string |
         * |currencyNameEn|数字货币简称  |string|
         * |dealAmount |成交金额 |decimal |
         * |fee  |交易费用 |decimal |
         * |num |委托数量  |decimal |
         * |orderNo |委托单号 |string |
         * |orderTime |委托时间 |string |
         * |price  |委托价格 |decimal |
         * |remainNum |剩余数量 |decimal |
         * |status |委托单状态，未成交=0、部分成交=1、全部成交=2、撤单=4、全部状态=10|int|
         * |tradeNum |成交数量 |decimal|
         * |tradePerc |成交率 | string |
         * |type |类型 | int | 1=限价 2=市价
         */

        /**
         * |0        |全部方向   |  int |
         * |1        |买入      |  int |
         * |2        |卖出      |  int |
         */

        @SerializedName("currencyNameEn")
        public String currencyNameEn;
        @SerializedName("remainNum")
        public String remainNum;
        @SerializedName("orderNo")
        public String orderNo;
        @SerializedName("dealAmount")
        public String dealAmount;
        @SerializedName("fee")
        public String fee;
        @SerializedName("num")
        public String num;
        @SerializedName("tradeNum")
        public String tradeNum;
        @SerializedName("baseCurrencyid")
        public int baseCurrencyid;
        @SerializedName("baseCurrencyName")
        public String baseCurrencyName;
        @SerializedName("baseCurrencyNameEn")
        public String baseCurrencyNameEn;
        /**
         * 1=buy 2=sell
         */
        @SerializedName("buyOrSell")
        public int buyOrSell;
        @SerializedName("orderTime")
        public String orderTime;
        @SerializedName("currencyName")
        public String currencyName;
        @SerializedName("price")
        public String price;
        @SerializedName("averagePrice")
        public String averagePrice;
        @SerializedName("status")
        public int status;
        @SerializedName("tradePerc")
        public String tradePerc;

        @SerializedName("type")
        public int type;

        /**
         * 不参与解析  代表被选中
         */
        public boolean isCheckOrder = false;

        public String getStatus() {
            switch (status) {
                case 2:
                    return MyApplication.getInstance().getResources().getString(R.string.dealed_status);
                case 4:
                    return MyApplication.getInstance().getResources().getString(R.string.revoked_status);
                case 5:
                    return MyApplication.getInstance().getResources().getString(R.string.dealedpart_status);
            }
            return "";
        }

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
