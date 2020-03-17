package com.android.tacu.module.otc.view;

import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.MathHelper;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundConstraintLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundLinearLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundRelativeLayout;

import butterknife.BindView;
import butterknife.OnClick;

public abstract class BaseOtcHalfOrderActvity<P extends BaseMvpPresenter> extends BaseActivity<P> {

    @BindView(R.id.view_top)
    QMUIRoundConstraintLayout view_top;
    @BindView(R.id.view_center)
    QMUIRoundConstraintLayout view_center;
    @BindView(R.id.view_bottom)
    QMUIRoundLinearLayout view_bottom;

    @BindView(R.id.img_people_top)
    ImageView img_people_top;
    @BindView(R.id.tv_people_nickname_top)
    TextView tv_people_nickname_top;
    @BindView(R.id.img_people_vip_top)
    ImageView img_people_vip_top;
    @BindView(R.id.rl_people_bg_top)
    QMUIRoundRelativeLayout rl_people_bg_top;

    @BindView(R.id.img_people)
    ImageView img_people;
    @BindView(R.id.tv_people_nickname)
    TextView tv_people_nickname;
    @BindView(R.id.img_people_vip)
    ImageView img_people_vip;
    @BindView(R.id.img_people_phone_auth)
    ImageView img_people_phone_auth;
    @BindView(R.id.img_people_truenname_auth)
    ImageView img_people_truenname_auth;
    @BindView(R.id.img_people_email_auth)
    ImageView img_people_email_auth;
    @BindView(R.id.img_people_video_auth)
    ImageView img_people_video_auth;
    @BindView(R.id.tv_people_phone_auth)
    TextView tv_people_phone_auth;
    @BindView(R.id.tv_people_truenname_auth)
    TextView tv_people_truenname_auth;
    @BindView(R.id.tv_people_email_auth)
    TextView tv_people_email_auth;
    @BindView(R.id.tv_people_video_auth)
    TextView tv_people_video_auth;

    @BindView(R.id.tv_bond)
    TextView tv_bond;
    @BindView(R.id.tv_all_deal_amount)
    TextView tv_all_deal_amount;
    @BindView(R.id.tv_buy_amount)
    TextView tv_buy_amount;
    @BindView(R.id.tv_sell_amount)
    TextView tv_sell_amount;
    @BindView(R.id.tv_deal_rate)
    TextView tv_deal_rate;

    @BindView(R.id.tv_dispute_amount)
    TextView tv_dispute_amount;
    @BindView(R.id.tv_be_arbitrated_amount)
    TextView tv_be_arbitrated_amount;
    @BindView(R.id.tv_arbitration_amount)
    TextView tv_arbitration_amount;
    @BindView(R.id.tv_appeal_amount)
    TextView tv_appeal_amount;
    @BindView(R.id.tv_win_lawsuit_amount)
    TextView tv_win_lawsuit_amount;

    @BindView(R.id.tv_average_payment_time)
    TextView tv_average_payment_time;

    @BindView(R.id.tv_people_pay_title)
    TextView tv_people_pay_title;
    @BindView(R.id.tv_people_pay)
    TextView tv_people_pay;
    @BindView(R.id.rl_people_bg)
    QMUIRoundRelativeLayout rl_people_bg;

    @OnClick(R.id.tv_open)
    void openClick() {
        view_top.setVisibility(View.GONE);
        view_center.setVisibility(View.VISIBLE);
        view_bottom.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.tv_hide)
    void hideClick() {
        view_top.setVisibility(View.VISIBLE);
        view_center.setVisibility(View.GONE);
        view_bottom.setVisibility(View.GONE);
    }

    /**
     * @param notOwnBuyOrSell //1买 2卖  确认对方是买还是卖
     */
    protected void setPayValueString(int notOwnBuyOrSell, OtcTradeModel model) {
        if (notOwnBuyOrSell == 1) {
            tv_people_pay_title.setText(getResources().getString(R.string.buy_pay));
            ((QMUIRoundButtonDrawable) rl_people_bg.getBackground()).setBgData(ContextCompat.getColorStateList(this, R.color.color_otc_buy_bg));
            ((QMUIRoundButtonDrawable) rl_people_bg_top.getBackground()).setBgData(ContextCompat.getColorStateList(this, R.color.color_otc_buy_bg));
            tv_people_pay.setTextColor(ContextCompat.getColorStateList(this, R.color.color_otc_buy_bg));
            if (model != null) {
                tv_people_pay.setText(model.amount + "CNY");
            }
        } else if (notOwnBuyOrSell == 2) {
            tv_people_pay_title.setText(getResources().getString(R.string.sell_pay));
            ((QMUIRoundButtonDrawable) rl_people_bg.getBackground()).setBgData(ContextCompat.getColorStateList(this, R.color.color_otc_sell_bg));
            ((QMUIRoundButtonDrawable) rl_people_bg_top.getBackground()).setBgData(ContextCompat.getColorStateList(this, R.color.color_otc_sell_bg));
            tv_people_pay.setTextColor(ContextCompat.getColorStateList(this, R.color.color_otc_sell_bg));
            if (model != null) {
                tv_people_pay.setText(model.num + " " + model.currencyName);
            }
        }
    }

    protected void setInfoValue(boolean isBuy, OtcMarketInfoModel infoModel) {
        if (isBuy) {
            ((QMUIRoundButtonDrawable) rl_people_bg_top.getBackground()).setBgData(ContextCompat.getColorStateList(this, R.color.color_otc_buy_bg));
        } else {
            ((QMUIRoundButtonDrawable) rl_people_bg_top.getBackground()).setBgData(ContextCompat.getColorStateList(this, R.color.color_otc_sell_bg));
        }
        setInfoValue(infoModel, false);
    }

