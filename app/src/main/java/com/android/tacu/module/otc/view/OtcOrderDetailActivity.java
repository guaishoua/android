package com.android.tacu.module.otc.view;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.tacu.R;
import com.android.tacu.module.otc.contract.OtcOrderDetailContract;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.orderView.ConfirmView;
import com.android.tacu.module.otc.presenter.OtcOrderDetailPresenter;
import com.android.tacu.widget.NodeProgressView;

import butterknife.BindView;

public class OtcOrderDetailActivity extends BaseOtcOrderActvity<OtcOrderDetailPresenter> implements OtcOrderDetailContract.IView, View.OnClickListener {

    private int current = -1;
    private Integer status = -1;
    private static final int ORDER_CONFIRMED = 1;//待确认
    private static final int ORDER_PAYED = 2;//待付款
    private static final int ORDER_PAYGET = 3;//待收款
    private static final int ORDER_COINED = 4;//待放币
    private static final int ORDER_COINGET = 5;//待收币
    private static final int ORDER_FINISHED = 6;//已完成
    private static final int ORDER_ARBITRATION = 7;//仲裁中
    //接口30s轮训一次
    private static final int KREFRESH_TIME = 1000 * 30;

    @BindView(R.id.node_progress)
    NodeProgressView nodeProgress;
    @BindView(R.id.lin_switch)
    LinearLayout linSwitch;

    //待确认
    private ConfirmView confirmView;

    //待付款
    private ImageView img_payment_code;

    //待收款

    //待放币

    //待收币

    //已完成

    //仲裁中

    private String orderNo;
    private boolean isFirst = true;

    private Handler kHandler = new Handler();
    private Runnable kRunnable = new Runnable() {
        @Override
        public void run() {
            upload();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_payment_code:
                break;
        }
    }

    @Override
    public void selectTradeOne(boolean isFirst, OtcTradeModel model) {
        if (model != null) {
            if (model.buyuid != null && isFirst) {
                mPresenter.userBaseInfo(1, model.buyuid);
            }
            if (model.selluid != null && isFirst) {
                mPresenter.userBaseInfo(2, model.selluid);
            }
            if (isFirst) {
                mPresenter.currentTime();
            }

            setBuyPayInfoString(model);
            setSellPayInfoString(model);

            // 1待确认 2 已确认待付款 3已付款待放币 4 仲裁 5 未确认超时取消 6 拒绝订单 7 付款超时取消 8放弃支付 9 放币超时 10放币完成 11 待确认 待付款，待放币
            if (model.status != null) {
                switch (model.status) {
                    case 1:
                        current = ORDER_CONFIRMED;
                        break;
                    case 2:
                        if (model.buyuid == spUtil.getUserUid()) {
                            current = ORDER_PAYED;
                        } else if (model.selluid == spUtil.getUserUid()) {
                            current = ORDER_PAYGET;
                        }
                        break;
                    case 3:
                        if (model.buyuid == spUtil.getUserUid()) {
                            current = ORDER_COINGET;
                        } else if (model.selluid == spUtil.getUserUid()) {
                            current = ORDER_COINED;
                        }
                        break;
                    case 4:
                        current = ORDER_ARBITRATION;
                        break;
                    case 10:
                        current = ORDER_FINISHED;
                        break;
                }
                if (!status.equals(model.status)) {
                    status = model.status;
                    destoryAllView();
                    switchView();
                }
            }

            switch (current) {
                case ORDER_CONFIRMED:
                    if (confirmView != null) {
                        confirmView.selectTradeOne(model);
                    }
                    break;
            }
        }
    }

    @Override
    public void userBaseInfo(int buyOrSell, OtcMarketInfoModel model) {
        if (model != null) {
            if (buyOrSell == 1) {//买
                setBuyValue(model);
            } else if (buyOrSell == 2) {
                setSellValue(model);
            }
        }
    }

    @Override
    public void currentTime(Long time) {
        switch (current) {
            case ORDER_CONFIRMED:
                if (confirmView != null) {
                    confirmView.currentTime(time);
                }
                break;
        }
    }

    private void upload() {
        mPresenter.selectTradeOne(isFirst, orderNo);
        if (isFirst) {
            isFirst = false;
        }
    }

    private void switchView() {
        View statusView = null;
        switch (current) {
            case ORDER_CONFIRMED://待确认
                mTopBar.setTitle(getResources().getString(R.string.otc_order_confirmed));
                confirmView = new ConfirmView();
                statusView = confirmView.create(this);
                nodeProgress.setCurentNode(1);
                break;
            case ORDER_PAYED://待付款
                mTopBar.setTitle(getResources().getString(R.string.otc_order_payed));
                statusView = View.inflate(this, R.layout.view_otc_order_payed, null);
                initPayedView(statusView);
                break;
            case ORDER_PAYGET://待收款
                mTopBar.setTitle(getResources().getString(R.string.otc_order_payed));
                statusView = View.inflate(this, R.layout.view_otc_order_payget, null);
                initPayGetView(statusView);
                break;
            case ORDER_COINED://待放币
                mTopBar.setTitle(getResources().getString(R.string.otc_order_coined));
                statusView = View.inflate(this, R.layout.view_otc_order_coined, null);
                initCoinedView(statusView);
                break;
            case ORDER_COINGET://待收币
                mTopBar.setTitle(getResources().getString(R.string.otc_order_coinget));
                statusView = View.inflate(this, R.layout.view_otc_order_coinget, null);
                initCoinGetView(statusView);
                break;
            case ORDER_FINISHED://已完成
                mTopBar.setTitle(getResources().getString(R.string.otc_order_finished));
                statusView = View.inflate(this, R.layout.view_otc_order_finished, null);
                initFinishedView(statusView);
                break;
            case ORDER_ARBITRATION://仲裁中
                mTopBar.setTitle(getResources().getString(R.string.otc_order_arbitration));
                statusView = View.inflate(this, R.layout.view_otc_order_arbitration, null);
                initArbitrationView(statusView);
                break;
        }
        if (statusView != null) {
            linSwitch.removeAllViews();
            linSwitch.addView(statusView);
        }
    }

    /**
     * 待付款
     */
    private void initPayedView(View view) {
        nodeProgress.setCurentNode(1);

        img_payment_code = view.findViewById(R.id.img_payment_code);

        img_payment_code.setOnClickListener(this);
    }

    /**
     * 待收款
     */
    private void initPayGetView(View view) {
        nodeProgress.setCurentNode(1);
    }

    /**
     * 待放币
     */
    private void initCoinedView(View view) {
        nodeProgress.setCurentNode(2);
    }

    /**
     * 待收币
     */
    private void initCoinGetView(View view) {
        nodeProgress.setCurentNode(2);
    }

    /**
     * 已完成
     */
    private void initFinishedView(View view) {
        nodeProgress.setCurentNode(4);
    }

    /**
     * 仲裁中
     */
    private void initArbitrationView(View view) {
        nodeProgress.setCurentNode(4);
    }

    private void destoryAllView() {
        if (confirmView != null) {
            confirmView.destory();
            confirmView = null;
        }
    }
}
