package com.android.tacu.module.vip.view;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.VipBuyEvent;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.common.MyFragmentPagerAdapter;
import com.android.tacu.module.vip.contract.BuyVipContract;
import com.android.tacu.module.vip.model.VipDetailModel;
import com.android.tacu.module.vip.model.VipDetailRankModel;
import com.android.tacu.module.vip.presenter.BuyVipPresenter;

import java.util.List;

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

    @Override
    protected void setView() {
        setContentView(R.layout.activity_buy_vip);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.buy_vip));

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

    @Override
    protected void onPresenterCreated(BuyVipPresenter presenter) {
        super.onPresenterCreated(presenter);
        upload();
    }

    @Override
    protected void receiveEvent(BaseEvent event) {
        super.receiveEvent(event);
        if (event != null) {
            switch (event.getCode()) {
                case EventConstant.VipBuyCode:
                    VipBuyEvent vipBuyEvent = (VipBuyEvent) event.getData();
                    if (vipBuyEvent.isNotify()) {
                        upload();
                    }
                    break;
            }
        }
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

    @Override
    public void selectVip(VipDetailModel model) {
        if (monthFragment != null) {
            monthFragment.setVipDetailModel(model);
        }
        if (yearFragment != null) {
            yearFragment.setVipDetailModel(model);
        }
        if (yearContinueFragment != null) {
            yearContinueFragment.setVipDetailModel(model);
        }
    }

    @Override
    public void selectVipDetail(List<VipDetailRankModel> list) {
        VipDetailRankModel model = null;
        for (int i = 0; i < list.size(); i++) {
            model = list.get(i);
            if (list.get(i).id == 1) {
                if (monthFragment != null && list.get(0) != null) {
                    if (!TextUtils.equals(model.amount, model.currentAmount)) {
                        tag_month.setVisibility(View.VISIBLE);
                    } else {
                        tag_month.setVisibility(View.GONE);
                    }
                    monthFragment.setVipDetailRankModel(model);
                }
            } else if (list.get(i).id == 2) {
                if (!TextUtils.equals(model.amount, model.currentAmount)) {
                    tag_year.setVisibility(View.VISIBLE);
                } else {
                    tag_year.setVisibility(View.GONE);
                }
                yearFragment.setVipDetailRankModel(model);
            } else if (list.get(i).id == 3) {
                if (!TextUtils.equals(model.amount, model.currentAmount)) {
                    tag_year_continue.setVisibility(View.VISIBLE);
                } else {
                    tag_year_continue.setVisibility(View.GONE);
                }
                yearContinueFragment.setVipDetailRankModel(model);
            }
        }
    }


    @Override
    public void customerCoinByOneCoin(Double value) {
        if (monthFragment != null) {
            monthFragment.customerCoinByOneCoin(value);
        }
        if (yearFragment != null) {
            yearFragment.customerCoinByOneCoin(value);
        }
        if (yearContinueFragment != null) {
            yearContinueFragment.customerCoinByOneCoin(value);
        }
    }

    private void initFragment() {
        monthFragment = BuyVipFragment.newInstance(1);
        yearFragment = BuyVipFragment.newInstance(2);
        yearContinueFragment = BuyVipFragment.newInstance(3);

        fragments = new Fragment[]{monthFragment, yearFragment, yearContinueFragment};
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments));
        viewPager.setOffscreenPageLimit(fragments.length - 1);
    }

    private void upload() {
        mPresenter.selectVip();
        mPresenter.selectVipDetail();
        mPresenter.customerCoinByOneCoin(Constant.ACU_CURRENCY_ID);
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
