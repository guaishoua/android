package com.android.tacu.module.market.view;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.JumpTradeCodeIsBuyEvent;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.interfaces.ISocketEvent;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.module.main.view.MainActivity;
import com.android.tacu.module.market.contract.MarketDetailsContract;
import com.android.tacu.module.market.model.CurrentTradeCoinModel;
import com.android.tacu.module.market.model.KLineModel;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.market.model.SelfModel;
import com.android.tacu.module.market.presenter.MarketDetailsPresenter;
import com.android.tacu.socket.BaseSocketManager;
import com.android.tacu.socket.ObserverModel;
import com.android.tacu.socket.SocketConstant;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.widget.popupwindow.CoinPopWindow;
import com.android.tacu.widget.tab.TabLayoutView;
import com.github.tifezh.kchartlib.chart.KLineChartView;
import com.github.tifezh.kchartlib.chart.adapter.KLineChartAdapter;
import com.github.tifezh.kchartlib.chart.entity.KLineEntity;
import com.github.tifezh.kchartlib.chart.interfaces.OnChartEventListener;
import com.github.tifezh.kchartlib.chart.utils.DataHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.OnClick;

import static com.android.tacu.api.Constant.SELFCOIN_LIST;

public class MarketDetailsActivity extends BaseActivity<MarketDetailsPresenter> implements MarketDetailsContract.IView, ISocketEvent, Observer {

    public static final int REQUESTCODE = 1001;

    @BindView(R.id.scrollView)
    CoordinatorLayout scrollView;
    @BindView(R.id.lin_indicator)
    TabLayoutView linIndicator;
    @BindView(R.id.tv_news_price)
    TextView tvNewsPrice;
    @BindView(R.id.tv_rmb_scale)
    TextView tvRmbScale;
    @BindView(R.id.tv_change_rate)
    TextView tvChangeRate;
    @BindView(R.id.tv_volume)
    TextView tvVolume;
    @BindView(R.id.tv_highprice)
    TextView tvHighPrice;
    @BindView(R.id.tv_lowprice)
    TextView tvLowPrice;
    @BindView(R.id.kchart_view)
    KLineChartView mKChartView;
    @BindView(R.id.img_collect)
    ImageView imgCollect;
    @BindView(R.id.magic_indicator)
    FixedIndicatorView magicIndicator;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private TextView tvCenterTitle;
    //深度
    private MarketDetailDepthFragment marketDetailDepthFragment;
    //成交记录
    private MarketDetailHistoryFragment marketDetailHistoryFragment;

    //k线的请求 30s一次
    private static final int KREFRESH_TIME = 1000 * 30;

    //k线是否加载动画和网络请求动画
    private boolean isAnim = true;
    private boolean isFirst = true;
    private KLineChartAdapter kAdapter;

    private int pointPrice;
    private int currencyId;
    private int baseCurrencyId;
    private String currencyNameEn;
    private String baseCurrencyNameEn;
    private Gson gson = new Gson();

    private CoinPopWindow coinPopWindow;

    private CurrentTradeCoinModel currentTradeCoinModel;
    private IndicatorViewPager indicatorViewPager;

    //是否添加到自选
    private boolean isCollect = false;
    private SelfModel selfModel;

    private List<String> tabDownTitle = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<KLineEntity> kLineEntityList = new ArrayList<>();

    //防止socket刷新频繁
    private int pointPriceTemp;

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

