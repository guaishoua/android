package com.github.tifezh.kchartlib.chart.utils;

import android.content.Context;

import com.github.tifezh.kchartlib.chart.base.ChartConstant;
import com.github.tifezh.kchartlib.chart.entity.KLineEntity;
import com.github.tifezh.kchartlib.chart.entity.IndexModel;
import com.google.gson.Gson;

import java.util.List;

/**
 * 数据辅助类 计算macd rsi等
 * Created by tifezh on 2016/11/26.
 */
public class DataHelper {

    /**
     * 计算MA BOLL RSI KDJ MACD
     *
     * @param dataList
     */
    public static void calculate(List<KLineEntity> dataList, Context context) {
        String temp = SPChartUtils.getInstance(context).getString(ChartConstant.KLINE_INDEX_SETTING);
        IndexModel indexModel = new Gson().fromJson(temp, IndexModel.class);
        if (indexModel == null) {
            indexModel = new IndexModel();
        }

        calculateMA(dataList, indexModel);
        calculateBOLL(dataList, indexModel);
        calculateVolumeMA(dataList);
        calculateMACD(dataList, indexModel);
        calculateKDJ(dataList, indexModel);
        calculateRSI(dataList, indexModel);
        calculateWR(dataList, indexModel);
    }

    /**
     * 计算ma
     *
     * @param dataList
     */
    static void calculateMA(List<KLineEntity> dataList, IndexModel indexModel) {
        float ma1 = 0, ma2 = 0, ma3 = 0, ma4 = 0, ma5 = 0, ma6 = 0;
        Integer ma1Value = null, ma2Value = null, ma3Value = null, ma4Value = null, ma5Value = null, ma6Value = null;

        if (indexModel.isMa1Check && indexModel.Ma1Value != null) {
            ma1Value = indexModel.Ma1Value;
        }
        if (indexModel.isMa2Check && indexModel.Ma2Value != null) {
            ma2Value = indexModel.Ma2Value;
        }
        if (indexModel.isMa3Check && indexModel.Ma3Value != null) {
            ma3Value = indexModel.Ma3Value;
        }
        if (indexModel.isMa4Check && indexModel.Ma4Value != null) {
            ma4Value = indexModel.Ma4Value;
        }
        if (indexModel.isMa5Check && indexModel.Ma5Value != null) {
            ma5Value = indexModel.Ma5Value;
        }
        if (indexModel.isMa6Check && indexModel.Ma6Value != null) {
            ma6Value = indexModel.Ma6Value;
        }

        for (int i = 0; i < dataList.size(); i++) {
            KLineEntity point = dataList.get(i);
            final float closePrice = point.getClosePrice();

            if (ma1Value != null) {
                ma1 += closePrice;
                if (i >= ma1Value) {
                    ma1 -= dataList.get(i - ma1Value).getClosePrice();
                    point.MA1Price = ma1 / ma1Value;
                } else {
                    point.MA1Price = ma1 / (i + 1);
                }
                point.MA1Value = ma1Value;
            }
            if (ma2Value != null) {
                ma2 += closePrice;
                if (i >= ma2Value) {
                    ma2 -= dataList.get(i - ma2Value).getClosePrice();
                    point.MA2Price = ma2 / ma2Value;
                } else {
                    point.MA2Price = ma2 / (i + 1);
                }
                point.MA2Value = ma2Value;
            }
            if (ma3Value != null) {
                ma3 += closePrice;
                if (i >= ma3Value) {
                    ma3 -= dataList.get(i - ma3Value).getClosePrice();
                    point.MA3Price = ma3 / ma3Value;
                } else {
                    point.MA3Price = ma3 / (i + 1);
                }
                point.MA3Value = ma3Value;
            }
            if (ma4Value != null) {
                ma4 += closePrice;
                if (i >= ma4Value) {
                    ma4 -= dataList.get(i - ma4Value).getClosePrice();
                    point.MA4Price = ma4 / ma4Value;
                } else {
                    point.MA4Price = ma4 / (i + 1);
                }
                point.MA4Value = ma4Value;
            }
            if (ma5Value != null) {
                ma5 += closePrice;
                if (i >= ma5Value) {
                    ma5 -= dataList.get(i - ma5Value).getClosePrice();
                    point.MA5Price = ma5 / ma5Value;
                } else {
                    point.MA5Price = ma5 / (i + 1);
                }
                point.MA5Value = ma5Value;
            }
            if (ma6Value != null) {
                ma6 += closePrice;
                if (i >= ma6Value) {
                    ma6 -= dataList.get(i - ma6Value).getClosePrice();
                    point.MA6Price = ma6 / ma6Value;
                } else {
                    point.MA6Price = ma6 / (i + 1);
                }
                point.MA6Value = ma6Value;
            }
        }
    }

