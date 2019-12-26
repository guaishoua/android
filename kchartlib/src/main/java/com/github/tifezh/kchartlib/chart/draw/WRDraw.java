package com.github.tifezh.kchartlib.chart.draw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.tifezh.kchartlib.chart.BaseKChartView;
import com.github.tifezh.kchartlib.chart.base.IChartDraw;
import com.github.tifezh.kchartlib.chart.base.IValueFormatter;
import com.github.tifezh.kchartlib.chart.entity.IWR;
import com.github.tifezh.kchartlib.chart.formatter.ValueFormatter;

/**
 * WR指标
 * Created by jiazhen on 2018/6/4.
 */

public class WRDraw implements IChartDraw<IWR> {

    private Paint mWR1Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mWR2Paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public WRDraw(BaseKChartView view) {

    }

    @Override
    public void drawTranslated(@Nullable IWR lastPoint, @NonNull IWR curPoint, float lastX, float curX, @NonNull Canvas canvas, @NonNull BaseKChartView view, int position) {
        view.drawChildLine(canvas, mWR1Paint, lastX, lastPoint.getWR1(), curX, curPoint.getWR1());
        view.drawChildLine(canvas, mWR2Paint, lastX, lastPoint.getWR2(), curX, curPoint.getWR2());
    }

    @Override
    public void drawText(@NonNull Canvas canvas, @NonNull BaseKChartView view, int position, float x, float y) {
        IWR point = (IWR) view.getItem(position);
        /*String text = "WR(10,6)  ";
        canvas.drawText(text, x, y, view.getTextPaint());
        x += view.getTextPaint().measureText(text);
        text = "WR1:" + view.formatValue(point.getWR1()) + "  ";
        canvas.drawText(text, x, y, mWR1Paint);
        x += mWR1Paint.measureText(text);
        text = "WR2:" + view.formatValue(point.getWR2()) + "  ";
        canvas.drawText(text, x, y, mWR2Paint);*/

        String text = "WR2:" + view.formatValue(point.getWR2()) + "  ";
        x -= mWR2Paint.measureText(text);
        canvas.drawText(text, x, y, mWR2Paint);

        text = "WR1:" + view.formatValue(point.getWR1()) + "  ";
        x -= mWR1Paint.measureText(text);
        canvas.drawText(text, x, y, mWR1Paint);

        text = "WR(10,6)  ";
        x -= view.getTextPaint().measureText(text);
        canvas.drawText(text, x, y, view.getTextPaint());
    }

    @Override
    public float getMaxValue(IWR point) {
        return Math.max(point.getWR1(), point.getWR2());
    }

    @Override
    public float getMinValue(IWR point) {
        return Math.min(point.getWR1(), point.getWR2());
    }

    @Override
    public IValueFormatter getValueFormatter() {
        return new ValueFormatter();
    }

    public void setWR1Color(int color) {
        mWR1Paint.setColor(color);
    }

    public void setWR2Color(int color) {
        mWR2Paint.setColor(color);
    }

    /**
     * 设置曲线宽度
     */
    public void setLineWidth(float width) {
        mWR1Paint.setStrokeWidth(width);
        mWR2Paint.setStrokeWidth(width);
    }

    /**
     * 设置文字大小
     */
    public void setTextSize(float textSize) {
        mWR1Paint.setTextSize(textSize);
        mWR2Paint.setTextSize(textSize);
    }
}
