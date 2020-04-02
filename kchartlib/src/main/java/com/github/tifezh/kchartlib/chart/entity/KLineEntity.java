package com.github.tifezh.kchartlib.chart.entity;

/**
 * K线实体
 * Created by tifezh on 2016/5/16.
 */
public class KLineEntity implements IKLine {

    public String Date;
    public float Open;
    public float High;
    public float Low;
    public float Close;

    // MA--
    public Float MA1Price;
    public Float MA2Price;
    public Float MA3Price;
    public Float MA4Price;
    public Float MA5Price;
    public Float MA6Price;

    // BOLL
    public float ub;
    public float boll;
    public float lb;

    // VOL
    public float Volume;
    public float MA5Volume;
    public float MA10Volume;

    // MACD
    public float macd;
    public float dif;
    public float dea;

    // KDJ
    public float k;
    public float d;
    public float j;

    // RSI--
    public Float rsi1;
    public Float rsi2;
    public Float rsi3;

    // WR--
    public Float wr1;
    public Float wr2;
    public Float wr3;

    public String getDate() {
        return Date;
    }


    @Override
    public float getOpenPrice() {
        return Open;
    }

    @Override
    public float getHighPrice() {
        return High;
    }

    @Override
    public float getLowPrice() {
        return Low;
    }

    @Override
    public float getClosePrice() {
        return Close;
    }

    @Override
    public Float getMA1Price() {
        return MA1Price;
    }

    @Override
    public Float getMA2Price() {
        return MA2Price;
    }

    @Override
    public Float getMA3Price() {
        return MA3Price;
    }

    @Override
    public Float getMA4Price() {
        return MA4Price;
    }

    @Override
    public Float getMA5Price() {
        return MA5Price;
    }

    @Override
    public Float getMA6Price() {
        return MA6Price;
    }

    @Override
    public float getUb() {
        return ub;
    }

    @Override
    public float getBoll() {
        return boll;
    }

    @Override
    public float getLb() {
        return lb;
    }

    @Override
    public float getVolume() {
        return Volume;
    }

    @Override
    public float getMA5Volume() {
        return MA5Volume;
    }

    @Override
    public float getMA10Volume() {
        return MA10Volume;
    }

    @Override
    public float getMacd() {
        return macd;
    }

    @Override
    public float getDif() {
        return dif;
    }

    @Override
    public float getDea() {
        return dea;
    }

    @Override
    public float getK() {
        return k;
    }

    @Override
    public float getD() {
        return d;
    }

    @Override
    public float getJ() {
        return j;
    }

    @Override
    public Float getRsi1() {
        return rsi1;
    }

    @Override
    public Float getRsi2() {
        return rsi2;
    }

    @Override
    public Float getRsi3() {
        return rsi3;
    }

    @Override
    public Float getWR1() {
        return wr1;
    }

    @Override
    public Float getWR2() {
        return wr2;
    }

    @Override
    public Float getWR3() {
        return wr3;
    }
}