    /**
     * 计算 BOLL
     *
     * @param dataList
     */
    static void calculateBOLL(List<KLineEntity> dataList, IndexModel indexModel) {
        Integer NValue = indexModel.BOLLNValue;
        Integer PValue = indexModel.BOLLPValue;
        float maN = 0;

        if (NValue != null && PValue != null) {
            for (int i = 0; i < dataList.size(); i++) {
                KLineEntity point = dataList.get(i);
                final float closePrice = point.getClosePrice();

                maN += closePrice;
                if (i >= NValue) {
                    maN -= dataList.get(i - NValue).getClosePrice();
                    point.MANPrice = maN / NValue;
                } else {
                    point.MANPrice = maN / (i + 1);
                }

                if (i == 0) {
                    point.boll = closePrice;
                    point.ub = Float.NaN;
                    point.lb = Float.NaN;
                } else {
                    int n = NValue;
                    if (i < NValue) {
                        n = i + 1;
                    }
                    float md = 0;
                    for (int j = i - n + 1; j <= i; j++) {
                        float c = dataList.get(j).getClosePrice();
                        float m = point.getMANPrice();
                        float value = c - m;
                        md += value * value;
                    }
                    md = md / (n - 1);
                    md = (float) Math.sqrt(md);
                    point.boll = point.getMANPrice();
                    point.ub = point.boll + PValue * md;
                    point.lb = point.boll - PValue * md;
                }
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
                entry.MA5Volume = (volumeMa5 / 5);
            } else {
                entry.MA5Volume = (volumeMa5 / (i + 1));
            }

            if (i >= 10) {
                volumeMa10 -= entries.get(i - 10).getVolume();
                if (volumeMa10 < 0) {
                    volumeMa10 = 0;
                }
                entry.MA10Volume = (volumeMa10 / 10);
            } else {
                entry.MA10Volume = (volumeMa10 / (i + 1));
            }
        }
    }

    /**
     * 计算macd
     *
     * @param dataList
     */
    static void calculateMACD(List<KLineEntity> dataList, IndexModel indexModel) {
        Integer SValue = indexModel.MACDSValue;
        Integer LValue = indexModel.MACDLValue;
        Integer MValue = indexModel.MACDMValue;

        float emaS = 0, emaL = 0;
        float dif = 0, dea = 0, macd = 0;

        if (SValue != null && LValue != null && MValue != null) {
            for (int i = 0; i < dataList.size(); i++) {
                KLineEntity point = dataList.get(i);
                final float closePrice = point.getClosePrice();
                if (i == 0) {
                    emaS = closePrice;
                    emaL = closePrice;
                } else {
                    // EMA（12） = 前一日EMA（12） X 11/13 + 今日收盘价 X 2/13
                    emaS = emaS * (SValue - 1) / (SValue + 1) + closePrice * 2 / (SValue + 1);
                    // EMA（26） = 前一日EMA（26） X 25/27 + 今日收盘价 X 2/27
                    emaL = emaL * (LValue - 1) / (LValue + 1) + closePrice * 2 / (LValue + 1);
                }
                // DIF = EMA（12） - EMA（26） 。
                // 今日DEA = （前一日DEA X 8/10 + 今日DIF X 2/10）
                // 用（DIF-DEA）*2即为MACD柱状图。
                dif = emaS - emaL;
                dea = dea * (MValue - 1) / (MValue + 1) + dif * 2 / (MValue + 1);
                macd = (dif - dea) * 2;
                point.dif = dif;
                point.dea = dea;
                point.macd = macd;
                point.MACDSValue = SValue;
                point.MACDLValue = LValue;
                point.MACDMValue = MValue;
            }
        }
    }

