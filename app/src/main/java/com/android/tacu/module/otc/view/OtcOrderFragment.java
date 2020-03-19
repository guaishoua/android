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
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.OTCOrderVisibleHintEvent;
import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.otc.contract.OtcOrderContract;
import com.android.tacu.module.otc.dialog.OtcTradeDialogUtils;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcTradeAllModel;
import com.android.tacu.module.otc.model.OtcTradeListModel;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.presenter.OtcOrderPresenter;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.LogUtils;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.UIUtils;
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

public class OtcOrderFragment extends BaseFragment<OtcOrderPresenter> implements OtcOrderContract.IView {

    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshManage;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private final long DATA_TIME = 1000;

    private int orderStatus = 0;
    //0=全部 1=买 2=卖
    private int buyOrSell;
    private OtcOrderAdapter orderAdapter;

    private int start = 1;
    private boolean isFirst = true;
    private boolean isVisibleToUserParent = false;
    private List<OtcTradeAllModel> tradeModelList = new ArrayList<>();

    private DroidDialog droidDialog;
    private DroidDialog sureDialog;

    private Long currentTime;//当前服务器时间戳
    private long valueTime;
    private SparseArray<TimeModel> timeArray = new SparseArray<>();
    private TimeModel timeModel;

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

    public static OtcOrderFragment newInstance(int buyOrSell, int orderStatus) {
        Bundle bundle = new Bundle();
        bundle.putInt("buyOrSell", buyOrSell);
        bundle.putInt("orderStatus", orderStatus);
        OtcOrderFragment fragment = new OtcOrderFragment();
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
            buyOrSell = bundle.getInt("buyOrSell");
            orderStatus = bundle.getInt("orderStatus");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_order;
    }

