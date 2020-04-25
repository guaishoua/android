package com.android.tacu.module.transaction.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.market.model.CurrentTradeCoinModel;
import com.android.tacu.module.transaction.model.RecordModel;
import com.android.tacu.utils.UIUtils;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundTextView;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class QuotationFragment extends BaseFragment {

    @BindView(R.id.magic_indicator)
    FixedIndicatorView magicIndicator;
    @BindView(R.id.vp)
    ViewPager viewPager;

    private int currencyId;
    private int baseCurrencyId;
    private String currencyNameEn;
    private String baseCurrencyNameEn;

    private List<String> tabDownTitle = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();
    private IndicatorViewPager indicatorViewPager;

    private KlineFragment klineFragment;
    private DepthFragment depthFragment;

    public static QuotationFragment newInstance(int currencyId, int baseCurrencyId, String currencyNameEn, String baseCurrencyNameEn) {
        Bundle bundle = new Bundle();
        bundle.putInt("currencyId", currencyId);
        bundle.putInt("baseCurrencyId", baseCurrencyId);
        bundle.putString("currencyNameEn", currencyNameEn);
        bundle.putString("baseCurrencyNameEn", baseCurrencyNameEn);
        QuotationFragment fragment = new QuotationFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            currencyId = bundle.getInt("currencyId");
            baseCurrencyId = bundle.getInt("baseCurrencyId");
            currencyNameEn = bundle.getString("currencyNameEn");
            baseCurrencyNameEn = bundle.getString("baseCurrencyNameEn");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_quotation;
    }

    @Override
    protected void initData(View view) {
    }

    @Override
    public void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();

        magicIndicator.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.content_bg_color));
        magicIndicator.setOnTransitionListener(new OnTransitionTextListener() {
            @Override
            public void onTransition(View view, int position, float selectPercent) {
                super.onTransition(view, position, selectPercent);
                QMUIRoundTextView selectText = (QMUIRoundTextView) getTextView(view, position);

                if (selectPercent == 1) {
                    ((QMUIRoundButtonDrawable) selectText.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.chart_gradient_start_color));
                    ((QMUIRoundButtonDrawable) selectText.getBackground()).setStrokeData(UIUtils.dp2px(1), ContextCompat.getColorStateList(getContext(), R.color.color_transparent));
                    selectText.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
                } else {
                    ((QMUIRoundButtonDrawable) selectText.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.content_bg_color));
                    ((QMUIRoundButtonDrawable) selectText.getBackground()).setStrokeData(UIUtils.dp2px(1), ContextCompat.getColorStateList(getContext(), R.color.color_grey_2));
                    selectText.setTextColor(ContextCompat.getColor(getContext(), R.color.text_grey_2));
                }
            }
        });
        magicIndicator.setSplitMethod(FixedIndicatorView.SPLITMETHOD_WRAP);

        tabDownTitle.add(getResources().getString(R.string.kline_view));
        tabDownTitle.add(getResources().getString(R.string.depth_view));

        klineFragment = KlineFragment.newInstance(currencyId, baseCurrencyId, currencyNameEn, baseCurrencyNameEn);
        klineFragment.setItemTouch(new KlineFragment.ItemTouch() {
            @Override
            public void parentTouch(boolean boo) {
                viewPager.requestDisallowInterceptTouchEvent(boo);
            }
        });
        depthFragment = DepthFragment.newInstance();
        fragmentList.add(klineFragment);
        fragmentList.add(depthFragment);

        indicatorViewPager = new IndicatorViewPager(magicIndicator, viewPager);
        indicatorViewPager.setAdapter(new TabAdapter(getChildFragmentManager()));
        viewPager.setOffscreenPageLimit(fragmentList.size() - 1);
        viewPager.setCurrentItem(0, false);
    }

    public void setValue(int currencyId, int baseCurrencyId, String currencyNameEn, String baseCurrencyNameEn) {
        this.currencyId = currencyId;
        this.baseCurrencyId = baseCurrencyId;
        this.currencyNameEn = currencyNameEn;
        this.baseCurrencyNameEn = baseCurrencyNameEn;

        if (klineFragment != null) {
            klineFragment.setValue(currencyId, baseCurrencyId, currencyNameEn, baseCurrencyNameEn);
        }
    }

    public void setCurrentTradeCoinModel(CurrentTradeCoinModel currentTradeCoinModel) {
        if (depthFragment != null) {
            depthFragment.setCurrentTradeCoinModel(currentTradeCoinModel);
        }
    }

    public void entrustInfo(RecordModel recordModel, String currencyNameEn, String baseCurrencyNameEn) {
        if (depthFragment != null) {
            depthFragment.entrustInfo(recordModel, currencyNameEn, baseCurrencyNameEn);
        }
    }

    private class TabAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

        public TabAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return tabDownTitle != null ? tabDownTitle.size() : 0;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.view_tab_2, container, false);
            }
            int padding = UIUtils.dp2px(10);
            QMUIRoundTextView textView = (QMUIRoundTextView) convertView;
            textView.setText(tabDownTitle.get(position));
            textView.setPadding(padding, 0, padding, 0);

            ((QMUIRoundButtonDrawable) textView.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.content_bg_color));
            ((QMUIRoundButtonDrawable) textView.getBackground()).setStrokeData(UIUtils.dp2px(1), ContextCompat.getColorStateList(getContext(), R.color.color_grey_2));
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
