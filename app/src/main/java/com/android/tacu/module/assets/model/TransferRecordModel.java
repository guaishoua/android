package com.android.tacu.module.assets.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaohong on 2018/7/9.
 */

public class TransferRecordModel implements Serializable{

    @SerializedName("total")
    public int total;
    @SerializedName("trans_list")
    public List<TransferListDean> tranList = new ArrayList<>();

    public static class TransferListDean implements Serializable{
        @SerializedName("currency_name")
        public String currencyName;//币种
        @SerializedName("create_time")
        public String createTime;
        @SerializedName("trans_num")
        public double transNum;
        @SerializedName("trans_status")
        public String transStatus;
        @SerializedName("trans_type")
        public String transType;
    }
}
