package com.android.tacu.module.login.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * ================================
 *
 * @作者: 魏玉帅
 * @创建日期:2018/1/16
 * @版本 1.0
 * @描述: ================================
 */

public class CountryModel implements Serializable {

    /**
     * en : Angola
     * zh : 安哥拉
     * locale : AO
     * code : 244
     */
    @SerializedName("en")
    public String en;
    @SerializedName("zh")
    public String zh;
    @SerializedName("code")
    public int code;
    @SerializedName("piyin")
    public String piyin;
}
