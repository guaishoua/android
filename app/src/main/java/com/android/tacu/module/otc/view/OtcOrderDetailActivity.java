package com.android.tacu.module.otc.view;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.OtcDetailNotifyEvent;
import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.otc.contract.OtcOrderDetailContract;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.orderView.ArbitrationView;
import com.android.tacu.module.otc.orderView.CancelView;
import com.android.tacu.module.otc.orderView.CoinGetView;
import com.android.tacu.module.otc.orderView.CoinedView;
import com.android.tacu.module.otc.orderView.ConfirmView;
import com.android.tacu.module.otc.orderView.FinishView;
import com.android.tacu.module.otc.orderView.PayGetView;
import com.android.tacu.module.otc.orderView.PayedView;
import com.android.tacu.module.otc.presenter.OtcOrderDetailPresenter;

import butterknife.BindView;

public class OtcOrderDetailActivity extends BaseActivity<OtcOrderDetailPresenter> implements OtcOrderDetailContract.IView {

    // 1待确认 2 已确认待付款 3已付款待放币 4 仲裁 5 未确认超时取消 6 拒绝订单 7 付款超时取消 8放弃支付 9 放币超时  10放币完成
    // 12 买家成功 13 卖家成功
    private Integer status = -1;

    //接口30s轮训一次
    private static final int KREFRESH_TIME = 1000 * 30;

    @BindView(R.id.lin_switch)
    LinearLayout linSwitch;

    //待确认
    private ConfirmView confirmView;
    //待付款
    private PayedView payedView;
    //待收款
    private PayGetView payGetView;
    //待放币
    private CoinedView coinedView;
    //待收币
    private CoinGetView coinGetView;
    //已完成
    private FinishView finishView;
    //仲裁中
    private ArbitrationView arbitrationView;
    //取消
    private CancelView cancelView;

    private String orderNo;
    private boolean isFirst = true;
    private boolean isNeedChange = false;
    private OtcTradeModel model;

    private Handler kHandler = new Handler();
    private Runnable kRunnable = new Runnable() {
        @Override
        public void run() {
            upload(isFirst);
            if (kHandler != null) {
                kHandler.postDelayed(this, KREFRESH_TIME);
            }
        }
    };

