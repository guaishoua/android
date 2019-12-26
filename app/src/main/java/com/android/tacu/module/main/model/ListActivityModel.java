package com.android.tacu.module.main.model;

import com.google.gson.annotations.*;

import java.io.Serializable;

/**
 * Created by jiazhen on 2018/11/20.
 */
public class ListActivityModel implements Serializable {

    @SerializedName("content")
    public String content;
    @SerializedName("butten")
    public String butten;
    @SerializedName("index")
    public Integer index;
    @SerializedName("order")
    public Long order;

    /**
     * -1注册有奖;
     * 0领取充值奖励；
     * 1首次邀请好友奖励;
     * 2领取注册奖励并前往KYC2认证；
     * 3领取注册奖励；
     * 4前往KYC2认证；
     * 5领取KYC2奖励并前往KYC3认证；
     * 6领取KYC2奖励；
     * 7前往KYC3认证；
     * 8领取KYC3奖励。
     */
}
