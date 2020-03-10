package com.android.tacu.module.otc.view;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.otc.contract.OtcManageBuySellDetailContract;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcMarketOrderAllModel;
import com.android.tacu.module.otc.model.OtcMarketOrderModel;
import com.android.tacu.module.otc.model.OtcTradeAllModel;
import com.android.tacu.module.otc.model.OtcTradeListModel;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.presenter.OtcManageBuySellDetailPresenter;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.MathHelper;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class OtcManageBuySellDetailActivity extends BaseActivity<OtcManageBuySellDetailPresenter> implements OtcManageBuySellDetailContract.IDetailView, View.OnClickListener {

    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshManage;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private final long DATA_TIME = 1000;

    private TextView tv_left;
    private TextView tv_right;
    private TextView tv_single_price;
    private TextView tv_single_limit_range;
    private TextView tv_finished_trade;
    private TextView tv_success_buy_title;
    private TextView tv_success_buy;
    private TextView tv_pay_after2_title;
    private TextView tv_pay_after2;
    private TextView tv_surplus_total;
    private TextView tv_yhk_pay;
    private TextView tv_wx_pay;
    private TextView tv_zfb_pay;
    private TextView tv_failure_trade;
    private TextView tv_fee_send;
    private TextView tv_record;

    private String orderId;
    private OtcOrderAdapter orderAdapter;

    private OtcMarketOrderAllModel allModel;
    private int start = 1;
    private List<OtcTradeAllModel> tradeModelList = new ArrayList<>();
    private Integer buyorsell;
    private Long currentTime;//当前服务器时间戳
    private long valueTime;
    private TimeModel timeModel;

    private SparseArray<TimeModel> timeArray = new SparseArray<>();

    private Handler timeHandler = new Handler();
    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            dealTime();
            if (timeHandler != null) {
                timeHandler.postDelayed(this, DATA_TIME);
            }
        }
    };

    /**
     * @param orderId 广告的id
     * @return
     */
    public static Intent createActivity(Context context, String orderId) {
        Intent intent = new Intent(context, OtcManageBuySellDetailActivity.class);
        intent.putExtra("orderId", orderId);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_manage_buy_sell_detail);
    }

    @Override
    protected void initView() {
        orderId = getIntent().getStringExtra("orderId");

        CustomTextHeaderView header = new CustomTextHeaderView(this);
        header.setPrimaryColors(ContextCompat.getColor(this, R.color.content_bg_color), ContextCompat.getColor(this, R.color.text_color));
        refreshManage.setRefreshHeader(header);
        refreshManage.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_default)));
        refreshManage.setEnableLoadmore(false);
        refreshManage.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                upload(false, true);
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                upload(false, false);
            }
        });

        orderAdapter = new OtcOrderAdapter();
        orderAdapter.setHeaderFooterEmpty(true, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_recyclerview_divider));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(orderAdapter);

        initHeader();
    }

    @Override
    protected OtcManageBuySellDetailPresenter createPresenter(OtcManageBuySellDetailPresenter mPresenter) {
        return new OtcManageBuySellDetailPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        upload(true, true);
        if (timeHandler != null && timeRunnable != null) {
            timeHandler.post(timeRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timeHandler != null && timeRunnable != null) {
            timeHandler.removeCallbacks(timeRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeHandler != null && timeRunnable != null) {
            timeHandler.removeCallbacks(timeRunnable);
        }
        cancelTime();
    }

    @Override
    public void hideRefreshView() {
        super.hideRefreshView();
        if (refreshManage != null && (refreshManage.isRefreshing() || refreshManage.isLoading())) {
            refreshManage.finishRefresh();
            refreshManage.finishLoadmore();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_record:
                if (allModel != null && allModel.orderModel != null) {
                    jumpTo(OtcManageBuySellRecordActivity.createActivity(this, allModel.orderModel.id));
                }
                break;
        }
    }

    @Override
    public void orderListOne(boolean isShowView, boolean isTop, OtcMarketOrderAllModel model) {
        this.allModel = model;
        if (model != null) {
            OtcMarketOrderModel orderModel = model.orderModel;
            if (orderModel != null) {
                String valueWei = " " + Constant.CNY;
                if (orderModel.buyorsell != null) { //1.买 2.卖
                    switch (orderModel.buyorsell) {
                        case 1:
                            mTopBar.setTitle(getResources().getString(R.string.buy_order_detail));
                            if (orderModel.money != null && orderModel.money == 1) {
                                valueWei = " " + Constant.CNY;
                            }
                            tv_left.setText(orderModel.amount + valueWei);
                            tv_right.setText(orderModel.num + " " + orderModel.currencyName);
                            tv_success_buy_title.setText(getResources().getString(R.string.success_buy));
                            tv_pay_after2_title.setText(getResources().getString(R.string.pay_after2));
                            if (!TextUtils.isEmpty(orderModel.remainAmount) && !TextUtils.isEmpty(orderModel.price)) {
                                double value = MathHelper.mul(Double.parseDouble(orderModel.remainAmount), Double.parseDouble(orderModel.price));
                                tv_surplus_total.setText(FormatterUtils.getFormatRoundDown(2, value) + valueWei);
                            } else {
                                tv_surplus_total.setText("0" + valueWei);
                            }
                            break;
                        case 2:
                            mTopBar.setTitle(getResources().getString(R.string.sell_order_detail));
                            tv_left.setText(orderModel.num + " " + orderModel.currencyName);
                            if (orderModel.money != null && orderModel.money == 1) {
                                valueWei = " " + Constant.CNY;
                            }
                            tv_right.setText(orderModel.amount + valueWei);
                            tv_success_buy_title.setText(getResources().getString(R.string.success_sell));
                            tv_pay_after2_title.setText(getResources().getString(R.string.get_money));
                            tv_surplus_total.setText(orderModel.remainAmount + " " + orderModel.currencyName);
                            break;
                    }

                    buyorsell = orderModel.buyorsell;
                    mPresenter.tradeList(isShowView, orderModel.id, null, start, 10, null, 16);
                }
                tv_single_price.setText("1 CNY/ACU");
                tv_single_limit_range.setText(orderModel.lowLimit + "~" + orderModel.highLimit + valueWei);
                tv_finished_trade.setText(orderModel.finishNum + getResources().getString(R.string.dan));
                tv_success_buy.setText(orderModel.tradeAmount + " " + orderModel.currencyName);
                if (!TextUtils.isEmpty(orderModel.tradeAmount) && !TextUtils.isEmpty(orderModel.price)) {
                    double value = MathHelper.mul(Double.parseDouble(orderModel.tradeAmount), Double.parseDouble(orderModel.price));
                    tv_pay_after2.setText(FormatterUtils.getFormatRoundDown(2, value) + valueWei);
                } else {
                    tv_pay_after2.setText("0" + valueWei);
                }
                tv_yhk_pay.setText(orderModel.cardPay + valueWei);
                tv_wx_pay.setText(orderModel.chatPay + valueWei);
                tv_zfb_pay.setText(orderModel.aliPay + valueWei);
                tv_failure_trade.setText(orderModel.failNum + getResources().getString(R.string.dan));
                tv_fee_send.setText(orderModel.feeAll + " " + orderModel.currencyName);
            }
        }
    }

    @Override
    public void tradeList(OtcTradeListModel model) {
        if (model != null) {
            if (model.list != null && model.list.size() > 0) {
                OtcTradeAllModel allModel = null;
                OtcMarketInfoModel infoModel = null;
                OtcTradeModel tradeModel = null;
                int notOwnUid = 0;
                int ownUid = spUtil.getUserUid();
                List<OtcTradeAllModel> list = new ArrayList<>();

                for (int i = 0; i < model.list.size(); i++) {
                    allModel = new OtcTradeAllModel();
                    tradeModel = model.list.get(i);
                    allModel.tradeModel = tradeModel;
                    if (tradeModel.buyuid != null && tradeModel.buyuid == ownUid && tradeModel.selluid != null && tradeModel.selluid != ownUid) {
                        notOwnUid = tradeModel.selluid;
                    } else if (tradeModel.selluid != null && tradeModel.selluid == ownUid && tradeModel.buyuid != null && tradeModel.buyuid != ownUid) {
                        notOwnUid = tradeModel.buyuid;
                    }
                    if (model.infoList != null && model.infoList.size() > 0) {
                        for (int j = 0; j < model.infoList.size(); j++) {
                            infoModel = model.infoList.get(j);
                            if (infoModel.uid != null && infoModel.uid.equals(notOwnUid)) {
                                allModel.infoModel = infoModel;
                            }
                        }
                    }
                    list.add(allModel);
                }

                if (tradeModelList != null && list.size() > 0) {
                    tradeModelList.addAll(list);
                } else {
                    tradeModelList = list;
                }
                if (tradeModelList != null && tradeModelList.size() > 0) {
                    orderAdapter.setNewData(tradeModelList);
                    if (tradeModelList.size() >= model.total) {
                        refreshManage.setEnableLoadmore(false);
                    } else {
                        start++;
                        refreshManage.setEnableLoadmore(true);
                    }
                }
            } else if (start == 1) {
                orderAdapter.setNewData(null);
                refreshManage.setEnableLoadmore(false);
            }
        } else if (start == 1) {
            orderAdapter.setNewData(null);
            refreshManage.setEnableLoadmore(false);
        }
    }

    @Override
    public void confirmOrderSuccess() {
        showToastSuccess(getResources().getString(R.string.success));
        upload(true, true);
    }

    @Override
    public void confirmCancelOrderSuccess() {
        showToastSuccess(getResources().getString(R.string.success));
        upload(true, true);
    }

    @Override
    public void currentTime(Long time) {
        this.currentTime = time;
        orderAdapter.notifyDataSetChanged();
    }

    private void initHeader() {
        View headerView = View.inflate(this, R.layout.header_otc_manage_buy_sell_detail, null);
        tv_left = headerView.findViewById(R.id.tv_left);
        tv_right = headerView.findViewById(R.id.tv_right);
        tv_single_price = headerView.findViewById(R.id.tv_single_price);
        tv_single_limit_range = headerView.findViewById(R.id.tv_single_limit_range);
        tv_finished_trade = headerView.findViewById(R.id.tv_finished_trade);
        tv_success_buy_title = headerView.findViewById(R.id.tv_success_buy_title);
        tv_success_buy = headerView.findViewById(R.id.tv_success_buy);
        tv_pay_after2_title = headerView.findViewById(R.id.tv_pay_after2_title);
        tv_pay_after2 = headerView.findViewById(R.id.tv_pay_after2);
        tv_surplus_total = headerView.findViewById(R.id.tv_surplus_total);
        tv_yhk_pay = headerView.findViewById(R.id.tv_yhk_pay);
        tv_wx_pay = headerView.findViewById(R.id.tv_wx_pay);
        tv_zfb_pay = headerView.findViewById(R.id.tv_zfb_pay);
        tv_failure_trade = headerView.findViewById(R.id.tv_failure_trade);
        tv_fee_send = headerView.findViewById(R.id.tv_fee_send);
        tv_record = headerView.findViewById(R.id.tv_record);

        tv_record.setOnClickListener(this);
        orderAdapter.addHeaderView(headerView);
    }

    private void upload(boolean isShowView, boolean isTop) {
        if (isTop) {
            start = 1;
        }
        if (start == 1 && tradeModelList != null && tradeModelList.size() > 0) {
            tradeModelList.clear();
        }
        currentTime = null;
        mPresenter.currentTime();
        mPresenter.orderListOne(isShowView, isTop, orderId);
    }

    // 1待确认 2 已确认待付款 3已付款待放币 4 仲裁  9 放币超时
    public class OtcOrderAdapter extends BaseQuickAdapter<OtcTradeAllModel, BaseViewHolder> {

        public OtcOrderAdapter() {
            super(R.layout.item_otc_manage_buy_sell_detail);
        }

        @Override
        protected void convert(BaseViewHolder holder, final OtcTradeAllModel item) {
            if (item.infoModel != null) {
                GlideUtils.disPlay(OtcManageBuySellDetailActivity.this, CommonUtils.getHead(item.infoModel.headImg), (ImageView) holder.getView(R.id.img_user));
                holder.setText(R.id.tv_user_name, item.infoModel.nickname + "(" + CommonUtils.nameXing(item.infoModel.secondName) + ")");
            }
            if (item.tradeModel != null) {
                if (item.tradeModel.payType != null) {
                    //支付类型 1 银行 2微信3支付宝
                    switch (item.tradeModel.payType) {
                        case 1:
                            holder.setImageResource(R.id.img_pay, R.mipmap.img_yhk);
                            break;
                        case 2:
                            holder.setImageResource(R.id.img_pay, R.mipmap.img_wx);
                            break;
                        case 3:
                            holder.setImageResource(R.id.img_pay, R.mipmap.img_zfb);
                            break;
                    }
                }
                holder.setText(R.id.tv_sell_num_title, getResources().getString(R.string.amount) + "(" + item.tradeModel.currencyName + ")");
                holder.setText(R.id.tv_receive_money_title, getResources().getString(R.string.amount_price) + "(" + Constant.CNY + ")");

                holder.setText(R.id.tv_sell_num, item.tradeModel.num);
                holder.setText(R.id.tv_receive_money, item.tradeModel.amount);

                holder.setGone(R.id.tv_order_status_title, true);
                holder.setGone(R.id.tv_order_status, true);
                holder.setGone(R.id.btn_left, false);
                holder.setGone(R.id.btn_center, false);
                holder.setGone(R.id.btn_right, true);

                ((QMUIRoundButtonDrawable) holder.getView(R.id.btn_right).getBackground()).setBgData(ContextCompat.getColorStateList(mContext, R.color.color_transparent));
                ((QMUIRoundButtonDrawable) holder.getView(R.id.btn_right).getBackground()).setStrokeData(UIUtils.dp2px(1), ContextCompat.getColorStateList(mContext, R.color.text_default));
                holder.setTextColor(R.id.btn_right, ContextCompat.getColor(mContext, R.color.text_default));
                switch (item.tradeModel.status) {
                    case 1:
                        holder.setGone(R.id.tv_order_status_title, false);
                        holder.setGone(R.id.tv_order_status, false);
                        holder.setGone(R.id.btn_left, true);
                        holder.setGone(R.id.btn_center, true);
                        holder.setGone(R.id.btn_right, true);

                        holder.setText(R.id.btn_right, getResources().getString(R.string.look_detail));

                        if (currentTime != null) {
                            if (!TextUtils.isEmpty(item.tradeModel.confirmEndTime)) {
                                valueTime = DateUtils.string2Millis(item.tradeModel.confirmEndTime, DateUtils.DEFAULT_PATTERN) - currentTime;
                                if (valueTime > 0) {
                                    timeArray.put(holder.getLayoutPosition(), new TimeModel(valueTime, (TextView) holder.getView(R.id.tv_operation_countdown)));
                                }
                            }
                        }
                        break;
                    case 2:
                        switch (buyorsell) {
                            case 1:
                                holder.setText(R.id.tv_order_status, getResources().getString(R.string.otc_order_payed));
                                ((QMUIRoundButtonDrawable) holder.getView(R.id.btn_right).getBackground()).setBgData(ContextCompat.getColorStateList(mContext, R.color.color_default));
                                ((QMUIRoundButtonDrawable) holder.getView(R.id.btn_right).getBackground()).setStrokeData(UIUtils.dp2px(1), ContextCompat.getColorStateList(mContext, R.color.color_transparent));
                                holder.setTextColor(R.id.btn_right, ContextCompat.getColor(mContext, R.color.text_white));
                                holder.setText(R.id.btn_right, getResources().getString(R.string.pay1));
                                break;
                            case 2:
                                //holder.setText(R.id.tv_order_status, getResources().getString(R.string.otc_order_payget));
                                holder.setText(R.id.btn_right, getResources().getString(R.string.look_detail));
                                break;
                        }
                        if (currentTime != null) {
                            if (!TextUtils.isEmpty(item.tradeModel.payEndTime)) {
                                valueTime = DateUtils.string2Millis(item.tradeModel.payEndTime, DateUtils.DEFAULT_PATTERN) - currentTime;
                                if (valueTime > 0) {
                                    timeArray.put(holder.getLayoutPosition(), new TimeModel(valueTime, (TextView) holder.getView(R.id.tv_operation_countdown)));
                                }
                            }
                        }
                        break;
                    case 3:
                        switch (buyorsell) {
                            case 1:
                                //holder.setText(R.id.tv_order_status, getResources().getString(R.string.otc_order_coinget));
                                break;
                            case 2:
                                holder.setText(R.id.tv_order_status, getResources().getString(R.string.otc_order_coined));
                                break;
                        }
                        holder.setText(R.id.btn_right, getResources().getString(R.string.look_detail));
                        if (currentTime != null) {
                            if (!TextUtils.isEmpty(item.tradeModel.transCoinEndTime)) {
                                valueTime = DateUtils.string2Millis(item.tradeModel.transCoinEndTime, DateUtils.DEFAULT_PATTERN) - currentTime;
                                if (valueTime > 0) {
                                    timeArray.put(holder.getLayoutPosition(), new TimeModel(valueTime, (TextView) holder.getView(R.id.tv_operation_countdown)));
                                }
                            }
                        }
                        break;
                    case 4:
                        holder.setText(R.id.tv_order_status, getResources().getString(R.string.otc_order_arbitration));
                        holder.setText(R.id.btn_right, getResources().getString(R.string.look_detail));
                        holder.setText(R.id.tv_operation_countdown, getResources().getString(R.string.pay_timeout));
                        break;
                    case 9:
                        switch (buyorsell) {
                            case 1:
                                //holder.setText(R.id.tv_order_status, getResources().getString(R.string.otc_order_coinget));
                                break;
                            case 2:
                                holder.setText(R.id.tv_order_status, getResources().getString(R.string.otc_order_coined));
                                break;
                        }
                        holder.setText(R.id.btn_right, getResources().getString(R.string.look_detail));
                        holder.setText(R.id.tv_operation_countdown, getResources().getString(R.string.pay_timeout));
                        break;
                }

                holder.setOnClickListener(R.id.btn_left, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.confirmOrder(item.tradeModel.id);
                    }
                });
                holder.setOnClickListener(R.id.btn_center, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.confirmCancelOrder(item.tradeModel.id);
                    }
                });
                holder.setOnClickListener(R.id.btn_right, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.tradeModel != null) {
                            jumpTo(OtcOrderDetailActivity.createActivity(OtcManageBuySellDetailActivity.this, item.tradeModel.orderNo));
                        }
                    }
                });
            }
        }
    }

    private void dealTime() {
        if (timeArray != null && timeArray.size() > 0) {
            for (int i = 0; i < timeArray.size(); i++) {
                timeModel = timeArray.get(timeArray.keyAt(i));
                if (timeModel != null) {
                    timeModel.tv.setText(DateUtils.getCountDownTime1(timeModel.millisInFuture));
                    timeModel.millisInFuture = timeModel.millisInFuture - DATA_TIME;
                    if (timeModel.millisInFuture <= 0) {
                        timeArray.remove(timeArray.keyAt(i));
                    }
                }
            }
        }
    }

    private void cancelTime() {
        if (timeArray != null && timeArray.size() > 0) {
            timeArray.clear();
        }
    }

    class TimeModel implements Serializable {
        public long millisInFuture;
        public TextView tv;

        public TimeModel(long millisInFuture, TextView tv) {
            this.millisInFuture = millisInFuture;
            this.tv = tv;
        }
    }
}
