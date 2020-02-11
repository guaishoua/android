package com.android.tacu.module.otc.view;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.otc.contract.OtcOrderDetailContract;
import com.android.tacu.view.NodeProgressView;

import butterknife.BindView;

/**
 * 待确认
 */
public class OtcOrderConfirmedActivity extends BaseActivity implements OtcOrderDetailContract.IConfirmedView {

    @BindView(R.id.node_progress)
    NodeProgressView nProgress;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_order_confirmed);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.otc_order_confirmed));
    }
}
