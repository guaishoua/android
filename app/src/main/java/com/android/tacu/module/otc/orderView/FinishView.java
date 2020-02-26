package com.android.tacu.module.otc.orderView;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.module.ZoomImageViewActivity;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.view.OtcOrderDetailActivity;
import com.android.tacu.utils.GlideUtils;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

//已完成
public class FinishView implements View.OnClickListener {

    private OtcOrderDetailActivity activity;
    private Integer status;

    private TextView tv_order_id;
    private TextView tv_pay_method;
    private TextView tv_trade_get;
    private TextView tv_trade_coin;
    private ImageView img_trade_get;
    private ImageView img_trade_coin;

    private QMUIRadiusImageView img_voucher;

    private TextView tv_order_make;
    private TextView tv_order_make_status;
    private TextView tv_order_pay;
    private TextView tv_order_pay_status;
    private TextView tv_order_finish;
    private TextView tv_order_finish_status;

    private QMUIRoundButton btn_return;

    private OtcTradeModel tradeModel;
    private String imageUrl;

    //5 未确认超时取消 6 拒绝订单 7 付款超时取消 8放弃支付   10放币完成  12仲裁成功 13仲裁失败
    public View create(OtcOrderDetailActivity activity, Integer status) {
        this.activity = activity;
        this.status = status;
        View statusView = View.inflate(activity, R.layout.view_otc_order_finished, null);
        initFinishedView(statusView);
        return statusView;
    }

    private void initFinishedView(View view) {
        tv_order_id = view.findViewById(R.id.tv_order_id);
        tv_pay_method = view.findViewById(R.id.tv_pay_method);
        tv_trade_get = view.findViewById(R.id.tv_trade_get);
        tv_trade_coin = view.findViewById(R.id.tv_trade_coin);
        img_trade_get = view.findViewById(R.id.img_trade_get);
        img_trade_coin = view.findViewById(R.id.img_trade_coin);

        img_voucher = view.findViewById(R.id.img_voucher);

        tv_order_make = view.findViewById(R.id.tv_order_make);
        tv_order_make_status = view.findViewById(R.id.tv_order_make_status);
        tv_order_pay = view.findViewById(R.id.tv_order_pay);
        tv_order_pay_status = view.findViewById(R.id.tv_order_pay_status);
        tv_order_finish = view.findViewById(R.id.tv_order_finish);
        tv_order_finish_status = view.findViewById(R.id.tv_order_finish_status);

        btn_return = view.findViewById(R.id.btn_return);

        img_voucher.setOnClickListener(this);
        btn_return.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_voucher:
                if (!TextUtils.isEmpty(imageUrl)) {
                    activity.jumpTo(ZoomImageViewActivity.createActivity(activity, imageUrl));
                }
                break;
            case R.id.btn_return:
                activity.finish();
                break;
        }
    }

    public void selectTradeOne(OtcTradeModel model) {
        this.tradeModel = model;
        dealFinish();
    }

    public void uselectUserInfo(String imageUrl) {
        this.imageUrl = imageUrl;
        if (!TextUtils.isEmpty(imageUrl)){
            GlideUtils.disPlay(activity, imageUrl, img_voucher);
            img_voucher.setVisibility(View.VISIBLE);
        }else{
            img_voucher.setVisibility(View.GONE);
        }
    }

    private void dealFinish() {
        if (tradeModel != null) {
            tv_order_id.setText(tradeModel.orderNo);
            tv_trade_get.setText(tradeModel.amount + " CNY");
            tv_trade_coin.setText(tradeModel.num + " " + tradeModel.currencyName);
            if (tradeModel.payType != null) {
                switch (tradeModel.payType) {//支付类型 1 银行 2微信3支付宝
                    case 1:
                        tv_pay_method.setText(activity.getResources().getString(R.string.yinhanngka));
                        break;
                    case 2:
                        tv_pay_method.setText(activity.getResources().getString(R.string.weixin));
                        break;
                    case 3:
                        tv_pay_method.setText(activity.getResources().getString(R.string.zhifubao));
                        break;
                }
            }
            if (!TextUtils.isEmpty(tradeModel.createTime)) {
                tv_order_make.setVisibility(View.VISIBLE);
                tv_order_make_status.setVisibility(View.VISIBLE);
                tv_order_make.setText(tradeModel.createTime);
            } else {
                tv_order_make.setVisibility(View.GONE);
                tv_order_make_status.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(tradeModel.payTime)) {
                tv_order_pay.setVisibility(View.VISIBLE);
                tv_order_pay_status.setVisibility(View.VISIBLE);
                tv_order_pay.setText(tradeModel.payTime);

                if (status != null) {
                    switch (status) {
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                            tv_order_pay_status.setText(activity.getResources().getString(R.string.not_pay));
                            tv_order_pay_status.setTextColor(ContextCompat.getColor(activity, R.color.color_otc_sell));
                            break;
                    }
                }
            } else {
                tv_order_pay.setVisibility(View.GONE);
                tv_order_pay_status.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(tradeModel.updateTime)) {
                tv_order_finish.setVisibility(View.VISIBLE);
                tv_order_finish_status.setVisibility(View.VISIBLE);
                tv_order_finish.setText(tradeModel.updateTime);

                if (status != null) {
                    switch (status) {
                        case 12:
                        case 13:
                            tv_order_finish_status.setText(activity.getResources().getString(R.string.not_coined));
                            tv_order_finish_status.setTextColor(ContextCompat.getColor(activity, R.color.color_otc_sell));
                            break;
                    }
                }
            } else {
                tv_order_finish.setVisibility(View.GONE);
                tv_order_finish_status.setVisibility(View.GONE);
            }
        }
        if (status != null) {
            switch (status) {
                case 5:
                case 6:
                case 7:
                case 8:
                    img_trade_get.setImageResource(R.drawable.icon_auth_failure);
                    img_trade_coin.setImageResource(R.drawable.icon_auth_failure);
                    break;
                case 10:
                    img_trade_get.setImageResource(R.drawable.icon_auth_success);
                    img_trade_coin.setImageResource(R.drawable.icon_auth_success);
                    break;
                case 11:
                case 12:
                    img_trade_get.setImageResource(R.drawable.icon_auth_success);
                    img_trade_coin.setImageResource(R.drawable.icon_auth_failure);
                    break;
            }
        }
    }

    public void destory() {
        activity = null;
    }
}
