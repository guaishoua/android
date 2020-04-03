package com.github.tifezh.kchartlib.chart.draw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.tifezh.kchartlib.chart.BaseKLineChartView;
import com.github.tifezh.kchartlib.chart.base.IChartDraw;
import com.github.tifezh.kchartlib.chart.base.IValueFormatter;
import com.github.tifezh.kchartlib.chart.entity.IRSI;
import com.github.tifezh.kchartlib.chart.formatter.ValueFormatter;
import com.github.tifezh.kchartlib.chart.utils.ViewUtil;

import java.util.Collections;

/**
 * RSI实现类
 * Created by tifezh on 2016/6/19.
 */
public class RSIDraw implements IChartDraw<IRSI> {

    private Paint mRSI1Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mRSI2Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mRSI3Paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int paddingWidth;

    public RSIDraw(BaseKLineChartView view) {
        paddingWidth = ViewUtil.Dp2Px(view.getContext(), 10);
    }

    @Override
    public void drawTranslated(@Nullable IRSI lastPoint, @NonNull IRSI curPoint, float lastX, float curX, @NonNull Canvas canvas, @NonNull BaseKLineChartView view, int position) {
        if (lastPoint.getRsi1() != null) {
            view.drawChildLine(canvas, mRSI1Paint, lastX, lastPoint.getRsi1(), curX, curPoint.getRsi1());
        }
        if (lastPoint.getRsi2() != null) {
            view.drawChildLine(canvas, mRSI2Paint, lastX, lastPoint.getRsi2(), curX, curPoint.getRsi2());
        }
        if (lastPoint.getRsi3() != null) {
            view.drawChildLine(canvas, mRSI3Paint, lastX, lastPoint.getRsi3(), curX, curPoint.getRsi3());
        }
    }

    @Override
    public void drawText(@NonNull Canvas canvas, @NonNull BaseKLineChartView view, int position, float x, float y) {
        IRSI point = (IRSI) view.getItem(position);
        if (point.getRsi1() != null) {
            String text = "RSI(" + point.getRsi1Value() + ")" + view.formatValue(point.getRsi1());
            canvas.drawText(text, x, y, mRSI1Paint);
            x += view.getTextPaint().measureText(text) + paddingWidth;
        }
        if (point.getRsi2() != null) {
            String text = "RSI(" + point.getRsi2Value() + ")" + view.formatValue(point.getRsi2());
            canvas.drawText(text, x, y, mRSI2Paint);
            x += view.getTextPaint().measureText(text) + paddingWidth;
        }
        if (point.getRsi3() != null) {
            String text = "RSI(" + point.getRsi3Value() + ")" + view.formatValue(point.getRsi3());
            canvas.drawText(text, x, y, mRSI3Paint);
        }
    }

    @Override
    public float getMaxValue(IRSI point) {
        return Math.max(point.getRsi1(), Math.max(point.getRsi2(), point.getRsi3()));
    }

    @Override
    public float getMinValue(IRSI point) {
        return Math.min(point.getRsi1(), Math.min(point.getRsi2(), point.getRsi3()));
    }

    @Override
    public IValueFormatter getValueFormatter() {
        return new ValueFormatter();
    }

    public void setRSI1Color(int color) {
        mRSI1Paint.setColor(color);
    }

    public void setRSI2Color(int color) {
        mRSI2Paint.setColor(color);
    }

    public void setRSI3Color(int color) {
        mRSI3Paint.setColor(color);
    }

    /**
     * 设置曲线宽度
     */
    public void setLineWidth(float width) {
        mRSI1Paint.setStrokeWidth(width);
        mRSI2Paint.setStrokeWidth(width);
        mRSI3Paint.setStrokeWidth(width);
    }

    /**
     * 设置文字大小
     */
    public void setTextSize(float textSize) {
        mRSI2Paint.setTextSize(textSize);
        mRSI3Paint.setTextSize(textSize);
        mRSI1Paint.setTextSize(textSize);
    }
}
