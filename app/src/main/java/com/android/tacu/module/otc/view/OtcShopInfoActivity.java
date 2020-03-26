package com.android.tacu.module.otc.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcMarketOrderAllModel;
import com.android.tacu.module.otc.model.OtcMarketOrderModel;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.MathHelper;

import butterknife.BindView;

public class OtcShopInfoActivity extends BaseActivity {

    @BindView(R.id.tv_pay_method_title)
    TextView tv_pay_method_title;
    @BindView(R.id.tv_buy_amount_title)
    TextView tv_buy_amount_title;

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

    @BindView(R.id.tv_bond)
    TextView tv_bond;
    @BindView(R.id.tv_all_deal_amount)
    TextView tv_all_deal_amount;
    @BindView(R.id.tv_buy_amount)
    TextView tv_buy_amount;
    @BindView(R.id.tv_deal_rate)
    TextView tv_deal_rate;
    @BindView(R.id.tv_average_payment_time)
    TextView tv_average_payment_time;

    private boolean isBuy = true;
    private OtcMarketOrderAllModel allModel;

    public static Intent createActivity(Context context, boolean isBuy, OtcMarketOrderAllModel allModel) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("allModel", allModel);
        Intent intent = new Intent(context, OtcShopInfoActivity.class);
        intent.putExtra("isBuy", isBuy);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_shop_info);
    }

    @Override
    protected void initView() {
        isBuy = getIntent().getBooleanExtra("isBuy", true);
        allModel = (OtcMarketOrderAllModel) getIntent().getSerializableExtra("allModel");

        mTopBar.setTitle(getResources().getString(R.string.shop_info));

        if (isBuy) {
            tv_pay_method_title.setText(getResources().getString(R.string.current_getmoney_type));
            tv_buy_amount_title.setText(getResources().getString(R.string.sell_amount));
        } else {
            tv_pay_method_title.setText(getResources().getString(R.string.current_paymoney_type));
            tv_buy_amount_title.setText(getResources().getString(R.string.buy_amount));
        }
        setOrderInfo();
    }

    public void setOrderInfo() {
        if (allModel != null) {
            OtcMarketOrderModel orderModel = allModel.orderModel;
            String valueWei = " " + Constant.CNY;
            if (orderModel.money != null && orderModel.money == 1) {
                valueWei = " " + Constant.CNY;
            }
            tv_single_price.setText(orderModel.price + valueWei);
            tv_plan_buy_amount.setText(orderModel.num + " " + orderModel.currencyName);
            tv_remaining_unsold.setText(orderModel.remainAmount + " " + orderModel.currencyName);
            if (!TextUtils.isEmpty(orderModel.price) && !TextUtils.isEmpty(orderModel.remainAmount)) {
                double value = MathHelper.mul(Double.parseDouble(orderModel.price), Double.parseDouble(orderModel.remainAmount));
                tv_remaining_all.setText(FormatterUtils.getFormatRoundDown(2, value) + valueWei);
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

            OtcMarketInfoModel infoModel = allModel.infoModel;
            tv_bond.setText(FormatterUtils.getFormatValue(infoModel.bondMoney) + " " + Constant.ACU_CURRENCY_NAME);
            tv_all_deal_amount.setText(FormatterUtils.getFormatValue(infoModel.total) + getResources().getString(R.string.dan));
            if (isBuy) {
                tv_buy_amount.setText(FormatterUtils.getFormatValue(infoModel.sell) + getResources().getString(R.string.dan));
            } else {
                tv_buy_amount.setText(FormatterUtils.getFormatValue(infoModel.buy) + getResources().getString(R.string.dan));
            }
            if (!TextUtils.isEmpty(infoModel.success) && !TextUtils.isEmpty(infoModel.total) && !TextUtils.equals(infoModel.success, "0") && !TextUtils.equals(infoModel.total, "0")) {
                double value = MathHelper.div(Double.parseDouble(infoModel.success), Double.valueOf(infoModel.total));
                tv_deal_rate.setText(FormatterUtils.getFormatRoundDown(2, MathHelper.mul(value, 100)) + " %");
            } else {
                tv_deal_rate.setText("0%");
            }

            if (!TextUtils.isEmpty(infoModel.allTime) && !TextUtils.isEmpty(infoModel.success) && !TextUtils.equals(infoModel.allTime, "0") && !TextUtils.equals(infoModel.success, "0")) {
                double valueD = MathHelper.div(Double.valueOf(infoModel.allTime), Double.valueOf(infoModel.success));
                String value = FormatterUtils.getFormatRoundDown(2, valueD) + " min";
                tv_average_payment_time.setText(value);
            } else {
                tv_average_payment_time.setText("0 min");
            }
        }
    }
}
