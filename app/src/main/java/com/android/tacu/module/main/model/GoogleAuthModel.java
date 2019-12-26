package com.android.tacu.module.main.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * ================================
 *
 * @作者: 魏玉帅
 * @创建日期:2017/7/21
 * @版本 1.0
 * @描述: ================================
 */

public class GoogleAuthModel implements Serializable {

    @SerializedName("qrcode")
    public String qrcode;
    @SerializedName("secretKey")
    public String secretKey;
    @SerializedName("isUsed")
    public String isUsed;
    @SerializedName("confirmNum")
    public int confirmNum;
}
