package com.android.tacu.module.market.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.android.tacu.utils.KShareUtils;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.utils.ScreenShareHelper;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.widget.popupwindow.CoinPopWindow;
import com.android.tacu.widget.tab.TabLayoutView;
import com.android.tacu.widget.tab.TabPopup;
import com.github.tifezh.kchartlib.chart.KChartView;
import com.github.tifezh.kchartlib.chart.adapter.KChartAdapter;
import com.github.tifezh.kchartlib.chart.entity.KLineEntity;
import com.github.tifezh.kchartlib.utils.DataHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qmuiteam.qmui.alpha.QMUIAlphaImageButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundRelativeLayout;
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

    @BindView(R.id.layout_kline)
    RelativeLayout layout_kline;
    @BindView(R.id.rl_coin)
    RelativeLayout rlCoinDetail;
    @BindView(R.id.appbar_layout)
    AppBarLayout appbar_layout;
    @BindView(R.id.lin_indicator)
    TabLayoutView linIndicator;
    @BindView(R.id.rl_kview)
    QMUIRoundRelativeLayout rlKView;
    @BindView(R.id.rl_bottom)
    LinearLayout rlBottom;
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
    KChartView mKChartView;
    @BindView(R.id.btn_buy)
    QMUIRoundButton btnBuy;
    @BindView(R.id.btn_sell)
    QMUIRoundButton btnSell;
    @BindView(R.id.img_collect)
    ImageView imgCollect;
    @BindView(R.id.magic_indicator)
    FixedIndicatorView magicIndicator;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private TextView tvCenterTitle;
    private QMUIAlphaImageButton btnRight;

    //深度
    private MarketDetailDepthFragment marketDetailDepthFragment;
    //成交记录
    private MarketDetailHistoryFragment marketDetailHistoryFragment;

    //k线的请求 30s一次
    private static final int KREFRESH_TIME = 1000 * 30;

    //k线是否加载动画和网络请求动画
    private boolean isAnim = true;
    private KChartAdapter kAdapter;

    //时间
    private List<String> tabTitle = new ArrayList<>();
    private List<Long> timeTitle = new ArrayList<>();
    //默认1小时
    private final long CHART_TIME = 3600000;
    private long chartTime = CHART_TIME;
    private int chartIndex = 4;

    private int pointPrice;
    private int currencyId;
    private int baseCurrencyId;
    private String currencyNameEn;
    private String baseCurrencyNameEn;
    private Gson gson = new Gson();

    private TabPopup targetPopUp;
    private TabPopup timePopUp;

    private CurrentTradeCoinModel currentTradeCoinModel;
    private IndicatorViewPager indicatorViewPager;

    private KShareUtils shareUtil;
    private CoinPopWindow coinPopWindow;

    //是否添加到自选
    private boolean isCollect = false;
    private SelfModel selfModel;

    private Bitmap bitmapZxing;
    private Bitmap bitmapPart;
    private Bitmap bitmapScreenShoot;

    private List<String> tabDownTitle = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();

    //防止socket刷新频繁
    private int pointPriceTemp;

    private ScreenShareHelper screenShareHelper;

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

        currencyId = getIntent().getIntExtra("currencyId", Constant.BTC_CURRENCY_ID);
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
        mTopBar.setBackgroundDividerEnabled(false);
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MarketDetailsActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    setScreenHorizontalCancle();
                } else {
                    finish();
                }
            }
        });
        btnRight = mTopBar.addRightImageButton(0, R.id.qmui_topbar_item_right, 22, 22);
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MarketDetailsActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    setScreenHorizontalCancle();
                } else {
                    if (screenShareHelper == null) {
                        screenShareHelper = new ScreenShareHelper(MarketDetailsActivity.this);
                    }
                    screenShareHelper.invoke(layout_kline);
                }
            }
        });

        //分时
        linIndicator.addImageView();
        linIndicator.setTextColor(ContextCompat.getColor(this, R.color.text_default), ContextCompat.getColor(this, R.color.text_white));
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
        linIndicator.setOnLargeSelectListener(new TabLayoutView.EnLargeSelectListener() {
            @Override
            public void onLargeSelected() {
                boolean isVertical = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
                if (isVertical) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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

        //获取之前选择的时间段
        chartTime = SPUtils.getInstance().getLong(Constant.MARKET_DETAIL_TIME, CHART_TIME);
        for (int i = 0; i < timeTitle.size(); i++) {
            if (chartTime == timeTitle.get(i)) {
                chartIndex = i;
                linIndicator.setTabText(2, tabTitle.get(i));
                break;
            }
        }

        kAdapter = new KChartAdapter();
        mKChartView.setAdapter(kAdapter);
        mKChartView.setGridRows(3);
        mKChartView.setGridColumns(3);
        setKLineWidhtAndHeight(1);

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
        if (bitmapZxing != null && !bitmapZxing.isRecycled()) {
            bitmapZxing.recycle();
            bitmapZxing = null;
        }
        if (bitmapPart != null && !bitmapPart.isRecycled()) {
            bitmapPart.recycle();
            bitmapPart = null;
        }
        if (bitmapScreenShoot != null && !bitmapScreenShoot.isRecycled()) {
            bitmapScreenShoot.recycle();
            bitmapScreenShoot = null;
        }
        if (shareUtil != null) {
            shareUtil.clearBitmap();
            shareUtil = null;
        }
        if (targetPopUp != null && targetPopUp.isShowing()) {
            targetPopUp.dismiss();
            targetPopUp = null;
        }
        if (timePopUp != null && timePopUp.isShowing()) {
            timePopUp.dismiss();
            timePopUp = null;
        }
        System.gc();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        View mAppBarChildAt = appbar_layout.getChildAt(0);
        AppBarLayout.LayoutParams mAppBarParams = (AppBarLayout.LayoutParams) mAppBarChildAt.getLayoutParams();

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            rlCoinDetail.setVisibility(View.GONE);
            rlBottom.setVisibility(View.GONE);
            btnRight.setImageResource(R.drawable.icon_close_default);
            magicIndicator.setVisibility(View.GONE);
            viewpager.setVisibility(View.GONE);
            setKLineWidhtAndHeight(2);

            mAppBarParams.setScrollFlags(0);

            mKChartView.setGridRows(3);
            mKChartView.setGridColumns(6);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
            rlCoinDetail.setVisibility(View.VISIBLE);
            rlBottom.setVisibility(View.VISIBLE);
            //btnRight.setImageResource(R.drawable.icon_share_white);
            btnRight.setImageResource(0);
            magicIndicator.setVisibility(View.VISIBLE);
            viewpager.setVisibility(View.VISIBLE);
            setKLineWidhtAndHeight(1);

            mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);
            mAppBarChildAt.setLayoutParams(mAppBarParams);

            mKChartView.setGridRows(4);
            mKChartView.setGridColumns(3);
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
                KChartView.decimalsCount = pointPrice;
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
     * 取消横屏
     */
    private void setScreenHorizontalCancle() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
            targetPopUp = new TabPopup(this, 0);
            targetPopUp.create(ContextCompat.getColor(this, R.color.text_color), ContextCompat.getColor(this, R.color.content_bg_color), UIUtils.getScreenWidth(), UIUtils.dp2px(40), 6, mKChartView.getmChildName(), new TabPopup.TabItemSelect() {
                @Override
                public void onTabItemSelectListener(int position) {
                    mKChartView.setChildDraw(position);
                    targetPopUp.dismiss();
                }
            });
        }
        targetPopUp.setWidthAndHeight(UIUtils.getScreenWidth(), UIUtils.dp2px(40));
        targetPopUp.showAsDropDown(view, 0, 0);
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
            timePopUp = new TabPopup(this, chartIndex);
            timePopUp.create(ContextCompat.getColor(this, R.color.text_color), ContextCompat.getColor(this, R.color.content_bg_color), UIUtils.getScreenWidth(), UIUtils.dp2px(40), 6, tabTitle, new TabPopup.TabItemSelect() {
                @Override
                public void onTabItemSelectListener(int position) {
                    chartTime = timeTitle.get(position);
                    //保存用户当前的选择
                    SPUtils.getInstance().put(Constant.MARKET_DETAIL_TIME, chartTime);

                    linIndicator.setTabText(2, tabTitle.get(position));
                    isAnim = true;
                    upLoad();
                    timePopUp.dismiss();
                }
            });
        }
        timePopUp.setWidthAndHeight(UIUtils.getScreenWidth(), UIUtils.dp2px(40));
        timePopUp.showAsDropDown(view, UIUtils.dp2px(0), 0);
    }

    /**
     * 设置K线的宽高
     * flag : 1代表竖屏  2代表横屏
     */
    private void setKLineWidhtAndHeight(int flag) {
        int screenHeight = UIUtils.getScreenHeight();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rlKView.getLayoutParams();
        layoutParams.width = UIUtils.getScreenWidth() - UIUtils.dp2px(20);
        if (flag == 1) {
            layoutParams.height = (screenHeight - UIUtils.dp2px(120)) * 2 / 3;
        } else if (flag == 2) {
            layoutParams.height = screenHeight - UIUtils.dp2px(120);
        }
        rlKView.setLayoutParams(layoutParams);
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
        mPresenter.getBestexKline((currencyNameEn + baseCurrencyNameEn).toLowerCase(), chartTime, 1);
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

        upLoad();
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
