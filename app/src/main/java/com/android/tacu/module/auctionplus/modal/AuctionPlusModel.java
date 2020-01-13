package com.android.tacu.module.auctionplus.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jiazhen on 2019/4/17.
 */
public class AuctionPlusModel implements Serializable {

    //列表的数据
    @SerializedName("id")
    public String id;
    @SerializedName("currencyId")
    public int currencyId;
    @SerializedName("currencyName")
    public String currencyName;
    @SerializedName("num")
    public String num;//数量
    @SerializedName("auctionName")
    public String auctionName;
    @SerializedName("auctionNameEn")
    public String auctionNameEn;
    @SerializedName("auctionNameKo")
    public String auctionNameKo;
    @SerializedName("img")
    public String img;
    @SerializedName("imgEn")
    public String imgEn;
    @SerializedName("imgKo")
    public String imgKo;
    @SerializedName("currencyIntroduce")
    public String currencyIntroduce;//币种介绍
    @SerializedName("currencyIntroduceEn")
    public String currencyIntroduceEn;//币种介绍
    @SerializedName("currencyIntroduceKo")
    public String currencyIntroduceKo;//币种介绍
    @SerializedName("paper")
    public String paper;//白皮书
    @SerializedName("paperEn")
    public String paperEn;//白皮书
    @SerializedName("paperKo")
    public String paperKo;//白皮书
    @SerializedName("bonusCurrencyId")
    public String bonusCurrencyId;//奖励币种id
    @SerializedName("feeCurrencyId")
    public String feeCurrencyId;//手续费币种
    @SerializedName("feeRang")
    public String feeRang;//手续费加价幅度
    @SerializedName("fee")
    public String fee;
    @SerializedName("feeCurrencyName")
    public String feeCurrencyName;
    @SerializedName("uid")
    public String uid;//中标uid
    @SerializedName("auctionStatus")
    public int auctionStatus;//标的状态 1进行中 2未开始 3已结束
    @SerializedName("eachNum")
    public String eachNum;//每份数量
    @SerializedName("eachPrice")
    public String eachPrice;//每份价格
    @SerializedName("totalNumber")
    public String totalNumber;//总数量
    @SerializedName("surplusNumber")
    public String surplusNumber;//剩余数量
    @SerializedName("tradePrice")
    public String tradePrice;//成交价
    @SerializedName("paymentOverdueTime")
    public String paymentOverdueTime;//支付过期时间
    @SerializedName("startTime")
    public String startTime;//开始时间
    @SerializedName("endTime")
    public String endTime;//结束书剑

    @SerializedName("timeLength")
    public String timeLength;//倒计时长
    @SerializedName("followCount")
    public String followCount;//收藏人数
    @SerializedName("watchCount")
    public String watchCount;//围观人数
    @SerializedName("currentCountPeo")
    public String currentCountPeo;//出价人数

    @SerializedName("payCurrencyId")
    public Integer payCurrencyId;//支付币种
    @SerializedName("payCurrencyName")
    public String payCurrencyName;//支付币种名称

    @SerializedName("timestamp")
    public long timestamp;//倒计时时间戳
    @Expose(deserialize = false)
    public int payPlusStatus = 0;//支付状态 1 待支付 2支付成功 3支付过期
}
