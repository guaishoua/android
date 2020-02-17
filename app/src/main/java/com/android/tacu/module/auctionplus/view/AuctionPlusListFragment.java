package com.android.tacu.module.auctionplus.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.AuctionPlusListVisibleHintEvent;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.utils.UIUtils;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by jiazhen on 2019/6/3.
 */
public class AuctionPlusListFragment extends BaseFragment {

    @BindView(R.id.title_IndicatorView)
    ScrollIndicatorView titleIndicatorView;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private String[] tabTitle;
    private List<Fragment> fragmentList = new ArrayList<>();

    private IndicatorViewPager indicatorViewPager;

    public static AuctionPlusListFragment newInstance() {
        Bundle bundle = new Bundle();
        AuctionPlusListFragment fragment = new AuctionPlusListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (spUtil != null) {
            EventManage.sendEvent(new BaseEvent<>(EventConstant.AuctionPlusListCode, new AuctionPlusListVisibleHintEvent(isVisibleToUser)));
        }
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_auction_plus_list;
    }

    @Override
    protected void initData(View view) {
        tabTitle = new String[]{getResources().getString(R.string.auction_list_all), getResources().getString(R.string.auction_list_going), getResources().getString(R.string.auctionplus_list_start), getResources().getString(R.string.auction_list_finish), getResources().getString(R.string.pay_before), getResources().getString(R.string.auction_list_collection)};
        fragmentList.add(AuctionPlusFragment.newInstance(0));
        fragmentList.add(AuctionPlusFragment.newInstance(1));
        fragmentList.add(AuctionPlusFragment.newInstance(2));
        fragmentList.add(AuctionPlusFragment.newInstance(3));
        fragmentList.add(AuctionPlusFragment.newInstance(4));
        fragmentList.add(AuctionPlusFragment.newInstance(5));

        titleIndicatorView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tab_bg_color));
        titleIndicatorView.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(getContext(), R.color.tab_default), ContextCompat.getColor(getContext(), R.color.tab_text_color)).setSize(14, 14));
        titleIndicatorView.setScrollBar(new ColorBar(getContext(), ContextCompat.getColor(getContext(), R.color.tab_default), 4));
        titleIndicatorView.setSplitAuto(true);

        viewpager.setOffscreenPageLimit(tabTitle.length - 1);
        indicatorViewPager = new IndicatorViewPager(titleIndicatorView, viewpager);
        indicatorViewPager.setAdapter(new TabAdapter(getChildFragmentManager()));
        indicatorViewPager.setOnIndicatorItemClickListener(new Indicator.OnIndicatorItemClickListener() {
            @Override
            public boolean onItemClick(View clickItemView, int position) {
                switch (position) {
                    case 4:
                    case 5:
                        if (!spUtil.getLogin()) {
                            jumpTo(LoginActivity.class);
                            return true;
                        }
                        break;
                }
                return false;
            }
        });
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
