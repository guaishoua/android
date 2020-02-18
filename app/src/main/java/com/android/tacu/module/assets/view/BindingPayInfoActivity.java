package com.android.tacu.module.assets.view;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.PayInfoEvent;
import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.common.TabAdapter;
import com.android.tacu.module.assets.contract.BindingPayInfoContract;
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.assets.presenter.BindingPayInfoPresenter;
import com.android.tacu.utils.user.UserManageUtils;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.TextWidthColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class BindingPayInfoActivity extends BaseActivity<BindingPayInfoPresenter> implements BindingPayInfoContract.IView {

    @BindView(R.id.magic_indicator)
    ScrollIndicatorView magic_indicator;
    @BindView(R.id.vp)
    ViewPager viewPager;

    private List<String> tabTitle = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();
    private BindingInfoYhkFragment yhkFragment;
    private BindingInfoWxFragment wxFragment;
    private BindingInfoZfbFragment zfbFragment;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_binding_pay_info);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.binding_pay_info));

        tabTitle.add(getResources().getString(R.string.yinhanngka));
        tabTitle.add(getResources().getString(R.string.weixin));
        tabTitle.add(getResources().getString(R.string.zhifubao));

        yhkFragment = BindingInfoYhkFragment.newInstance();
        wxFragment = BindingInfoWxFragment.newInstance();
        zfbFragment = BindingInfoZfbFragment.newInstance();

        fragmentList.add(yhkFragment);
        fragmentList.add(wxFragment);
        fragmentList.add(zfbFragment);

        magic_indicator.setBackgroundColor(ContextCompat.getColor(this, R.color.tab_bg_color));
        magic_indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(this, R.color.text_default), ContextCompat.getColor(this, R.color.tab_text_color)).setSize(14, 14));
        magic_indicator.setScrollBar(new TextWidthColorBar(this, magic_indicator, ContextCompat.getColor(this, R.color.text_default), 4));
        magic_indicator.setSplitAuto(true);

        IndicatorViewPager indicatorViewPager = new IndicatorViewPager(magic_indicator, viewPager);
        indicatorViewPager.setAdapter(new TabAdapter(getSupportFragmentManager(), this, tabTitle, fragmentList));
        viewPager.setOffscreenPageLimit(fragmentList.size() - 1);
        viewPager.setCurrentItem(0, false);
    }

    @Override
    protected void initLazy() {
        super.initLazy();
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                mPresenter.selectBank();
            }
        });
    }

    @Override
    protected BindingPayInfoPresenter createPresenter(BindingPayInfoPresenter mPresenter) {
        return new BindingPayInfoPresenter();
    }

    @Override
    protected void receiveEvent(BaseEvent event) {
        super.receiveEvent(event);
        if (event != null) {
            switch (event.getCode()) {
                case EventConstant.PayInfoCode:
                    PayInfoEvent payInfoEvent = (PayInfoEvent) event.getData();
                    if (payInfoEvent.isNotify()) {
                        mPresenter.selectBank();
                    }
                    break;
            }
        }
    }

    @Override
    public void selectBank(List<PayInfoModel> list) {
        UserManageUtils.setPersonInfo(null, list);
        PayInfoModel yhkModel = null, wxModel = null, zfbModel = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).type != null && list.get(i).type == 1) {
                    yhkModel = list.get(i);
                }
                if (list.get(i).type != null && list.get(i).type == 2) {
                    wxModel = list.get(i);
                }
                if (list.get(i).type != null && list.get(i).type == 3) {
                    zfbModel = list.get(i);
                }
            }
        }
        if (yhkFragment != null) {
            yhkFragment.setValue(yhkModel);
        }
        if (wxFragment != null) {
            wxFragment.setValue(wxModel);
        }
        if (zfbFragment != null) {
            zfbFragment.setValue(zfbModel);
        }
    }
}
