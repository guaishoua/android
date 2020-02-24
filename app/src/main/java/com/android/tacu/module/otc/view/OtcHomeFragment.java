package com.android.tacu.module.otc.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
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
import com.android.tacu.common.MyFragmentPagerAdapter;
import com.android.tacu.module.main.model.HomeModel;
import com.android.tacu.module.otc.contract.OtcHomeContract;
import com.android.tacu.module.otc.dialog.OtcDialogUtils;
import com.android.tacu.module.otc.presenter.OtcHomePresenter;
import com.android.tacu.module.webview.view.WebviewActivity;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.UIUtils;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.stx.xhb.xbanner.XBanner;
import com.stx.xhb.xbanner.entity.CustomViewsInfo;
import com.stx.xhb.xbanner.entity.LocalImageInfo;
import com.stx.xhb.xbanner.transformers.Transformer;

import java.util.ArrayList;
import java.util.List;

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
   /* @BindView(R.id.tv_usdt)
    TextView tv_usdt;
    @BindView(R.id.view_usdt)
    View view_usdt;
    @BindView(R.id.tv_btc)
    TextView tv_btc;
    @BindView(R.id.view_btc)
    View view_btc;*/
    @BindView(R.id.tv_c2c)
    TextView tv_c2c;
    @BindView(R.id.img_c2c)
    ImageView img_c2c;
    @BindView(R.id.vp)
    ViewPager viewpager;

    private Fragment[] fragments;
    private OtcHomeChildFragment acuFragment;
    private OtcHomeC2cFragment c2cFragment;

    private HomeModel homeModel;
    private List<CustomViewsInfo> bannerImageList = new ArrayList<>();
    private List<LocalImageInfo> bannerLocalList = new ArrayList<>();

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
    protected void initData(View view) {
        initTitle();

        banner_home.setPageTransformer(Transformer.Default);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

        initFragments();
        setBannerValue();
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

    /*@OnClick(R.id.rl_usdt)
    void rlUsdtClick() {
        setCurrentValue(1);
    }

    @OnClick(R.id.rl_btc)
    void rlBtcClick() {
        setCurrentValue(2);
    }*/

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
        mTopBar.addRightImageButton(R.drawable.icon_ordercenter, R.id.qmui_topbar_item_right, 22, 22).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!OtcDialogUtils.isDialogShow(getContext())) {
                    jumpTo(OtcOrderListActivity.class);
                }
            }
        });
    }

    private void initFragments() {
        acuFragment = OtcHomeChildFragment.newInstance(Constant.ACU_CURRENCY_ID, Constant.OTC_ACU);
        c2cFragment = OtcHomeC2cFragment.newInstance();

        fragments = new Fragment[]{acuFragment, c2cFragment};
        viewpager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), fragments));
        viewpager.setOffscreenPageLimit(3);
    }

    /**
     * @param current 0=acu 1=usdt 2=btc 3=c2c
     */
    private void setCurrentValue(int current) {
        setTabChange(current);
        viewpager.setCurrentItem(current, true);
    }

    private void setTabChange(int current) {
        clearStatus();
        switch (current) {
            case 0:
                tv_acu.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                view_acu.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_default));
                break;
           /* case 1:
                tv_usdt.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                view_usdt.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_default));
                break;
            case 2:
                tv_btc.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                view_btc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_default));
                break;*/
            case 3:
                tv_c2c.setTextColor(ContextCompat.getColor(getContext(), R.color.text_default));
                img_c2c.setImageResource(R.drawable.icon_arrow_right_default);
                break;
        }
    }

    private void clearStatus() {
        tv_acu.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
        view_acu.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_transparent));
      /*  tv_usdt.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
        view_usdt.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_transparent));
        tv_btc.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
        view_btc.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_transparent));*/
        tv_c2c.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
        img_c2c.setImageResource(R.drawable.icon_arrow_right);
    }

    public void setHome(HomeModel model, boolean isCache) {
        this.homeModel = model;
        if (!isCache) {
            setBannerValue();
        }
    }

    private void setBannerValue() {
        if (homeModel != null && homeModel.banner != null && homeModel.banner.size() > 0) {
            banner_home.setOnItemClickListener(new XBanner.OnItemClickListener() {
                @Override
                public void onItemClick(XBanner banner, Object model, View view, int position) {
                    CustomViewsInfo customViewsInfo = (CustomViewsInfo) model;
                    if (!TextUtils.isEmpty(customViewsInfo.getXBannerUrl())) {
                        jumpTo(WebviewActivity.createActivity(getContext(), customViewsInfo.getXBannerUrl()));
                    }
                }
            });
            banner_home.loadImage(new XBanner.XBannerAdapter() {
                @Override
                public void loadBanner(XBanner banner, Object model, View view, int position) {
                    ImageView imageView = (ImageView) view;
                    imageView.setScaleType(CENTER_CROP);
                    CustomViewsInfo customViewsInfo = (CustomViewsInfo) model;
                    GlideUtils.disPlay(getContext(), customViewsInfo.getXBannerImage(), imageView);
                }
            });
            bannerImageList.clear();
            for (int i = 0; i < homeModel.banner.size(); i++) {
                bannerImageList.add(new CustomViewsInfo(homeModel.banner.get(i).image, homeModel.banner.get(i).url));
            }
            banner_home.setBannerData(bannerImageList);
        } else {
            banner_home.loadImage(new XBanner.XBannerAdapter() {
                @Override
                public void loadBanner(XBanner banner, Object model, View view, int position) {
                    ImageView imageView = (ImageView) view;
                    imageView.setScaleType(CENTER_CROP);
                    LocalImageInfo localImageInfo = (LocalImageInfo) model;
                    GlideUtils.disPlay(getContext(), localImageInfo.getXBannerUrl(), imageView);
                }
            });
            bannerLocalList.clear();
            bannerLocalList.add(new LocalImageInfo(R.mipmap.img_banner));
            bannerLocalList.add(new LocalImageInfo(R.mipmap.img_banner));
            bannerLocalList.add(new LocalImageInfo(R.mipmap.img_banner));
            banner_home.setBannerData(bannerLocalList);
        }
    }
}
