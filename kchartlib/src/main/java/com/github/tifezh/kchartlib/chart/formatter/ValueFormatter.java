package com.github.tifezh.kchartlib.chart.formatter;

import com.github.tifezh.kchartlib.chart.KLineChartView;
import com.github.tifezh.kchartlib.chart.base.IValueFormatter;

/**
 * Value格式化类
 * Created by tifezh on 2016/6/21.
 */
public class ValueFormatter implements IValueFormatter {
    @Override
    public String format(float value) {
        if (KLineChartView.decimalsCount > 0) {
            return String.format("%." + KLineChartView.decimalsCount + "f", value);
        }
        return String.valueOf(value);
    }
}
