package com.android.tacu.utils;

import com.android.tacu.module.market.model.KLineModel;
import com.github.tifezh.kchartlib.chart.KLineChartView;
import com.github.tifezh.kchartlib.chart.entity.KLineEntity;
import com.github.tifezh.kchartlib.chart.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class KlineUtils {

    public static List<KLineEntity> dealKlines(KLineModel model, long range) {
        List<KLineEntity> data = new ArrayList<>();
        int count = model.data.lines.size();
        KLineModel.DataModel dataModel;
        KLineEntity kLineEntity;
        for (int i = 0; i < count; i++) {
            dataModel = model.data;
            kLineEntity = new KLineEntity();
            if (range == KLineChartView.MIN_1) {
                kLineEntity.Date = DateUtil.millis2String(Long.parseLong(dataModel.lines.get(i).get(0)), DateUtil.FORMAT_DATE_MS);
            } else if (range >= KLineChartView.MIN_5 && range <= KLineChartView.HOUR_6) {
                kLineEntity.Date = DateUtil.millis2String(Long.parseLong(dataModel.lines.get(i).get(0)), DateUtil.FORMAT_DATE_MDHM);
            } else {
                kLineEntity.Date = DateUtil.millis2String(Long.parseLong(dataModel.lines.get(i).get(0)), DateUtil.FORMAT_DATE_YMD);
            }
            kLineEntity.Open = Float.parseFloat(dataModel.lines.get(i).get(1));
            kLineEntity.High = Float.parseFloat(dataModel.lines.get(i).get(2));
            kLineEntity.Low = Float.parseFloat(dataModel.lines.get(i).get(3));
            kLineEntity.Close = Float.parseFloat(dataModel.lines.get(i).get(4));
            kLineEntity.Volume = Float.parseFloat(dataModel.lines.get(i).get(5));
            data.add(kLineEntity);
        }
        return data;
    }
}
