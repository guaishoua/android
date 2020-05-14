package com.android.tacu.module.Auction.view;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.Auction.adapter.AuctionRecordAdapter;
import com.android.tacu.module.Auction.contract.AuctionContract;
import com.android.tacu.module.Auction.presenter.AuctionPresenter;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import butterknife.BindView;

public class AuctionRecordActivity extends BaseActivity<AuctionPresenter> implements AuctionContract.IRecordView {

    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshlayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private AuctionRecordAdapter recordAdapter;
    private View emptyView;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_auction_record);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.auction_record));

        CustomTextHeaderView header = new CustomTextHeaderView(this);
        header.setPrimaryColors(ContextCompat.getColor(this, R.color.content_bg_color), ContextCompat.getColor(this, R.color.text_color));
        refreshlayout.setRefreshHeader(header);
        refreshlayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_default)));
        refreshlayout.setEnableLoadmore(false);
        refreshlayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
            }
        });

        recordAdapter = new AuctionRecordAdapter();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_recyclerview_divider_dp10));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(recordAdapter);

        emptyView = View.inflate(this, R.layout.view_empty, null);
    }

    @Override
    protected AuctionPresenter createPresenter(AuctionPresenter mPresenter) {
        return new AuctionPresenter();
    }
}
