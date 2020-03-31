package com.android.tacu.widget.tab;

import java.io.Serializable;

public class IndexKlineModel implements Serializable {

    public long ChartTime = TabLayoutView.DAY_1;
    //是否分时线
    public boolean isLine = false;
    //null=隐藏 0=ma 1=boll
    public Integer MainView = 0;
    //null=隐藏 0=macd 1=kdj 2=rsi 3=wr
    public Integer SecondView = 0;
}
