package com.android.tacu.module.assets.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by xiaohong on 2018/10/21.
 */

public class AmountModel implements Serializable {
    @SerializedName("attachment")
    public double attachment;
    @SerializedName("status")
    public int status;
}
