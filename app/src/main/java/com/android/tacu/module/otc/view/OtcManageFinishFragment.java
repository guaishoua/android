package com.android.tacu.module.otc.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.otc.contract.OtcManageFinishContract;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcTradeAllModel;
import com.android.tacu.module.otc.model.OtcTradeListModel;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.presenter.OtcManageFinishPresenter;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.GlideUtils;
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

public class OtcManageFinishFragment extends BaseFragment<OtcManageFinishPresenter> implements OtcManageFinishContract.IView {

    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshManage;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private View emptyView;

    private String orderId;
    private OtcOrderAdapter orderAdapter;

    private int start = 1;
    private List<OtcTradeAllModel> tradeModelList = new ArrayList<>();
    private boolean isFirst = true;
    private OtcManageOrderActivity.onFinishLister lister;

    public static OtcManageFinishFragment newInstance(String orderId) {
        Bundle bundle = new Bundle();
        bundle.putString("orderId", orderId);
        OtcManageFinishFragment fragment = new OtcManageFinishFragment();
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
            orderId = bundle.getString("orderId");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_otc_manage_finish;
    }

    @Override
    protected void initData(View view) {
        emptyView = View.inflate(getContext(), R.layout.view_empty, null);

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
    protected OtcManageFinishPresenter createPresenter(OtcManageFinishPresenter mPresenter) {
        return new OtcManageFinishPresenter();
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
                    if (lister != null) {
                        lister.onFinishL(tradeModelList);
                    }
                    if (tradeModelList.size() >= model.total) {
                        refreshManage.setEnableLoadmore(false);
                    } else {
                        start++;
                        refreshManage.setEnableLoadmore(true);
                    }
                }
            } else if (start == 1) {
                orderAdapter.setNewData(null);
                if (lister != null) {
                    lister.onFinishL(null);
                }
                orderAdapter.setEmptyView(emptyView);
                refreshManage.setEnableLoadmore(false);
            }
        } else if (start == 1) {
            orderAdapter.setNewData(null);
            if (lister != null) {
                lister.onFinishL(null);
            }
            orderAdapter.setEmptyView(emptyView);
            refreshManage.setEnableLoadmore(false);
        }
    }


    private void upload(boolean isShowView, boolean isTop) {
        if (!isVisibleToUser) {
            return;
        }
        if (isTop) {
            start = 1;
        }
        if (isFirst) {
            isFirst = false;
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
        protected void convert(BaseViewHolder holder, final OtcTradeAllModel item) {
            if (item.infoModel != null) {
                GlideUtils.disPlay(getContext(), CommonUtils.getHead(item.infoModel.headImg), (ImageView) holder.getView(R.id.img_user));
                holder.setText(R.id.tv_nickname, item.infoModel.nickname + "(" + CommonUtils.nameXing(item.infoModel.firstName, item.infoModel.secondName) + ")");
            }
            if (item.tradeModel != null) {
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

                holder.setText(R.id.tv_coin_name, item.tradeModel.currencyName);

                holder.setGone(R.id.rl_red, false);
                if (item.tradeModel.buyuid == spUtil.getUserUid() && item.tradeModel.buyreadstatus != null && item.tradeModel.buyreadstatus == 0) {
                    holder.setGone(R.id.rl_red, true);
                } else if (item.tradeModel.selluid == spUtil.getUserUid() && item.tradeModel.sellreadstatus != null && item.tradeModel.sellreadstatus == 0) {
                    holder.setGone(R.id.rl_red, true);
                }

                Boolean isBuy = null;
                if (item.tradeModel.buyuid == spUtil.getUserUid()) {
                    isBuy = true;
                } else if (item.tradeModel.selluid == spUtil.getUserUid()) {
                    isBuy = false;
                }
                if (isBuy) {
                    holder.setText(R.id.tv_buyorsell, getResources().getString(R.string.buy));
                    holder.setTextColor(R.id.tv_buyorsell, ContextCompat.getColor(mContext, R.color.color_otc_buy));
                } else {
                    holder.setText(R.id.tv_buyorsell, getResources().getString(R.string.sell));
                    holder.setTextColor(R.id.tv_buyorsell, ContextCompat.getColor(mContext, R.color.color_otc_sell));
                }

                holder.setText(R.id.tv_num_title, getResources().getString(R.string.trade_num) + "(" + item.tradeModel.currencyName + ")");
                holder.setText(R.id.tv_amount_title, getResources().getString(R.string.trade_price) + "(" + Constant.ACU_CURRENCY_NAME + ")");

                holder.setText(R.id.tv_num, item.tradeModel.num);
                holder.setText(R.id.tv_amount, item.tradeModel.amount);

                holder.setOnClickListener(R.id.tv_look_detail, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        jumpTo(OtcOrderDetailActivity.createActivity(getContext(), item.tradeModel.orderNo));
                    }
                });
            }
        }
    }

    public void setOnFinishLister(OtcManageOrderActivity.onFinishLister lister) {
        this.lister = lister;
    }
}
