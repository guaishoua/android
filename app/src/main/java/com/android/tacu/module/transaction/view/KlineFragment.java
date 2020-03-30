package com.android.tacu.module.transaction.view;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.TradeVisibleHintEvent;
import com.github.tifezh.kchartlib.chart.KLineChartView;
import com.github.tifezh.kchartlib.chart.adapter.KLineChartAdapter;
import com.github.tifezh.kchartlib.chart.entity.KLineEntity;
import com.github.tifezh.kchartlib.chart.interfaces.OnChartEventListener;
import com.github.tifezh.kchartlib.chart.utils.DataHelper;
import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.interfaces.ISocketEvent;
import com.android.tacu.module.market.contract.MarketDetailsContract;
import com.android.tacu.module.market.model.CurrentTradeCoinModel;
import com.android.tacu.module.market.model.KLineModel;
import com.android.tacu.module.market.presenter.MarketDetailsPresenter;
import com.android.tacu.socket.BaseSocketManager;
import com.android.tacu.socket.ObserverModel;
import com.android.tacu.socket.SocketConstant;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.widget.tab.TabLayoutView;
import com.android.tacu.widget.tab.TabPopup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;

public class KlineFragment extends BaseFragment<MarketDetailsPresenter> implements ISocketEvent, Observer, MarketDetailsContract.IKlineView {

    @BindView(R.id.tv_news_price)
    TextView tvNewsPrice;
    @BindView(R.id.tv_rmb_scale)
    TextView tvRmbScale;
    @BindView(R.id.tv_change_rate)
    TextView tvChangeRate;
    @BindView(R.id.tv_high)
    TextView tvHighPrice;
    @BindView(R.id.tv_low)
    TextView tvLowPrice;
    @BindView(R.id.lin_indicator)
    TabLayoutView linIndicator;
    @BindView(R.id.kchart_view)
    KLineChartView mKChartView;

    private static long MIN_1 = 60000L;
    private static long MIN_5 = 300000L;
    private static long MIN_15 = 900000L;
    private static long MIN_30 = 1800000L;
    private static long HOUR_1 = 3600000L;
    private static long DAY_1 = 86400000L;

    private int currencyId;
    private int baseCurrencyId;
    private String currencyNameEn;
    private String baseCurrencyNameEn;

    private ItemTouch itemTouch;

    private KLineChartAdapter kAdapter;
    private int pointPrice;
    //防止socket刷新频繁
    private int pointPriceTemp;

    private final long CHART_TIME = HOUR_1;
    private static final int KREFRESH_TIME = 1000 * 30;
    private long chartTime = CHART_TIME;

    private TabPopup timePopUp;

    private boolean isAnim = true;
    private boolean isFirst = true;
    private boolean isVisibleToUserTrade = false;
    private boolean isVisibleToUserQuotation = false;

    //时间
    private List<String> tabTitle = new ArrayList<>();
    private List<Long> timeTitle = new ArrayList<>();

    private Handler kHandler = new Handler();
    private Runnable kRunnable = new Runnable() {
        @Override
        public void run() {
            upLoad(isFirst);
            //每隔2分钟循环执行run方法
            if (kHandler != null) {
                kHandler.postDelayed(this, KREFRESH_TIME);
            }
        }
    };

    public static KlineFragment newInstance(int currencyId, int baseCurrencyId, String currencyNameEn, String baseCurrencyNameEn) {
        Bundle bundle = new Bundle();
        bundle.putInt("currencyId", currencyId);
        bundle.putInt("baseCurrencyId", baseCurrencyId);
        bundle.putString("currencyNameEn", currencyNameEn);
        bundle.putString("baseCurrencyNameEn", baseCurrencyNameEn);
        KlineFragment fragment = new KlineFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            currencyId = bundle.getInt("currencyId");
            baseCurrencyId = bundle.getInt("baseCurrencyId");
            currencyNameEn = bundle.getString("currencyNameEn");
            baseCurrencyNameEn = bundle.getString("baseCurrencyNameEn");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_kline;
    }

