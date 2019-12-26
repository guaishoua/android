package com.android.tacu.module.my.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * ================================
 *
 * @作者: 魏玉帅
 * @创建日期:2017/9/7
 * @版本 1.0
 * @描述: ================================
 */

public class InvitedInfoModel implements Serializable {

    @SerializedName("invited_id")
    public String invited_id;
    @SerializedName("count")
    public String count;
}
