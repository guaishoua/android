package com.android.tacu.module.otc.view;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.otc.contract.OtcManageBuySellContract;
import com.android.tacu.module.otc.model.OtcPublishParam;
import com.android.tacu.module.otc.model.OtcSelectFeeModel;
import com.android.tacu.module.otc.presenter.OtcManageBuySellPresenter;
import com.android.tacu.module.vip.view.RechargeDepositActivity;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.widget.popupwindow.ListPopWindow;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class OtcManageBuySellFragment extends BaseFragment<OtcManageBuySellPresenter> implements OtcManageBuySellContract.IChildView {

    @BindView(R.id.tv_trade_type)
    TextView tv_trade_type;
    @BindView(R.id.tv_trade_money)
    TextView tv_trade_money;
    @BindView(R.id.tv_order_operation_time_limit)
    TextView tv_order_operation_time_limit;

    @BindView(R.id.tv_trade_single_price_tag)
    TextView tv_trade_single_price_tag;
    @BindView(R.id.tv_trade_num_tag)
    TextView tv_trade_num_tag;
    @BindView(R.id.tv_trade_all_price_tag)
    TextView tv_trade_all_price_tag;
    @BindView(R.id.tv_min_limit_tag)
    TextView tv_min_limit_tag;
    @BindView(R.id.tv_max_limit_tag)
    TextView tv_max_limit_tag;

    @BindView(R.id.cb_zhifubao)
    CheckBox cb_zhifubao;
    @BindView(R.id.cb_weixin)
    CheckBox cb_weixin;
    @BindView(R.id.cb_yinhangka)
    CheckBox cb_yinhangka;

    @BindView(R.id.edit_trade_single_price)
    EditText edit_trade_single_price;
    @BindView(R.id.edit_trade_num)
    EditText edit_trade_num;
    @BindView(R.id.edit_min_limit)
    EditText edit_min_limit;
    @BindView(R.id.edit_max_limit)
    EditText edit_max_limit;
    @BindView(R.id.tv_trade_all_price)
    TextView tv_trade_all_price;

    @BindView(R.id.tv_deal_fee)
    TextView tv_deal_fee;
    @BindView(R.id.tv_purchase_deposit)
    TextView tv_purchase_deposit;
    @BindView(R.id.tv_margin_balance)
    TextView tv_margin_balance;
    @BindView(R.id.tv_otc_balance)
    TextView tv_otc_balance;

    @BindView(R.id.edit_ask)
    EditText edit_ask;
    @BindView(R.id.et_pwd)
    QMUIRoundEditText et_pwd;
    @BindView(R.id.cb_xieyi)
    CheckBox cb_xieyi;
    @BindView(R.id.tv_xieyi)
    TextView tv_xieyi;

    private ListPopWindow coinPopup;
    private ListPopWindow moneyPopup;
    private ListPopWindow timeLimitPopup;

    private boolean isBuy = true; //默认true=买
    private OtcPublishParam param;
    private OtcSelectFeeModel otcSelectFeeModel;
    private String coinName = Constant.OTC_ACU;
    private PayInfoModel yhkModel = null, wxModel = null, zfbModel = null;

    public static OtcManageBuySellFragment newInstance(boolean isBuy) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isBuy", isBuy);
        OtcManageBuySellFragment fragment = new OtcManageBuySellFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initLazy() {
        super.initLazy();
        upload();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            isBuy = bundle.getBoolean("isBuy", true);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_otc_manage_buysell;
    }

    @Override
    protected void initData(View view) {
        param = new OtcPublishParam(isBuy);
        tv_xieyi.setText(Html.fromHtml(getResources().getString(R.string.otc_xieyi) + "<font color=" + ContextCompat.getColor(getContext(), R.color.color_otc_unhappy) + ">" + getResources().getString(R.string.otc_xieyi_name) + "</font>"));

        edit_trade_single_price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (!TextUtils.isEmpty(s.toString()) && !TextUtils.isEmpty(edit_trade_num.getText().toString())) {
                        tv_trade_all_price.setText(FormatterUtils.getFormatValue(Double.valueOf(s.toString()) * Double.valueOf(edit_trade_num.getText().toString())));
                        if (otcSelectFeeModel != null) {
                            if (isBuy) {
                                tv_purchase_deposit.setText(FormatterUtils.getFormatRoundDown(2, Double.valueOf(tv_trade_all_price.getText().toString()) * otcSelectFeeModel.buyFee) + " " + coinName);
                            } else {
                                tv_purchase_deposit.setText(FormatterUtils.getFormatRoundDown(2, Double.valueOf(tv_trade_all_price.getText().toString()) * otcSelectFeeModel.sellFee) + " " + coinName);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        edit_trade_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (!TextUtils.isEmpty(s.toString()) && !TextUtils.isEmpty(edit_trade_single_price.getText().toString())) {
                        tv_trade_all_price.setText(FormatterUtils.getFormatValue(Double.valueOf(s.toString()) * Double.valueOf(edit_trade_single_price.getText().toString())));
                        if (otcSelectFeeModel != null) {
                            if (isBuy) {
                                tv_purchase_deposit.setText(FormatterUtils.getFormatValue(Double.valueOf(tv_trade_all_price.getText().toString()) * otcSelectFeeModel.buyFee) + " " + coinName);
                            } else {
                                tv_purchase_deposit.setText(FormatterUtils.getFormatValue(Double.valueOf(tv_trade_all_price.getText().toString()) * otcSelectFeeModel.sellFee) + " " + coinName);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (!spUtil.getPwdVisibility()) {
            et_pwd.setVisibility(View.GONE);
        }
    }

    @Override
    protected OtcManageBuySellPresenter createPresenter(OtcManageBuySellPresenter mPresenter) {
        return new OtcManageBuySellPresenter();
    }

    @Override
    protected void onPresenterCreated(OtcManageBuySellPresenter mPresenter) {
        super.onPresenterCreated(mPresenter);
        upload();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (coinPopup != null && coinPopup.isShowing()) {
            coinPopup.dismiss();
            coinPopup = null;
        }
        if (moneyPopup != null && moneyPopup.isShowing()) {
            moneyPopup.dismiss();
            moneyPopup = null;
        }
        if (timeLimitPopup != null && timeLimitPopup.isShowing()) {
            timeLimitPopup.dismiss();
            timeLimitPopup = null;
        }
    }

    @OnClick(R.id.tv_trade_type)
    void tradeTypeClick() {
        showCoinType();
    }

    @OnClick(R.id.tv_trade_money)
    void tradeMoneyClick() {
        showMoneyType();
    }

    @OnClick(R.id.tv_order_operation_time_limit)
    void timeLimitClick() {
        showTimeType();
    }

    @OnClick(R.id.btn_recharge)
    void rechargeClick() {
        jumpTo(RechargeDepositActivity.class);
    }

    @OnClick(R.id.btn_confirm)
    void confirmClick() {
        if (!cb_yinhangka.isChecked() && !cb_weixin.isChecked() && !cb_zhifubao.isChecked()) {
            showToastError(getResources().getString(R.string.please_choose_pay_method));
            return;
        }
        if (cb_yinhangka.isChecked() && yhkModel == null) {
            showToastError(getResources().getString(R.string.no_pay_tip));
            return;
        }
        if (cb_weixin.isChecked() && wxModel == null) {
            showToastError(getResources().getString(R.string.no_pay_tip));
            return;
        }
        if (cb_zhifubao.isChecked() && zfbModel == null) {
            showToastError(getResources().getString(R.string.no_pay_tip));
            return;
        }
        if (cb_yinhangka.isChecked()) {
            param.payByCard = 1;
        } else {
            param.payByCard = 0;
        }
        if (cb_weixin.isChecked()) {
            param.payWechat = 1;
        } else {
            param.payWechat = 0;
        }
        if (cb_zhifubao.isChecked()) {
            param.payAlipay = 1;
        } else {
            param.payAlipay = 0;
        }

        String price = edit_trade_single_price.getText().toString();
        String num = edit_trade_num.getText().toString();
        String min = edit_min_limit.getText().toString();
        String max = edit_max_limit.getText().toString();
        String explain = edit_ask.getText().toString();
        String pwd = et_pwd.getText().toString();

        if (TextUtils.isEmpty(price)) {
            showToastError(getResources().getString(R.string.please_input_trade_single_price));
            return;
        }
        if (TextUtils.isEmpty(num)) {
            showToastError(getResources().getString(R.string.please_input_trade_num));
            return;
        }
        if (spUtil.getPwdVisibility() && TextUtils.isEmpty(pwd)) {
            showToastError(getResources().getString(R.string.hint_assets_take_coin_enter_pwd));
            return;
        }
        if (!cb_xieyi.isChecked()) {
            showToastError(getResources().getString(R.string.please_check_xieyi));
            return;
        }
        param.price = price;
        param.num = num;
        param.amount = tv_trade_all_price.getText().toString();
        param.lowLimit = min;
        param.highLimit = max;
        param.explain = explain;
        if (spUtil.getPwdVisibility()) {
            param.fdPassword = Md5Utils.encryptFdPwd(pwd, spUtil.getUserUid()).toLowerCase();
        } else {
            param.fdPassword = null;
        }
        mPresenter.order(param);
    }

    @OnClick(R.id.btn_cancel)
    void cancelClick() {
        getHostActivity().finish();
    }

    @Override
    public void selectFee(OtcSelectFeeModel model) {
        this.otcSelectFeeModel = model;
        if (model != null) {
            if (isBuy) {
                if (model.buyType != null && model.buyFee != null) {
                    tv_deal_fee.setText((model.buyType == 2 ? BigDecimal.valueOf(model.buyFee * 100).toPlainString() + "%" : BigDecimal.valueOf(model.buyFee).toPlainString()));
                }
            } else {
                if (model.sellType != null && model.sellFee != null) {
                    tv_deal_fee.setText((model.sellType == 2 ? BigDecimal.valueOf(model.sellFee * 100).toPlainString() + "%" : BigDecimal.valueOf(model.sellFee).toPlainString()));
                }
            }
        }
    }

    @Override
    public void BondAccount(OtcAmountModel model) {
        if (model != null) {
            if (!TextUtils.isEmpty(model.cashAmount)) {
                tv_margin_balance.setText(FormatterUtils.getFormatValue(model.cashAmount) + " " + coinName);
            } else {
                tv_margin_balance.setText("0 " + coinName);
            }
        }
    }

    @Override
    public void OtcAccount(OtcAmountModel model) {
        if (model != null) {
            tv_otc_balance.setText(FormatterUtils.getFormatValue(model.cashAmount) + Constant.OTC_ACU);
        }
    }

    @Override
    public void orderSuccess() {
        showToastSuccess(getResources().getString(R.string.success));
        getHostActivity().finish();
    }

    public void setPayList(List<PayInfoModel> list) {
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

    private void upload() {
        if (isVisibleToUser) {
            mPresenter.selectFee(param.currencyId);
            mPresenter.BondAccount(param.currencyId);
            mPresenter.OtcAccount(param.currencyId);
        }
    }

    private void showCoinType() {
        if (coinPopup != null && coinPopup.isShowing()) {
            coinPopup.dismiss();
            return;
        }
        if (coinPopup == null) {
            final List<String> data = new ArrayList<>();
            final List<Integer> dataId = new ArrayList<>();
            data.add(Constant.OTC_ACU);
            dataId.add(Constant.ACU_CURRENCY_ID);
            ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.simple_list_item, data);
            coinPopup = new ListPopWindow(getContext(), adapter);
            coinPopup.create(UIUtils.dp2px(100), UIUtils.dp2px(40), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tv_trade_type.setText(data.get(position));
                    tv_trade_num_tag.setText(data.get(position));
                    param.currencyId = dataId.get(position);
                    coinName = data.get(position);
                    upload();
                    coinPopup.dismiss();
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            coinPopup.setDropDownGravity(Gravity.END);
        }
        coinPopup.setAnchorView(tv_trade_type);
        coinPopup.show();
    }

    private void showMoneyType() {
        if (moneyPopup != null && moneyPopup.isShowing()) {
            moneyPopup.dismiss();
            return;
        }
        if (moneyPopup == null) {
            final List<String> data = new ArrayList<>();
            final List<Integer> dataId = new ArrayList<>();
            data.add(getResources().getString(R.string.renminbi));
            dataId.add(1);
            ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.simple_list_item, data);
            moneyPopup = new ListPopWindow(getContext(), adapter);
            moneyPopup.create(UIUtils.dp2px(120), UIUtils.dp2px(40), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tv_trade_money.setText(data.get(position));
                    tv_trade_single_price_tag.setText(data.get(position));
                    tv_trade_all_price_tag.setText(data.get(position));
                    tv_min_limit_tag.setText(data.get(position));
                    tv_max_limit_tag.setText(data.get(position));
                    param.money = 1;
                    moneyPopup.dismiss();
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            moneyPopup.setDropDownGravity(Gravity.END);
        }
        moneyPopup.setAnchorView(tv_trade_money);
        moneyPopup.show();
    }

    private void showTimeType() {
        if (timeLimitPopup != null && timeLimitPopup.isShowing()) {
            timeLimitPopup.dismiss();
            return;
        }
        if (timeLimitPopup == null) {
            final List<String> data = new ArrayList<>();
            final List<Integer> dataId = new ArrayList<>();
            data.add(getResources().getString(R.string.min_15));
            data.add(getResources().getString(R.string.min_30));
            data.add(getResources().getString(R.string.hour_1));
            data.add(getResources().getString(R.string.hour_1_5));
            data.add(getResources().getString(R.string.hour_2));
            data.add(getResources().getString(R.string.hour_4));
            data.add(getResources().getString(R.string.hour_6));
            data.add(getResources().getString(R.string.hour_8));
            data.add(getResources().getString(R.string.hour_12));
            data.add(getResources().getString(R.string.hour_24));
            dataId.add(15);
            dataId.add(30);
            dataId.add(1 * 60);
            dataId.add(90);
            dataId.add(2 * 60);
            dataId.add(4 * 60);
            dataId.add(6 * 60);
            dataId.add(8 * 60);
            dataId.add(12 * 60);
            dataId.add(24 * 60);
            ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.simple_list_item, data);
            timeLimitPopup = new ListPopWindow(getContext(), adapter);
            timeLimitPopup.create(UIUtils.dp2px(120), UIUtils.dp2px(409), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tv_order_operation_time_limit.setText(data.get(position));
                    param.timeOut = dataId.get(position);
                    timeLimitPopup.dismiss();
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            timeLimitPopup.setDropDownGravity(Gravity.END);
        }
        timeLimitPopup.setAnchorView(tv_order_operation_time_limit);
        timeLimitPopup.show();
    }

}