    @Override
    protected void initData(View view) {
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
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.item_recyclerview_divider_dp10));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(orderAdapter);

        if (buyOrSell == 0) {
            isVisibleToUserParent = true;
        }
    }

    @Override
    protected OtcOrderPresenter createPresenter(OtcOrderPresenter mPresenter) {
        return new OtcOrderPresenter();
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
        cancelTime();
        if (droidDialog != null && droidDialog.isShowing()) {
            droidDialog.dismiss();
        }
        if (sureDialog != null && sureDialog.isShowing()) {
            sureDialog.dismiss();
        }
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
    protected void receiveEvent(BaseEvent event) {
        super.receiveEvent(event);
        if (event != null) {
            switch (event.getCode()) {
                case EventConstant.OTCOrderVisibleCode:
                    OTCOrderVisibleHintEvent otcOrderVisibleHintEvent = (OTCOrderVisibleHintEvent) event.getData();
                    isVisibleToUserParent = otcOrderVisibleHintEvent.isVisibleToUser();
                    int buyOrSell1 = otcOrderVisibleHintEvent.getBuyOrSell();
                    if (buyOrSell1 == buyOrSell) {
                        upload(isFirst, true);
                    }

                    LogUtils.i("jiazhen", "isVisibleToUserParent=" + isVisibleToUserParent + " buyOrSell1=" + buyOrSell1);
                    break;
            }
        }
    }

    @Override
    public void tradeList(boolean isClean, OtcTradeListModel model) {
        if (model != null) {
            if (model.list != null && model.list.size() > 0) {
                if (isClean) {
                    cancelTime();
                }

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
                cancelTime();
                orderAdapter.setNewData(null);
                refreshManage.setEnableLoadmore(false);
            }
        } else if (start == 1) {
            cancelTime();
            orderAdapter.setNewData(null);
            refreshManage.setEnableLoadmore(false);
        }
    }

    @Override
    public void confirmOrderSuccess() {
        showToastSuccess(getResources().getString(R.string.order_confirm));
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
        if (!isVisibleToUserParent || !isVisibleToUser) {
            return;
        }
        if (isFirst) {
            isFirst = false;
        }
        if (isTop) {
            start = 1;
        }
        if (start == 1 && tradeModelList != null && tradeModelList.size() > 0) {
            tradeModelList.clear();
        }
        currentTime = null;
        mPresenter.currentTime();
        mPresenter.tradeList(isShowView, isTop, null, null, start, 10, buyOrSell, orderStatus);
    }

    public class OtcOrderAdapter extends BaseQuickAdapter<OtcTradeAllModel, BaseViewHolder> {

        private String otcPhone;

        public OtcOrderAdapter() {
            super(R.layout.item_otc_order);
        }

        @Override
        protected void convert(BaseViewHolder holder, final OtcTradeAllModel item) {
            if (item.infoModel != null) {
                holder.setText(R.id.tv_nickname, item.infoModel.nickname + "(" + CommonUtils.nameXing(item.infoModel.firstName, item.infoModel.secondName) + ")");
                if (item.infoModel.merchantStatus == 0) {
                    holder.setText(R.id.tv_offline, getResources().getString(R.string.offline));
                    holder.setTextColor(R.id.tv_offline, ContextCompat.getColor(getContext(), R.color.text_grey));
                } else {
                    holder.setText(R.id.tv_offline, getResources().getString(R.string.online));
                    holder.setTextColor(R.id.tv_offline, ContextCompat.getColor(getContext(), R.color.text_default));
                }
            }
            if (item.tradeModel != null) {
                holder.setText(R.id.tv_name, item.tradeModel.currencyName);
                if (item.tradeModel.buyuid == spUtil.getUserUid()) {
                    holder.setText(R.id.tv_buy_status, getResources().getString(R.string.buy));
                    holder.setTextColor(R.id.tv_buy_status, ContextCompat.getColor(getContext(), R.color.color_otc_buy));
                } else if (item.tradeModel.selluid == spUtil.getUserUid()) {
                    holder.setText(R.id.tv_buy_status, getResources().getString(R.string.sell));
                    holder.setTextColor(R.id.tv_buy_status, ContextCompat.getColor(getContext(), R.color.color_otc_sell));
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

                holder.setGone(R.id.rl_red, false);
                if (item.tradeModel.buyuid == spUtil.getUserUid() && item.tradeModel.buyreadstatus != null && item.tradeModel.buyreadstatus == 0) {
                    holder.setGone(R.id.rl_red, true);
                } else if (item.tradeModel.selluid == spUtil.getUserUid() && item.tradeModel.sellreadstatus != null && item.tradeModel.sellreadstatus == 0) {
                    holder.setGone(R.id.rl_red, true);
                }

                holder.setGone(R.id.view_four, false);
                holder.setGone(R.id.view_three, false);
                holder.setGone(R.id.img_leftbottom, false);
                holder.setGone(R.id.img_rightbottom, false);
                holder.setGone(R.id.img_leftbottom1, false);
                holder.setGone(R.id.img_rightbottom1, false);
                holder.setGone(R.id.tv_time_title, true);
                holder.setGone(R.id.tv_time, true);
                holder.setGone(R.id.btn_left, false);
                holder.setTextColor(R.id.tv_lefttop, ContextCompat.getColor(mContext, R.color.text_color));
                holder.setTextColor(R.id.tv_righttop, ContextCompat.getColor(mContext, R.color.text_color));

                holder.setTextColor(R.id.tv_leftbottom, ContextCompat.getColor(mContext, R.color.text_color));
                holder.setTextColor(R.id.tv_rightbottom, ContextCompat.getColor(mContext, R.color.text_color));
                holder.setTextColor(R.id.tv_leftbottom1, ContextCompat.getColor(mContext, R.color.text_color));
                holder.setTextColor(R.id.tv_rightbottom1, ContextCompat.getColor(mContext, R.color.text_color));

                ((QMUIRoundButtonDrawable) holder.getView(R.id.btn_right).getBackground()).setBgData(ContextCompat.getColorStateList(mContext, R.color.color_transparent));
                ((QMUIRoundButtonDrawable) holder.getView(R.id.btn_right).getBackground()).setStrokeData(UIUtils.dp2px(1), ContextCompat.getColorStateList(mContext, R.color.text_default));
                holder.setTextColor(R.id.btn_right, ContextCompat.getColor(mContext, R.color.text_default));
                holder.setText(R.id.btn_right, getResources().getString(R.string.look_order));

                if (item.tradeModel.status != null) {
                    // 1待确认 2 已确认待付款 3已付款待放币 4 仲裁 5 未确认超时取消 6 拒绝订单 7 付款超时取消 8放弃支付 9 放币超时  10放币完成
                    // 12裁决完成 13裁决完成

                    holder.setText(R.id.tv_leftbottom_title, getResources().getString(R.string.trade_num) + "(" + item.tradeModel.currencyName + ")");
                    holder.setText(R.id.tv_rightbottom_title, getResources().getString(R.string.trade_price) + "(CNY)");
                    holder.setText(R.id.tv_leftbottom_title1, getResources().getString(R.string.trade_num) + "(" + item.tradeModel.currencyName + ")");
                    holder.setText(R.id.tv_rightbottom_title1, getResources().getString(R.string.trade_price) + "(CNY)");

                    holder.setText(R.id.tv_leftbottom, item.tradeModel.num);
                    holder.setText(R.id.tv_rightbottom, item.tradeModel.amount);
                    holder.setText(R.id.tv_leftbottom1, item.tradeModel.num);
                    holder.setText(R.id.tv_rightbottom1, item.tradeModel.amount);

                    switch (item.tradeModel.status) {
                        case 1:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_confirmed));
                            holder.setTextColor(R.id.tv_status, ContextCompat.getColor(mContext, R.color.text_color));
                            holder.setGone(R.id.view_four, true);
                            holder.setText(R.id.tv_lefttop_title, getResources().getString(R.string.order_time));
                            holder.setText(R.id.tv_righttop_title, getResources().getString(R.string.mobile));
                            holder.setText(R.id.tv_lefttop, item.tradeModel.createTime);

                            otcPhone = "";
                            if (item.tradeModel.merchantId == spUtil.getUserUid()) {
                                otcPhone = item.infoModel.mobile;

                                holder.setGone(R.id.btn_left, true);
                                holder.setText(R.id.btn_left, getResources().getString(R.string.confirm_order));
                            } else {
                                otcPhone = item.tradeModel.explaininfo;
                            }
                            holder.setText(R.id.tv_righttop, otcPhone);

                            if (currentTime != null) {
                                if (!TextUtils.isEmpty(item.tradeModel.confirmEndTime)) {
                                    valueTime = DateUtils.string2Millis(item.tradeModel.confirmEndTime, DateUtils.DEFAULT_PATTERN) - currentTime;
                                    if (valueTime > 0) {
                                        timeArray.put(holder.getLayoutPosition(), new TimeModel(valueTime, (TextView) holder.getView(R.id.tv_time)));
                                    } else {
                                        holder.setText(R.id.tv_time, getResources().getString(R.string.timeouted));
                                    }
                                }
                            }
                            break;
                        case 2:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_payed));
                            holder.setTextColor(R.id.tv_status, ContextCompat.getColor(mContext, R.color.text_default));
                            holder.setGone(R.id.view_four, true);
                            holder.setText(R.id.tv_lefttop_title, getResources().getString(R.string.order_time));
                            holder.setText(R.id.tv_righttop_title, getResources().getString(R.string.mobile));
                            holder.setText(R.id.tv_lefttop, item.tradeModel.createTime);

                            otcPhone = "";
                            if (item.tradeModel.merchantId == spUtil.getUserUid()) {
                                otcPhone = item.infoModel.mobile;
                            } else {
                                otcPhone = item.tradeModel.explaininfo;
                            }
                            holder.setText(R.id.tv_righttop, otcPhone);

                            holder.setTextColor(R.id.tv_rightbottom, ContextCompat.getColor(mContext, R.color.color_otc_buy));

                            if (item.tradeModel.buyuid == spUtil.getUserUid()) {
                                holder.setText(R.id.btn_right, getResources().getString(R.string.go_pay1));
                                ((QMUIRoundButtonDrawable) holder.getView(R.id.btn_right).getBackground()).setBgData(ContextCompat.getColorStateList(mContext, R.color.color_default));
                                holder.setTextColor(R.id.btn_right, ContextCompat.getColor(mContext, R.color.text_color));
                            }

                            if (currentTime != null) {
                                if (!TextUtils.isEmpty(item.tradeModel.payEndTime)) {
                                    valueTime = DateUtils.string2Millis(item.tradeModel.payEndTime, DateUtils.DEFAULT_PATTERN) - currentTime;
                                    if (valueTime > 0) {
                                        timeArray.put(holder.getLayoutPosition(), new TimeModel(valueTime, (TextView) holder.getView(R.id.tv_time)));
                                    } else {
                                        holder.setText(R.id.tv_time, getResources().getString(R.string.timeouted));
                                    }
                                }
                            }
                            break;
                        case 3:
                        case 9:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_coined));
                            holder.setTextColor(R.id.tv_status, ContextCompat.getColor(mContext, R.color.color_coined));
                            holder.setGone(R.id.view_four, true);
                            holder.setText(R.id.tv_lefttop_title, getResources().getString(R.string.order_time));
                            holder.setText(R.id.tv_righttop_title, getResources().getString(R.string.mobile));
                            holder.setText(R.id.tv_lefttop, item.tradeModel.createTime);

                            otcPhone = "";
                            if (item.tradeModel.merchantId == spUtil.getUserUid()) {
                                otcPhone = item.infoModel.mobile;
                            } else {
                                otcPhone = item.tradeModel.explaininfo;
                            }
                            holder.setText(R.id.tv_righttop, otcPhone);

                            holder.setTextColor(R.id.tv_leftbottom, ContextCompat.getColor(mContext, R.color.color_otc_sell));
                            holder.setTextColor(R.id.tv_rightbottom, ContextCompat.getColor(mContext, R.color.color_otc_buy));
                            holder.setGone(R.id.img_leftbottom, true);
                            holder.setGone(R.id.img_rightbottom, true);
                            holder.setImageResource(R.id.img_leftbottom, R.drawable.icon_auth_failure);
                            holder.setImageResource(R.id.img_rightbottom, R.drawable.icon_auth_success);

                            if (item.tradeModel.selluid == spUtil.getUserUid()) {
                                holder.setGone(R.id.btn_left, true);
                                holder.setText(R.id.btn_left, getResources().getString(R.string.confirm_coined));
                            }

                            if (item.tradeModel.status == 3) {
                                if (currentTime != null) {
                                    if (!TextUtils.isEmpty(item.tradeModel.transCoinEndTime)) {
                                        valueTime = DateUtils.string2Millis(item.tradeModel.transCoinEndTime, DateUtils.DEFAULT_PATTERN) - currentTime;
                                        if (valueTime > 0) {
                                            timeArray.put(holder.getLayoutPosition(), new TimeModel(valueTime, (TextView) holder.getView(R.id.tv_time)));
                                        } else {
                                            holder.setText(R.id.tv_time, getResources().getString(R.string.timeouted));
                                        }
                                    }
                                }
                            } else {
                                holder.setText(R.id.tv_time, getResources().getString(R.string.timeouted));
                            }
                            break;
                        case 4:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_arbitration));
                            holder.setTextColor(R.id.tv_status, ContextCompat.getColor(mContext, R.color.color_arbitration));
                            holder.setGone(R.id.view_four, true);
                            holder.setText(R.id.tv_lefttop_title, getResources().getString(R.string.order_time));
                            holder.setText(R.id.tv_righttop_title, getResources().getString(R.string.duifang_shensu));
                            holder.setText(R.id.tv_lefttop, item.tradeModel.createTime);
                            if (item.tradeModel.buyuid == spUtil.getUserUid()) {
                                holder.setText(R.id.tv_righttop_title, getResources().getString(R.string.duifang_shensu));
                            } else if (item.tradeModel.selluid == spUtil.getUserUid()) {
                                holder.setText(R.id.tv_righttop_title, getResources().getString(R.string.tijiao_shensu));
                            }
                            if (item.tradeModel.beArbitrateUid != null && item.tradeModel.beArbitrateUid != 0) {
                                holder.setText(R.id.tv_righttop, getResources().getString(R.string.submited));
                            } else {
                                holder.setText(R.id.tv_righttop, getResources().getString(R.string.unsubmit));
                            }
                            holder.setTextColor(R.id.tv_righttop, ContextCompat.getColor(mContext, R.color.color_otc_sell));
                            holder.setTextColor(R.id.tv_leftbottom, ContextCompat.getColor(mContext, R.color.color_otc_sell));
                            holder.setTextColor(R.id.tv_rightbottom, ContextCompat.getColor(mContext, R.color.color_otc_buy));
                            holder.setGone(R.id.img_leftbottom, true);
                            holder.setGone(R.id.img_rightbottom, true);
                            holder.setImageResource(R.id.img_leftbottom, R.drawable.icon_auth_failure);
                            holder.setImageResource(R.id.img_rightbottom, R.drawable.icon_auth_success);

                            if (currentTime != null) {
                                if (!TextUtils.isEmpty(item.tradeModel.arbitrationEndTime)) {
                                    valueTime = DateUtils.string2Millis(item.tradeModel.arbitrationEndTime, DateUtils.DEFAULT_PATTERN) - currentTime;
                                    if (valueTime > 0) {
                                        timeArray.put(holder.getLayoutPosition(), new TimeModel(valueTime, (TextView) holder.getView(R.id.tv_time)));
                                    } else {
                                        holder.setText(R.id.tv_time, getResources().getString(R.string.timeouted));
                                    }
                                }
                            }
                            break;
                        case 5:
                        case 7:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_timeout));
                            holder.setTextColor(R.id.tv_status, ContextCompat.getColor(mContext, R.color.color_arbitration));
                            holder.setGone(R.id.view_three, true);
                            holder.setText(R.id.tv_top_title, getResources().getString(R.string.order_id));
                            holder.setText(R.id.tv_top, item.tradeModel.orderNo);
                            holder.setTextColor(R.id.tv_rightbottom, ContextCompat.getColor(mContext, R.color.color_otc_buy));

                            holder.setGone(R.id.tv_time_title, false);
                            holder.setGone(R.id.tv_time, false);
                            break;
                        case 6:
                        case 8:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_cancel));
                            holder.setTextColor(R.id.tv_status, ContextCompat.getColor(mContext, R.color.text_grey));
                            holder.setGone(R.id.view_three, true);
                            holder.setText(R.id.tv_top_title, getResources().getString(R.string.order_id));
                            holder.setText(R.id.tv_top, item.tradeModel.orderNo);
                            holder.setTextColor(R.id.tv_rightbottom, ContextCompat.getColor(mContext, R.color.color_otc_buy));

                            holder.setGone(R.id.tv_time_title, false);
                            holder.setGone(R.id.tv_time, false);
                            break;
                        case 10:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_finished));
                            holder.setTextColor(R.id.tv_status, ContextCompat.getColor(mContext, R.color.color_finish));
                            holder.setGone(R.id.view_three, true);
                            holder.setText(R.id.tv_top_title, getResources().getString(R.string.order_id));
                            holder.setText(R.id.tv_top, item.tradeModel.orderNo);
                            holder.setTextColor(R.id.tv_leftbottom, ContextCompat.getColor(mContext, R.color.color_otc_buy));
                            holder.setTextColor(R.id.tv_rightbottom, ContextCompat.getColor(mContext, R.color.color_otc_buy));
                            holder.setGone(R.id.img_leftbottom1, true);
                            holder.setGone(R.id.img_rightbottom1, true);

                            holder.setImageResource(R.id.img_leftbottom1, R.drawable.icon_auth_success);
                            holder.setImageResource(R.id.img_rightbottom1, R.drawable.icon_auth_success);

                            holder.setGone(R.id.tv_time_title, false);
                            holder.setGone(R.id.tv_time, false);
                            break;
                        case 12:
                        case 13:
                            holder.setText(R.id.tv_status, getResources().getString(R.string.otc_order_adjude));
                            holder.setTextColor(R.id.tv_status, ContextCompat.getColor(mContext, R.color.color_arbitration_finish));
                            holder.setGone(R.id.view_four, true);
                            holder.setText(R.id.tv_lefttop_title, getResources().getString(R.string.order_time));
                            holder.setText(R.id.tv_righttop_title, getResources().getString(R.string.arbitration_result1));
                            holder.setText(R.id.tv_lefttop, item.tradeModel.createTime);
                            holder.setText(R.id.tv_righttop, getResources().getString(R.string.arbitrationed));
                            holder.setTextColor(R.id.tv_rightbottom, ContextCompat.getColor(mContext, R.color.color_otc_buy));
                            holder.setTextColor(R.id.tv_righttop, ContextCompat.getColor(mContext, R.color.color_arbitration_finish));
                            holder.setGone(R.id.img_leftbottom, true);
                            holder.setGone(R.id.img_rightbottom, true);

                            if (item.tradeModel.status == 12) {
                                holder.setImageResource(R.id.img_leftbottom, R.drawable.icon_auth_success);
                                holder.setTextColor(R.id.tv_leftbottom, ContextCompat.getColor(mContext, R.color.color_otc_buy));
                            } else if (item.tradeModel.status == 13) {
                                holder.setImageResource(R.id.img_leftbottom, R.drawable.icon_auth_failure);
                                holder.setTextColor(R.id.tv_leftbottom, ContextCompat.getColor(mContext, R.color.color_otc_sell));
                            }
                            holder.setImageResource(R.id.img_rightbottom, R.drawable.icon_auth_success);

                            holder.setGone(R.id.tv_time_title, false);
                            holder.setGone(R.id.tv_time, false);
                            break;

                    }
                } else {
                    holder.setText(R.id.tv_status, "");
                }
            }

            holder.setOnClickListener(R.id.btn_left, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.tradeModel.status != null) {
                        switch (item.tradeModel.status) {
                            case 1://确认订单
                                mPresenter.confirmOrder(item.tradeModel.id);
                                break;
                            case 3://放币
                            case 9:
                                if (!OtcTradeDialogUtils.isDialogShow(mContext)) {
                                    showDialog(item.tradeModel.id);
                                }
                                break;
                        }
                    }
                }
            });
            holder.setOnClickListener(R.id.btn_right, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.tradeModel.status != null) {
                        switch (item.tradeModel.status) {
                            case 1:
                                if (item.tradeModel.merchantId == spUtil.getUserUid()) {
                                    jumpTo(OtcOrderConfirmActivity.createActivity(getContext(), item.tradeModel.id, item.tradeModel.orderNo));
                                } else {
                                    jumpTo(OtcOrderDetailActivity.createActivity(getContext(), item.tradeModel.orderNo));
                                }
                                break;
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                            case 12:
                            case 13:
                                jumpTo(OtcOrderDetailActivity.createActivity(getContext(), item.tradeModel.orderNo));
                                break;
                        }
                    }
                }
            });
        }
    }

    private void showDialog(final String orderId) {
        View view = View.inflate(getContext(), R.layout.view_dialog_trade_pwd, null);
        final QMUIRoundEditText edit_trade_pwd = view.findViewById(R.id.edit_trade_pwd);
        droidDialog = new DroidDialog.Builder(getContext())
                .title(getResources().getString(R.string.trade_password))
                .viewCustomLayout(view)
                .positiveButton(getResources().getString(R.string.confirm_new_pwd), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        String pwd = edit_trade_pwd.getText().toString().trim();
                        if (TextUtils.isEmpty(pwd)) {
                            showToastError(getResources().getString(R.string.please_input_trade_password));
                            return;
                        }
                        showSure(orderId, pwd);
                    }
                })
                .cancelable(true, true)
                .show();
    }

    private void showSure(final String orderId, final String pwdString) {
        sureDialog = new DroidDialog.Builder(getContext())
                .title(getResources().getString(R.string.sure_again))
                .content(getResources().getString(R.string.please_sure_again_coined))
                .contentGravity(Gravity.CENTER)
                .positiveButton(getResources().getString(R.string.sure), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        mPresenter.finishOrder(orderId, spUtil.getPwdVisibility() ? Md5Utils.encryptFdPwd(pwdString, spUtil.getUserUid()).toLowerCase() : null);
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
