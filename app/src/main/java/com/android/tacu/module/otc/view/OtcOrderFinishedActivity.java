package com.android.tacu.module.otc.view;

import com.android.tacu.R;
import com.android.tacu.module.otc.contract.OtcOrderDetailContract;

/**
 * 已完成
 */
public class OtcOrderFinishedActivity extends BaseOtcOrderActvity implements OtcOrderDetailContract.IFinishedView {

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_order_finished);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.otc_order_finished));
    }
}
