package com.android.tacu.module.vip.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.common.MyFragmentPagerAdapter;
import com.android.tacu.module.vip.contract.BuyVipContract;
import com.android.tacu.module.vip.presenter.BuyVipPresenter;

import butterknife.BindView;
import butterknife.OnClick;

public class BuyVipActivity extends BaseActivity<BuyVipPresenter> implements BuyVipContract.IView {

    @BindView(R.id.tv_vip_month)
    TextView tv_vip_month;
    @BindView(R.id.tag_month)
    TextView tag_month;
    @BindView(R.id.view_vip_month)
    View view_vip_month;
    @BindView(R.id.tv_vip_year)
    TextView tv_vip_year;
    @BindView(R.id.tag_year)
    TextView tag_year;
    @BindView(R.id.view_vip_year)
    View view_vip_year;
    @BindView(R.id.tv_vip_year_continue)
    TextView tv_vip_year_continue;
    @BindView(R.id.tag_year_continue)
    TextView tag_year_continue;
    @BindView(R.id.view_vip_year_continue)
    View view_vip_year_continue;
    @BindView(R.id.vp)
    ViewPager viewPager;

    private Fragment[] fragments;
    private BuyVipFragment monthFragment;
    private BuyVipFragment yearFragment;
    private BuyVipFragment yearContinueFragment;

    //true=当前页面是支付页面
    private boolean isPay = false;

    public static Intent createActivity(Context context, boolean isPay) {
        Intent intent = new Intent(context, BuyVipActivity.class);
        intent.putExtra("isPay", isPay);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_buy_vip);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.buy_vip));
        isPay = getIntent().getBooleanExtra("isPay", false);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                setTabChange(position);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        initFragment();
        setCurrentValue(0);
    }

    @Override
    protected BuyVipPresenter createPresenter(BuyVipPresenter mPresenter) {
        return new BuyVipPresenter();
    }

    @OnClick(R.id.tv_vip_month)
    void monthClick() {
        setCurrentValue(0);
    }

    @OnClick(R.id.tv_vip_year)
    void yearClick() {
        setCurrentValue(1);
    }

    @OnClick(R.id.tv_vip_year_continue)
    void yearContinueClick() {
        setCurrentValue(2);
    }

    private void initFragment() {
        monthFragment = BuyVipFragment.newInstance(1, isPay);
        yearFragment = BuyVipFragment.newInstance(2, isPay);
        yearContinueFragment = BuyVipFragment.newInstance(3, isPay);

        fragments = new Fragment[]{monthFragment, yearFragment, yearContinueFragment};
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments));
        viewPager.setOffscreenPageLimit(fragments.length - 1);
    }

    /**
     * 0=包月 1=包年 2=连续包年
     *
     * @param current
     */
    private void setCurrentValue(int current) {
        setTabChange(current);
        viewPager.setCurrentItem(current, true);
    }

    private void setTabChange(int current) {
        clearStatus();
        switch (current) {
            case 0:
                tv_vip_month.setTextColor(ContextCompat.getColor(this, R.color.text_default));
                view_vip_month.setBackgroundColor(ContextCompat.getColor(this, R.color.color_default));
                break;
            case 1:
                tv_vip_year.setTextColor(ContextCompat.getColor(this, R.color.text_default));
                view_vip_year.setBackgroundColor(ContextCompat.getColor(this, R.color.color_default));
                break;
            case 2:
                tv_vip_year_continue.setTextColor(ContextCompat.getColor(this, R.color.text_default));
                view_vip_year_continue.setBackgroundColor(ContextCompat.getColor(this, R.color.color_default));
                break;
        }
    }

    private void clearStatus() {
        tv_vip_month.setTextColor(ContextCompat.getColor(this, R.color.text_color));
        view_vip_month.setBackgroundColor(ContextCompat.getColor(this, R.color.color_transparent));
        tv_vip_year.setTextColor(ContextCompat.getColor(this, R.color.text_color));
        view_vip_year.setBackgroundColor(ContextCompat.getColor(this, R.color.color_transparent));
        tv_vip_year_continue.setTextColor(ContextCompat.getColor(this, R.color.text_color));
        view_vip_year_continue.setBackgroundColor(ContextCompat.getColor(this, R.color.color_transparent));
    }
}
