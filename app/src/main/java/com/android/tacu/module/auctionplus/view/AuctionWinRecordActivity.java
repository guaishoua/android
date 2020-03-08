package com.android.tacu.module.auctionplus.view;

import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.MainSwitchEvent;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.assets.model.CoinListModel;
import com.android.tacu.module.auctionplus.contract.AuctionWinRecordConstract;
import com.android.tacu.module.auctionplus.dialog.AuctionPlusPayDialogUtils;
import com.android.tacu.module.auctionplus.modal.AuctionPlusWinLisyModel;
import com.android.tacu.module.auctionplus.modal.AuctionRecordParam;
import com.android.tacu.module.auctionplus.presenter.AuctionWinRecordPresenter;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.utils.StatusBarUtils;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.android.tacu.widget.dialog.DroidDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by jiazhen on 2019/6/3.
 */
public class AuctionWinRecordActivity extends BaseActivity<AuctionWinRecordPresenter> implements AuctionWinRecordConstract.IView {

    @BindView(R.id.drawerLayout_win)
    DrawerLayout drawerManage;
    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshlayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.drawer_right)
    View viewDrawer;

    private int start = 1;
    private AuctionRecordParam param;
    private Gson gson = new Gson();

    private RecordPlusAdapter recordPlusAdapter;

    private AuctionRecordDrawerLayoutHelper mHelper;
    private List<CoinListModel.AttachmentBean> cacheList;

    private List<AuctionPlusWinLisyModel.Bean> plusWinList = new ArrayList<>();

    private DroidDialog dialog;
    private View emptyView;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_auction_win_record);
    }

    @Override
    protected void initView() {
        StatusBarUtils.setColorForDrawerLayout(this, drawerManage, ContextCompat.getColor(this, R.color.content_bg_color), 0);
        mTopBar.setTitle(getResources().getString(R.string.auction_win_record));
        mTopBar.addRightImageButton(R.drawable.icon_edit, R.id.qmui_topbar_item_right, 20, 20).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerManage.isDrawerOpen(Gravity.RIGHT)) {
                    drawerManage.openDrawer(Gravity.RIGHT);
                } else {
                    drawerManage.closeDrawer(Gravity.RIGHT);
                }
            }
        });

        CustomTextHeaderView headerView = new CustomTextHeaderView(this);
        headerView.setPrimaryColors(ContextCompat.getColor(this, R.color.content_bg_color), ContextCompat.getColor(this, R.color.text_color));
        refreshlayout.setRefreshHeader(headerView);
        refreshlayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_default)));
        refreshlayout.setEnableLoadmore(false);
        refreshlayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                start = 1;
                upLoad(false);
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                upLoad(false);
            }
        });

        initDatas();
        initAdapter();

        emptyView = View.inflate(this, R.layout.view_empty, null);
    }

    @Override
    protected void initLazy() {
        super.initLazy();
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                initDrawer();
                if (cacheList != null && cacheList.size() > 0) {
                    mPresenter.coins(false);
                } else {
                    mPresenter.coins(true);
                }
            }
        });
    }

    @Override
    protected AuctionWinRecordPresenter createPresenter(AuctionWinRecordPresenter mPresenter) {
        return new AuctionWinRecordPresenter();
    }

    @Override
    protected void onPresenterCreated(AuctionWinRecordPresenter presenter) {
        super.onPresenterCreated(presenter);
        param = new AuctionRecordParam(0, 4);
        upLoad(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            mHelper.clearActivity();
            mHelper = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerManage.isDrawerOpen(Gravity.RIGHT)) {
            drawerManage.closeDrawer(Gravity.RIGHT);
            return;
        }
        finish();
    }

    @Override
    public void hideRefreshView() {
        super.hideRefreshView();
        if (refreshlayout != null && (refreshlayout.isRefreshing() || refreshlayout.isLoading())) {
            refreshlayout.finishRefresh();
            refreshlayout.finishLoadmore();
        }
    }

    @Override
    public void currencyView(List<CoinListModel.AttachmentBean> attachment) {
        SPUtils.getInstance().put(Constant.SELECT_COIN_NOGROUP_CACHE, gson.toJson(attachment));
        if (mHelper != null) {
            mHelper.setCoinList(attachment);
        }
    }

    @Override
    public void plusWinLisy(AuctionPlusWinLisyModel model) {
        if (model != null && model.list != null && model.list.size() > 0) {
            if (plusWinList != null && plusWinList.size() > 0) {
                plusWinList.addAll(model.list);
            } else {
                plusWinList = model.list;
            }
            if (plusWinList != null && plusWinList.size() > 0) {
                recordPlusAdapter.setNewData(plusWinList);
                if (plusWinList.size() >= model.total) {
                    refreshlayout.setEnableLoadmore(false);
                } else {
                    start++;
                    refreshlayout.setEnableLoadmore(true);
                }
            }
        } else if (start == 1) {
            recordPlusAdapter.setNewData(null);
            recordPlusAdapter.setEmptyView(emptyView);
            refreshlayout.setEnableLoadmore(false);
        }
    }

    @Override
    public void customerCoinByOneCoin(Double value, final AuctionPlusWinLisyModel.Bean plusBean) {
        dialog = AuctionPlusPayDialogUtils.dialogWinPlusShow(this, value, plusBean, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.plusPay(plusBean.id, 1);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventManage.sendEvent(new BaseEvent<>(EventConstant.MainSwitchCode, new MainSwitchEvent(Constant.MAIN_ASSETS)));
                activityManage.finishActivity(AuctionActivity.class);
                finish();
            }
        });
    }

    @Override
    public void auctionPaySuccess() {
        dialog.dismiss();
        showToastSuccess(getResources().getString(R.string.auction_pay_success));
        start = 1;
        param = new AuctionRecordParam(0, 4);
        upLoad(true);
    }

    private void initDatas() {
        String cacheString = SPUtils.getInstance().getString(Constant.SELECT_COIN_NOGROUP_CACHE);
        cacheList = gson.fromJson(cacheString, new TypeToken<List<CoinListModel.AttachmentBean>>() {
        }.getType());
    }

    private void initAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recordPlusAdapter = new RecordPlusAdapter();
        recyclerView.setAdapter(recordPlusAdapter);
    }

    private void initDrawer() {
        mHelper = new AuctionRecordDrawerLayoutHelper(AuctionWinRecordActivity.this, viewDrawer);
        mHelper.setInitView(new AuctionRecordDrawerLayoutHelper.RecordClickListener() {
            @Override
            public void RecordClick(AuctionRecordParam param) {
                AuctionWinRecordActivity.this.param = param;
                start = 1;
                upLoad(true);
                drawerManage.closeDrawer(Gravity.RIGHT);
            }

            @Override
            public void timeClick(TextView tv, String time, boolean isStart) {
                if (isStart) {
                    CommonUtils.selectTime(AuctionWinRecordActivity.this, tv, time);
                } else {
                    CommonUtils.endTime(AuctionWinRecordActivity.this, tv, time);
                }
            }
        });
        mHelper.setCoinList(cacheList);
    }

    private void upLoad(boolean isShowViewing) {
        if (start == 1 && plusWinList != null && plusWinList.size() > 0) {
            plusWinList.clear();
        }
        mPresenter.plusWinLisy(start, param.getStatus(), param.getCurrencyId(), param.getStartTime(), param.getEndTime(), isShowViewing);
    }

    private class RecordPlusAdapter extends BaseQuickAdapter<AuctionPlusWinLisyModel.Bean, BaseViewHolder> {

        public RecordPlusAdapter() {
            super(R.layout.item_auction_record);
        }

        @Override
        protected void convert(BaseViewHolder holder, final AuctionPlusWinLisyModel.Bean item) {
            holder.setText(R.id.tv_name, item.name);
            holder.setText(R.id.tv_mode, "Auction Plus");
            holder.setText(R.id.tv_fee, FormatterUtils.getFormatValue(item.fee) + item.feeName);
            holder.setText(R.id.tv_bonus, FormatterUtils.getFormatValue(item.bonus) + item.bonusName);
            holder.setText(R.id.tv_date, item.createTime);
            holder.setText(R.id.tv_currency, FormatterUtils.getFormatValue(item.gain) + item.gainName);
            holder.setText(R.id.tv_dealprice, FormatterUtils.getFormatValue(item.tradePrice) + item.payName);

            switch (item.status) {
                case 1://待支付
                    holder.setText(R.id.btn_status, getResources().getString(R.string.pay));
                    holder.setTextColor(R.id.btn_status, ContextCompat.getColor(AuctionWinRecordActivity.this, R.color.text_default));
                    holder.getView(R.id.btn_status).setEnabled(true);
                    break;
                case 2://已支付
                    holder.setText(R.id.btn_status, getResources().getString(R.string.pay_after));
                    holder.setTextColor(R.id.btn_status, ContextCompat.getColor(AuctionWinRecordActivity.this, R.color.text_color));
                    holder.getView(R.id.btn_status).setEnabled(false);
                    break;
                case 3://过期
                    holder.setText(R.id.btn_status, getResources().getString(R.string.pay_gotime));
                    holder.setTextColor(R.id.btn_status, ContextCompat.getColor(AuctionWinRecordActivity.this, R.color.text_color));
                    holder.getView(R.id.btn_status).setEnabled(false);
                    break;
            }

            holder.setOnClickListener(R.id.btn_status, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.customerCoinByOneCoin(item.payCurrencyId, item);
                }
            });
        }
    }
}
