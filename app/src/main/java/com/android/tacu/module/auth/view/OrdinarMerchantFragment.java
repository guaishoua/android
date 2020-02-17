package com.android.tacu.module.auth.view;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.auth.contract.AuthMerchantContract;
import com.android.tacu.module.auth.presenter.AuthMerchantPresenter;

import butterknife.BindView;

public class OrdinarMerchantFragment extends BaseFragment<AuthMerchantPresenter> implements AuthMerchantContract.IOrdinarView {

    @BindView(R.id.tv_xieyi)
    TextView tv_xieyi;

    public static OrdinarMerchantFragment newInstance() {
        Bundle bundle = new Bundle();
        OrdinarMerchantFragment fragment = new OrdinarMerchantFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_ordinar_merchant;
    }

    @Override
    protected void initData(View view) {
        tv_xieyi.setText(Html.fromHtml(getResources().getString(R.string.auth_xieyi1) + "<font color=" + ContextCompat.getColor(getContext(), R.color.text_default) + ">" + getResources().getString(R.string.auth_xieyi2) + "</font>"));

    }
}
