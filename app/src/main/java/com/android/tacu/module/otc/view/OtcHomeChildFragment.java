package com.android.tacu.module.otc.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.otc.contract.OtcHomeChildContract;
import com.android.tacu.module.otc.presenter.OtcHomeChildPresenter;

import butterknife.BindView;

public class OtcHomeChildFragment extends BaseFragment<OtcHomeChildPresenter> implements OtcHomeChildContract.IView {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private int currencyId;
    private String currencyNameEn;

    public static OtcHomeChildFragment newInstance(int currencyId, String currencyNameEn) {
        Bundle bundle = new Bundle();
        bundle.putInt("currencyId", currencyId);
        bundle.putString("currencyNameEn", currencyNameEn);
        OtcHomeChildFragment fragment = new OtcHomeChildFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            currencyId = bundle.getInt("currencyId");
            currencyNameEn = bundle.getString("currencyNameEn");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_otc_home_child;
    }

    @Override
    protected void initData() {

    }
}
