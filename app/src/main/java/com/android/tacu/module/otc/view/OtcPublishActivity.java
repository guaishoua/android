package com.android.tacu.module.otc.view;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.common.TabAdapter;
import com.android.tacu.module.payinfo.model.PayInfoModel;
import com.android.tacu.module.otc.contract.OtcManageBuySellContract;
import com.android.tacu.module.otc.presenter.OtcManageBuySellPresenter;
import com.android.tacu.utils.user.UserManageUtils;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.TextWidthColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class OtcPublishActivity extends BaseActivity<OtcManageBuySellPresenter> implements OtcManageBuySellContract.IView {

    @BindView(R.id.magic_indicator)
    ScrollIndicatorView titleIndicatorView;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private String[] tabTitle;
    private List<Fragment> fragmentList = new ArrayList<>();
    private OtcManageBuySellFragment buyFragment;
    private OtcManageBuySellFragment sellFragment;

    private IndicatorViewPager indicatorViewPager;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_publish);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.publish_otc_order) + "ACU/CNY");

        tabTitle = new String[]{getResources().getString(R.string.goumai), getResources().getString(R.string.chushou)};

        buyFragment = OtcManageBuySellFragment.newInstance(true);
        sellFragment = OtcManageBuySellFragment.newInstance(false);

        fragmentList.add(buyFragment);
        fragmentList.add(sellFragment);

        titleIndicatorView.setBackgroundColor(ContextCompat.getColor(this, R.color.tab_bg_color));
        titleIndicatorView.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(this, R.color.tab_default), ContextCompat.getColor(this, R.color.tab_text_color)).setSize(14, 14));
        titleIndicatorView.setScrollBar(new TextWidthColorBar(this, titleIndicatorView, ContextCompat.getColor(this, R.color.tab_default), 4));
        titleIndicatorView.setSplitAuto(true);

        viewpager.setOffscreenPageLimit(fragmentList.size() - 1);
        indicatorViewPager = new IndicatorViewPager(titleIndicatorView, viewpager);
        indicatorViewPager.setAdapter(new TabAdapter(getSupportFragmentManager(), this, tabTitle, fragmentList));
    }

    @Override
    protected OtcManageBuySellPresenter createPresenter(OtcManageBuySellPresenter mPresenter) {
        return new OtcManageBuySellPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.selectBank();
    }

    @Override
    public void selectBank(List<PayInfoModel> list) {
        UserManageUtils.setPeoplePayInfo(list);
        if (buyFragment != null) {
            buyFragment.setPayList(list);
        }
        if (sellFragment != null) {
            sellFragment.setPayList(list);
        }
    }
}
