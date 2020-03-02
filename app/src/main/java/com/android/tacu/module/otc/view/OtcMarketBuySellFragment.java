package com.android.tacu.module.otc.view;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.OTCListVisibleHintEvent;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.otc.contract.OtcMarketBuySellContract;
import com.android.tacu.module.otc.dialog.OtcDialogUtils;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcMarketOrderAllModel;
import com.android.tacu.module.otc.model.OtcMarketOrderModel;
import com.android.tacu.module.otc.model.OtcMarketOrderListModel;
import com.android.tacu.module.otc.presenter.OtcMarketBuySellPresenter;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.widget.popupwindow.ListPopWindow;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class OtcMarketBuySellFragment extends BaseFragment<OtcMarketBuySellPresenter> implements OtcMarketBuySellContract.IView, View.OnClickListener {

    @BindView(R.id.refreshlayout_home)
    SmartRefreshLayout refreshlayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private TextView tv_price_sort;
    private TextView tv_surplus_amount_sort;
    private TextView tv_quota_sort;
    private TextView tv_all_manner_sort;
    private View view_flag;

    //0=代表向下，由高到低 1=代表向上，由低到高
    private int sort_price = 0;
    private int sort_surplus_amount = 0;
    private int sort_quota = 0;
    //1价格升序 2价格降序 3 剩余量升序 4 剩余量降序 5最高限价降序 6最低限价升序
    private Integer type = -1;
    //支付方式 0 全部 1 银行卡 2 微信 3 支付宝
    private Integer payType = 0;

    private int currencyId;
    private String currencyNameEn;
    private boolean isBuy = true; //默认true=买
    private int start = 1;

    private ListPopWindow listPopup;
    private OtcMarketBuySellAdapter mAdapter;
    private List<OtcMarketOrderAllModel> allList = new ArrayList<>();
    private boolean isVisibleToUserParent = false;

    public static OtcMarketBuySellFragment newInstance(int currencyId, String currencyNameEn, boolean isBuy) {
        Bundle bundle = new Bundle();
        bundle.putInt("currencyId", currencyId);
        bundle.putString("currencyNameEn", currencyNameEn);
        bundle.putBoolean("isBuy", isBuy);
        OtcMarketBuySellFragment fragment = new OtcMarketBuySellFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initLazy() {
        super.initLazy();
        upload(true, true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            currencyId = bundle.getInt("currencyId");
            currencyNameEn = bundle.getString("currencyNameEn");
            isBuy = bundle.getBoolean("isBuy", true);
            if (isBuy) {
                type = 1;
            } else {
                type = 2;
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_otc_market_buy_sell;
    }

    @Override
    protected void initData(View view) {
        MaterialHeader header = new MaterialHeader(getContext());
        header.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.color_default));
        refreshlayout.setRefreshHeader(header);
        refreshlayout.setRefreshFooter(new BallPulseFooter(getContext()).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(getContext(), R.color.color_default)));
        refreshlayout.setEnableLoadmore(false);
        refreshlayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                upload(false, true);
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                upload(false, false);
            }
        });

        mAdapter = new OtcMarketBuySellAdapter();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.item_recyclerview_divider));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(mAdapter);

        initHeader(view);
    }

    @Override
    protected OtcMarketBuySellPresenter createPresenter(OtcMarketBuySellPresenter mPresenter) {
        return new OtcMarketBuySellPresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
        upload(false, true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listPopup != null) {
            listPopup.dismiss();
        }
    }

    @Override
    protected void receiveEvent(BaseEvent event) {
        super.receiveEvent(event);
        if (event != null) {
            switch (event.getCode()) {
                case EventConstant.OTCListVisibleCode:
                    OTCListVisibleHintEvent otcListVisibleHintEvent = (OTCListVisibleHintEvent) event.getData();
                    isVisibleToUserParent = otcListVisibleHintEvent.isVisibleToUser();
                    upload(true, true);
                    break;
            }
        }
    }

    @Override
    public void hideRefreshView() {
        super.hideRefreshView();
        if (refreshlayout != null && (refreshlayout.isRefreshing() || refreshlayout.isLoading())) {
            refreshlayout.finishRefresh();
            refreshlayout.finishLoadmore();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_price_sort:
                sort_price = (sort_price + 1) > 1 ? 0 : sort_price + 1;
                sort_surplus_amount = 0;
                sort_quota = 0;
                setSortView(tv_price_sort, sort_price);
                if (sort_price == 0) {
                    type = 2;
                } else if (sort_price == 1) {
                    type = 1;
                }
                upload(true, true);
                break;
            case R.id.tv_surplus_amount_sort:
                sort_surplus_amount = (sort_surplus_amount + 1) > 1 ? 0 : sort_surplus_amount + 1;
                sort_price = 0;
                sort_quota = 0;
                setSortView(tv_surplus_amount_sort, sort_surplus_amount);
                if (sort_surplus_amount == 0) {
                    type = 4;
                } else if (sort_surplus_amount == 1) {
                    type = 3;
                }
                upload(true, true);
                break;
            case R.id.tv_quota_sort:
                sort_quota = (sort_quota + 1) > 1 ? 0 : sort_quota + 1;
                sort_price = 0;
                sort_surplus_amount = 0;
                setSortView(tv_quota_sort, sort_quota);
                if (sort_quota == 0) {
                    type = 5;
                } else if (sort_quota == 1) {
                    type = 6;
                }
                upload(true, true);
                break;
            case R.id.tv_all_manner_sort:
                showAllMannerType(tv_all_manner_sort);
                break;
        }
    }

    @Override
    public void orderList(OtcMarketOrderListModel model) {
        if (model != null) {
            if (model.list != null && model.list.size() > 0) {
                OtcMarketOrderAllModel allModel = null;
                OtcMarketInfoModel infoListModel = null;
                OtcMarketOrderModel orderListModel = null;
                List<OtcMarketOrderAllModel> list = new ArrayList<>();

                for (int i = 0; i < model.list.size(); i++) {
                    allModel = new OtcMarketOrderAllModel();
                    orderListModel = model.list.get(i);
                    allModel.orderModel = orderListModel;
                    if (model.infoList != null && model.infoList.size() > 0) {
                        for (int j = 0; j < model.infoList.size(); j++) {
                            infoListModel = model.infoList.get(j);
                            if (infoListModel.uid != null && orderListModel.uid != null && infoListModel.uid.equals(orderListModel.uid)) {
                                allModel.infoModel = infoListModel;
                            }
                        }
                    }
                    list.add(allModel);
                }

                if (allList != null && list.size() > 0) {
                    allList.addAll(list);
                } else {
                    allList = list;
                }
                if (allList != null && allList.size() > 0) {
                    mAdapter.setNewData(allList);
                    if (allList.size() >= model.total) {
                        refreshlayout.setEnableLoadmore(false);
                    } else {
                        start++;
                        refreshlayout.setEnableLoadmore(true);
                    }
                }
            } else if (start == 1) {
                mAdapter.setNewData(null);
                refreshlayout.setEnableLoadmore(false);
            }
        } else if (start == 1) {
            mAdapter.setNewData(null);
            refreshlayout.setEnableLoadmore(false);
        }
    }

    private void upload(boolean isShowViewing, boolean isTop) {
        if (!isVisibleToUser || !isVisibleToUserParent) {
            return;
        }
        if (isTop) {
            start = 1;
        }
        if (start == 1 && allList != null && allList.size() > 0) {
            allList.clear();
        }
        mPresenter.orderList(isShowViewing, type, currencyId, start, 10, payType, isBuy ? 2 : 1);
    }

    private void initHeader(View view) {
        tv_price_sort = view.findViewById(R.id.tv_price_sort);
        tv_surplus_amount_sort = view.findViewById(R.id.tv_surplus_amount_sort);
        tv_quota_sort = view.findViewById(R.id.tv_quota_sort);
        tv_all_manner_sort = view.findViewById(R.id.tv_all_manner_sort);
        view_flag = view.findViewById(R.id.view_flag);

        tv_price_sort.setOnClickListener(this);
        tv_surplus_amount_sort.setOnClickListener(this);
        tv_quota_sort.setOnClickListener(this);
        tv_all_manner_sort.setOnClickListener(this);
    }

    private void setSortView(TextView tv, int status) {
        clearSortView();
        switch (status) {
            case 0:
                setDrawableR(tv, R.drawable.icon_sort_down);
                break;
            case 1:
                setDrawableR(tv, R.drawable.icon_sort_up);
                break;
        }
    }

    private void clearSortView() {
        setDrawableR(tv_price_sort, R.drawable.icon_sort_default);
        setDrawableR(tv_surplus_amount_sort, R.drawable.icon_sort_default);
        setDrawableR(tv_quota_sort, R.drawable.icon_sort_default);
    }

    private void setDrawableR(TextView textDrawable, int drawable) {
        Drawable drawableRight = getResources().getDrawable(drawable);
        textDrawable.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);
    }

    private void showAllMannerType(final TextView tv) {
        if (listPopup != null && listPopup.isShowing()) {
            listPopup.dismiss();
            return;
        }
        if (listPopup == null) {
            final List<String> data = new ArrayList<>();
            data.add(getResources().getString(R.string.all));
            data.add(getResources().getString(R.string.yinhanngka));
            data.add(getResources().getString(R.string.zhifubao));
            data.add(getResources().getString(R.string.weixin));
            ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.simple_list_item, data);
            listPopup = new ListPopWindow(getContext(), adapter);
            listPopup.create(UIUtils.dp2px(120), UIUtils.dp2px(163), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tv.setText(data.get(position));
                    //支付方式 0 全部 1 银行卡 2 微信 3 支付宝
                    switch (position) {
                        case 0:
                            payType = 0;
                            break;
                        case 1:
                            payType = 1;
                            break;
                        case 2:
                            payType = 3;
                            break;
                        case 3:
                            payType = 2;
                            break;
                    }
                    upload(true, true);
                    listPopup.dismiss();
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            listPopup.setDropDownGravity(Gravity.END);
        }
        listPopup.setAnchorView(view_flag);
        listPopup.show();
    }

    public class OtcMarketBuySellAdapter extends BaseQuickAdapter<OtcMarketOrderAllModel, BaseViewHolder> {

        public OtcMarketBuySellAdapter() {
            super(R.layout.item_otc_market_buy_sell);
        }

        @Override
        protected void convert(BaseViewHolder holder, final OtcMarketOrderAllModel item) {
            if (item.infoModel != null) {
                GlideUtils.disPlay(getContext(), CommonUtils.getHead(item.infoModel.headImg), (ImageView) holder.getView(R.id.img_user));
                holder.setText(R.id.tv_nickname, item.infoModel.nickname + "(" + CommonUtils.nameXing(item.infoModel.secondName) + ")");
                if (item.infoModel.vip != null && item.infoModel.vip != 0) {
                    holder.setImageResource(R.id.img_vip, R.mipmap.img_vip_green);
                } else if (item.infoModel.applyMerchantStatus != null && item.infoModel.applyMerchantStatus == 2) {
                    holder.setImageResource(R.id.img_vip, R.mipmap.img_vip_grey);
                } else if (item.infoModel.applyAuthMerchantStatus != null && item.infoModel.applyAuthMerchantStatus == 2) {
                    holder.setImageResource(R.id.img_vip, R.drawable.icon_vip);
                }
                holder.setText(R.id.tv_history_deal, getResources().getString(R.string.history_deal) + "：" + item.infoModel.total + getResources().getString(R.string.dan));
            }
            if (item.orderModel != null) {
                holder.setText(R.id.tv_surplus, item.orderModel.remainAmount + "/" + item.orderModel.amount + " " + currencyNameEn);

                String moneyWei = " " + Constant.CNY;
                if (item.orderModel.money != null && item.orderModel.money == 1) {
                    moneyWei = " " + Constant.CNY;
                }
                holder.setText(R.id.tv_single_quota, item.orderModel.lowLimit + "~" + item.orderModel.highLimit + " " + moneyWei);
                holder.setText(R.id.tv_single_price, item.orderModel.price + " " + moneyWei);

                if (item.orderModel.payByCard != null && item.orderModel.payByCard == 1) {
                    holder.setGone(R.id.img_yhk, true);
                } else {
                    holder.setGone(R.id.img_yhk, false);
                }
                if (item.orderModel.payWechat != null && item.orderModel.payWechat == 1) {
                    holder.setGone(R.id.img_wx, true);
                } else {
                    holder.setGone(R.id.img_wx, false);
                }
                if (item.orderModel.payAlipay != null && item.orderModel.payAlipay == 1) {
                    holder.setGone(R.id.img_zfb, true);
                } else {
                    holder.setGone(R.id.img_zfb, false);
                }
            }

            if (isBuy) {
                holder.setTextColor(R.id.tv_single_price, ContextCompat.getColor(getContext(), R.color.color_riseup));
                ((QMUIRoundButtonDrawable) holder.getView(R.id.btn).getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_riseup));
            } else {
                holder.setTextColor(R.id.tv_single_price, ContextCompat.getColor(getContext(), R.color.color_risedown));
                ((QMUIRoundButtonDrawable) holder.getView(R.id.btn).getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_risedown));
            }

            holder.setOnClickListener(R.id.btn, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!OtcDialogUtils.isDialogShow(getContext())) {
                        if (item.infoModel.uid != null && spUtil.getUserUid() == item.infoModel.uid) {
                            jumpTo(OtcManageBuySellDetailActivity.createActivity(getContext(), item.orderModel.id));
                        } else {
                            jumpTo(OtcBuyOrSellActivity.createActivity(getContext(), isBuy, item.orderModel.id));
                        }
                    }
                }
            });
        }
    }
}
