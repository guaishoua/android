package com.android.tacu.module.otc.view;

import com.android.tacu.R;
import com.android.tacu.module.otc.contract.OtcOrderDetailContract;

/**
 * 待放币
 */
public class OtcOrderCoinedActivity extends BaseOtcOrderActvity implements OtcOrderDetailContract.ICoinedView {

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_order_coined);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.otc_order_coined));
    }
}
