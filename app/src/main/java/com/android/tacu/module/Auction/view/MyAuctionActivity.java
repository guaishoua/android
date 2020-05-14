package com.android.tacu.module.Auction.view;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.Auction.contract.AuctionContract;
import com.android.tacu.module.Auction.presenter.AuctionPresenter;

public class MyAuctionActivity extends BaseActivity<AuctionPresenter> implements AuctionContract.IMyView {

    @Override
    protected void setView() {
        setContentView(R.layout.activity_my_auction);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.my_auction));
    }

    @Override
    protected AuctionPresenter createPresenter(AuctionPresenter mPresenter) {
        return new AuctionPresenter();
    }
}
