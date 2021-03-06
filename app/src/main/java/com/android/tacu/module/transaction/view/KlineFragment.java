package com.android.tacu.module.transaction.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.android.tacu.utils.KlineUtils;
import com.android.tacu.widget.tab.TabLayoutView;
import com.github.tifezh.kchartlib.chart.KLineChartView;
import com.github.tifezh.kchartlib.chart.adapter.KLineChartAdapter;
import com.github.tifezh.kchartlib.chart.entity.KLineEntity;
import com.github.tifezh.kchartlib.chart.interfaces.OnChartEventListener;
import com.github.tifezh.kchartlib.chart.utils.DataHelper;
import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.market.contract.MarketDetailsContract;
import com.android.tacu.module.market.model.CurrentTradeCoinModel;
import com.android.tacu.module.market.model.KLineModel;
import com.android.tacu.module.market.presenter.MarketDetailsPresenter;
import com.android.tacu.socket.BaseSocketManager;
import com.android.tacu.socket.ObserverModel;
import com.android.tacu.socket.SocketConstant;

import java.math.BigDecimal;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;

import static android.app.Activity.RESULT_OK;
import static com.android.tacu.module.market.view.MarketDetailsActivity.KREFRESH_TIME;
import static com.android.tacu.module.market.view.MarketDetailsActivity.REQUESTCODE;

public class KlineFragment extends BaseFragment<MarketDetailsPresenter> implements Observer, MarketDetailsContract.IKlineView {

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

    private int currencyId;
    private int baseCurrencyId;
    private String currencyNameEn;
    private String baseCurrencyNameEn;

    private ItemTouch itemTouch;

    private KLineChartAdapter kAdapter;
    private int pointPrice;
    //防止socket刷新频繁
    private int pointPriceTemp;

    private boolean isAnim = true;
    private boolean isFirst = true;

    private KLineModel kLineModel;
    private long klineRange;

    private CurrentTradeCoinModel currentTradeCoinModel;

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

            currentTradeCoinModel = TradeFragment.currentTradeCoinModel;
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_kline;
    }

    @Override
    protected void initData(View view) {
    }

    @Override
    protected MarketDetailsPresenter createPresenter(MarketDetailsPresenter mPresenter) {
        return new MarketDetailsPresenter();
    }

    @Override
    public void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();

        TradeFragment.tradeSocketManager.addObserver(this);

        linIndicator.setOnKChartView(mKChartView);
        linIndicator.setOnTabSelectListener(new TabLayoutView.TabSelectListener() {
            @Override
            public void onTabSelected() {
                isAnim = true;
                upLoad(true);
            }
        });
        linIndicator.setFlagFragment(1, this);

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

        coinInfo(currentTradeCoinModel);
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if (kHandler != null && kRunnable != null) {
            kHandler.post(kRunnable);
        }
    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();
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
        if (linIndicator != null) {
            linIndicator.clear();
        }
        System.gc();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUESTCODE && resultCode == RESULT_OK) {
            if (kLineModel != null && kLineModel.data != null && kLineModel.data.lines != null) {
                List<KLineEntity> data = KlineUtils.dealKlines(kLineModel, klineRange);
                if (data != null) {
                    DataHelper.calculate(data, getContext());
                    kAdapter.clearData();
                    kAdapter.addFooterData(data);
                    kAdapter.notifyInvalidated();
                }
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
                                currentTradeCoinModel = coinInfo.getCoinModel();
                                coinInfo(coinInfo.getCoinModel());
                            }
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void success(KLineModel model, String symbol, long range, boolean isClear) {
        this.kLineModel = model;
        this.klineRange = range;

        if (model != null && model.data != null && model.data.lines != null) {
            List<KLineEntity> data = KlineUtils.dealKlines(model, range);

            if (data != null) {
                if (isAnim) {
                    isAnim = false;
                }
                KLineChartView.decimalsCount = pointPrice;
                DataHelper.calculate(data, getContext());
                kAdapter.setCoinName(symbol);
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

        upLoad(true);
    }

    /**
     * 请求K线数据
     */
    private void upLoad(boolean isClear) {
        if (isAnim) {
            kAdapter.clearDataAndNotify();
            mKChartView.resetLoadMoreEnd();
            mKChartView.showLoading();
        }
        mPresenter.getBestexKline((currencyNameEn + baseCurrencyNameEn).toLowerCase(), linIndicator.getChartTime(), 2, isClear);
        if (isFirst) {
            isFirst = false;
        }
    }

    private void coinInfo(CurrentTradeCoinModel model) {
        if (model != null) {
            pointPrice = model.currentTradeCoin.pointPrice;

            if (kAdapter != null) {
                if (pointPrice != pointPriceTemp) {
                    KLineChartView.decimalsCount = pointPrice;
                    pointPriceTemp = pointPrice;
                    kAdapter.notifyDataSetChanged();
                }
            }
            if (model.currentTradeCoin.lowPrice != 0) {
                tvLowPrice.setText(getResources().getString(R.string.lower) + BigDecimal.valueOf(model.currentTradeCoin.lowPrice).setScale(pointPrice, BigDecimal.ROUND_DOWN).toPlainString());
            }
            if (model.currentTradeCoin.highPrice != 0) {
                tvHighPrice.setText(getResources().getString(R.string.higher) + BigDecimal.valueOf(model.currentTradeCoin.highPrice).setScale(pointPrice, BigDecimal.ROUND_DOWN).toPlainString());
            }
            String changeRate = BigDecimal.valueOf(model.currentTradeCoin.changeRate).toPlainString();
            if (model.currentTradeCoin.currentAmount != 0) {
                tvNewsPrice.setText(BigDecimal.valueOf(model.currentTradeCoin.currentAmount).setScale(pointPrice, BigDecimal.ROUND_DOWN).toPlainString());
                tvRmbScale.setText("≈" + getMcM(baseCurrencyId, model.currentTradeCoin.currentAmount));
            }

            if (model.currentTradeCoin.changeRate >= 0) {
                tvChangeRate.setTextColor(ContextCompat.getColor(getContext(), R.color.color_riseup));
                tvChangeRate.setText("+" + changeRate + "%");
            } else {
                tvChangeRate.setTextColor(ContextCompat.getColor(getContext(), R.color.color_risedown));
                tvChangeRate.setText(changeRate + "%");
            }
        }
    }

    public interface ItemTouch {
        void parentTouch(boolean boo);
    }

    public void setItemTouch(ItemTouch itemTouch) {
        this.itemTouch = itemTouch;
    }
}
