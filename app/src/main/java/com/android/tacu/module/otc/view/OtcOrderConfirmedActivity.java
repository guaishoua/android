package com.android.tacu.module.otc.view;

import com.android.tacu.R;
import com.android.tacu.module.otc.contract.OtcOrderDetailContract;

/**
 * 待确认
 */
public class OtcOrderConfirmedActivity extends BaseOtcOrderActvity implements OtcOrderDetailContract.IConfirmedView {

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_order_confirmed);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.otc_order_confirmed));
    }
}
