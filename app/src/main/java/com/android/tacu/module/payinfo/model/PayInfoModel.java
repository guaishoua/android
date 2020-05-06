package com.android.tacu.module.payinfo.model;

import com.google.gson.annotations.*;

import java.io.Serializable;

public class PayInfoModel implements Serializable {

    @SerializedName("id")
    public Integer id;
    @SerializedName("uid")
    public Integer uid;
    @SerializedName("uuid")
    public String uuid;
    @SerializedName("type")
    public Integer type;//1=银行卡 2=微信 3=支付宝
    @SerializedName("name")
    public String name;
    @SerializedName("bankName")
    public String bankName;
    @SerializedName("openBankName")
    public String openBankName;
    @SerializedName("openBankAdress")
    public String openBankAdress;
    @SerializedName("bankCard")
    public String bankCard;
    @SerializedName("weChatNo")
    public String weChatNo;
    @SerializedName("weChatImg")
    public String weChatImg;
    @SerializedName("aliPayNo")
    public String aliPayNo;
    @SerializedName("aliPayImg")
    public String aliPayImg;
    @SerializedName("createTime")
    public String createTime;
    @SerializedName("updateTime")
    public String updateTime;
    @SerializedName("status")
    public Integer status;// 0关闭 1开启

    @Expose(deserialize = false)
    public boolean isCB = false;
}
