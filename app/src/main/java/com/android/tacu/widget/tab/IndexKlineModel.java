package com.android.tacu.widget.tab;

import com.github.tifezh.kchartlib.chart.KLineChartView;

import java.io.Serializable;

public class IndexKlineModel implements Serializable {

    public long ChartTime = KLineChartView.HOUR_1;
    //是否分时线
    public boolean isLine = false;
    //-1=隐藏 0=ma 1=boll
    public int MainView = 0;
    //-1=隐藏 0=macd 1=kdj 2=rsi 3=wr
    public int SecondView = 0;
}
