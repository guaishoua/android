package com.android.tacu.module.assets.view;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.assets.contract.BindingPayInfoContract;
import com.android.tacu.module.assets.presenter.BindingPayInfoPresenter;

public class BindingInfoChildFragment extends BaseFragment<BindingPayInfoPresenter> implements BindingPayInfoContract.IView {

    //银行卡

    //微信

    //支付宝

    //0=银行卡 1=微信 2=支付宝
    private int status = 0;

    public static BindingInfoChildFragment newInstance(int status) {
        Bundle bundle = new Bundle();
        bundle.putInt("status", status);
        BindingInfoChildFragment fragment = new BindingInfoChildFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            status = bundle.getInt("status");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        if (status == 0) {
            return R.layout.fragment_yhk;
        } else if (status == 1) {
            return R.layout.fragment_wx;
        }
        return R.layout.fragment_zfb;
    }

    @Override
    protected void initData() {

    }
}
