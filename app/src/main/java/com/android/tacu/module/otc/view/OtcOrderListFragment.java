package com.android.tacu.module.otc.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.OTCOrderVisibleHintEvent;
import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.common.TabAdapter;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.TextWidthColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

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
    //0=全部 1=买 2=卖
    private int buyOrSell;

    public static OtcOrderListFragment newInstance(int buyOrSell) {
        Bundle bundle = new Bundle();
        bundle.putInt("buyOrSell", buyOrSell);
        OtcOrderListFragment fragment = new OtcOrderListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (spUtil != null) {
            EventManage.sendEvent(new BaseEvent<>(EventConstant.OTCOrderVisibleCode, new OTCOrderVisibleHintEvent(isVisibleToUser)));
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            buyOrSell = bundle.getInt("buyOrSell");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_otc_order_list;
    }

    @Override
    protected void initData(View view) {
        tabTitle = new String[]{
                getResources().getString(R.string.otc_order_all),
                getResources().getString(R.string.otc_order_confirmed),
                getResources().getString(R.string.otc_order_finished),
                getResources().getString(R.string.otc_order_payed),
                getResources().getString(R.string.otc_order_coined),
                getResources().getString(R.string.otc_order_cancel),
                getResources().getString(R.string.otc_order_timeout),
                getResources().getString(R.string.otc_order_arbitration),
                getResources().getString(R.string.otc_order_adjude)
        };
        fragmentList.add(OtcOrderFragment.newInstance(buyOrSell, 0));
        fragmentList.add(OtcOrderFragment.newInstance(buyOrSell, 1));
        fragmentList.add(OtcOrderFragment.newInstance(buyOrSell, 10));
        fragmentList.add(OtcOrderFragment.newInstance(buyOrSell, 2));
        fragmentList.add(OtcOrderFragment.newInstance(buyOrSell, 3));
        fragmentList.add(OtcOrderFragment.newInstance(buyOrSell, 17));
        fragmentList.add(OtcOrderFragment.newInstance(buyOrSell, 18));
        fragmentList.add(OtcOrderFragment.newInstance(buyOrSell, 4));
        fragmentList.add(OtcOrderFragment.newInstance(buyOrSell, 12));

        titleIndicatorView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.content_bg_color_grey));
        titleIndicatorView.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(getContext(), R.color.tab_default), ContextCompat.getColor(getContext(), R.color.tab_text_color)).setSize(14, 14));
        titleIndicatorView.setScrollBar(new TextWidthColorBar(getContext(), titleIndicatorView, ContextCompat.getColor(getContext(), R.color.color_transparent), 4));
        titleIndicatorView.setSplitAuto(true);

        viewpager.setOffscreenPageLimit(tabTitle.length - 1);
        indicatorViewPager = new IndicatorViewPager(titleIndicatorView, viewpager);
        indicatorViewPager.setAdapter(new TabAdapter(getChildFragmentManager(), getContext(), tabTitle, fragmentList));
    }
}
