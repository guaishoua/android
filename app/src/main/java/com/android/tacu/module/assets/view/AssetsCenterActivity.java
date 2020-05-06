package com.android.tacu.module.assets.view;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.module.my.view.ConvertActivity;
import com.android.tacu.module.payinfo.view.PayInfoListActivity;

import butterknife.OnClick;

public class AssetsCenterActivity extends BaseActivity {

    @Override
    protected void setView() {
        setContentView(R.layout.activity_asset_center);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.drawer_asset_center));

    }

    @OnClick(R.id.rl_convert_money)
    void covertMoneyClick() {
        jumpTo(ConvertActivity.class);
    }

    @OnClick(R.id.rl_binding_pay_info)
    void bindingPayInfoClick() {
        if (!spUtil.getLogin()) {
            jumpTo(LoginActivity.class);
        } else if (spUtil.getIsAuthSenior() == -1 || spUtil.getIsAuthSenior() == 0 || spUtil.getIsAuthSenior() == 1) {
            showToastError(getResources().getString(R.string.please_get_the_level_of_KYC));
        } else {
            jumpTo(PayInfoListActivity.class);
        }
    }
}
