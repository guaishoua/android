package com.android.tacu.module.otc.orderView;

import android.app.Dialog;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.OtcDetailNotifyEvent;
import com.android.tacu.R;
import com.android.tacu.module.payinfo.model.PayInfoModel;
import com.android.tacu.module.otc.dialog.OtcTradeDialogUtils;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.presenter.OtcOrderDetailPresenter;
import com.android.tacu.module.otc.view.OtcOrderDetailActivity;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.ShowToast;
import com.android.tacu.utils.user.UserInfoUtils;
import com.android.tacu.widget.dialog.DroidDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;

//待放币
public class CoinedView extends BaseOtcView implements View.OnClickListener {

    private OtcOrderDetailActivity activity;
    private OtcOrderDetailPresenter mPresenter;
    private UserInfoUtils spUtil;

    private LinearLayout lin_countdown;
    private TextView tv_timeout;
    private TextView tv_hour;
    private TextView tv_minute;
    private TextView tv_second;

    private TextView tv_money_type;
    private ImageView img_pay;
    private TextView tv_pay;
    private TextView tv_pay_account;

    private QMUIRoundButton btn_coined;

    private OtcTradeModel tradeModel;
    private Long currentTime;
    private CountDownTimer time;
    private String[] getCountDownTimes;
    private boolean isLock = false;

    private DroidDialog droidDialog;

    //1待确认  2 已确认待付款 3已付款待放币 4 仲裁 5 未确认超时取消 6 拒绝订单 7 付款超时取消 8放弃支付 9 放币超时 10放币完成  12仲裁成功 13仲裁失败
    public View create(OtcOrderDetailActivity activity, OtcOrderDetailPresenter mPresenter) {
        this.activity = activity;
        this.mPresenter = mPresenter;
        View statusView = View.inflate(activity, R.layout.view_otc_order_coined, null);
        initCoinedView(statusView);
        return statusView;
    }

    private void initCoinedView(View view) {
        setBaseView(view, activity);

        lin_countdown = view.findViewById(R.id.lin_countdown);
        tv_timeout = view.findViewById(R.id.tv_timeout);
        tv_hour = view.findViewById(R.id.tv_hour);
        tv_minute = view.findViewById(R.id.tv_minute);
        tv_second = view.findViewById(R.id.tv_second);

        tv_money_type = view.findViewById(R.id.tv_money_type);
        img_pay = view.findViewById(R.id.img_pay);
        tv_pay = view.findViewById(R.id.tv_pay);
        tv_pay_account = view.findViewById(R.id.tv_pay_account);
        btn_coined = view.findViewById(R.id.btn_coined);
        btn_coined.setOnClickListener(this);

        spUtil = UserInfoUtils.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_coined:
                if (!OtcTradeDialogUtils.isDialogShow(activity)) {
                    showSure();
                }
                break;
        }
    }

    public void selectTradeOne(OtcTradeModel model) {
        this.tradeModel = model;
        dealCoined();
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

    private void dealCoined() {
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
                lin_countdown.setVisibility(View.GONE);
            } else {
                long transCoinEndTime = DateUtils.string2Millis(tradeModel.transCoinEndTime, DateUtils.DEFAULT_PATTERN) - currentTime;
                if (transCoinEndTime > 0) {
                    tv_timeout.setVisibility(View.GONE);
                    lin_countdown.setVisibility(View.VISIBLE);
                    startCountDownTimer(transCoinEndTime);
                } else {
                    tv_timeout.setVisibility(View.VISIBLE);
                    lin_countdown.setVisibility(View.GONE);
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

    private void showSure() {
        View view = View.inflate(activity, R.layout.dialog_coined_confirm, null);
        final CheckBox cb_xieyi = view.findViewById(R.id.cb_xieyi);
        final QMUIRoundEditText edit_trade_pwd = view.findViewById(R.id.edit_trade_pwd);

        if (spUtil.getPwdVisibility()) {
            edit_trade_pwd.setVisibility(View.VISIBLE);
        } else {
            edit_trade_pwd.setVisibility(View.GONE);
        }

        droidDialog = new DroidDialog.Builder(activity)
                .title(activity.getResources().getString(R.string.coined_confirm))
                .viewCustomLayout(view)
                .positiveButton(activity.getResources().getString(R.string.sure), false, new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        if (!cb_xieyi.isChecked()) {
                            ShowToast.error(activity.getResources().getString(R.string.please_check_xieyi));
                            return;
                        }
                        String pwdString = edit_trade_pwd.getText().toString();
                        if (spUtil.getPwdVisibility() && TextUtils.isEmpty(pwdString)) {
                            ShowToast.error(activity.getResources().getString(R.string.please_input_trade_password));
                            return;
                        }

                        if (tradeModel != null) {
                            droidDialog.dismiss();
                            mPresenter.finishOrder(tradeModel.id, spUtil.getPwdVisibility() ? Md5Utils.encryptFdPwd(pwdString, spUtil.getUserUid()).toLowerCase() : null);
                        }
                    }
                })
                .negativeButton(activity.getResources().getString(R.string.cancel), null)
                .cancelable(false, false)
                .show();
    }

    public void destory() {
        activity = null;
        mPresenter = null;
        if (droidDialog != null && droidDialog.isShowing()) {
            droidDialog.dismiss();
        }
        if (time != null) {
            time.cancel();
            time = null;
        }
    }
}
