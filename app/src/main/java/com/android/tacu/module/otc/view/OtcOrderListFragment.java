package com.android.tacu.module.otc.view;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class OtcOrderListFragment extends BaseFragment {

    @BindView(R.id.title_IndicatorView)
    ScrollIndicatorView titleIndicatorView;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private String[] tabTitle;
    private List<Fragment> fragmentList = new ArrayList<>();

    private IndicatorViewPager indicatorViewPager;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_otc_order_list;
    }

    @Override
    protected void initData(View view) {

    }
}
