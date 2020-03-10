package com.android.tacu.module.otc.orderView;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.module.ZoomImageViewActivity;
import com.android.tacu.module.otc.dialog.OtcTradeDialogUtils;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.presenter.OtcOrderDetailPresenter;
import com.android.tacu.module.otc.view.OtcOrderDetailActivity;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.ShowToast;
import com.android.tacu.utils.user.UserInfoUtils;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundImageView;

//待放币
public class CoinedView implements View.OnClickListener {

    private OtcOrderDetailActivity activity;
    private OtcOrderDetailPresenter mPresenter;
    private Integer status;

    private TextView tv_countdown;
    private TextView tv_order_id;
    private TextView tv_pay_method;
    private TextView tv_trade_get;
    private TextView tv_trade_coin;

    private QMUIRoundImageView img_voucher;
    private TextView tv_get_money_tip;

    private QMUIRoundEditText et_pwd;
    private QMUIRoundButton btn_coined;
    private QMUIRoundButton btn_return;

    private OtcTradeModel tradeModel;
    private Long currentTime;
    private CountDownTimer time;
    private String imageUrl;

    private UserInfoUtils spUtil;

    //1待确认  2 已确认待付款 3已付款待放币 4 仲裁 5 未确认超时取消 6 拒绝订单 7 付款超时取消 8放弃支付 9 放币超时 10放币完成  12仲裁成功 13仲裁失败
    public View create(OtcOrderDetailActivity activity, OtcOrderDetailPresenter mPresenter, Integer status) {
        this.activity = activity;
        this.mPresenter = mPresenter;
        this.status = status;
        View statusView = View.inflate(activity, R.layout.view_otc_order_coined, null);
        initCoinedView(statusView);
        return statusView;
    }

    private void initCoinedView(View view) {
        tv_countdown = view.findViewById(R.id.tv_countdown);
        tv_order_id = view.findViewById(R.id.tv_order_id);
        tv_pay_method = view.findViewById(R.id.tv_pay_method);
        tv_trade_get = view.findViewById(R.id.tv_trade_get);
        tv_trade_coin = view.findViewById(R.id.tv_trade_coin);

        img_voucher = view.findViewById(R.id.img_voucher);
        tv_get_money_tip = view.findViewById(R.id.tv_get_money_tip);

        et_pwd = view.findViewById(R.id.et_pwd);
        btn_coined = view.findViewById(R.id.btn_coined);
        btn_return = view.findViewById(R.id.btn_return);

        img_voucher.setOnClickListener(this);
        btn_coined.setOnClickListener(this);
        btn_return.setOnClickListener(this);

        spUtil = UserInfoUtils.getInstance();
        if (spUtil.getPwdVisibility()) {
            et_pwd.setVisibility(View.VISIBLE);
        } else {
            et_pwd.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_voucher:
                if (!TextUtils.isEmpty(imageUrl)) {
                    activity.jumpTo(ZoomImageViewActivity.createActivity(activity, imageUrl));
                }
                break;
            case R.id.btn_coined:
                if (!OtcTradeDialogUtils.isDialogShow(activity)) {
                    String pwdString = et_pwd.getText().toString().trim();
                    if (spUtil.getPwdVisibility() && TextUtils.isEmpty(pwdString)) {
                        ShowToast.error(activity.getResources().getString(R.string.please_input_trade_password));
                        return;
                    }
                    if (tradeModel != null) {
                        mPresenter.finishOrder(tradeModel.id, spUtil.getPwdVisibility() ? Md5Utils.encryptFdPwd(pwdString, spUtil.getUserUid()).toLowerCase() : null);
                    }
                }
                break;
            case R.id.btn_return:
                activity.finish();
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

    public void uselectUserInfo(String imageUrl) {
        this.imageUrl = imageUrl;
        if (!TextUtils.isEmpty(imageUrl)) {
            GlideUtils.disPlay(activity, imageUrl, img_voucher);
            img_voucher.setVisibility(View.VISIBLE);
        } else {
            img_voucher.setVisibility(View.GONE);
        }
    }

    private void dealCoined() {
        if (tradeModel != null) {
            tv_order_id.setText(tradeModel.orderNo);
            tv_trade_get.setText(tradeModel.amount + " " + Constant.CNY);
            tv_trade_coin.setText(tradeModel.num + " " + tradeModel.currencyName);
            if (tradeModel.payType != null) {
                switch (tradeModel.payType) {//支付类型 1 银行 2微信3支付宝
                    case 1:
                        tv_pay_method.setText(activity.getResources().getString(R.string.yinhanngka));
                        tv_get_money_tip.setText(activity.getResources().getString(R.string.please_confirm_yhk_get_money));
                        break;
                    case 2:
                        tv_pay_method.setText(activity.getResources().getString(R.string.weixin));
                        tv_get_money_tip.setText(activity.getResources().getString(R.string.please_confirm_wx_get_money));
                        break;
                    case 3:
                        tv_pay_method.setText(activity.getResources().getString(R.string.zhifubao));
                        tv_get_money_tip.setText(activity.getResources().getString(R.string.please_confirm_zfb_get_money));
                        break;
                }
            }
        }
    }

    private void dealTime() {
        if (currentTime != null && tradeModel != null && !TextUtils.isEmpty(tradeModel.transCoinEndTime)) {
            if (status != null && status == 9) {
                tv_countdown.setText(activity.getResources().getString(R.string.timeouted));
            } else {
                long transCoinEndTime = DateUtils.string2Millis(tradeModel.transCoinEndTime, DateUtils.DEFAULT_PATTERN) - currentTime;
                if (transCoinEndTime > 0) {
                    startCountDownTimer(transCoinEndTime);
                }
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
        mPresenter = null;
        if (time != null) {
            time.cancel();
            time = null;
        }
    }
}
