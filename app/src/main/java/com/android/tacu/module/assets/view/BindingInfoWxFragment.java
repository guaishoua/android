package com.android.tacu.module.assets.view;

import android.os.Bundle;
import android.view.View;

import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.assets.contract.BindingPayInfoContract;
import com.android.tacu.module.assets.presenter.BindingPayInfoPresenter;

public class BindingInfoWxFragment extends BaseFragment<BindingPayInfoPresenter> implements BindingPayInfoContract.IView {

    public static BindingInfoWxFragment newInstance() {
        Bundle bundle = new Bundle();
        BindingInfoWxFragment fragment = new BindingInfoWxFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_wx;
    }

    @Override
    protected void initData(View view) {
    }
}
