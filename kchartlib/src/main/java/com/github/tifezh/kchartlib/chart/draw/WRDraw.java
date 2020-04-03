package com.github.tifezh.kchartlib.chart.draw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.tifezh.kchartlib.chart.BaseKLineChartView;
import com.github.tifezh.kchartlib.chart.base.IChartDraw;
import com.github.tifezh.kchartlib.chart.base.IValueFormatter;
import com.github.tifezh.kchartlib.chart.entity.IWR;
import com.github.tifezh.kchartlib.chart.formatter.ValueFormatter;
import com.github.tifezh.kchartlib.chart.utils.ViewUtil;

/**
 * KDJ实现类
 * Created by tifezh on 2016/6/19.
 */
public class WRDraw implements IChartDraw<IWR> {

    private Paint mWR1Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mWR2Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mWR3Paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int paddingWidth;

    public WRDraw(BaseKLineChartView view) {
        paddingWidth = ViewUtil.Dp2Px(view.getContext(), 10);
    }

    @Override
    public void drawTranslated(@Nullable IWR lastPoint, @NonNull IWR curPoint, float lastX, float curX, @NonNull Canvas canvas, @NonNull BaseKLineChartView view, int position) {
        if (lastPoint.getWR1() != null) {
            view.drawChildLine(canvas, mWR1Paint, lastX, lastPoint.getWR1(), curX, curPoint.getWR1());
        }
        if (lastPoint.getWR2() != null) {
            view.drawChildLine(canvas, mWR2Paint, lastX, lastPoint.getWR2(), curX, curPoint.getWR2());
        }
        if (lastPoint.getWR3() != null) {
            view.drawChildLine(canvas, mWR3Paint, lastX, lastPoint.getWR3(), curX, curPoint.getWR3());
        }
    }

    @Override
    public void drawText(@NonNull Canvas canvas, @NonNull BaseKLineChartView view, int position, float x, float y) {
        IWR point = (IWR) view.getItem(position);
        if (point.getWR1() != null) {
            String text = "WR(" + point.getWR1Value() + "):" + view.formatValue(point.getWR1());
            canvas.drawText(text, x, y, mWR1Paint);
            x += view.getTextPaint().measureText(text) + paddingWidth;
        }
        if (point.getWR2() != null) {
            String text = "WR(" + point.getWR2Value() + "):" + view.formatValue(point.getWR2());
            canvas.drawText(text, x, y, mWR2Paint);
            x += view.getTextPaint().measureText(text) + paddingWidth;
        }
        if (point.getWR3() != null) {
            String text = "WR(" + point.getWR3Value() + "):" + view.formatValue(point.getWR3());
            canvas.drawText(text, x, y, mWR3Paint);
        }
    }

    @Override
    public float getMaxValue(IWR point) {
        return Math.max(point.getWR1(), Math.max(point.getWR2(), point.getWR3()));
    }

    @Override
    public float getMinValue(IWR point) {
        return Math.min(point.getWR1(), Math.min(point.getWR2(), point.getWR3()));
    }

    @Override
    public IValueFormatter getValueFormatter() {
        return new ValueFormatter();
    }

    /**
     * 设置%R颜色
     */
    public void setWR1Color(int color) {
        mWR1Paint.setColor(color);
    }

    public void setWR2Color(int color) {
        mWR2Paint.setColor(color);
    }

    public void setWR3Color(int color) {
        mWR3Paint.setColor(color);
    }

    /**
     * 设置曲线宽度
     */
    public void setLineWidth(float width) {
        mWR1Paint.setStrokeWidth(width);
        mWR2Paint.setStrokeWidth(width);
        mWR3Paint.setStrokeWidth(width);
    }

    /**
     * 设置文字大小
     */
    public void setTextSize(float textSize) {
        mWR1Paint.setTextSize(textSize);
        mWR2Paint.setTextSize(textSize);
        mWR3Paint.setTextSize(textSize);
    }
}
