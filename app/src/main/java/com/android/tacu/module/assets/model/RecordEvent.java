package com.android.tacu.module.assets.model;

import java.io.Serializable;

/**
 * ================================
 *
 * @作者: 魏玉帅
 * @创建日期:2017/7/25
 * @版本 1.0
 * @描述: ================================
 */

public class RecordEvent implements Serializable {
    private Integer currencyId;
    private String currencyNameEn;
    private String startTime;
    private String endTime;

    public RecordEvent(Integer currencyId, String currencyNameEn, String startTime, String endTime) {
        this.currencyId = currencyId;
        this.currencyNameEn = currencyNameEn;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public String getCurrencyNameEn() {
        return currencyNameEn;
    }

    public void setCurrencyNameEn(String currencyNameEn) {
        this.currencyNameEn = currencyNameEn;
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

    @Override
    public String toString() {
        return "RecordEvent{" +
                ", currencyId=" + currencyId +
                ", currencyNameEn='" + currencyNameEn + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
