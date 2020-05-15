package com.android.tacu.module.auction.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PCAModel implements Serializable {

    @SerializedName("name")
    public String name;
    @SerializedName("code")
    public String code;

    public PCAModel(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