    @Override
    protected void initData(View view) {
        setSocketEvent(this, this, SocketConstant.LOGINAFTERCHANGETRADECOIN);

        //分时
        linIndicator.addImageView();
        linIndicator.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default), ContextCompat.getColor(getContext(), R.color.text_grey_2));
        linIndicator.addTab(getResources().getString(R.string.target));
        linIndicator.addTab(getResources().getString(R.string.hour_1));
        linIndicator.setOnTabSelectListener(new TabLayoutView.TabSelectListener() {
            @Override
            public void onTabSelected(int position) {
                //因为先添加了一个子控件（横竖屏切换的）所以tabview从1开始
                if (position == 1) {
                } else if (position == 2) {
                    initTimePopUp(linIndicator);
                }
            }
        });

        tabTitle.add(getResources().getString(R.string.min_1));
        tabTitle.add(getResources().getString(R.string.min_5));
        tabTitle.add(getResources().getString(R.string.min_15));
        tabTitle.add(getResources().getString(R.string.min_30));
        tabTitle.add(getResources().getString(R.string.hour_1));
        tabTitle.add(getResources().getString(R.string.day_1));

        timeTitle.add(MIN_1);
        timeTitle.add(MIN_5);
        timeTitle.add(MIN_15);
        timeTitle.add(MIN_30);
        timeTitle.add(HOUR_1);
        timeTitle.add(DAY_1);

        kAdapter = new KLineChartAdapter();
        mKChartView.setAdapter(kAdapter);
        mKChartView.setOnChartEventListener(new OnChartEventListener() {
            @Override
            public void onChartTouchListener(boolean boo) {
                //不允许ScrollView截断点击事件，点击事件由子View处理
                if (itemTouch != null) {
                    itemTouch.parentTouch(boo);
                }
            }
        });
    }

    @Override
    protected MarketDetailsPresenter createPresenter(MarketDetailsPresenter mPresenter) {
        return new MarketDetailsPresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (kHandler != null && kRunnable != null) {
            kHandler.post(kRunnable);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (kHandler != null && kRunnable != null) {
            kHandler.removeCallbacks(kRunnable);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (kHandler != null && kRunnable != null) {
            kHandler.removeCallbacks(kRunnable);
        }
    }

    @Override
    public void socketConnectEventAgain() {
        if (baseAppSocket != null) {
            baseAppSocket.coinInfo(currencyId, baseCurrencyId);
        }
    }

    @Override
    protected void receiveEvent(BaseEvent event) {
        super.receiveEvent(event);
        if (event != null) {
            switch (event.getCode()) {
                case EventConstant.TradeVisibleCode:
                    TradeVisibleHintEvent tradeVisibleHintEvent = (TradeVisibleHintEvent) event.getData();
                    isVisibleToUserTrade = tradeVisibleHintEvent.isVisibleToUser();
                    upLoad(true);
                    break;
            }
        }
    }

    @Override
    public void update(final Observable observable, final Object object) {
        getHostActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (observable instanceof BaseSocketManager) {
                    ObserverModel model = (ObserverModel) object;
                    switch (model.getEventType()) {
                        case SocketConstant.LOGINAFTERCHANGETRADECOIN:
                            ObserverModel.LoginAfterChangeTradeCoin coinInfo = model.getTradeCoin();
                            if (coinInfo != null) {
                                coinInfo(coinInfo.getCoinModel());
                            }
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void success(KLineModel model, long range, boolean isClear) {
        if (model != null && model.data != null && model.data.lines != null) {
            List<KLineEntity> data = new ArrayList<>();
            int count = model.data.lines.size();
            KLineModel.DataModel dataModel;
            KLineEntity kLineEntity;
            for (int i = 0; i < count; i++) {
                dataModel = model.data;
                kLineEntity = new KLineEntity();
                if (range >= DAY_1) {
                    kLineEntity.Date = DateUtils.millis2String(dataModel.lines.get(i).get(0).longValue(), DateUtils.FORMAT_DATE_YMD);
                } else {
                    kLineEntity.Date = DateUtils.millis2String(dataModel.lines.get(i).get(0).longValue(), DateUtils.FORMAT_DATE_HMS);
                }
                kLineEntity.Open = dataModel.lines.get(i).get(1);
                kLineEntity.High = dataModel.lines.get(i).get(2);
                kLineEntity.Low = dataModel.lines.get(i).get(3);
                kLineEntity.Close = dataModel.lines.get(i).get(4);
                kLineEntity.Volume = dataModel.lines.get(i).get(5);
                data.add(kLineEntity);
            }

            if (data != null) {
                if (isAnim) {
                    isAnim = false;
                }
                KLineChartView.decimalsCount = pointPrice;
                DataHelper.calculate(data);
                kAdapter.clearData();
                kAdapter.addFooterData(data);
                mKChartView.refreshEnd();
                if (isClear) {
                    if (!mKChartView.isFullScreen()) {
                        mKChartView.setScrollX(0);
                    } else {
                        mKChartView.setScrollColumnSpace();
                    }
                }
            }
        }
    }

    public void setValue(int currencyId, int baseCurrencyId, String currencyNameEn, String baseCurrencyNameEn) {
        this.currencyId = currencyId;
        this.baseCurrencyId = baseCurrencyId;
        this.currencyNameEn = currencyNameEn;
        this.baseCurrencyNameEn = baseCurrencyNameEn;

        socketConnectEventAgain();
        upLoad(true);
    }

    public void setQuotationVisible(boolean isVisibleToUserQuotation) {
        this.isVisibleToUserQuotation = isVisibleToUserQuotation;
        upLoad(true);
    }

    /**
     * 请求K线数据
     */
    private void upLoad(boolean isClear) {
        if (!isVisibleToUserTrade || !isVisibleToUserQuotation || !isVisibleToUser) {
            return;
        }
        if (isAnim) {
            kAdapter.clearDataAndNotify();
            mKChartView.resetLoadMoreEnd();
            mKChartView.showLoading();
        }
        mPresenter.getBestexKline((currencyNameEn + baseCurrencyNameEn).toLowerCase(), chartTime, 2, isClear);
        if (isFirst) {
            isFirst = false;
        }
    }

    private void coinInfo(CurrentTradeCoinModel model) {
        if (model != null) {
            pointPrice = model.currentTradeCoin.pointPrice;

            if (kAdapter != null && pointPrice != pointPriceTemp) {
                KLineChartView.decimalsCount = pointPrice;
                pointPriceTemp = pointPrice;
                kAdapter.notifyDataSetChanged();
            }
            tvLowPrice.setText(getResources().getString(R.string.lower) + BigDecimal.valueOf(model.currentTradeCoin.lowPrice).setScale(pointPrice, BigDecimal.ROUND_DOWN).toPlainString());
            tvHighPrice.setText(getResources().getString(R.string.higher) + BigDecimal.valueOf(model.currentTradeCoin.highPrice).setScale(pointPrice, BigDecimal.ROUND_DOWN).toPlainString());
            String changeRate = BigDecimal.valueOf(model.currentTradeCoin.changeRate).toPlainString();
            tvNewsPrice.setText(BigDecimal.valueOf(model.currentTradeCoin.currentAmount).setScale(pointPrice, BigDecimal.ROUND_DOWN).toPlainString());
            tvRmbScale.setText("≈" + getMcM(baseCurrencyId, model.currentTradeCoin.currentAmount));

            if (model.currentTradeCoin.changeRate >= 0) {
                tvChangeRate.setTextColor(ContextCompat.getColor(getContext(), R.color.color_riseup));
                tvChangeRate.setText("+" + changeRate + "%");
            } else {
                tvChangeRate.setTextColor(ContextCompat.getColor(getContext(), R.color.color_risedown));
                tvChangeRate.setText(changeRate + "%");
            }
        }
    }

    private void initTimePopUp(View view) {
       /* if (timePopUp != null && timePopUp.isShowing()) {
            timePopUp.dismiss();
            return;
        }
        if (timePopUp == null) {
            timePopUp = new TabPopup(getContext(), 0);
            timePopUp.create(ContextCompat.getColor(getContext(), R.color.text_color), ContextCompat.getColor(getContext(), R.color.content_bg_color), UIUtils.getScreenWidth() - UIUtils.dp2px(20), UIUtils.dp2px(40), 6, tabTitle, new TabPopup.TabItemSelect() {
                @Override
                public void onTabItemSelectListener(int position) {
                    chartTime = timeTitle.get(position);
                    linIndicator.setTabText(2, tabTitle.get(position));
                    isAnim = true;
                    upLoad(true);
                    timePopUp.dismiss();
                }
            });
        }
        timePopUp.showAsDropDown(view, UIUtils.dp2px(10), 0);*/
    }

    public interface ItemTouch {
        void parentTouch(boolean boo);
    }

    public void setItemTouch(ItemTouch itemTouch) {
        this.itemTouch = itemTouch;
    }
}
