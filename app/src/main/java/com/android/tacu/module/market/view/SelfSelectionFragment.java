package com.android.tacu.module.market.view;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.module.market.contract.MarketContract;
import com.android.tacu.module.market.model.MarketNewModel;
import com.android.tacu.module.market.model.SelfModel;
import com.android.tacu.module.market.presenter.MarketPresenter;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.widget.SmallChartView;
import com.google.gson.Gson;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 自选
 * Created by jiazhen on 2018/8/18.
 */
public class SelfSelectionFragment extends BaseFragment<MarketPresenter> implements MarketContract.ISelfView {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private ImageView img_edit;
    private TextView tv_edit;

    //无数据页面
    private View nodataView;
    private Gson gson = new Gson();
    private SelfAdapter selfAdapter;
    private SelfModel selfModel;
    private List<MarketNewModel.TradeCoinsBean> allTradeCoinList = new ArrayList<>();//所有币种的集合
    private List<MarketNewModel.TradeCoinsBean> tradeCoinList = new ArrayList<>();//自选的币种的列表

    private Handler mHandler = new Handler();
    private Handler mSocketHandler = new Handler();

    private String currentStatus;
    private int pairVolStatus;
    private int lastPriceStatus;
    private int hourStatus;

    // 被选中的币
    private String coinNameFlag;

    //temp
    private String tradeString;

    private View viewFooter;