    public static Intent createActivity(Context context, String orderNo) {
        Intent intent = new Intent(context, OtcOrderDetailActivity.class);
        intent.putExtra("orderNo", orderNo);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_order_detail);
    }

    @Override
    protected void initView() {
        orderNo = getIntent().getStringExtra("orderNo");
    }

    @Override
    protected OtcOrderDetailPresenter createPresenter(OtcOrderDetailPresenter mPresenter) {
        return new OtcOrderDetailPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (kHandler != null && kRunnable != null) {
            kHandler.post(kRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (kHandler != null && kRunnable != null) {
            kHandler.removeCallbacks(kRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (kHandler != null && kRunnable != null) {
            kHandler.removeCallbacks(kRunnable);
            kHandler = null;
            kRunnable = null;
        }
        destoryAllView();
    }

    @Override
    protected void receiveEvent(BaseEvent event) {
        super.receiveEvent(event);
        if (event != null) {
            switch (event.getCode()) {
                case EventConstant.OTCDetailCode:
                    OtcDetailNotifyEvent otcDetailNotifyEvent = (OtcDetailNotifyEvent) event.getData();
                    if (otcDetailNotifyEvent.isNotify()) {
                        upload(true);
                    }
                    break;
            }
        }
    }

    @Override
    public void selectTradeOne(boolean isFirst, OtcTradeModel model) {
        this.model = model;
        if (model != null) {
            Boolean isBuy = null;
            if (model.buyuid == spUtil.getUserUid()) {
                isBuy = true;
            } else if (model.selluid == spUtil.getUserUid()) {
                isBuy = false;
            }

            isNeedChange = false;

            if (model.status != null && !status.equals(model.status)) {
                status = model.status;
                isNeedChange = true;
                destoryAllView();
                switchView(model);
            }

            if (isFirst || isNeedChange) {
                if (isNeedChange) {
                    isNeedChange = false;
                }
                if (isBuy) {
                    mPresenter.userBaseInfo(model.selluid);
                } else {
                    mPresenter.userBaseInfo(model.buyuid);
                }
                // 1待确认 2 已确认待付款 3已付款待放币 4 仲裁 5 未确认超时取消 6 拒绝订单 7 付款超时取消 8放弃支付 9 放币超时  10放币完成
                // 12 买家成功 13 卖家成功
                switch (status) {
                    case 4:
                    case 12:
                    case 13:
                        mPresenter.currentTime();
                        if (!TextUtils.isEmpty(model.arbitrateImg)) {
                            mPresenter.uselectUserInfoArbitration(1, model.arbitrateImg);
                        }
                        if (!TextUtils.isEmpty(model.beArbitrateImg)) {
                            mPresenter.uselectUserInfoArbitration(2, model.beArbitrateImg);
                        }
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 9:
                    case 10:
                        mPresenter.currentTime();
                        mPresenter.selectPayInfoById(model.payId);
                        break;
                }
            }

            // 1待确认 2 已确认待付款 3已付款待放币 4 仲裁 5 未确认超时取消 6 拒绝订单 7 付款超时取消 8放弃支付 9 放币超时  10放币完成
            // 12 买家成功 13 卖家成功
            switch (status) {
                case 1:
                    if (confirmView != null) {
                        confirmView.selectTradeOne(model);
                    }
                    break;
                case 2:
                    if (model.buyuid == spUtil.getUserUid()) {
                        if (payedView != null) {
                            payedView.selectTradeOne(model);
                        }
                    } else if (model.selluid == spUtil.getUserUid()) {
                        if (payGetView != null) {
                            payGetView.selectTradeOne(model);
                        }
                    }
                    break;
                case 3:
                case 9:
                    if (model.buyuid == spUtil.getUserUid()) {
                        if (coinGetView != null) {
                            coinGetView.selectTradeOne(model);
                        }
                    } else if (model.selluid == spUtil.getUserUid()) {
                        if (coinedView != null) {
                            coinedView.selectTradeOne(model);
                        }
                    }
                    break;
                case 5:
                case 6:
                case 7:
                case 8:
                    if (cancelView != null) {
                        cancelView.selectTradeOne(model);
                    }
                    break;
                case 10:
                    if (finishView != null) {
                        finishView.selectTradeOne(model);
                    }
                    break;
                case 4:
                case 12:
                case 13:
                    if (arbitrationView != null) {
                        arbitrationView.selectTradeOne(model);
                    }
                    break;
            }
        }
    }

    @Override
    public void userBaseInfo(OtcMarketInfoModel marketInfoModel) {
        if (marketInfoModel != null) {
            // 1待确认 2 已确认待付款 3已付款待放币 4 仲裁 5 未确认超时取消 6 拒绝订单 7 付款超时取消 8放弃支付 9 放币超时  10放币完成
            // 12 买家成功 13 卖家成功
            switch (status) {
                case 1:
                    if (confirmView != null) {
                        confirmView.setUserInfo(marketInfoModel);
                    }
                    break;
                case 2:
                    if (model != null) {
                        if (model.buyuid == spUtil.getUserUid()) {
                            if (payedView != null) {
                                payedView.setUserInfo(marketInfoModel);
                                payedView.userBaseInfo(marketInfoModel);
                            }
                        } else if (model.selluid == spUtil.getUserUid()) {
                            if (payGetView != null) {
                                payGetView.setUserInfo(marketInfoModel);
                            }
                        }
                    }
                    break;
                case 3:
                case 9:
                    if (model != null) {
                        if (model.buyuid == spUtil.getUserUid()) {
                            if (coinGetView != null) {
                                coinGetView.setUserInfo(marketInfoModel);
                            }
                        } else if (model.selluid == spUtil.getUserUid()) {
                            if (coinedView != null) {
                                coinedView.setUserInfo(marketInfoModel);
                            }
                        }
                    }
                    break;
                case 10:
                    if (finishView != null) {
                        finishView.setUserInfo(marketInfoModel);
                    }
                    break;
                case 4:
                case 12:
                case 13:
                    if (arbitrationView != null) {
                        arbitrationView.setUserInfo(marketInfoModel);
                    }
                    break;
                case 5:
                case 6:
                case 7:
                case 8:
                    if (cancelView != null) {
                        cancelView.setUserInfo(marketInfoModel);
                    }
                    break;
            }
        }
    }

    @Override
    public void currentTime(Long time) {
        // 1待确认 2 已确认待付款 3已付款待放币 4 仲裁 5 未确认超时取消 6 拒绝订单 7 付款超时取消 8放弃支付 9 放币超时  10放币完成
        // 12 买家成功 13 卖家成功
        switch (status) {
            case 1:
                if (confirmView != null) {
                    confirmView.currentTime(time);
                }
                break;
            case 2:
                if (model != null) {
                    if (model.buyuid == spUtil.getUserUid()) {
                        if (payedView != null) {
                            payedView.currentTime(time);
                        }
                    } else if (model.selluid == spUtil.getUserUid()) {
                        if (payGetView != null) {
                            payGetView.currentTime(time);
                        }
                    }
                }
                break;
            case 3:
            case 9:
                if (model != null) {
                    if (model.buyuid == spUtil.getUserUid()) {
                        if (coinGetView != null) {
                            coinGetView.currentTime(time);
                        }
                    } else if (model.selluid == spUtil.getUserUid()) {
                        if (coinedView != null) {
                            coinedView.currentTime(time);
                        }
                    }
                }
                break;
            case 4:
            case 12:
            case 13:
                if (arbitrationView != null) {
                    arbitrationView.currentTime(time);
                }
                break;
        }
    }

    @Override
    public void selectPayInfoById(PayInfoModel payInfoModel) {
        // 1待确认 2 已确认待付款 3已付款待放币 4 仲裁 5 未确认超时取消 6 拒绝订单 7 付款超时取消 8放弃支付 9 放币超时  10放币完成
        // 12 买家成功 13 卖家成功
        switch (status) {
            case 1:
                if (confirmView!=null){
                    confirmView.selectPayInfoById(payInfoModel);
                }
                break;
            case 2:
                if (model != null) {
                    if (model.buyuid == spUtil.getUserUid()) {
                        if (payedView != null) {
                            payedView.selectPayInfoById(payInfoModel);
                        }
                    } else if (model.selluid == spUtil.getUserUid()) {
                        if (payGetView != null) {
                            payGetView.selectPayInfoById(payInfoModel);
                        }
                    }
                }
                break;
            case 3:
            case 9:
                if (model != null) {
                    if (model.buyuid == spUtil.getUserUid()) {
                        if (coinGetView != null) {
                            coinGetView.selectPayInfoById(payInfoModel);
                        }
                    } else if (model.selluid == spUtil.getUserUid()) {
                        if (coinedView != null) {
                            coinedView.selectPayInfoById(payInfoModel);
                        }
                    }
                }
                break;
            case 10:
                if (finishView != null) {
                    finishView.selectPayInfoById(payInfoModel);
                }
                break;
        }
    }

    @Override
    public void uselectUserInfo(String imageUrl) {
        switch (status) {
            case 2:
                if (model != null && model.buyuid == spUtil.getUserUid()) {
                    if (payedView != null) {
                        payedView.uselectUserInfo(imageUrl);
                    }
                }
                break;
        }
    }

    @Override
    public void uselectUserInfoArbitration(int type, String imageUrl) {
        switch (status) {
            case 4:
            case 12:
            case 13:
                if (arbitrationView != null) {
                    arbitrationView.uselectUserInfoArbitration(type, imageUrl);
                }
                break;
        }
    }

    @Override
    public void confirmOrderSuccess() {
        showToastSuccess(getResources().getString(R.string.order_confirm));
        upload(true);
    }

    @Override
    public void confirmCancelOrderSuccess() {
        showToastSuccess(getResources().getString(R.string.cancel_success));
        upload(true);
    }

    @Override
    public void payOrderSuccess() {
        showToastSuccess(getResources().getString(R.string.submit_success));
        upload(true);
    }

    @Override
    public void payCancelOrderSuccess() {
        showToastSuccess(getResources().getString(R.string.cancel_success));
        finish();
    }

    @Override
    public void finishOrderSuccess() {
        showToastSuccess(getResources().getString(R.string.submit_success));
        upload(true);
    }

    private void upload(boolean isShowView) {
        mPresenter.selectTradeOne(isShowView, isFirst, orderNo);
        if (isFirst) {
            isFirst = false;
        }
    }

    private void switchView(OtcTradeModel model) {
        View statusView = null;
        // 1待确认 2 已确认待付款 3已付款待放币 4 仲裁 5 未确认超时取消 6 拒绝订单 7 付款超时取消 8放弃支付 9 放币超时  10放币完成
        // 12 买家成功 13 卖家成功

        switch (status) {
            case 1://待确认
                mTopBar.setTitle(getResources().getString(R.string.otc_order_confirmed));

                confirmView = new ConfirmView();
                statusView = confirmView.create(this, mPresenter);
                break;
            case 2:
                mTopBar.setTitle(getResources().getString(R.string.otc_order_payed));

                if (model.buyuid == spUtil.getUserUid()) {
                    payedView = new PayedView();
                    statusView = payedView.create(this, mPresenter);
                } else if (model.selluid == spUtil.getUserUid()) {
                    payGetView = new PayGetView();
                    statusView = payGetView.create(this);
                }
                break;
            case 3:
            case 9:
                mTopBar.setTitle(getResources().getString(R.string.otc_order_coined));

                if (model.buyuid == spUtil.getUserUid()) {
                    coinGetView = new CoinGetView();
                    statusView = coinGetView.create(this);
                } else if (model.selluid == spUtil.getUserUid()) {
                    coinedView = new CoinedView();
                    statusView = coinedView.create(this, mPresenter);
                }
                break;
            case 4:
            case 12:
            case 13:
                if (status == 4) {
                    mTopBar.setTitle(getResources().getString(R.string.otc_order_arbitration));
                } else {
                    mTopBar.setTitle(getResources().getString(R.string.otc_order_adjude));
                }

                arbitrationView = new ArbitrationView();
                statusView = arbitrationView.create(this, mPresenter);
                break;
            case 5:
            case 6:
            case 7:
            case 8:
                mTopBar.setTitle(getResources().getString(R.string.otc_order_cancel));

                cancelView = new CancelView();
                statusView = cancelView.create(this);
                break;
            case 10:
                mTopBar.setTitle(getResources().getString(R.string.otc_order_finished));

                finishView = new FinishView();
                statusView = finishView.create(this);
                break;
        }
        if (statusView != null) {
            linSwitch.removeAllViews();
            linSwitch.addView(statusView);
        }
    }

    private void destoryAllView() {
        if (confirmView != null) {
            confirmView.destory();
            confirmView = null;
        }
        if (payedView != null) {
            payedView.destory();
            payedView = null;
        }
        if (payGetView != null) {
            payGetView.destory();
            payGetView = null;
        }
        if (coinedView != null) {
            coinedView.destory();
            coinedView = null;
        }
        if (coinGetView != null) {
            coinGetView.destory();
            coinGetView = null;
        }
        if (finishView != null) {
            finishView.destory();
            finishView = null;
        }
        if (arbitrationView != null) {
            arbitrationView.destory();
            arbitrationView = null;
        }
        if (cancelView != null) {
            cancelView.destory();
        }
    }
}
