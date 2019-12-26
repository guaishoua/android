package com.android.tacu.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.android.tacu.R;
import com.android.tacu.module.market.model.PriceModal;
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

public class BuySellChartView extends View {

    private Context mContext;
    private List<PriceModal> buyList = new ArrayList<>();
    private List<PriceModal> sellList = new ArrayList<>();
    private int point = 0, pointNum = 0;
    private double buyMax = 0, buyMin = 0, sellMax = 0, sellMin = 0;
    private PriceModal buyMaxModal, buyMinModal, sellMaxModal, sellMinModal;

    private int mWidth;//控件宽度
    private int mBuyWidth;
    private int mSellWidth;
    private int mHeight;//控件高度
    private float xOriginLeft;//x轴原点坐标
    private float xOriginRight;//x轴原点坐标
    private float yOriginTop;//y轴原点坐标
    private float yOriginDown;//y轴原点坐标
    private float xWidth;
    private float yHeight;

    private float buyXInterval;//x轴坐标间隔
    private float buyYInterval;//y轴坐标间隔
    private float sellXInterval;//x轴坐标间隔
    private float sellYInterval;//y轴坐标间隔

    private Paint mGridPaint;//格子线
    private Paint mTextPaint;//画文字的画笔
    private Paint mBuyLinePaint;//折线画笔
    private Paint mSellLinePaint;//折线画笔
    private Paint mBuyPaintShader;//渐变色画笔
    private Paint mSellPaintShader;//渐变色画笔

    private CornerPathEffect cornerPathEffect;//给折线设置圆角

    private Path mBuyPath;//折线路径
    private Path mSellPath;//折线路径
    private Path mBuyPathShader;//渐变色路径
    private Path mSellPathShader;//渐变色路径

    private int[] buyShadeColors;//渐变阴影颜色
    private int[] sellShadeColors;//渐变阴影颜色

    private int mTextSize = UIUtils.dp2px(10);
    private float rowSpace;
    private float columnSpace;

    private String currencyNameEn;
    private String baseCurrencyNameEn;

    public void setItems(List<PriceModal> buyList, List<PriceModal> sellList, int point, int pointNum, String currencyNameEn, String baseCurrencyNameEn) {
        if (buyList != null && buyList.size() > 0 || sellList != null && sellList.size() > 0) {
            this.buyList = buyList;
            this.sellList = sellList;
            this.point = point;
            this.pointNum = pointNum;
            this.currencyNameEn = currencyNameEn;
            this.baseCurrencyNameEn = baseCurrencyNameEn;

            if (buyList != null && buyList.size() > 0) {
                SortList(buyList, "up");
                buyMaxModal = Collections.max(buyList, comp);
                buyMinModal = Collections.min(buyList, comp);
                buyMax = buyMaxModal.getAll();
                buyMin = buyMinModal.getAll();
            }

            if (sellList != null && sellList.size() > 0) {
                SortList(sellList, "up");
                sellMaxModal = Collections.max(sellList, comp);
                sellMinModal = Collections.min(sellList, comp);
                sellMax = sellMaxModal.getAll();
                sellMin = sellMinModal.getAll();
            }

            invalidate();
        }
    }

    public BuySellChartView(Context context) {
        this(context, null);
    }

