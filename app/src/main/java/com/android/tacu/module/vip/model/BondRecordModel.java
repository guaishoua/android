package com.android.tacu.module.vip.model;

import android.support.v4.content.ContextCompat;

import com.android.tacu.R;
import com.android.tacu.base.MyApplication;

import java.io.Serializable;

public class BondRecordModel implements Serializable {

    public String id;
    public String uuid;
    public String uid;
    public Integer currencyId;
    public String currencyName;
    public Integer type;// 1转入 2转出
    public Integer status;//0失败 1审核中 2成功
    public String amount;
    public String createTime;
    public String updateTime;

    public String getType() {
        if (type == 1) {
            return MyApplication.getInstance().getString(R.string.home_recharge);
        } else if (type == 2) {
            return MyApplication.getInstance().getString(R.string.extract);
        } else {
            return "";
        }
    }

    public int getTypeColor() {
        if (type == 1) {
            return ContextCompat.getColor(MyApplication.getInstance(), R.color.color_otc_buy);
        } else if (type == 2) {
            return ContextCompat.getColor(MyApplication.getInstance(), R.color.color_otc_sell);
        } else {
            return ContextCompat.getColor(MyApplication.getInstance(), R.color.text_color);
        }
    }

    public String getStatus() {
        if (type == 0) {
            return MyApplication.getInstance().getString(R.string.failure);
        } else if (type == 1) {
            return MyApplication.getInstance().getString(R.string.auditing);
        } else if (type == 2) {
            return MyApplication.getInstance().getString(R.string.success_get_account);
        } else {
            return "";
        }
    }

    public int getStatusColor() {
        if (type == 0) {
            return ContextCompat.getColor(MyApplication.getInstance(), R.color.text_color);
        } else if (type == 1) {
            return ContextCompat.getColor(MyApplication.getInstance(), R.color.color_otc_sell);
        } else if (type == 2) {
            return ContextCompat.getColor(MyApplication.getInstance(), R.color.text_color);
        } else {
            return ContextCompat.getColor(MyApplication.getInstance(), R.color.text_color);
        }
    }
}
