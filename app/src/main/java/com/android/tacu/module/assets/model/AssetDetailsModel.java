package com.android.tacu.module.assets.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ================================
 *
 * @作者: 魏玉帅
 * @创建日期:2017/6/28
 * @版本 1.0
 * @描述: ================================
 */

public class AssetDetailsModel implements Serializable {
    /**
     * attachment : {"allMoney":0,"takeBtcMax":2,"takeBtcNum":0,"coinList":[{"currencyNameEn":"BTC","amount":0,"freezeAmount":0,"currencyName":"BitCoin","cashAmount":0,"icoUrl":"btc","baseCurrencyId":1,"currencyId":1},{"currencyNameEn":"LTC","amount":0,"freezeAmount":0,"currencyName":"Litecoin","cashAmount":0,"icoUrl":"ltc","baseCurrencyId":2,"currencyId":2},{"currencyNameEn":"ETH","amount":0,"freezeAmount":0,"currencyName":"Ethereum","cashAmount":0,"icoUrl":"eth","baseCurrencyId":3,"currencyId":3},{"currencyNameEn":"DLC","amount":0,"freezeAmount":0,"currencyName":"DLC","cashAmount":0,"icoUrl":"dlc","baseCurrencyId":4,"currencyId":4},{"currencyNameEn":"TLC","amount":0,"freezeAmount":0,"currencyName":"Treasure Land","cashAmount":0,"icoUrl":"tlc","baseCurrencyId":5,"currencyId":5},{"currencyNameEn":"LSK","amount":0,"freezeAmount":0,"currencyName":"Lisk","cashAmount":0,"icoUrl":"lsk","baseCurrencyId":6,"currencyId":6},{"currencyNameEn":"ARC","amount":0,"freezeAmount":0,"currencyName":"ArronCoin","cashAmount":0,"icoUrl":"China","baseCurrencyId":7,"currencyId":7},{"currencyNameEn":"BXB","amount":0,"freezeAmount":0,"currencyName":"bixing coin","cashAmount":0,"icoUrl":"china","baseCurrencyId":8,"currencyId":8}]}
     * message : null
     */
    /**
     * allMoney : 0.0
     * takeBtcMax : 2.0
     * takeBtcNum : 0.0
     * coinList : [{"currencyNameEn":"BTC","amount":0,"freezeAmount":0,"currencyName":"BitCoin","cashAmount":0,"icoUrl":"btc","baseCurrencyId":1,"currencyId":1},{"currencyNameEn":"LTC","amount":0,"freezeAmount":0,"currencyName":"Litecoin","cashAmount":0,"icoUrl":"ltc","baseCurrencyId":2,"currencyId":2},{"currencyNameEn":"ETH","amount":0,"freezeAmount":0,"currencyName":"Ethereum","cashAmount":0,"icoUrl":"eth","baseCurrencyId":3,"currencyId":3},{"currencyNameEn":"DLC","amount":0,"freezeAmount":0,"currencyName":"DLC","cashAmount":0,"icoUrl":"dlc","baseCurrencyId":4,"currencyId":4},{"currencyNameEn":"TLC","amount":0,"freezeAmount":0,"currencyName":"Treasure Land","cashAmount":0,"icoUrl":"tlc","baseCurrencyId":5,"currencyId":5},{"currencyNameEn":"LSK","amount":0,"freezeAmount":0,"currencyName":"Lisk","cashAmount":0,"icoUrl":"lsk","baseCurrencyId":6,"currencyId":6},{"currencyNameEn":"ARC","amount":0,"freezeAmount":0,"currencyName":"ArronCoin","cashAmount":0,"icoUrl":"China","baseCurrencyId":7,"currencyId":7},{"currencyNameEn":"BXB","amount":0,"freezeAmount":0,"currencyName":"bixing coin","cashAmount":0,"icoUrl":"china","baseCurrencyId":8,"currencyId":8}]
     */
    @SerializedName("allMoney")
    public String allMoney;
    @SerializedName("takeBtcMax")
    public double takeBtcMax;
    @SerializedName("takeBtcNum")
    public double takeBtcNum;

    @SerializedName("coinList")
    public List<CoinListBean> coinList = new ArrayList<>();
    @SerializedName("selfCoinList")
    public List<CoinListBean> selfCoinList = new ArrayList<>();
    @SerializedName("otcCoinList")
    public List<CoinListBean> otcCoinList = new ArrayList<>();

    public static class CoinListBean implements Serializable {
        /**
         * currencyNameEn : BTC
         * amount : 0.0
         * freezeAmount : 0.0
         * currencyName : BitCoin
         * cashAmount : 0.0
         * icoUrl : btc
         * baseCurrencyId : 1
         * currencyId : 1
         */
        @SerializedName("currencyNameEn")
        public String currencyNameEn;
        @SerializedName("amount")
        public double amount;
        @SerializedName("freezeAmount")
        public double freezeAmount;
        @SerializedName("currencyName")
        public String currencyName;
        @SerializedName("lockAmount")
        public double lockAmount;
        @SerializedName("cashAmount")
        public double cashAmount;
        @SerializedName("icoUrl")
        public String icoUrl;
        @SerializedName("baseCurrencyId")
        public int baseCurrencyId;
        @SerializedName("currencyId")
        public Integer currencyId;
        @SerializedName("btc_value")
        public String btc_value;

        public boolean isUuexOtc = false;
    }
}
