package com.android.tacu.module.otc.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SelectStatusModel implements Serializable {

    @SerializedName("uid")
    public Long uid;
    @SerializedName("merchantStatus")
    public Integer merchantStatus; //0=下线 1=上线
    @SerializedName("orderNum")
    public Integer orderNum; //待处理单数
}
