package com.android.tacu.module.otc.view;

import com.android.tacu.R;
import com.android.tacu.module.otc.contract.OtcOrderDetailContract;

/**
 * 待收币
 */
public class OtcOrderCoinGetActivity extends BaseOtcOrderActvity implements OtcOrderDetailContract.ICoinGetView {

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_order_coinget);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.otc_order_coinget));
    }
}
