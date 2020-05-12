package com.android.tacu.module.Auction.view;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.Auction.contract.AuctionContract;
import com.android.tacu.module.Auction.presenter.AuctionPresenter;
import com.android.tacu.widget.BubbleProgressView;

import butterknife.BindView;

public class AuctionDetailsActivity extends BaseActivity<AuctionPresenter> implements AuctionContract.IView {

    @BindView(R.id.progress)
    BubbleProgressView progress;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_auction_details);
    }

    @Override
    protected void initView() {
        progress.setProgress(50, 100);
    }
}
