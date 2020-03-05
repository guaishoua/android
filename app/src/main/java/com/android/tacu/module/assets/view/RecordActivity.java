package com.android.tacu.module.assets.view;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.tacu.api.Constant;
import com.android.tacu.module.assets.model.CoinListModel;
import com.android.tacu.utils.SPUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.assets.contract.RecordContract;
import com.android.tacu.module.assets.model.ChargeModel;
import com.android.tacu.module.assets.model.RecordEvent;
import com.android.tacu.module.assets.model.TakeCoinListModel;
import com.android.tacu.module.assets.presenter.RecordPresenter;
import com.android.tacu.utils.ShowToast;
import com.android.tacu.view.popup.CoinFilterView;
import com.android.tacu.view.popup.TypeSwitcherView;
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
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.android.tacu.utils.UIUtils.getContext;

/**
 * Created by xiaohong on 2018/8/29.
 * <p>
 * 查看记录(提币、充值)
 */

public class RecordActivity extends BaseActivity<RecordPresenter> implements RecordContract.IRecordView {

    @BindView(R.id.title)
    View title;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshlayout;
    @BindView(R.id.btn_selected_coin)
    View filterView;
    @BindView(R.id.tv_selected_coin)
    TextView tvSelectedCoin;

    private int status = 0;
    private int pageSize = 10;
    private int currentPage = 1;
    private int typeRecord = 0;
    private TypeSwitcherView switchView;
    private CoinFilterView coinFilterView;
    private Gson gson = new Gson();

    private View emptyView;
    private RecordEvent recordEvent;
    private TakeCoinAdapter takeCoinAdapter;
    private ChargeCoinAdapter chargeCoinAdapter;
    private List<CoinListModel.AttachmentBean> cacheList;

