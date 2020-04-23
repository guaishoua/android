package com.android.tacu.module.payinfo.view;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.otc.model.OtcTradeAllModel;
import com.android.tacu.module.payinfo.contract.PayInfoListContract;
import com.android.tacu.module.payinfo.model.PayInfoModel;
import com.android.tacu.module.payinfo.presenter.PayInfoPresenter;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;


import java.util.List;

import butterknife.BindView;

public class PayInfoListActivity extends BaseActivity<PayInfoPresenter> implements PayInfoListContract.IView {

    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshManage;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private PayInfoAdapter payInfoAdapter;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_pay_info_list);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.get_setting));
        mTopBar.addRightImageButton(R.drawable.icon_add_white, R.id.qmui_topbar_item_right, 18, 18).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        CustomTextHeaderView header = new CustomTextHeaderView(this);
        header.setPrimaryColors(ContextCompat.getColor(this, R.color.content_bg_color), ContextCompat.getColor(this, R.color.text_color));
        refreshManage.setRefreshHeader(header);
        refreshManage.setEnableLoadmore(false);
        refreshManage.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                jumpTo(PayInfoTypeActivity.class);
            }
        });

        payInfoAdapter = new PayInfoAdapter();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_recyclerview_divider_dp10));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(payInfoAdapter);
    }

    @Override
    protected PayInfoPresenter createPresenter(PayInfoPresenter mPresenter) {
        return new PayInfoPresenter();
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
    public void selectBank(List<PayInfoModel> list) {

    }

    public class PayInfoAdapter extends BaseQuickAdapter<OtcTradeAllModel, BaseViewHolder> {

        public PayInfoAdapter() {
            super(R.layout.item_pay_info);
        }

        @Override
        protected void convert(BaseViewHolder helper, OtcTradeAllModel item) {

        }
    }
}
