package com.android.tacu.module.otc.view;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.otc.contract.OtcManageWaitContract;
import com.android.tacu.module.otc.dialog.OtcTradeDialogUtils;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcTradeAllModel;
import com.android.tacu.module.otc.model.OtcTradeListModel;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.presenter.OtcManageWaitPresenter;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.ShowToast;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.android.tacu.widget.dialog.DroidDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class OtcManageWaitFragment extends BaseFragment<OtcManageWaitPresenter> implements OtcManageWaitContract.IView {

    private final long DATA_TIME = 1000;

    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshManage;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private View emptyView;

    private String orderId;
    private OtcOrderAdapter orderAdapter;
    private List<OtcTradeAllModel> tradeModelList = new ArrayList<>();

    private int start = 1;
    private Long currentTime;//当前服务器时间戳
    private long valueTime;
    private TimeModel timeModel;
    private boolean isFirst = true;
    private DroidDialog droidDialog;

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

    public static OtcManageWaitFragment newInstance(String orderId) {
        Bundle bundle = new Bundle();
        bundle.putString("orderId", orderId);
        OtcManageWaitFragment fragment = new OtcManageWaitFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initLazy() {
        super.initLazy();
        if (isFirst) {
            upload(true, true);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            orderId = bundle.getString("orderId");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_otc_manage_wait;
    }

    @Override
    protected void initData(View view) {
        emptyView = View.inflate(getContext(), R.layout.view_empty, null);

        CustomTextHeaderView header = new CustomTextHeaderView(getContext());
        header.setPrimaryColors(ContextCompat.getColor(getContext(), R.color.content_bg_color), ContextCompat.getColor(getContext(), R.color.text_color));
        refreshManage.setRefreshHeader(header);
        refreshManage.setRefreshFooter(new BallPulseFooter(getContext()).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(getContext(), R.color.color_default)));
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
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.item_recyclerview_divider));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(orderAdapter);
    }

    @Override
    protected OtcManageWaitPresenter createPresenter(OtcManageWaitPresenter mPresenter) {
        return new OtcManageWaitPresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
        upload(isFirst, true);
        if (timeHandler != null && timeRunnable != null) {
            timeHandler.post(timeRunnable);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (timeHandler != null && timeRunnable != null) {
            timeHandler.removeCallbacks(timeRunnable);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timeHandler != null && timeRunnable != null) {
            timeHandler.removeCallbacks(timeRunnable);
        }
        if (droidDialog != null && droidDialog.isShowing()) {
            droidDialog.dismiss();
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
                orderAdapter.setEmptyView(emptyView);
                refreshManage.setEnableLoadmore(false);
            }
        } else if (start == 1) {
            orderAdapter.setNewData(null);
            orderAdapter.setEmptyView(emptyView);
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
    public void payOrderSuccess() {
        showToastSuccess(getResources().getString(R.string.submit_success));
        upload(true, true);
    }

    @Override
    public void finishOrderSuccess() {
        showToastSuccess(getResources().getString(R.string.submit_success));
        upload(true, true);
    }

    @Override
    public void currentTime(Long time) {
        this.currentTime = time;
        orderAdapter.notifyDataSetChanged();
    }

    private void upload(boolean isShowView, boolean isTop) {
        if (!isVisibleToUser) {
            return;
        }
        if (isTop) {
            start = 1;
        }
        if (isFirst) {
            isFirst = false;
        }
        if (start == 1 && tradeModelList != null && tradeModelList.size() > 0) {
            tradeModelList.clear();
        }
        currentTime = null;
        mPresenter.currentTime();
        mPresenter.tradeList(isShowView, orderId, null, start, 10, null, 16);
    }

    // 1待确认 2 已确认待付款 3已付款待放币 4 仲裁  9 放币超时
    public class OtcOrderAdapter extends BaseQuickAdapter<OtcTradeAllModel, BaseViewHolder> {

        public OtcOrderAdapter() {
            super(R.layout.item_otc_manage_buy_sell_detail);
        }

        @Override
        protected void convert(BaseViewHolder holder, final OtcTradeAllModel item) {
            if (item.infoModel != null) {
                GlideUtils.disPlay(getContext(), CommonUtils.getHead(item.infoModel.headImg), (ImageView) holder.getView(R.id.img_user));
                holder.setText(R.id.tv_user_name, item.infoModel.nickname + "(" + CommonUtils.nameXing(item.infoModel.firstName, item.infoModel.secondName) + ")");
                holder.setText(R.id.tv_mobile, item.infoModel.mobile);
            }
            if (item.tradeModel != null) {
                holder.setText(R.id.tv_coin_name, item.tradeModel.currencyName);
                Boolean isBuy = null;
                if (item.tradeModel.buyuid == spUtil.getUserUid()) {
                    isBuy = true;
                } else if (item.tradeModel.selluid == spUtil.getUserUid()) {
                    isBuy = false;
                }
                if (isBuy) {
                    holder.setText(R.id.tv_buyorsell, getResources().getString(R.string.buy));
                    holder.setTextColor(R.id.tv_buyorsell, ContextCompat.getColor(mContext, R.color.color_otc_buy));
                } else {
                    holder.setText(R.id.tv_buyorsell, getResources().getString(R.string.sell));
                    holder.setTextColor(R.id.tv_buyorsell, ContextCompat.getColor(mContext, R.color.color_otc_sell));
                }
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

                holder.setGone(R.id.btn_left, false);
                holder.setGone(R.id.btn_center, false);
                holder.setGone(R.id.btn_right, true);

                switch (item.tradeModel.status) {
                    case 1:
                        holder.setText(R.id.tv_order_status, getResources().getString(R.string.otc_order_confirmed));
                        holder.setTextColor(R.id.tv_order_status, ContextCompat.getColor(mContext, R.color.text_color));
                        holder.setText(R.id.tv_operation_countdown_title, getResources().getString(R.string.confirm_counterdown));
                        if (item.tradeModel.merchantId == spUtil.getUserUid()) {
                            holder.setGone(R.id.btn_left, true);
                            holder.setGone(R.id.btn_center, true);
                            ((QMUIRoundButtonDrawable) holder.getView(R.id.btn_center).getBackground()).setBgData(ContextCompat.getColorStateList(mContext, R.color.color_grey));
                        }

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
                        if (isBuy) {
                            holder.setText(R.id.tv_order_status, getResources().getString(R.string.wait_payed));
                            holder.setGone(R.id.btn_center, true);
                            ((QMUIRoundButtonDrawable) holder.getView(R.id.btn_center).getBackground()).setBgData(ContextCompat.getColorStateList(mContext, R.color.color_default));
                            holder.setText(R.id.btn_right, getResources().getString(R.string.paid));
                        } else {
                            holder.setText(R.id.tv_order_status, getResources().getString(R.string.wait_payget));
                        }
                        holder.setTextColor(R.id.tv_order_status, ContextCompat.getColor(mContext, R.color.text_default));
                        holder.setText(R.id.tv_operation_countdown_title, getResources().getString(R.string.pay_counterdown));

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
                        if (isBuy) {
                            holder.setText(R.id.tv_order_status, getResources().getString(R.string.wait_coinget));
                        } else {
                            holder.setText(R.id.tv_order_status, getResources().getString(R.string.wait_coined));
                            holder.setGone(R.id.btn_center, true);
                            ((QMUIRoundButtonDrawable) holder.getView(R.id.btn_center).getBackground()).setBgData(ContextCompat.getColorStateList(mContext, R.color.color_default));
                            holder.setText(R.id.btn_right, getResources().getString(R.string.go_coined));
                        }
                        holder.setTextColor(R.id.tv_order_status, ContextCompat.getColor(mContext, R.color.text_color));
                        holder.setText(R.id.tv_operation_countdown_title, getResources().getString(R.string.coined_counterdown));

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
                        holder.setText(R.id.tv_order_status, getResources().getString(R.string.arbitrationing));
                        holder.setTextColor(R.id.tv_order_status, ContextCompat.getColor(mContext, R.color.color_arbitration));
                        holder.setText(R.id.tv_operation_countdown_title, getResources().getString(R.string.arbitration_countdown));

                        holder.setText(R.id.tv_operation_countdown, getResources().getString(R.string.pay_timeout));
                        break;
                    case 9:
                        holder.setText(R.id.tv_order_status, getResources().getString(R.string.coined_timeout));
                        holder.setTextColor(R.id.tv_order_status, ContextCompat.getColor(mContext, R.color.color_arbitration_finish));
                        holder.setText(R.id.tv_operation_countdown_title, getResources().getString(R.string.coined_counterdown));

                        if (!isBuy) {
                            holder.setGone(R.id.btn_center, true);
                            ((QMUIRoundButtonDrawable) holder.getView(R.id.btn_center).getBackground()).setBgData(ContextCompat.getColorStateList(mContext, R.color.color_default));
                            holder.setText(R.id.btn_right, getResources().getString(R.string.go_coined));
                        }

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
                        switch (item.tradeModel.status) {
                            case 1:
                                mPresenter.confirmCancelOrder(item.tradeModel.id);
                                break;
                            case 2:
                                mPresenter.payOrder(item.tradeModel.id, null);
                                break;
                            case 3:
                            case 9:
                                if (!OtcTradeDialogUtils.isDialogShow(mContext)) {
                                    showSure(item.tradeModel);
                                }
                                break;
                        }
                    }
                });
                holder.setOnClickListener(R.id.btn_right, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.tradeModel != null) {
                            jumpTo(OtcOrderDetailActivity.createActivity(getContext(), item.tradeModel.orderNo));
                        }
                    }
                });
            }
        }
    }

    private void showSure(final OtcTradeModel tradeModel) {
        View view = View.inflate(getContext(), R.layout.dialog_coined_confirm, null);
        final CheckBox cb_xieyi = view.findViewById(R.id.cb_xieyi);
        final QMUIRoundEditText edit_trade_pwd = view.findViewById(R.id.edit_trade_pwd);

        if (spUtil.getPwdVisibility()) {
            edit_trade_pwd.setVisibility(View.VISIBLE);
        } else {
            edit_trade_pwd.setVisibility(View.GONE);
        }

        droidDialog = new DroidDialog.Builder(getContext())
                .title(getResources().getString(R.string.coined_confirm))
                .viewCustomLayout(view)
                .positiveButton(getResources().getString(R.string.sure), false, new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        if (!cb_xieyi.isChecked()) {
                            ShowToast.error(getContext().getResources().getString(R.string.please_check_xieyi));
                            return;
                        }
                        String pwdString = edit_trade_pwd.getText().toString();
                        if (spUtil.getPwdVisibility() && TextUtils.isEmpty(pwdString)) {
                            ShowToast.error(getResources().getString(R.string.please_input_trade_password));
                            return;
                        }

                        if (tradeModel != null) {
                            droidDialog.dismiss();
                            mPresenter.finishOrder(tradeModel.id, spUtil.getPwdVisibility() ? Md5Utils.encryptFdPwd(pwdString, spUtil.getUserUid()).toLowerCase() : null);
                        }
                    }
                })
                .negativeButton(getResources().getString(R.string.cancel), null)
                .cancelable(false, false)
                .show();
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
