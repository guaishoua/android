package com.android.tacu.module.auth.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.MainSwitchEvent;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.auth.contract.AuthMerchantContract;
import com.android.tacu.module.auth.presenter.AuthMerchantPresenter;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.main.view.MainActivity;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.widget.dialog.DroidDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;

import butterknife.BindView;
import butterknife.OnClick;

public class AuthMerchantFragment extends BaseFragment<AuthMerchantPresenter> implements AuthMerchantContract.IAuthView {

    @BindView(R.id.img_pass)
    ImageView img_pass;
    @BindView(R.id.tv_pass_error)
    TextView tv_pass_error;
    @BindView(R.id.tv_pass)
    TextView tv_pass;

    @BindView(R.id.img_otc)
    ImageView img_otc;
    @BindView(R.id.btn_otc_right)
    QMUIRoundButton btn_otc_right;
    @BindView(R.id.tv_otc_finish)
    TextView tv_otc_finish;
    @BindView(R.id.tv_otc_error)
    TextView tv_otc_error;

    @BindView(R.id.btn_dropout)
    QMUIRoundButton btn_dropout;
    @BindView(R.id.btn_submit)
    QMUIRoundButton btn_submit;

    private Integer otcTradeNum = null;
    private OtcMarketInfoModel marketInfoModel = null;
    private boolean isMechant = false;
    private boolean isOtc = false;

    public static AuthMerchantFragment newInstance() {
        Bundle bundle = new Bundle();
        AuthMerchantFragment fragment = new AuthMerchantFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_auth_merchant;
    }

    @Override
    protected void initData(View view) {
        dealValue();
    }

    @Override
    protected AuthMerchantPresenter createPresenter(AuthMerchantPresenter mPresenter) {
        return new AuthMerchantPresenter();
    }

    @OnClick(R.id.btn_otc_right)
    void otcClick() {
        jumpTo(MainActivity.createActivity(getContext(), true));
        EventManage.sendEvent(new BaseEvent<>(EventConstant.MainSwitchCode, new MainSwitchEvent(Constant.MAIN_OTC)));
    }

    @OnClick(R.id.btn_dropout)
    void dropOutClick() {
        dropOutDialog();
    }

    @OnClick(R.id.btn_submit)
    void submitClick() {
        if (TextUtils.equals(btn_submit.getText().toString(), getResources().getString(R.string.apply_downgrade))) {
            applyDowngradeDialog();
        } else {
            showAuthMerchant();
        }
    }

    @Override
    public void applyMerchantAuthSuccess() {
        showToastSuccess(getResources().getString(R.string.apply_success));
        getHostActivity().finish();
    }

    @Override
    public void downMerchantAuthSuccess() {
        spUtil.setApplyAuthMerchantStatus(0);
        EventManage.sendEvent(new BaseEvent<>(EventConstant.AuthMerchant, new Object()));
    }

    @Override
    public void quitMerchantSuccess() {
        spUtil.setApplyMerchantStatus(0);
        spUtil.setApplyAuthMerchantStatus(0);
        EventManage.sendEvent(new BaseEvent<>(EventConstant.AuthMerchant, new Object()));
    }

    public void setCountTrade(Integer num) {
        this.otcTradeNum = num;
        dealValue();
    }

    public void setValue(OwnCenterModel model) {
        dealValue();
    }

    public void setUserBaseInfo(OtcMarketInfoModel model) {
        this.marketInfoModel = model;
        dealValue();
    }