    public static SelfSelectionFragment newInstance() {
        Bundle bundle = new Bundle();
        SelfSelectionFragment fragment = new SelfSelectionFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initLazy() {
        super.initLazy();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisLocalValue();
            }
        }, 500);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_selfselection;
    }

    @Override
    protected void initData(View view) {
        nodataView = View.inflate(getContext(), R.layout.view_selfselection_nodata, null);
        viewFooter = View.inflate(getContext(), R.layout.footer_edit_selfselection, null);

        LinearLayout rl_select = nodataView.findViewById(R.id.rl_select);
        img_edit = viewFooter.findViewById(R.id.img_edit);
        tv_edit = viewFooter.findViewById(R.id.tv_edit);

        rl_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spUtil.getLogin()) {
                    jumpTo(SelectedCoinsActivity.createActivity(getContext(), true, true, gson.toJson(selfModel), "", ""));
                } else {
                    jumpTo(LoginActivity.class);
                }
            }
        });
        img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpTo(SelfSelectionEditActivity.class);
            }
        });
        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpTo(SelfSelectionEditActivity.class);
            }
        });

        selfAdapter = new SelfAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(selfAdapter);
        setErrorView(nodataView);
    }

    @Override
    protected MarketPresenter createPresenter(MarketPresenter mPresenter) {
        return new MarketPresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
        setVisLocalValue();
    }

    @Override
    public void onPause() {
        super.onPause();
        //防止异常退出再登录 影响页面刷新
        if (!spUtil.getLogin()) {
            tradeString = "";
        }
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
    }

    @Override
    public void getSelfSelectionValue(SelfModel selfModel) {
        if (selfModel == null) {
            selfModel = new SelfModel();
        }
        this.selfModel = selfModel;
        SPUtils.getInstance().put(Constant.SELFCOIN_LIST, gson.toJson(selfModel));
        changeData();
    }

    @Override
    protected void receiveEvent(BaseEvent event) {
        super.receiveEvent(event);
        if (event != null) {
            switch (event.getCode()) {
                case EventConstant.MarkListVisibleCode:
                    MarketListVisibleHintEvent marketListVisibleHintEvent = (MarketListVisibleHintEvent) event.getData();
                    if (marketListVisibleHintEvent.isVisibleToUser()) {
                        setVisLocalValue();
                    }
                    break;
                case EventConstant.HomeNotifyCode:
                    HomeNotifyEvent homeNotifyEvent = (HomeNotifyEvent) event.getData();
                    if (homeNotifyEvent.isNotify()) {
                        selfAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    }

    /**
     * 排序
     */
    public void setSortCoinValue(String currentStatus, int pairVolStatus, int lastPriceStatus, int hourStatus) {
        this.currentStatus = currentStatus;
        this.pairVolStatus = pairVolStatus;
        this.lastPriceStatus = lastPriceStatus;
        this.hourStatus = hourStatus;
        if (spUtil != null && spUtil.getLogin()) {
            if (!TextUtils.isEmpty(currentStatus) && tradeCoinList != null && tradeCoinList.size() > 0) {
                mPresenter.sortList(currentStatus, pairVolStatus, lastPriceStatus, hourStatus, tradeCoinList);
                if (isVisibleToUser) {
                    selfAdapter.setNewData(tradeCoinList);
                }
            }
        }
    }

    /**
     * 本地自选SelfModel数据库数据的加载
     * 分三种情况 onresume 当前fragmnet的setUserVisibleHint  上一级fragmnet的setUserVisibleHint
     */
    private void setVisLocalValue() {
        if (spUtil.getLogin() && isVisibleToUser) {
            getLocalSelfList();
        } else if (!spUtil.getLogin() && isVisibleToUser) {
            setClearList();
        }
    }

    /**
     * Socket传递数据
     */
    public void setCoinValue(List<MarketNewModel.TradeCoinsBean> allCoinList) {
        this.allTradeCoinList = allCoinList;
        if (spUtil != null && spUtil.getLogin()) {
            changeData();
        } else {
            if (tradeCoinList != null && tradeCoinList.size() > 0) {
                setClearList();
            }
        }
    }

    private void changeData() {
        dealData();
        setNoDataView();
        if (this.isVisibleToUser) {
            setEditLis();
        }
        String tempString = gson.toJson(tradeCoinList);
        if (!TextUtils.equals(tempString, tradeString) && isVisibleToUser) {
            selfAdapter.setNewData(tradeCoinList);
            tradeString = tempString;
        }
    }

    private void dealData() {
        tradeCoinList.clear();
        if (selfModel != null && selfModel.checkedList != null && selfModel.checkedList.size() > 0 && allTradeCoinList != null && allTradeCoinList.size() > 0) {
            for (int i = 0; i < selfModel.checkedList.size(); i++) {
                for (int j = 0; j < allTradeCoinList.size(); j++) {
                    if (TextUtils.equals(selfModel.checkedList.get(i).symbol, allTradeCoinList.get(j).currencyId + "," + allTradeCoinList.get(j).baseCurrencyId)) {
                        tradeCoinList.add(allTradeCoinList.get(j));
                        break;
                    }
                }
            }
        }
        //如果不加这个判断 就不走下面那一步 不知道为啥
        if (!TextUtils.isEmpty(currentStatus) && tradeCoinList != null && tradeCoinList.size() > 0) {
            mPresenter.sortList(currentStatus, pairVolStatus, lastPriceStatus, hourStatus, tradeCoinList);
        }
    }

    /**
     * 如果没数据 显示无数据图片
     */
    private void setNoDataView() {
        if (tradeCoinList == null || tradeCoinList.size() < 0) {
            tradeCoinList = new ArrayList<>();
            setErrorView(nodataView);
        }
    }

    private void setEditLis() {
        if (tradeCoinList != null && tradeCoinList.size() > 0) {
            selfAdapter.setFooterView(viewFooter);
        } else {
            //没有数据就通知MarketListFragment按钮不显示
            selfAdapter.removeAllFooterView();
        }
    }

    private void getLocalSelfList() {
        String selfModelString = SPUtils.getInstance().getString(Constant.SELFCOIN_LIST);
        selfModel = gson.fromJson(selfModelString, SelfModel.class);
        if (selfModel == null) {
            selfModel = new SelfModel();
        }
        changeData();
    }

    /**
     * 清空数据集合list
     */
    private void setClearList() {
        tradeString = "";
        tradeCoinList.clear();
        selfAdapter.notifyDataSetChanged();
        setEditLis();
    }

    /**
     * 设置无数据和无网络视图
     */
    private void setErrorView(View view) {
        selfAdapter.setEmptyView(view);
    }

    private class SelfAdapter extends BaseQuickAdapter<MarketNewModel.TradeCoinsBean, BaseViewHolder> {

        public SelfAdapter() {
            super(R.layout.item_market);
        }

        @Override
        protected void convert(BaseViewHolder helper, final MarketNewModel.TradeCoinsBean item) {
            GlideUtils.disPlay(getContext(), Constant.SMALL_ICON_URL + item.icoUrl, (ImageView) helper.getView(R.id.img_coins_icon));
            helper.setText(R.id.tv_coins_name, item.currencyNameEn + "/" + item.baseCurrencyNameEn);
            helper.setText(R.id.tv_current_amount, BigDecimal.valueOf(item.currentAmount).setScale(item.pointPrice, BigDecimal.ROUND_DOWN).toPlainString());
            String value = BigDecimal.valueOf(item.currentAmount * item.volume).setScale(item.pointPrice, BigDecimal.ROUND_DOWN).toPlainString();
            helper.setText(R.id.tv_amount, value);
            helper.setText(R.id.tv_rmb_scale, "≈" + getMcM(item.baseCurrencyId, item.currentAmount));

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
                timeList.add(Long.parseLong(item.hours24TrendList.get(i).get(0)));
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
                    selfAdapter.notifyDataSetChanged();
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
