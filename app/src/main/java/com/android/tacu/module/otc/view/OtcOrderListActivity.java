package com.android.tacu.module.otc.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.utils.UIUtils;
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
        fragmentList.add(OtcOrderFragment.newInstance(0));
        fragmentList.add(OtcOrderFragment.newInstance(0));
        fragmentList.add(OtcOrderFragment.newInstance(0));
        fragmentList.add(OtcOrderFragment.newInstance(0));
        fragmentList.add(OtcOrderFragment.newInstance(0));

        titleIndicatorView.setBackgroundColor(ContextCompat.getColor(this, R.color.tab_bg_color));
        titleIndicatorView.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(this, R.color.tab_default), ContextCompat.getColor(this, R.color.tab_text_color)).setSize(14, 14));
        titleIndicatorView.setScrollBar(new ColorBar(this, ContextCompat.getColor(this, R.color.tab_default), 4));
        titleIndicatorView.setSplitAuto(true);

        viewpager.setOffscreenPageLimit(tabTitle.length - 1);
        indicatorViewPager = new IndicatorViewPager(titleIndicatorView, viewpager);
        indicatorViewPager.setAdapter(new TabAdapter(getSupportFragmentManager()));
    }

    private class TabAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

        public TabAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return tabTitle != null ? tabTitle.length : 0;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.view_tab, container, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(tabTitle[position]);
            int padding = UIUtils.dp2px(10);
            textView.setPadding(padding, 0, padding, 0);
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            //这是ViewPager适配器的特点,有两个值 POSITION_NONE，POSITION_UNCHANGED，默认就是POSITION_UNCHANGED,
            // 表示数据没变化不用更新.notifyDataChange的时候重新调用getViewForPage
            return PagerAdapter.POSITION_UNCHANGED;
        }
    }
}
