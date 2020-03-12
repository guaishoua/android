package com.android.tacu.module.otc.model;

import com.google.gson.annotations.*;

import java.io.Serializable;

public class OtcSelectFeeModel implements Serializable {

    @SerializedName("id")
    public Integer id;
    @SerializedName("marketCurrencyId")
    public Integer marketCurrencyId;  //市场币种id
    @SerializedName("marketCurrencyName")
    public String marketCurrencyName; //市场币种名称
    @SerializedName("bondRate")
    public Double bondRate;      //保证金比例
}