    private void dealValue() {
        if (spUtil.getApplyAuthMerchantStatus() != 2) {
            if (spUtil.getApplyMerchantStatus() == 2) {

                if (marketInfoModel != null && !TextUtils.isEmpty(marketInfoModel.timestamp) && !TextUtils.equals(marketInfoModel.timestamp, "0") && !TextUtils.isEmpty(marketInfoModel.applyTime) && !TextUtils.equals(marketInfoModel.applyTime, "0")) {
                    int between = DateUtils.differentDaysByMillisecond(Long.parseLong(marketInfoModel.timestamp), DateUtils.string2Millis(marketInfoModel.applyTime, DateUtils.DEFAULT_PATTERN));
                    int waitTime = 3 - between;
                    if (waitTime >= 3) {
                        isMechant = true;
                        img_pass.setImageResource(R.drawable.icon_auth_success);
                        tv_pass.setText(getResources().getText(R.string.passed));
                        tv_pass_error.setText("");
                    } else {
                        isMechant = false;
                        if (waitTime == 0) {
                            waitTime = 1;
                        }
                        img_pass.setImageResource(R.drawable.icon_auth_failure);
                        tv_pass.setText(getResources().getText(R.string.not_passed));
                        tv_pass_error.setText(String.format(getResources().getString(R.string.wait_day), String.valueOf(waitTime)));
                    }
                } else {
                    isMechant = false;
                    img_pass.setImageResource(R.drawable.icon_auth_failure);
                    tv_pass.setText(getResources().getText(R.string.not_passed));
                    tv_pass_error.setText("");
                }
            } else {
                isMechant = false;
                img_pass.setImageResource(R.drawable.icon_auth_failure);
                tv_pass.setText(getResources().getText(R.string.not_passed));
                tv_pass_error.setText("");
            }

            if (otcTradeNum != null) {
                if (otcTradeNum >= 50) {
                    isOtc = true;
                    img_otc.setImageResource(R.drawable.icon_auth_success);
                    btn_otc_right.setVisibility(View.GONE);
                    tv_otc_finish.setVisibility(View.VISIBLE);
                } else {
                    isOtc = false;
                    img_otc.setImageResource(R.drawable.icon_auth_failure);
                    btn_otc_right.setVisibility(View.VISIBLE);
                    tv_otc_finish.setVisibility(View.GONE);
                    tv_otc_error.setText(String.format(getResources().getString(R.string.cha_bi), String.valueOf(50 - otcTradeNum)));
                }
            } else {
                isOtc = false;
                img_otc.setImageResource(R.drawable.icon_auth_failure);
                btn_otc_right.setVisibility(View.VISIBLE);
                tv_otc_finish.setVisibility(View.GONE);
            }
        } else {
            isMechant = true;
            img_pass.setImageResource(R.drawable.icon_auth_success);
            tv_pass.setText(getResources().getText(R.string.passed));
            tv_pass_error.setText("");

            isOtc = true;
            img_otc.setImageResource(R.drawable.icon_auth_success);
            btn_otc_right.setVisibility(View.GONE);
            tv_otc_finish.setVisibility(View.VISIBLE);
        }

        btn_dropout.setVisibility(View.GONE);
        if (spUtil.getApplyAuthMerchantStatus() == 1 || spUtil.getApplyAuthMerchantStatus() == 2) {
            if (spUtil.getApplyAuthMerchantStatus() == 1) {
                btn_submit.setEnabled(false);
                ((QMUIRoundButtonDrawable) btn_submit.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_otc_unhappy));
                btn_submit.setText(getResources().getString(R.string.to_be_examine));
            } else if (spUtil.getApplyAuthMerchantStatus() == 2) {
                btn_dropout.setVisibility(View.VISIBLE);
                btn_submit.setEnabled(true);
                ((QMUIRoundButtonDrawable) btn_submit.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_default));
                btn_submit.setText(getResources().getString(R.string.apply_downgrade));
            }
        } else {
            if (marketInfoModel != null && marketInfoModel.days != null && !TextUtils.isEmpty(marketInfoModel.timestamp) && !TextUtils.equals(marketInfoModel.timestamp, "0") && !TextUtils.isEmpty(marketInfoModel.downTime) && !TextUtils.equals(marketInfoModel.downTime, "0")) {
                int between = DateUtils.differentDaysByMillisecond(Long.parseLong(marketInfoModel.timestamp), DateUtils.string2Millis(marketInfoModel.downTime, DateUtils.DEFAULT_PATTERN));
                int waitTime = marketInfoModel.days - between;
                if (waitTime > 0) {
                    btn_submit.setEnabled(false);
                    ((QMUIRoundButtonDrawable) btn_submit.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_otc_unhappy));
                    btn_submit.setText(String.format(getResources().getString(R.string.countdown_day), String.valueOf(waitTime)));
                } else {
                    btn_submit.setText(getResources().getString(R.string.confirm_apply_submit));

                    if (isMechant && isOtc) {
                        btn_submit.setEnabled(true);
                        ((QMUIRoundButtonDrawable) btn_submit.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_default));
                    } else {
                        btn_submit.setEnabled(false);
                        ((QMUIRoundButtonDrawable) btn_submit.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_otc_unhappy));
                    }
                }
            } else {
                btn_submit.setText(getResources().getString(R.string.confirm_apply_submit));

                if (isMechant && isOtc) {
                    btn_submit.setEnabled(true);
                    ((QMUIRoundButtonDrawable) btn_submit.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_default));
                } else {
                    btn_submit.setEnabled(false);
                    ((QMUIRoundButtonDrawable) btn_submit.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_otc_unhappy));
                }
            }
        }
    }

    private void showAuthMerchant() {
        new DroidDialog.Builder(getContext())
                .title(getResources().getString(R.string.certified_shoper))
                .content(getResources().getString(R.string.certified_merchant_tips))
                .contentGravity(Gravity.CENTER)
                .positiveButton(getResources().getString(R.string.sure), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        mPresenter.applyMerchantAuth();
                    }
                })
                .negativeButton(getResources().getString(R.string.cancel), null)
                .cancelable(false, false)
                .show();
    }

    private void dropOutDialog() {
        int days = 60;
        if (marketInfoModel != null && marketInfoModel.days != null) {
            days = marketInfoModel.days;
        }
        new DroidDialog.Builder(getContext())
                .title(getResources().getString(R.string.dropout_mechant))
                .content(String.format(getResources().getString(R.string.dropout_mechant_tip), String.valueOf(days)))
                .contentGravity(Gravity.CENTER)
                .positiveButton(getResources().getString(R.string.sure), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        mPresenter.quitMerchant(2);
                    }
                })
                .negativeButton(getResources().getString(R.string.cancel), null)
                .cancelable(false, false)
                .show();
    }

    private void applyDowngradeDialog() {
        new DroidDialog.Builder(getContext())
                .title(getResources().getString(R.string.apply_downgrade))
                .content(getResources().getString(R.string.apply_downgrade_tip))
                .contentGravity(Gravity.CENTER)
                .positiveButton(getResources().getString(R.string.sure), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        mPresenter.downMerchantAuth();
                    }
                })
                .negativeButton(getResources().getString(R.string.cancel), null)
                .cancelable(false, false)
                .show();
    }
}
