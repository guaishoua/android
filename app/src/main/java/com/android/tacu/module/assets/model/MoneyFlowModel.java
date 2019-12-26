package com.android.tacu.module.assets.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MoneyFlowModel implements Serializable {

    @SerializedName("total")
    public int total;
    @SerializedName("list")
    public List<ListBean> list = new ArrayList<>();
    @SerializedName("type")
    public List<TypeModel> type = new ArrayList();

    public static class ListBean implements Serializable {
        /**
         * "id": null,
         * "uid": null,
         * "customerUuid": null,
         * "currencyId": 1,
         * "currencyNameEn": null,
         * "type": 5,
         * "amount": 1,
         * "fee": 0,
         * "txid": null,
         * "adress": null,
         * "createTime": 1538035512000
         */

        @SerializedName("currencyNameEn")
        public String currencyNameEn;
        @SerializedName("amount")
        public double amount;
        @SerializedName("adress")
        public String adress;
        @SerializedName("txid")
        public String txid;
        @SerializedName("currencyName")
        public String currencyName;
        @SerializedName("createTime")
        public String createTime;
        @SerializedName("fee")
        public double fee;
        @SerializedName("currencyId")
        public int currencyId;
        @SerializedName("type")
        public String type;
    }

    public static class TypeModel implements Serializable {
        @SerializedName("code")
        public String code;
        @SerializedName("value")
        public String value;
    }
}
