package com.android.tacu.module.otc.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.MainDrawerLayoutOpenEvent;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.otc.contract.OtcHomeContract;
import com.android.tacu.module.otc.presenter.OtcHomePresenter;
import com.android.tacu.utils.UIUtils;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.stx.xhb.xbanner.XBanner;
import com.stx.xhb.xbanner.transformers.Transformer;

import butterknife.BindView;
import butterknife.OnClick;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class OtcHomeFragment extends BaseFragment<OtcHomePresenter> implements OtcHomeContract.IView {

    @BindView(R.id.title)
    QMUITopBar mTopBar;
    @BindView(R.id.banner_home)
    XBanner banner_home;
    @BindView(R.id.tv_acu)
    TextView tv_acu;
    @BindView(R.id.view_acu)
    View view_acu;
    @BindView(R.id.tv_usdt)
    TextView tv_usdt;
    @BindView(R.id.view_usdt)
    View view_usdt;
    @BindView(R.id.tv_btc)
    TextView tv_btc;
    @BindView(R.id.view_btc)
    View view_btc;
    @BindView(R.id.tv_c2c)
    TextView tv_c2c;
    @BindView(R.id.img_c2c)
    ImageView img_c2c;
    @BindView(R.id.vp)
    ViewPager viewpager;

    private Fragment[] fragments;
    private OtcHomeChildFragment acuFragment;
    private OtcHomeChildFragment usdtFragment;
    private OtcHomeChildFragment btcFragment;
    private OtcHomeC2cFragment c2cFragment;

    public static OtcHomeFragment newInstance() {
        Bundle bundle = new Bundle();
        OtcHomeFragment fragment = new OtcHomeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_otc_home;
    }

    @Override
    protected void initData() {
        initTitle();

        banner_home.setPageTransformer(Transformer.Default);

        initFragments();
        setCurrentValue(0);
    }

    @Override
    protected OtcHomePresenter createPresenter(OtcHomePresenter mPresenter) {
        return new OtcHomePresenter();
    }

    @OnClick(R.id.rl_acu)
    void rlAcuClick() {
        setCurrentValue(0);
    }

    @OnClick(R.id.rl_usdt)
    void rlUsdtClick() {
        setCurrentValue(1);
    }

    @OnClick(R.id.rl_btc)
    void rlBtcClick() {
        setCurrentValue(2);
    }

    @OnClick(R.id.rl_c2c)
    void rlC2cClick() {
        setCurrentValue(3);
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
    }

    private void initFragments() {
        acuFragment = OtcHomeChildFragment.newInstance(0, "ACU");
        usdtFragment = OtcHomeChildFragment.newInstance(0, "USDT");
        btcFragment = OtcHomeChildFragment.newInstance(0, "BTC");
        c2cFragment = OtcHomeC2cFragment.newInstance();

        fragments = new Fragment[]{acuFragment, usdtFragment, btcFragment, c2cFragment};
        viewpager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), fragments));
        viewpager.setOffscreenPageLimit(3);
    }

    /**
     * @param current 0=acu 1=usdt 2=btc 3=c2c
     */
    private void setCurrentValue(int current) {
        clearStatus();
        switch (current) {
            case 0:
                tv_acu.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                view_acu.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_default));
                break;
            case 1:
                tv_usdt.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                view_usdt.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_default));
                break;
            case 2:
                tv_btc.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                view_btc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_default));
                break;
            case 3:
                tv_c2c.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                img_c2c.setImageResource(R.drawable.icon_arrow_right_default);
                break;
        }
        viewpager.setCurrentItem(current, false);
    }

    private void clearStatus() {
        tv_acu.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
        view_acu.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_transparent));
        tv_usdt.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
        view_usdt.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_transparent));
        tv_btc.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
        view_btc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_transparent));
        tv_c2c.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
        img_c2c.setImageResource(R.drawable.icon_arrow_right);
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private Fragment[] list;

        public MyFragmentPagerAdapter(FragmentManager fm, Fragment[] list) {
            super(fm);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            return list[position];
        }

        @Override
        public int getCount() {
            return list.length;
        }
    }
}
