package com.android.tacu.module.main.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jiazhen on 2018/10/24.
 */
public class OwnCenterModel implements Serializable {
    /**
     * uid : null
     * fdPwdOrderEnabled : 2
     * isValidateGoogle : 0
     * isAuthPrimary : 2
     * isAuthSenior : 2
     * isAuthVideo : 2
     * authLevel : null
     * uname : null
     * email : null
     * isAuth : 3
     * phone : 15093086219
     * chexDed : 1
     * isValidatePhone : 1
     * isValidatePass : 1
     * isValidateEmail : 0
     * address : {"uaid":null,"country":null,"city":null,"town":null,"location":null,"phone":null,"name":null,"postCode":null,"isDefault":null,"phonePwd":"MTUwOTMwODYyMTk=\n","email":null}
     * userLoginRecord : {"start":1,"size":20,"total":0,"orderBy":"desc","ulrId":"194149","time":"2018-10-24 15:19:16","host":"118.194.240.71","location":"Beijing,Beijing,China","source":2,"status":1,"type":null}
     * score : null
     * hasAddress : 0
     * name : null
     * idNumber : null
     * nation : null
     * gender : null
     * birthday : null
     * usualAddress : null
     * positiveImages : null
     * oppositeImages : null
     * handImages : null
     * authFailReason : null
     * reward : null
     * hidedPhone : null
     * hidedEmail : null
     * country :
     */


    @SerializedName("uid")
    public int uid;
    @SerializedName("fdPwdOrderEnabled")
    public String fdPwdOrderEnabled;//是否每次输入交易密码 1=是 2=否
    @SerializedName("isValidateGoogle")
    public String isValidateGoogle;//谷歌设置标示 0=未设置 1=已设置 2=已关闭
    @SerializedName("isAuthSenior")
    public int isAuthSenior;
    @SerializedName("isAuthVideo")
    public int isAuthVideo;
    @SerializedName("email")
    public String email;
    @SerializedName("isAuth")
    public int isAuth;
    @SerializedName("phone")
    public String phone;
    @SerializedName("isValidatePass")
    public int isValidatePass;
    @SerializedName("nickname")
    public String nickname;
    @SerializedName("headImg")
    public String headImg;

    public boolean getIsValidatePass() {
        if (isValidatePass == 1) {
            return true;
        } else if (isValidatePass == 0) {
            return false;
        }
        return true;
    }
}
