package com.android.tacu.module.assets.view;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
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
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.IdentityAuthUtils;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.utils.ShowToast;
import com.android.tacu.utils.StatusBarUtils;
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

    @BindView(R.id.view_drawer)
    View view_drawer;
    @BindView(R.id.drawer_right)
    DrawerLayout drawer_right;
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
    private MoneyFlowEvent moneyFlowEvent;
    private Gson gson = new Gson();
    private TakeCoinAdapter takeCoinAdapter;
    private List<CoinListModel.AttachmentBean> cacheList;
    private List<MoneyFlowModel.TypeModel> typeList = new ArrayList<>();

    private MoneyFlowDrawerLayoutHelper helper;
    private CoinFilterView coinFilterView;

    @Override
    protected void setView() {
        setContentView(R.layout.fragment_record);
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
        StatusBarUtils.setColorForDrawerLayout(this, drawer_right, ContextCompat.getColor(this, R.color.color_default), 0);

        mTopBar.setTitle(getResources().getString(R.string.history));

        emptyView = View.inflate(this, R.layout.view_empty, null);
        filterView.findViewById(R.id.btn_header_filter).setVisibility(View.VISIBLE);
        initRecyclerView();
        initDatas();
        updateFilterView();
    }

    @OnClick(R.id.btn_selected_coin)
    void onSelectedCoinsPressed() {
        if (coinFilterView != null)
            coinFilterView.showOnAnchor(filterView,
                    RelativePopupWindow.VerticalPosition.BELOW,
                    RelativePopupWindow.HorizontalPosition.CENTER,
                    true);
    }


    @OnClick(R.id.btn_header_filter)
    void onFilterPressed() {
        if (!drawer_right.isDrawerOpen(Gravity.RIGHT)) {
            drawer_right.openDrawer(Gravity.RIGHT);
        } else {
            drawer_right.closeDrawer(Gravity.RIGHT);
        }
    }

    @Override
    protected void initLazy() {
        super.initLazy();
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                initDrawer();
                initData(true, true);
                if (cacheList != null && cacheList.size() > 0) {
                    mPresenter.coins(false);
                } else {
                    mPresenter.coins(true);
                }
                drawerListener();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (helper != null) {
            helper.clearActivity();
            helper = null;
        }
        if (coinFilterView != null) {
            coinFilterView.destroy();
            coinFilterView = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer_right.isDrawerOpen(Gravity.RIGHT)) {
            drawer_right.closeDrawer(Gravity.RIGHT);
            return;
        }
        finish();
    }

    private void initDatas() {
        String cacheString = SPUtils.getInstance().getString(Constant.SELECT_COIN_NOGROUP_CACHE);
        cacheList = gson.fromJson(cacheString, new TypeToken<List<CoinListModel.AttachmentBean>>() {
        }.getType());
    }

    private void drawerListener() {
        drawer_right.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                IdentityAuthUtils.closeKeyBoard(MoneyFlowActivity.this);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    private void updateFilterView() {
        if (moneyFlowEvent != null) {
            tvSelectedCoin.setText(moneyFlowEvent.getCurrencyNameEn());
        }
    }

    private void initDrawer() {
        helper = new MoneyFlowDrawerLayoutHelper(getContext(), view_drawer, moneyFlowEvent.getCurrencyId());
        helper.setDrawerMenuView(new MoneyFlowDrawerLayoutHelper.RecordClickListener() {
            @Override
            public void RecordClick(MoneyFlowEvent money) {
                MoneyFlowActivity.this.moneyFlowEvent = money;
                initData(true, true);
                drawer_right.closeDrawer(Gravity.RIGHT);
                updateFilterView();
            }

            @Override
            public void timeClick(TextView tv, String time, boolean isStart) {
                if (isStart) {
                    CommonUtils.selectTime(MoneyFlowActivity.this, tv, time);
                } else {
                    CommonUtils.endTime(MoneyFlowActivity.this, tv, time);
                }
            }
        });
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
                if (isAvailable()) {
                    initData(false, false);
                } else {
                    showToastError(getResources().getString(R.string.net_unavailable));
                }
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData(true, false);
            }
        });

        takeCoinAdapter = new TakeCoinAdapter();
        recyclerView.setAdapter(takeCoinAdapter);
    }

    @Override
    protected MoneyFlowPresenter createPresenter(MoneyFlowPresenter mPresenter) {
        return new MoneyFlowPresenter();
    }

    @Override
    public void hideRefreshView() {
        super.hideRefreshView();
        if (refreshLayout != null && (refreshLayout.isRefreshing() || refreshLayout.isLoading())) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadmore();
        }
    }

    @Override
    public void showTakeCoinList(MoneyFlowModel attachment) {
        if (currentPage == 1) {
            takeCoinAdapter.setNewData(null);
        }
        if (attachment != null && attachment.type.size() > 0) {
            typeList = attachment.type;
            helper.initDatas(attachment.type);
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
        if (helper != null) {
            helper.setDatas(attachment);
            coinFilterView.setList(attachment);
        }
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

    /**
     * 复制
     *
     * @param str
     */
    private void copy(String str) {
        ClipboardManager cmb = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(str); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
        ShowToast.success(getString(R.string.copy_success));
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
            helper.updateSelectedCoin(event.getCurrencyId());
            updateFilterView();
            initData(true, true);
        }
    };
}
