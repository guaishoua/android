package com.android.tacu.module.market.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * ================================
 *
 * @作者: 魏玉帅
 * @创建日期:2017/7/28
 * @版本 1.0
 * @描述: ================================
 */

public class NoticeModel implements Serializable {

    @SerializedName("id")
    public String id;
    @SerializedName("title")
    public String title;
    @SerializedName("type")
    public String type;
    @SerializedName("htmlUrl")
    public String htmlUrl;
    @SerializedName("createTime")
    public String createTime;
}
