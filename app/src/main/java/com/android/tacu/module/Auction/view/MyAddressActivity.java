package com.android.tacu.module.Auction.view;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.Auction.contract.MyAddressContract;
import com.android.tacu.module.Auction.presenter.MyAddressPresenter;

public class MyAddressActivity extends BaseActivity<MyAddressPresenter> implements MyAddressContract.IView {

    @Override
    protected void setView() {
        setContentView(R.layout.activity_my_address);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.my_shipping_address));
    }

    @Override
    protected MyAddressPresenter createPresenter(MyAddressPresenter mPresenter) {
        return new MyAddressPresenter();
    }
}
