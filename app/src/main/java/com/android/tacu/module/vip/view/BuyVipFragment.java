package com.android.tacu.module.vip.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.VipBuyEvent;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.assets.model.AmountModel;
import com.android.tacu.module.assets.view.AssetsActivity;
import com.android.tacu.module.auth.view.AuthActivity;
import com.android.tacu.module.otc.dialog.OtcTradeDialogUtils;
import com.android.tacu.module.vip.contract.BuyVipContract;
import com.android.tacu.module.vip.model.VipDetailModel;
import com.android.tacu.module.vip.model.VipDetailRankModel;
import com.android.tacu.module.vip.presenter.BuyVipPresenter;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.Md5Utils;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;

import butterknife.BindView;
import butterknife.OnClick;

public class BuyVipFragment extends BaseFragment<BuyVipPresenter> implements BuyVipContract.IChildView {

    @BindView(R.id.lin_layout)
    LinearLayout lin_layout;
    @BindView(R.id.tv_vip_name)
    TextView tv_vip_name;
    @BindView(R.id.tv_price)
    TextView tv_price;
    @BindView(R.id.tv_vip_time)
    TextView tv_vip_time;
    @BindView(R.id.tv_account_balance)
    TextView tv_account_balance;
    @BindView(R.id.lin_vip_good)
    LinearLayout lin_vip_good;
    @BindView(R.id.lin_need_pay)
    LinearLayout lin_need_pay;
    @BindView(R.id.tv_need_pay)
    TextView tv_need_pay;
    @BindView(R.id.lin_trade_pwd)
    LinearLayout lin_trade_pwd;
    @BindView(R.id.edit_trade_password)
    QMUIRoundEditText edit_trade_password;
    @BindView(R.id.tv_error)
    TextView tv_error;
    @BindView(R.id.btn)
    QMUIRoundButton btn;

    @BindView(R.id.lin_success)
    LinearLayout lin_success;
    @BindView(R.id.tv_tip)
    TextView tv_tip;
    @BindView(R.id.tv_tip_time)
    TextView tv_tip_time;
    @BindView(R.id.tv_tip_surplus)
    TextView tv_tip_surplus;
    @BindView(R.id.tv_tip1)
    TextView tv_tip1;
    @BindView(R.id.tv_tip2)
    TextView tv_tip2;
    @BindView(R.id.img_success)
    ImageView img_success;
    @BindView(R.id.btn_cancel)
    QMUIRoundButton btn_cancel;

    //1=月度会员(30天)  2=年度会员(12月) 3=连续包年
    private int vipType = 0;
    //true=当前页面是支付页面
    private boolean isPay = false;
    //true=当前页面已经购买成功
    private boolean isBuy = false;
    //0=购买会员 1=去认证 2=确认支付
    private int btnStatus = 0;

    //截止日期
    private String dateTime;
    private Long timestamp;
    private VipDetailModel vipDetailModel;

    public static BuyVipFragment newInstance(int vipType) {
        Bundle bundle = new Bundle();
        bundle.putInt("vipType", vipType);
        BuyVipFragment fragment = new BuyVipFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            vipType = bundle.getInt("vipType");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_buy_vip;
    }

    @Override
    protected void initData(View view) {
        dealView();
    }

    @Override
    protected BuyVipPresenter createPresenter(BuyVipPresenter mPresenter) {
        return new BuyVipPresenter();
    }

    @OnClick(R.id.btn_recharge)
    void btnRechargeClick() {
        if (!isKeyc2()) {
            return;
        }
        jumpTo(AssetsActivity.createActivity(getContext(), Constant.OTC_ACU, Constant.ACU_CURRENCY_ID, 0, true));
    }

    @OnClick(R.id.btn)
    void btnClick() {
        if (!OtcTradeDialogUtils.isDialogShow(getContext())) {
            switch (btnStatus) {
                case 0:
                    isPay = true;
                    dealView();
                    break;
                case 1:
                    jumpTo(AuthActivity.class);
                    break;
                case 2:
                    String pwdString = edit_trade_password.getText().toString().trim();
                    if (spUtil.getPwdVisibility() && TextUtils.isEmpty(pwdString)) {
                        showToastError(getResources().getString(R.string.please_input_trade_password));
                        return;
                    }
                    mPresenter.buyVip(vipType, spUtil.getPwdVisibility() ? Md5Utils.encryptFdPwd(pwdString, spUtil.getUserUid()).toLowerCase() : null);
                    break;
            }
        }
    }

    @OnClick(R.id.btn_cancel)
    void cancelClick() {
        mPresenter.cancleVip();
    }

    @Override
    public void buyVipSuccess() {
        isPay = false;
        EventManage.sendEvent(new BaseEvent<>(EventConstant.VipBuyCode, new VipBuyEvent(true)));
    }

