package com.github.tifezh.kchartlib.chart.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.github.tifezh.kchartlib.R;
import com.github.tifezh.kchartlib.chart.BaseKLineChartView;
import com.github.tifezh.kchartlib.chart.KLineChartView;
import com.github.tifezh.kchartlib.chart.base.IChartDraw;
import com.github.tifezh.kchartlib.chart.base.IValueFormatter;
import com.github.tifezh.kchartlib.chart.base.Status;
import com.github.tifezh.kchartlib.chart.entity.ICandle;
import com.github.tifezh.kchartlib.chart.entity.IKLine;
import com.github.tifezh.kchartlib.chart.formatter.BigValueFormatter;
import com.github.tifezh.kchartlib.chart.formatter.ValueFormatter;
import com.github.tifezh.kchartlib.chart.utils.MathUtils;
import com.github.tifezh.kchartlib.chart.utils.ViewUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 主图的实现类
 * Created by tifezh on 2016/6/14.
 */
public class MainDraw implements IChartDraw<ICandle> {

    private Context mContext;
    private float mCandleWidth = 0;
    private float mCandleLineWidth = 0;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mBuyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSellPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint ma1Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ma2Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ma3Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ma4Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ma5Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ma6Paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mSelectorTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mBuySelectorTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSellSelectorTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSelectorBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSelectorBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean mCandleSolid = true;
    // 是否分时
    private boolean isLine = false;
    private Status status = Status.MA;
    private KLineChartView kChartView;

    private List<Float> maxMAList = new ArrayList<>();
    private List<Float> minMAList = new ArrayList<>();

    private int paddingWidth;

