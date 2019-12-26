package com.android.tacu.module.assets.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TransInfoCoinModal implements Serializable {

    @SerializedName("in")
    public List<CoinModel> inList = new ArrayList<>();
    @SerializedName("out")
    public List<CoinModel> outList = new ArrayList<>();

    public static class CoinModel implements Serializable {
        @SerializedName("currencyId")
        public int currencyId;
        @SerializedName("currencyName")
        public String currencyName;
        @SerializedName("actionId")
        public int actionId;
    }
}