    /**
     * 计算kdj
     *
     * @param dataList
     */
    static void calculateKDJ(List<KLineEntity> dataList, IndexModel indexModel) {
        Integer NValue = indexModel.KDJNValue;
        Integer M1Value = indexModel.KDJM1Value;
        Integer M2Value = indexModel.KDJM2Value;

        float k = 50F;
        float d = 50F;
        float j = 0;
        float rsv = 0;

        if (NValue != null && M1Value != null && M2Value != null) {
            for (int i = 0; i < dataList.size(); i++) {
                KLineEntity point = dataList.get(i);
                final float closePrice = point.getClosePrice();
                int startIndex = i - (NValue - 1);
                if (startIndex < 0) {
                    startIndex = 0;
                }
                float maxN = dataList.get(startIndex).getHighPrice();
                float minN = dataList.get(startIndex).getLowPrice();
                for (int index = startIndex; index <= i; index++) {
                    maxN = Math.max(maxN, dataList.get(index).getHighPrice());
                    minN = Math.min(minN, dataList.get(index).getLowPrice());
                }
                if (maxN != minN) {
                    rsv = 100f * (closePrice - minN) / (maxN - minN);
                } else {
                    rsv = 0;
                }

                k = (rsv + (M1Value - 1) * k) / M1Value;
                d = (k + (M2Value - 1) * d) / M2Value;
                j = 3 * k - 2 * d;

                point.k = k;
                point.d = d;
                point.j = j;
                point.KNValue = NValue;
                point.KM1Value = M1Value;
                point.KM2Value = M2Value;
            }
        }
    }

