package com.android.tacu.module.otc.view;

import com.android.tacu.R;
import com.android.tacu.module.otc.contract.OtcOrderDetailContract;

/**
 * 仲裁中
 */
public class OtcOrderArbitrationActivity extends BaseOtcOrderActvity implements OtcOrderDetailContract.IArbitrationView {

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_order_arbitration);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.otc_order_arbitration));
    }
}
