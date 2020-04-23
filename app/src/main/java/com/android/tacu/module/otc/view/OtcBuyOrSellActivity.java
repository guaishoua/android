package com.android.tacu.module.otc.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.payinfo.model.PayInfoModel;
import com.android.tacu.module.assets.view.BindingPayInfoActivity;
import com.android.tacu.module.otc.contract.OtcBuyOrSellContract;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcMarketOrderAllModel;
import com.android.tacu.module.otc.model.OtcMarketOrderModel;
import com.android.tacu.module.otc.presenter.OtcBuyOrSellPresenter;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.MathHelper;
import com.android.tacu.utils.user.UserManageUtils;
import com.android.tacu.widget.dialog.DroidDialog;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class OtcBuyOrSellActivity extends BaseActivity<OtcBuyOrSellPresenter> implements OtcBuyOrSellContract.IView, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.img_people)
    QMUIRadiusImageView img_people;
    @BindView(R.id.tv_people_nickname)
    TextView tv_people_nickname;
    @BindView(R.id.btn_shop_info)
    QMUIAlphaButton btn_shop_info;
    @BindView(R.id.img_people_phone_auth)
    ImageView img_people_phone_auth;
    @BindView(R.id.img_people_truenname_auth)
    ImageView img_people_truenname_auth;
    @BindView(R.id.img_people_email_auth)
    ImageView img_people_email_auth;
    @BindView(R.id.img_people_video_auth)
    ImageView img_people_video_auth;

    @BindView(R.id.tv_num)
    TextView tv_num;
    @BindView(R.id.edit_num)
    EditText edit_num;
    @BindView(R.id.tv_amount)
    TextView tv_amount;
    @BindView(R.id.edit_amount)
    EditText edit_amount;
    @BindView(R.id.tv_limit)
    TextView tv_limit;
    @BindView(R.id.tv_account_balance)
    TextView tv_account_balance;
    @BindView(R.id.btn_sure)
    QMUIRoundButton btn_sure;

    private CheckBox cb_wx;
    private CheckBox cb_zfb;
    private CheckBox cb_yhk;

    private boolean isBuy = true;
    private String orderId;

    private OtcMarketOrderAllModel allModel;
    //防止EditText和EditText死循环
    private boolean isInputNum = true;
    private boolean isInputAmount = true;
    private boolean isInputAll = true;

    //支付类型 1银行卡 2微信 3支付宝
    private Integer payType;
    private PayInfoModel yhkModel = null, wxModel = null, zfbModel = null;
    private DroidDialog droidDialog;

    public static Intent createActivity(Context context, boolean isBuy, String orderId) {
        Intent intent = new Intent(context, OtcBuyOrSellActivity.class);
        intent.putExtra("isBuy", isBuy);
        intent.putExtra("orderId", orderId);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_buy_sell);
    }

    @Override
    protected void initView() {
        isBuy = getIntent().getBooleanExtra("isBuy", true);
        orderId = getIntent().getStringExtra("orderId");

        if (isBuy) {
            mTopBar.setTitle(getResources().getString(R.string.otc_buy_page));
            btn_shop_info.setText(getResources().getString(R.string.seller_info));
            tv_num.setText(getResources().getString(R.string.buy_number));
            edit_num.setHint(getResources().getString(R.string.input_buy_number));
            tv_amount.setText(getResources().getString(R.string.buy_all_money));
            edit_amount.setHint(getResources().getString(R.string.input_buy_all_money));
            btn_sure.setText(getResources().getString(R.string.confirm_buy));
            ((QMUIRoundButtonDrawable) btn_sure.getBackground()).setBgData(ContextCompat.getColorStateList(this, R.color.color_otc_buy));
        } else {
            mTopBar.setTitle(getResources().getString(R.string.otc_sell_page));
            btn_shop_info.setText(getResources().getString(R.string.buyer_info));
            tv_num.setText(getResources().getString(R.string.sell_number));
            edit_num.setHint(getResources().getString(R.string.input_sell_number));
            tv_amount.setText(getResources().getString(R.string.sell_all_money));
            edit_amount.setHint(getResources().getString(R.string.input_sell_all_money));
            btn_sure.setText(getResources().getString(R.string.confirm_sell));
            ((QMUIRoundButtonDrawable) btn_sure.getBackground()).setBgData(ContextCompat.getColorStateList(this, R.color.color_otc_sell));
        }

        edit_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isInputNum = false;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isInputAmount && isInputAll && !TextUtils.isEmpty(s.toString().trim()) && allModel != null && allModel.orderModel != null && !TextUtils.isEmpty(allModel.orderModel.price)) {
                    try {
                        double value = MathHelper.mul(Double.parseDouble(s.toString()), Double.parseDouble(allModel.orderModel.price));
                        edit_amount.setText(FormatterUtils.getFormatRoundDown(2, value));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                isInputNum = true;
            }
        });
        edit_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isInputAmount = false;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isInputNum && isInputAll && !TextUtils.isEmpty(s.toString().trim()) && allModel != null && allModel.orderModel != null && !TextUtils.isEmpty(allModel.orderModel.price)) {
                    try {
                        double value = MathHelper.div(Double.parseDouble(s.toString()), Double.parseDouble(allModel.orderModel.price));
                        edit_num.setText(FormatterUtils.getFormatRoundDown(2, value));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                isInputAmount = true;
            }
        });
    }

    @Override
    protected OtcBuyOrSellPresenter createPresenter(OtcBuyOrSellPresenter mPresenter) {
        return new OtcBuyOrSellPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        upload();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (droidDialog != null && droidDialog.isShowing()) {
            droidDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_go_wx:
            case R.id.btn_go_zfb:
            case R.id.btn_go_yhk:
                droidDialog.dismiss();
                jumpTo(BindingPayInfoActivity.class);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            //支付类型 1银行卡 2微信 3支付宝
            switch (buttonView.getId()) {
                case R.id.cb_wx:
                    payType = 2;
                    cb_zfb.setChecked(false);
                    cb_yhk.setChecked(false);
                    break;
                case R.id.cb_zfb:
                    payType = 3;
                    cb_wx.setChecked(false);
                    cb_yhk.setChecked(false);
                    break;
                case R.id.cb_yhk:
                    payType = 1;
                    cb_wx.setChecked(false);
                    cb_zfb.setChecked(false);
                    break;
            }
        }
    }

    @OnClick(R.id.btn_shop_info)
    void goShopInfoClick() {
        jumpTo(OtcShopInfoActivity.createActivity(this, isBuy, allModel));
    }

    @OnClick(R.id.btn_num_all)
    void btnAllNumber() {
        if (allModel != null && allModel.orderModel != null && !TextUtils.isEmpty(allModel.orderModel.price) && !TextUtils.isEmpty(allModel.orderModel.remainAmount) && !TextUtils.isEmpty(allModel.orderModel.remainAmount)) {
            isInputAll = false;
            double remainAmountValue = MathHelper.mul(Double.parseDouble(allModel.orderModel.remainAmount), Double.parseDouble(allModel.orderModel.price));
            double value;
            if (remainAmountValue <= Double.parseDouble(allModel.orderModel.highLimit)) {
                edit_amount.setText(FormatterUtils.getFormatRoundDown(2, remainAmountValue));
                value = MathHelper.div(remainAmountValue, Double.parseDouble(allModel.orderModel.price));
                edit_num.setText(FormatterUtils.getFormatRoundDown(2, value));
            } else {
                edit_amount.setText(FormatterUtils.getFormatRoundDown(2, allModel.orderModel.highLimit));
                value = MathHelper.div(Double.parseDouble(allModel.orderModel.highLimit), Double.parseDouble(allModel.orderModel.price));
                edit_num.setText(FormatterUtils.getFormatRoundDown(2, value));
            }
            isInputAll = true;
        }
    }

    @OnClick(R.id.btn_num_amount)
    void btnAllmoney() {
        if (allModel != null && allModel.orderModel != null && !TextUtils.isEmpty(allModel.orderModel.price) && !TextUtils.isEmpty(allModel.orderModel.remainAmount) && !TextUtils.isEmpty(allModel.orderModel.remainAmount)) {
            isInputAll = false;
            double remainAmountValue = MathHelper.mul(Double.parseDouble(allModel.orderModel.remainAmount), Double.parseDouble(allModel.orderModel.price));
            double value;
            if (remainAmountValue <= Double.parseDouble(allModel.orderModel.highLimit)) {
                edit_amount.setText(FormatterUtils.getFormatRoundDown(2, remainAmountValue));
                value = MathHelper.div(remainAmountValue, Double.parseDouble(allModel.orderModel.price));
                edit_num.setText(FormatterUtils.getFormatRoundDown(2, value));
            } else {
                edit_amount.setText(FormatterUtils.getFormatRoundDown(2, allModel.orderModel.highLimit));
                value = MathHelper.div(Double.parseDouble(allModel.orderModel.highLimit), Double.parseDouble(allModel.orderModel.price));
                edit_num.setText(FormatterUtils.getFormatRoundDown(2, value));
            }
            isInputAll = true;
        }
    }

    @OnClick(R.id.btn_cancel)
    void cancelClick() {
        finish();
    }

    @OnClick(R.id.btn_sure)
    void confirmClick() {
        try {
            String num = edit_num.getText().toString();
            String amount = edit_amount.getText().toString();
            if (TextUtils.isEmpty(num)) {
                showToastError(getResources().getString(R.string.input_sell_number));
                return;
            }
            if (TextUtils.isEmpty(amount)) {
                showToastError(getResources().getString(R.string.sell_all_money));
                return;
            }
            if (allModel != null && allModel.orderModel != null) {
                if (TextUtils.isEmpty(allModel.orderModel.lowLimit) || Double.parseDouble(amount) < Double.parseDouble(allModel.orderModel.lowLimit)) {
                    showToastError(getResources().getString(R.string.exceeding_min_limit));
                    return;
                }
                if (TextUtils.isEmpty(allModel.orderModel.highLimit) || Double.parseDouble(amount) > Double.parseDouble(allModel.orderModel.highLimit)) {
                    showToastError(getResources().getString(R.string.exceeding_max_limit));
                    return;
                }
            }

            chooseType(FormatterUtils.getFormatRoundDown(2, num), FormatterUtils.getFormatRoundDown(2, amount));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void otcAmount(OtcAmountModel model) {
        if (model != null) {
            tv_account_balance.setText(FormatterUtils.getFormatValue(model.cashAmount) + Constant.ACU_CURRENCY_NAME);
        }
    }

    @Override
    public void orderListOne(OtcMarketOrderAllModel model) {
        this.allModel = model;
        setOrderInfo();
    }

    @Override
    public void selectBank(List<PayInfoModel> list) {
        UserManageUtils.setPeoplePayInfo(list);
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).type != null && list.get(i).type == 1) {
                    yhkModel = list.get(i);
                }
                if (list.get(i).type != null && list.get(i).type == 2) {
                    wxModel = list.get(i);
                }
                if (list.get(i).type != null && list.get(i).type == 3) {
                    zfbModel = list.get(i);
                }
            }
        } else {
            yhkModel = null;
            wxModel = null;
            zfbModel = null;
        }
    }

    @Override
    public void otcTradeSuccess() {
        finish();
        jumpTo(OtcOrderListActivity.class);
    }

    private void upload() {
        mPresenter.otcAmount(Constant.ACU_CURRENCY_ID);
        mPresenter.orderListOne(orderId);
        mPresenter.selectBank();
    }

    public void setOrderInfo() {
        if (allModel != null) {
            OtcMarketInfoModel infoModel = allModel.infoModel;
            GlideUtils.disPlay(this, CommonUtils.getHead(infoModel.headImg), img_people);
            tv_people_nickname.setText(infoModel.nickname + "(" + CommonUtils.nameXing(infoModel.secondName) + ")");
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

            OtcMarketOrderModel orderModel = allModel.orderModel;
            String valueWei = " " + Constant.CNY;
            if (orderModel.money != null && orderModel.money == 1) {
                valueWei = " " + Constant.CNY;
            }
            tv_limit.setText(getResources().getString(R.string.quota) + orderModel.lowLimit + "-" + orderModel.highLimit + valueWei);
        }
    }

    private void chooseType(final String num, final String amount) {
        View view = View.inflate(this, R.layout.dialog_choose_money_type, null);
        ConstraintLayout view_wx = view.findViewById(R.id.view_wx);
        ConstraintLayout view_zfb = view.findViewById(R.id.view_zfb);
        ConstraintLayout view_yhk = view.findViewById(R.id.view_yhk);
        TextView tv_wx_account = view.findViewById(R.id.tv_wx_account);
        TextView tv_zfb_account = view.findViewById(R.id.tv_zfb_account);
        TextView tv_yhk_account = view.findViewById(R.id.tv_yhk_account);
        cb_wx = view.findViewById(R.id.cb_wx);
        cb_zfb = view.findViewById(R.id.cb_zfb);
        cb_yhk = view.findViewById(R.id.cb_yhk);
        QMUIAlphaButton btn_go_wx = view.findViewById(R.id.btn_go_wx);
        QMUIAlphaButton btn_go_zfb = view.findViewById(R.id.btn_go_zfb);
        QMUIAlphaButton btn_go_yhk = view.findViewById(R.id.btn_go_yhk);

        view_wx.setVisibility(View.GONE);
        view_zfb.setVisibility(View.GONE);
        view_yhk.setVisibility(View.GONE);

        cb_wx.setOnCheckedChangeListener(this);
        cb_zfb.setOnCheckedChangeListener(this);
        cb_yhk.setOnCheckedChangeListener(this);

        btn_go_wx.setOnClickListener(this);
        btn_go_zfb.setOnClickListener(this);
        btn_go_yhk.setOnClickListener(this);

        if (allModel != null) {
            OtcMarketOrderModel orderModel = allModel.orderModel;
            if (orderModel != null) {
                if (orderModel.payByCard == 1) {
                    view_yhk.setVisibility(View.VISIBLE);
                    if (yhkModel != null) {
                        cb_yhk.setVisibility(View.VISIBLE);
                        btn_go_yhk.setVisibility(View.GONE);
                        tv_yhk_account.setText(CommonUtils.hideCardNo(yhkModel.bankCard));
                    } else {
                        cb_yhk.setVisibility(View.GONE);
                        btn_go_yhk.setVisibility(View.VISIBLE);
                        tv_yhk_account.setText("");
                    }
                }
                if (orderModel.payWechat == 1) {
                    view_wx.setVisibility(View.VISIBLE);
                    if (wxModel != null) {
                        cb_wx.setVisibility(View.VISIBLE);
                        btn_go_wx.setVisibility(View.GONE);
                        tv_wx_account.setText(CommonUtils.hidePhoneNo(wxModel.weChatNo));
                    } else {
                        cb_wx.setVisibility(View.GONE);
                        btn_go_wx.setVisibility(View.VISIBLE);
                        tv_wx_account.setText("");
                    }
                }
                if (orderModel.payAlipay == 1) {
                    view_zfb.setVisibility(View.VISIBLE);
                    if (zfbModel != null) {
                        cb_zfb.setVisibility(View.VISIBLE);
                        btn_go_zfb.setVisibility(View.GONE);
                        tv_zfb_account.setText(CommonUtils.hidePhoneNo(zfbModel.aliPayNo));
                    } else {
                        cb_zfb.setVisibility(View.GONE);
                        btn_go_zfb.setVisibility(View.VISIBLE);
                        tv_zfb_account.setText("");
                    }
                }
            }
        }

        payType = null;
        String titleString = "";
        if (isBuy) {
            titleString = getResources().getString(R.string.choose_paymoney_type);
        } else {
            titleString = getResources().getString(R.string.choose_getmoney_type);
        }
        droidDialog = new DroidDialog.Builder(this)
                .title(titleString)
                .viewCustomLayout(view)
                .positiveButton(getResources().getString(R.string.sure), false, new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        if (payType == null || payType == 0) {
                            showToastError(getResources().getString(R.string.please_choose_get_pay));
                            return;
                        }
                        droidDialog.dismiss();
                        mPresenter.otcTrade(allModel.orderModel.id, payType, num, amount);
                    }
                })
                .setShowImgClose(true)
                .cancelable(false, false)
                .show();
    }
}
