package com.android.tacu.module.otc.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.payinfo.model.PayInfoModel;
import com.android.tacu.module.otc.contract.OtcBuyOrSellContract;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcMarketOrderAllModel;
import com.android.tacu.module.otc.model.OtcMarketOrderModel;
import com.android.tacu.module.otc.presenter.OtcBuyOrSellPresenter;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.MathHelper;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.utils.user.UserManageUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class OtcBuyOrSellActivity extends BaseActivity<OtcBuyOrSellPresenter> implements OtcBuyOrSellContract.IView {

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

    private boolean isBuy = true;
    private String orderId;

    private OtcMarketOrderAllModel allModel;
    //防止EditText和EditText死循环
    private boolean isInputNum = true;
    private boolean isInputAmount = true;
    private boolean isInputAll = true;

    private Dialog dialog;
    private List<PayInfoModel> selectBankList = new ArrayList<>();
    private PayInfoAdapter adapter;
    private Integer payId;

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
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
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

            if (isBuy) {
                mPresenter.selectMerchantBank(String.valueOf(allModel.orderModel.uid), FormatterUtils.getFormatRoundDown(2, num), FormatterUtils.getFormatRoundDown(2, amount));
            } else {
                mPresenter.selectBank(FormatterUtils.getFormatRoundDown(2, num), FormatterUtils.getFormatRoundDown(2, amount));
            }
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
    public void selectBank(List<PayInfoModel> list, String num, String amount) {
        UserManageUtils.setPeoplePayInfo(list);
        showSure(list, num, amount);
    }

    @Override
    public void selectMerchantBank(List<PayInfoModel> list, String num, String amount) {
        showSure(list, num, amount);
    }

    @Override
    public void otcTradeSuccess() {
        finish();
        jumpTo(OtcOrderListActivity.class);
    }

    private void upload() {
        mPresenter.otcAmount(Constant.ACU_CURRENCY_ID);
        mPresenter.orderListOne(orderId);
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
            tv_limit.setText(getResources().getString(R.string.quota) + FormatterUtils.getFormatValue(orderModel.lowLimit) + "-" + FormatterUtils.getFormatValue(orderModel.highLimit) + valueWei);
        }
    }

    private void showSure(List<PayInfoModel> pays, final String num, final String amount) {
        if (pays == null || pays.size() <= 0 || allModel == null) {
            return;
        }

        payId = null;
        selectBankList.clear();
        for (PayInfoModel pay : pays) {
            if (allModel.orderModel.payByCard != null && allModel.orderModel.payByCard == 1) {
                if (pay.type != null && pay.type == 1 && pay.status == 1) {
                    selectBankList.add(pay);
                }
            }
            if (allModel.orderModel.payWechat != null && allModel.orderModel.payWechat == 1) {
                if (pay.type != null && pay.type == 2 && pay.status == 1) {
                    selectBankList.add(pay);
                }
            }
            if (allModel.orderModel.payAlipay != null && allModel.orderModel.payAlipay == 1) {
                if (pay.type != null && pay.type == 3 && pay.status == 1) {
                    selectBankList.add(pay);
                }
            }
        }

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_payinfo, null);

        ImageView img_close = view.findViewById(R.id.img_close);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        QMUIRoundButton btn_sure = view.findViewById(R.id.btn_sure);

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (payId == null) {
                    showToastError(getResources().getString(R.string.please_select_getmoney_type));
                    return;
                }
                dialog.dismiss();
                mPresenter.otcTrade(allModel.orderModel.id, payId, num, amount);
            }
        });

        adapter = new PayInfoAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setNewData(selectBankList);

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.width = UIUtils.getScreenWidth();
        //判断高度
        int size = 0;
        if (selectBankList != null && selectBankList.size() > 0) {
            size = selectBankList.size();
        }
        if (UIUtils.dp2px(50) * size >= UIUtils.getScreenHeight() / 2) {
            params.height = UIUtils.getScreenHeight() / 2;
        } else {
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        params.gravity = Gravity.BOTTOM | Gravity.CENTER;
        dialog.getWindow().setBackgroundDrawableResource(R.color.color_transparent);
        dialog.getWindow().setAttributes(params);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public class PayInfoAdapter extends BaseQuickAdapter<PayInfoModel, BaseViewHolder> {

        public PayInfoAdapter() {
            super(R.layout.item_payinfo);
        }

        @Override
        protected void convert(final BaseViewHolder holder, final PayInfoModel item) {
            if (item.type == 1) {
                holder.setBackgroundRes(R.id.img_pay, R.mipmap.img_yhk);
                holder.setText(R.id.tv_pay, getResources().getString(R.string.yinhanngka));
                holder.setText(R.id.tv_pay_account, CommonUtils.hideCardNo(item.bankCard));
            } else if (item.type == 2) {
                holder.setBackgroundRes(R.id.img_pay, R.mipmap.img_wx);
                holder.setText(R.id.tv_pay, getResources().getString(R.string.weixin));
                holder.setText(R.id.tv_pay_account, CommonUtils.hidePhoneNo(item.weChatNo));
            } else if (item.type == 3) {
                holder.setBackgroundRes(R.id.img_pay, R.mipmap.img_zfb);
                holder.setText(R.id.tv_pay, getResources().getString(R.string.zhifubao));
                holder.setText(R.id.tv_pay_account, CommonUtils.hidePhoneNo(item.aliPayNo));
            }

            ((CheckBox) holder.getView(R.id.cb)).setChecked(item.isCB);

            holder.setOnClickListener(R.id.cb, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyCheckbox(item.id);
                }
            });
        }
    }

    private void notifyCheckbox(int id) {
        payId = id;
        if (selectBankList != null && selectBankList.size() > 0) {
            for (int i = 0; i < selectBankList.size(); i++) {
                if (selectBankList.get(i).id == id) {
                    selectBankList.get(i).isCB = true;
                } else {
                    selectBankList.get(i).isCB = false;
                }
            }
            adapter.notifyDataSetChanged();
        }
    }
}
