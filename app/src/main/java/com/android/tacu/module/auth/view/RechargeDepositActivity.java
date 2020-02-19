package com.android.tacu.module.auth.view;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;

public class RechargeDepositActivity extends BaseActivity {
    @Override
    protected void setView() {
        setContentView(R.layout.activity_recharge_deposit);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.recharge_deposit));
    }
}
