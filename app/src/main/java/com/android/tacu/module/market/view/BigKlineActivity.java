package com.android.tacu.module.market.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.common.TabIndicatorAdapter;
import com.android.tacu.interfaces.ISocketEvent;
import com.android.tacu.module.market.contract.MarketDetailsContract;
import com.android.tacu.module.market.model.CurrentTradeCoinModel;
import com.android.tacu.module.market.model.KLineModel;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.market.presenter.MarketDetailsPresenter;
import com.android.tacu.socket.BaseSocketManager;
import com.android.tacu.socket.ObserverModel;
import com.android.tacu.socket.SocketConstant;
import com.android.tacu.utils.KlineUtils;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.widget.tab.IndexKlineModel;
import com.github.tifezh.kchartlib.chart.KLineChartView;
import com.github.tifezh.kchartlib.chart.adapter.KLineChartAdapter;
import com.github.tifezh.kchartlib.chart.base.Status;
import com.github.tifezh.kchartlib.chart.entity.KLineEntity;
import com.github.tifezh.kchartlib.chart.utils.DataHelper;
import com.github.tifezh.kchartlib.chart.view.IndexKlineActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.TextWidthColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.OnClick;

import static com.android.tacu.module.market.view.MarketDetailsActivity.KREFRESH_TIME;
import static com.android.tacu.module.market.view.MarketDetailsActivity.REQUESTCODE;

public class BigKlineActivity extends BaseActivity<MarketDetailsPresenter> implements MarketDetailsContract.IBigKlineView, ISocketEvent, Observer {

    @BindView(R.id.tv_coin_name)
    TextView tv_coin_name;
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
    @BindView(R.id.img_close)
    ImageView img_close;
    @BindView(R.id.kchart_view)
    KLineChartView mKChartView;
    @BindView(R.id.magic_indicator)
    ScrollIndicatorView magic_indicator;

    @BindView(R.id.tv_ma)
    TextView tv_ma;
    @BindView(R.id.tv_boll)
    TextView tv_boll;
    @BindView(R.id.img_main_eye)
    ImageView img_main_eye;
    @BindView(R.id.tv_macd)
    TextView tv_macd;
    @BindView(R.id.tv_kdj)
    TextView tv_kdj;
    @BindView(R.id.tv_rsi)
    TextView tv_rsi;
    @BindView(R.id.tv_wr)
    TextView tv_wr;
    @BindView(R.id.img_second_eye)
    ImageView img_second_eye;

    private int currencyId;
    private int baseCurrencyId;
    private String currencyNameEn;
    private String baseCurrencyNameEn;

    private List<String> tabTitle = new ArrayList<>();

    private int pointPrice;
    //防止socket刷新频繁
    private int pointPriceTemp;

    //k线是否加载动画和网络请求动画
    private boolean isAnim = true;
    private boolean isFirst = true;
    private KLineChartAdapter kAdapter;
    private KLineModel kLineModel;
    private IndexKlineModel indexKlineModel;
    private List<TextView> mainViewList = new ArrayList<>();
    private List<TextView> secondViewList = new ArrayList<>();

    private CurrentTradeCoinModel currentTradeCoinModel;

    private Gson gson = new Gson();

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

