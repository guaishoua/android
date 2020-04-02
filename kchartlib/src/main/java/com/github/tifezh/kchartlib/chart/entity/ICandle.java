package com.github.tifezh.kchartlib.chart.entity;

/**
 * 蜡烛图实体接口
 * Created by tifezh on 2016/6/9.
 */
public interface ICandle {

    /**
     * 开盘价
     */
    float getOpenPrice();

    /**
     * 最高价
     */
    float getHighPrice();

    /**
     * 最低价
     */
    float getLowPrice();

    /**
     * 收盘价
     */
    float getClosePrice();

    /**
     * 成交量
     */
    float getVolume();

    /**
     * MA
     *
     * @return
     */

    Float getMA1Price();

    Float getMA2Price();

    Float getMA3Price();

    Float getMA4Price();

    Float getMA5Price();

    Float getMA6Price();

    Integer getMA1Value();

    Integer getMA2Value();

    Integer getMA3Value();

    Integer getMA4Value();

    Integer getMA5Value();

    Integer getMA6Value();


    // 以下为BOLL数据

    /**
     * 中轨线 平均线
     */
    float getBoll();

    /**
     * 上轨线
     */
    float getUb();

    /**
     * 下轨线
     */
    float getLb();

    /**
     * 平均线
     */
    float getMANPrice();
}
