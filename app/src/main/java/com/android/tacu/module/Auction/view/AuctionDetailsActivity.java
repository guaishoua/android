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
import com.android.tacu.widget.BubbleProgressView;

import butterknife.BindView;
import butterknife.OnClick;

public class AuctionDetailsActivity extends BaseActivity<AuctionPresenter> implements AuctionContract.IDetailsView {

    @BindView(R.id.progress)
    BubbleProgressView progress;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private AuctionRecordAdapter recordAdapter;
    private View emptyView;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_auction_details);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.auction_paimai));
        mTopBar.addRightTextButton(getResources().getString(R.string.my_auction), R.id.qmui_topbar_item_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

    @OnClick(R.id.tv_look_more)
    void lookMoreClick() {
        jumpTo(AuctionRecordActivity.class);
    }
}
