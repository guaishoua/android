package com.android.tacu.EventBus.model;

import java.io.Serializable;

/**
 * 详情页跳转
 * Created by jiazhen on 2018/9/27.
 */
public class JumpTradeCodeIsBuyEvent implements Serializable {

    private int currencyId;
    private int baseCurrencyId;
    private String currencyName;
    private String baseCurrencyNameEn;
    private boolean isBuy;

    public JumpTradeCodeIsBuyEvent(int currencyId, int baseCurrencyId, String currencyName, String baseCurrencyNameEn, boolean isBuy) {
        this.currencyId = currencyId;
        this.baseCurrencyId = baseCurrencyId;
        this.currencyName = currencyName;
        this.baseCurrencyNameEn = baseCurrencyNameEn;
        this.isBuy = isBuy;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public int getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public void setBaseCurrencyId(int baseCurrencyId) {
        this.baseCurrencyId = baseCurrencyId;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getBaseCurrencyNameEn() {
        return baseCurrencyNameEn;
    }

    public void setBaseCurrencyNameEn(String baseCurrencyNameEn) {
        this.baseCurrencyNameEn = baseCurrencyNameEn;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public void setBuy(boolean buy) {
        isBuy = buy;
    }
}
