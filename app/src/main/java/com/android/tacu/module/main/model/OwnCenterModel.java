package com.android.tacu.module.main.model;

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

    public int uid;
    public String fdPwdOrderEnabled;//是否每次输入交易密码 1=是 2=否
    public String isValidateGoogle;//谷歌设置标示 0=未设置 1=已设置 2=已关闭
    public int isAuthPrimary;
    public int isAuthSenior;
    public int isAuthVideo;
    public Object authLevel;
    public Object uname;
    public String email;
    public int isAuth;
    public int chexDed;//（chex抵扣手续开关）1:表示开启， 2:表示关闭
    public String phone;
    public int isValidatePhone;
    public int isValidatePass;
    public int isValidateEmail;
    public AddressBean address;
    public UserLoginRecordBean userLoginRecord;
    public Object score;
    public int hasAddress;
    public Object name;
    public Object idNumber;
    public Object nation;
    public Object gender;
    public Object birthday;
    public Object usualAddress;
    public Object positiveImages;
    public Object oppositeImages;
    public Object handImages;
    public Object authFailReason;
    public Object reward;
    public Object hidedPhone;
    public Object hidedEmail;
    public String country;

    public boolean getIsValidatePass() {
        if (isValidatePass == 1) {
            return true;
        } else if (isValidatePass == 0) {
            return false;
        }
        return true;
    }

    public boolean getChexDed() {
        if (chexDed == 1) {
            return true;
        } else if (chexDed == 2) {
            return false;
        }
        return false;
    }

    public static class AddressBean {
        /**
         * uaid : null
         * country : null
         * city : null
         * town : null
         * location : null
         * phone : null
         * name : null
         * postCode : null
         * isDefault : null
         * phonePwd : MTUwOTMwODYyMTk=
         * <p>
         * email : null
         */

        public Object uaid;
        public Object country;
        public Object city;
        public Object town;
        public Object location;
        public Object phone;
        public Object name;
        public Object postCode;
        public Object isDefault;
        public String phonePwd;
        public Object email;
    }

    public static class UserLoginRecordBean {
        /**
         * start : 1
         * size : 20
         * total : 0
         * orderBy : desc
         * ulrId : 194149
         * time : 2018-10-24 15:19:16
         * host : 118.194.240.71
         * location : Beijing,Beijing,China
         * source : 2
         * status : 1
         * type : null
         */

        public int start;
        public int size;
        public int total;
        public String orderBy;
        public String ulrId;
        public String time;
        public String host;
        public String location;
        public int source;
        public int status;
        public Object type;
    }
}
