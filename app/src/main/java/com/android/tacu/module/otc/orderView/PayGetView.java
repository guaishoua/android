package com.android.tacu.module.otc.orderView;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.OtcDetailNotifyEvent;
import com.android.tacu.R;
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.view.OtcOrderDetailActivity;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.user.UserInfoUtils;

//待收款
public class PayGetView extends BaseOtcView {

    private OtcOrderDetailActivity activity;
    private UserInfoUtils spUtil;

    private TextView tv_hour;
    private TextView tv_minute;
    private TextView tv_second;

    private TextView tv_money_type;
    private ImageView img_pay;
    private TextView tv_pay;
    private TextView tv_pay_account;

    private OtcTradeModel tradeModel;
    private Long currentTime;
    private CountDownTimer time;
    private String[] getCountDownTimes;
    private boolean isLock = false;

    public View create(OtcOrderDetailActivity activity) {
        this.activity = activity;
        View statusView = View.inflate(activity, R.layout.view_otc_order_payget, null);
        initPayGetView(statusView);
        return statusView;
    }

    private void initPayGetView(View view) {
        setBaseView(view, activity);

        tv_hour = view.findViewById(R.id.tv_hour);
        tv_minute = view.findViewById(R.id.tv_minute);
        tv_second = view.findViewById(R.id.tv_second);

        tv_money_type = view.findViewById(R.id.tv_money_type);
        img_pay = view.findViewById(R.id.img_pay);
        tv_pay = view.findViewById(R.id.tv_pay);
        tv_pay_account = view.findViewById(R.id.tv_pay_account);

        spUtil = UserInfoUtils.getInstance();
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

    public void selectPayInfoById(PayInfoModel model) {
        if (model != null && model.type != null) {
            switch (model.type) {
                case 1:
                    img_pay.setImageResource(R.mipmap.img_yhk);
                    tv_pay.setText(activity.getResources().getString(R.string.yinhanngka));
                    tv_pay_account.setText(CommonUtils.hideCardNo(model.bankCard));
                    break;
                case 2:
                    img_pay.setImageResource(R.mipmap.img_wx);
                    tv_pay.setText(activity.getResources().getString(R.string.weixin));
                    tv_pay_account.setText(CommonUtils.hidePhoneNo(model.weChatNo));
                    break;
                case 3:
                    img_pay.setImageResource(R.mipmap.img_zfb);
                    tv_pay.setText(activity.getResources().getString(R.string.zhifubao));
                    tv_pay_account.setText(CommonUtils.hidePhoneNo(model.aliPayNo));
                    break;
            }
        }
    }

    private void dealPayGet() {
        if (tradeModel != null) {
            setBaseValue(activity, tradeModel, spUtil);
            Boolean isBuy = null;
            if (tradeModel.buyuid == spUtil.getUserUid()) {
                isBuy = true;
            } else if (tradeModel.selluid == spUtil.getUserUid()) {
                isBuy = false;
            }
            if (isBuy) {
                tv_money_type.setText(activity.getResources().getString(R.string.pay_type));
            } else {
                tv_money_type.setText(activity.getResources().getString(R.string.getmoney_type));
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
        isLock = false;
        time = new CountDownTimer(valueTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                try {
                    if (isLock) {
                        return;
                    }
                    if (millisUntilFinished >= 0) {
                        getCountDownTimes = DateUtils.getCountDownTime2(millisUntilFinished);
                        if (getCountDownTimes != null && getCountDownTimes.length == 3) {
                            tv_hour.setText(getCountDownTimes[0]);
                            tv_minute.setText(getCountDownTimes[1]);
                            tv_second.setText(getCountDownTimes[2]);
                        }
                    } else {
                        cancel();
                        isLock = true;
                        EventManage.sendEvent(new BaseEvent<>(EventConstant.OTCDetailCode, new OtcDetailNotifyEvent(true)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                cancel();
                isLock = true;
                EventManage.sendEvent(new BaseEvent<>(EventConstant.OTCDetailCode, new OtcDetailNotifyEvent(true)));
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
