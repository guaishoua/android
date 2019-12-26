package com.android.tacu.module.transaction.model;

import java.io.Serializable;

/**
 * 委托的参数
 * Created by jiazhen on 2018/10/17.
 */
public class OrderParam implements Serializable {

    public OrderParam(int currencyId, int baseCurrencyId) {
        this.currencyId = currencyId;
        this.baseCurrencyId = baseCurrencyId;
    }

    public OrderParam(int start, int size, String status, int buyOrSell, Integer currencyId, Integer baseCurrencyId) {
        this.start = start;
        this.size = size;
        this.status = status;
        this.buyOrSell = buyOrSell;
        this.currencyId = currencyId;
        this.baseCurrencyId = baseCurrencyId;
    }

    public int start = 1;
    public int size = 10;
    /**
     * |0         |未成交   |  string |
     * |1         |部分成交   |  string |
     * |0，1      |表示当前委托   |  string |
     * |2         |表示全部成交   |  string |
     * |4         |表示已撤单     |  string |
     * |5         |部分成交后撤单 |  string |
     * |2，4，5   |表示历史委托全部   |  string |
     */
    public String status;
    /**
     * |0        |全部方向   |  int |
     * |1        |买入   |  int |
     * |2        |卖出   |  int |
     */
    public int buyOrSell = 0;
    public Integer currencyId;
    public Integer baseCurrencyId;
}