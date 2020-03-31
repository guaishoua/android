package com.android.tacu.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.android.tacu.R;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.UIUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 轻量级的折线图
 * Created by jiazhen on 2018/9/14.
 */

public class SmallChartView extends View {

    private Context mContext;
    private float xOriginLeft;//x轴原点坐标
    private float xOriginRight;//x轴原点坐标
    private float yOriginTop;//y轴原点坐标
    private float yOriginDown;//y轴原点坐标
    private float xWidth;
    private float yHeight;

    private int mWidth;//控件宽度
    private int mHeight;//控件高度
    private float xInterval;//x轴坐标间隔
    private float yInterval;//y轴坐标间隔
    private int startColor;//渐变色的起始颜色
    private int centerColor;//渐变色的中间颜色
    private int stopColor;//渐变色的
    private int[] shadeColors;//渐变阴影颜色
    private int mLineColor;//折线颜色
    private float max = 0, min = 0;//最大值、最小值
    private int maxIndex = 0, minIndex = 0;
    private int mPoint = 0;//小数点后位数
    private int mTextSize = UIUtils.dp2px(12);
    private int mTextMaxMinSize = UIUtils.dp2px(10);

    private List<Float> mItems = new ArrayList<>();//折线数据
    private List<Long> mTimeItems = new ArrayList<>();//时间数据

    private CornerPathEffect cornerPathEffect;//给折线设置圆角
    private DashPathEffect dattEffect;//虚线
    private Paint mPaintLine;//折线画笔
    private Paint mPaintShader;//渐变色画笔
    private Paint mPaintText;//画文字的画笔
    private Paint mPaintMaxMinText;//画文字的画笔
    private Paint mPaintDottes1;//虚线
    private Paint mPaintDottes2;//虚线
    private Paint mPaintDottes3;//虚线
    private Paint mPaintDottes4;//虚线

    private Path mPath;//折线路径
    private Path mPathShader;//渐变色路径
    private Path mDattPath1;//虚线路径
    private Path mDattPath2;//虚线路径
    private Path mDattPath3;//虚线路径
    private Path mDattPath4;//虚线路径

    private Comparator<Float> comp;

    public void setItems(List<Long> timeItems, List<Float> items, int point) {
        if (timeItems != null && timeItems.size() > 0 && items != null && items.size() > 0) {
            mTimeItems = timeItems;
            mItems = items;
            mPoint = point;
            max = Collections.max(items, comp);
            min = Collections.min(items, comp);
            maxIndex = mItems.indexOf(max);
            minIndex = mItems.indexOf(min);
        }
    }

    public SmallChartView(Context context) {
        this(context, null);
    }