    /**
     * 计算RSI
     *
     * @param dataList
     */
    static void calculateRSI(List<KLineEntity> dataList, IndexModel indexModel) {
        float rsi1 = 0, rsi2 = 0, rsi3 = 0;
        float rsi1ABSEma = 0, rsi2ABSEma = 0, rsi3ABSEma = 0;
        float rsi1MaxEma = 0, rsi2MaxEma = 0, rsi3MaxEma = 0;
        Integer rsi1Value = null, rsi2Value = null, rsi3Value = null;

        if (indexModel.isRSI1Check && indexModel.RSI1Value != null) {
            rsi1Value = indexModel.RSI1Value;
        }
        if (indexModel.isRSI2Check && indexModel.RSI2Value != null) {
            rsi2Value = indexModel.RSI2Value;
        }
        if (indexModel.isRSI3Check && indexModel.RSI3Value != null) {
            rsi3Value = indexModel.RSI3Value;
        }

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

                if (rsi1Value != null) {
                    rsi1MaxEma = (Rmax + (rsi1Value - 1) * rsi1MaxEma) / rsi1Value;
                    rsi1ABSEma = (RAbs + (rsi1Value - 1) * rsi1ABSEma) / rsi1Value;
                    rsi1 = (rsi1MaxEma / rsi1ABSEma) * 100;
                    point.rsi1 = rsi1;
                    point.rsi1Value = rsi1Value;
                }
                if (rsi2Value != null) {
                    rsi2MaxEma = (Rmax + (rsi2Value - 1) * rsi2MaxEma) / rsi2Value;
                    rsi2ABSEma = (RAbs + (rsi2Value - 1) * rsi2ABSEma) / rsi2Value;
                    rsi2 = (rsi2MaxEma / rsi2ABSEma) * 100;
                    point.rsi2 = rsi2;
                    point.rsi2Value = rsi2Value;
                }
                if (rsi3Value != null) {
                    rsi3MaxEma = (Rmax + (rsi3Value - 1) * rsi3MaxEma) / rsi3Value;
                    rsi3ABSEma = (RAbs + (rsi3Value - 1) * rsi3ABSEma) / rsi3Value;
                    rsi3 = (rsi3MaxEma / rsi3ABSEma) * 100;
                    point.rsi3 = rsi3;
                    point.rsi3Value = rsi3Value;
                }
            }
        }
    }

    /**
     * 计算wr
     *
     * @param dataList
     */
    static void calculateWR(List<KLineEntity> dataList, IndexModel indexModel) {
        float wr1 = 0, wr2 = 0, wr3 = 0;
        Integer wr1Value = null, wr2Value = null, wr3Value = null;

        if (indexModel.isWR1Check && indexModel.WR1Value != null) {
            wr1Value = indexModel.WR1Value;
        }
        if (indexModel.isWR2Check && indexModel.WR2Value != null) {
            wr2Value = indexModel.WR2Value;
        }
        if (indexModel.isWR3Check && indexModel.WR3Value != null) {
            wr3Value = indexModel.WR3Value;
        }

        for (int i = 0; i < dataList.size(); i++) {
            KLineEntity point = dataList.get(i);
            final float closePrice = point.getClosePrice();

            if (wr1Value != null) {
                //WR1一般是10天买卖强弱指标
                int startIndexWR1 = i - (wr1Value - 1);
                if (startIndexWR1 < 0) {
                    startIndexWR1 = 0;
                }
                float maxWR1 = dataList.get(startIndexWR1).getHighPrice();
                float minWR1 = dataList.get(startIndexWR1).getLowPrice();
                for (int index = startIndexWR1; index <= i; index++) {
                    maxWR1 = Math.max(maxWR1, dataList.get(index).getHighPrice());
                    minWR1 = Math.min(minWR1, dataList.get(index).getLowPrice());
                }
                if (maxWR1 != minWR1) {
                    wr1 = 100 * (maxWR1 - closePrice) / (maxWR1 - minWR1);
                } else {
                    wr1 = 0;
                }
                point.wr1 = wr1;
                point.wr1Value = wr1Value;
            }
            if (wr2Value != null) {
                //WR2一般是6天买卖强弱指标
                int startIndexWR2 = i - (wr2Value - 1);
                if (startIndexWR2 < 0) {
                    startIndexWR2 = 0;
                }
                float maxWR2 = dataList.get(startIndexWR2).getHighPrice();
                float minWR2 = dataList.get(startIndexWR2).getLowPrice();
                for (int index = startIndexWR2; index <= i; index++) {
                    maxWR2 = Math.max(maxWR2, dataList.get(index).getHighPrice());
                    minWR2 = Math.min(minWR2, dataList.get(index).getLowPrice());
                }
                if (maxWR2 != minWR2) {
                    wr2 = 100 * (maxWR2 - closePrice) / (maxWR2 - minWR2);
                } else {
                    wr2 = 0;
                }
                point.wr2 = wr2;
                point.wr2Value = wr2Value;
            }
            if (wr3Value != null) {
                //WR3一般是4天买卖强弱指标
                int startIndexWR3 = i - (wr3Value - 1);
                if (startIndexWR3 < 0) {
                    startIndexWR3 = 0;
                }
                float maxWR3 = dataList.get(startIndexWR3).getHighPrice();
                float minWR3 = dataList.get(startIndexWR3).getLowPrice();
                for (int index = startIndexWR3; index <= i; index++) {
                    maxWR3 = Math.max(maxWR3, dataList.get(index).getHighPrice());
                    minWR3 = Math.min(minWR3, dataList.get(index).getLowPrice());
                }
                if (maxWR3 != minWR3) {
                    wr3 = 100 * (maxWR3 - closePrice) / (maxWR3 - minWR3);
                } else {
                    wr3 = 0;
                }
                point.wr3 = wr3;
                point.wr3Value = wr3Value;
            }
        }
    }
}
