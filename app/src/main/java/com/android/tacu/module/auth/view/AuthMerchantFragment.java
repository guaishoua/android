package com.android.tacu.module.auth.view;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.auth.contract.AuthMerchantContract;
import com.android.tacu.module.auth.presenter.AuthMerchantPresenter;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.vip.view.RechargeDepositActivity;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;

import butterknife.BindView;
import butterknife.OnClick;

public class AuthMerchantFragment extends BaseFragment<AuthMerchantPresenter> implements AuthMerchantContract.IAuthView {

    @BindView(R.id.img_satisfy_ordinary_mechant)
    ImageView img_satisfy_ordinary_mechant;
    @BindView(R.id.img_bond)
    ImageView img_bond;
    @BindView(R.id.btn_submit)
    QMUIRoundButton btn_submit;

    private boolean isMechant = false;
    private boolean isBond = false;

    private OwnCenterModel ownCenterModel;
    private OtcAmountModel otcAmountModel;

    public static AuthMerchantFragment newInstance() {
        Bundle bundle = new Bundle();
        AuthMerchantFragment fragment = new AuthMerchantFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_auth_merchant;
    }

    @Override
    protected void initData(View view) {
        dealValue();
    }

    @Override
    protected AuthMerchantPresenter createPresenter(AuthMerchantPresenter mPresenter) {
        return new AuthMerchantPresenter();
    }

    @OnClick(R.id.btn_asset_right)
    void btnClick() {
        jumpTo(RechargeDepositActivity.class);
    }

    @OnClick(R.id.btn_submit)
    void submitClick() {
        mPresenter.applyMerchantAuth();
    }

    @Override
    public void applyMerchantAuthSuccess() {
        showToastSuccess(getResources().getString(R.string.apply_success));
        getHostActivity().finish();
    }

    public void setValue(OwnCenterModel model) {
        this.ownCenterModel = model;
        dealValue();
    }

    public void setBondAccount(OtcAmountModel model) {
        this.otcAmountModel = model;
        dealValue();
    }

    private void dealValue() {
        if (spUtil.getApplyMerchantStatus() == 2) {
            isMechant = true;
            img_satisfy_ordinary_mechant.setImageResource(R.drawable.icon_auth_success);
        } else {
            isMechant = false;
            img_satisfy_ordinary_mechant.setImageResource(R.drawable.icon_auth_failure);
        }
        if (otcAmountModel != null && !TextUtils.isEmpty(otcAmountModel.cashAmount) && Double.valueOf(otcAmountModel.cashAmount) >= 50000) {
            isBond = true;
            img_bond.setImageResource(R.drawable.icon_auth_success);
        } else {
            isBond = false;
            img_bond.setImageResource(R.drawable.icon_auth_failure);
        }
        if (isMechant && isBond) {
            btn_submit.setEnabled(true);
            ((QMUIRoundButtonDrawable) btn_submit.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_default));
        } else {
            btn_submit.setEnabled(false);
            ((QMUIRoundButtonDrawable) btn_submit.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_grey));
        }
        if (spUtil.getApplyAuthMerchantStatus() == 1 || spUtil.getApplyAuthMerchantStatus() == 2) {
            btn_submit.setEnabled(false);
            ((QMUIRoundButtonDrawable) btn_submit.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_grey));
            if (spUtil.getApplyAuthMerchantStatus() == 1) {
                btn_submit.setText(getResources().getString(R.string.to_be_examine));
            } else if (spUtil.getApplyAuthMerchantStatus() == 2) {
                btn_submit.setText(getResources().getString(R.string.apply_success1));
            }
        }
    }
}