    public static Intent createActivity(Context context, int typeRecord, RecordEvent event) {
        Intent intent = new Intent(context, RecordActivity.class);
        intent.putExtra("typeRecord", typeRecord);
        intent.putExtra("recordEvent", event);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_record);
    }

    @Override
    protected void initView() {
        typeRecord = getIntent().getIntExtra("typeRecord", 0);
        recordEvent = (RecordEvent) getIntent().getSerializableExtra("recordEvent");

        coinFilterView = new CoinFilterView(this, onReceiveEvent);

        emptyView = View.inflate(this, R.layout.view_empty, null);
        initRecyclerView();
        updateCenterTitleView();
        initDatas();
        updateFilterView();
    }

    @Override
    protected RecordPresenter createPresenter(RecordPresenter mPresenter) {
        return new RecordPresenter();
    }

    @Override
    protected void onPresenterCreated(RecordPresenter mPresenter) {
        super.onPresenterCreated(mPresenter);
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
        if (refreshlayout != null && (refreshlayout.isRefreshing() || refreshlayout.isLoading())) {
            refreshlayout.finishRefresh();
            refreshlayout.finishLoadmore();
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

    /**
     * 充币记录
     *
     * @param attachment
     */
    @Override
    public void showChargeCoinList(ChargeModel attachment) {
        if (currentPage == 1) {
            chargeCoinAdapter.setNewData(null);
        }
        if (attachment.total == 0) {
            chargeCoinAdapter.setEmptyView(emptyView);
            chargeCoinAdapter.setNewData(null);
            chargeCoinAdapter.notifyDataSetChanged();
        } else if (attachment.points.size() == 0) {
            refreshlayout.setEnableLoadmore(false);
        } else if (attachment.total >= 10) {
            chargeCoinAdapter.addData(attachment.points);
            refreshlayout.setEnableLoadmore(true);
        } else if (attachment.total < 10) {
            refreshlayout.setEnableLoadmore(false);
            chargeCoinAdapter.setNewData(attachment.points);
        }
    }

    /**
     * 提币列表
     *
     * @param attachment
     */
    @Override
    public void showTakeCoinList(TakeCoinListModel attachment) {
        if (currentPage == 1) {
            takeCoinAdapter.setNewData(null);
        }
        if (attachment.total == 0) {
            takeCoinAdapter.setEmptyView(emptyView);
            takeCoinAdapter.setNewData(null);
            takeCoinAdapter.notifyDataSetChanged();
        } else if (attachment.list.size() == 0) {
            refreshlayout.setEnableLoadmore(false);
        } else if (attachment.total > 10) {
            takeCoinAdapter.addData(attachment.list);
            refreshlayout.setEnableLoadmore(true);
        } else if (attachment.total <= 10) {
            refreshlayout.setEnableLoadmore(false);
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

    private void updateFilterView() {
        if (recordEvent != null) {
            tvSelectedCoin.setText(recordEvent.getCurrencyNameEn());
        }
    }

    private void updateCenterTitleView() {
        mTopBar.setTitle("");
        TextView view = (TextView) View.inflate(this, R.layout.view_record_switch, null);
        view.setText(typeRecord == 0 ? getString(R.string.recharge) : getString(R.string.withdrawals));
        view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_btn_arrow_right_white, 0);
        mTopBar.setCenterView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView view = (TextView) v;
                view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_btn_arrow_down_white, 0);
                showSwitchPopupWindow();
            }
        });
    }

    private void updateEmptyView() {
        ViewGroup view = (ViewGroup) emptyView.getParent();
        if (view != null)
            view.removeView(emptyView);
    }

    private void showSwitchPopupWindow() {
        setBackGroundAlpha(0.5f);
        switchView = new TypeSwitcherView(this, typeRecord, new TypeSwitcherView.Listener() {

            @Override
            public void onItemPressed(int flag) {
                if (flag == typeRecord) return;
                updateEmptyView();
                typeRecord = flag;
                initAdapter();
                updateCenterTitleView();
                initData(true, true);
            }

            @Override
            public void onDismiss() {
                setBackGroundAlpha(1f);
            }
        });

        switchView.showOnAnchor(title,
                RelativePopupWindow.VerticalPosition.BELOW,
                RelativePopupWindow.HorizontalPosition.CENTER,
                false);
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CustomTextHeaderView headerView = new CustomTextHeaderView(getContext());
        headerView.setPrimaryColors(ContextCompat.getColor(getContext(), R.color.content_bg_color), ContextCompat.getColor(getContext(), R.color.text_color));
        refreshlayout.setRefreshHeader(headerView);
        refreshlayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_default)));
        refreshlayout.setEnableLoadmore(false);
        refreshlayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
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
        chargeCoinAdapter = new ChargeCoinAdapter();

        initAdapter();
    }

    /**
     * 根据不同的记录绑定不同的adapter
     */
    private void initAdapter() {
        switch (typeRecord) {
            case 0:
                recyclerView.setAdapter(chargeCoinAdapter);
                break;
            case 1:
                recyclerView.setAdapter(takeCoinAdapter);
                break;
        }
    }

    /**
     * 提币
     */
    private class TakeCoinAdapter extends BaseQuickAdapter<TakeCoinListModel.ListBean, BaseViewHolder> {

        public TakeCoinAdapter() {
            super(R.layout.item_record_global);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final TakeCoinListModel.ListBean item) {
            helper.setText(R.id.tv_fee, BigDecimal.valueOf(item.fee).toPlainString());
            helper.setText(R.id.tv_coins_name, getMainCoinString() + getResources().getString(R.string.withdrawals));
            helper.setText(R.id.tv_coin_currency_name, item.currencyNameEn);
            helper.setTextColor(R.id.tv_coin_currency_name, getResources().getColor(R.color.color_risedown));
            helper.setText(R.id.tv_coins_amount, BigDecimal.valueOf(item.amount).toPlainString());
            helper.setTextColor(R.id.tv_coins_amount, getResources().getColor(R.color.color_risedown));
            helper.setText(R.id.tv_status, getResources().getString(R.string.withdrawals));
            helper.setText(R.id.tv_state, "(" + item.confirms + ")");
            helper.setText(R.id.tv_time, item.createTime);
            helper.setText(R.id.tv_coins_address, item.address);
            helper.setText(R.id.tv_txid, item.walletWaterSn);

            helper.setOnClickListener(R.id.tv_txid, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copy(item.walletWaterSn);
                }
            });
            helper.setOnClickListener(R.id.tv_coins_address, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copy(item.address);
                }
            });
        }
    }

    /**
     * 充币
     */
    private class ChargeCoinAdapter extends BaseQuickAdapter<ChargeModel.PointsBean, BaseViewHolder> {
        public ChargeCoinAdapter() {
            super(R.layout.item_record_global);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final ChargeModel.PointsBean item) {
            helper.setText(R.id.tv_fee, BigDecimal.valueOf(item.fee).toPlainString());
            helper.setText(R.id.tv_coins_name, getMainCoinString() + getResources().getString(R.string.recharge));
            helper.setText(R.id.tv_coin_currency_name, item.currencyNameEn);
            helper.setTextColor(R.id.tv_coin_currency_name, getResources().getColor(R.color.color_riseup));
            helper.setText(R.id.tv_coins_amount, BigDecimal.valueOf(item.realNum).toPlainString());
            helper.setTextColor(R.id.tv_coins_amount, getResources().getColor(R.color.color_riseup));
            helper.setText(R.id.tv_status, getResources().getString(R.string.recharge));
            helper.setText(R.id.tv_state, "(" + item.confirms + ")");
            helper.setText(R.id.tv_time, item.createTime);
            helper.setText(R.id.tv_coins_address, item.walletSn);
            helper.setText(R.id.tv_txid, item.txId);

            helper.setOnClickListener(R.id.tv_txid, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copy(item.txId);
                }
            });
            helper.setOnClickListener(R.id.tv_coins_address, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copy(item.walletSn);
                }
            });
        }
    }

    private String getMainCoinString() {
        String coin = "";
        if (recordEvent != null)
            coin = recordEvent.getCurrencyNameEn();
        return coin;
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

    private void initDatas() {
        String cacheString = SPUtils.getInstance().getString(Constant.SELECT_COIN_NOGROUP_CACHE);
        cacheList = gson.fromJson(cacheString, new TypeToken<List<CoinListModel.AttachmentBean>>() {
        }.getType());
        if (coinFilterView != null) {
            coinFilterView.setList(cacheList);
        }
    }

    public void initData(boolean isLoad, boolean showLoad) {
        if (isLoad) {
            currentPage = 1;
        } else {
            ++currentPage;
        }
        switch (typeRecord) {
            case 0:
                if (recordEvent == null) {
                    mPresenter.selectListByUuid("", "", pageSize, 0, currentPage, status, showLoad);
                } else {
                    mPresenter.selectListByUuid(recordEvent.getStartTime(), recordEvent.getEndTime(), pageSize, recordEvent.getCurrencyId(), currentPage, status, showLoad);
                }
                break;
            case 1:
                if (recordEvent == null) {
                    mPresenter.selectTakeList("", "", pageSize, 0, currentPage, status, showLoad);
                } else {
                    mPresenter.selectTakeList(recordEvent.getStartTime(), recordEvent.getEndTime(), pageSize, recordEvent.getCurrencyId(), currentPage, status, showLoad);
                }
                break;
        }
    }

    CoinFilterView.Listener onReceiveEvent = new CoinFilterView.Listener() {
        @Override
        public void onItemPress(RecordEvent event) {
            recordEvent = event;
            updateFilterView();
            initData(true, true);
        }
    };
}