    public MainDraw(BaseKLineChartView view) {
        Context context = view.getContext();
        kChartView = (KLineChartView) view;
        mContext = context;
        mBuyPaint.setColor(ContextCompat.getColor(context, R.color.chart_buy));
        mBuySelectorTextPaint.setColor(ContextCompat.getColor(context, R.color.chart_buy));
        mSellPaint.setColor(ContextCompat.getColor(context, R.color.chart_sell));
        mSellSelectorTextPaint.setColor(ContextCompat.getColor(context, R.color.chart_sell));
        mLinePaint.setColor(ContextCompat.getColor(context, R.color.chart_line));
        mLinePaint.setStrokeWidth(ViewUtil.Dp2Px(mContext, 0.5F));
        paint.setColor(ContextCompat.getColor(context, R.color.chart_line_background));
        mSelectorBorderPaint.setColor(ContextCompat.getColor(context, R.color.chart_text));
        mSelectorBorderPaint.setStrokeWidth(ViewUtil.Dp2Px(mContext, 0.5F));
        mSelectorBorderPaint.setStyle(Paint.Style.STROKE);

        paddingWidth = ViewUtil.Dp2Px(view.getContext(), 10);
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public void drawTranslated(@Nullable ICandle lastPoint, @NonNull ICandle curPoint, float lastX, float curX, @NonNull Canvas canvas, @NonNull BaseKLineChartView view, int position) {
        if (isLine) {
            view.drawMainLine(canvas, mLinePaint, lastX, lastPoint.getClosePrice(), curX, curPoint.getClosePrice());
            view.drawMainMinuteLine(canvas, paint, lastX, lastPoint.getClosePrice(), curX, curPoint.getClosePrice());
        } else {
            drawCandle(view, canvas, curX, curPoint.getHighPrice(), curPoint.getLowPrice(), curPoint.getOpenPrice(), curPoint.getClosePrice());
            if (status == Status.MA) {
                if (lastPoint.getMA1Price() != null) {
                    view.drawMainLine(canvas, ma1Paint, lastX, lastPoint.getMA1Price(), curX, curPoint.getMA1Price());
                }
                if (lastPoint.getMA2Price() != null) {
                    view.drawMainLine(canvas, ma2Paint, lastX, lastPoint.getMA2Price(), curX, curPoint.getMA2Price());
                }
                if (lastPoint.getMA3Price() != null) {
                    view.drawMainLine(canvas, ma3Paint, lastX, lastPoint.getMA3Price(), curX, curPoint.getMA3Price());
                }
                if (lastPoint.getMA4Price() != null) {
                    view.drawMainLine(canvas, ma4Paint, lastX, lastPoint.getMA4Price(), curX, curPoint.getMA4Price());
                }
                if (lastPoint.getMA5Price() != null) {
                    view.drawMainLine(canvas, ma5Paint, lastX, lastPoint.getMA5Price(), curX, curPoint.getMA5Price());
                }
                if (lastPoint.getMA6Price() != null) {
                    view.drawMainLine(canvas, ma6Paint, lastX, lastPoint.getMA6Price(), curX, curPoint.getMA6Price());
                }
            } else if (status == Status.BOLL) {
                //画boll
                if (lastPoint.getBoll() != 0) {
                    view.drawMainLine(canvas, ma1Paint, lastX, lastPoint.getBoll(), curX, curPoint.getBoll());
                }
                if (lastPoint.getUb() != 0) {
                    view.drawMainLine(canvas, ma2Paint, lastX, lastPoint.getUb(), curX, curPoint.getUb());
                }
                if (lastPoint.getLb() != 0) {
                    view.drawMainLine(canvas, ma3Paint, lastX, lastPoint.getLb(), curX, curPoint.getLb());
                }
            }
        }

    }

    @Override
    public void drawText(@NonNull Canvas canvas, @NonNull BaseKLineChartView view, int position, float x, float y) {
        ICandle point = (IKLine) view.getItem(position);
        if (!isLine) {
            if (status == Status.MA) {
                String text;
                if (point.getMA1Price() != null) {
                    text = "MA" + point.getMA1Value() + ":" + view.formatValue(point.getMA1Price());
                    canvas.drawText(text, x, y, ma1Paint);
                    x += ma1Paint.measureText(text) + paddingWidth;
                }
                if (point.getMA2Price() != null) {
                    text = "MA" + point.getMA2Value() + ":" + view.formatValue(point.getMA2Price());
                    canvas.drawText(text, x, y, ma2Paint);
                    x += ma2Paint.measureText(text) + paddingWidth;
                }
                if (point.getMA3Price() != null) {
                    text = "MA" + point.getMA3Value() + ":" + view.formatValue(point.getMA3Price());
                    canvas.drawText(text, x, y, ma3Paint);
                    x += ma3Paint.measureText(text) + paddingWidth;
                }
                if (point.getMA4Price() != null) {
                    text = "MA" + point.getMA4Value() + ":" + view.formatValue(point.getMA4Price());
                    canvas.drawText(text, x, y, ma4Paint);
                    x += ma4Paint.measureText(text) + paddingWidth;
                }
                if (point.getMA5Price() != null) {
                    text = "MA" + point.getMA5Value() + ":" + view.formatValue(point.getMA5Price());
                    canvas.drawText(text, x, y, ma5Paint);
                    x += ma5Paint.measureText(text) + paddingWidth;
                }
                if (point.getMA6Price() != null) {
                    text = "MA" + point.getMA6Value() + ":" + view.formatValue(point.getMA6Price());
                    canvas.drawText(text, x, y, ma6Paint);
                }
            } else if (status == Status.BOLL) {
                if (point.getBoll() != 0) {
                    String text = "BOLL:" + view.formatValue(point.getBoll());
                    canvas.drawText(text, x, y, ma1Paint);
                    x += ma1Paint.measureText(text) + paddingWidth;

                    text = "UB:" + view.formatValue(point.getUb());
                    canvas.drawText(text, x, y, ma2Paint);
                    x += ma2Paint.measureText(text) + paddingWidth;

                    text = "LB:" + view.formatValue(point.getLb());
                    canvas.drawText(text, x, y, ma3Paint);
                }
            }
        }
        if (view.isLongPress()) {
            drawSelector(view, canvas);
        }
    }

    @Override
    public float getMaxValue(ICandle point) {
        if (status == Status.BOLL) {
            if (Float.isNaN(point.getUb())) {
                return Math.max(point.getBoll(), point.getHighPrice());
            } else {
                return Math.max(point.getUb(), point.getHighPrice());
            }
        } else {
            maxMAList.clear();
            maxMAList.add(point.getHighPrice());
            if (point.getMA1Price() != null) {
                maxMAList.add(point.getMA1Price());
            }
            if (point.getMA2Price() != null) {
                maxMAList.add(point.getMA2Price());
            }
            if (point.getMA3Price() != null) {
                maxMAList.add(point.getMA3Price());
            }
            if (point.getMA4Price() != null) {
                maxMAList.add(point.getMA4Price());
            }
            if (point.getMA5Price() != null) {
                maxMAList.add(point.getMA5Price());
            }
            if (point.getMA6Price() != null) {
                maxMAList.add(point.getMA6Price());
            }
            return Collections.max(maxMAList);
        }
    }

    @Override
    public float getMinValue(ICandle point) {
        if (status == Status.BOLL) {
            if (Float.isNaN(point.getLb())) {
                if (point.getBoll() == 0) {
                    return point.getLowPrice();
                }
                return Math.min(point.getBoll(), point.getLowPrice());
            } else {
                if (point.getLb() == 0) {
                    return point.getLowPrice();
                }
                return Math.min(point.getLb(), point.getLowPrice());
            }
        } else {
            minMAList.clear();
            minMAList.add(point.getLowPrice());
            if (point.getMA1Price() != null) {
                minMAList.add(point.getMA1Price());
            }
            if (point.getMA2Price() != null) {
                minMAList.add(point.getMA2Price());
            }
            if (point.getMA3Price() != null) {
                minMAList.add(point.getMA3Price());
            }
            if (point.getMA4Price() != null) {
                minMAList.add(point.getMA4Price());
            }
            if (point.getMA5Price() != null) {
                minMAList.add(point.getMA5Price());
            }
            if (point.getMA6Price() != null) {
                minMAList.add(point.getMA6Price());
            }
            return Collections.min(minMAList);
        }
    }

    @Override
    public IValueFormatter getValueFormatter() {
        return new ValueFormatter();
    }

    /**
     * 画Candle
     *
     * @param canvas
     * @param x      x轴坐标
     * @param high   最高价
     * @param low    最低价
     * @param open   开盘价
     * @param close  收盘价
     */
    private void drawCandle(BaseKLineChartView view, Canvas canvas, float x, float high, float low, float open, float close) {
        high = view.getMainY(high);
        low = view.getMainY(low);
        open = view.getMainY(open);
        close = view.getMainY(close);
        float r = mCandleWidth / 2;
        float lineR = mCandleLineWidth / 2;
        if (open > close) {
            //实心
            if (mCandleSolid) {
                canvas.drawRect(x - r, close, x + r, open, mBuyPaint);
                canvas.drawRect(x - lineR, high, x + lineR, low, mBuyPaint);
            } else {
                mBuyPaint.setStrokeWidth(mCandleLineWidth);
                canvas.drawLine(x, high, x, close, mBuyPaint);
                canvas.drawLine(x, open, x, low, mBuyPaint);
                canvas.drawLine(x - r + lineR, open, x - r + lineR, close, mBuyPaint);
                canvas.drawLine(x + r - lineR, open, x + r - lineR, close, mBuyPaint);
                mBuyPaint.setStrokeWidth(mCandleLineWidth * view.getScaleX());
                canvas.drawLine(x - r, open, x + r, open, mBuyPaint);
                canvas.drawLine(x - r, close, x + r, close, mBuyPaint);
            }

        } else if (open < close) {
            canvas.drawRect(x - r, open, x + r, close, mSellPaint);
            canvas.drawRect(x - lineR, high, x + lineR, low, mSellPaint);
        } else {
            canvas.drawRect(x - r, open, x + r, close + 1, mBuyPaint);
            canvas.drawRect(x - lineR, high, x + lineR, low, mBuyPaint);
        }
    }

    /**
     * draw选择器
     *
     * @param view
     * @param canvas
     */
    private void drawSelector(BaseKLineChartView view, Canvas canvas) {
        Paint.FontMetrics metrics = mSelectorTextPaint.getFontMetrics();
        float textHeight = metrics.descent - metrics.ascent;

        int index = view.getSelectedIndex();
        float padding = ViewUtil.Dp2Px(mContext, 2);
        float margin = ViewUtil.Dp2Px(mContext, 3);
        float width = 0;
        float left;
        float top = margin + view.getTopPadding();
        float height = padding * 9 + textHeight * 8;
        float centerMargin = ViewUtil.Dp2Px(mContext, 5);

        ICandle point = (ICandle) view.getItem(index);
        List<String> stringTexts = new ArrayList<>();
        List<String> strings = new ArrayList<>();

        stringTexts.add(mContext.getResources().getString(R.string.date));
        stringTexts.add(mContext.getResources().getString(R.string.open));
        stringTexts.add(mContext.getResources().getString(R.string.high));
        stringTexts.add(mContext.getResources().getString(R.string.low));
        stringTexts.add(mContext.getResources().getString(R.string.close));
        stringTexts.add(mContext.getResources().getString(R.string.up_down_value));
        stringTexts.add(mContext.getResources().getString(R.string.up_down_rate));
        stringTexts.add(mContext.getResources().getString(R.string.volume));

        strings.add(view.getAdapter().getDate(index));
        strings.add(getValueFormatter().format(point.getOpenPrice()));
        strings.add(getValueFormatter().format(point.getHighPrice()));
        strings.add(getValueFormatter().format(point.getLowPrice()));
        strings.add(getValueFormatter().format(point.getClosePrice()));
        double change = MathUtils.sub(point.getClosePrice(), point.getOpenPrice());
        if (change > 0) {
            strings.add("+" + MathUtils.getFormatValue(change));
        } else {
            strings.add(MathUtils.getFormatValue(change));
        }
        try {
            if (change > 0) {
                strings.add("+" + MathUtils.div(change, point.getOpenPrice()));
            } else {
                strings.add(MathUtils.div(change, point.getOpenPrice()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        strings.add(getBigValueFormatter().format(point.getVolume()));

        for (int i = 0; i < stringTexts.size(); i++) {
            width = Math.max(width, mSelectorTextPaint.measureText(stringTexts.get(i) + strings.get(i)));
        }
        width += padding * 3 + centerMargin;

        float x = view.translateXtoX(view.getX(index));
        if (x > view.getChartWidth() / 2) {
            left = margin;
        } else {
            left = view.getChartWidth() - width - margin;
        }

        RectF r = new RectF(left, top, left + width, top + height);
        canvas.drawRoundRect(r, padding, padding, mSelectorBackgroundPaint);
        canvas.drawRoundRect(r, padding, padding, mSelectorBorderPaint);
        float y = top + padding + (textHeight - metrics.bottom - metrics.top) / 2;

        for (int i = 0; i < stringTexts.size(); i++) {
            canvas.drawText(stringTexts.get(i), left + padding, y, mSelectorTextPaint);
            if (i == 5 || i == 6) {
                if (change < 0) {
                    canvas.drawText(strings.get(i), left + width - padding - mSellSelectorTextPaint.measureText(strings.get(i)), y, mSellSelectorTextPaint);
                    y += textHeight + padding;
                } else {
                    canvas.drawText(strings.get(i), left + width - padding - mBuySelectorTextPaint.measureText(strings.get(i)), y, mBuySelectorTextPaint);
                    y += textHeight + padding;
                }
            } else {
                canvas.drawText(strings.get(i), left + width - padding - mSelectorTextPaint.measureText(strings.get(i)), y, mSelectorTextPaint);
                y += textHeight + padding;
            }
        }
    }

    /**
     * 设置蜡烛宽度
     *
     * @param candleWidth
     */
    public void setCandleWidth(float candleWidth) {
        mCandleWidth = candleWidth;
    }

    /**
     * 上下影线的宽度
     *
     * @param candleLineWidth
     */
    public void setCandleLineWidth(float candleLineWidth) {
        mCandleLineWidth = candleLineWidth;
    }

    /**
     * 设置ma1颜色
     *
     * @param color
     */
    public void setMa1Color(int color) {
        this.ma1Paint.setColor(color);
    }

    /**
     * 设置ma2颜色
     *
     * @param color
     */
    public void setMa2Color(int color) {
        this.ma2Paint.setColor(color);
    }

    /**
     * 设置ma3颜色
     *
     * @param color
     */
    public void setMa3Color(int color) {
        this.ma3Paint.setColor(color);
    }

    /**
     * 设置ma4颜色
     *
     * @param color
     */
    public void setMa4Color(int color) {
        this.ma4Paint.setColor(color);
    }

    /**
     * 设置ma5颜色
     *
     * @param color
     */
    public void setMa5Color(int color) {
        this.ma5Paint.setColor(color);
    }

    /**
     * 设置ma6颜色
     *
     * @param color
     */
    public void setMa6Color(int color) {
        this.ma6Paint.setColor(color);
    }

    /**
     * 设置选择器文字颜色
     *
     * @param color
     */
    public void setSelectorTextColor(int color) {
        mSelectorTextPaint.setColor(color);
    }

    /**
     * 设置选择器文字大小
     *
     * @param textSize
     */
    public void setSelectorTextSize(float textSize) {
        mSelectorTextPaint.setTextSize(textSize);
        mBuySelectorTextPaint.setTextSize(textSize);
        mSellSelectorTextPaint.setTextSize(textSize);
    }

    /**
     * 设置选择器背景
     *
     * @param color
     */
    public void setSelectorBackgroundColor(int color) {
        mSelectorBackgroundPaint.setColor(color);
    }

    /**
     * 设置曲线宽度
     */
    public void setLineWidth(float width) {
        ma1Paint.setStrokeWidth(width);
        ma2Paint.setStrokeWidth(width);
        ma3Paint.setStrokeWidth(width);
        ma4Paint.setStrokeWidth(width);
        ma5Paint.setStrokeWidth(width);
        ma6Paint.setStrokeWidth(width);
        //mLinePaint.setStrokeWidth(width);
    }

    /**
     * 设置文字大小
     */
    public void setTextSize(float textSize) {
        ma1Paint.setTextSize(textSize);
        ma2Paint.setTextSize(textSize);
        ma3Paint.setTextSize(textSize);
        ma4Paint.setTextSize(textSize);
        ma5Paint.setTextSize(textSize);
        ma6Paint.setTextSize(textSize);
    }

    /**
     * 蜡烛是否实心
     */
    public void setCandleSolid(boolean candleSolid) {
        mCandleSolid = candleSolid;
    }

    public void setLine(boolean line) {
        if (isLine != line) {
            isLine = line;
            if (isLine) {
                kChartView.setCandleWidth(kChartView.dp2px(7f));
            } else {
                kChartView.setCandleWidth(kChartView.dp2px(6f));
            }
        }
    }

    public boolean isLine() {
        return isLine;
    }

    public BigValueFormatter getBigValueFormatter() {
        return new BigValueFormatter();
    }
}