    public BuySellChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BuySellChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        init();
    }

    private void init() {
        buyShadeColors = new int[]{ContextCompat.getColor(mContext, R.color.coin_depth_blue), ContextCompat.getColor(mContext, R.color.coin_depth_blue), ContextCompat.getColor(mContext, R.color.coin_depth_blue)};
        sellShadeColors = new int[]{ContextCompat.getColor(mContext, R.color.coin_depth_red), ContextCompat.getColor(mContext, R.color.coin_depth_red), ContextCompat.getColor(mContext, R.color.coin_depth_red)};
        cornerPathEffect = new CornerPathEffect(15);

        //初始化折线的画笔
        mGridPaint = new Paint();
        mGridPaint.setStyle(Paint.Style.STROKE);
        mGridPaint.setAntiAlias(true);
        mGridPaint.setStrokeWidth(1f);
        mGridPaint.setColor(ContextCompat.getColor(mContext, R.color.color_grey_10));

        //初始化文字画笔
        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setAntiAlias(true); //抗锯齿
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(ContextCompat.getColor(mContext, R.color.color_grey_2));
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        //初始化折线的画笔
        mBuyLinePaint = new Paint();
        mBuyLinePaint.setStyle(Paint.Style.STROKE);
        mBuyLinePaint.setAntiAlias(true);
        mBuyLinePaint.setStrokeWidth(4f);
        mBuyLinePaint.setColor(ContextCompat.getColor(mContext, R.color.color_riseup));
        mBuyLinePaint.setPathEffect(cornerPathEffect);

        mSellLinePaint = new Paint();
        mSellLinePaint.setStyle(Paint.Style.STROKE);
        mSellLinePaint.setAntiAlias(true);
        mSellLinePaint.setStrokeWidth(4f);
        mSellLinePaint.setColor(ContextCompat.getColor(mContext, R.color.color_risedown));
        mSellLinePaint.setPathEffect(cornerPathEffect);

        //阴影画笔
        mBuyPaintShader = new Paint();
        mBuyPaintShader.setAntiAlias(true);
        mBuyPaintShader.setStrokeWidth(2f);
        mBuyPaintShader.setPathEffect(cornerPathEffect);

        mSellPaintShader = new Paint();
        mSellPaintShader.setAntiAlias(true);
        mSellPaintShader.setStrokeWidth(2f);
        mSellPaintShader.setPathEffect(cornerPathEffect);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mWidth = getWidth();
            mBuyWidth = mWidth / 2;
            mSellWidth = mWidth / 2;
            mHeight = getHeight();

            xOriginLeft = 0;
            xOriginRight = mWidth;
            yOriginDown = mHeight - UIUtils.dp2px(15);
            yOriginTop = 0;

            xWidth = xOriginRight - xOriginLeft;
            yHeight = yOriginDown - yOriginTop;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (buyList != null && buyList.size() > 0) {
            buyXInterval = mBuyWidth / buyList.size();
            buyYInterval = (float) (buyMax - buyMin) / yHeight;
        }

        if (sellList != null && sellList.size() > 0) {
            sellXInterval = mSellWidth / sellList.size();
            sellYInterval = (float) (sellMax - sellMin) / yHeight;
        }

        mBuyPath = new Path();
        mSellPath = new Path();
        mBuyPathShader = new Path();
        mSellPathShader = new Path();

        rowSpace = yHeight / 4;
        columnSpace = xWidth / 3;

        drawGrid(canvas);
        drawText(canvas);
        drawLine(canvas);
    }

    // 划线
    private void drawGrid(Canvas canvas) {
        // 横轴
        for (int i = 0; i < 5; i++) {
            canvas.drawLine(0, rowSpace * i + yOriginTop, mWidth, rowSpace * i + yOriginTop, mGridPaint);
        }

        // 纵轴
        int marginRight = 0;
        for (int i = 0; i < 4; i++) {
            // 防止最右边的线不显示
            if (i == 3) {
                marginRight = 10;
            } else {
                marginRight = 0;
            }
            canvas.drawLine(columnSpace * i - marginRight, yOriginTop, columnSpace * i - marginRight, yOriginDown, mGridPaint);
        }
    }

    // 文案
    private void drawText(Canvas canvas) {
        if (buyList != null && buyList.size() > 0) {
            // 底部的文案
            String buyPriceLeft = BigDecimal.valueOf(buyList.get(0).getPrice()).setScale(point, BigDecimal.ROUND_DOWN).toPlainString();
            canvas.drawText(buyPriceLeft, 0, yOriginDown + mTextSize, mTextPaint);

            String buyPriceRight = BigDecimal.valueOf(buyList.get(buyList.size() * 2 / 3).getPrice()).setScale(point, BigDecimal.ROUND_DOWN).toPlainString();
            float buyPriceRightLength = mTextPaint.measureText(buyPriceRight);
            canvas.drawText(buyPriceRight, mWidth / 3 - buyPriceRightLength / 2, yOriginDown + mTextSize, mTextPaint);

            // 左边的文案
            canvas.drawText(BigDecimal.valueOf(buyList.get(0).getAll()).setScale(pointNum, BigDecimal.ROUND_DOWN).toPlainString() + "(" + baseCurrencyNameEn + ")", 0, yOriginTop + mTextSize, mTextPaint);
            canvas.drawText(BigDecimal.valueOf(buyList.get(buyList.size() / 4).getAll()).setScale(pointNum, BigDecimal.ROUND_DOWN).toPlainString(), 0, yOriginTop + rowSpace, mTextPaint);
            canvas.drawText(BigDecimal.valueOf(buyList.get(buyList.size() * 2 / 4).getAll()).setScale(pointNum, BigDecimal.ROUND_DOWN).toPlainString(), 0, yOriginTop + rowSpace * 2, mTextPaint);
            canvas.drawText(BigDecimal.valueOf(buyList.get(buyList.size() * 3 / 4).getAll()).setScale(pointNum, BigDecimal.ROUND_DOWN).toPlainString(), 0, yOriginTop + rowSpace * 3, mTextPaint);
            canvas.drawText(BigDecimal.valueOf(buyList.get(buyList.size() - 1).getAll()).setScale(pointNum, BigDecimal.ROUND_DOWN).toPlainString(), 0, yOriginDown, mTextPaint);
        }
        if (sellList != null && sellList.size() > 0) {
            // 底部的文案
            String sellPriceLeft = BigDecimal.valueOf(sellList.get(sellList.size() / 3).getPrice()).setScale(point, BigDecimal.ROUND_DOWN).toPlainString();
            float sellPriceLeftLength = mTextPaint.measureText(sellPriceLeft);
            canvas.drawText(sellPriceLeft, mWidth * 2 / 3 - sellPriceLeftLength / 2, yOriginDown + mTextSize, mTextPaint);

            String sellPriceRight = BigDecimal.valueOf(sellList.get(sellList.size() - 1).getPrice()).setScale(point, BigDecimal.ROUND_DOWN).toPlainString();
            float sellPriceRightLength = mTextPaint.measureText(sellPriceRight);
            canvas.drawText(sellPriceRight, mWidth - sellPriceRightLength, yOriginDown + mTextSize, mTextPaint);

            // 右边的文案
            String str1 = BigDecimal.valueOf(sellList.get(0).getAll()).setScale(pointNum, BigDecimal.ROUND_DOWN).toPlainString();
            canvas.drawText(str1, xOriginRight - mTextPaint.measureText(str1), yOriginDown, mTextPaint);
            String str2 = BigDecimal.valueOf(sellList.get(sellList.size() / 4).getAll()).setScale(pointNum, BigDecimal.ROUND_DOWN).toPlainString();
            canvas.drawText(str2, xOriginRight - mTextPaint.measureText(str2), yOriginDown - rowSpace, mTextPaint);
            String str3 = BigDecimal.valueOf(sellList.get(sellList.size() * 2 / 4).getAll()).setScale(pointNum, BigDecimal.ROUND_DOWN).toPlainString();
            canvas.drawText(str3, xOriginRight - mTextPaint.measureText(str3), yOriginDown - rowSpace * 2, mTextPaint);
            String str4 = BigDecimal.valueOf(sellList.get(sellList.size() * 3 / 4).getAll()).setScale(pointNum, BigDecimal.ROUND_DOWN).toPlainString();
            canvas.drawText(str4, xOriginRight - mTextPaint.measureText(str4), yOriginDown - rowSpace * 3, mTextPaint);
            String str5 = BigDecimal.valueOf(sellList.get(sellList.size() - 1).getAll()).setScale(pointNum, BigDecimal.ROUND_DOWN).toPlainString() + "(" + currencyNameEn + ")";
            canvas.drawText(str5, xOriginRight - mTextPaint.measureText(str5), yOriginTop + mTextSize, mTextPaint);
        }
    }

    private void drawLine(Canvas canvas) {
        if (buyList != null && buyList.size() > 0) {
            for (int i = 0; i < buyList.size(); i++) {
                float x = i * buyXInterval + xOriginLeft;
                if (i == 0) {
                    mBuyPath.moveTo(x, (float) (yOriginDown - (buyList.get(i).getAll() - buyMin) / buyYInterval));
                    mBuyPathShader.moveTo(x, (float) (yOriginDown - (buyList.get(i).getAll() - buyMin) / buyYInterval));
                } else {
                    mBuyPath.lineTo(x, (float) (yOriginDown - (buyList.get(i).getAll() - buyMin) / buyYInterval));
                    mBuyPathShader.lineTo(x, (float) (yOriginDown - (buyList.get(i).getAll() - buyMin) / buyYInterval));
                    if (i == buyList.size() - 1) {
                        mBuyPathShader.lineTo(x, yOriginDown);
                        mBuyPathShader.lineTo(xOriginLeft, yOriginDown);
                        mBuyPathShader.close();
                    }
                }
            }

            //渐变阴影
            Shader mShader = new LinearGradient(0, 0, 0, getHeight(), buyShadeColors, null, Shader.TileMode.CLAMP);
            mBuyPaintShader.setShader(mShader);

            //绘制折线
            canvas.drawPath(mBuyPath, mBuyLinePaint);
            //绘制渐变阴影
            canvas.drawPath(mBuyPathShader, mBuyPaintShader);
        }
        if (sellList != null && sellList.size() > 0) {
            for (int i = sellList.size() - 1; i >= 0; i--) {
                float x = xOriginRight - i * sellXInterval;
                if (i == sellList.size() - 1) {
                    mSellPath.moveTo(x, (float) (yOriginTop + (sellList.get(i).getAll() - sellMin) / sellYInterval));
                    mSellPathShader.moveTo(x, (float) (yOriginTop + (sellList.get(i).getAll() - sellMin) / sellYInterval));
                } else {
                    mSellPath.lineTo(x, (float) (yOriginTop + (sellList.get(i).getAll() - sellMin) / sellYInterval));
                    mSellPathShader.lineTo(x, (float) (yOriginTop + (sellList.get(i).getAll() - sellMin) / sellYInterval));
                    if (i == 0) {
                        mSellPathShader.lineTo(x, yOriginDown);
                        mSellPathShader.lineTo(xOriginRight, yOriginDown);
                        mSellPathShader.close();
                    }
                }
            }
            //渐变阴影
            Shader mShader = new LinearGradient(0, 0, 0, getHeight(), sellShadeColors, null, Shader.TileMode.CLAMP);
            mSellPaintShader.setShader(mShader);

            //绘制折线
            canvas.drawPath(mSellPath, mSellLinePaint);
            //绘制渐变阴影
            canvas.drawPath(mSellPathShader, mSellPaintShader);
        }
    }

    private Comparator<PriceModal> comp = new Comparator<PriceModal>() {
        @Override
        public int compare(PriceModal o1, PriceModal o2) {
            if (o1.getAll() > o2.getAll()) {
                return 1;
            } else if (o1.getAll() == o2.getAll()) {
                return 0;
            }
            return -1;
        }
    };

    /**
     * @param list
     * @param flag up=增 down=降
     */
    private void SortList(List<PriceModal> list, final String flag) {
        Collections.sort(list, new Comparator<PriceModal>() {
            @Override
            public int compare(PriceModal o1, PriceModal o2) {
                if (o1.getPrice() > o2.getPrice()) {
                    if (TextUtils.equals(flag, "up")) {
                        return 1;
                    } else if (TextUtils.equals(flag, "down")) {
                        return -1;
                    }
                    return 1;
                } else if (o1.getPrice() == o2.getPrice()) {
                    return 0;
                }
                if (TextUtils.equals(flag, "up")) {
                    return -1;
                } else if (TextUtils.equals(flag, "down")) {
                    return 1;
                }
                return -1;
            }
        });
    }
}
