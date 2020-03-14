package com.android.tacu.module.transaction.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.TradeVisibleHintEvent;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.transaction.contract.MyDealContract;
import com.android.tacu.module.transaction.model.ShowTradeListModel;
import com.android.tacu.module.transaction.presenter.MyDealPresenter;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.FormatterUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;

import butterknife.BindView;

public class MyDealFragment extends BaseFragment<MyDealPresenter> implements MyDealContract.IView {

    @BindView(R.id.refreshlayout_trade)
    SmartRefreshLayout refreshTrade;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private int currencyId;
    private int baseCurrencyId;

    private DealRecordAdapter dealRecordAdapter;

    private int start = 1;
    private int size = 20;

    private View emptyView;
    private ShowTradeListModel showTradeListModel;

    private boolean isVisibleToUserTrade = false;
    private boolean isVisibleToUserLastDeal = false;

    public static MyDealFragment newInstance(int currencyId, int baseCurrencyId) {
        Bundle bundle = new Bundle();
        bundle.putInt("currencyId", currencyId);
        bundle.putInt("baseCurrencyId", baseCurrencyId);
        MyDealFragment fragment = new MyDealFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initLazy() {
        super.initLazy();

        getDealList();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            currencyId = bundle.getInt("currencyId");
            baseCurrencyId = bundle.getInt("baseCurrencyId");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_mydeal;
    }

    @Override
    protected void initData(View view) {
        refreshTrade.setEnableRefresh(false);
        refreshTrade.setEnableLoadmore(false);
        refreshTrade.setEnableOverScrollBounce(false);
        refreshTrade.setEnableOverScrollDrag(false);
        refreshTrade.setRefreshFooter(new BallPulseFooter(getContext()).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(getContext(), R.color.color_default)));
        refreshTrade.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                start++;
                getDealList();
            }
        });

        emptyView = View.inflate(getContext(), R.layout.view_empty, null);

        dealRecordAdapter = new DealRecordAdapter();
        dealRecordAdapter.setHeaderFooterEmpty(true, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getHostActivity()));
        recyclerView.setAdapter(dealRecordAdapter);

        dealRecordAdapter.setEmptyView(emptyView);
    }

    @Override
    protected MyDealPresenter createPresenter(MyDealPresenter mPresenter) {
        return new MyDealPresenter();
    }

    @Override
    public void onResume() {
        super.onResume();

        getDealList();
        if (!spUtil.getLogin()) {
            dealRecordAdapter.setNewData(null);
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
                    getDealList();
                    break;
            }
        }
    }

    @Override
    public void showTradeList(ShowTradeListModel model) {
        if (showTradeListModel != null && showTradeListModel.list != null && showTradeListModel.list.size() > 0) {
            if (model == null || model.list == null || model.list.size() <= 0) {
                showTradeListModel.total = model.total;
                showTradeListModel.list.addAll(model.list);
            }
        } else {
            showTradeListModel = model;
        }

        if (showTradeListModel != null && showTradeListModel.list != null && showTradeListModel.list.size() > 0) {
            dealRecordAdapter.setNewData(model.list);
        } else {
            dealRecordAdapter.setNewData(null);
            dealRecordAdapter.setEmptyView(emptyView);
        }

        if (showTradeListModel == null || showTradeListModel.list == null || showTradeListModel.list.size() >= model.total) {
            refreshTrade.setEnableLoadmore(false);
        } else {
            refreshTrade.setEnableLoadmore(true);
        }
    }

    @Override
    public void showTradeListFail() {
        start--;
        if (start <= 1) {
            start = 1;
        }
        refreshTrade.setEnableLoadmore(true);
    }

    public void notiy() {
        getDealList();
    }

    public void setLastDealVisible(boolean isVisibleToUserLastDeal){
        this.isVisibleToUserLastDeal = isVisibleToUserLastDeal;
        getDealList();
    }

    public void setValue(int currencyId, int baseCurrencyId) {
        this.currencyId = currencyId;
        this.baseCurrencyId = baseCurrencyId;

        getDealList();
    }

    private void getDealList() {
        if (!isVisibleToUserTrade || !isVisibleToUserLastDeal || !isVisibleToUser) {
            return;
        }
        if (spUtil != null && spUtil.getLogin()) {
            mPresenter.showTradeList(start, size, 0, currencyId, baseCurrencyId);
        }
    }

    /**
     * 成交记录
     */
    private class DealRecordAdapter extends BaseQuickAdapter<ShowTradeListModel.ListBean, BaseViewHolder> {

        public DealRecordAdapter() {
            super(R.layout.item_deal_record);
        }

        @Override
        protected void convert(BaseViewHolder helper, ShowTradeListModel.ListBean item) {
            helper.setText(R.id.tv_buyorSell, item.getBuyOrSell());
            helper.setTextColor(R.id.tv_buyorSell, ContextCompat.getColor(getContext(), item.getTextColor()));
            helper.setText(R.id.tv_coins_name, item.currencyNameEn + "/" + item.baseCurrencyNameEn);

            helper.setText(R.id.tv_deal_singleprice_title, getResources().getString(R.string.deal_single_price) + "(" + item.baseCurrencyNameEn + ")");
            helper.setText(R.id.tv_deal_number_title, getResources().getString(R.string.vol) + "(" + item.currencyNameEn + ")");
            helper.setText(R.id.tv_deal_price_title, getResources().getString(R.string.deal_price) + "(" + item.baseCurrencyNameEn + ")");
            if (item.buyOrSell == 1) {//买
                if (!TextUtils.isEmpty(item.buyFeeCurrencyName)) {
                    helper.setText(R.id.tv_fee_title, getResources().getString(R.string.fee) + "(" + item.buyFeeCurrencyName + ")");
                } else {
                    helper.setText(R.id.tv_fee_title, getResources().getString(R.string.fee) + "(" + item.currencyNameEn + ")");
                }
            } else if (item.buyOrSell == 2) {
                if (!TextUtils.isEmpty(item.sellFeeCurrencyName)) {
                    helper.setText(R.id.tv_fee_title, getResources().getString(R.string.fee) + "(" + item.sellFeeCurrencyName + ")");
                } else {
                    helper.setText(R.id.tv_fee_title, getResources().getString(R.string.fee) + "(" + item.baseCurrencyNameEn + ")");
                }
            }

            helper.setText(R.id.tv_date, DateUtils.getStrToStr(item.tradeTime, DateUtils.DEFAULT_PATTERN, DateUtils.FORMAT_DATE_YMDHM));
            helper.setText(R.id.tv_deal_singleprice, FormatterUtils.getFormatValue(item.tradePrice));
            helper.setText(R.id.tv_deal_number, FormatterUtils.getFormatValue(item.tradeNum));
            helper.setText(R.id.tv_deal_price, FormatterUtils.getFormatValue(item.tradeAmount));
            helper.setText(R.id.tv_fee, FormatterUtils.getFormatValue(item.fee));
        }
    }
}
