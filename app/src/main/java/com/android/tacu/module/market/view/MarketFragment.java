package com.android.tacu.module.market.view;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.HomeNotifyEvent;
import com.android.tacu.EventBus.model.JumpTradeCodeIsBuyEvent;
import com.android.tacu.EventBus.model.MarketListVisibleHintEvent;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.market.contract.MarketContract;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.market.presenter.MarketPresenter;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.widget.SmallChartView;
import com.google.gson.Gson;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by jiazhen on 2017/5/25.
 */

public class MarketFragment extends BaseFragment<MarketPresenter> implements MarketContract.IMarketLiew {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private MarketNewModel currentMarketModel;
    private MarketListAdapter marketAdapter;
    private Gson gson = new Gson();

    private String currentStatus = "";
    private int pairVolStatus = 0;
    private int lastPriceStatus = 0;
    private int hourStatus = 0;

    //防止socket刷新数据频繁
    private String marketModelString;

    // 被选中的币
    private String coinNameFlag;

    private Handler mHandler = new Handler();
    private Handler mSocketHandler = new Handler();
    private Handler mSocketTwoHandler = new Handler();

    public static MarketFragment newInstance() {
        Bundle bundle = new Bundle();
        MarketFragment fragment = new MarketFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initLazy() {
        super.initLazy();
        //跳转过来就直接刷新Recycleivew会卡顿 延时0.5秒
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getVisibleSocektValue(true);
            }
        }, 500);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_market;
    }

    @Override
    protected void initData(View view) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        marketAdapter = new MarketListAdapter();
        recyclerView.setAdapter(marketAdapter);
    }

    @Override
    protected MarketPresenter createPresenter(MarketPresenter mPresenter) {
        return new MarketPresenter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mSocketHandler != null) {
            mSocketHandler.removeCallbacksAndMessages(null);
            mSocketHandler = null;
        }
        if (mSocketTwoHandler != null) {
            mSocketTwoHandler.removeCallbacksAndMessages(null);
            mSocketTwoHandler = null;
        }
    }

    @Override
    protected void receiveEvent(BaseEvent event) {
        super.receiveEvent(event);
        if (event != null) {
            switch (event.getCode()) {
                case EventConstant.MarkListVisibleCode:
                    MarketListVisibleHintEvent marketListVisibleHintEvent = (MarketListVisibleHintEvent) event.getData();
                    if (marketListVisibleHintEvent.isVisibleToUser()) {
                        getVisibleSocektValue(true);
                    }
                    break;
                case EventConstant.HomeNotifyCode:
                    HomeNotifyEvent homeNotifyEvent = (HomeNotifyEvent) event.getData();
                    if (homeNotifyEvent.isNotify()) {
                        marketAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    }

    public void setCoinValue(final String value) {
        mSocketHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(value)) {
                    if (!TextUtils.equals(marketModelString, value)) {
                        marketModelString = value;
                        currentMarketModel = gson.fromJson(value, MarketNewModel.class);
                        getVisibleSocektValue(false);
                    }
                }
            }
        });
    }

    /**
     * 排序
     */
    public void setSortCoinValue(final String currentStatus, final int pairVolStatus, final int lastPriceStatus, final int hourStatus) {
        this.currentStatus = currentStatus;
        this.pairVolStatus = pairVolStatus;
        this.lastPriceStatus = lastPriceStatus;
        this.hourStatus = hourStatus;

        mSocketTwoHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isVisibleToUser && !TextUtils.isEmpty(currentStatus) && currentMarketModel != null && currentMarketModel.tradeCoinsList != null && currentMarketModel.tradeCoinsList.size() > 0) {
                    mPresenter.sortList(currentStatus, pairVolStatus, lastPriceStatus, hourStatus, currentMarketModel.tradeCoinsList);
                    marketAdapter.setNewData(currentMarketModel.tradeCoinsList);
                }
            }
        });
    }

    /**
     * 如果当前显示就直接赋值 但是当前页面不可见的情况 就把数据缓存下来 可见的时候赋值上去
     * 优化内存 一次刷新6个页面还是太卡了
     * <p>
     * isSort:如果为true 需要排序  为false的情况都是前面给排好顺序了
     */
    private void getVisibleSocektValue(boolean isSort) {
        if ((isVisibleToUser && isVisibleActivity)) {
            if (isSort) {
                mPresenter.sortList(currentStatus, pairVolStatus, lastPriceStatus, hourStatus, currentMarketModel.tradeCoinsList);
            }
            marketAdapter.setNewData(currentMarketModel.tradeCoinsList);
        }
    }

    private class MarketListAdapter extends BaseQuickAdapter<MarketNewModel.TradeCoinsBean, BaseViewHolder> {

        public MarketListAdapter() {
            super(R.layout.item_market);
        }

        @Override
        protected void convert(BaseViewHolder helper, final MarketNewModel.TradeCoinsBean item) {
            GlideUtils.disPlay(getContext(), Constant.API_QINIU_URL + item.icoUrl, (ImageView) helper.getView(R.id.img_coins_icon));
            helper.setText(R.id.tv_coins_name, item.currencyNameEn + "/" + item.baseCurrencyNameEn);
            helper.setText(R.id.tv_current_amount, BigDecimal.valueOf(item.currentAmount).setScale(item.pointPrice, BigDecimal.ROUND_DOWN).toPlainString());
            String value = BigDecimal.valueOf(item.currentAmount * item.volume).setScale(item.pointPrice, BigDecimal.ROUND_DOWN).toPlainString();
            helper.setText(R.id.tv_amount, value);
            if (item.baseCurrencyId == 22) {
                helper.setText(R.id.tv_rmb_scale, "");
            } else {
                helper.setText(R.id.tv_rmb_scale, "≈" + getMcM(item.baseCurrencyId, item.currentAmount));
            }
            if (item.changeRate >= 0) {
                helper.setText(R.id.tv_change_rate, "+" + BigDecimal.valueOf(item.changeRate).toPlainString() + "%");
                ((QMUIRoundButtonDrawable) helper.getView(R.id.tv_change_rate).getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_riseup));
            } else {
                helper.setText(R.id.tv_change_rate, BigDecimal.valueOf(item.changeRate).toPlainString() + "%");
                ((QMUIRoundButtonDrawable) helper.getView(R.id.tv_change_rate).getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_risedown));
            }

            // 隐藏的部分

            if (!TextUtils.isEmpty(coinNameFlag) && TextUtils.equals(coinNameFlag, item.currencyNameEn + "/" + item.baseCurrencyNameEn)) {
                helper.setGone(R.id.lin_layout, true);
            } else {
                helper.setGone(R.id.lin_layout, false);
            }

            helper.setText(R.id.tv_highprice, getMcM(item.baseCurrencyId, item.highPrice));
            helper.setText(R.id.tv_lowprice, getMcM(item.baseCurrencyId, item.lowPrice));
            helper.setText(R.id.tv_volume, BigDecimal.valueOf(item.volume).setScale(2, BigDecimal.ROUND_DOWN).toPlainString());

            List<Float> animList = new ArrayList<>();
            List<Long> timeList = new ArrayList<>();
            for (int i = 0; i < item.hours24TrendList.size(); i++) {
                timeList.add(Long.parseLong(FormatterUtils.getFormatValue(item.hours24TrendList.get(i).get(0))));
                animList.add(Float.parseFloat(item.hours24TrendList.get(i).get(1)));
            }
            if (animList != null && animList.size() > 0) {
                ((SmallChartView) helper.getView(R.id.chart_view)).setItems(timeList, animList, item.pointPrice);
            }

            helper.setOnClickListener(R.id.lin_top, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.equals(coinNameFlag, item.currencyNameEn + "/" + item.baseCurrencyNameEn)) {
                        coinNameFlag = item.currencyNameEn + "/" + item.baseCurrencyNameEn;
                    } else {
                        coinNameFlag = "";
                    }
                    marketAdapter.notifyDataSetChanged();
                }
            });
            helper.setOnClickListener(R.id.lin_gotrade, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventManage.sendEvent(new BaseEvent<>(EventConstant.JumpTradeIsBuyCode, new JumpTradeCodeIsBuyEvent(item.currencyId, item.baseCurrencyId, item.currencyNameEn, item.baseCurrencyNameEn, true)));
                }
            });
            helper.setOnClickListener(R.id.lin_chart, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpTo(MarketDetailsActivity.createActivity(getContext(), item.currencyId, item.baseCurrencyId, item.currencyNameEn, item.baseCurrencyNameEn));
                }
            });
        }
    }
}




