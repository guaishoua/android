package com.android.tacu.module.otc.orderView;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.view.OtcOrderDetailActivity;
import com.android.tacu.utils.DateUtils;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

//待收款
public class PayGetView implements View.OnClickListener {

    private OtcOrderDetailActivity activity;

    private TextView tv_countdown;
    private TextView tv_order_id;
    private TextView tv_pay_method;
    private TextView tv_trade_get;
    private TextView tv_trade_coin;
    private QMUIRoundButton btn_return;

    private OtcTradeModel tradeModel;
    private Long currentTime;
    private CountDownTimer time;

    public View create(OtcOrderDetailActivity activity) {
        this.activity = activity;
        View statusView = View.inflate(activity, R.layout.view_otc_order_payget, null);
        initPayGetView(statusView);
        return statusView;
    }

    private void initPayGetView(View view) {
        tv_countdown = view.findViewById(R.id.tv_countdown);
        tv_order_id = view.findViewById(R.id.tv_order_id);
        tv_pay_method = view.findViewById(R.id.tv_pay_method);
        tv_trade_get = view.findViewById(R.id.tv_trade_get);
        tv_trade_coin = view.findViewById(R.id.tv_trade_coin);
        btn_return = view.findViewById(R.id.btn_return);

        btn_return.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_return:
                activity.finish();
                break;
        }
    }

    public void selectTradeOne(OtcTradeModel model) {
        this.tradeModel = model;
        dealPayGet();
        dealTime();
    }

    public void currentTime(Long currentTime) {
        this.currentTime = currentTime;
        dealTime();
    }

    private void dealPayGet() {
        if (tradeModel != null) {
            tv_order_id.setText(tradeModel.orderNo);
            tv_trade_get.setText(tradeModel.amount + " " + Constant.CNY);
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
        }
    }

    private void dealTime() {
        if (currentTime != null && tradeModel != null && !TextUtils.isEmpty(tradeModel.payEndTime)) {
            long payEndTime = DateUtils.string2Millis(tradeModel.payEndTime, DateUtils.DEFAULT_PATTERN) - currentTime;
            if (payEndTime > 0) {
                startCountDownTimer(payEndTime);
            }
        }
    }

    private void startCountDownTimer(long valueTime) {
        if (time != null) {
            return;
        }
        time = new CountDownTimer(valueTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                try {
                    tv_countdown.setText(DateUtils.getCountDownTime1(millisUntilFinished));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                cancel();
                activity.finish();
            }
        };
        time.start();
    }

    public void destory() {
        activity = null;
        if (time != null) {
            time.cancel();
            time = null;
        }
    }
}
