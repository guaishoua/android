package com.android.tacu.module.main.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * ================================
 *
 * @作者: 魏玉帅
 * @创建日期:2017/7/1
 * @版本 1.0
 * @描述: ================================
 */

public class UploadModel implements Serializable {

    @SerializedName("nowVersion")
    public String nowVersion;

    @SerializedName("msgContent")
    public String msgContent;

    @SerializedName("updateForce")
    public int updateForce;//1:强更 2:正常更新

    @SerializedName("appUrl")
    public String appUrl;
}