    public static Intent createActivity(Context context, int currencyId, int baseCurrencyId, String currencyNameEn, String baseCurrencyNameEn, KLineModel kModel) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("kModel", kModel);
        Intent intent = new Intent(context, BigKlineActivity.class);
        intent.putExtra("currencyId", currencyId);
        intent.putExtra("baseCurrencyId", baseCurrencyId);
        intent.putExtra("currencyNameEn", currencyNameEn);
        intent.putExtra("baseCurrencyNameEn", baseCurrencyNameEn);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_big_kline);
    }

    @Override
    protected void initView() {
        setSocketEvent(this, this, SocketConstant.LOGINAFTERCHANGETRADECOIN);

        currencyId = getIntent().getIntExtra("currencyId", Constant.TAC_CURRENCY_ID);
        baseCurrencyId = getIntent().getIntExtra("baseCurrencyId", Constant.ACU_CURRENCY_ID);
        currencyNameEn = getIntent().getStringExtra("currencyNameEn");
        baseCurrencyNameEn = getIntent().getStringExtra("baseCurrencyNameEn");
        kLineModel = (KLineModel) getIntent().getSerializableExtra("kModel");

        tv_coin_name.setText(currencyNameEn + "/" + baseCurrencyNameEn);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("kModel", kLineModel);
                intent.putExtra("chartTime", indexKlineModel.ChartTime);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        kAdapter = new KLineChartAdapter();
        mKChartView.setAdapter(kAdapter);

        tabTitle.add(getResources().getString(R.string.min_time));
        tabTitle.add(getResources().getString(R.string.min_1));
        tabTitle.add(getResources().getString(R.string.min_5));
        tabTitle.add(getResources().getString(R.string.min_15));
        tabTitle.add(getResources().getString(R.string.min_30));
        tabTitle.add(getResources().getString(R.string.hour_1));
        tabTitle.add(getResources().getString(R.string.hour_6));
        tabTitle.add(getResources().getString(R.string.day_1));
        tabTitle.add(getResources().getString(R.string.week_1));
        tabTitle.add(getResources().getString(R.string.month_1));

        magic_indicator.setBackgroundColor(ContextCompat.getColor(this, R.color.content_bg_color_grey));
        magic_indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(this, R.color.text_default), ContextCompat.getColor(this, R.color.tab_text_color)).setSize(14, 14));
        magic_indicator.setScrollBar(new TextWidthColorBar(this, magic_indicator, ContextCompat.getColor(this, R.color.text_default), 4));
        magic_indicator.setSplitAuto(true);
        magic_indicator.setAdapter(new TabIndicatorAdapter(this, tabTitle));

        String temp = SPUtils.getInstance().getString(Constant.MARKET_DETAIL_TIME);
        if (!TextUtils.isEmpty(temp)) {
            indexKlineModel = gson.fromJson(temp, IndexKlineModel.class);
        } else {
            indexKlineModel = new IndexKlineModel();
        }
        if (indexKlineModel.isLine) {
            magic_indicator.setCurrentItem(0);
        } else {
            if (indexKlineModel.ChartTime == KLineChartView.MIN_1) {
                magic_indicator.setCurrentItem(1);
            } else if (indexKlineModel.ChartTime == KLineChartView.MIN_5) {
                magic_indicator.setCurrentItem(2);
            } else if (indexKlineModel.ChartTime == KLineChartView.MIN_15) {
                magic_indicator.setCurrentItem(3);
            } else if (indexKlineModel.ChartTime == KLineChartView.MIN_30) {
                magic_indicator.setCurrentItem(4);
            } else if (indexKlineModel.ChartTime == KLineChartView.HOUR_1) {
                magic_indicator.setCurrentItem(5);
            } else if (indexKlineModel.ChartTime == KLineChartView.HOUR_6) {
                magic_indicator.setCurrentItem(6);
            } else if (indexKlineModel.ChartTime == KLineChartView.DAY_1) {
                magic_indicator.setCurrentItem(7);
            } else if (indexKlineModel.ChartTime == KLineChartView.WEEK_1) {
                magic_indicator.setCurrentItem(8);
            } else if (indexKlineModel.ChartTime == KLineChartView.MONTH_1) {
                magic_indicator.setCurrentItem(9);
            }
        }
        magic_indicator.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
                mKChartView.hideSelectData();
                indexKlineModel.isLine = false;
                mKChartView.setMainDrawLine(false);
                switch (select) {
                    case 0://分时
                        indexKlineModel.ChartTime = KLineChartView.MIN_1;
                        indexKlineModel.isLine = true;
                        mKChartView.setMainDrawLine(true);
                        break;
                    case 1://1分钟
                        indexKlineModel.ChartTime = KLineChartView.MIN_1;
                        break;
                    case 2://5分钟
                        indexKlineModel.ChartTime = KLineChartView.MIN_5;
                        break;
                    case 3://15分钟
                        indexKlineModel.ChartTime = KLineChartView.MIN_15;
                        break;
                    case 4://30分钟
                        indexKlineModel.ChartTime = KLineChartView.MIN_30;
                        break;
                    case 5://1小时
                        indexKlineModel.ChartTime = KLineChartView.HOUR_1;
                        break;
                    case 6://6小时
                        indexKlineModel.ChartTime = KLineChartView.HOUR_6;
                        break;
                    case 7://1天
                        indexKlineModel.ChartTime = KLineChartView.DAY_1;
                        break;
                    case 8://1周
                        indexKlineModel.ChartTime = KLineChartView.WEEK_1;
                        break;
                    case 9://1月
                        indexKlineModel.ChartTime = KLineChartView.MONTH_1;
                        break;
                }
                saveOpearte();
                isAnim = true;
                upLoad(true);
            }
        });

        //指标的处理
        mainViewList.add(tv_ma);
        mainViewList.add(tv_boll);
        secondViewList.add(tv_macd);
        secondViewList.add(tv_kdj);
        secondViewList.add(tv_rsi);
        secondViewList.add(tv_wr);

        mKChartView.hideSelectData();
        if (indexKlineModel.MainView == -1) {
            mKChartView.changeMainDrawType(Status.NONE);
            clearMainColor(false);
        } else {
            clearMainColor(true);
            switch (indexKlineModel.MainView) {
                case 0:
                    mKChartView.changeMainDrawType(Status.MA);
                    tv_ma.setTextColor(ContextCompat.getColor(this, R.color.text_default));
                    break;
                case 1:
                    mKChartView.changeMainDrawType(Status.BOLL);
                    tv_boll.setTextColor(ContextCompat.getColor(this, R.color.text_default));
                    break;
            }
        }
        if (indexKlineModel.SecondView == -1) {
            mKChartView.hideChildDraw();
            clearSecondColor(false);
        } else {
            mKChartView.setChildDraw(indexKlineModel.SecondView);
            clearSecondColor(true);
            switch (indexKlineModel.SecondView) {
                case 0:
                    tv_macd.setTextColor(ContextCompat.getColor(this, R.color.text_default));
                    break;
                case 1:
                    tv_kdj.setTextColor(ContextCompat.getColor(this, R.color.text_default));
                    break;
                case 2:
                    tv_rsi.setTextColor(ContextCompat.getColor(this, R.color.text_default));
                    break;
                case 3:
                    tv_wr.setTextColor(ContextCompat.getColor(this, R.color.text_default));
                    break;
            }
        }

        if (kLineModel != null) {
            isFirst = false;
            mKChartView.post(new Runnable() {
                @Override
                public void run() {
                    success(kLineModel, (currencyNameEn + baseCurrencyNameEn).toLowerCase(), true);
                }
            });
        }

        setCacheCoinInfo();
    }

    @Override
    protected MarketDetailsPresenter createPresenter(MarketDetailsPresenter mPresenter) {
        return new MarketDetailsPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            kHandler = null;
            kRunnable = null;
        }
        System.gc();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("kModel", kLineModel);
        intent.putExtra("chartTime", indexKlineModel.ChartTime);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUESTCODE && resultCode == RESULT_OK) {
            if (kLineModel != null && kLineModel.data != null && kLineModel.data.lines != null) {
                List<KLineEntity> data = KlineUtils.dealKlines(kLineModel, indexKlineModel.ChartTime);
                if (data != null) {
                    DataHelper.calculate(data, BigKlineActivity.this);
                    kAdapter.clearData();
                    kAdapter.addFooterData(data);
                    kAdapter.notifyInvalidated();
                }
            }
        }
    }

    @OnClick(R.id.tv_ma)
    void maClick() {
        clearMainColor(true);
        tv_ma.setTextColor(ContextCompat.getColor(this, R.color.text_default));

        indexKlineModel.MainView = 0;
        saveOpearte();

        mKChartView.hideSelectData();
        mKChartView.changeMainDrawType(Status.MA);
    }

    @OnClick(R.id.tv_boll)
    void bollClick() {
        clearMainColor(true);
        tv_boll.setTextColor(ContextCompat.getColor(this, R.color.text_default));

        indexKlineModel.MainView = 1;
        saveOpearte();

        mKChartView.hideSelectData();
        mKChartView.changeMainDrawType(Status.BOLL);
    }

    @OnClick(R.id.rl_main_eye)
    void mainEyeClick() {
        clearMainColor(false);

        indexKlineModel.MainView = -1;
        saveOpearte();

        mKChartView.hideSelectData();
        mKChartView.changeMainDrawType(Status.NONE);
    }

    @OnClick(R.id.tv_macd)
    void macdClick() {
        clearSecondColor(true);
        tv_macd.setTextColor(ContextCompat.getColor(this, R.color.text_default));

        indexKlineModel.SecondView = 0;
        saveOpearte();

        mKChartView.hideSelectData();
        mKChartView.setChildDraw(indexKlineModel.SecondView);
    }

    @OnClick(R.id.tv_kdj)
    void kdjClick() {
        clearSecondColor(true);
        tv_kdj.setTextColor(ContextCompat.getColor(this, R.color.text_default));

        indexKlineModel.SecondView = 1;
        saveOpearte();

        mKChartView.hideSelectData();
        mKChartView.setChildDraw(indexKlineModel.SecondView);
    }

    @OnClick(R.id.tv_rsi)
    void rsiClick() {
        clearSecondColor(true);
        tv_rsi.setTextColor(ContextCompat.getColor(this, R.color.text_default));

        indexKlineModel.SecondView = 2;
        saveOpearte();

        mKChartView.hideSelectData();
        mKChartView.setChildDraw(indexKlineModel.SecondView);
    }

    @OnClick(R.id.tv_wr)
    void wrClick() {
        clearSecondColor(true);
        tv_wr.setTextColor(ContextCompat.getColor(this, R.color.text_default));

        indexKlineModel.SecondView = 3;
        saveOpearte();

        mKChartView.hideSelectData();
        mKChartView.setChildDraw(indexKlineModel.SecondView);
    }

    @OnClick(R.id.rl_second_eye)
    void secEyeClick() {
        clearSecondColor(false);

        indexKlineModel.SecondView = -1;
        saveOpearte();

        mKChartView.hideSelectData();
        mKChartView.hideChildDraw();
    }

    @OnClick(R.id.rl_setting)
    void settingClick() {
        jumpTo(IndexKlineActivity.class, REQUESTCODE);
    }

    @Override
    protected void settingActivityScreenDir() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void success(KLineModel model, String symbol, boolean isClear) {
        this.kLineModel = model;

        if (model != null && model.data != null && model.data.lines != null) {
            List<KLineEntity> data = KlineUtils.dealKlines(model, indexKlineModel.ChartTime);
            if (data != null && data.size() > 0 && currentTradeCoinModel != null && TextUtils.equals(symbol, (currentTradeCoinModel.currentTradeCoin.currencyNameEn + currentTradeCoinModel.currentTradeCoin.baseCurrencyNameEn).toLowerCase())) {
                data.get(data.size() - 1).Close = BigDecimal.valueOf(currentTradeCoinModel.currentTradeCoin.currentAmount).floatValue();
                data.get(data.size() - 1).Volume = BigDecimal.valueOf(currentTradeCoinModel.currentTradeCoin.volume).floatValue();
            }

            if (data != null) {
                if (isAnim) {
                    isAnim = false;
                }
                KLineChartView.decimalsCount = pointPrice;
                DataHelper.calculate(data, this);
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
     * 请求K线数据
     */
    private void upLoad(boolean isClear) {
        if (isAnim) {
            kAdapter.clearDataAndNotify();
            mKChartView.resetLoadMoreEnd();
            mKChartView.showLoading();
        }
        mPresenter.getBestexKline((currencyNameEn + baseCurrencyNameEn).toLowerCase(), indexKlineModel.ChartTime, isClear);
        if (isFirst) {
            isFirst = false;
        }
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

            if (kAdapter != null) {
                kAdapter.changeCurrentItem(BigDecimal.valueOf(model.currentTradeCoin.currentAmount).floatValue(), BigDecimal.valueOf(model.currentTradeCoin.volume).floatValue(), (model.currentTradeCoin.currencyNameEn + model.currentTradeCoin.baseCurrencyNameEn).toLowerCase());
                if (pointPrice != pointPriceTemp) {
                    KLineChartView.decimalsCount = pointPrice;
                    pointPriceTemp = pointPrice;
                    kAdapter.notifyDataSetChanged();
                }
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
    }

    private void saveOpearte() {
        SPUtils.getInstance().put(Constant.MARKET_DETAIL_TIME, gson.toJson(indexKlineModel));
    }

    private void clearMainColor(boolean isEye) {
        for (int i = 0; i < mainViewList.size(); i++) {
            mainViewList.get(i).setTextColor(ContextCompat.getColor(this, R.color.text_color));
        }
        if (isEye) {
            img_main_eye.setImageResource(R.drawable.qmui_icon_edittext_pwdvis);
        } else {
            img_main_eye.setImageResource(R.drawable.qmui_icon_edittext_pwdgone);
        }
    }

    private void clearSecondColor(boolean isEye) {
        for (int i = 0; i < secondViewList.size(); i++) {
            secondViewList.get(i).setTextColor(ContextCompat.getColor(this, R.color.text_color));
        }
        if (isEye) {
            img_second_eye.setImageResource(R.drawable.qmui_icon_edittext_pwdvis);
        } else {
            img_second_eye.setImageResource(R.drawable.qmui_icon_edittext_pwdgone);
        }
    }
}
