package com.android.tacu.module.otc.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.otc.contract.OtcOrderContract;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcTradeAllModel;
import com.android.tacu.module.otc.model.OtcTradeListModel;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.presenter.OtcOrderPresenter;
import com.android.tacu.utils.CommonUtils;
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

public class OtcOrderFragment extends BaseFragment<OtcOrderPresenter> implements OtcOrderContract.IView {

    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshManage;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private int orderStatus = 0;
    private OtcOrderAdapter orderAdapter;

    private int start = 1;
    private List<OtcTradeAllModel> tradeModelList = new ArrayList<>();

    public static OtcOrderFragment newInstance(int orderStatus) {
        Bundle bundle = new Bundle();
        bundle.putInt("orderStatus", orderStatus);
        OtcOrderFragment fragment = new OtcOrderFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initLazy() {
        super.initLazy();
        upload(true, true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            orderStatus = bundle.getInt("orderStatus");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_order;
    }

    @Override
    protected void initData(View view) {
        CustomTextHeaderView header = new CustomTextHeaderView(getContext());
        header.setPrimaryColors(ContextCompat.getColor(getContext(), R.color.content_bg_color), ContextCompat.getColor(getContext(), R.color.text_color));
        refreshManage.setRefreshHeader(header);
        refreshManage.setRefreshFooter(new BallPulseFooter(getContext()).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(getContext(), R.color.color_default)));
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
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.item_recyclerview_divider));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(orderAdapter);
    }

    @Override
    protected OtcOrderPresenter createPresenter(OtcOrderPresenter mPresenter) {
        return new OtcOrderPresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisibleToUser) {
            upload(true, true);
        }
    }

    @Override
    public void hideRefreshView() {
        super.hideRefreshView();
        if (refreshManage != null && (refreshManage.isRefreshing() || refreshManage.isLoading())) {
            refreshManage.finishRefresh();
            refreshManage.finishLoadmore();
        }
    }


    @Override
    public void tradeList(OtcTradeListModel model) {
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
        mPresenter.tradeList(isShowView, null, null, start, 10, null, orderStatus);
    }

    public class OtcOrderAdapter extends BaseQuickAdapter<OtcTradeAllModel, BaseViewHolder> {

        public OtcOrderAdapter() {
            super(R.layout.item_otc_order);
        }

        @Override
        protected void convert(BaseViewHolder holder, final OtcTradeAllModel item) {
            if (item.infoModel != null) {
                holder.setText(R.id.tv_nickname, item.infoModel.nickname + "(" + CommonUtils.nameXing(item.infoModel.secondName) + ")");
            }
            if (item.tradeModel != null) {
                if (item.tradeModel.buyuid == spUtil.getUserUid()) {
                    holder.setText(R.id.tv_buy_status, getResources().getString(R.string.buy));
                    holder.setTextColor(R.id.tv_buy_status, ContextCompat.getColor(getContext(), R.color.color_otc_buy));
                } else if (item.tradeModel.selluid == spUtil.getUserUid()) {
                    holder.setText(R.id.tv_buy_status, getResources().getString(R.string.sell));
                    holder.setTextColor(R.id.tv_buy_status, ContextCompat.getColor(getContext(), R.color.color_otc_sell));
                }
                if (item.tradeModel.payType != null) {
                    //支付类型 1 银行 2微信3支付宝
                    switch (item.tradeModel.payType) {
                        case 1:
                            holder.setImageResource(R.id.img_pay, R.mipmap.img_yhk);
                            break;
                        case 2:
                            holder.setImageResource(R.id.img_pay, R.mipmap.img_wx);
                            break;
                        case 3:
                            holder.setImageResource(R.id.img_pay, R.mipmap.img_zfb);
                            break;
                    }
                }
                if (item.tradeModel.status != null) {
                    // 1待确认 2 已确认待付款 3已付款待放币 4 仲裁 5 未确认超时取消 6 拒绝订单 7 付款超时取消 8放弃支付 9 放币超时 10放币完成 11 待确认 待付款，待放币
                    switch (item.tradeModel.status) {
                        case 1:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_confirmed));
                            break;
                        case 2:
                            if (item.tradeModel.buyuid == spUtil.getUserUid()) {
                                holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_payed));
                            } else if (item.tradeModel.selluid == spUtil.getUserUid()) {
                                holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_payget));
                            }
                            break;
                        case 3:
                            if (item.tradeModel.buyuid == spUtil.getUserUid()) {
                                holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_coinget));
                            } else if (item.tradeModel.selluid == spUtil.getUserUid()) {
                                holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_coined));
                            }
                            break;
                        case 4:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_arbitration));
                            break;
                        case 5:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_notcomfirm_timeout_cancel));
                            break;
                        case 6:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_refuse_order));
                            break;
                        case 7:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_pay_timeout_cancel));
                            break;
                        case 8:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_giveup_pay));
                            break;
                        case 9:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_coin_timeout));
                            break;
                        case 10:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_finished));
                            break;
                    }
                } else {
                    holder.setText(R.id.tv_status, "");
                }
                holder.setText(R.id.tv_order_number, item.tradeModel.currencyName + " (" + item.tradeModel.orderNo + ")");
                holder.setText(R.id.tv_time, item.tradeModel.createTime);

                holder.setText(R.id.tv_amount_title, getResources().getString(R.string.amount) + item.tradeModel.currencyName);
                holder.setText(R.id.tv_amount, item.tradeModel.num);

                holder.setText(R.id.tv_price_title, getResources().getString(R.string.amount_price) + "(CNY)");
                holder.setText(R.id.tv_price, item.tradeModel.amount);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.tradeModel != null && item.tradeModel.status != null) {
                        // 1待确认 2 已确认待付款 3已付款待放币 4 仲裁 5 未确认超时取消 6 拒绝订单 7 付款超时取消 8放弃支付 9 放币超时 10放币完成 11 待确认 待付款，待放币
                        switch (item.tradeModel.status) {
                            case 1:
                                if (item.tradeModel.merchantId == spUtil.getUserUid()) {
                                    jumpTo(OtcOrderConfirmActivity.createActivity(getContext(), item.tradeModel.id, item.tradeModel.orderNo));
                                } else {
                                    jumpTo(OtcOrderDetailActivity.createActivity(getContext(), item.tradeModel.orderNo));
                                }
                                break;
                            case 2:
                                break;
                            case 3:
                                break;
                            case 4:
                                break;
                            case 10:
                                break;
                        }
                    }
                }
            });
        }
    }
}
