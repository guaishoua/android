package com.android.tacu.module.otc.view;

import android.text.TextUtils;
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

import butterknife.BindView;

public abstract class BaseOtcOrderActvity<P extends BaseMvpPresenter> extends BaseActivity<P> {

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

    public void setValue(boolean isBuy, OtcMarketOrderAllModel allModel, String num, String amount) {
        if (isBuy) {
            if (allModel != null && allModel.infoModel != null) {
                setSellValue(allModel.infoModel);
            }
            setBuyValueLocal();
        } else {
            setSellValueLocal();
            if (allModel != null && allModel.infoModel != null) {
                setBuyValue(allModel.infoModel);
            }
        }
        setBuyPayInfoString(allModel, amount);
        setSellPayInfoString(allModel, num);
    }

    public void setSellValue(OtcMarketInfoModel infoModel) {
        if (infoModel != null) {
            GlideUtils.disPlay(this, CommonUtils.getHead(infoModel.headImg), img_seller);
            tv_seller_nickname.setText(infoModel.nickname + "(" + infoModel.secondName + ")");
            if (infoModel.vip != null && infoModel.vip != 0) {
                img_seller_vip.setImageResource(R.mipmap.img_vip_green);
            } else if (infoModel.applyMerchantStatus != null && infoModel.applyMerchantStatus == 2) {
                img_seller_vip.setImageResource(R.mipmap.img_vip_grey);
            } else if (infoModel.applyAuthMerchantStatus != null && infoModel.applyAuthMerchantStatus == 2) {
                img_seller_vip.setImageResource(R.drawable.icon_vip);
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

    private void setSellValueLocal() {
        GlideUtils.disPlay(this, CommonUtils.getHead(spUtil.getHeadImg()), img_seller);
        tv_seller_nickname.setText(spUtil.getNickName() + "(" + spUtil.getKYCName() + ")");
        if (spUtil.getVip() != 0) {
            img_seller_vip.setImageResource(R.mipmap.img_vip_green);
        } else if (spUtil.getApplyMerchantStatus() == 2) {
            img_seller_vip.setImageResource(R.mipmap.img_vip_grey);
        } else if (spUtil.getApplyAuthMerchantStatus() == 2) {
            img_seller_vip.setImageResource(R.drawable.icon_vip);
        }
        if (spUtil.getPhoneStatus()) {
            img_seller_phone_auth.setImageResource(R.drawable.icon_auth_success);
        } else {
            img_seller_phone_auth.setImageResource(R.drawable.icon_auth_failure);
        }
        if (spUtil.getIsAuthSenior() >= 2) {
            img_seller_truenname_auth.setImageResource(R.drawable.icon_auth_success);
        } else {
            img_seller_truenname_auth.setImageResource(R.drawable.icon_auth_failure);
        }
        if (spUtil.getEmailStatus()) {
            img_seller_email_auth.setImageResource(R.drawable.icon_auth_success);
        } else {
            img_seller_email_auth.setImageResource(R.drawable.icon_auth_failure);
        }
        if ((spUtil.getApplyMerchantStatus() == 2) || (spUtil.getApplyAuthMerchantStatus() == 2)) {
            img_seller_video_auth.setImageResource(R.drawable.icon_auth_success);
        } else {
            img_seller_video_auth.setImageResource(R.drawable.icon_auth_failure);
        }
    }

    public void setBuyValue(OtcMarketInfoModel infoModel) {
        if (infoModel != null) {
            GlideUtils.disPlay(this, CommonUtils.getHead(infoModel.headImg), img_buyer);
            tv_buyer_nickname.setText(infoModel.nickname + "(" + infoModel.secondName + ")");
            if (infoModel.vip != null && infoModel.vip != 0) {
                img_buyer_vip.setImageResource(R.mipmap.img_vip_green);
            } else if (infoModel.applyMerchantStatus != null && infoModel.applyMerchantStatus == 2) {
                img_buyer_vip.setImageResource(R.mipmap.img_vip_grey);
            } else if (infoModel.applyAuthMerchantStatus != null && infoModel.applyAuthMerchantStatus == 2) {
                img_buyer_vip.setImageResource(R.drawable.icon_vip);
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

    private void setBuyValueLocal() {
        GlideUtils.disPlay(this, CommonUtils.getHead(spUtil.getHeadImg()), img_buyer);
        tv_buyer_nickname.setText(spUtil.getNickName() + "(" + spUtil.getKYCName() + ")");
        if (spUtil.getVip() != 0) {
            img_buyer_vip.setImageResource(R.mipmap.img_vip_green);
        } else if (spUtil.getApplyMerchantStatus() == 2) {
            img_buyer_vip.setImageResource(R.mipmap.img_vip_grey);
        } else if (spUtil.getApplyAuthMerchantStatus() == 2) {
            img_buyer_vip.setImageResource(R.drawable.icon_vip);
        }
        if (spUtil.getPhoneStatus()) {
            img_buyer_phone_auth.setImageResource(R.drawable.icon_auth_success);
        } else {
            img_buyer_phone_auth.setImageResource(R.drawable.icon_auth_failure);
        }
        if (spUtil.getIsAuthSenior() >= 2) {
            img_buyer_truenname_auth.setImageResource(R.drawable.icon_auth_success);
        } else {
            img_buyer_truenname_auth.setImageResource(R.drawable.icon_auth_failure);
        }
        if (spUtil.getEmailStatus()) {
            img_buyer_email_auth.setImageResource(R.drawable.icon_auth_success);
        } else {
            img_buyer_email_auth.setImageResource(R.drawable.icon_auth_failure);
        }
        if ((spUtil.getApplyMerchantStatus() == 2) || (spUtil.getApplyAuthMerchantStatus() == 2)) {
            img_buyer_video_auth.setImageResource(R.drawable.icon_auth_success);
        } else {
            img_buyer_video_auth.setImageResource(R.drawable.icon_auth_failure);
        }
    }

    private void setBuyPayInfoString(OtcMarketOrderAllModel allModel, String amount) {
        String valueWei = "CNY";
        if (allModel != null && allModel.orderModel != null && allModel.orderModel.money != null && allModel.orderModel.money == 1) {
            valueWei = "CNY";
        }
        tv_buyer_pay.setText(amount + " " + valueWei);
    }

    private void setSellPayInfoString(OtcMarketOrderAllModel allModel, String num) {
        String valueCoin = Constant.OTC_ACU;
        if (allModel != null && allModel.orderModel != null && allModel.orderModel.currencyName != null && !TextUtils.isEmpty(allModel.orderModel.currencyName)) {
            valueCoin = allModel.orderModel.currencyName;
        }
        tv_seller_pay.setText(num + " " + valueCoin);
    }

    public void setBuyPayInfoString(OtcTradeModel model) {
        if (model != null) {
            tv_buyer_pay.setText(model.amount + " CNY");
        }
    }

    public void setSellPayInfoString(OtcTradeModel model) {
        if (model != null) {
            tv_seller_pay.setText(model.num + " " + model.currencyName);
        }
    }
}
