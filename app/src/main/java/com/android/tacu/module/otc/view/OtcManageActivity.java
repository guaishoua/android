package com.android.tacu.module.otc.view;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.common.TabAdapter;
import com.android.tacu.module.auth.view.AuthMerchantActivity;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.otc.contract.OtcManageContract;
import com.android.tacu.module.otc.presenter.OtcManagePresenter;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.utils.user.UserManageUtils;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.TextWidthColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class OtcManageActivity extends BaseActivity<OtcManagePresenter> implements OtcManageContract.IView {

    @BindView(R.id.magic_indicator)
    ScrollIndicatorView titleIndicatorView;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.btn_on)
    QMUIRoundButton btn_on;

    private String[] tabTitle;
    private List<Fragment> fragmentList = new ArrayList<>();
    private IndicatorViewPager indicatorViewPager;

    private OwnCenterModel ownCenterModel;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_manage);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.otc_manage));

        tabTitle = new String[]{getResources().getString(R.string.all), getResources().getString(R.string.buy_order), getResources().getString(R.string.sell_order)};

        fragmentList.add(OtcManageFragment.newInstance(0));
        fragmentList.add(OtcManageFragment.newInstance(1));
        fragmentList.add(OtcManageFragment.newInstance(2));

        titleIndicatorView.setBackgroundColor(ContextCompat.getColor(this, R.color.content_bg_color_grey));
        titleIndicatorView.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(this, R.color.tab_default), ContextCompat.getColor(this, R.color.tab_text_color)).setSize(14, 14));
        titleIndicatorView.setScrollBar(new TextWidthColorBar(this, titleIndicatorView, ContextCompat.getColor(this, R.color.color_transparent), 4));
        titleIndicatorView.setSplitAuto(true);

        viewpager.setOffscreenPageLimit(tabTitle.length - 1);
        indicatorViewPager = new IndicatorViewPager(titleIndicatorView, viewpager);
        indicatorViewPager.setAdapter(new TabAdapter(getSupportFragmentManager(), this, tabTitle, fragmentList));
    }

    @Override
    protected OtcManagePresenter createPresenter(OtcManagePresenter mPresenter) {
        return new OtcManagePresenter();
    }

    @Override
    protected void onPresenterCreated(OtcManagePresenter presenter) {
        super.onPresenterCreated(presenter);
        mPresenter.ownCenter();
    }

    @OnClick(R.id.btn_on)
    void btnOnClick() {
        //0=下线 1=上线
        if (ownCenterModel != null) {
            if (ownCenterModel.merchantStatus != null && ownCenterModel.merchantStatus == 0) {
                mPresenter.merchanton();
            } else if (ownCenterModel.merchantStatus != null && ownCenterModel.merchantStatus == 1) {
                mPresenter.merchantoff();
            }
        }
    }

    @OnClick(R.id.lin_guanggao)
    void publishClick() {
        if (spUtil.getApplyMerchantStatus() != 2 && spUtil.getApplyAuthMerchantStatus() != 2) {
            showToastError(getResources().getString(R.string.you_art_shoper_first));
            return;
        }
        jumpTo(OtcPublishActivity.class);
    }

    @OnClick(R.id.lin_auth)
    void certifiedShoperClick() {
        jumpTo(AuthMerchantActivity.class);
    }

    @Override
    public void ownCenter(OwnCenterModel model) {
        this.ownCenterModel = model;
        UserManageUtils.setPersonInfo(model);
        showMerchantStatus();
    }

    @Override
    public void merchantoff() {
        mPresenter.ownCenter();
    }

    @Override
    public void merchanton() {
        mPresenter.ownCenter();
    }

    //0=下线 1=上线
    private void showMerchantStatus() {
        if (ownCenterModel != null) {
            if (ownCenterModel.merchantStatus != null && ownCenterModel.merchantStatus == 0) {
                ((QMUIRoundButtonDrawable) btn_on.getBackground()).setStrokeData(UIUtils.dp2px(1), ContextCompat.getColorStateList(this, R.color.text_grey));
                btn_on.setTextColor(ContextCompat.getColor(this, R.color.text_grey));
                btn_on.setText(getResources().getString(R.string.offline));
            } else if (ownCenterModel.merchantStatus != null && ownCenterModel.merchantStatus == 1) {
                ((QMUIRoundButtonDrawable) btn_on.getBackground()).setStrokeData(UIUtils.dp2px(1), ContextCompat.getColorStateList(this, R.color.text_default));
                btn_on.setTextColor(ContextCompat.getColor(this, R.color.text_default));
                btn_on.setText(getResources().getString(R.string.online));
            }
        }
    }
}
