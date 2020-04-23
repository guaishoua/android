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
    public Integer type;
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
}
