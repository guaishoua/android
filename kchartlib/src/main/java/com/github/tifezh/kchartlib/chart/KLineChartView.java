package com.github.tifezh.kchartlib.chart;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.github.tifezh.kchartlib.R;
import com.github.tifezh.kchartlib.chart.draw.KDJDraw;
import com.github.tifezh.kchartlib.chart.draw.MACDDraw;
import com.github.tifezh.kchartlib.chart.draw.MainDraw;
import com.github.tifezh.kchartlib.chart.draw.RSIDraw;
import com.github.tifezh.kchartlib.chart.draw.VolumeDraw;
import com.github.tifezh.kchartlib.chart.draw.WRDraw;

/**
 * k线图
 * Created by tian on 2016/5/20.
 */
public class KLineChartView extends BaseKLineChartView {

    public static long MIN_1 = 60000L;
    public static long MIN_5 = 300000L;
    public static long MIN_15 = 900000L;
    public static long MIN_30 = 1800000L;
    public static long HOUR_1 = 3600000L;
    public static long HOUR_6 = 21600000L;
    public static long DAY_1 = 86400000L;
    public static long WEEK_1 = 604800000L;
    public static long MONTH_1 = 2592000000L;

    //用于ValueFormatter的位数的显示
    public static int decimalsCount = 2;

    ProgressBar mProgressBar;
    private boolean isRefreshing = false;
    private boolean isLoadMoreEnd = false;
    private boolean mLastScrollEnable;
    private boolean mLastScaleEnable;

    private KChartRefreshListener mRefreshListener;

    private MACDDraw mMACDDraw;
    private RSIDraw mRSIDraw;
    private MainDraw mMainDraw;
    private KDJDraw mKDJDraw;
    private WRDraw mWRDraw;
    private VolumeDraw mVolumeDraw;

    public KLineChartView(Context context) {
        this(context, null);
    }

