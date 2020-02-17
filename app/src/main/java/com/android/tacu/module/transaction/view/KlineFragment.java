package com.android.tacu.module.transaction.view;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.github.tifezh.kchartlib.chart.KChartView;
import com.github.tifezh.kchartlib.chart.adapter.KChartAdapter;
import com.github.tifezh.kchartlib.chart.entity.KLineEntity;
import com.github.tifezh.kchartlib.chart.interfaces.OnChartEventListener;
import com.github.tifezh.kchartlib.utils.DataHelper;
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
    KChartView mKChartView;

    private int currencyId;
    private int baseCurrencyId;
    private String currencyNameEn;
    private String baseCurrencyNameEn;

    private ItemTouch itemTouch;

    private KChartAdapter kAdapter;
    private int pointPrice;
    //防止socket刷新频繁
    private int pointPriceTemp;

    private final long CHART_TIME = 3600000;
    private static final int KREFRESH_TIME = 1000 * 30;
    private long chartTime = CHART_TIME;

    private TabPopup targetPopUp;
    private TabPopup timePopUp;

    private boolean isAnim = true;

    //时间
    private List<String> tabTitle = new ArrayList<>();
    private List<Long> timeTitle = new ArrayList<>();

    private Handler kHandler = new Handler();
    private Runnable kRunnable = new Runnable() {
        @Override
        public void run() {
            upLoad();
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
                    initTargetPopUp(linIndicator);
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

        timeTitle.add(60000L);
        timeTitle.add(300000L);
        timeTitle.add(900000L);
        timeTitle.add(1800000L);
        timeTitle.add(3600000L);
        timeTitle.add(86400000L);

        kAdapter = new KChartAdapter();
        mKChartView.setAdapter(kAdapter);
        mKChartView.setGridRows(3);
        mKChartView.setGridColumns(3);
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
    public void success(KLineModel model) {
        if (model != null && model.data != null && model.data.lines != null) {
            List<KLineEntity> data = new ArrayList<>();
            int count = model.data.lines.size();
            KLineModel.DataModel dataModel;
            KLineEntity kLineEntity;
            for (int i = 0; i < count; i++) {
                dataModel = model.data;
                kLineEntity = new KLineEntity();
                kLineEntity.Date = DateUtils.millis2String(dataModel.lines.get(i).get(0).longValue(), DateUtils.DEFAULT_PATTERN);
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
                KChartView.decimalsCount = pointPrice;
                DataHelper.calculate(data);
                kAdapter.clearData();
                kAdapter.addFooterData(data);
                mKChartView.refreshEnd();
            }
        }
    }

    public void setValue(int currencyId, int baseCurrencyId, String currencyNameEn, String baseCurrencyNameEn) {
        this.currencyId = currencyId;
        this.baseCurrencyId = baseCurrencyId;
        this.currencyNameEn = currencyNameEn;
        this.baseCurrencyNameEn = baseCurrencyNameEn;

        socketConnectEventAgain();
        upLoad();
    }

    /**
     * 请求K线数据
     */
    private void upLoad() {
        if (isAnim) {
            kAdapter.clearDataAndNotify();
            mKChartView.resetLoadMoreEnd();
            mKChartView.showLoading();
        }
        mPresenter.getBestexKline((currencyNameEn + baseCurrencyNameEn).toLowerCase(), chartTime, 2);
    }

    private void coinInfo(CurrentTradeCoinModel model) {
        if (model != null) {
            pointPrice = model.currentTradeCoin.pointPrice;

            if (kAdapter != null && pointPrice != pointPriceTemp) {
                KChartView.decimalsCount = pointPrice;
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

    private void initTargetPopUp(View view) {
        if (targetPopUp != null && targetPopUp.isShowing()) {
            targetPopUp.dismiss();
            return;
        }
        if (timePopUp != null && timePopUp.isShowing()) {
            timePopUp.dismiss();
        }
        if (targetPopUp == null) {
            targetPopUp = new TabPopup(getContext(), 0);
            targetPopUp.create(ContextCompat.getColor(getContext(), R.color.text_color), ContextCompat.getColor(getContext(), R.color.content_bg_color), UIUtils.getScreenWidth() - UIUtils.dp2px(20), UIUtils.dp2px(40), 6, mKChartView.getmChildName(), new TabPopup.TabItemSelect() {
                @Override
                public void onTabItemSelectListener(int position) {
                    mKChartView.setChildDraw(position);
                    targetPopUp.dismiss();
                }
            });
        }
        targetPopUp.setWidthAndHeight(UIUtils.getScreenWidth() - UIUtils.dp2px(20), UIUtils.dp2px(40));
        targetPopUp.showAsDropDown(view, UIUtils.dp2px(10), 0);
    }

    private void initTimePopUp(View view) {
        if (timePopUp != null && timePopUp.isShowing()) {
            timePopUp.dismiss();
            return;
        }
        if (targetPopUp != null && targetPopUp.isShowing()) {
            targetPopUp.dismiss();
        }
        if (timePopUp == null) {
            timePopUp = new TabPopup(getContext(), 0);
            timePopUp.create(ContextCompat.getColor(getContext(), R.color.text_color), ContextCompat.getColor(getContext(), R.color.content_bg_color), UIUtils.getScreenWidth() - UIUtils.dp2px(20), UIUtils.dp2px(40), 6, tabTitle, new TabPopup.TabItemSelect() {
                @Override
                public void onTabItemSelectListener(int position) {
                    chartTime = timeTitle.get(position);
                    linIndicator.setTabText(2, tabTitle.get(position));
                    isAnim = true;
                    upLoad();
                    timePopUp.dismiss();
                }
            });
        }
        timePopUp.setWidthAndHeight(UIUtils.getScreenWidth() - UIUtils.dp2px(20), UIUtils.dp2px(40));
        timePopUp.showAsDropDown(view, UIUtils.dp2px(10), 0);
    }

    public interface ItemTouch {
        void parentTouch(boolean boo);
    }

    public void setItemTouch(ItemTouch itemTouch) {
        this.itemTouch = itemTouch;
    }
}
