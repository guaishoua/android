package com.github.tifezh.kchartlib.chart.entity;

import java.io.Serializable;

public class IndexModel implements Serializable {

    // MA
    public boolean isMa1Check = true;
    public Integer Ma1Value = 5;

    public boolean isMa2Check = true;
    public Integer Ma2Value = 10;

    public boolean isMa3Check = true;
    public Integer Ma3Value = 30;

    public boolean isMa4Check = false;
    public Integer Ma4Value = null;

    public boolean isMa5Check = false;
    public Integer Ma5Value = null;

    public boolean isMa6Check = false;
    public Integer Ma6Value = null;

    // BOLL
    public Integer BOLLNValue = 20;
    public Integer BOLLPValue = 2;

    // MACD
    public Integer MACDSValue = 12;
    public Integer MACDLValue = 26;
    public Integer MACDMValue = 9;

    // KDJ
    public Integer KDJNValue = 14;
    public Integer KDJM1Value = 1;
    public Integer KDJM2Value = 3;

    // RSI
    public boolean isRSI1Check = true;
    public Integer RSI1Value = 14;

    public boolean isRSI2Check = false;
    public Integer RSI2Value = null;

    public boolean isRSI3Check = false;
    public Integer RSI3Value = null;

    // WR
    public boolean isWR1Check = true;
    public Integer WR1Value = 14;

    public boolean isWR2Check = false;
    public Integer WR2Value = null;

    public boolean isWR3Check = false;
    public Integer WR3Value = null;
}
