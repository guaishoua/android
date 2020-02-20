package com.android.tacu.module.auth.view;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.common.TabAdapter;
import com.android.tacu.widget.popupwindow.OtcPopWindow;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.TextWidthColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class AuthMerchantActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.magic_indicator)
    ScrollIndicatorView magic_indicator;
    @BindView(R.id.vp)
    ViewPager viewPager;

    private QMUIAlphaButton btn_left;
    private QMUIAlphaButton btn_right;
    private TextView tv_text;

    private List<String> tabTitle = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();

    private OtcPopWindow popWindow;

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                btn_left.setTextColor(ContextCompat.getColor(this, R.color.text_default));
                btn_right.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                tv_text.setText(this.getResources().getString(R.string.merchant_otc_advantage_tip));
                break;
            case R.id.btn_right:
                btn_left.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                btn_right.setTextColor(ContextCompat.getColor(this, R.color.text_default));
                tv_text.setText(this.getResources().getString(R.string.merchant_c2c_advantage_tip));
                break;
        }
    }

    private void initPop() {
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
            return;
        }

        if (popWindow == null) {
            View view = View.inflate(this, R.layout.pop_merchant_advantage, null);
            btn_left = view.findViewById(R.id.btn_left);
            btn_right = view.findViewById(R.id.btn_right);
            tv_text = view.findViewById(R.id.tv_text);
            btn_left.setOnClickListener(this);
            btn_right.setOnClickListener(this);

            popWindow = new OtcPopWindow(this);
            popWindow.create(view);
            popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    popWindow.setBg(1F);
                }
            });
        }
        popWindow.setBg(0.3F);
        popWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }
}
