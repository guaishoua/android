package com.android.tacu.module.otc.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.common.TabAdapter;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.TextWidthColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class OtcMarketListActivity extends BaseActivity {

    @BindView(R.id.magic_indicator)
    ScrollIndicatorView magic_indicator;
    @BindView(R.id.vp)
    ViewPager viewPager;

    private List<String> tabTitle = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();

    private int currentPage = 0;//当前页面是第几
    private String currencyNameEn;

    /**
     * currencyNameEn如果有参数就跳转到对应的页面
     *
     * @param context
     * @param currencyNameEn
     * @return
     */
    public static Intent createActivity(Context context, String currencyNameEn) {
        Intent intent = new Intent(context, OtcMarketListActivity.class);
        intent.putExtra("currencyNameEn", currencyNameEn);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_market_list);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle("OTC");

        currencyNameEn = getIntent().getStringExtra("currencyNameEn");

        tabTitle.add(Constant.OTC_ACU);
        fragmentList.add(OtcMarketFragment.newInstance(Constant.ACU_CURRENCY_ID, Constant.OTC_ACU));

        magic_indicator.setBackgroundColor(ContextCompat.getColor(this, R.color.tab_bg_color));
        magic_indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(this, R.color.text_default), ContextCompat.getColor(this, R.color.tab_text_color)).setSize(14, 14));
        magic_indicator.setScrollBar(new TextWidthColorBar(this, magic_indicator, ContextCompat.getColor(this, R.color.text_default), 4));
        magic_indicator.setSplitAuto(true);

        if (TextUtils.equals(currencyNameEn, Constant.OTC_ACU)) {
            currentPage = 0;
        }

        IndicatorViewPager indicatorViewPager = new IndicatorViewPager(magic_indicator, viewPager);
        indicatorViewPager.setAdapter(new TabAdapter(getSupportFragmentManager(), this, tabTitle, fragmentList));
        viewPager.setOffscreenPageLimit(fragmentList.size() - 1);
        viewPager.setCurrentItem(currentPage, false);
    }
}
