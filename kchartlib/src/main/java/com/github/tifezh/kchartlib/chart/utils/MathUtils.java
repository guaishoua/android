package com.github.tifezh.kchartlib.chart.utils;

import com.github.tifezh.kchartlib.chart.KLineChartView;

import java.math.BigDecimal;

public class MathUtils {

    public static String getFormatValue(double value) {
        try {
            return BigDecimal.valueOf(value).toPlainString();
        } catch (Exception e) {
            return String.valueOf(value);
        }
    }

    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).setScale(KLineChartView.decimalsCount, BigDecimal.ROUND_UP).doubleValue();
    }

    public static String div(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        BigDecimal divideDouble = b1.divide(b2, 4, BigDecimal.ROUND_UP);
        return divideDouble.multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_UP).doubleValue() + "%";
    }
}
