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
import com.android.tacu.module.otc.contract.OtcManageContract;
import com.android.tacu.module.otc.model.OtcMarketOrderListModel;
import com.android.tacu.module.otc.model.OtcMarketOrderModel;
import com.android.tacu.module.otc.presenter.OtcManagePresenter;
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

public class OtcManageFragment extends BaseFragment<OtcManagePresenter> implements OtcManageContract.IChildView {

    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshManage;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private ManageAdapter manageAdapter;
    //0=全部 1买 2卖
    private int buyorsell = 0;
    private int start = 1;
    private boolean isFirst = true;

    private List<OtcMarketOrderModel> orderList = new ArrayList<>();

    public static OtcManageFragment newInstance(int buyorsell) {
        Bundle bundle = new Bundle();
        bundle.putInt("buyorsell", buyorsell);
        OtcManageFragment fragment = new OtcManageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initLazy() {
        super.initLazy();
        upload(isFirst, true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            buyorsell = bundle.getInt("buyorsell", 0);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_otc_manage;
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

        manageAdapter = new ManageAdapter();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.item_recyclerview_divider));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(manageAdapter);
    }

    @Override
    protected OtcManagePresenter createPresenter(OtcManagePresenter mPresenter) {
        return new OtcManagePresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisibleToUser) {
            upload(isFirst, true);
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
    public void orderListOwn(OtcMarketOrderListModel model) {
        if (model != null) {
            if (model.list != null && model.list.size() > 0) {
                if (orderList != null && orderList.size() > 0) {
                    orderList.addAll(model.list);
                } else {
                    orderList = model.list;
                }
                if (orderList != null && orderList.size() > 0) {
                    manageAdapter.setNewData(orderList);
                    if (orderList.size() >= model.total) {
                        refreshManage.setEnableLoadmore(false);
                    } else {
                        start++;
                        refreshManage.setEnableLoadmore(true);
                    }
                }
            } else if (start == 1) {
                manageAdapter.setNewData(null);
                refreshManage.setEnableLoadmore(false);
            }
        } else if (start == 1) {
            manageAdapter.setNewData(null);
            refreshManage.setEnableLoadmore(false);
        }
    }

    @Override
    public void cancelOrderSuccess() {
        upload(true, true);
    }

    @Override
    public void unshow() {
        upload(true, true);
    }

    @Override
    public void show() {
        upload(true, true);
    }

    private void upload(boolean isShowView, boolean isTop) {
        if (isFirst) {
            isFirst = false;
        }
        if (isTop) {
            start = 1;
        }
        if (start == 1 && orderList != null && orderList.size() > 0) {
            orderList.clear();
        }
        mPresenter.orderListOwn(isShowView, start, 10, buyorsell);
    }

    public class ManageAdapter extends BaseQuickAdapter<OtcMarketOrderModel, BaseViewHolder> {

        public ManageAdapter() {
            super(R.layout.item_otc_manage);
        }

        @Override
        protected void convert(BaseViewHolder holder, final OtcMarketOrderModel item) {
            holder.setText(R.id.tv_coin_name, item.currencyName);
            if (item.buyorsell != null && item.buyorsell == 1) {
                holder.setText(R.id.tv_status, getResources().getString(R.string.buy));
                holder.setTextColor(R.id.tv_status, ContextCompat.getColor(mContext, R.color.color_otc_buy));
            } else if (item.buyorsell != null && item.buyorsell == 2) {
                holder.setText(R.id.tv_status, getResources().getString(R.string.sell));
                holder.setTextColor(R.id.tv_status, ContextCompat.getColor(mContext, R.color.color_otc_sell));
            }

            holder.setGone(R.id.btn_look, true);
            holder.setGone(R.id.btn_show, true);
            holder.setGone(R.id.btn_cancel, true);
            holder.setText(R.id.btn_show, getResources().getString(R.string.hangup));

            if (item.status != null && item.status == 2) {
                holder.setText(R.id.tv_type, getResources().getString(R.string.finished));
                holder.setTextColor(R.id.tv_type, ContextCompat.getColor(mContext, R.color.text_color));
                holder.setGone(R.id.btn_show, false);
                holder.setGone(R.id.btn_cancel, false);
            } else if (item.isshow != null && item.isshow == 0) {
                holder.setText(R.id.tv_type, getResources().getString(R.string.hangup));
                holder.setTextColor(R.id.tv_type, ContextCompat.getColor(mContext, R.color.text_default));
                holder.setText(R.id.btn_show, getResources().getString(R.string.recovery));
            } else {
                holder.setText(R.id.tv_type, getResources().getString(R.string.going));
                holder.setTextColor(R.id.tv_type, ContextCompat.getColor(mContext, R.color.color_otc_buy));
            }

            holder.setText(R.id.tv_sell_thing_title, getResources().getString(R.string.sell_thing) + "(" + item.currencyName + ")");
            holder.setText(R.id.tv_allamount_title, getResources().getString(R.string.all_price) + "(" + item.currencyName + ")");
            holder.setText(R.id.tv_surplus_title, getResources().getString(R.string.order_surplus) + "(" + item.currencyName + ")");
            holder.setText(R.id.tv_deposit_title, getResources().getString(R.string.frezon_deposit) + "(" + item.currencyName + ")");

            holder.setText(R.id.tv_sell_thing, item.num);
            holder.setText(R.id.tv_price, item.price);
            holder.setText(R.id.tv_surplus, item.remainAmount);
            holder.setText(R.id.tv_order_number, item.finishNum);
            holder.setText(R.id.tv_allamount, item.amount);
            holder.setText(R.id.tv_deposit, item.bondFreezeAmount);
            holder.setText(R.id.tv_wait_operation, item.waitNum);

            if (item.payByCard != null && item.payByCard == 1) {
                holder.setGone(R.id.img_yhk, true);
            } else {
                holder.setGone(R.id.img_yhk, false);
            }
            if (item.payWechat != null && item.payWechat == 1) {
                holder.setGone(R.id.img_wx, true);
            } else {
                holder.setGone(R.id.img_wx, false);
            }
            if (item.payAlipay != null && item.payAlipay == 1) {
                holder.setGone(R.id.img_zfb, true);
            } else {
                holder.setGone(R.id.img_zfb, false);
            }

            holder.setOnClickListener(R.id.btn_look, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpTo(OtcManageBuySellDetailActivity.createActivity(getContext(), item.id));
                }
            });

            holder.setOnClickListener(R.id.btn_show, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //1=显示 0=挂起
                    if (item.isshow != null && item.isshow == 0) {
                        mPresenter.show(item.id);
                    } else if (item.isshow != null && item.isshow == 1) {
                        mPresenter.unshow(item.id);
                    }
                }
            });

            holder.setOnClickListener(R.id.btn_cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.cancelOrder(item.id);
                }
            });
        }
    }
}
