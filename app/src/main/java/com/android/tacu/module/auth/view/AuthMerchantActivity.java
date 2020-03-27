package com.android.tacu.module.auth.view;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.common.MyFragmentPagerAdapter;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.auth.contract.AuthMerchantContract;
import com.android.tacu.module.auth.model.OtcSectionModel;
import com.android.tacu.module.auth.presenter.AuthMerchantPresenter;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.utils.user.UserManageUtils;
import com.android.tacu.widget.popupwindow.OtcPopWindow;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class AuthMerchantActivity extends BaseActivity<AuthMerchantPresenter> implements AuthMerchantContract.IView, View.OnClickListener {

    @BindView(R.id.vp)
    ViewPager viewPager;

    @BindView(R.id.img_ordinary_merchant)
    ImageView img_ordinary_merchant;
    @BindView(R.id.tv_ordinary_merchant)
    TextView tv_ordinary_merchant;
    @BindView(R.id.view_ordinary_merchant)
    View view_ordinary_merchant;

    @BindView(R.id.img_certified_shoper)
    ImageView img_certified_shoper;
    @BindView(R.id.tv_certified_shoper)
    TextView tv_certified_shoper;
    @BindView(R.id.view_certified_shoper)
    View view_certified_shoper;

    private QMUIAlphaButton btn_left;
    private TextView tv_text;

    private List<Fragment> fragmentList = new ArrayList<>();
    private OrdinarMerchantFragment ordinarMerchantFragment;
    private AuthMerchantFragment authMerchantFragment;

    private String leftString;
    private OtcPopWindow popWindow;

    private int type = 0;// 0=普通商户 1= 认证商户

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

        ordinarMerchantFragment = OrdinarMerchantFragment.newInstance();
        authMerchantFragment = AuthMerchantFragment.newInstance();

        fragmentList.add(ordinarMerchantFragment);
        fragmentList.add(authMerchantFragment);

        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        viewPager.setOffscreenPageLimit(fragmentList.size() - 1);
        viewPager.setCurrentItem(0, false);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                type = i;
                setShowBtn();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        setShowBtn();
    }

    @Override
    protected AuthMerchantPresenter createPresenter(AuthMerchantPresenter mPresenter) {
        return new AuthMerchantPresenter();
    }

    @Override
    protected void onPresenterCreated(AuthMerchantPresenter presenter) {
        super.onPresenterCreated(presenter);
        mPresenter.selectOtcSection();
    }

    @Override
    protected void onResume() {
        super.onResume();
        upload();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
        }
    }

    @OnClick(R.id.rl_ordinary_merchant)
    void ordinaryMerchantClick() {
        type = 1;
        setShowBtn();
    }

    @OnClick(R.id.rl_certified_shoper)
    void certifiedClick() {
        type = 2;
        setShowBtn();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                btn_left.setTextColor(ContextCompat.getColor(this, R.color.text_default));
                if (!TextUtils.isEmpty(leftString)) {
                    try {
                        tv_text.setText(leftString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public void ownCenter(OwnCenterModel model) {
        UserManageUtils.setPersonInfo(model);
        if (ordinarMerchantFragment != null) {
            ordinarMerchantFragment.setValue(model);
        }
        if (authMerchantFragment != null) {
            authMerchantFragment.setValue(model);
        }
        if (spUtil.getApplyMerchantStatus() == 2) {
            img_ordinary_merchant.setVisibility(View.VISIBLE);
        } else {
            img_ordinary_merchant.setVisibility(View.GONE);
        }
        if (spUtil.getApplyAuthMerchantStatus() == 2) {
            img_certified_shoper.setVisibility(View.VISIBLE);
        } else {
            img_certified_shoper.setVisibility(View.GONE);
        }
    }

    @Override
    public void BondAccount(OtcAmountModel model) {
        if (ordinarMerchantFragment != null) {
            ordinarMerchantFragment.setBondAccount(model);
        }
        if (authMerchantFragment != null) {
            authMerchantFragment.setBondAccount(model);
        }
    }

    @Override
    public void selectOtcSection(List<OtcSectionModel> list) {
        if (list != null && list.size() > 0) {
            OtcSectionModel model;
            String value1 = null, value2 = null, value3 = null, value4 = null, value5 = null, value6 = null, value7 = null, value8 = null;
            for (int i = 0; i < list.size(); i++) {
                model = list.get(i);
                if (model.id != null) {
                    if (model.id == 1) {
                        value1 = model.sellStartPriceSection + "~" + model.sellEndPriceSection;
                        value2 = model.buyStartPriceSection + "~" + model.buyEndPriceSection;
                        value3 = model.startTradeSection + "~" + model.endTradeSection;
                        value4 = model.otcUserTypeEndTimeDay;
                    } else if (model.id == 2) {
                        value5 = model.buyStartPriceSection + "~" + model.buyEndPriceSection;
                        value6 = model.startTradeSection + "~" + model.endTradeSection;
                    } else if (model.id == 3) {
                        value7 = model.buyStartPriceSection + "~" + model.buyEndPriceSection;
                        value8 = model.startTradeSection + "~" + model.endTradeSection;
                    }
                }
            }
            leftString = String.format(getResources().getString(R.string.merchant_otc_advantage_tip), value1, value2, value3, value4, value5, value6, value7, value8);
        }
    }

    private void setShowBtn() {
        tv_ordinary_merchant.setTextColor(ContextCompat.getColor(this, R.color.text_color));
        tv_certified_shoper.setTextColor(ContextCompat.getColor(this, R.color.text_color));
        view_ordinary_merchant.setVisibility(View.GONE);
        view_certified_shoper.setVisibility(View.GONE);

        if (type == 0) {
            tv_ordinary_merchant.setTextColor(ContextCompat.getColor(this, R.color.text_default));
            view_ordinary_merchant.setVisibility(View.VISIBLE);
            viewPager.setCurrentItem(0);
        } else if (type == 1) {
            tv_certified_shoper.setTextColor(ContextCompat.getColor(this, R.color.text_default));
            view_certified_shoper.setVisibility(View.VISIBLE);
            viewPager.setCurrentItem(1);
        }
    }

    private void upload() {
        mPresenter.ownCenter();
        mPresenter.BondAccount(Constant.ACU_CURRENCY_ID);
    }

    private void initPop() {
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
            return;
        }

        if (popWindow == null) {
            View view = View.inflate(this, R.layout.pop_merchant_advantage, null);
            btn_left = view.findViewById(R.id.btn_left);
            tv_text = view.findViewById(R.id.tv_text);
            btn_left.setOnClickListener(this);

            if (!TextUtils.isEmpty(leftString)) {
                tv_text.setText(leftString);
            }

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
