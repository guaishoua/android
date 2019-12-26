package com.android.tacu.module.market.model;

import java.io.Serializable;

/**
 * Created by jiazhen on 2018/9/26.
 */
public class MarketDepthModel implements Serializable {

    private double buyCurrent;
    private double buyNumber;
    private double buyDepth;
    private double sellCurrent;
    private double sellNumber;
    private double sellDepth;

    public double getBuyCurrent() {
        return buyCurrent;
    }

    public void setBuyCurrent(double buyCurrent) {
        this.buyCurrent = buyCurrent;
    }

    public double getBuyNumber() {
        return buyNumber;
    }

    public void setBuyNumber(double buyNumber) {
        this.buyNumber = buyNumber;
    }

    public double getBuyDepth() {
        return buyDepth;
    }

    public void setBuyDepth(double buyDepth) {
        this.buyDepth = buyDepth;
    }

    public double getSellCurrent() {
        return sellCurrent;
    }

    public void setSellCurrent(double sellCurrent) {
        this.sellCurrent = sellCurrent;
    }

    public double getSellNumber() {
        return sellNumber;
    }

    public void setSellNumber(double sellNumber) {
        this.sellNumber = sellNumber;
    }

    public double getSellDepth() {
        return sellDepth;
    }

    public void setSellDepth(double sellDepth) {
        this.sellDepth = sellDepth;
    }
}
