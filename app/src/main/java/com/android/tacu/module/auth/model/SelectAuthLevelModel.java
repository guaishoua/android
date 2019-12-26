package com.android.tacu.module.auth.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * ================================
 *
 * @作者: 魏玉帅
 * @创建日期:2017/6/28
 * @版本 1.0
 * @描述: ================================
 */

public class SelectAuthLevelModel implements Serializable{

    /**
     * gradeName : 测试内容ecpy
     * gradeNameEn : 测试内容rkr8
     * gradeVal : 27174
     * isAuthPrimary : 1
     * isAuthSenior : 1
     * isAuthVideo : 1
     */
    @SerializedName("gradeName")
    public String gradeName;
    @SerializedName("gradeNameEn")
    public String gradeNameEn;
    @SerializedName("gradeVal")
    public int gradeVal;
    @SerializedName("isAuthPrimary")
    public int isAuthPrimary;
    @SerializedName("isAuthSenior")
    public int isAuthSenior;
    @SerializedName("isAuthVideo")
    public int isAuthVideo;
    @SerializedName("auth_fail_reason")
    public String auth_fail_reason;
}
