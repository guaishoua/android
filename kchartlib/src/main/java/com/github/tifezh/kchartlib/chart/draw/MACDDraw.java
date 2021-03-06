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
import com.github.tifezh.kchartlib.chart.entity.IMACD;
import com.github.tifezh.kchartlib.chart.formatter.ValueFormatter;
import com.github.tifezh.kchartlib.chart.utils.ViewUtil;


/**
 * macd实现类
 * Created by tifezh on 2016/6/19.
 */

public class MACDDraw implements IChartDraw<IMACD> {

    private Paint mSellPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mBuyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mDIFPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mDEAPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mMACDPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int paddingWidth;

    /**
     * macd 中柱子的宽度
     */
    private float mMACDWidth = 0;

    public MACDDraw(BaseKLineChartView view) {
        Context context = view.getContext();
        mSellPaint.setColor(ContextCompat.getColor(context, R.color.chart_sell));
        mBuyPaint.setColor(ContextCompat.getColor(context, R.color.chart_buy));

        paddingWidth = ViewUtil.Dp2Px(context, 10);
    }

    @Override
    public void drawTranslated(@Nullable IMACD lastPoint, @NonNull IMACD curPoint, float lastX, float curX, @NonNull Canvas canvas, @NonNull BaseKLineChartView view, int position) {
        IMACD point = (IMACD) view.getItem(position);
        if (point.getMACDSValue() != null && point.getMACDLValue() != null && point.getMACDMValue() != null) {
            drawMACD(canvas, view, curX, curPoint.getMacd());
            view.drawChildLine(canvas, mDIFPaint, lastX, lastPoint.getDif(), curX, curPoint.getDif());
            view.drawChildLine(canvas, mDEAPaint, lastX, lastPoint.getDea(), curX, curPoint.getDea());
        }
    }

    @Override
    public void drawText(@NonNull Canvas canvas, @NonNull BaseKLineChartView view, int position, float x, float y) {
        IMACD point = (IMACD) view.getItem(position);
        if (point.getMACDSValue() != null && point.getMACDLValue() != null && point.getMACDMValue() != null) {
            String text = "MACD(" + point.getMACDSValue() + "," + point.getMACDLValue() + "," + point.getMACDMValue() + ")";
            canvas.drawText(text, x, y, view.getTextPaint());
            x += view.getTextPaint().measureText(text) + paddingWidth;

            if (point.getMacd() != 0) {
                text = "MACD:" + view.formatValue(point.getMacd());
                canvas.drawText(text, x, y, mMACDPaint);
                x += mMACDPaint.measureText(text) + paddingWidth;
            }

            text = "DIF:" + view.formatValue(point.getDif());
            canvas.drawText(text, x, y, mDIFPaint);
            x += mDIFPaint.measureText(text) + paddingWidth;

            text = "DEA:" + view.formatValue(point.getDea());
            canvas.drawText(text, x, y, mDEAPaint);
        }
    }

    @Override
    public float getMaxValue(IMACD point) {
        return Math.max(point.getMacd(), Math.max(point.getDea(), point.getDif()));
    }

    @Override
    public float getMinValue(IMACD point) {
        return Math.min(point.getMacd(), Math.min(point.getDea(), point.getDif()));
    }

    @Override
    public IValueFormatter getValueFormatter() {
        return new ValueFormatter();
    }

    /**
     * 画macd
     *
     * @param canvas
     * @param x
     * @param macd
     */
    private void drawMACD(Canvas canvas, BaseKLineChartView view, float x, float macd) {
        float macdy = view.getChildY(macd);
        float r = mMACDWidth / 2;
        float zeroy = view.getChildY(0);

        if (macd > 0) {
            //               left   top   right  bottom
            canvas.drawRect(x - r, macdy, x + r, zeroy, mBuyPaint);
        } else {
            canvas.drawRect(x - r, zeroy, x + r, macdy, mSellPaint);
        }
    }

    /**
     * 设置DIF颜色
     */
    public void setDIFColor(int color) {
        this.mDIFPaint.setColor(color);
    }

    /**
     * 设置DEA颜色
     */
    public void setDEAColor(int color) {
        this.mDEAPaint.setColor(color);
    }

    /**
     * 设置MACD颜色
     */
    public void setMACDColor(int color) {
        this.mMACDPaint.setColor(color);
    }

    /**
     * 设置MACD的宽度
     *
     * @param MACDWidth
     */
    public void setMACDWidth(float MACDWidth) {
        mMACDWidth = MACDWidth;
    }

    /**
     * 设置曲线宽度
     */
    public void setLineWidth(float width) {
        mDEAPaint.setStrokeWidth(width);
        mDIFPaint.setStrokeWidth(width);
        mMACDPaint.setStrokeWidth(width);
    }

    /**
     * 设置文字大小
     */
    public void setTextSize(float textSize) {
        mDEAPaint.setTextSize(textSize);
        mDIFPaint.setTextSize(textSize);
        mMACDPaint.setTextSize(textSize);
    }
}
