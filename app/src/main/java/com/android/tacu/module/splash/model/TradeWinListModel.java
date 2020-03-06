package com.android.tacu.module.splash.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TradeWinListModel implements Serializable {

    @SerializedName("win")
    public List<TradeWinModel> win = new ArrayList<>();

    @SerializedName("expe")
    public List<TradeWinModel> expe = new ArrayList<>();
}
