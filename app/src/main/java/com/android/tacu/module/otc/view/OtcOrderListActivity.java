package com.android.tacu.module.otc.view;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.common.MyFragmentPagerAdapter;
import com.android.tacu.module.webview.view.WebviewFragment;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class OtcOrderListActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private View centerView;
    private QMUIRoundButton btn_c2c;
    private QMUIRoundButton btn_otc;

    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_order_list);
    }

    @Override
    protected void initView() {
        initTitle();

        if (isMerchant()) {
            mTopBar.setCenterView(centerView);
            fragmentList.add(WebviewFragment.newInstance(Constant.C2C_ORDER_LIST_URL));
            fragmentList.add(OtcOrderListFragment.newInstance());
        } else {
            mTopBar.setTitle(getResources().getString(R.string.order_center));
            fragmentList.add(OtcOrderListFragment.newInstance());
        }

        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        viewPager.setOffscreenPageLimit(fragmentList.size() - 1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                setTabPosition(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        setTabSelection(0);
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

    private void initTitle() {
        centerView = View.inflate(this, R.layout.view_otchome_title, null);
        btn_c2c = centerView.findViewById(R.id.btn_c2c);
        btn_otc = centerView.findViewById(R.id.btn_otc);
        btn_c2c.setText("C2C " + getResources().getString(R.string.order));
        btn_otc.setText("OTC " + getResources().getString(R.string.order));
        setCenterStyle(btn_c2c, true);
        setCenterStyle(btn_otc, false);
        btn_c2c.setOnClickListener(this);
        btn_otc.setOnClickListener(this);
    }

    private void setTabPosition(int pos) {
        switch (pos) {
            case 0:
                setCenterStyle(btn_c2c, true);
                setCenterStyle(btn_otc, false);
                break;
            case 1:
                setCenterStyle(btn_c2c, false);
                setCenterStyle(btn_otc, true);
                break;
        }
    }

    private void setTabSelection(int pos) {
        setTabPosition(pos);
        viewPager.setCurrentItem(pos);
    }

    private void setCenterStyle(QMUIRoundButton btn, boolean isFouce) {
        if (isFouce) {
            btn.setTextColor(ContextCompat.getColor(this, R.color.content_bg_color));
            ((QMUIRoundButtonDrawable) btn.getBackground()).setBgData(ContextCompat.getColorStateList(this, R.color.text_default));
        } else {
            btn.setTextColor(ContextCompat.getColor(this, R.color.text_default));
            ((QMUIRoundButtonDrawable) btn.getBackground()).setBgData(ContextCompat.getColorStateList(this, R.color.content_bg_color));
        }
    }

    private boolean isMerchant() {
        return spUtil.getApplyMerchantStatus() == 2 && spUtil.getApplyAuthMerchantStatus() != 2;
    }
}
