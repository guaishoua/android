package com.android.tacu.module.otc.view;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.otc.contract.OtcOrderConfirmContract;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcMarketOrderAllModel;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.presenter.OtcOrderConfirmPresenter;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.FormatterUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class OtcOrderConfirmActivity extends BaseOtcHalfOrderActvity<OtcOrderConfirmPresenter> implements OtcOrderConfirmContract.IView {

    @BindView(R.id.tv_current_trade_amount)
    TextView tv_current_trade_amount;
    @BindView(R.id.tv_current_trade_price)
    TextView tv_current_trade_price;
    @BindView(R.id.tv_current_pay_method)
    TextView tv_current_pay_method;
    @BindView(R.id.tv_shoper_confirm_count_down)
    TextView tv_shoper_confirm_count_down;
    @BindView(R.id.tv_account_balance)
    TextView tv_account_balance;
    @BindView(R.id.tv_completed_transaction_advertising_balance)
    TextView tv_completed_transaction_advertising_balance;
    @BindView(R.id.cb_xieyi)
    CheckBox cb_xieyi;
    @BindView(R.id.tv_xieyi)
    TextView tv_xieyi;

    private String orderId;
    private String orderNo;
    private Long currentTime;//当前服务器时间戳

    private OtcTradeModel tradeModel;
    private OtcMarketOrderAllModel model;
    private CountDownTimer time;

    public static Intent createActivity(Context context, String orderId, String orderNo) {
        Intent intent = new Intent(context, OtcOrderConfirmActivity.class);
        intent.putExtra("orderId", orderId);
        intent.putExtra("orderNo", orderNo);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_order_confirm);
    }

    @Override
    protected void initView() {
        orderId = getIntent().getStringExtra("orderId");
        orderNo = getIntent().getStringExtra("orderNo");

        mTopBar.setTitle(getResources().getString(R.string.otc_order_confirm));

        tv_xieyi.setText(Html.fromHtml(getResources().getString(R.string.otc_xieyi) + "<font color=" + ContextCompat.getColor(this, R.color.color_otc_unhappy) + ">" + getResources().getString(R.string.otc_xieyi_name) + "</font>"));
    }

    @Override
    protected OtcOrderConfirmPresenter createPresenter(OtcOrderConfirmPresenter mPresenter) {
        return new OtcOrderConfirmPresenter();
    }

    @Override
    protected void onPresenterCreated(OtcOrderConfirmPresenter presenter) {
        super.onPresenterCreated(presenter);
        mPresenter.selectTradeOne(orderNo);
        mPresenter.currentTime();
        mPresenter.OtcAccount(Constant.ACU_CURRENCY_ID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (time != null) {
            time.cancel();
        }
    }

    @OnClick(R.id.tv_look_advertising_info)
    void infoClick() {
        if (tradeModel != null) {
            Boolean isBuy = null;
            if (tradeModel.buyuid == spUtil.getUserUid()) {
                isBuy = true;
            } else if (tradeModel.selluid == spUtil.getUserUid()) {
                isBuy = false;
            }
            if (isBuy != null) {
                jumpTo(OtcManageBuySellDetailActivity.createActivity(this, isBuy));
            }
        }
    }

    @OnClick(R.id.btn_confirm)
    void confirmClick() {
        if (!cb_xieyi.isChecked()) {
            showToastError(getResources().getString(R.string.please_check_xieyi));
            return;
        }
        mPresenter.confirmOrder(orderId);
    }

    @OnClick(R.id.btn_cancel)
    void cancelClick() {
        mPresenter.confirmCancelOrder(orderId);
    }

    @Override
    public void confirmOrderSuccess() {
        showToastSuccess(getResources().getString(R.string.order_confirm));
        finish();
    }

    @Override
    public void confirmCancelOrderSuccess() {
        showToastSuccess(getResources().getString(R.string.cancel_success));
        finish();
    }

    @Override
    public void selectTradeOne(OtcTradeModel model) {
        this.tradeModel = model;
        if (model != null) {
            Integer notOwnUid = null;
            int ownUid = spUtil.getUserUid();
            int notOwnBuyOrSell = 0;//1买 2卖  确认对方是买还是卖
            if (model.buyuid != null && model.buyuid == ownUid && model.selluid != null && model.selluid != ownUid) {
                notOwnUid = model.selluid;
                notOwnBuyOrSell = 2;
            } else if (model.selluid != null && model.selluid == ownUid && model.buyuid != null && model.buyuid != ownUid) {
                notOwnUid = model.buyuid;
                notOwnBuyOrSell = 1;
            }
            setPayValueString(notOwnBuyOrSell, model);
            if (notOwnUid != null) {
                mPresenter.userBaseInfo(notOwnUid);
            }
            if (!TextUtils.isEmpty(model.orderId)) {
                mPresenter.orderListOne(model.orderId);
            }

            tv_current_trade_amount.setText(model.num + model.currencyName);
            tv_current_trade_price.setText(model.amount + "CNY");
            if (model.payType != null) {
                switch (model.payType) {//支付类型 1 银行 2微信3支付宝
                    case 1:
                        tv_current_pay_method.setText(getResources().getString(R.string.yinhanngka));
                        break;
                    case 2:
                        tv_current_pay_method.setText(getResources().getString(R.string.weixin));
                        break;
                    case 3:
                        tv_current_pay_method.setText(getResources().getString(R.string.zhifubao));
                        break;
                }
            }
            dealTime();
            dealValueNum();
        }
    }

    @Override
    public void userBaseInfo(OtcMarketInfoModel model) {
        setInfoValue(model);
    }

    @Override
    public void currentTime(Long time) {
        this.currentTime = time;
        dealTime();
    }

    @Override
    public void OtcAccount(OtcAmountModel model) {
        if (model != null) {
            tv_account_balance.setText(FormatterUtils.getFormatValue(model.cashAmount) + Constant.OTC_ACU);
        }
    }

    @Override
    public void orderListOne(OtcMarketOrderAllModel model) {
        this.model = model;
        dealValueNum();
    }

    private void dealTime() {
        if (currentTime != null && tradeModel != null && tradeModel.confirmEndTime != null) {
            long confirmEndTime = DateUtils.string2Millis(tradeModel.confirmEndTime, DateUtils.DEFAULT_PATTERN) - currentTime;
            if (confirmEndTime > 0) {
                startCountDownTimer(confirmEndTime);
            }
        }
    }

    private void dealValueNum() {
        if (model != null && model.orderModel != null && model.orderModel.remainAmount != null && tradeModel != null) {
            double value = Double.parseDouble(model.orderModel.remainAmount) - Double.parseDouble(tradeModel.num);
            tv_completed_transaction_advertising_balance.setText(FormatterUtils.getFormatValue(value) + tradeModel.currencyName);
        }
    }

    private void startCountDownTimer(long valueTime) {
        time = new CountDownTimer(valueTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                try {
                    tv_shoper_confirm_count_down.setText(DateUtils.getCountDownTime1(millisUntilFinished));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                cancel();
                finish();
            }
        };
        time.start();
    }
}
