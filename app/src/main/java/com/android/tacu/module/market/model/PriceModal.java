package com.android.tacu.module.market.model;

import java.io.Serializable;

public class PriceModal implements Serializable {

    private double price;
    private double amount;
    private double all;

    public PriceModal(double price, double amount, double all) {
        this.price = price;
        this.amount = amount;
        this.all = all;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAll() {
        return all;
    }

    public void setAll(double all) {
        this.all = all;
    }
}
