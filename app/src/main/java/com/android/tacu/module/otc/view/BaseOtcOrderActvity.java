package com.android.tacu.module.otc.view;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcMarketOrderAllModel;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.GlideUtils;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundConstraintLayout;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundRelativeLayout;

import butterknife.BindView;
import butterknife.OnClick;

public abstract class BaseOtcOrderActvity<P extends BaseMvpPresenter> extends BaseActivity<P> {

    @BindView(R.id.view_top)
    QMUIRoundConstraintLayout view_top;
    @BindView(R.id.view_center)
    ConstraintLayout view_center;
    @BindView(R.id.view_line)
    View view_line;
    @BindView(R.id.view_bottom)
    ConstraintLayout view_bottom;

    @BindView(R.id.img_people_top)
    ImageView img_people_top;
    @BindView(R.id.tv_people_nickname_top)
    TextView tv_people_nickname_top;
    @BindView(R.id.img_people_vip_top)
    ImageView img_people_vip_top;
    @BindView(R.id.rl_people_bg_top)
    QMUIRoundRelativeLayout rl_people_bg_top;

    //卖
    @BindView(R.id.img_seller)
    ImageView img_seller;
    @BindView(R.id.tv_seller_nickname)
    TextView tv_seller_nickname;
    @BindView(R.id.img_seller_vip)
    ImageView img_seller_vip;
    @BindView(R.id.img_seller_phone_auth)
    ImageView img_seller_phone_auth;
    @BindView(R.id.img_seller_truenname_auth)
    ImageView img_seller_truenname_auth;
    @BindView(R.id.img_seller_email_auth)
    ImageView img_seller_email_auth;
    @BindView(R.id.img_seller_video_auth)
    ImageView img_seller_video_auth;
    @BindView(R.id.tv_seller_phone_auth)
    TextView tv_seller_phone_auth;
    @BindView(R.id.tv_seller_truenname_auth)
    TextView tv_seller_truenname_auth;
    @BindView(R.id.tv_seller_email_auth)
    TextView tv_seller_email_auth;
    @BindView(R.id.tv_seller_video_auth)
    TextView tv_seller_video_auth;
    @BindView(R.id.tv_seller_pay)
    TextView tv_seller_pay;

    //买
    @BindView(R.id.img_buyer)
    ImageView img_buyer;
    @BindView(R.id.tv_buyer_nickname)
    TextView tv_buyer_nickname;
    @BindView(R.id.img_buyer_vip)
    ImageView img_buyer_vip;
    @BindView(R.id.img_buyer_phone_auth)
    ImageView img_buyer_phone_auth;
    @BindView(R.id.img_buyer_truenname_auth)
    ImageView img_buyer_truenname_auth;
    @BindView(R.id.img_buyer_email_auth)
    ImageView img_buyer_email_auth;
    @BindView(R.id.img_buyer_video_auth)
    ImageView img_buyer_video_auth;
    @BindView(R.id.tv_buyer_phone_auth)
    TextView tv_buyer_phone_auth;
    @BindView(R.id.tv_buyer_truenname_auth)
    TextView tv_buyer_truenname_auth;
    @BindView(R.id.tv_buyer_email_auth)
    TextView tv_buyer_email_auth;
    @BindView(R.id.tv_buyer_video_auth)
    TextView tv_buyer_video_auth;
    @BindView(R.id.tv_buyer_pay)
    TextView tv_buyer_pay;

    @OnClick(R.id.tv_open)
    void openClick() {
        view_top.setVisibility(View.GONE);
        view_line.setVisibility(View.VISIBLE);
        view_center.setVisibility(View.VISIBLE);
        view_bottom.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.tv_hide)
    void hideClick() {
        view_top.setVisibility(View.VISIBLE);
        view_line.setVisibility(View.GONE);
        view_center.setVisibility(View.GONE);
        view_bottom.setVisibility(View.GONE);
    }

    public void setSellValue(OtcMarketInfoModel infoModel, Integer queryUid) {
        if (infoModel != null) {
            GlideUtils.disPlay(this, CommonUtils.getHead(infoModel.headImg), img_seller);
            tv_seller_nickname.setText(infoModel.nickname + "(" + CommonUtils.nameXing(infoModel.secondName) + ")");
            if (infoModel.applyAuthMerchantStatus != null && infoModel.applyAuthMerchantStatus == 2) {
                img_seller_vip.setImageResource(R.drawable.icon_vip);
            } else if (infoModel.applyMerchantStatus != null && infoModel.applyMerchantStatus == 2) {
                img_seller_vip.setImageResource(R.drawable.icon_vip_grey);
            } else if (infoModel.vip != null && infoModel.vip != 0) {
                img_seller_vip.setImageResource(R.mipmap.img_vip_green);
            }

            if (queryUid != null && queryUid != 0 && queryUid != spUtil.getUserUid()) {
                GlideUtils.disPlay(this, CommonUtils.getHead(infoModel.headImg), img_people_top);
                tv_people_nickname_top.setText(infoModel.nickname + "(" + CommonUtils.nameXing(infoModel.secondName) + ")");
                if (infoModel.applyAuthMerchantStatus != null && infoModel.applyAuthMerchantStatus == 2) {
                    img_people_vip_top.setImageResource(R.drawable.icon_vip);
                } else if (infoModel.applyMerchantStatus != null && infoModel.applyMerchantStatus == 2) {
                    img_people_vip_top.setImageResource(R.drawable.icon_vip_grey);
                } else if (infoModel.vip != null && infoModel.vip != 0) {
                    img_people_vip_top.setImageResource(R.mipmap.img_vip_green);
                }
            }

            if (infoModel.isValidatePhone != null && infoModel.isValidatePhone == 1) {
                img_seller_phone_auth.setImageResource(R.drawable.icon_auth_success);
            } else {
                img_seller_phone_auth.setImageResource(R.drawable.icon_auth_failure);
            }
            if (infoModel.isAuthSenior != null && infoModel.isAuthSenior >= 2) {
                img_seller_truenname_auth.setImageResource(R.drawable.icon_auth_success);
            } else {
                img_seller_truenname_auth.setImageResource(R.drawable.icon_auth_failure);
            }
            if (infoModel.isValidateEmail != null && infoModel.isValidateEmail == 1) {
                img_seller_email_auth.setImageResource(R.drawable.icon_auth_success);
            } else {
                img_seller_email_auth.setImageResource(R.drawable.icon_auth_failure);
            }
            if ((infoModel.applyMerchantStatus != null && infoModel.applyMerchantStatus == 2) || (infoModel.applyAuthMerchantStatus != null && infoModel.applyAuthMerchantStatus == 2)) {
                img_seller_video_auth.setImageResource(R.drawable.icon_auth_success);
            } else {
                img_seller_video_auth.setImageResource(R.drawable.icon_auth_failure);
            }
        }
    }

