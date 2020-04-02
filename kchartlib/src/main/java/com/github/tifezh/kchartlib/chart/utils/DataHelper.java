package com.github.tifezh.kchartlib.chart.utils;

import android.content.Context;

import com.github.tifezh.kchartlib.chart.base.ChartConstant;
import com.github.tifezh.kchartlib.chart.entity.KLineEntity;
import com.github.tifezh.kchartlib.chart.view.IndexModel;
import com.google.gson.Gson;

import java.util.List;

/**
 * 数据辅助类 计算macd rsi等
 * Created by tifezh on 2016/11/26.
 */
public class DataHelper {

    /**
     * 计算ma
     *
     * @param dataList
     */
    static void calculateMA(List<KLineEntity> dataList, IndexModel indexModel) {
        float ma5 = 0;
        float ma10 = 0;
        float ma20 = 0;
        float ma30 = 0;
        float ma60 = 0;

        for (int i = 0; i < dataList.size(); i++) {
            KLineEntity point = dataList.get(i);
            final float closePrice = point.getClosePrice();

            ma5 += closePrice;
            ma10 += closePrice;
            ma20 += closePrice;
            ma30 += closePrice;
            ma60 += closePrice;
            if (i >= 5) {
                ma5 -= dataList.get(i - 5).getClosePrice();
                point.MA5Price = ma5 / 5f;
            } else {
                point.MA5Price = ma5 / (i + 1f);
            }
            if (i >= 10) {
                ma10 -= dataList.get(i - 10).getClosePrice();
                point.MA10Price = ma10 / 10f;
            } else {
                point.MA10Price = ma10 / (i + 1f);
            }
            if (i >= 20) {
                ma20 -= dataList.get(i - 20).getClosePrice();
                point.MA20Price = ma20 / 20f;
            } else {
                point.MA20Price = ma20 / (i + 1f);
            }
            if (i >= 30) {
                ma30 -= dataList.get(i - 30).getClosePrice();
                point.MA30Price = ma30 / 30f;
            } else {
                point.MA30Price = ma30 / (i + 1f);
            }
            if (i >= 60) {
                ma60 -= dataList.get(i - 60).getClosePrice();
                point.MA60Price = ma60 / 60f;
            } else {
                point.MA60Price = ma60 / (i + 1f);
            }
        }
    }

    private static void calculateVolumeMA(List<KLineEntity> entries) {
        float volumeMa5 = 0;
        float volumeMa10 = 0;

        for (int i = 0; i < entries.size(); i++) {
            KLineEntity entry = entries.get(i);

            volumeMa5 += entry.getVolume();
            volumeMa10 += entry.getVolume();

            if (i >= 5) {
                volumeMa5 -= entries.get(i - 5).getVolume();
                if (volumeMa5 < 0) {
                    volumeMa5 = 0;
                }
                entry.MA5Volume = (volumeMa5 / 5f);
            } else {
                entry.MA5Volume = (volumeMa5 / (i + 1f));
            }

            if (i >= 10) {
                volumeMa10 -= entries.get(i - 10).getVolume();
                if (volumeMa10 < 0) {
                    volumeMa10 = 0;
                }
                entry.MA10Volume = (volumeMa10 / 10f);
            } else {
                entry.MA10Volume = (volumeMa10 / (i + 1f));
            }
        }
    }

    /**
     * 计算macd
     *
     * @param dataList
     */
    static void calculateMACD(List<KLineEntity> dataList) {
        float ema12 = 0;
        float ema26 = 0;
        float dif = 0;
        float dea = 0;
        float macd = 0;

        for (int i = 0; i < dataList.size(); i++) {
            KLineEntity point = dataList.get(i);
            final float closePrice = point.getClosePrice();
            if (i == 0) {
                ema12 = closePrice;
                ema26 = closePrice;
            } else {
                // EMA（12） = 前一日EMA（12） X 11/13 + 今日收盘价 X 2/13
                ema12 = ema12 * 11f / 13f + closePrice * 2f / 13f;
                // EMA（26） = 前一日EMA（26） X 25/27 + 今日收盘价 X 2/27
                ema26 = ema26 * 25f / 27f + closePrice * 2f / 27f;
            }
            // DIF = EMA（12） - EMA（26） 。
            // 今日DEA = （前一日DEA X 8/10 + 今日DIF X 2/10）
            // 用（DIF-DEA）*2即为MACD柱状图。
            dif = ema12 - ema26;
            dea = dea * 8f / 10f + dif * 2f / 10f;
            macd = (dif - dea) * 2f;
            point.dif = dif;
            point.dea = dea;
            point.macd = macd;
        }
    }

    /**
     * 计算kdj
     *
     * @param dataList
     */
    static void calculateKDJ(List<KLineEntity> dataList) {
        float k = 50.0f;
        float d = 50.0f;
        float j = 0.0f;
        float rsv = 0.0f;

        for (int i = 0; i < dataList.size(); i++) {
            KLineEntity point = dataList.get(i);
            final float closePrice = point.getClosePrice();
            int startIndex = i - 8;
            if (startIndex < 0) {
                startIndex = 0;
            }
            float max9 = dataList.get(startIndex).getHighPrice();
            float min9 = dataList.get(startIndex).getLowPrice();
            for (int index = startIndex; index <= i; index++) {
                max9 = Math.max(max9, dataList.get(index).getHighPrice());
                min9 = Math.min(min9, dataList.get(index).getLowPrice());
            }
            if (max9 != min9) {
                rsv = 100f * (closePrice - min9) / (max9 - min9);
            } else {
                rsv = 0;
            }

            k = (rsv + 2f * k) / 3f;
            d = (k + 2f * d) / 3f;
            j = 3f * k - 2 * d;

            point.k = k;
            point.d = d;
            point.j = j;
        }
    }