    public static Intent createActivity(Context context, int currencyId, int baseCurrencyId, String currencyNameEn, String baseCurrencyNameEn) {
        Intent intent = new Intent(context, MarketDetailsActivity.class);
        intent.putExtra("currencyId", currencyId);
        intent.putExtra("baseCurrencyId", baseCurrencyId);
        intent.putExtra("currencyNameEn", currencyNameEn);
        intent.putExtra("baseCurrencyNameEn", baseCurrencyNameEn);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_market_details);
    }

    @Override
    protected void initView() {
        setSocketEvent(MarketDetailsActivity.this, MarketDetailsActivity.this, SocketConstant.LOGINAFTERCHANGETRADECOIN);

        currencyId = getIntent().getIntExtra("currencyId", Constant.TAC_CURRENCY_ID);
        baseCurrencyId = getIntent().getIntExtra("baseCurrencyId", Constant.ACU_CURRENCY_ID);
        currencyNameEn = getIntent().getStringExtra("currencyNameEn");
        baseCurrencyNameEn = getIntent().getStringExtra("baseCurrencyNameEn");

        tvCenterTitle = mTopBar.setTitle(currencyNameEn + "/" + baseCurrencyNameEn, R.drawable.icon_drop_down_white);
        tvCenterTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCoinPopUp(mTopBar);
            }
        });
        mTopBar.setBackgroundDividerEnabled(true);
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        linIndicator.setOnKChartView(mKChartView);
        linIndicator.setOnTabSelectListener(new TabLayoutView.TabSelectListener() {
            @Override
            public void onTabSelected() {
                isAnim = true;
                upLoad(true);
            }
        });

        kAdapter = new KLineChartAdapter();
        mKChartView.setAdapter(kAdapter);
        mKChartView.setOnChartEventListener(new OnChartEventListener() {
            @Override
            public void onChartTouchListener(boolean boo) {
                //不允许ScrollView截断点击事件，点击事件由子View处理
                scrollView.requestDisallowInterceptTouchEvent(boo);
            }
        });

        magicIndicator.setBackgroundColor(ContextCompat.getColor(this, R.color.color_kline));
        magicIndicator.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(this, R.color.tab_default), ContextCompat.getColor(this, R.color.text_white)).setSize(14, 14));
        magicIndicator.setScrollBar(new ColorBar(this, ContextCompat.getColor(this, R.color.tab_default), 4));
        magicIndicator.setSplitMethod(FixedIndicatorView.SPLITMETHOD_WRAP);

        tabDownTitle.add(getResources().getString(R.string.depth));
        tabDownTitle.add(getResources().getString(R.string.latest_transactions));

        marketDetailDepthFragment = MarketDetailDepthFragment.newInstance(currencyId, baseCurrencyId, currencyNameEn, baseCurrencyNameEn);
        marketDetailHistoryFragment = MarketDetailHistoryFragment.newInstance(currencyId, baseCurrencyId, 0);
        fragmentList.add(marketDetailDepthFragment);
        fragmentList.add(marketDetailHistoryFragment);

        indicatorViewPager = new IndicatorViewPager(magicIndicator, viewpager);
        indicatorViewPager.setAdapter(new TabAdapter(getSupportFragmentManager()));
        viewpager.setOffscreenPageLimit(fragmentList.size() - 1);
        viewpager.setCurrentItem(0, false);

        setCacheCoinInfo();
    }

    @Override
    protected MarketDetailsPresenter createPresenter(MarketDetailsPresenter mPresenter) {
        return new MarketDetailsPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCacheSelf();
        if (kHandler != null && kRunnable != null) {
            kHandler.post(kRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (kHandler != null && kRunnable != null) {
            kHandler.removeCallbacks(kRunnable);
        }
    }

    @Override
    protected void onDestroy() {
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE && resultCode == RESULT_OK) {
            DataHelper.calculate(kLineEntityList, MarketDetailsActivity.this);
            kAdapter.clearData();
            kAdapter.addFooterData(kLineEntityList);
            mKChartView.refreshEnd();
        }
    }

    @OnClick(R.id.btn_buy)
    void buy() {
        EventManage.sendEvent(new BaseEvent<>(EventConstant.JumpTradeIsBuyCode, new JumpTradeCodeIsBuyEvent(currencyId, baseCurrencyId, currencyNameEn, baseCurrencyNameEn, true)));
        jumpTo(MainActivity.class);
        finish();
    }

    @OnClick(R.id.btn_sell)
    void sell() {
        EventManage.sendEvent(new BaseEvent<>(EventConstant.JumpTradeIsBuyCode, new JumpTradeCodeIsBuyEvent(currencyId, baseCurrencyId, currencyNameEn, baseCurrencyNameEn, false)));
        jumpTo(MainActivity.class);
        finish();
    }

    @OnClick(R.id.img_collect)
    void selfCollectClick() {
        if (spUtil.getLogin()) {
            if (selfModel == null) {
                selfModel = new SelfModel();
            }
            collectSelf();
            mPresenter.uploadSelfList(gson.toJson(selfModel));
        } else {
            jumpTo(LoginActivity.class);
        }
    }

    @Override
    public void success(KLineModel model, long range, boolean isClear, boolean isLine) {
        if (model != null && model.data != null && model.data.lines != null) {
            List<KLineEntity> data = new ArrayList<>();
            int count = model.data.lines.size();
            KLineModel.DataModel dataModel;
            KLineEntity kLineEntity;
            for (int i = 0; i < count; i++) {
                dataModel = model.data;
                kLineEntity = new KLineEntity();
                if (range >= linIndicator.DAY_1) {
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
                this.kLineEntityList = data;
                if (isAnim) {
                    isAnim = false;
                }
                KLineChartView.decimalsCount = pointPrice;
                DataHelper.calculate(data, this);
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
                if (isLine) {
                    mKChartView.hideSelectData();
                    mKChartView.setMainDrawLine(true);
                }
            }
        }
    }

    @Override
    public void uploadSelfSuccess() {
        SPUtils.getInstance().put(SELFCOIN_LIST, gson.toJson(selfModel));
        if (isCollect) {
            showToastSuccess(getResources().getString(R.string.business_collect));
        } else {
            showToastSuccess(getResources().getString(R.string.business_uncollect));
        }
    }

    @Override
    public void uploadSelfError() {
        if (isCollect) {
            showToastError(getResources().getString(R.string.business_collect_error));
        } else {
            showToastError(getResources().getString(R.string.business_uncollect_error));
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
        runOnUiThread(new Runnable() {
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

    /**
     * 从缓存中拿到数据
     */
    private void setCacheCoinInfo() {
        String cacheString = SPUtils.getInstance().getString(Constant.SELECT_COIN_GROUP_CACHE);
        List<MarketNewModel> cacheList = new Gson().fromJson(cacheString, new TypeToken<List<MarketNewModel>>() {
        }.getType());
        if (cacheList != null && cacheList.size() > 0) {
            MarketNewModel.TradeCoinsBean bean = null;
            Flag:
            for (int i = 0; i < cacheList.size(); i++) {
                for (int j = 0; j < cacheList.get(i).tradeCoinsList.size(); j++) {
                    if (cacheList.get(i).tradeCoinsList.get(j).baseCurrencyId == baseCurrencyId && cacheList.get(i).tradeCoinsList.get(j).currencyId == currencyId) {
                        bean = cacheList.get(i).tradeCoinsList.get(j);
                        break Flag;
                    }
                }
            }
            if (bean != null) {
                CurrentTradeCoinModel model = new CurrentTradeCoinModel();
                CurrentTradeCoinModel.CurrentTradeCoinBean coinBean = new CurrentTradeCoinModel.CurrentTradeCoinBean();

                coinBean.coinCode = bean.coinCode;
                coinBean.baseCurrencyId = bean.baseCurrencyId;
                coinBean.baseCurrencyNameEn = bean.baseCurrencyNameEn;
                coinBean.currentAmount = bean.currentAmount;
                coinBean.highPrice = bean.highPrice;
                coinBean.lowPrice = bean.lowPrice;
                coinBean.openPrice = bean.openPrice;
                coinBean.closePrice = bean.closePrice;
                coinBean.yesterClosePirce = bean.yesterClosePirce;
                coinBean.changeRate = bean.changeRate;
                coinBean.changeAmount = bean.changeAmount;
                coinBean.volume = bean.volume;
                coinBean.amount = bean.amount;
                coinBean.currencyId = bean.currencyId;
                coinBean.currencyName = bean.currencyName;
                coinBean.currencyNameEn = bean.currencyNameEn;
                coinBean.pointNum = bean.pointNum;
                coinBean.pointPrice = bean.pointPrice;
                coinBean.previousPrice = bean.previousPrice;
                coinBean.buyFee = bean.buyFee;
                coinBean.sellFee = bean.sellFee;
                coinBean.amountHighLimit = bean.amountHighLimit;
                coinBean.amountLowLimit = bean.amountLowLimit;
                coinBean.entrustPriceMax = bean.entrustPriceMax;
                coinBean.entrustPriceMin = bean.entrustPriceMin;
                coinBean.relationStatus = bean.relationStatus;
                coinBean.entrustScale = bean.entrustScale;
                coinBean.rmbScale = bean.rmbScale;

                model.currentTradeCoin = coinBean;
                coinInfo(model);
            }
        }
    }

    private void coinInfo(CurrentTradeCoinModel model) {
        currentTradeCoinModel = model;
        if (model != null) {
            pointPrice = model.currentTradeCoin.pointPrice;

            if (kAdapter != null && pointPrice != pointPriceTemp) {
                KLineChartView.decimalsCount = pointPrice;
                pointPriceTemp = pointPrice;
                kAdapter.notifyDataSetChanged();
            }
            tvVolume.setText(BigDecimal.valueOf(model.currentTradeCoin.volume).setScale(2, BigDecimal.ROUND_DOWN).toPlainString());
            tvLowPrice.setText(BigDecimal.valueOf(model.currentTradeCoin.lowPrice).setScale(pointPrice, BigDecimal.ROUND_DOWN).toPlainString());
            tvHighPrice.setText(BigDecimal.valueOf(model.currentTradeCoin.highPrice).setScale(pointPrice, BigDecimal.ROUND_DOWN).toPlainString());
            String changeRate = BigDecimal.valueOf(model.currentTradeCoin.changeRate).toPlainString();
            tvNewsPrice.setText(BigDecimal.valueOf(model.currentTradeCoin.currentAmount).setScale(pointPrice, BigDecimal.ROUND_DOWN).toPlainString());
            tvRmbScale.setText("≈" + getMcM(baseCurrencyId, model.currentTradeCoin.currentAmount));

            if (model.currentTradeCoin.changeRate >= 0) {
                tvChangeRate.setTextColor(ContextCompat.getColor(this, R.color.color_riseup));
                tvChangeRate.setText("+" + changeRate + "%");
            } else {
                tvChangeRate.setTextColor(ContextCompat.getColor(this, R.color.color_risedown));
                tvChangeRate.setText(changeRate + "%");
            }
        }
        if (marketDetailDepthFragment != null) {
            marketDetailDepthFragment.setCurrentTradeCoinModel(currentTradeCoinModel);
        }
        if (marketDetailHistoryFragment != null) {
            marketDetailHistoryFragment.setCurrentTradeCoinModel(currentTradeCoinModel);
        }
    }

    private void initCacheSelf() {
        isCollect = false;
        imgCollect.setImageResource(R.drawable.icon_rating_uncollect);
        if (spUtil.getLogin()) {
            String tempString = SPUtils.getInstance().getString(SELFCOIN_LIST);
            selfModel = gson.fromJson(tempString, SelfModel.class);
            if (selfModel == null) {
                selfModel = new SelfModel();
            }
            if (selfModel != null && selfModel.checkedList != null && selfModel.checkedList.size() > 0) {
                for (int i = 0; i < selfModel.checkedList.size(); i++) {
                    if (TextUtils.equals(selfModel.checkedList.get(i).symbol, currencyId + "," + baseCurrencyId)) {
                        isCollect = true;
                        break;
                    }
                }
            }
            if (isCollect) {
                imgCollect.setImageResource(R.drawable.icon_rating_collect);
            } else {
                imgCollect.setImageResource(R.drawable.icon_rating_uncollect);
            }
        }
    }

    private void collectSelf() {
        SelfModel.SymbolBean bean = new SelfModel.SymbolBean();
        for (int i = 0; i < selfModel.checkedList.size(); i++) {
            if (TextUtils.equals(selfModel.checkedList.get(i).symbol, currencyId + "," + baseCurrencyId)) {
                bean = selfModel.checkedList.get(i);
                break;
            }
        }
        if (isCollect) {
            isCollect = false;
            imgCollect.setImageResource(R.drawable.icon_rating_uncollect);
            selfModel.checkedList.remove(bean);
        } else {
            isCollect = true;
            imgCollect.setImageResource(R.drawable.icon_rating_collect);
            SelfModel.SymbolBean symbolBean = new SelfModel.SymbolBean();
            symbolBean.symbol = currencyId + "," + baseCurrencyId;
            selfModel.checkedList.add(0, symbolBean);
        }
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
        mPresenter.getBestexKline((currencyNameEn + baseCurrencyNameEn).toLowerCase(), linIndicator.getChartTime(), 1, isClear, linIndicator.getIsLine());
        if (isFirst) {
            isFirst = false;
        }
    }

    private class TabAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

        public TabAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return tabDownTitle != null ? tabDownTitle.size() : 0;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.view_tab, container, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(tabDownTitle.get(position));
            int padding = UIUtils.dp2px(10);
            textView.setPadding(padding, 0, padding, 0);
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            //这是ViewPager适配器的特点,有两个值 POSITION_NONE，POSITION_UNCHANGED，默认就是POSITION_UNCHANGED,
            // 表示数据没变化不用更新.notifyDataChange的时候重新调用getViewForPage
            return PagerAdapter.POSITION_UNCHANGED;
        }
    }

    private void initCoinPopUp(View view) {
        if (coinPopWindow != null && coinPopWindow.isShowing()) {
            coinPopWindow.dismiss();
            return;
        }

        if (coinPopWindow == null) {
            coinPopWindow = new CoinPopWindow(this);
            coinPopWindow.create(UIUtils.getScreenWidth(), UIUtils.getScreenHeight() / 2, new CoinPopWindow.TabItemSelect() {
                @Override
                public void coinClick(int currencyId, int baseCurrencyId, String currencyNameEn, String baseCurrencyNameEn) {
                    setCoinInfo(currencyId, baseCurrencyId, currencyNameEn, baseCurrencyNameEn);
                    coinPopWindow.dismiss();
                }
            });
        }
        coinPopWindow.notifyCoinInfo();
        coinPopWindow.showAsDropDown(view, 0, 0);
    }

    private void setCoinInfo(int currencyId, int baseCurrencyId, String currencyNameEn, String baseCurrencyNameEn) {
        this.currencyId = currencyId;
        this.baseCurrencyId = baseCurrencyId;
        this.currencyNameEn = currencyNameEn;
        this.baseCurrencyNameEn = baseCurrencyNameEn;

        isAnim = true;
        tvCenterTitle.setText(currencyNameEn + "/" + baseCurrencyNameEn);

        upLoad(true);
        socketConnectEventAgain();
        setCacheCoinInfo();

        if (marketDetailDepthFragment != null) {
            marketDetailDepthFragment.setValue(currencyId, baseCurrencyId, currencyNameEn, baseCurrencyNameEn);
        }
        if (marketDetailHistoryFragment != null) {
            marketDetailHistoryFragment.setValue(currencyId, baseCurrencyId);
        }
    }
}
