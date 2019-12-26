package com.github.tifezh.kchartlib.chart.adapter;

import com.github.tifezh.kchartlib.chart.BaseKChartAdapter;
import com.github.tifezh.kchartlib.chart.entity.KLineEntity;
import com.github.tifezh.kchartlib.utils.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数据适配器
 * Created by tifezh on 2016/6/18.
 */

public class KChartAdapter extends BaseKChartAdapter {

    private List<KLineEntity> datas = new ArrayList<>();

    public KChartAdapter() {

    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public Date getDate(int position) {
        try {
            String dateSring = datas.get(position).Date;
            return DateUtil.stringToDate(dateSring);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasData() {
        if (datas != null && !datas.isEmpty()) {
            return true;
        }
        return false;
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
     * 清除数据
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

    /**
     * 改变某个点的值
     *
     * @param position 索引值
     */
    public void changeItem(int position, KLineEntity data) {
        datas.set(position, data);
        notifyDataSetChanged();
    }
}
