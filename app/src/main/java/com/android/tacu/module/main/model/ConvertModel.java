package com.android.tacu.module.main.model;

import com.google.gson.annotations.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiazhen on 2018/10/20.
 */
public class ConvertModel implements Serializable {

    /**
     * convertCurrencyList : [{"price":"0.15","name":"USD","sign":"$","priceDot":"2"},{"price":"1","name":"CNY","sign":"￥","priceDot":"0"},{"price":"0.1","name":"EUR","sign":"\u20ac","priceDot":"1"}]
     * convertBaseCurrencyId : 22
     * convertBaseCurrencyName : CODE
     */

    @SerializedName("convertBaseCurrencyId")
    public String convertBaseCurrencyId;
    @SerializedName("convertBaseCurrencyName")
    public String convertBaseCurrencyName;
    @SerializedName("convertCurrencyList")
    public List<ConvertCurrencyListBean> convertCurrencyList = new ArrayList<>();

    public static class ConvertCurrencyListBean {
        /**
         * price : 0.15
         * name : USD
         * sign : $
         * priceDot : 2
         */

        @SerializedName("price")
        public double price;//当前法币相对于convertBaseCurrencyName所对应基础币的 比率
        @SerializedName("name")
        public String name;
        @SerializedName("sign")
        public String sign;
        @SerializedName("priceDot")
        public int priceDot;
    }
}
