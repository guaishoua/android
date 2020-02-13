package com.android.tacu.module.otc.view;

import com.android.tacu.R;
import com.android.tacu.module.otc.contract.OtcOrderConfirmContract;
import com.android.tacu.module.otc.presenter.OtcOrderConfirmPresenter;

public class OtcOrderConfirmActivity extends BaseOtcHalfOrderActvity<OtcOrderConfirmPresenter> implements OtcOrderConfirmContract.IView {

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_order_confirm);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.otc_order_confirm));
    }

    @Override
    protected OtcOrderConfirmPresenter createPresenter(OtcOrderConfirmPresenter mPresenter) {
        return new OtcOrderConfirmPresenter();
    }
}
