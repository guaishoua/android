package com.android.tacu.module.otc.orderView;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.view.OtcOrderDetailActivity;
import com.android.tacu.utils.DateUtils;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

//待确认
public class ConfirmView implements View.OnClickListener {

    private OtcOrderDetailActivity activity;

    private TextView tv_please_get_pay;
    private TextView tv_confirm_time;
    private QMUIRoundButton btn_return;

    private OtcTradeModel tradeModel;
    private Long currentTime;
    private CountDownTimer time;

    public View create(OtcOrderDetailActivity activity) {
        this.activity = activity;
        View statusView = View.inflate(activity, R.layout.view_otc_order_confirmed, null);
        initConfirmedView(statusView);
        return statusView;
    }

    private void initConfirmedView(View view) {
        tv_please_get_pay = view.findViewById(R.id.tv_please_get_pay);
        tv_confirm_time = view.findViewById(R.id.tv_confirm_time);
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
        dealConfirmed();
        dealTime();
    }

    public void currentTime(Long currentTime) {
        this.currentTime = currentTime;
        dealTime();
    }

    private void dealConfirmed() {
        if (tradeModel != null && tradeModel.payType != null) {
            switch (tradeModel.payType) {//支付类型 1 银行 2微信3支付宝
                case 1:
                    tv_please_get_pay.setText(activity.getResources().getString(R.string.yinhanngka));
                    break;
                case 2:
                    tv_please_get_pay.setText(activity.getResources().getString(R.string.weixin));
                    break;
                case 3:
                    tv_please_get_pay.setText(activity.getResources().getString(R.string.zhifubao));
                    break;
            }
        }
    }

    private void dealTime() {
        if (currentTime != null && tradeModel != null && tradeModel.confirmEndTime != null) {
            long confirmEndTime = DateUtils.string2Millis(tradeModel.confirmEndTime, DateUtils.DEFAULT_PATTERN) - currentTime;
            if (confirmEndTime > 0) {
                startCountDownTimer(confirmEndTime);
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
                    tv_confirm_time.setText(String.format(activity.getResources().getString(R.string.dui_fang_confirm_time), DateUtils.getCountDownTime1(millisUntilFinished)));
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
