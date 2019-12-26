package com.android.tacu.module.assets.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaohong on 2018/10/23.
 */

public class ExchangListModel implements Serializable {
    @SerializedName("total")
    public int total;
    @SerializedName("list")
    public List<ExchangeModel> list = new ArrayList();


    public static class ExchangeModel implements Serializable {
        @SerializedName("id")
        public int id;
        @SerializedName("oldCurrencyName")
        public String oldCurrencyName;
        @SerializedName("newCurrencyName")
        public String newCurrencyName;
        @SerializedName("oldAmount")
        public double oldAmount;
        @SerializedName("newAmount")
        public double newAmount;
        @SerializedName("status")
        public int status;//0:失败   1：成功
        @SerializedName("createTime")
        public String createTime;
        @SerializedName("fee")
        public double fee;
    }
}