    public SmallChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmallChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SmallChartView);
        mLineColor = typedArray.getColor(R.styleable.SmallChartView_lineColor, ContextCompat.getColor(getContext(), R.color.chart_line_color));
        startColor = typedArray.getColor(R.styleable.SmallChartView_gradient_Start_Color, ContextCompat.getColor(getContext(), R.color.chart_gradient_start_color));
        centerColor = typedArray.getColor(R.styleable.SmallChartView_gradient_Center_Color, ContextCompat.getColor(getContext(), R.color.chart_gradient_center_color));
        stopColor = typedArray.getColor(R.styleable.SmallChartView_gradient_Stop_Color, ContextCompat.getColor(getContext(), R.color.chart_gradient_stop_color));
        shadeColors = new int[]{startColor, centerColor, stopColor};
        typedArray.recycle();

        init();

        comp = new Comparator<Float>() {
            @Override
            public int compare(Float o1, Float o2) {
                if (o1 > o2) {
                    return 1;
                } else if (o1 == o2) {
                    return 0;
                }
                return -1;
            }
        };
    }

    private void init() {
        cornerPathEffect = new CornerPathEffect(20);
        dattEffect = new DashPathEffect(new float[]{4, 8}, 1);

        //初始化折线的画笔
        mPaintLine = new Paint();
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setAntiAlias(true);
        mPaintLine.setStrokeWidth(2f);
        mPaintLine.setColor(mLineColor);
        mPaintLine.setPathEffect(cornerPathEffect);

        //阴影画笔
        mPaintShader = new Paint();
        mPaintShader.setAntiAlias(true);
        mPaintShader.setStrokeWidth(2f);
        mPaintShader.setPathEffect(cornerPathEffect);

        //初始化文字画笔
        mPaintText = new Paint();
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setAntiAlias(true); //抗锯齿
        mPaintText.setTextSize(mTextSize);
        mPaintText.setColor(ContextCompat.getColor(mContext, R.color.text_grey));
        mPaintText.setTextAlign(Paint.Align.LEFT);

        //最大值，最小值
        mPaintMaxMinText = new Paint();
        mPaintMaxMinText.setStyle(Paint.Style.FILL);
        mPaintMaxMinText.setAntiAlias(true); //抗锯齿
        mPaintMaxMinText.setTextSize(mTextMaxMinSize);
        mPaintMaxMinText.setColor(ContextCompat.getColor(mContext, R.color.text_default));
        mPaintMaxMinText.setTextAlign(Paint.Align.LEFT);

        //虚线
        mPaintDottes1 = new Paint();
        setDottPaint(mPaintDottes1);
        mPaintDottes2 = new Paint();
        setDottPaint(mPaintDottes2);
        mPaintDottes3 = new Paint();
        setDottPaint(mPaintDottes3);
        mPaintDottes4 = new Paint();
        setDottPaint(mPaintDottes4);
    }

    private void setDottPaint(Paint paint) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2f);
        paint.setColor(ContextCompat.getColor(mContext, R.color.text_grey_2));
        paint.setPathEffect(dattEffect);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mWidth = getWidth();
            mHeight = getHeight();
            //初始化原点坐标
            xOriginLeft = mPaintText.measureText(BigDecimal.valueOf(max).setScale(mPoint, BigDecimal.ROUND_DOWN).toPlainString()) + UIUtils.dp2px(10);
            xOriginRight = mWidth - mPaintText.measureText("24:00");
            yOriginDown = mHeight - UIUtils.dp2px(15);
            yOriginTop = UIUtils.dp2px(10);

            xWidth = xOriginRight - xOriginLeft;
            yHeight = yOriginDown - yOriginTop;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Y轴间距
        yInterval = (max - min) / yHeight;
        xInterval = xWidth / (mItems.size() - 1);

        mPath = new Path();
        mPathShader = new Path();
        mDattPath1 = new Path();
        mDattPath2 = new Path();
        mDattPath3 = new Path();
        mDattPath4 = new Path();

        //画文字
        drawText(canvas);
        //虚线
        drawDatt(canvas);
        //画折线
        drawLine(canvas);
    }

    private void drawText(Canvas canvas) {
        float yLenght = yHeight / 3;

        //  绘制最大值
        String maxValue = BigDecimal.valueOf(max).setScale(mPoint, BigDecimal.ROUND_DOWN).toPlainString();
        canvas.drawText(maxValue, 0, yOriginTop + mTextSize, mPaintText);
        //  绘制2/3
        String midValue2 = BigDecimal.valueOf(min + (max - min) * 2 / 3).setScale(mPoint, BigDecimal.ROUND_DOWN).toPlainString();
        canvas.drawText(midValue2, 0, yOriginTop + yLenght + (mTextSize / 2), mPaintText);
        //  绘制1/3
        String midValue1 = BigDecimal.valueOf(min + (max - min) / 3).setScale(mPoint, BigDecimal.ROUND_DOWN).toPlainString();
        canvas.drawText(midValue1, 0, yOriginTop + yLenght * 2 + (mTextSize / 2), mPaintText);
        //  绘制最小值
        String minValue = BigDecimal.valueOf(min).setScale(mPoint, BigDecimal.ROUND_DOWN).toPlainString();
        canvas.drawText(minValue, 0, yOriginTop + yHeight, mPaintText);

        if (mTimeItems != null && mTimeItems.size() > 0) {
            float xLength = xWidth / 4;

            // 第一个时间
            if (mTimeItems.get(0) != null) {
                String time1 = DateUtils.millis2String(mTimeItems.get(0), DateUtils.FORMAT_DATE_HM);
                canvas.drawText(time1, xOriginLeft - mTextSize, yOriginDown + mTextSize + UIUtils.dp2px(2), mPaintText);
            }

            //第二个
            if (mTimeItems.size() >= 6 && mTimeItems.get(5) != null) {
                String time1 = DateUtils.millis2String(mTimeItems.get(5), DateUtils.FORMAT_DATE_HM);
                canvas.drawText(time1, xOriginLeft + xLength - mTextSize, yOriginDown + mTextSize + UIUtils.dp2px(2), mPaintText);
            }

            //第三个
            if (mTimeItems.size() >= 12 && mTimeItems.get(11) != null) {
                String time1 = DateUtils.millis2String(mTimeItems.get(11), DateUtils.FORMAT_DATE_HM);
                canvas.drawText(time1, xOriginLeft + xLength * 2 - mTextSize, yOriginDown + mTextSize + UIUtils.dp2px(2), mPaintText);
            }

            //第四个
            if (mTimeItems.size() >= 18 && mTimeItems.get(17) != null) {
                String time1 = DateUtils.millis2String(mTimeItems.get(17), DateUtils.FORMAT_DATE_HM);
                canvas.drawText(time1, xOriginLeft + xLength * 3 - mTextSize, yOriginDown + mTextSize + UIUtils.dp2px(2), mPaintText);
            }

            // 第五个时间
            if (mTimeItems.size() >= 24 && mTimeItems.get(23) != null) {
                String time1 = DateUtils.millis2String(mTimeItems.get(23), DateUtils.FORMAT_DATE_HM);
                canvas.drawText(time1, xOriginLeft + xWidth - mTextSize, yOriginDown + mTextSize + UIUtils.dp2px(2), mPaintText);
            }
        }
    }

    private void drawDatt(Canvas canvas) {
        float yLenght = yHeight / 3;

        //  绘制最大值
        mDattPath1.moveTo(xOriginLeft, yOriginTop + mTextSize / 2);
        mDattPath1.lineTo(xOriginLeft + xWidth, yOriginTop + mTextSize / 2);
        canvas.drawPath(mDattPath1, mPaintDottes1);
        //  绘制2/3
        mDattPath2.moveTo(xOriginLeft, yOriginTop + yLenght + (mTextSize / 4));
        mDattPath2.lineTo(xOriginLeft + xWidth, yOriginTop + yLenght + (mTextSize / 4));
        canvas.drawPath(mDattPath2, mPaintDottes2);
        //  绘制1/3
        mDattPath3.moveTo(xOriginLeft, yOriginTop + yLenght * 2 + (mTextSize / 4));
        mDattPath3.lineTo(xOriginLeft + xWidth, yOriginTop + yLenght * 2 + (mTextSize / 4));
        canvas.drawPath(mDattPath3, mPaintDottes3);
        //  绘制最小值
        mDattPath4.moveTo(xOriginLeft, yOriginTop + yHeight);
        mDattPath4.lineTo(xOriginLeft + xWidth, yOriginTop + yHeight);
        canvas.drawPath(mDattPath4, mPaintDottes4);
    }

    private void drawLine(Canvas canvas) {
        //画坐标点
        for (int i = 0; i < mItems.size(); i++) {
            float x = i * xInterval + xOriginLeft;
            if (i == 0) {
                mPathShader.moveTo(x, yOriginDown - (mItems.get(i) - min) / yInterval);
                mPath.moveTo(x, yOriginDown - (mItems.get(i) - min) / yInterval);
            } else {
                mPath.lineTo(x, yOriginDown - (mItems.get(i) - min) / yInterval);
                mPathShader.lineTo(x, yOriginDown - (mItems.get(i) - min) / yInterval);
                if (i == mItems.size() - 1) {
                    mPathShader.lineTo(x, yOriginDown);
                    mPathShader.lineTo(xOriginLeft, yOriginDown);
                    mPathShader.close();
                }
            }

            if (maxIndex == i) {
                String maxValue = BigDecimal.valueOf(max).setScale(mPoint, BigDecimal.ROUND_DOWN).toPlainString();
                canvas.drawText(String.valueOf(maxValue), x - mPaintMaxMinText.measureText(BigDecimal.valueOf(max).setScale(mPoint, BigDecimal.ROUND_DOWN).toPlainString()) / 2, yOriginDown - (mItems.get(i) - min) / yInterval + mTextSize, mPaintMaxMinText);
            }
            if (maxIndex != minIndex) {
                if (minIndex == i) {
                    String minValue = BigDecimal.valueOf(min).setScale(mPoint, BigDecimal.ROUND_DOWN).toPlainString();
                    canvas.drawText(String.valueOf(minValue), x - mPaintMaxMinText.measureText(BigDecimal.valueOf(min).setScale(mPoint, BigDecimal.ROUND_DOWN).toPlainString()) / 2, yOriginDown - (mItems.get(i) - min) / yInterval - mTextSize, mPaintMaxMinText);
                }
            }
        }

        //渐变阴影
        Shader mShader = new LinearGradient(0, 0, 0, getHeight(), shadeColors, null, Shader.TileMode.CLAMP);
        mPaintShader.setShader(mShader);

        //绘制折线
        canvas.drawPath(mPath, mPaintLine);
        //绘制渐变阴影
        canvas.drawPath(mPathShader, mPaintShader);
    }
}
