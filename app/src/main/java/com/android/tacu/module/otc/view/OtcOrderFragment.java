package com.android.tacu.module.otc.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.OTCOrderVisibleHintEvent;
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
    //0=全部 1=买 2=卖
    private int buyOrSell;
    private OtcOrderAdapter orderAdapter;

    private int start = 1;
    private boolean isFirst = true;
    private boolean isVisibleToUserParent = false;
    private List<OtcTradeAllModel> tradeModelList = new ArrayList<>();

    public static OtcOrderFragment newInstance(int buyOrSell, int orderStatus) {
        Bundle bundle = new Bundle();
        bundle.putInt("buyOrSell", buyOrSell);
        bundle.putInt("orderStatus", orderStatus);
        OtcOrderFragment fragment = new OtcOrderFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initLazy() {
        super.initLazy();
        if (isFirst) {
            upload(true, true);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            buyOrSell = bundle.getInt("buyOrSell");
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
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.item_recyclerview_divider_dp10));
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
        upload(isFirst, true);
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
    protected void receiveEvent(BaseEvent event) {
        super.receiveEvent(event);
        if (event != null) {
            switch (event.getCode()) {
                case EventConstant.OTCOrderVisibleCode:
                    OTCOrderVisibleHintEvent otcOrderVisibleHintEvent = (OTCOrderVisibleHintEvent) event.getData();
                    isVisibleToUserParent = otcOrderVisibleHintEvent.isVisibleToUser();
                    upload(isFirst, true);
                    break;
            }
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
        if (!isVisibleToUserParent || !isVisibleToUser) {
            return;
        }
        if (isFirst) {
            isFirst = false;
        }
        if (isTop) {
            start = 1;
        }
        if (start == 1 && tradeModelList != null && tradeModelList.size() > 0) {
            tradeModelList.clear();
        }
        mPresenter.tradeList(isShowView, null, null, start, 10, buyOrSell, orderStatus);
    }

    public class OtcOrderAdapter extends BaseQuickAdapter<OtcTradeAllModel, BaseViewHolder> {

        public OtcOrderAdapter() {
            super(R.layout.item_otc_order1);
        }

        @Override
        protected void convert(BaseViewHolder holder, final OtcTradeAllModel item) {
            if (item.infoModel != null) {
                holder.setText(R.id.tv_nickname, item.infoModel.nickname + "(" + CommonUtils.nameXing(item.infoModel.secondName) + ")");
                if (item.infoModel.merchantStatus == 0) {
                    holder.setText(R.id.tv_offline, getResources().getString(R.string.offline));
                    holder.setTextColor(R.id.tv_offline, ContextCompat.getColor(getContext(), R.color.text_grey));
                } else {
                    holder.setText(R.id.tv_offline, getResources().getString(R.string.online));
                    holder.setTextColor(R.id.tv_offline, ContextCompat.getColor(getContext(), R.color.text_default));
                }
            }
            if (item.tradeModel != null) {
                holder.setText(R.id.tv_name, item.tradeModel.currencyName);
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

                holder.setGone(R.id.view_four, false);
                holder.setGone(R.id.view_three, false);
                holder.setGone(R.id.img_leftbottom, false);
                holder.setGone(R.id.img_rightbottom, false);
                holder.setGone(R.id.img_leftbottom1, false);
                holder.setGone(R.id.img_rightbottom1, false);
                holder.setTextColor(R.id.tv_lefttop, ContextCompat.getColor(mContext, R.color.text_color));
                holder.setTextColor(R.id.tv_righttop, ContextCompat.getColor(mContext, R.color.text_color));
                holder.setTextColor(R.id.tv_leftbottom, ContextCompat.getColor(mContext, R.color.text_color));
                holder.setTextColor(R.id.tv_rightbottom, ContextCompat.getColor(mContext, R.color.text_color));

                if (item.tradeModel.status != null) {
                    // 1待确认 2 已确认待付款 3已付款待放币 4 仲裁 5 未确认超时取消 6 拒绝订单 7 付款超时取消 8放弃支付 9 放币超时  10放币完成
                    // 12裁决完成 13裁决完成

                    holder.setText(R.id.tv_leftbottom_title, getResources().getString(R.string.trade_num) + "(" + item.tradeModel.currencyName + ")");
                    holder.setText(R.id.tv_rightbottom_title, getResources().getString(R.string.trade_price) + "(CNY)");
                    holder.setText(R.id.tv_leftbottom, item.tradeModel.num);
                    holder.setText(R.id.tv_rightbottom, item.tradeModel.amount);

                    switch (item.tradeModel.status) {
                        case 1:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_confirmed));
                            holder.setTextColor(R.id.tv_status, ContextCompat.getColor(mContext, R.color.text_color));
                            holder.setGone(R.id.view_four, true);
                            holder.setText(R.id.tv_lefttop_title, getResources().getString(R.string.order_time));
                            holder.setText(R.id.tv_righttop_title, getResources().getString(R.string.mobile));
                            holder.setText(R.id.tv_lefttop, item.tradeModel.createTime);
                            holder.setText(R.id.tv_righttop, item.infoModel != null ? item.infoModel.mobile : "");
                            break;
                        case 2:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_payed));
                            holder.setTextColor(R.id.tv_status, ContextCompat.getColor(mContext, R.color.text_default));
                            holder.setGone(R.id.view_four, true);
                            holder.setText(R.id.tv_lefttop_title, getResources().getString(R.string.order_time));
                            holder.setText(R.id.tv_righttop_title, getResources().getString(R.string.mobile));
                            holder.setText(R.id.tv_lefttop, item.tradeModel.createTime);
                            holder.setText(R.id.tv_righttop, item.infoModel != null ? item.infoModel.mobile : "");
                            holder.setTextColor(R.id.tv_rightbottom, ContextCompat.getColor(mContext, R.color.color_otc_buy));
                            break;
                        case 3:
                        case 9:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_coined));
                            holder.setTextColor(R.id.tv_status, ContextCompat.getColor(mContext, R.color.color_coined));
                            holder.setGone(R.id.view_four, true);
                            holder.setText(R.id.tv_lefttop_title, getResources().getString(R.string.order_time));
                            holder.setText(R.id.tv_righttop_title, getResources().getString(R.string.mobile));
                            holder.setText(R.id.tv_lefttop, item.tradeModel.createTime);
                            holder.setText(R.id.tv_righttop, item.infoModel != null ? item.infoModel.mobile : "");
                            holder.setTextColor(R.id.tv_leftbottom, ContextCompat.getColor(mContext, R.color.color_otc_sell));
                            holder.setTextColor(R.id.tv_rightbottom, ContextCompat.getColor(mContext, R.color.color_otc_buy));
                            holder.setGone(R.id.img_leftbottom, true);
                            holder.setGone(R.id.img_rightbottom, true);
                            holder.setImageResource(R.id.img_leftbottom, R.drawable.icon_auth_failure);
                            holder.setImageResource(R.id.img_leftbottom, R.drawable.icon_auth_success);
                            break;
                        case 4:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_arbitration));
                            holder.setTextColor(R.id.tv_status, ContextCompat.getColor(mContext, R.color.color_arbitration));
                            holder.setGone(R.id.view_four, true);
                            holder.setText(R.id.tv_lefttop_title, getResources().getString(R.string.order_time));
                            holder.setText(R.id.tv_righttop_title, getResources().getString(R.string.duifang_shensu));
                            holder.setText(R.id.tv_lefttop, item.tradeModel.createTime);
                            if (item.tradeModel.buyuid == spUtil.getUserUid()) {
                                holder.setText(R.id.tv_righttop_title, getResources().getString(R.string.duifang_shensu));
                            } else if (item.tradeModel.selluid == spUtil.getUserUid()) {
                                holder.setText(R.id.tv_righttop_title, getResources().getString(R.string.tijiao_shensu));
                            }
                            if (item.tradeModel.beArbitrateUid != null && item.tradeModel.beArbitrateUid != 0) {
                                holder.setText(R.id.tv_righttop, getResources().getString(R.string.submited));
                            }else{
                                holder.setText(R.id.tv_righttop, getResources().getString(R.string.unsubmit));
                            }
                            holder.setTextColor(R.id.tv_righttop, ContextCompat.getColor(mContext, R.color.color_otc_sell));
                            holder.setTextColor(R.id.tv_leftbottom, ContextCompat.getColor(mContext, R.color.color_otc_sell));
                            holder.setTextColor(R.id.tv_rightbottom, ContextCompat.getColor(mContext, R.color.color_otc_buy));
                            holder.setGone(R.id.img_leftbottom, true);
                            holder.setGone(R.id.img_rightbottom, true);
                            holder.setImageResource(R.id.img_leftbottom, R.drawable.icon_auth_failure);
                            holder.setImageResource(R.id.img_leftbottom, R.drawable.icon_auth_success);
                            break;
                        case 5:
                        case 7:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_timeout));
                            holder.setTextColor(R.id.tv_status, ContextCompat.getColor(mContext, R.color.color_arbitration));
                            holder.setGone(R.id.view_three, true);
                            holder.setText(R.id.tv_top_title, getResources().getString(R.string.order_id));
                            holder.setText(R.id.tv_top, item.tradeModel.orderNo);
                            holder.setTextColor(R.id.tv_rightbottom, ContextCompat.getColor(mContext, R.color.color_otc_buy));
                            break;
                        case 6:
                        case 8:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_cancel));
                            holder.setTextColor(R.id.tv_status, ContextCompat.getColor(mContext, R.color.text_grey));
                            holder.setGone(R.id.view_three, true);
                            holder.setText(R.id.tv_top_title, getResources().getString(R.string.order_id));
                            holder.setText(R.id.tv_top, item.tradeModel.orderNo);
                            holder.setTextColor(R.id.tv_rightbottom, ContextCompat.getColor(mContext, R.color.color_otc_buy));
                            break;
                        case 10:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_finished));
                            holder.setTextColor(R.id.tv_status, ContextCompat.getColor(mContext, R.color.color_finish));
                            holder.setGone(R.id.view_three, true);
                            holder.setText(R.id.tv_top_title, getResources().getString(R.string.order_id));
                            holder.setText(R.id.tv_top, item.tradeModel.orderNo);
                            holder.setTextColor(R.id.tv_leftbottom, ContextCompat.getColor(mContext, R.color.color_otc_buy));
                            holder.setTextColor(R.id.tv_rightbottom, ContextCompat.getColor(mContext, R.color.color_otc_buy));
                            holder.setGone(R.id.img_leftbottom1, true);
                            holder.setGone(R.id.img_rightbottom1, true);
                            break;
                        case 12:
                        case 13:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_adjude));
                            holder.setTextColor(R.id.tv_status, ContextCompat.getColor(mContext, R.color.color_arbitration_finish));
                            holder.setGone(R.id.view_four, true);
                            holder.setText(R.id.tv_lefttop_title, getResources().getString(R.string.order_time));
                            holder.setText(R.id.tv_righttop_title, getResources().getString(R.string.arbitration_result1));
                            holder.setText(R.id.tv_lefttop, item.tradeModel.createTime);
                            holder.setText(R.id.tv_righttop, getResources().getString(R.string.arbitrationed));
                            holder.setTextColor(R.id.tv_leftbottom, ContextCompat.getColor(mContext, R.color.color_otc_sell));
                            holder.setTextColor(R.id.tv_rightbottom, ContextCompat.getColor(mContext, R.color.color_otc_buy));
                            holder.setTextColor(R.id.tv_righttop, ContextCompat.getColor(mContext, R.color.color_arbitration_finish));
                            holder.setGone(R.id.img_leftbottom, true);
                            holder.setGone(R.id.img_rightbottom, true);
                            holder.setImageResource(R.id.img_leftbottom, R.drawable.icon_auth_failure);
                            holder.setImageResource(R.id.img_leftbottom, R.drawable.icon_auth_success);
                            break;

                    }
                } else {
                    holder.setText(R.id.tv_status, "");
                }
            }

            /*if (item.infoModel != null) {
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
                    // 1待确认 2 已确认待付款 3已付款待放币 4 仲裁 5 未确认超时取消 6 拒绝订单 7 付款超时取消 8放弃支付 9 放币超时 10放币完成
                    // 12仲裁成功 13仲裁失败
                    switch (item.tradeModel.status) {
                        case 1:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_confirmed));
                            break;
                        case 2:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_payed));
                            break;
                        case 3:
                        case 9:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_coined));
                            break;
                        case 4:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_arbitration));
                            break;
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 10:
                        case 12:
                        case 13:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_finished));
                            break;
                    }
                } else {
                    holder.setText(R.id.tv_status, "");
                }
                holder.setText(R.id.tv_order_number, item.tradeModel.currencyName + " (" + item.tradeModel.orderNo + ")");
                holder.setText(R.id.tv_time, item.tradeModel.createTime);

                holder.setText(R.id.tv_amount_title, getResources().getString(R.string.amount) + " " + item.tradeModel.currencyName);
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
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                            case 12:
                            case 13:
                                jumpTo(OtcOrderDetailActivity.createActivity(getContext(), item.tradeModel.orderNo));
                                break;
                        }
                    }
                }
            });*/
        }
    }
}
