package com.android.tacu.module.auth.view;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.common.TabAdapter;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.auth.contract.AuthMerchantContract;
import com.android.tacu.module.auth.model.OtcSectionModel;
import com.android.tacu.module.auth.model.SelectC2cSection;
import com.android.tacu.module.auth.presenter.AuthMerchantPresenter;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.utils.user.UserManageUtils;
import com.android.tacu.widget.popupwindow.OtcPopWindow;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.TextWidthColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class AuthMerchantActivity extends BaseActivity<AuthMerchantPresenter> implements AuthMerchantContract.IView, View.OnClickListener {

    @BindView(R.id.magic_indicator)
    ScrollIndicatorView magic_indicator;
    @BindView(R.id.vp)
    ViewPager viewPager;

    private QMUIAlphaButton btn_left;
    private QMUIAlphaButton btn_right;
    private TextView tv_text;

    private List<String> tabTitle = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();
    private OrdinarMerchantFragment ordinarMerchantFragment;
    private AuthMerchantFragment authMerchantFragment;

    private String leftString;
    private String rightString;
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

        ordinarMerchantFragment = OrdinarMerchantFragment.newInstance();
        authMerchantFragment = AuthMerchantFragment.newInstance();

        fragmentList.add(ordinarMerchantFragment);
        fragmentList.add(authMerchantFragment);

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
    protected AuthMerchantPresenter createPresenter(AuthMerchantPresenter mPresenter) {
        return new AuthMerchantPresenter();
    }

    @Override
    protected void onPresenterCreated(AuthMerchantPresenter presenter) {
        super.onPresenterCreated(presenter);
        mPresenter.selectOtcSection();
        mPresenter.selectC2cSection();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                btn_left.setTextColor(ContextCompat.getColor(this, R.color.text_default));
                btn_right.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                if (!TextUtils.isEmpty(leftString)) {
                    tv_text.setText(leftString);
                }
                break;
            case R.id.btn_right:
                btn_left.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                btn_right.setTextColor(ContextCompat.getColor(this, R.color.text_default));
                if (!TextUtils.isEmpty(rightString)) {
                    tv_text.setText(rightString);
                }
                break;
        }
    }

    @Override
    public void ownCenter(OwnCenterModel model) {
        UserManageUtils.setPersonInfo(model, null);
        if (ordinarMerchantFragment != null) {
            ordinarMerchantFragment.setValue(model);
        }
        if (authMerchantFragment != null) {
            authMerchantFragment.setValue(model);
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
            tv_text.setText(leftString);
        }
    }

    @Override
    public void selectC2cSection(List<SelectC2cSection> list) {
        if (list != null && list.size() > 0) {
            SelectC2cSection model;
            String value1 = null, value2 = null, value3 = null, value4 = null, value5 = null, value6 = null, value7 = null;
            String value8 = null, value9 = null, value10 = null, value11 = null, value12 = null, value13 = null, value14 = null;
            String value15 = null, value16 = null, value17 = null, value18 = null, value19 = null, value20 = null, value21 = null;
            String value22 = null, value23 = null, value24 = null, value25 = null, value26 = null, value27 = null, value28 = null;
            for (int i = 0; i < list.size(); i++) {
                model = list.get(i);
                if (model.id != null) {
                    switch (model.id) {
                        case 1:
                            value1 = model.startNumSection + "-" + model.endNumSection;
                            value2 = model.endNumSection;
                            value3 = model.price;
                            break;
                        case 2:
                            value4 = model.startNumSection + "-" + model.endNumSection;
                            value5 = model.endNumSection;
                            value6 = model.price;
                            break;
                        case 3:
                            value7 = model.startNumSection + "-" + model.endNumSection;
                            value8 = model.endNumSection;
                            value9 = model.price;
                            break;
                        case 4:
                            value10 = model.startNumSection + "-" + model.endNumSection;
                            value11 = model.endNumSection;
                            value12 = model.price;
                            break;
                        case 5:
                            value13 = model.startNumSection;
                            value14 = model.price;
                            break;
                        case 6:
                            value15 = model.startNumSection + "-" + model.endNumSection;
                            value16 = model.endNumSection;
                            value17 = model.price;
                            break;
                        case 7:
                            value18 = model.startNumSection + "-" + model.endNumSection;
                            value19 = model.endNumSection;
                            value20 = model.price;
                            break;
                        case 8:
                            value21 = model.startNumSection + "-" + model.endNumSection;
                            value22 = model.endNumSection;
                            value23 = model.price;
                            break;
                        case 9:
                            value24 = model.startNumSection + "-" + model.endNumSection;
                            value25 = model.endNumSection;
                            value26 = model.price;
                            break;
                        case 10:
                            value27 = model.startNumSection;
                            value28 = model.price;
                            break;
                    }
                }
            }
            rightString = String.format(getResources().getString(R.string.merchant_c2c_advantage_tip), value1, value2, value3, value4, value5, value6, value7, value8, value9, value10, value11, value12, value13, value14, value15, value16, value17, value18, value19, value20, value21, value22, value23, value24, value25, value26, value27, value28);
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
