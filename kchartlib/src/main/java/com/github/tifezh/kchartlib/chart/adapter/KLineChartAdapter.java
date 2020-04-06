package com.github.tifezh.kchartlib.chart.adapter;

import android.text.TextUtils;

import com.github.tifezh.kchartlib.chart.entity.KLineEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据适配器
 * Created by tifezh on 2016/6/18.
 */
public class KLineChartAdapter extends BaseKLineChartAdapter {

    private String coinName;
    private List<KLineEntity> datas = new ArrayList<>();

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public String getDate(int position) {
        return datas.get(position).Date;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    /**
     * 向头部添加数据
     */
    public void addHeaderData(List<KLineEntity> data) {
        if (data != null && !data.isEmpty()) {
            datas.addAll(data);
            notifyDataSetChanged();
        }
    }

    /**
     * 向尾部添加数据
     */
    public void addFooterData(List<KLineEntity> data) {
        if (data != null && !data.isEmpty()) {
            datas.addAll(0, data);
            notifyDataSetChanged();
        }
    }

    /**
     * 改变某个点的值
     *
     * @param position 索引值
     */
    public void changeItem(int position, KLineEntity data) {
        datas.set(position, data);
        notifyDataSetChanged();
    }

    public void changeCurrentItem(float closePrice, String symbol) {
        if (datas != null && datas.size() > 0 && TextUtils.equals(symbol, coinName)) {
            KLineEntity data = (KLineEntity) getItem(getCount() - 1);
            data.Close = closePrice;
            notifyDataSetChanged();
        }
    }

    /**
     * 数据清除
     */
    public void clearData() {
        if (datas != null && !datas.isEmpty()) {
            datas.clear();
        }
    }

    /**
     * 清除数据并且刷新
     */
    public void clearDataAndNotify() {
        if (datas != null && !datas.isEmpty()) {
            datas.clear();
            notifyDataSetChanged();
        }
    }
}
