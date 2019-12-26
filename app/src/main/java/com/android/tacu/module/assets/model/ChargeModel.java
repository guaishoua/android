package com.android.tacu.module.assets.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ================================
 *
 * @作者: 魏玉帅
 * @创建日期:2017/7/25
 * @版本 1.0
 * @描述: ================================
 */

public class ChargeModel implements Serializable {


    /**
     * attachment : {"total":2,"sumMoney":1.3E-4,"points":[{"currencyNameEn":"DLC","walletSn":"CL3rZ7Fej1kXfQxPs98YwjsEBmbAzVn4VU","realNum":0,"currencyName":"蝶恋币","createTime":"2017-09-19 17:38:00","fee":0,"coinNum":0,"confirms":"success","txId":"ce85ad10e9bb1da8b4b005453495a79f4d43e47d420dd564e36b449b74126af9","currencyId":4,"status":2,"rechargeId":"201709191738002254718585"},{"currencyNameEn":"DLC","walletSn":"CL3rZ7Fej1kXfQxPs98YwjsEBmbAzVn4VU","realNum":1.3E-4,"currencyName":"蝶恋币","createTime":"2017-09-19 14:42:00","fee":0,"coinNum":1.3E-4,"confirms":"success","txId":"ce85ad10e9bb1da8b4b005453495a79f4d43e47d420dd564e36b449b74126af9","currencyId":4,"status":2,"rechargeId":"2017091914420001349155824"}]}
     * message : null
     */


    /**
     * total : 2
     * sumMoney : 1.3E-4
     * points : [{"currencyNameEn":"DLC","walletSn":"CL3rZ7Fej1kXfQxPs98YwjsEBmbAzVn4VU","realNum":0,"currencyName":"蝶恋币","createTime":"2017-09-19 17:38:00","fee":0,"coinNum":0,"confirms":"success","txId":"ce85ad10e9bb1da8b4b005453495a79f4d43e47d420dd564e36b449b74126af9","currencyId":4,"status":2,"rechargeId":"201709191738002254718585"},{"currencyNameEn":"DLC","walletSn":"CL3rZ7Fej1kXfQxPs98YwjsEBmbAzVn4VU","realNum":1.3E-4,"currencyName":"蝶恋币","createTime":"2017-09-19 14:42:00","fee":0,"coinNum":1.3E-4,"confirms":"success","txId":"ce85ad10e9bb1da8b4b005453495a79f4d43e47d420dd564e36b449b74126af9","currencyId":4,"status":2,"rechargeId":"2017091914420001349155824"}]
     */

    @SerializedName("total")
    public int total;
    @SerializedName("sumMoney")
    public double sumMoney;
    @SerializedName("points")
    public List<PointsBean> points = new ArrayList<>();

    public static class PointsBean implements Serializable{
        /**
         * currencyNameEn : DLC
         * walletSn : CL3rZ7Fej1kXfQxPs98YwjsEBmbAzVn4VU
         * realNum : 0.0
         * currencyName : 蝶恋币
         * createTime : 2017-09-19 17:38:00
         * fee : 0.0
         * coinNum : 0.0
         * confirms : success
         * txId : ce85ad10e9bb1da8b4b005453495a79f4d43e47d420dd564e36b449b74126af9
         * currencyId : 4
         * status : 2
         * rechargeId : 201709191738002254718585
         */

        @SerializedName("currencyNameEn")
        public String currencyNameEn;
        @SerializedName("walletSn")
        public String walletSn;
        @SerializedName("realNum")
        public double realNum;
        @SerializedName("currencyName")
        public String currencyName;
        @SerializedName("createTime")
        public String createTime;
        @SerializedName("fee")
        public double fee;
        @SerializedName("coinNum")
        public double coinNum;
        @SerializedName("confirms")
        public String confirms;
        @SerializedName("txId")
        public String txId;
        @SerializedName("currencyId")
        public int currencyId;
        @SerializedName("status")
        public int statusX;
        @SerializedName("rechargeId")
        public String rechargeId;
    }
}