    public KLineChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KLineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initAttrs();
    }

    private void initView() {
        mProgressBar = new ProgressBar(getContext());
        LayoutParams layoutParams = new LayoutParams(dp2px(50), dp2px(50));
        layoutParams.addRule(CENTER_IN_PARENT);
        addView(mProgressBar, layoutParams);
        mProgressBar.setVisibility(GONE);
        mVolumeDraw = new VolumeDraw(this);
        mMACDDraw = new MACDDraw(this);
        mWRDraw = new WRDraw(this);
        mKDJDraw = new KDJDraw(this);
        mRSIDraw = new RSIDraw(this);
        mMainDraw = new MainDraw(this);
        addChildDraw(mMACDDraw);
        addChildDraw(mKDJDraw);
        addChildDraw(mRSIDraw);
        addChildDraw(mWRDraw);
        setVolDraw(mVolumeDraw);
        setMainDraw(mMainDraw);
    }

    private void initAttrs() {
        try {
            //设置每个点的宽度
            setPointWidth(getDimension(R.dimen.chart_point_width));

            //字体的大小
            setTextSize(getDimension(R.dimen.chart_text_size));
            //默认字体颜色
            setTextColor(getColor(R.color.chart_text));

            //最大值最小值颜色
            setMTextSize(getDimension(R.dimen.chart_text_size));
            setMTextColor(getColor(R.color.chart_candle_text));

            //折线的宽度
            setLineWidth(getDimension(R.dimen.chart_line_width));
            //k线的背景色
            setBackgroundColor(getColor(R.color.chart_background));

            //选择器的横线
            setSelectedXLineColor(Color.WHITE);
            setSelectedXLineWidth(getDimension(R.dimen.chart_line_width));

            //选择器的纵线
            //setSelectedYLineColor(Color.WHITE);
            setSelectedYLineWidth(getDimension(R.dimen.chart_point_width));

            //表格线的粗细
            setGridLineWidth(getDimension(R.dimen.chart_grid_line_width));
            //表格线的颜色
            setGridLineColor(getColor(R.color.chart_grid_line));

            //当前最新价格
            setCurrentPriceTextSize(getDimension(R.dimen.chart_text_size));
            setCurrentPriceColor(getColor(R.color.text_default));
            setCurrentPriceBgColor(getColor(R.color.round_rect_bg));

            //macd
            setMACDWidth(getDimension(R.dimen.chart_candle_width));
            setMACDColor(getColor(R.color.chart_text));
            setDIFColor(getColor(R.color.chart_color1));
            setDEAColor(getColor(R.color.chart_color2));

            //kdj
            setKColor(getColor(R.color.chart_color1));
            setDColor(getColor(R.color.chart_color2));
            setJColor(getColor(R.color.chart_color3));

            //wr
            setWR1Color(getColor(R.color.chart_color1));
            setWR2Color(getColor(R.color.chart_color2));
            setWR3Color(getColor(R.color.chart_color3));

            //rsi
            setRSI1Color(getColor(R.color.chart_color1));
            setRSI2Color(getColor(R.color.chart_color2));
            setRSI3Color(getColor(R.color.chart_color3));

            //main
            setMa1Color(getColor(R.color.chart_color1));
            setMa2Color(getColor(R.color.chart_color2));
            setMa3Color(getColor(R.color.chart_color3));
            setMa4Color(getColor(R.color.chart_color4));
            setMa5Color(getColor(R.color.chart_color5));
            setMa6Color(getColor(R.color.chart_color6));

            //蜡烛线宽度
            setCandleWidth(getDimension(R.dimen.chart_candle_width));
            //上下影线的宽度
            setCandleLineWidth(getDimension(R.dimen.chart_candle_line_width));
            //蜡烛图是否实心
            setCandleSolid(true);

            //选中的point显示收盘价和时间的背景边线
            setSelectorPointColor(Color.WHITE);
            setPointTextPaintColor(getColor(R.color.chart_point_text));
            setSelectorPaintWidth(getDimension(R.dimen.chart_select_frame_width));
            setSelectorPaintTextSize(getDimension(R.dimen.chart_point_text_size));
            //选中的point显示收盘价和时间的背景色
            setSelectorFramePaint(getColor(R.color.chart_select_point_color));

            //长按的显示的详情的背景色
            setSelectorBackgroundColor(getColor(R.color.chart_selector));
            setSelectorTextSize(getDimension(R.dimen.chart_selector_text_size));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private float getDimension(@DimenRes int resId) {
        return getResources().getDimension(resId);
    }

    private int getColor(@ColorRes int resId) {
        return ContextCompat.getColor(getContext(), resId);
    }

    @Override
    public void onLeftSide() {
        showLoading();
    }

    @Override
    public void onRightSide() {
    }

    public void showLoading() {
        if (!isLoadMoreEnd && !isRefreshing) {
            isRefreshing = true;
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
            if (mRefreshListener != null) {
                mRefreshListener.onLoadMoreBegin(this);
            }
            mLastScaleEnable = isScaleEnable();
            mLastScrollEnable = isScrollEnable();
            super.setScrollEnable(false);
            super.setScaleEnable(false);
        }
    }

    public void justShowLoading() {
        if (!isRefreshing) {
            isLongPress = false;
            isRefreshing = true;
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
            if (mRefreshListener != null) {
                mRefreshListener.onLoadMoreBegin(this);
            }
            mLastScaleEnable = isScaleEnable();
            mLastScrollEnable = isScrollEnable();
            super.setScrollEnable(false);
            super.setScaleEnable(false);
        }
    }

    private void hideLoading() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
        super.setScrollEnable(mLastScrollEnable);
        super.setScaleEnable(mLastScaleEnable);
    }

    /**
     * 隐藏选择器内容
     */
    public void hideSelectData() {
        isLongPress = false;
        invalidate();
    }

    /**
     * 刷新完成
     */
    public void refreshComplete() {
        isRefreshing = false;
        hideLoading();
    }

    /**
     * 刷新完成，没有数据
     */
    public void refreshEnd() {
        isLoadMoreEnd = true;
        isRefreshing = false;
        hideLoading();
    }

    /**
     * 重置加载更多
     */
    public void resetLoadMoreEnd() {
        isLoadMoreEnd = false;
    }

    public void setLoadMoreEnd() {
        isLoadMoreEnd = true;
    }

    public interface KChartRefreshListener {
        /**
         * 加载更多
         *
         * @param chart
         */
        void onLoadMoreBegin(KLineChartView chart);
    }

    @Override
    public void setScaleEnable(boolean scaleEnable) {
        if (isRefreshing) {
            throw new IllegalStateException("请勿在刷新状态设置属性");
        }
        super.setScaleEnable(scaleEnable);

    }

    @Override
    public void setScrollEnable(boolean scrollEnable) {
        if (isRefreshing) {
            throw new IllegalStateException("请勿在刷新状态设置属性");
        }
        super.setScrollEnable(scrollEnable);
    }

    /**
     * 设置DIF颜色
     */
    public void setDIFColor(int color) {
        mMACDDraw.setDIFColor(color);
    }

    /**
     * 设置DEA颜色
     */
    public void setDEAColor(int color) {
        mMACDDraw.setDEAColor(color);
    }

    /**
     * 设置MACD颜色
     */
    public void setMACDColor(int color) {
        mMACDDraw.setMACDColor(color);
    }

    /**
     * 设置MACD的宽度
     *
     * @param MACDWidth
     */
    public void setMACDWidth(float MACDWidth) {
        mMACDDraw.setMACDWidth(MACDWidth);
    }

    /**
     * 设置K颜色
     */
    public void setKColor(int color) {
        mKDJDraw.setKColor(color);
    }

    /**
     * 设置D颜色
     */
    public void setDColor(int color) {
        mKDJDraw.setDColor(color);
    }

    /**
     * 设置J颜色
     */
    public void setJColor(int color) {
        mKDJDraw.setJColor(color);
    }

    /**
     * 设置R颜色
     */
    public void setWR1Color(int color) {
        mWRDraw.setWR1Color(color);
    }

    public void setWR2Color(int color) {
        mWRDraw.setWR2Color(color);
    }

    public void setWR3Color(int color) {
        mWRDraw.setWR3Color(color);
    }

    /**
     * 设置ma1颜色
     *
     * @param color
     */
    public void setMa1Color(int color) {
        mMainDraw.setMa1Color(color);
        mVolumeDraw.setMa5Color(color);
    }

    /**
     * 设置ma2颜色
     *
     * @param color
     */
    public void setMa2Color(int color) {
        mMainDraw.setMa2Color(color);
        mVolumeDraw.setMa10Color(color);
    }

    /**
     * 设置ma3颜色
     *
     * @param color
     */
    public void setMa3Color(int color) {
        mMainDraw.setMa3Color(color);
    }

    /**
     * 设置ma4颜色
     *
     * @param color
     */
    public void setMa4Color(int color) {
        mMainDraw.setMa4Color(color);
    }

    /**
     * 设置ma5颜色
     *
     * @param color
     */
    public void setMa5Color(int color) {
        mMainDraw.setMa5Color(color);
    }

    /**
     * 设置ma6颜色
     *
     * @param color
     */
    public void setMa6Color(int color) {
        mMainDraw.setMa6Color(color);
    }

    /**
     * 设置选择器文字大小
     *
     * @param textSize
     */
    public void setSelectorTextSize(float textSize) {
        mMainDraw.setSelectorTextSize(textSize);
    }

    /**
     * 设置选择器背景
     *
     * @param color
     */
    public void setSelectorBackgroundColor(int color) {
        mMainDraw.setSelectorBackgroundColor(color);
    }

    /**
     * 设置蜡烛宽度
     *
     * @param candleWidth
     */
    public void setCandleWidth(float candleWidth) {
        mMainDraw.setCandleWidth(candleWidth);
    }

    /**
     * 上下影线的宽度
     *
     * @param candleLineWidth
     */
    public void setCandleLineWidth(float candleLineWidth) {
        mMainDraw.setCandleLineWidth(candleLineWidth);
    }

    /**
     * 蜡烛是否空心
     */
    public void setCandleSolid(boolean candleSolid) {
        mMainDraw.setCandleSolid(candleSolid);
    }

    public void setRSI1Color(int color) {
        mRSIDraw.setRSI1Color(color);
    }

    public void setRSI2Color(int color) {
        mRSIDraw.setRSI2Color(color);
    }

    public void setRSI3Color(int color) {
        mRSIDraw.setRSI3Color(color);
    }

    @Override
    public void setTextSize(float textSize) {
        super.setTextSize(textSize);
        mMainDraw.setTextSize(textSize);
        mRSIDraw.setTextSize(textSize);
        mMACDDraw.setTextSize(textSize);
        mKDJDraw.setTextSize(textSize);
        mWRDraw.setTextSize(textSize);
        mVolumeDraw.setTextSize(textSize);
    }

    @Override
    public void setLineWidth(float lineWidth) {
        super.setLineWidth(lineWidth);
        mMainDraw.setLineWidth(lineWidth);
        mRSIDraw.setLineWidth(lineWidth);
        mMACDDraw.setLineWidth(lineWidth);
        mKDJDraw.setLineWidth(lineWidth);
        mWRDraw.setLineWidth(lineWidth);
        mVolumeDraw.setLineWidth(lineWidth);
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        mMainDraw.setSelectorTextColor(color);
    }

    /**
     * 设置刷新监听
     */
    public void setRefreshListener(KChartRefreshListener refreshListener) {
        mRefreshListener = refreshListener;
    }

    public void setMainDrawLine(boolean isLine) {
        mMainDraw.setLine(isLine);
        invalidate();
    }

    private int startX;
    private int startY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) ev.getX();
                startY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dX = (int) (ev.getX() - startX);
                int dY = (int) (ev.getY() - startX);
                if (Math.abs(dX) > Math.abs(dY)) {
                    //左右滑动
                    return true;
                } else {
                    //上下滑动
                    return false;
                }
            case MotionEvent.ACTION_UP:
                break;
            default:
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (!isRefreshing) {
            return super.onDown(e);
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (!isRefreshing) {
            super.onLongPress(e);
        }
    }
}
