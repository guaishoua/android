package com.android.tacu.module.otc.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.otc.model.OtcMarketOrderModel;
import com.android.tacu.utils.FormatterUtils;

import butterknife.BindView;

public class OtcManageDetailsActivity extends BaseActivity {

    @BindView(R.id.tv_coin_name)
    TextView tv_coin_name;
    @BindView(R.id.tv_buyorsell)
    TextView tv_buyorsell;
    @BindView(R.id.img_wx)
    ImageView img_wx;
    @BindView(R.id.img_zfb)
    ImageView img_zfb;
    @BindView(R.id.img_yhk)
    ImageView img_yhk;

    @BindView(R.id.tv_num_title)
    TextView tv_num_title;
    @BindView(R.id.tv_deal_amount_title)
    TextView tv_deal_amount_title;
    @BindView(R.id.tv_remain_amount_title)
    TextView tv_remain_amount_title;
    @BindView(R.id.tv_frozen_title)
    TextView tv_frozen_title;

    @BindView(R.id.tv_price)
    TextView tv_price;
    @BindView(R.id.tv_num)
    TextView tv_num;
    @BindView(R.id.tv_amount)
    TextView tv_amount;
    @BindView(R.id.tv_deal_dan_num)
    TextView tv_deal_dan_num;
    @BindView(R.id.tv_deal_amount)
    TextView tv_deal_amount;
    @BindView(R.id.tv_remain_amount)
    TextView tv_remain_amount;
    @BindView(R.id.tv_frozen)
    TextView tv_frozen;
    @BindView(R.id.tv_limit)
    TextView tv_limit;
    @BindView(R.id.tv_otc_phone)
    TextView tv_otc_phone;
    @BindView(R.id.tv_time_limit)
    TextView tv_time_limit;
    @BindView(R.id.tv_otc_publish_time)
    TextView tv_otc_publish_time;

    private OtcMarketOrderModel otcMarketOrderModel;

    public static Intent createActivity(Context context, OtcMarketOrderModel otcMarketOrderModel) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("otcMarketOrderModel", otcMarketOrderModel);
        Intent intent = new Intent(context, OtcManageDetailsActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_manage_details);
    }

    @Override
    protected void initView() {
        otcMarketOrderModel = (OtcMarketOrderModel) getIntent().getSerializableExtra("otcMarketOrderModel");

        mTopBar.setTitle(getResources().getString(R.string.guanggao_detail));

        setValue();
    }

    private void setValue() {
        if (otcMarketOrderModel != null) {
            tv_coin_name.setText(otcMarketOrderModel.currencyName);
            if (otcMarketOrderModel.buyorsell != null) {
                if (otcMarketOrderModel.buyorsell == 1) {
                    tv_buyorsell.setText(getResources().getString(R.string.goumai));
                    tv_buyorsell.setTextColor(ContextCompat.getColor(this, R.color.color_otc_buy));
                } else if (otcMarketOrderModel.buyorsell == 2) {
                    tv_buyorsell.setText(getResources().getString(R.string.sell));
                    tv_buyorsell.setTextColor(ContextCompat.getColor(this, R.color.color_otc_sell));
                }
            }

            if (otcMarketOrderModel.payByCard != null && otcMarketOrderModel.payByCard == 1) {
                img_yhk.setVisibility(View.VISIBLE);
            } else {
                img_yhk.setVisibility(View.GONE);
            }
            if (otcMarketOrderModel.payWechat != null && otcMarketOrderModel.payWechat == 1) {
                img_wx.setVisibility(View.VISIBLE);
            } else {
                img_wx.setVisibility(View.GONE);
            }
            if (otcMarketOrderModel.payAlipay != null && otcMarketOrderModel.payAlipay == 1) {
                img_zfb.setVisibility(View.VISIBLE);
            } else {
                img_zfb.setVisibility(View.GONE);
            }

            tv_price.setText(otcMarketOrderModel.price);

            tv_num_title.setText(getResources().getString(R.string.all_num) + "(" + otcMarketOrderModel.currencyName + ")");
            tv_num.setText(otcMarketOrderModel.num);

            tv_amount.setText(otcMarketOrderModel.amount);

            tv_deal_dan_num.setText(otcMarketOrderModel.finishNum);

            tv_deal_amount_title.setText(getResources().getString(R.string.deal_num) + "(" + otcMarketOrderModel.currencyName + ")");
            tv_deal_amount.setText(otcMarketOrderModel.tradeAmount);

            tv_remain_amount_title.setText(getResources().getString(R.string.remain_amount) + "(" + otcMarketOrderModel.currencyName + ")");
            tv_remain_amount.setText(FormatterUtils.getFormatValue(otcMarketOrderModel.remainAmount));

            tv_frozen_title.setText(getResources().getString(R.string.frozen_num) + "(" + otcMarketOrderModel.currencyName + ")");
            tv_frozen.setText(otcMarketOrderModel.lockAmount);

            tv_limit.setText(FormatterUtils.getFormatValue(otcMarketOrderModel.lowLimit) + "-" + FormatterUtils.getFormatValue(otcMarketOrderModel.highLimit));

            tv_otc_phone.setText(otcMarketOrderModel.explaininfo);

            if (otcMarketOrderModel.timeOut != null) {
                switch (otcMarketOrderModel.timeOut) {
                    case 15:
                        tv_time_limit.setText(getResources().getString(R.string.min_15));
                        break;
                    case 30:
                        tv_time_limit.setText(getResources().getString(R.string.min_30));
                        break;
                    case 60:
                        tv_time_limit.setText(getResources().getString(R.string.hour_1));
                        break;
                }
            }

            tv_otc_publish_time.setText(otcMarketOrderModel.createTime);
        }
    }
}
