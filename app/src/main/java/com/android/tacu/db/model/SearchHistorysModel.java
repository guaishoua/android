package com.android.tacu.db.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 搜索的历史记录
 * Created by jiazhen on 2018/5/12.
 */
@Entity
public class SearchHistorysModel {

    @Id(autoincrement = true)//自增 大写的Long 可以传null 表示自增
    private Long id;
    private int uid;
    private int currencyId;
    private int baseCurrencyId;
    private String currencyNameEn;
    private String baseCurrencyNameEn;

    @Generated(hash = 1094915502)
    public SearchHistorysModel(Long id, int uid, int currencyId, int baseCurrencyId, String currencyNameEn, String baseCurrencyNameEn) {
        this.id = id;
        this.uid = uid;
        this.currencyId = currencyId;
        this.baseCurrencyId = baseCurrencyId;
        this.currencyNameEn = currencyNameEn;
        this.baseCurrencyNameEn = baseCurrencyNameEn;
    }

    @Generated(hash = 2018846985)
    public SearchHistorysModel() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getUid() {
        return this.uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getCurrencyId() {
        return this.currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public int getBaseCurrencyId() {
        return this.baseCurrencyId;
    }

    public void setBaseCurrencyId(int baseCurrencyId) {
        this.baseCurrencyId = baseCurrencyId;
    }

    public String getCurrencyNameEn() {
        return this.currencyNameEn;
    }

    public void setCurrencyNameEn(String currencyNameEn) {
        this.currencyNameEn = currencyNameEn;
    }

    public String getBaseCurrencyNameEn() {
        return this.baseCurrencyNameEn;
    }

    public void setBaseCurrencyNameEn(String baseCurrencyNameEn) {
        this.baseCurrencyNameEn = baseCurrencyNameEn;
    }
}
