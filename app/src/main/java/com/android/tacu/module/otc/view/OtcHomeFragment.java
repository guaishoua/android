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
import com.android.tacu.common.MyFragmentPagerAdapter;
import com.android.tacu.module.otc.dialog.OtcDialogUtils;
import com.android.tacu.module.webview.view.WebviewFragment;
import com.android.tacu.utils.UIUtils;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundLinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class OtcHomeFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.title)
    QMUITopBar mTopBar;
    @BindView(R.id.vp)
    ViewPager viewPager;
    @BindView(R.id.lin_guanggao)
    QMUIRoundLinearLayout lin_guanggao;

    private View centerView;
    private QMUIRoundButton btn_c2c;
    private QMUIRoundButton btn_otc;

    private WebviewFragment c2cFragment;
    private OtcMarketFragment otcMarketFragment;
    private List<Fragment> fragmentList = new ArrayList<>();
    private MyFragmentPagerAdapter pagerAdapter;

    public static OtcHomeFragment newInstance() {
        Bundle bundle = new Bundle();
        OtcHomeFragment fragment = new OtcHomeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initLazy() {
        super.initLazy();
        setShow();
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
        return R.layout.fragment_otc_home;
    }

    @Override
    protected void initData(View view) {
        initTitle();

        c2cFragment = WebviewFragment.newInstance(Constant.C2C_URL);
        otcMarketFragment = OtcMarketFragment.newInstance();

        if (isMerchant()) {
            fragmentList.add(c2cFragment);
            fragmentList.add(otcMarketFragment);
        } else {
            fragmentList.add(otcMarketFragment);
        }

        pagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentList);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(fragmentList.size() - 1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                setTabSelection(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        setTabSelection(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        setShow();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_c2c:
                setTabSelection(0);
                break;
            case R.id.btn_otc:
                setTabSelection(1);
                break;
        }
    }

    @OnClick(R.id.lin_guanggao)
    void guanggaoClick() {
        if (!OtcDialogUtils.isDialogShow(getContext())) {
            jumpTo(OtcManageActivity.class);
        }
    }

    private void initTitle() {
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
        mTopBar.addRightImageButton(R.drawable.icon_ordercenter, R.id.qmui_topbar_item_right, 17, 20).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!OtcDialogUtils.isDialogShow(getContext())) {
                    jumpTo(OtcOrderListActivity.class);
                }
            }
        });

        centerView = View.inflate(getContext(), R.layout.view_otchome_title, null);
        btn_c2c = centerView.findViewById(R.id.btn_c2c);
        btn_otc = centerView.findViewById(R.id.btn_otc);
        setCenterStyle(btn_c2c, true);
        setCenterStyle(btn_otc, false);
        btn_c2c.setOnClickListener(this);
        btn_otc.setOnClickListener(this);
    }

    private void setTabSelection(int pos) {
        switch (pos) {
            case 0:
                setCenterStyle(btn_c2c, true);
                setCenterStyle(btn_otc, false);
                break;
            case 1:
                setCenterStyle(btn_c2c, false);
                setCenterStyle(btn_otc, true);

                if (spUtil.getLogin()) {
                    boolean isShowXieyi = spUtil.getDisclaimer() == 0 ? true : false;
                    if (isShowXieyi) {
                        OtcDialogUtils.setShowOtcXieyi(getContext());
                    }
                }
                break;
        }
        viewPager.setCurrentItem(pos);
    }

    private void setCenterStyle(QMUIRoundButton btn, boolean isFouce) {
        if (isFouce) {
            btn.setTextColor(ContextCompat.getColor(getContext(), R.color.content_bg_color));
            ((QMUIRoundButtonDrawable) btn.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.text_default));
        } else {
            btn.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
            ((QMUIRoundButtonDrawable) btn.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.content_bg_color));
        }
    }

    private void setShow() {
        if (spUtil.getApplyMerchantStatus() == 2 || spUtil.getApplyAuthMerchantStatus() == 2) {
            lin_guanggao.setVisibility(View.VISIBLE);
        } else {
            lin_guanggao.setVisibility(View.GONE);
        }

        if (isMerchant()) {
            mTopBar.setCenterView(centerView);
            if (!fragmentList.contains(c2cFragment)) {
                fragmentList.add(0, c2cFragment);
                pagerAdapter.setList(fragmentList);
            }
        } else {
            mTopBar.setTitle("OTC");
            if (fragmentList.contains(c2cFragment)) {
                fragmentList.remove(c2cFragment);
                pagerAdapter.setList(fragmentList);
            }
        }
    }

    /**
     * 判断当前用户是否是普通商户
     *
     * @return
     */
    private boolean isMerchant() {
        return spUtil.getLogin() && spUtil.getApplyMerchantStatus() == 2 && spUtil.getApplyAuthMerchantStatus() != 2;
    }
}
