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

public class MoneyFlowEvent implements Serializable {
    private String type;
    private String typeValue;
    private Integer currencyId = 0;
    private String currencyNameEn;
    private String startTime;
    private String endTime;

    public MoneyFlowEvent() {
    }

    public MoneyFlowEvent(String type, String typeValue, Integer currencyId, String currencyNameEn, String startTime, String endTime) {
        this.type = type;
        this.typeValue = typeValue;
        this.currencyId = currencyId;
        this.currencyNameEn = currencyNameEn;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
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
                "type=" + type +
                ", typeValue='" + typeValue + '\'' +
                ", currencyId=" + currencyId +
                ", currencyNameEn='" + currencyNameEn + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
