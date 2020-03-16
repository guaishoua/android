package com.android.tacu.module.auth.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * ================================
 *
 * @作者: 魏玉帅
 * @创建日期:2017/9/19
 * @版本 1.0
 * @描述: ================================
 */

public class UserInfoModel implements Serializable {
    @SerializedName("country")
    public String country;
    @SerializedName("name")
    public String name;
    @SerializedName("firstName")
    public String firstName;
    @SerializedName("secondName")
    public String secondName;
    @SerializedName("singnTime")
    public String singnTime;
    @SerializedName("outofTime")
    public String outofTime;
    @SerializedName("idNumber")
    public String idNumber;
    @SerializedName("positiveImages")
    public String positiveImages;
    @SerializedName("oppositeImages")
    public String oppositeImages;
    @SerializedName("handImages")
    public String handImages;
    @SerializedName("createTime")
    public String createTime;
    @SerializedName("updateTime")
    public String updateTime;
    @SerializedName("gender")
    public String gender;
    @SerializedName("isChina")
    public String isChina;
    @SerializedName("birthday")
    public String birthday;
    @SerializedName("isAllTime")
    public int isAllTime;//1：长期有效  0：不是长期有效

    @SerializedName("positiveImagesStatus")
    public int positiveImagesStatus;//身份证正面上传状态 1.已上传 0.未上传
    @SerializedName("oppositeImagesStatus")
    public int oppositeImagesStatus;//身份证背面上传状态  1.已上传 0.未上传
    @SerializedName("handImagesStatus")
    public int handImagesStatus;//手持身份证上传状态 1.已上传 0.未上传

    @SerializedName("reload")
    public int reload;//是否需要重新上传 1.需要 0.不需要
    @SerializedName("currentLocation")
    public Integer currentLocation;//1身份证  2 护照

    public boolean isChina(String isChina) {
        if (TextUtils.equals(isChina, "0")) {
            return false;
        } else {
            return true;
        }
    }
}
