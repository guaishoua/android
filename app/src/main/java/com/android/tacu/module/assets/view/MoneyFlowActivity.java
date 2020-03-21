package com.android.tacu.module.assets.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.assets.contract.MoneyFlowContract;
import com.android.tacu.module.assets.model.CoinListModel;
import com.android.tacu.module.assets.model.MoneyFlowEvent;
import com.android.tacu.module.assets.model.MoneyFlowModel;
import com.android.tacu.module.assets.model.RecordEvent;
import com.android.tacu.module.assets.presenter.MoneyFlowPresenter;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.view.popup.CoinFilterView;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.android.tacu.utils.UIUtils.getContext;

/**
 * Created by xiaohong on 2018/8/29.
 * <p>
 * 提币记录
 */

public class MoneyFlowActivity extends BaseActivity<MoneyFlowPresenter> implements MoneyFlowContract.IView {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.btn_selected_coin)
    View filterView;
    @BindView(R.id.tv_selected_coin)
    TextView tvSelectedCoin;

    private int pageSize = 10;
    private int currentPage = 1;
    private String type = "";
    private Integer currencyId = null;

    private View emptyView;
    private Gson gson = new Gson();
    private MoneyFlowEvent moneyFlowEvent;
    private TakeCoinAdapter takeCoinAdapter;
    private List<CoinListModel.AttachmentBean> cacheList;
    private List<MoneyFlowModel.TypeModel> typeList = new ArrayList<>();

    private CoinFilterView coinFilterView;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_record);
    }

    public static Intent createActivity(Context context, MoneyFlowEvent event) {
        Intent intent = new Intent(context, MoneyFlowActivity.class);
        intent.putExtra("moneyFlowEvent", event);
        return intent;
    }

    @Override
    protected void initView() {
        moneyFlowEvent = (MoneyFlowEvent) getIntent().getSerializableExtra("moneyFlowEvent");
        coinFilterView = new CoinFilterView(this, onReceiveEvent);

        mTopBar.setTitle(getResources().getString(R.string.history));

        emptyView = View.inflate(this, R.layout.view_empty, null);
        initRecyclerView();
        initDatas();
        updateFilterView();
    }

    @Override
    protected MoneyFlowPresenter createPresenter(MoneyFlowPresenter mPresenter) {
        return new MoneyFlowPresenter();
    }

    @Override
    protected void onPresenterCreated(MoneyFlowPresenter presenter) {
        super.onPresenterCreated(presenter);
        if (cacheList != null && cacheList.size() > 0) {
            mPresenter.coins(false);
        } else {
            mPresenter.coins(true);
        }
        initData(true, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (coinFilterView != null) {
            coinFilterView.destroy();
            coinFilterView = null;
        }
    }

    @Override
    public void hideRefreshView() {
        super.hideRefreshView();
        if (refreshLayout != null && (refreshLayout.isRefreshing() || refreshLayout.isLoading())) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadmore();
        }
    }

    @OnClick(R.id.btn_selected_coin)
    void onSelectedCoinsPressed() {
        if (coinFilterView != null)
            coinFilterView.showOnAnchor(filterView,
                    RelativePopupWindow.VerticalPosition.BELOW,
                    RelativePopupWindow.HorizontalPosition.CENTER,
                    true);
    }

    @Override
    public void showTakeCoinList(MoneyFlowModel attachment) {
        if (currentPage == 1) {
            takeCoinAdapter.setNewData(null);
        }
        if (attachment != null && attachment.type.size() > 0) {
            typeList = attachment.type;
        }
        if (attachment.total == 0) {
            takeCoinAdapter.setEmptyView(emptyView);
            takeCoinAdapter.setNewData(null);
            takeCoinAdapter.notifyDataSetChanged();
        } else if (attachment.list.size() == 0) {
            refreshLayout.setEnableLoadmore(false);
        } else if (attachment.total > 10) {
            takeCoinAdapter.addData(attachment.list);
            refreshLayout.setEnableLoadmore(true);
        } else if (attachment.total <= 10) {
            refreshLayout.setEnableLoadmore(false);
            takeCoinAdapter.setNewData(attachment.list);
        }
    }

    @Override
    public void currencyView(List<CoinListModel.AttachmentBean> attachment) {
        SPUtils.getInstance().put(Constant.SELECT_COIN_NOGROUP_CACHE, gson.toJson(attachment));
        if (coinFilterView != null) {
            coinFilterView.setList(attachment);
        }
    }

    private void initDatas() {
        String cacheString = SPUtils.getInstance().getString(Constant.SELECT_COIN_NOGROUP_CACHE);
        cacheList = gson.fromJson(cacheString, new TypeToken<List<CoinListModel.AttachmentBean>>() {
        }.getType());
        if (coinFilterView != null) {
            coinFilterView.setList(cacheList);
        }
    }

    private void updateFilterView() {
        if (moneyFlowEvent != null) {
            tvSelectedCoin.setText(moneyFlowEvent.getCurrencyNameEn());
        }
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CustomTextHeaderView headerView = new CustomTextHeaderView(getContext());
        headerView.setPrimaryColors(ContextCompat.getColor(this, R.color.content_bg_color), ContextCompat.getColor(this, R.color.text_color));
        refreshLayout.setRefreshHeader(headerView);
        refreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_default)));
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                initData(false, false);
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData(true, false);
            }
        });

        takeCoinAdapter = new TakeCoinAdapter();
        recyclerView.setAdapter(takeCoinAdapter);
    }

    private class TakeCoinAdapter extends BaseQuickAdapter<MoneyFlowModel.ListBean, BaseViewHolder> {

        public TakeCoinAdapter() {
            super(R.layout.item_record);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final MoneyFlowModel.ListBean item) {
            helper.setText(R.id.tv_fee, getResources().getString(R.string.fee2) + BigDecimal.valueOf(item.fee).toPlainString());
            helper.setText(R.id.tv_coins_name, BigDecimal.valueOf(item.amount).toPlainString() + item.currencyNameEn);
            helper.setTextColor(R.id.tv_coins_name, getResources().getColor(item.amount > 0 ? R.color.color_riseup : R.color.color_risedown));
            if (typeList != null) {
                for (MoneyFlowModel.TypeModel typeModel : typeList) {
                    if (TextUtils.equals(item.type, typeModel.code)) {
                        helper.setText(R.id.tv_status, typeModel.value);
                    }
                }
            }
            helper.setText(R.id.tv_time, item.createTime);
        }
    }

    public void initData(boolean isLoad, boolean showLoad) {
        if (isLoad) {
            currentPage = 1;
            takeCoinAdapter.setNewData(null);
        } else {
            ++currentPage;
        }
        if (moneyFlowEvent == null) {
            mPresenter.selectTakeList(currentPage, pageSize, null, null, null, null, showLoad);
        } else {
            if (TextUtils.equals(moneyFlowEvent.getType(), "0")) {
                type = null;
            } else {
                type = moneyFlowEvent.getType();
            }
            if (moneyFlowEvent.getCurrencyId() == 0) {
                currencyId = null;
            } else {
                currencyId = moneyFlowEvent.getCurrencyId();
            }
            if (TextUtils.isEmpty(moneyFlowEvent.getStartTime())) {
                moneyFlowEvent.setStartTime(null);
            }
            if (TextUtils.isEmpty(moneyFlowEvent.getEndTime())) {
                moneyFlowEvent.setEndTime(null);
            }
            mPresenter.selectTakeList(currentPage, pageSize, type, currencyId, moneyFlowEvent.getStartTime(), moneyFlowEvent.getEndTime(), showLoad);
        }
    }


    CoinFilterView.Listener onReceiveEvent = new CoinFilterView.Listener() {
        @Override
        public void onItemPress(RecordEvent event) {
            moneyFlowEvent.setCurrencyId(event.getCurrencyId());
            moneyFlowEvent.setCurrencyNameEn(event.getCurrencyNameEn());
            updateFilterView();
            initData(true, true);
        }
    };
}
