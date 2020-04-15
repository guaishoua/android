package com.android.tacu.module.otc.orderView;

import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.OtcDetailNotifyEvent;
import com.android.tacu.R;
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.view.ArbitrationSubmitActivity;
import com.android.tacu.module.otc.view.OtcOrderDetailActivity;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.user.UserInfoUtils;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;

//待收币
public class CoinGetView extends BaseOtcView implements View.OnClickListener {

    private OtcOrderDetailActivity activity;
    private UserInfoUtils spUtil;

    private TextView tv_countdown;
    private LinearLayout lin_countdown;
    private TextView tv_timeout;
    private TextView tv_hour;
    private TextView tv_minute;
    private TextView tv_second;

    private TextView tv_money_type;
    private ImageView img_pay;
    private TextView tv_pay;
    private TextView tv_pay_account;

    private QMUIRoundButton btn_submit_arbitration;

    private OtcTradeModel tradeModel;
    private Long currentTime;
    private CountDownTimer time;
    private String[] getCountDownTimes;
    private boolean isLock = false;

    public View create(OtcOrderDetailActivity activity) {
        this.activity = activity;
        View statusView = View.inflate(activity, R.layout.view_otc_order_coinget, null);
        initCoinGetView(statusView);
        return statusView;
    }

    private void initCoinGetView(View view) {
        setBaseView(view, activity);

        tv_countdown = view.findViewById(R.id.tv_countdown);
        lin_countdown = view.findViewById(R.id.lin_countdown);
        tv_timeout = view.findViewById(R.id.tv_timeout);
        tv_hour = view.findViewById(R.id.tv_hour);
        tv_minute = view.findViewById(R.id.tv_minute);
        tv_second = view.findViewById(R.id.tv_second);

        tv_money_type = view.findViewById(R.id.tv_money_type);
        img_pay = view.findViewById(R.id.img_pay);
        tv_pay = view.findViewById(R.id.tv_pay);
        tv_pay_account = view.findViewById(R.id.tv_pay_account);
        btn_submit_arbitration = view.findViewById(R.id.btn_submit_arbitration);
        btn_submit_arbitration.setOnClickListener(this);

        spUtil = UserInfoUtils.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit_arbitration:
                activity.startActivity(ArbitrationSubmitActivity.createActivity(activity, true, tradeModel.id));
                break;
        }
    }

    public void selectTradeOne(OtcTradeModel model) {
        this.tradeModel = model;
        dealCoinGet();
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

    private void dealCoinGet() {
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
        if (currentTime != null && tradeModel != null && !TextUtils.isEmpty(tradeModel.transCoinEndTime)) {
            if (tradeModel.status != null && tradeModel.status == 9) {
                tv_timeout.setVisibility(View.VISIBLE);
                tv_countdown.setVisibility(View.GONE);
                lin_countdown.setVisibility(View.GONE);

                btn_submit_arbitration.setEnabled(true);
                ((QMUIRoundButtonDrawable) btn_submit_arbitration.getBackground()).setBgData(ContextCompat.getColorStateList(activity, R.color.color_default));
            } else {
                long transCoinEndTime = DateUtils.string2Millis(tradeModel.transCoinEndTime, DateUtils.DEFAULT_PATTERN) - currentTime;
                if (transCoinEndTime > 0) {
                    tv_timeout.setVisibility(View.GONE);
                    tv_countdown.setVisibility(View.VISIBLE);
                    lin_countdown.setVisibility(View.VISIBLE);
                    startCountDownTimer(transCoinEndTime);

                    btn_submit_arbitration.setEnabled(false);
                    ((QMUIRoundButtonDrawable) btn_submit_arbitration.getBackground()).setBgData(ContextCompat.getColorStateList(activity, R.color.color_grey));
                } else {
                    tv_timeout.setVisibility(View.VISIBLE);
                    tv_countdown.setVisibility(View.GONE);
                    lin_countdown.setVisibility(View.GONE);

                    btn_submit_arbitration.setEnabled(true);
                    ((QMUIRoundButtonDrawable) btn_submit_arbitration.getBackground()).setBgData(ContextCompat.getColorStateList(activity, R.color.color_default));
                }
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
