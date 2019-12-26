package com.android.tacu.module.market.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取自选列表
 * Created by jiazhen on 2018/7/17.
 */

public class SelfModel implements Serializable {

    @SerializedName("checked")
    public List<SymbolBean> checkedList = new ArrayList<>();

    public static class SymbolBean implements Serializable {
        @SerializedName("symbol")
        public String symbol;
    }
}
