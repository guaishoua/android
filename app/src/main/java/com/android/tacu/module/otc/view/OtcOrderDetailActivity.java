package com.android.tacu.module.otc.view;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.tacu.R;
import com.android.tacu.module.otc.contract.OtcOrderDetailContract;
import com.android.tacu.widget.NodeProgressView;

import butterknife.BindView;

public class OtcOrderDetailActivity extends BaseOtcOrderActvity implements OtcOrderDetailContract.IView, View.OnClickListener {

    private int current = -1;
    public static final int ORDER_CONFIRMED = 1;//待确认
    public static final int ORDER_PAYED = 2;//待付款
    public static final int ORDER_PAYGET = 3;//待收款
    public static final int ORDER_COINED = 4;//待放币
    public static final int ORDER_COINGET = 5;//待收币
    public static final int ORDER_FINISHED = 6;//已完成
    public static final int ORDER_ARBITRATION = 7;//仲裁中

    @BindView(R.id.node_progress)
    NodeProgressView nodeProgress;
    @BindView(R.id.lin_switch)
    LinearLayout linSwitch;

    //待确认

    //待付款
    private ImageView img_payment_code;

    //待收款

    //待放币

    //待收币

    //已完成

    //仲裁中

    public static Intent createActivity(Context context, int current) {
        Intent intent = new Intent(context, OtcOrderDetailActivity.class);
        intent.putExtra("current", current);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_order_detail);
    }

    @Override
    protected void initView() {
        current = getIntent().getIntExtra("current", -1);
        switchView();
    }

    private void switchView() {
        View statusView = null;
        switch (current) {
            case ORDER_CONFIRMED://待确认
                mTopBar.setTitle(getResources().getString(R.string.otc_order_confirmed));
                statusView = View.inflate(this, R.layout.view_otc_order_confirmed, null);
                initConfirmedView(statusView);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_payment_code:
                break;
        }
    }

    /**
     * 待确认
     */
    private void initConfirmedView(View view) {
        nodeProgress.setCurentNode(1);
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
}
