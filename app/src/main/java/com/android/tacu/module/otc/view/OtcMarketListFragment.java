package com.android.tacu.module.otc.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.MainDrawerLayoutOpenEvent;
import com.android.tacu.EventBus.model.OTCListVisibleHintEvent;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.common.TabAdapter;
import com.android.tacu.module.auth.view.AuthMerchantActivity;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.module.otc.dialog.OtcDialogUtils;
import com.android.tacu.utils.UIUtils;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.TextWidthColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class OtcMarketListFragment extends BaseFragment {

    @BindView(R.id.title)
    QMUITopBar mTopBar;
    @BindView(R.id.magic_indicator)
    ScrollIndicatorView magic_indicator;
    @BindView(R.id.vp)
    ViewPager viewPager;

    private List<String> tabTitle = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();

    public static OtcMarketListFragment newInstance() {
        Bundle bundle = new Bundle();
        OtcMarketListFragment fragment = new OtcMarketListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (spUtil != null) {
            EventManage.sendEvent(new BaseEvent<>(EventConstant.OTCListVisibleCode, new OTCListVisibleHintEvent(isVisibleToUser)));
        }
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_otc_market_list;
    }

    @Override
    protected void initData(View view) {
        initTitle();

        tabTitle.add(Constant.OTC_ACU);
        fragmentList.add(OtcMarketFragment.newInstance(Constant.ACU_CURRENCY_ID, Constant.OTC_ACU));

        magic_indicator.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tab_bg_color));
        magic_indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(getContext(), R.color.text_default), ContextCompat.getColor(getContext(), R.color.tab_text_color)).setSize(14, 14));
        magic_indicator.setScrollBar(new TextWidthColorBar(getContext(), magic_indicator, ContextCompat.getColor(getContext(), R.color.text_default), 4));
        magic_indicator.setSplitAuto(false);

        IndicatorViewPager indicatorViewPager = new IndicatorViewPager(magic_indicator, viewPager);
        indicatorViewPager.setAdapter(new TabAdapter(getChildFragmentManager(), getContext(), tabTitle, fragmentList));
        viewPager.setOffscreenPageLimit(fragmentList.size() - 1);
        viewPager.setCurrentItem(0, false);
    }

    @OnClick(R.id.lin_guanggao)
    void guanggaoClick() {
        if (!OtcDialogUtils.isDialogShow(getContext())) {
            jumpTo(OtcManageActivity.class);
        }
    }

    @OnClick(R.id.lin_auth)
    void authClick() {
        if (spUtil.getLogin()) {
            jumpTo(AuthMerchantActivity.class);
        } else {
            jumpTo(LoginActivity.class);
        }
    }

    private void initTitle() {
        mTopBar.setTitle("OTC");
        mTopBar.setBackgroundDividerEnabled(true);

        ImageView circleImageView = new ImageView(getContext());
        circleImageView.setBackgroundColor(Color.TRANSPARENT);
        circleImageView.setScaleType(CENTER_CROP);
        circleImageView.setImageResource(R.mipmap.icon_mines);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventManage.sendEvent(new BaseEvent<>(EventConstant.MainDrawerLayoutOpenCode, new MainDrawerLayoutOpenEvent(Constant.MAIN_HOME)));
            }
        });
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(UIUtils.dp2px(20), UIUtils.dp2px(20));
        lps.topMargin = UIUtils.dp2px(15);
        lps.rightMargin = UIUtils.dp2px(8);
        mTopBar.addLeftView(circleImageView, R.id.qmui_topbar_item_left_back, lps);
        mTopBar.addRightImageButton(R.drawable.icon_ordercenter, R.id.qmui_topbar_item_right, 22, 22).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!OtcDialogUtils.isDialogShow(getContext())) {
                    jumpTo(OtcOrderListActivity.class);
                }
            }
        });
    }
}
