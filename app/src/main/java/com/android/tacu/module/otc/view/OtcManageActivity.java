package com.android.tacu.module.otc.view;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.common.TabAdapter;
import com.android.tacu.module.auth.view.AuthMerchantActivity;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.otc.contract.OtcManageContract;
import com.android.tacu.module.otc.presenter.OtcManagePresenter;
import com.android.tacu.utils.user.UserManageUtils;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.TextWidthColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class OtcManageActivity extends BaseActivity<OtcManagePresenter> implements OtcManageContract.IView {

    @BindView(R.id.magic_indicator)
    ScrollIndicatorView titleIndicatorView;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private String[] tabTitle;
    private List<Fragment> fragmentList = new ArrayList<>();
    private IndicatorViewPager indicatorViewPager;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_manage);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.otc_manage));

        tabTitle = new String[]{getResources().getString(R.string.all_otc_order), getResources().getString(R.string.look_buy_order), getResources().getString(R.string.look_sell_order)};

        fragmentList.add(OtcManageFragment.newInstance(0));
        fragmentList.add(OtcManageFragment.newInstance(1));
        fragmentList.add(OtcManageFragment.newInstance(2));

        titleIndicatorView.setBackgroundColor(ContextCompat.getColor(this, R.color.tab_bg_color));
        titleIndicatorView.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(this, R.color.tab_default), ContextCompat.getColor(this, R.color.tab_text_color)).setSize(14, 14));
        titleIndicatorView.setScrollBar(new TextWidthColorBar(this, titleIndicatorView, ContextCompat.getColor(this, R.color.tab_default), 4));
        titleIndicatorView.setSplitAuto(true);

        viewpager.setOffscreenPageLimit(tabTitle.length - 1);
        indicatorViewPager = new IndicatorViewPager(titleIndicatorView, viewpager);
        indicatorViewPager.setAdapter(new TabAdapter(getSupportFragmentManager(), this, tabTitle, fragmentList));
    }

    @OnClick(R.id.btn_publish_otc_order)
    void publishClick() {
        if (spUtil.getApplyMerchantStatus() != 2 && spUtil.getApplyAuthMerchantStatus() != 2) {
            showToastError(getResources().getString(R.string.you_art_shoper_first));
            return;
        }
        jumpTo(OtcPublishActivity.class);
    }

    @OnClick(R.id.btn_certified_shoper)
    void certifiedShoperClick() {
        jumpTo(AuthMerchantActivity.class);
    }

    @Override
    public void ownCenter(OwnCenterModel model) {
        UserManageUtils.setPersonInfo(model);
    }
}