    public void setBuyValue(OtcMarketInfoModel infoModel, Integer queryUid) {
        if (infoModel != null) {
            GlideUtils.disPlay(this, CommonUtils.getHead(infoModel.headImg), img_buyer);
            tv_buyer_nickname.setText(infoModel.nickname + "(" + CommonUtils.nameXing(infoModel.secondName) + ")");
            if (infoModel.applyAuthMerchantStatus != null && infoModel.applyAuthMerchantStatus == 2) {
                img_buyer_vip.setImageResource(R.drawable.icon_vip);
            } else if (infoModel.applyMerchantStatus != null && infoModel.applyMerchantStatus == 2) {
                img_buyer_vip.setImageResource(R.drawable.icon_vip_grey);
            } else if (infoModel.vip != null && infoModel.vip != 0) {
                img_buyer_vip.setImageResource(R.mipmap.img_vip_green);
            }

            if (queryUid != null && queryUid != 0 && queryUid != spUtil.getUserUid()) {
                GlideUtils.disPlay(this, CommonUtils.getHead(infoModel.headImg), img_people_top);
                tv_people_nickname_top.setText(infoModel.nickname + "(" + CommonUtils.nameXing(infoModel.secondName) + ")");
                if (infoModel.applyAuthMerchantStatus != null && infoModel.applyAuthMerchantStatus == 2) {
                    img_people_vip_top.setImageResource(R.drawable.icon_vip);
                } else if (infoModel.applyMerchantStatus != null && infoModel.applyMerchantStatus == 2) {
                    img_people_vip_top.setImageResource(R.drawable.icon_vip_grey);
                } else if (infoModel.vip != null && infoModel.vip != 0) {
                    img_people_vip_top.setImageResource(R.mipmap.img_vip_green);
                }
            }

            if (infoModel.isValidatePhone != null && infoModel.isValidatePhone == 1) {
                img_buyer_phone_auth.setImageResource(R.drawable.icon_auth_success);
            } else {
                img_buyer_phone_auth.setImageResource(R.drawable.icon_auth_failure);
            }
            if (infoModel.isAuthSenior != null && infoModel.isAuthSenior >= 2) {
                img_buyer_truenname_auth.setImageResource(R.drawable.icon_auth_success);
            } else {
                img_buyer_truenname_auth.setImageResource(R.drawable.icon_auth_failure);
            }
            if (infoModel.isValidateEmail != null && infoModel.isValidateEmail == 1) {
                img_buyer_email_auth.setImageResource(R.drawable.icon_auth_success);
            } else {
                img_buyer_email_auth.setImageResource(R.drawable.icon_auth_failure);
            }
            if ((infoModel.applyMerchantStatus != null && infoModel.applyMerchantStatus == 2) || (infoModel.applyAuthMerchantStatus != null && infoModel.applyAuthMerchantStatus == 2)) {
                img_buyer_video_auth.setImageResource(R.drawable.icon_auth_success);
            } else {
                img_buyer_video_auth.setImageResource(R.drawable.icon_auth_failure);
            }
        }
    }

    public void setBuyPayInfoString(OtcMarketOrderAllModel allModel, String amount) {
        String valueWei = " " + Constant.CNY;
        if (allModel != null && allModel.orderModel != null && allModel.orderModel.money != null && allModel.orderModel.money == 1) {
            valueWei = " " + Constant.CNY;
        }
        tv_buyer_pay.setText(amount + " " + valueWei);
    }

    public void setSellPayInfoString(OtcMarketOrderAllModel allModel, String num) {
        if (allModel != null && allModel.orderModel != null) {
            tv_seller_pay.setText(num + " " + allModel.orderModel.currencyName);
        }
    }

    public void setBuyPayInfoString(OtcTradeModel model) {
        if (model != null) {
            tv_buyer_pay.setText(model.amount + " " + Constant.CNY);
        }
    }

    public void setSellPayInfoString(OtcTradeModel model) {
        if (model != null) {
            tv_seller_pay.setText(model.num + " " + model.currencyName);
        }
    }
}
