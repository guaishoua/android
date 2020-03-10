package com.android.tacu.module.vip.view;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.vip.contract.RechargeDepositContract;
import com.android.tacu.module.vip.model.BondRecordModel;
import com.android.tacu.module.vip.presenter.RechargeDepositPresenter;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.List;

import butterknife.BindView;

public class DepositRecordActivity extends BaseActivity<RechargeDepositPresenter> implements RechargeDepositContract.IRecordView {

    @BindView(R.id.refreshlayout_home)
    SmartRefreshLayout refreshlayout;
    @BindView(R.id.rv)
    RecyclerView recyclerView;

    private OrderAdapter orderAdapter;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_deposit_record);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.margin_account_record));

        CustomTextHeaderView header = new CustomTextHeaderView(this);
        header.setPrimaryColors(ContextCompat.getColor(this, R.color.content_bg_color), ContextCompat.getColor(this, R.color.text_color));
        refreshlayout.setRefreshHeader(header);
        refreshlayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_default)));
        refreshlayout.setEnableLoadmore(false);
        refreshlayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                upload(false);
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
            }
        });

        orderAdapter = new OrderAdapter();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_recyclerview_divider));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(orderAdapter);
    }

    @Override
    protected RechargeDepositPresenter createPresenter(RechargeDepositPresenter mPresenter) {
        return new RechargeDepositPresenter();
    }

    @Override
    protected void onPresenterCreated(RechargeDepositPresenter presenter) {
        super.onPresenterCreated(presenter);
        upload(true);
    }

    @Override
    public void selectBondRecord(List<BondRecordModel> list) {
        if (list != null && list.size() > 0) {
            orderAdapter.setNewData(list);
        } else {
            orderAdapter.setNewData(null);
        }
    }

    @Override
    public void cancelBondRecordSuccess() {
        showToastSuccess(getResources().getString(R.string.order_cancel_success));
        upload(true);
    }

    @Override
    public void hideRefreshView() {
        super.hideRefreshView();
        if (refreshlayout != null && (refreshlayout.isRefreshing() || refreshlayout.isLoading())) {
            refreshlayout.finishRefresh();
            refreshlayout.finishLoadmore();
        }
    }

    private void upload(boolean isShowview) {
        mPresenter.selectBondRecord(isShowview);
    }

    public class OrderAdapter extends BaseQuickAdapter<BondRecordModel, BaseViewHolder> {

        public OrderAdapter() {
            super(R.layout.item_recharge_deposit);
        }

        @Override
        protected void convert(BaseViewHolder helper, final BondRecordModel item) {
            helper.setText(R.id.tv_coin_name, item.currencyName);
            helper.setText(R.id.tv_type, item.getType());
            helper.setTextColor(R.id.tv_type, item.getTypeColor());
            helper.setText(R.id.tv_status, item.getStatus());
            helper.setTextColor(R.id.tv_status, item.getStatusColor());
            helper.setText(R.id.tv_time, item.createTime);
            helper.setText(R.id.tv_amount_title, getResources().getString(R.string.amount) + "(" + item.currencyName + ")");
            helper.setText(R.id.tv_amount, item.amount);
            if (item.status != 1) {
                helper.setGone(R.id.btn_order_cancel, false);
            } else {
                helper.setGone(R.id.btn_order_cancel, true);
            }
            helper.setOnClickListener(R.id.btn_order_cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.cancelBondRecord(item.id);
                }
            });
        }
    }
}
