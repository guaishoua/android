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
    public Integer type;// 1币币到保证金  2保证金到币币 3otc到保证金 4保证金到otc
    public Integer status;//1审核中 2成功 3失败 4撤销
    public String amount;
    public String createTime;
    public String updateTime;

    public String getType() {
        if (type == 1) {
            return MyApplication.getInstance().getString(R.string.recharge_deposit_text1);
        } else if (type == 2) {
            return MyApplication.getInstance().getString(R.string.recharge_deposit_text3);
        } else if (type == 3) {
            return MyApplication.getInstance().getString(R.string.recharge_deposit_text2);
        } else if (type == 4) {
            return MyApplication.getInstance().getString(R.string.recharge_deposit_text4);
        }
        return "";
    }

    public int getTypeColor() {
        if (type == 1 || type == 3) {
            return ContextCompat.getColor(MyApplication.getInstance(), R.color.color_otc_buy);
        } else if (type == 2 || type == 4) {
            return ContextCompat.getColor(MyApplication.getInstance(), R.color.color_otc_sell);
        } else {
            return ContextCompat.getColor(MyApplication.getInstance(), R.color.text_color);
        }
    }

    public String getStatus() {
        if (status == 1) {
            return MyApplication.getInstance().getString(R.string.auditing);
        } else if (status == 2) {
            return MyApplication.getInstance().getString(R.string.success_get_account);
        } else if (status == 3) {
            return MyApplication.getInstance().getString(R.string.failure);
        } else if (status == 4) {
            return MyApplication.getInstance().getString(R.string.order_cancel);
        }
        return "";
    }

    public int getStatusColor() {
        if (status == 1) {
            return ContextCompat.getColor(MyApplication.getInstance(), R.color.color_otc_sell);
        } else {
            return ContextCompat.getColor(MyApplication.getInstance(), R.color.text_color);
        }
    }
}
