package com.android.tacu.module.vip.model;

import com.google.gson.annotations.*;

import java.io.Serializable;

public class VipDetailModel implements Serializable {

    @SerializedName("id")
    public Integer id;
    @SerializedName("uuid")
    public Integer uuid;
    @SerializedName("uid")
    public Integer uid;
    @SerializedName("vipMonth")
    public Integer vipMonth; //是否按月，0不是 1是
    @SerializedName("vipOneYear")
    public Integer vipOneYear;//是否按年，0不是 1是
    @SerializedName("vipYearAuto")
    public Integer vipYearAuto;//是否包年续费，0不是 1是
    @SerializedName("endTime")
    public String endTime;//到期时间
    @SerializedName("timestamp")
    public Long timestamp;
    @SerializedName("createTime")
    public String createTime;
    @SerializedName("updateTime")
    public String updateTime;
}
