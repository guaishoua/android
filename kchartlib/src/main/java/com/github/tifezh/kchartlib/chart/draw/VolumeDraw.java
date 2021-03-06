package com.github.tifezh.kchartlib.chart.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.github.tifezh.kchartlib.R;
import com.github.tifezh.kchartlib.chart.BaseKLineChartView;
import com.github.tifezh.kchartlib.chart.base.IChartDraw;
import com.github.tifezh.kchartlib.chart.base.IValueFormatter;
import com.github.tifezh.kchartlib.chart.entity.IVolume;
import com.github.tifezh.kchartlib.chart.formatter.BigValueFormatter;
import com.github.tifezh.kchartlib.chart.utils.ViewUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hjm on 2017/11/14 17:49.
 */
public class VolumeDraw implements IChartDraw<IVolume> {

    private Paint mSellPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mBuyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ma5Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ma10Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int pillarWidth = 0;

    private int paddingWidth;

    private List<Float> maxVOLList = new ArrayList<>();
    private List<Float> minVOLList = new ArrayList<>();

    public VolumeDraw(BaseKLineChartView view) {
        Context context = view.getContext();
        mSellPaint.setColor(ContextCompat.getColor(context, R.color.chart_sell));
        mBuyPaint.setColor(ContextCompat.getColor(context, R.color.chart_buy));
        pillarWidth = ViewUtil.Dp2Px(context, 4);

        paddingWidth = ViewUtil.Dp2Px(context, 10);
    }

    @Override
    public void drawTranslated(@Nullable IVolume lastPoint, @NonNull IVolume curPoint, float lastX, float curX, @NonNull Canvas canvas, @NonNull BaseKLineChartView view, int position) {
        drawHistogram(canvas, curPoint, lastPoint, curX, view, position);
        if (lastPoint.getMA5Volume() != null) {
            view.drawVolLine(canvas, ma5Paint, lastX, lastPoint.getMA5Volume(), curX, curPoint.getMA5Volume());
        }
        if (lastPoint.getMA10Volume() != null) {
            view.drawVolLine(canvas, ma10Paint, lastX, lastPoint.getMA10Volume(), curX, curPoint.getMA10Volume());
        }
    }

    private void drawHistogram(Canvas canvas, IVolume curPoint, IVolume lastPoint, float curX, BaseKLineChartView view, int position) {
        float r = pillarWidth / 2;
        float top = view.getVolY(curPoint.getVolume());
        int bottom = view.getVolRect().bottom;
        if (curPoint.getClosePrice() >= curPoint.getOpenPrice()) {//涨
            canvas.drawRect(curX - r, top, curX + r, bottom, mBuyPaint);
        } else {
            canvas.drawRect(curX - r, top, curX + r, bottom, mSellPaint);
        }
    }

    @Override
    public void drawText(@NonNull Canvas canvas, @NonNull BaseKLineChartView view, int position, float x, float y) {
        IVolume point = (IVolume) view.getItem(position);
        String text = "VOL:" + getValueFormatter().format(point.getVolume());
        canvas.drawText(text, x, y, view.getTextPaint());
        x += view.getTextPaint().measureText(text) + paddingWidth;

        if (point.getMA5Volume() != null) {
            text = "MA5:" + getValueFormatter().format(point.getMA5Volume());
            canvas.drawText(text, x, y, ma5Paint);
            x += ma5Paint.measureText(text) + paddingWidth;
        }

        if (point.getMA10Volume() != null) {
            text = "MA10:" + getValueFormatter().format(point.getMA10Volume());
            canvas.drawText(text, x, y, ma10Paint);
        }
    }

    @Override
    public float getMaxValue(IVolume point) {
        maxVOLList.clear();
        maxVOLList.add(point.getVolume());
        if (point.getMA5Volume() != null) {
            maxVOLList.add(point.getMA5Volume());
        }
        if (point.getMA10Volume() != null) {
            maxVOLList.add(point.getMA10Volume());
        }
        return Collections.max(maxVOLList);
    }

    @Override
    public float getMinValue(IVolume point) {
        minVOLList.clear();
        minVOLList.add(point.getVolume());
        if (point.getMA5Volume() != null) {
            minVOLList.add(point.getMA5Volume());
        }
        if (point.getMA10Volume() != null) {
            minVOLList.add(point.getMA10Volume());
        }
        return Collections.min(minVOLList);
    }

    @Override
    public IValueFormatter getValueFormatter() {
        return new BigValueFormatter();
    }

    /**
     * 设置 MA5 线的颜色
     *
     * @param color
     */
    public void setMa5Color(int color) {
        this.ma5Paint.setColor(color);
    }

    /**
     * 设置 MA10 线的颜色
     *
     * @param color
     */
    public void setMa10Color(int color) {
        this.ma10Paint.setColor(color);
    }

    public void setLineWidth(float width) {
        this.ma5Paint.setStrokeWidth(width);
        this.ma10Paint.setStrokeWidth(width);
    }

    /**
     * 设置文字大小
     *
     * @param textSize
     */
    public void setTextSize(float textSize) {
        this.ma5Paint.setTextSize(textSize);
        this.ma10Paint.setTextSize(textSize);
    }
}
