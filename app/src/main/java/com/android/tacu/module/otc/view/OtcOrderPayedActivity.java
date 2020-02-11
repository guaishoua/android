package com.android.tacu.module.otc.view;

import com.android.tacu.R;
import com.android.tacu.module.otc.contract.OtcOrderDetailContract;

import butterknife.OnClick;

/**
 * 待付款
 */
public class OtcOrderPayedActivity extends BaseOtcOrderActvity implements OtcOrderDetailContract.IPayedView {

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_order_payget);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.otc_order_payed));
    }

    @OnClick(R.id.img_payment_code)
    void paymentCodeClick() {

    }
}
