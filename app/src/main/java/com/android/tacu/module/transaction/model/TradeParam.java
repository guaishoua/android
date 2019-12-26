package com.android.tacu.module.transaction.model;

import java.io.Serializable;

/**
 * 委托的参数
 * Created by jiazhen on 2018/10/17.
 */
public class TradeParam implements Serializable {

    public TradeParam(int currencyId, int baseCurrencyId) {
        this.currencyId = currencyId;
        this.baseCurrencyId = baseCurrencyId;
    }

    public int start = 1;
    public int size = 10;
    public int buyOrSell = 0;
    public Integer currencyId;
    public Integer baseCurrencyId;
}
