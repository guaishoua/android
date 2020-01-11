package com.android.tacu.module.auctionplus.modal;

import java.io.Serializable;

/**
 * Auction记录的参数
 * Created by jiazhen on 2018/10/17.
 */
public class AuctionRecordParam implements Serializable {

    private int currencyId;//币种
    private int status;//1待支付 2已支付 3 支付过期 4 全部
    private String startTime;
    private String endTime;

    public AuctionRecordParam(int currencyId, int status) {
        this.currencyId = currencyId;
        this.status = status;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
