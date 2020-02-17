package com.android.tacu.module.auth.view;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.common.TabAdapter;
import com.android.tacu.widget.popupwindow.MerchantPopWindow;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.TextWidthColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class AuthMerchantActivity extends BaseActivity {

    @BindView(R.id.magic_indicator)
    ScrollIndicatorView magic_indicator;
    @BindView(R.id.vp)
    ViewPager viewPager;

    private List<String> tabTitle = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();

    private MerchantPopWindow popWindow;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_auth_merchant);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.certified_shoper));
        TextView rightText = new TextView(this);
        rightText.setText(getResources().getString(R.string.merchant_advantage));
        rightText.setTextColor(ContextCompat.getColor(this, R.color.text_default));
        rightText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        rightText.setLayoutParams(mTopBar.generateTopBarRightTextButtonLayoutParams());
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPop();
            }
        });
        mTopBar.addRightView(rightText, R.id.qmui_topbar_item_right);

        tabTitle.add(getResources().getString(R.string.ordinary_merchant));
        tabTitle.add(getResources().getString(R.string.certified_shoper));

        fragmentList.add(OrdinarMerchantFragment.newInstance());
        fragmentList.add(AuthMerchantFragment.newInstance());

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
    protected void onDestroy() {
        super.onDestroy();
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
        }
    }

    private void initPop() {
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
            return;
        }

        if (popWindow == null) {
            popWindow = new MerchantPopWindow(this);
            popWindow.create();
            popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    setBg(1F);
                }
            });
        }
        setBg(0.3F);
        popWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    private void setBg(float value) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = value;
        getWindow().setAttributes(lp);
    }
}
