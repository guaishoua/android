package com.android.tacu.module.assets.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xiaohong on 2018/11/15.
 */

public class TransferInfo implements Serializable {
    @SerializedName("switch_info")
    public SwitchInfo swittchInfo;
    @SerializedName("user_info")
    public UserInfo userInfo;
    @SerializedName("config_info")
    public ConfigInfo configInfo;

    public static class UserInfo implements Serializable {
        @SerializedName("balance")
        public double balance;
        @SerializedName("account_list")
        public List<AccountList> accountList;
    }

    public static class AccountList{
        @SerializedName("account")
        public String account;
        @SerializedName("note")
        public String note;
    }

    public static class ConfigInfoDestails implements Serializable {
        @SerializedName("min_trans_count")
        public double minCount;
        @SerializedName("fee_type")
        public int feeType;//1:固定额度，2:百分比
        @SerializedName("fee")
        public double fee;
    }

    public static class SwitchInfo {
        @SerializedName("in")// 转入开关 2:表示关闭划转功能，1:表示开启
        public int in;
        @SerializedName("out")//转出开关 2:表示关闭划转功能，1:表示开启
        public int out;
    }

    public static class ConfigInfo {
        @SerializedName("in")
        public ConfigInfoDestails in;
        @SerializedName("out")
        public ConfigInfoDestails out;
    }
}