    /**
     * 计算RSI
     *
     * @param dataList
     */
    static void calculateRSI(List<KLineEntity> dataList) {
        float rsi1 = 0;
        float rsi2 = 0;
        float rsi3 = 0;
        float rsi1ABSEma = 0;
        float rsi2ABSEma = 0;
        float rsi3ABSEma = 0;
        float rsi1MaxEma = 0;
        float rsi2MaxEma = 0;
        float rsi3MaxEma = 0;
        for (int i = 0; i < dataList.size(); i++) {
            KLineEntity point = dataList.get(i);
            final float closePrice = point.getClosePrice();
            if (i == 0) {
                rsi1 = 0;
                rsi2 = 0;
                rsi3 = 0;
                rsi1ABSEma = 0;
                rsi2ABSEma = 0;
                rsi3ABSEma = 0;
                rsi1MaxEma = 0;
                rsi2MaxEma = 0;
                rsi3MaxEma = 0;
            } else {
                float Rmax = Math.max(0, closePrice - dataList.get(i - 1).getClosePrice());
                float RAbs = Math.abs(closePrice - dataList.get(i - 1).getClosePrice());
                rsi1MaxEma = (Rmax + (6f - 1) * rsi1MaxEma) / 6f;
                rsi1ABSEma = (RAbs + (6f - 1) * rsi1ABSEma) / 6f;

                rsi2MaxEma = (Rmax + (12f - 1) * rsi2MaxEma) / 12f;
                rsi2ABSEma = (RAbs + (12f - 1) * rsi2ABSEma) / 12f;

                rsi3MaxEma = (Rmax + (24f - 1) * rsi3MaxEma) / 24f;
                rsi3ABSEma = (RAbs + (24f - 1) * rsi3ABSEma) / 24f;

                rsi1 = (rsi1MaxEma / rsi1ABSEma) * 100;
                rsi2 = (rsi2MaxEma / rsi2ABSEma) * 100;
                rsi3 = (rsi3MaxEma / rsi3ABSEma) * 100;
            }
            point.rsi = rsi1;
            //point.rsi2 = rsi2;
            //point.rsi3 = rsi3;
        }
    }

    /**
     * 计算 BOLL 需要在计算ma之后进行
     *
     * @param dataList
     */
    static void calculateBOLL(List<KLineEntity> dataList) {
        for (int i = 0; i < dataList.size(); i++) {
            KLineEntity point = dataList.get(i);
            final float closePrice = point.getClosePrice();
            if (i == 0) {
                point.mb = closePrice;
                point.up = Float.NaN;
                point.dn = Float.NaN;
            } else {
                int n = 20;
                if (i < 20) {
                    n = i + 1;
                }
                float md = 0;
                for (int j = i - n + 1; j <= i; j++) {
                    float c = dataList.get(j).getClosePrice();
                    float m = point.getMA20Price();
                    float value = c - m;
                    md += value * value;
                }
                md = md / (n - 1);
                md = (float) Math.sqrt(md);
                point.mb = point.getMA20Price();
                point.up = point.mb + 2f * md;
                point.dn = point.mb - 2f * md;
            }
        }
    }

    /**
     * 计算wr
     *
     * @param dataList
     */
    static void calculateWR(List<KLineEntity> dataList) {
        float wr1 = 0.0f;
        float wr2 = 0.0f;
        for (int i = 0; i < dataList.size(); i++) {
            KLineEntity point = dataList.get(i);
            final float closePrice = point.getClosePrice();

            //WR1一般是10天买卖强弱指标
            int startIndex10 = i - 9;
            if (startIndex10 < 0) {
                startIndex10 = 0;
            }
            float max10 = dataList.get(startIndex10).getHighPrice();
            float min10 = dataList.get(startIndex10).getLowPrice();
            for (int index = startIndex10; index <= i; index++) {
                max10 = Math.max(max10, dataList.get(index).getHighPrice());
                min10 = Math.min(min10, dataList.get(index).getLowPrice());
            }
            if (max10 != min10) {
                wr1 = 100f * (max10 - closePrice) / (max10 - min10);
            } else {
                wr1 = 0.0f;
            }
            point.r = wr1;

            //WR2一般是6天买卖强弱指标
            int startIndex6 = i - 5;
            if (startIndex6 < 0) {
                startIndex6 = 0;
            }
            float max6 = dataList.get(startIndex6).getHighPrice();
            float min6 = dataList.get(startIndex6).getLowPrice();
            for (int index = startIndex6; index <= i; index++) {
                max6 = Math.max(max6, dataList.get(index).getHighPrice());
                min6 = Math.min(min6, dataList.get(index).getLowPrice());
            }
            if (max6 != min6) {
                wr2 = 100f * (max6 - closePrice) / (max6 - min6);
            } else {
                wr2 = 0.0f;
            }
            //point.WR2 = wr2;
        }
    }

    /**
     * 计算MA BOLL RSI KDJ MACD
     *
     * @param dataList
     */
    public static void calculate(List<KLineEntity> dataList, Context context) {
        String temp = SPChartUtils.getInstance(context).getString(ChartConstant.KLINE_INDEX_SETTING);
        IndexModel indexModel = new Gson().fromJson(temp, IndexModel.class);

        calculateMA(dataList);
        calculateVolumeMA(dataList);
        calculateMACD(dataList);
        calculateKDJ(dataList);
        calculateRSI(dataList);
        calculateBOLL(dataList);
        calculateWR(dataList);
    }
}
