package com.android.tacu.module.main.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * ================================
 *
 * @作者: 魏玉帅
 * @创建日期:2017/6/19
 * @版本 1.0
 * @描述: ================================
 */

public class PersonalInfoModel implements Serializable {

    @SerializedName("uname")
    public String uname;
    @SerializedName("email")
    public String email;
    @SerializedName("isAuth")
    public int isAuth;
    @SerializedName("phone")
    public String phone;
    @SerializedName("isValidatePass")
    public int isValidatePass;
    @SerializedName("isValidateEmail")
    public int isValidateEmail;
    @SerializedName("score")
    public int score;
    @SerializedName("hasAddress")
    public int hasAddress;
    @SerializedName("authFailReason")
    public String authFailReason;
    @SerializedName("name")
    public String name;
    @SerializedName("idNumber")
    public String idNumber;
    @SerializedName("nation")
    public String nation;
    @SerializedName("gender")
    public String gender;
    @SerializedName("birthday")
    public String birthday;
    @SerializedName("usualAddress")
    public String usualAddress;
    @SerializedName("isAuthPrimary")
    public int isAuthPrimary;
    @SerializedName("isAuthSenior")
    public int isAuthSenior;
    @SerializedName("isAuthVideo")
    public int isAuthVideo;
    @SerializedName("profession")
    public String profession;
    @SerializedName("positiveImages")
    public String positiveImages;
    @SerializedName("oppositeImages")
    public String oppositeImages;
    @SerializedName("handImages")
    public String handImages;
    @SerializedName("country")
    public String country;
    @SerializedName("address")
    public AddressBean address;

    public boolean getIsValidatePass() {
        if (isValidatePass == 1) {
            return true;
        } else if (isValidatePass == 0) {
            return false;
        }
        return true;
    }

    public String getLevel() {
        switch (isAuthSenior) {
            case -1:
            case 0:
            case 1:
                return "KYC1";
            case 2:
                if (isAuthVideo == 2) {
                    return "KYC3";
                } else {
                    return "KYC2";
                }
        }
        return "KYC1";
    }

    public static class AddressBean {
        @SerializedName("uaid")
        public String uaid;
        @SerializedName("country")
        public String country;
        @SerializedName("city")
        public String city;
        @SerializedName("town")
        public String town;
        @SerializedName("location")
        public String location;
        @SerializedName("phone")
        public String phone;
        @SerializedName("name")
        public String name;
        @SerializedName("postCode")
        public String postCode;
        @SerializedName("isDefault")
        public String isDefault;
        @SerializedName("phonePwd")
        public String phonePwd;
    }

    public static class UserLoginRecordBean  {
        @SerializedName("ulrId")
        public String ulrId;
        @SerializedName("time")
        public String time;
        @SerializedName("host")
        public String host;
        @SerializedName("location")
        public String location;
        @SerializedName("source")
        public int source;
    }
}