    @Override
    public void cancleVipSuccess() {
        EventManage.sendEvent(new BaseEvent<>(EventConstant.VipBuyCode, new VipBuyEvent(true)));
    }

    public void setVipDetailModel(VipDetailModel model) {
        this.vipDetailModel = model;
        if (vipType == 1) {
            this.isBuy = model.vipMonth != null && model.vipMonth == 1;
        } else if (vipType == 2) {
            this.isBuy = model.vipOneYear != null && model.vipOneYear == 1;
        } else if (vipType == 3) {
            this.isBuy = model.vipYearAuto != null && model.vipYearAuto == 1;
        }
        this.dateTime = model.endTime;
        this.timestamp = model.timestamp;
        dealView();
        if (!judgeBuy()) {
            btn.setEnabled(false);
            ((QMUIRoundButtonDrawable) btn.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_grey));
        } else {
            btn.setEnabled(true);
            ((QMUIRoundButtonDrawable) btn.getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_default));
        }
    }

    public void setVipDetailRankModel(VipDetailRankModel model) {
        if (model != null) {
            if (TextUtils.equals(model.amount, model.currentAmount)) {
                if (vipType == 1) {
                    tv_price.setText(String.format(getResources().getString(R.string.acu_month), model.currentAmount));
                    tv_need_pay.setText(String.format(getResources().getString(R.string.acu_month), model.currentAmount));
                } else {
                    tv_price.setText(String.format(getResources().getString(R.string.acu_year), model.currentAmount));
                    tv_need_pay.setText(String.format(getResources().getString(R.string.acu_year), model.currentAmount));
                }
            } else {
                String orignalPrice;
                String currentPrice;
                String value = null;
                if (vipType == 1) {
                    orignalPrice = String.format(getResources().getString(R.string.acu_month), model.amount);
                    currentPrice = String.format(getResources().getString(R.string.acu_month), model.currentAmount);
                    value = "<small>" + getResources().getString(R.string.original_price) + " " + orignalPrice + "</small>"
                            + "<br><font color=" + ContextCompat.getColor(getContext(), R.color.text_default) + ">" + getResources().getString(R.string.current_price) + " " + currentPrice + "</font></br>";
                } else {
                    orignalPrice = String.format(getResources().getString(R.string.acu_year), model.amount);
                    currentPrice = String.format(getResources().getString(R.string.acu_year), model.currentAmount);
                    value = "<small>" + getResources().getString(R.string.original_price) + " " + orignalPrice + "</small>"
                            + "<br><font color=" + ContextCompat.getColor(getContext(), R.color.text_default) + ">" + getResources().getString(R.string.current_price) + " " + currentPrice + "</font></br>";
                }
                tv_price.setText(Html.fromHtml(value));

                if (vipType == 1) {
                    orignalPrice = String.format(getResources().getString(R.string.acu_month), model.amount);
                    currentPrice = String.format(getResources().getString(R.string.acu_month), model.currentAmount);
                    value = "<small><font color=" + ContextCompat.getColor(getContext(), R.color.text_grey_3) + ">" + getResources().getString(R.string.original_price) + " " + orignalPrice + "</font></small>"
                            + "<br>" + getResources().getString(R.string.current_price) + currentPrice + "</br>";
                } else {
                    orignalPrice = String.format(getResources().getString(R.string.acu_year), model.amount);
                    currentPrice = String.format(getResources().getString(R.string.acu_year), model.currentAmount);
                    value = "<small><font color=" + ContextCompat.getColor(getContext(), R.color.text_grey_3) + ">" + getResources().getString(R.string.original_price) + " " + orignalPrice + "</font></small>"
                            + "<br>" + getResources().getString(R.string.current_price) + currentPrice + "</br>";
                }
                tv_need_pay.setText(Html.fromHtml(value));
            }
        }
    }

    public void customerCoinByOneCoin(AmountModel model) {
        if (model != null) {
            tv_account_balance.setText(FormatterUtils.getFormatValue(model.attachment) + Constant.OTC_ACU);
        }
    }

    private void dealView() {
        switch (vipType) {
            case 1:
                tv_vip_name.setText(getResources().getString(R.string.vip_month));
                tv_vip_time.setText("30");
                break;
            case 2:
                tv_vip_name.setText(getResources().getString(R.string.vip_year));
                tv_vip_time.setText(getResources().getString(R.string.month_12));
                break;
            case 3:
                tv_vip_name.setText(getResources().getString(R.string.vip_year_continue));
                tv_vip_time.setText(getResources().getString(R.string.automatic_renewal));
                break;
        }
        if (!isPay) {
            lin_vip_good.setVisibility(View.VISIBLE);
            lin_need_pay.setVisibility(View.GONE);
            lin_trade_pwd.setVisibility(View.GONE);
            btnStatus = 0;

            if (spUtil.getIsAuthSenior() == 2) {
                tv_error.setVisibility(View.GONE);
                btnStatus = 0;
            } else if (spUtil.getIsAuthSenior() < 2) {
                tv_error.setVisibility(View.VISIBLE);
                btnStatus = 1;
            }
        } else {
            lin_vip_good.setVisibility(View.GONE);
            lin_need_pay.setVisibility(View.VISIBLE);
            lin_trade_pwd.setVisibility(spUtil.getPwdVisibility() ? View.VISIBLE : View.GONE);
            btnStatus = 2;
        }
        showBtn();

        if (isBuy) {
            lin_layout.setVisibility(View.GONE);
            lin_success.setVisibility(View.VISIBLE);
            buyVipSuccessView();
        } else {
            lin_layout.setVisibility(View.VISIBLE);
            lin_success.setVisibility(View.GONE);
        }
    }

    private void showBtn() {
        switch (btnStatus) {
            case 0:
                btn.setText(getResources().getString(R.string.buy_vip));
                break;
            case 1:
                btn.setText(getResources().getString(R.string.go_auth));
                break;
            case 2:
                btn.setText(getResources().getString(R.string.confirm_pay));
                break;
        }
    }

    private void buyVipSuccessView() {
        switch (vipType) {
            case 1:
                tv_tip.setText(String.format(getResources().getString(R.string.buy_success_tip), getResources().getString(R.string.vip_month)));
                if (!TextUtils.isEmpty(dateTime)) {
                    tv_tip_time.setText(String.format(getResources().getString(R.string.vip_end_time), DateUtils.getStrToStr(dateTime, DateUtils.DEFAULT_PATTERN, DateUtils.FORMAT_DATE_YMD)));
                }
                tv_tip_surplus.setVisibility(View.VISIBLE);
                if (timestamp != null && !TextUtils.isEmpty(dateTime)) {
                    tv_tip_surplus.setText(String.format(getResources().getString(R.string.vip_surplus_time), String.valueOf(DateUtils.differentDaysByMillisecond(DateUtils.string2Millis(dateTime, DateUtils.DEFAULT_PATTERN), timestamp))));
                }
                tv_tip1.setVisibility(View.GONE);
                tv_tip2.setVisibility(View.GONE);
                img_success.setVisibility(View.VISIBLE);
                btn_cancel.setVisibility(View.GONE);
                break;
            case 2:
                tv_tip.setText(String.format(getResources().getString(R.string.buy_success_tip), getResources().getString(R.string.vip_year)));
                if (!TextUtils.isEmpty(dateTime)) {
                    tv_tip_time.setText(String.format(getResources().getString(R.string.vip_end_time), DateUtils.getStrToStr(dateTime, DateUtils.DEFAULT_PATTERN, DateUtils.FORMAT_DATE_YMD)));
                }
                tv_tip_surplus.setVisibility(View.VISIBLE);
                if (timestamp != null && !TextUtils.isEmpty(dateTime)) {
                    tv_tip_surplus.setText(String.format(getResources().getString(R.string.vip_surplus_time), String.valueOf(DateUtils.differentDaysByMillisecond(DateUtils.string2Millis(dateTime, DateUtils.DEFAULT_PATTERN), timestamp))));
                }
                tv_tip1.setVisibility(View.GONE);
                tv_tip2.setVisibility(View.GONE);
                img_success.setVisibility(View.VISIBLE);
                btn_cancel.setVisibility(View.GONE);
                break;
            case 3:
                tv_tip.setText(String.format(getResources().getString(R.string.buy_success_tip), getResources().getString(R.string.vip_year_continue)));
                tv_tip_time.setText(getResources().getString(R.string.endtime_renewal));
                tv_tip_surplus.setVisibility(View.GONE);
                tv_tip1.setVisibility(View.VISIBLE);
                tv_tip2.setVisibility(View.VISIBLE);
                img_success.setVisibility(View.GONE);
                btn_cancel.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * @return true允许购买
     */
    private boolean judgeBuy() {
        if (vipDetailModel != null) {
            if (vipType == 1) {
                if (vipDetailModel.vipYearAuto != null && vipDetailModel.vipYearAuto == 1) {
                    return false;
                }
            } else if (vipType == 2) {
                if (vipDetailModel.vipYearAuto != null && vipDetailModel.vipYearAuto == 1) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isKeyc2() {
        int flag = spUtil.getIsAuthSenior();
        if (flag <= 1) {
            showToastError(getString(R.string.please_get_the_level_of_KYC));
            return false;
        }
        return true;
    }
}
