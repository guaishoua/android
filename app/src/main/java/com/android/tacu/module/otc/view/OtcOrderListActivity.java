package com.android.tacu.module.otc.view;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.common.TabAdapter;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class OtcOrderListActivity extends BaseActivity {

    @BindView(R.id.title_IndicatorView)
    ScrollIndicatorView titleIndicatorView;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private String[] tabTitle;
    private List<Fragment> fragmentList = new ArrayList<>();

    private IndicatorViewPager indicatorViewPager;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_order_list);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.order_center));

        tabTitle = new String[]{getResources().getString(R.string.otc_order_all), getResources().getString(R.string.otc_order_finished), getResources().getString(R.string.otc_order_confirmed), getResources().getString(R.string.otc_order_payed), getResources().getString(R.string.otc_order_coined), getResources().getString(R.string.otc_order_arbitration)};
        fragmentList.add(OtcOrderFragment.newInstance(0));
        fragmentList.add(OtcOrderFragment.newInstance(14));
        fragmentList.add(OtcOrderFragment.newInstance(1));
        fragmentList.add(OtcOrderFragment.newInstance(2));
        fragmentList.add(OtcOrderFragment.newInstance(15));
        fragmentList.add(OtcOrderFragment.newInstance(4));

        titleIndicatorView.setBackgroundColor(ContextCompat.getColor(this, R.color.tab_bg_color));
        titleIndicatorView.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(this, R.color.tab_default), ContextCompat.getColor(this, R.color.tab_text_color)).setSize(14, 14));
        titleIndicatorView.setScrollBar(new ColorBar(this, ContextCompat.getColor(this, R.color.tab_default), 4));
        titleIndicatorView.setSplitAuto(true);

        viewpager.setOffscreenPageLimit(tabTitle.length - 1);
        indicatorViewPager = new IndicatorViewPager(titleIndicatorView, viewpager);
        indicatorViewPager.setAdapter(new TabAdapter(getSupportFragmentManager(), this, tabTitle, fragmentList));
    }
}