    /**
     * @param infoModel
     * @param isShowName true=显示真实名字 false=不显示
     */
    protected void setInfoValue(OtcMarketInfoModel infoModel, boolean isShowName) {
        if (infoModel != null) {
            GlideUtils.disPlay(this, CommonUtils.getHead(infoModel.headImg), img_people);
            if (isShowName) {
                tv_people_nickname.setText(infoModel.nickname + "(" + CommonUtils.nameXing(infoModel.firstName, infoModel.secondName) + ")");
            } else {
                tv_people_nickname.setText(infoModel.nickname + "(" + CommonUtils.nameXing(infoModel.secondName) + ")");
            }
            if (infoModel.applyAuthMerchantStatus != null && infoModel.applyAuthMerchantStatus == 2) {
                img_people_vip.setImageResource(R.drawable.icon_vip);
            } else if (infoModel.applyMerchantStatus != null && infoModel.applyMerchantStatus == 2) {
                img_people_vip.setImageResource(R.drawable.icon_vip_grey);
            } else if (infoModel.vip != null && infoModel.vip != 0) {
                img_people_vip.setImageResource(R.mipmap.img_vip_green);
            }

            GlideUtils.disPlay(this, CommonUtils.getHead(infoModel.headImg), img_people_top);
            if (isShowName) {
                tv_people_nickname_top.setText(infoModel.nickname + "(" + CommonUtils.nameXing(infoModel.firstName, infoModel.secondName) + ")");
            } else {
                tv_people_nickname_top.setText(infoModel.nickname + "(" + CommonUtils.nameXing(infoModel.secondName) + ")");
            }
            if (infoModel.applyAuthMerchantStatus != null && infoModel.applyAuthMerchantStatus == 2) {
                img_people_vip_top.setImageResource(R.drawable.icon_vip);
            } else if (infoModel.applyMerchantStatus != null && infoModel.applyMerchantStatus == 2) {
                img_people_vip_top.setImageResource(R.drawable.icon_vip_grey);
            } else if (infoModel.vip != null && infoModel.vip != 0) {
                img_people_vip_top.setImageResource(R.mipmap.img_vip_green);
            }

            if (infoModel.isValidatePhone != null && infoModel.isValidatePhone == 1) {
                img_people_phone_auth.setImageResource(R.drawable.icon_auth_success);
            } else {
                img_people_phone_auth.setImageResource(R.drawable.icon_auth_failure);
            }
            if (infoModel.isAuthSenior != null && infoModel.isAuthSenior >= 2) {
                img_people_truenname_auth.setImageResource(R.drawable.icon_auth_success);
            } else {
                img_people_truenname_auth.setImageResource(R.drawable.icon_auth_failure);
            }
            if (infoModel.isValidateEmail != null && infoModel.isValidateEmail == 1) {
                img_people_email_auth.setImageResource(R.drawable.icon_auth_success);
            } else {
                img_people_email_auth.setImageResource(R.drawable.icon_auth_failure);
            }
            if ((infoModel.applyMerchantStatus != null && infoModel.applyMerchantStatus == 2) || (infoModel.applyAuthMerchantStatus != null && infoModel.applyAuthMerchantStatus == 2)) {
                img_people_video_auth.setImageResource(R.drawable.icon_auth_success);
            } else {
                img_people_video_auth.setImageResource(R.drawable.icon_auth_failure);
            }
            tv_bond.setText(FormatterUtils.getFormatValue(infoModel.bondMoney) + " " + Constant.ACU_CURRENCY_NAME);
            tv_all_deal_amount.setText(FormatterUtils.getFormatValue(infoModel.total) + getResources().getString(R.string.dan));
            tv_buy_amount.setText(FormatterUtils.getFormatValue(infoModel.buy) + getResources().getString(R.string.dan));
            tv_sell_amount.setText(FormatterUtils.getFormatValue(infoModel.sell) + getResources().getString(R.string.dan));
            if (!TextUtils.isEmpty(infoModel.success) && !TextUtils.isEmpty(infoModel.total) && !TextUtils.equals(infoModel.success, "0") && !TextUtils.equals(infoModel.total, "0")) {
                double value = MathHelper.div(Double.parseDouble(infoModel.success), Double.valueOf(infoModel.total));
                tv_deal_rate.setText(FormatterUtils.getFormatRoundDown(2, MathHelper.mul(value, 100)) + " %");
            } else {
                tv_deal_rate.setText("0%");
            }
            tv_dispute_amount.setText(infoModel.disputeTotal);
            tv_be_arbitrated_amount.setText(infoModel.disputeByOther);
            tv_arbitration_amount.setText(infoModel.disputeBySelf);
            tv_appeal_amount.setText(infoModel.otherDispute);
            tv_win_lawsuit_amount.setText(infoModel.winDispute);

            if (!TextUtils.isEmpty(infoModel.allTime) && !TextUtils.isEmpty(infoModel.success) && !TextUtils.equals(infoModel.allTime, "0") && !TextUtils.equals(infoModel.success, "0")) {
                double valueD = MathHelper.div(Double.valueOf(infoModel.allTime), Double.valueOf(infoModel.success));
                String value = FormatterUtils.getFormatRoundDown(2, valueD) + " min";
                Spanned text = Html.fromHtml(getResources().getString(R.string.average_payment_time) + "<font color=" + ContextCompat.getColor(this, R.color.text_default) + ">" + value + "</font>");
                tv_average_payment_time.setText(text);
            } else {
                Spanned text = Html.fromHtml(getResources().getString(R.string.average_payment_time) + "<font color=" + ContextCompat.getColor(this, R.color.text_default) + ">0 min</font>");
                tv_average_payment_time.setText(text);
            }
        }
    }
}
