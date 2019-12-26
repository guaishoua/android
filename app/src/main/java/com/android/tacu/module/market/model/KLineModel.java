package com.android.tacu.module.market.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xiaohong on 2018/5/16.
 */

public class KLineModel implements Serializable {

    @SerializedName("data")
    public DataModel data;
    @SerializedName("success")
    public boolean success;

    public static class DataModel implements Serializable {
        @SerializedName("lines")
        public List<List<Float>> lines;
    }
}
