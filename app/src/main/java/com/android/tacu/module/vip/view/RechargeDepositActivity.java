package com.android.tacu.module.vip.view;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.vip.contract.RechargeDepositContract;
import com.android.tacu.module.vip.presenter.RechargeDepositPresenter;

public class RechargeDepositActivity extends BaseActivity<RechargeDepositPresenter> implements RechargeDepositContract.IView {

    @Override
    protected void setView() {
        setContentView(R.layout.activity_recharge_deposit);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.recharge_deposit));
    }

    @Override
    protected RechargeDepositPresenter createPresenter(RechargeDepositPresenter mPresenter) {
        return new RechargeDepositPresenter();
    }

    @Override
    public void customerCoinByOneCoin(Double value) {
    }

    @Override
    public void BondAccount(OtcAmountModel model) {
        if (model != null) {
        } else {
        }
    }

    @Override
    public void CcToBondSuccess() {
    }

    @Override
    public void BondToCcSuccess() {
    }
}
