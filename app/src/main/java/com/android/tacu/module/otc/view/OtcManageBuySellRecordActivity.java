package com.android.tacu.module.otc.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.otc.contract.OtcManageBuySellDetailContract;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcTradeAllModel;
import com.android.tacu.module.otc.model.OtcTradeListModel;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.presenter.OtcManageBuySellDetailPresenter;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.user.UserManageUtils;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class OtcManageBuySellRecordActivity extends BaseActivity<OtcManageBuySellDetailPresenter> implements OtcManageBuySellDetailContract.IRecordView {

    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshManage;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private String orderId;
    private OtcOrderAdapter orderAdapter;

    private int start = 1;
    private List<OtcTradeAllModel> tradeModelList = new ArrayList<>();

    public static Intent createActivity(Context context, String orderId) {
        Intent intent = new Intent(context, OtcManageBuySellRecordActivity.class);
        intent.putExtra("orderId", orderId);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_manage_buy_sell_record);
    }

    @Override
    protected void initView() {
        orderId = getIntent().getStringExtra("orderId");

        mTopBar.setTitle(getResources().getString(R.string.record));

        CustomTextHeaderView header = new CustomTextHeaderView(this);
        header.setPrimaryColors(ContextCompat.getColor(this, R.color.content_bg_color), ContextCompat.getColor(this, R.color.text_color));
        refreshManage.setRefreshHeader(header);
        refreshManage.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_default)));
        refreshManage.setEnableLoadmore(false);
        refreshManage.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                upload(false, true);
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                upload(false, false);
            }
        });

        orderAdapter = new OtcOrderAdapter();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_recyclerview_divider));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(orderAdapter);
    }

    @Override
    protected OtcManageBuySellDetailPresenter createPresenter(OtcManageBuySellDetailPresenter mPresenter) {
        return new OtcManageBuySellDetailPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        upload(true, true);
    }

    @Override
    public void hideRefreshView() {
        super.hideRefreshView();
        if (refreshManage != null && (refreshManage.isRefreshing() || refreshManage.isLoading())) {
            refreshManage.finishRefresh();
            refreshManage.finishLoadmore();
        }
    }

    @OnClick(R.id.tv_create)
    void createClick() {
        if (spUtil.getApplyMerchantStatus() != 2 && spUtil.getApplyAuthMerchantStatus() != 2) {
            showToastError(getResources().getString(R.string.you_art_shoper_first));
            return;
        }
        jumpTo(OtcPublishActivity.class);
    }

    @Override
    public void ownCenter(OwnCenterModel model) {
        UserManageUtils.setPersonInfo(model);
    }

    @Override
    public void tradeListByOrder(OtcTradeListModel model) {
        if (model != null) {
            if (model.list != null && model.list.size() > 0) {
                OtcTradeAllModel allModel = null;
                OtcMarketInfoModel infoModel = null;
                OtcTradeModel tradeModel = null;
                int notOwnUid = 0;
                int ownUid = spUtil.getUserUid();
                List<OtcTradeAllModel> list = new ArrayList<>();

                for (int i = 0; i < model.list.size(); i++) {
                    allModel = new OtcTradeAllModel();
                    tradeModel = model.list.get(i);
                    allModel.tradeModel = tradeModel;
                    if (tradeModel.buyuid != null && tradeModel.buyuid == ownUid && tradeModel.selluid != null && tradeModel.selluid != ownUid) {
                        notOwnUid = tradeModel.selluid;
                    } else if (tradeModel.selluid != null && tradeModel.selluid == ownUid && tradeModel.buyuid != null && tradeModel.buyuid != ownUid) {
                        notOwnUid = tradeModel.buyuid;
                    }
                    if (model.infoList != null && model.infoList.size() > 0) {
                        for (int j = 0; j < model.infoList.size(); j++) {
                            infoModel = model.infoList.get(j);
                            if (infoModel.uid != null && infoModel.uid.equals(notOwnUid)) {
                                allModel.infoModel = infoModel;
                            }
                        }
                    }
                    list.add(allModel);
                }

                if (tradeModelList != null && list.size() > 0) {
                    tradeModelList.addAll(list);
                } else {
                    tradeModelList = list;
                }
                if (tradeModelList != null && tradeModelList.size() > 0) {
                    orderAdapter.setNewData(tradeModelList);
                    if (tradeModelList.size() >= model.total) {
                        refreshManage.setEnableLoadmore(false);
                    } else {
                        start++;
                        refreshManage.setEnableLoadmore(true);
                    }
                }
            } else if (start == 1) {
                orderAdapter.setNewData(null);
                refreshManage.setEnableLoadmore(false);
            }
        } else if (start == 1) {
            orderAdapter.setNewData(null);
            refreshManage.setEnableLoadmore(false);
        }
    }

    private void upload(boolean isShowView, boolean isTop) {
        if (isTop) {
            start = 1;
        }
        if (start == 1 && tradeModelList != null && tradeModelList.size() > 0) {
            tradeModelList.clear();
        }
        mPresenter.tradeListByOrder(isShowView, orderId, start, 10);
    }

    public class OtcOrderAdapter extends BaseQuickAdapter<OtcTradeAllModel, BaseViewHolder> {

        public OtcOrderAdapter() {
            super(R.layout.item_otc_manage_buy_sell_record);
        }

        @Override
        protected void convert(BaseViewHolder holder, OtcTradeAllModel item) {
            if (item.infoModel != null) {
                GlideUtils.disPlay(OtcManageBuySellRecordActivity.this, CommonUtils.getHead(item.infoModel.headImg), (ImageView) holder.getView(R.id.img_user));
            }
            if (item.tradeModel != null) {
                holder.setText(R.id.tv_time, item.tradeModel.updateTime);
                holder.setText(R.id.tv_deal_amount_title, getResources().getString(R.string.quantity_closed) + "(" + item.tradeModel.currencyName + ")");
                holder.setText(R.id.tv_deal_amount, item.tradeModel.num);
            }
        }
    }
}
