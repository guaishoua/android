package com.android.tacu.module.otc.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.otc.contract.OtcManageBuySellDetailContract;
import com.android.tacu.module.otc.presenter.OtcManageBuySellDetailPresenter;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import butterknife.BindView;

public class OtcManageBuySellDetailActivity extends BaseActivity<OtcManageBuySellDetailPresenter> implements OtcManageBuySellDetailContract.IDetailView {

    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshManage;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private boolean isBuy = true; //默认true=买
    private OtcOrderAdapter orderAdapter;

    public static Intent createActivity(Context context, boolean isBuy) {
        Intent intent = new Intent(context, OtcManageBuySellDetailActivity.class);
        intent.putExtra("isBuy", isBuy);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_manage_buy_sell_detail);
    }

    @Override
    protected void initView() {
        if (isBuy) {
            mTopBar.setTitle(getResources().getString(R.string.buy_order_detail));
        } else {
            mTopBar.setTitle(getResources().getString(R.string.sell_order_detail));
        }

        isBuy = getIntent().getBooleanExtra("isBuy", true);

        CustomTextHeaderView header = new CustomTextHeaderView(this);
        header.setPrimaryColors(ContextCompat.getColor(this, R.color.content_bg_color), ContextCompat.getColor(this, R.color.text_color));
        refreshManage.setRefreshHeader(header);
        refreshManage.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_default)));
        refreshManage.setEnableLoadmore(false);
        refreshManage.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
            }
        });

        orderAdapter = new OtcOrderAdapter();
        orderAdapter.setHeaderFooterEmpty(true, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_recyclerview_divider));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(orderAdapter);

        initHeader();
    }

    @Override
    protected OtcManageBuySellDetailPresenter createPresenter(OtcManageBuySellDetailPresenter mPresenter) {
        return new OtcManageBuySellDetailPresenter();
    }

    private void initHeader() {
        View headerView = View.inflate(this, R.layout.header_otc_manage_buy_sell_detail, null);
        orderAdapter.addHeaderView(headerView);
    }

    public class OtcOrderAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public OtcOrderAdapter() {
            super(R.layout.item_otc_manage_buy_sell_detail);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {

        }
    }
}
