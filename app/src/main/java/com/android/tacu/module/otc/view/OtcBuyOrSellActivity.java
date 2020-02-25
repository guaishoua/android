package com.android.tacu.module.otc.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.common.TabIndicatorAdapter;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.otc.contract.OtcBuyOrSellContract;
import com.android.tacu.module.otc.model.OtcMarketOrderAllModel;
import com.android.tacu.module.otc.model.OtcMarketOrderModel;
import com.android.tacu.module.otc.presenter.OtcBuyOrSellPresenter;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.user.UserManageUtils;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class OtcBuyOrSellActivity extends BaseOtcHalfOrderActvity<OtcBuyOrSellPresenter> implements OtcBuyOrSellContract.IView {

    @BindView(R.id.base_scrollIndicatorView)
    ScrollIndicatorView payIndicatorView;

    @BindView(R.id.tv_plan_buy_amount_title)
    TextView tv_plan_buy_amount_title;
    @BindView(R.id.tv_remaining_unsold_title)
    TextView tv_remaining_unsold_title;
    @BindView(R.id.tv_remaining_all_title)
    TextView tv_remaining_all_title;

    @BindView(R.id.tv_single_price)
    TextView tv_single_price;
    @BindView(R.id.tv_plan_buy_amount)
    TextView tv_plan_buy_amount;
    @BindView(R.id.tv_remaining_unsold)
    TextView tv_remaining_unsold;
    @BindView(R.id.tv_remaining_all)
    TextView tv_remaining_all;
    @BindView(R.id.tv_single_min_limit)
    TextView tv_single_min_limit;
    @BindView(R.id.tv_single_max_limit)
    TextView tv_single_max_limit;
    @BindView(R.id.tv_seller_coined_time_limit)
    TextView tv_seller_coined_time_limit;

    @BindView(R.id.img_wx)
    ImageView img_wx;
    @BindView(R.id.img_zfb)
    ImageView img_zfb;
    @BindView(R.id.img_yhk)
    ImageView img_yhk;

    @BindView(R.id.tv_receiving_account)
    TextView tv_receiving_account;
    @BindView(R.id.img_pay)
    QMUIRadiusImageView img_pay;
    @BindView(R.id.tv_account_balance)
    TextView tv_account_balance;
    @BindView(R.id.tv_sell_number_title)
    TextView tv_sell_number_title;
    @BindView(R.id.edit_sell_number)
    EditText edit_sell_number;
    @BindView(R.id.tv_sell_allmoney_title)
    TextView tv_sell_allmoney_title;
    @BindView(R.id.edit_sell_allmoney)
    EditText edit_sell_allmoney;

    @BindView(R.id.lin_trade_pwd)
    LinearLayout lin_trade_pwd;
    @BindView(R.id.edit_trade_password)
    QMUIRoundEditText edit_trade_password;

    private int selectPayInfo = 0;//0=支付宝 1=微信 2=银行卡
    private boolean isBuy = true;
    private String orderId;
    private List<String> tabTitle = new ArrayList<>();

    private OtcMarketOrderAllModel allModel;
    private PayInfoModel yhkModel = null, wxModel = null, zfbModel = null;
    private String wxImage = null, zfbImage = null;
    //防止EditText和SeekBar死循环
    private boolean isInput = true;

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
            ((QMUIRoundButtonDrawable) rl_people_bg.getBackground()).setBgData(ContextCompat.getColorStateList(this, R.color.color_otc_buy_bg));
            tv_plan_buy_amount_title.setText(getResources().getString(R.string.all_num));
            tv_remaining_unsold_title.setText(getResources().getString(R.string.remind_num));
            tv_remaining_all_title.setText(getResources().getString(R.string.remind_all));
            tv_sell_number_title.setText(getResources().getString(R.string.buy_number));
            edit_sell_number.setHint(getResources().getString(R.string.input_buy_number));
            tv_sell_allmoney_title.setText(getResources().getString(R.string.buy_all_money));
            edit_sell_allmoney.setHint(getResources().getString(R.string.input_buy_all_money));
        } else {
            mTopBar.setTitle(getResources().getString(R.string.otc_sell_page));
            ((QMUIRoundButtonDrawable) rl_people_bg.getBackground()).setBgData(ContextCompat.getColorStateList(this, R.color.color_otc_sell_bg));
            tv_plan_buy_amount_title.setText(getResources().getString(R.string.plan_buy_amount));
            tv_remaining_unsold_title.setText(getResources().getString(R.string.remaining_unsold));
            tv_remaining_all_title.setText(getResources().getString(R.string.remaining_unsold_all));
            tv_sell_number_title.setText(getResources().getString(R.string.sell_number));
            edit_sell_number.setHint(getResources().getString(R.string.input_sell_number));
            tv_sell_allmoney_title.setText(getResources().getString(R.string.sell_all_money));
            edit_sell_allmoney.setHint(getResources().getString(R.string.input_sell_all_money));
        }

        tabTitle.add(getResources().getString(R.string.zhifubao));
        tabTitle.add(getResources().getString(R.string.weixin));
        tabTitle.add(getResources().getString(R.string.yinhanngka));

        payIndicatorView.setBackgroundColor(ContextCompat.getColor(this, R.color.tab_bg_color));
        payIndicatorView.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(this, R.color.text_default), ContextCompat.getColor(this, R.color.tab_text_color)).setSize(14, 14));
        payIndicatorView.setScrollBar(new ColorBar(this, ContextCompat.getColor(this, R.color.text_default), 4));
        payIndicatorView.setSplitAuto(true);
        payIndicatorView.setAdapter(new TabIndicatorAdapter(this, tabTitle));
        payIndicatorView.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
                selectPayInfo = select;
                if (select == 0) {
                    if (zfbModel != null && TextUtils.isEmpty(zfbImage)) {
                        mPresenter.uselectUserInfo(0, zfbModel.aliPayImg);
                    }
                } else if (select == 1) {
                    if (wxModel != null && TextUtils.isEmpty(wxImage)) {
                        mPresenter.uselectUserInfo(1, wxModel.weChatImg);
                    }
                }
                dealPayInfo();
            }
        });

        edit_sell_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isInput = false;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isInput && !TextUtils.isEmpty(s.toString().trim()) && allModel != null && allModel.orderModel != null && !TextUtils.isEmpty(allModel.orderModel.price)) {
                    try {
                        edit_sell_allmoney.setText(String.valueOf(Double.parseDouble(s.toString()) * Double.parseDouble(allModel.orderModel.price)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                isInput = true;
            }
        });
        edit_sell_allmoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isInput = false;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isInput && !TextUtils.isEmpty(s.toString().trim()) && allModel != null && allModel.orderModel != null && !TextUtils.isEmpty(allModel.orderModel.price)) {
                    try {
                        edit_sell_number.setText(String.valueOf(Double.parseDouble(s.toString()) / Double.parseDouble(allModel.orderModel.price)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                isInput = true;
            }
        });
        if (spUtil.getPwdVisibility()) {
            lin_trade_pwd.setVisibility(View.VISIBLE);
        } else {
            lin_trade_pwd.setVisibility(View.GONE);
        }
    }

    @Override
    protected OtcBuyOrSellPresenter createPresenter(OtcBuyOrSellPresenter mPresenter) {
        return new OtcBuyOrSellPresenter();
    }

    @Override
    protected void onPresenterCreated(OtcBuyOrSellPresenter presenter) {
        super.onPresenterCreated(presenter);

        mPresenter.selectBank();
        mPresenter.otcAmount(Constant.ACU_CURRENCY_ID);
        mPresenter.orderListOne(orderId);
    }

    @OnClick(R.id.btn_all_number)
    void btnAllNumber() {
        if (allModel != null && allModel.orderModel != null && !TextUtils.isEmpty(allModel.orderModel.price) && !TextUtils.isEmpty(allModel.orderModel.remainAmount) && !TextUtils.isEmpty(allModel.orderModel.remainAmount)) {
            double highLimitNum = Double.parseDouble(allModel.orderModel.highLimit) / Double.parseDouble(allModel.orderModel.price);
            if (highLimitNum <= Double.parseDouble(allModel.orderModel.remainAmount)) {
                edit_sell_number.setText(FormatterUtils.getFormatRoundDown(2, highLimitNum));
            } else {
                edit_sell_number.setText(FormatterUtils.getFormatRoundDown(2, allModel.orderModel.remainAmount));
            }
        }
    }

    @OnClick(R.id.btn_allmoney)
    void btnAllmoney() {
        if (allModel != null && allModel.orderModel != null) {
            double reminALL = Double.parseDouble(allModel.orderModel.remainAmount) * Double.parseDouble(allModel.orderModel.price);
            if (reminALL <= Double.parseDouble(allModel.orderModel.highLimit)) {
                edit_sell_allmoney.setText(FormatterUtils.getFormatRoundDown(2, reminALL));
            } else {
                edit_sell_allmoney.setText(FormatterUtils.getFormatRoundDown(2, allModel.orderModel.highLimit));
            }
        }
    }

    @OnClick(R.id.btn_confirm)
    void confirmClick() {
        String num = edit_sell_number.getText().toString();
        String amount = edit_sell_allmoney.getText().toString();
        String pwd = edit_trade_password.getText().toString();
        if (TextUtils.isEmpty(num)) {
            showToastError(getResources().getString(R.string.input_sell_number));
            return;
        }
        if (TextUtils.isEmpty(amount)) {
            showToastError(getResources().getString(R.string.sell_all_money));
            return;
        }
        if (spUtil.getPwdVisibility() && TextUtils.isEmpty(pwd)) {
            showToastError(getResources().getString(R.string.please_input_trade_password));
            return;
        }
        jumpTo(OtcOrderCreateActivity.createActivity(this, isBuy, pwd, FormatterUtils.getFormatRoundDown(2, num), FormatterUtils.getFormatRoundDown(2, amount), allModel));
    }

    @OnClick(R.id.btn_return)
    void returnClick() {
        finish();
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
        dealPayInfo();
    }

    @Override
    public void uselectUserInfo(Integer type, String imageUrl) {
        switch (type) {
            case 1://微信
                wxImage = imageUrl;
                break;
            case 0://支付宝
                zfbImage = imageUrl;
                break;
        }
        dealPayInfo();
    }

    @Override
    public void otcAmount(OtcAmountModel model) {
        if (model != null) {
            tv_account_balance.setText(FormatterUtils.getFormatValue(model.cashAmount) + Constant.OTC_ACU);
        }
    }

    @Override
    public void orderListOne(OtcMarketOrderAllModel model) {
        this.allModel = model;
        if (allModel != null && allModel.infoModel != null) {
            setInfoValue(allModel.infoModel);
        }
        setOrderInfo();
    }

    public void setOrderInfo() {
        if (allModel != null) {
            OtcMarketOrderModel orderModel = allModel.orderModel;
            String valueWei = "CNY";
            if (orderModel.money != null && orderModel.money == 1) {
                valueWei = " CNY";
            }
            tv_single_price.setText(orderModel.price + valueWei);
            tv_plan_buy_amount.setText(orderModel.num + " " + orderModel.currencyName);
            tv_remaining_unsold.setText(orderModel.remainAmount + " " + orderModel.currencyName);
            if (!TextUtils.isEmpty(orderModel.price) && !TextUtils.isEmpty(orderModel.remainAmount)) {
                tv_remaining_all.setText(FormatterUtils.getFormatRoundDown(2, Double.parseDouble(orderModel.price) * Double.parseDouble(orderModel.remainAmount)) + valueWei);
            }
            tv_single_min_limit.setText(orderModel.lowLimit + valueWei);
            tv_single_max_limit.setText(orderModel.highLimit + valueWei);

            if (orderModel.payByCard != null && orderModel.payByCard == 1) {
                img_yhk.setVisibility(View.VISIBLE);
            } else {
                img_yhk.setVisibility(View.GONE);
            }
            if (orderModel.payWechat != null && orderModel.payWechat == 1) {
                img_wx.setVisibility(View.VISIBLE);
            } else {
                img_wx.setVisibility(View.GONE);
            }
            if (orderModel.payAlipay != null && orderModel.payAlipay == 1) {
                img_zfb.setVisibility(View.VISIBLE);
            } else {
                img_zfb.setVisibility(View.GONE);
            }
            tv_seller_coined_time_limit.setText(orderModel.timeOut + " min");
        }
    }

    private void dealPayInfo() {
        //0=支付宝 1=微信 2=银行卡
        switch (selectPayInfo) {
            case 0:
                if (zfbModel != null) {
                    tv_receiving_account.setText(zfbModel.aliPayNo);
                    img_pay.setImageResource(0);
                    if (!TextUtils.isEmpty(zfbImage)) {
                        img_pay.setVisibility(View.VISIBLE);
                        GlideUtils.disPlay(this, zfbImage, img_pay);
                    } else {
                        img_pay.setVisibility(View.GONE);
                    }
                } else {
                    tv_receiving_account.setText("");
                    img_pay.setVisibility(View.GONE);
                }
                break;
            case 1:
                if (wxModel != null) {
                    tv_receiving_account.setText(wxModel.weChatNo);
                    img_pay.setImageResource(0);
                    if (!TextUtils.isEmpty(wxImage)) {
                        img_pay.setVisibility(View.VISIBLE);
                        GlideUtils.disPlay(this, wxImage, img_pay);
                    } else {
                        img_pay.setVisibility(View.GONE);
                    }
                    GlideUtils.disPlay(this, wxImage, img_pay);
                } else {
                    tv_receiving_account.setText("");
                    img_pay.setVisibility(View.GONE);
                }
                break;
            case 2:
                img_pay.setVisibility(View.GONE);
                if (yhkModel != null) {
                    tv_receiving_account.setText(yhkModel.bankCard);
                } else {
                    tv_receiving_account.setText("");
                }
                break;
        }
    }
}
