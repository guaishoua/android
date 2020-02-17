package com.android.tacu.module.auth.view;

import android.os.Bundle;
import android.view.View;

import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.auth.contract.AuthMerchantContract;
import com.android.tacu.module.auth.presenter.AuthMerchantPresenter;


public class AuthMerchantFragment extends BaseFragment<AuthMerchantPresenter> implements AuthMerchantContract.IView {

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
    }
}
