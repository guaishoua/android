package com.github.tifezh.kchartlib.chart.draw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.tifezh.kchartlib.chart.BaseKLineChartView;
import com.github.tifezh.kchartlib.chart.base.IChartDraw;
import com.github.tifezh.kchartlib.chart.base.IValueFormatter;
import com.github.tifezh.kchartlib.chart.entity.IKDJ;
import com.github.tifezh.kchartlib.chart.formatter.ValueFormatter;

/**
 * KDJ实现类
 * Created by tifezh on 2016/6/19.
 */

public class KDJDraw implements IChartDraw<IKDJ> {

    private Paint mKPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mDPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mJPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public KDJDraw(BaseKLineChartView view) {
    }

    @Override
    public void drawTranslated(@Nullable IKDJ lastPoint, @NonNull IKDJ curPoint, float lastX, float curX, @NonNull Canvas canvas, @NonNull BaseKLineChartView view, int position) {
        if (lastPoint.getK() != 0) {
            view.drawChildLine(canvas, mKPaint, lastX, lastPoint.getK(), curX, curPoint.getK());
        }
        if (lastPoint.getD() != 0) {
            view.drawChildLine(canvas, mDPaint, lastX, lastPoint.getD(), curX, curPoint.getD());
        }
        if (lastPoint.getJ() != 0) {
            view.drawChildLine(canvas, mJPaint, lastX, lastPoint.getJ(), curX, curPoint.getJ());
        }
    }

    @Override
    public void drawText(@NonNull Canvas canvas, @NonNull BaseKLineChartView view, int position, float x, float y) {
        IKDJ point = (IKDJ) view.getItem(position);
        String text = "KDJ(" + point.getKDJNValue() + "," + point.getKDJM1Value() + "," + point.getKDJM2Value() + ")" + "\t\t";
        canvas.drawText(text, x, y, view.getTextPaint());
        x += view.getTextPaint().measureText(text);

        text = "K:" + view.formatValue(point.getK()) + "\t\t";
        canvas.drawText(text, x, y, mKPaint);
        x += mKPaint.measureText(text);

        text = "D:" + view.formatValue(point.getD()) + "\t\t";
        canvas.drawText(text, x, y, mDPaint);
        x += mDPaint.measureText(text);

        text = "J:" + view.formatValue(point.getJ()) + "\t\t";
        canvas.drawText(text, x, y, mJPaint);
    }

    @Override
    public float getMaxValue(IKDJ point) {
        return Math.max(point.getK(), Math.max(point.getD(), point.getJ()));
    }

    @Override
    public float getMinValue(IKDJ point) {
        return Math.min(point.getK(), Math.min(point.getD(), point.getJ()));
    }

    @Override
    public IValueFormatter getValueFormatter() {
        return new ValueFormatter();
    }

    /**
     * 设置K颜色
     */
    public void setKColor(int color) {
        mKPaint.setColor(color);
    }

    /**
     * 设置D颜色
     */
    public void setDColor(int color) {
        mDPaint.setColor(color);
    }

    /**
     * 设置J颜色
     */
    public void setJColor(int color) {
        mJPaint.setColor(color);
    }

    /**
     * 设置曲线宽度
     */
    public void setLineWidth(float width) {
        mKPaint.setStrokeWidth(width);
        mDPaint.setStrokeWidth(width);
        mJPaint.setStrokeWidth(width);
    }

    /**
     * 设置文字大小
     */
    public void setTextSize(float textSize) {
        mKPaint.setTextSize(textSize);
        mDPaint.setTextSize(textSize);
        mJPaint.setTextSize(textSize);
    }
}
