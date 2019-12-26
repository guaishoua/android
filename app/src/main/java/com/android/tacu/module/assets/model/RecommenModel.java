package com.android.tacu.module.assets.model;

/**
 * Created by xiaohong on 2018/10/21.
 */

public class RecommenModel {
    private int currencyId;
    private String currencyName;
    private int currencyBaseId;
    private String currencyBaseName;
    private double rmbScale;
    private double changeRate;
    private double currentAmount;
    private int pointPrice;

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public int getCurrencyBaseId() {
        return currencyBaseId;
    }

    public void setCurrencyBaseId(int currencyBaseId) {
        this.currencyBaseId = currencyBaseId;
    }

    public String getCurrencyBaseName() {
        return currencyBaseName;
    }

    public void setCurrencyBaseName(String currencyBaseName) {
        this.currencyBaseName = currencyBaseName;
    }

    public double getRmbScale() {
        return rmbScale;
    }

    public void setRmbScale(double rmbScale) {
        this.rmbScale = rmbScale;
    }

    public double getChangeRate() {
        return changeRate;
    }

    public void setChangeRate(double changeRate) {
        this.changeRate = changeRate;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public int getPointPrice() {
        return pointPrice;
    }

    public void setPointPrice(int pointPrice) {
        this.pointPrice = pointPrice;
    }
}
