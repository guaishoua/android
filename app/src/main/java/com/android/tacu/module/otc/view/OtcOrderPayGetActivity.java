package com.android.tacu.module.otc.view;

import com.android.tacu.R;
import com.android.tacu.module.otc.contract.OtcOrderDetailContract;

/**
 * 待收款
 */
public class OtcOrderPayGetActivity extends BaseOtcOrderActvity implements OtcOrderDetailContract.IPayedView {

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_order_payget);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.otc_order_payed));
    }
}
