package com.android.tacu.module.transaction.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.transaction.contract.TradeRecordManageContract;
import com.android.tacu.module.transaction.model.OrderParam;
import com.android.tacu.module.transaction.model.ShowOrderListModel;
import com.android.tacu.module.transaction.model.ShowTradeListModel;
import com.android.tacu.module.transaction.model.TradeParam;
import com.android.tacu.module.transaction.presenter.TradeRecordManagePresenter;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.StatusBarUtils;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.android.tacu.widget.dialog.DroidDialog;
import com.google.gson.Gson;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;
import com.qmuiteam.qmui.alpha.QMUIAlphaImageButton;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jiazhen on 2018/10/13.
 */
public class TradeRecordManageActivity extends BaseActivity<TradeRecordManagePresenter> implements TradeRecordManageContract.IView {

    @BindView(R.id.drawerLayout_manage)
    DrawerLayout drawerManage;
    @BindView(R.id.refresh_Layout)
    SmartRefreshLayout refreshManage;
    @BindView(R.id.recyclerView_manage)
    RecyclerView ryManage;
    @BindView(R.id.drawer_right)
    View viewDrawer;
    @BindView(R.id.conlayout_bottom)
    ConstraintLayout conlayoutBottom;
    @BindView(R.id.img_select)
    ImageView imgSelect;
    @BindView(R.id.tv_select)
    TextView tvSelect;
    @BindView(R.id.img_page_select)
    ImageView imgPageSelect;
    @BindView(R.id.tv_page_select)
    TextView tvPageSelect;
    @BindView(R.id.btn_revoke)
    QMUIAlphaButton btnRevoke;

    private TextView tvTitle;
    private QMUIAlphaImageButton btnScreen;

    private TradeRecordDrawerLayoutHelper tradeRecordDrawerLayoutHelper;

    private OrderParam orderParam;
    private TradeParam tradeParam;

    private int currencyIdOrgin = -1;
    private int baseCurrencyIdOrgin = -1;
    private String currencyNameEnOrgin;
    private String baseCurrencyNameEnOrgin;
    //当前的类型 1：当前委托 2：历史委托 3：成交记录
    private int orderType = 2;
    //全选的标记 指的是搜出来的所有的 包含分页没加载的
    private boolean isAllSelectFlag = false;
    //本页全选的标记
    private boolean isPageSelectFlag = false;
    //是否处于编辑状态（当前委托才有）
    private boolean isEditFlag = false;

    private CurrentAdapter currentAdapter;
    private HistoryAdapter historyAdapter;
    private DealRecordAdapter dealRecordAdapter;

    private ShowOrderListModel showOrderListModel;
    private ShowTradeListModel showTradeListModel;

    private View emptyView;
    private QMUIBottomSheet mQmuiBottomSheet;

    public static Intent createActivity(Context context, int currencyId, int baseCurrencyId, String currencyNameEn, String baseCurrencyNameEn, int orderType) {
        Intent intent = new Intent(context, TradeRecordManageActivity.class);
        intent.putExtra("currencyId", currencyId);
        intent.putExtra("baseCurrencyId", baseCurrencyId);
        intent.putExtra("currencyNameEn", currencyNameEn);
        intent.putExtra("baseCurrencyNameEn", baseCurrencyNameEn);
        intent.putExtra("orderType", orderType);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_trade_record_manage);
    }

    @Override
    protected void initView() {
        StatusBarUtils.setColorForDrawerLayout(this, drawerManage, ContextCompat.getColor(this, R.color.content_bg_color), 0);

        currencyIdOrgin = getIntent().getIntExtra("currencyId", -1);
        baseCurrencyIdOrgin = getIntent().getIntExtra("baseCurrencyId", -1);
        currencyNameEnOrgin = getIntent().getStringExtra("currencyNameEn");
        baseCurrencyNameEnOrgin = getIntent().getStringExtra("baseCurrencyNameEn");
        orderType = getIntent().getIntExtra("orderType", 2);

        mTopBar.removeAllLeftViews();
        tvTitle = mTopBar.setTitle(getResources().getString(R.string.history_record));
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backEditStatus();
            }
        });
        btnScreen = mTopBar.addRightImageButton(R.mipmap.img_screen, R.id.qmui_topbar_item_right_two, 18, 18);
        btnScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerManage.isDrawerOpen(Gravity.RIGHT)) {
                    drawerManage.openDrawer(Gravity.RIGHT);
                } else {
                    drawerManage.closeDrawer(Gravity.RIGHT);
                }
            }
        });

        CustomTextHeaderView header = new CustomTextHeaderView(this);
        header.setPrimaryColors(ContextCompat.getColor(this, R.color.content_bg_color), ContextCompat.getColor(this, R.color.text_color));
        refreshManage.setRefreshHeader(header);
        refreshManage.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_default)));
        refreshManage.setEnableLoadmore(true);
        refreshManage.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                switchOrderType(orderType, false);
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                switchOrderType(orderType, true);
            }
        });

        ryManage.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void initLazy() {
        super.initLazy();
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                tradeRecordDrawerLayoutHelper = new TradeRecordDrawerLayoutHelper(TradeRecordManageActivity.this, viewDrawer);
                tradeRecordDrawerLayoutHelper.setHomeDrawerMenuView(currencyIdOrgin, baseCurrencyIdOrgin, currencyNameEnOrgin, baseCurrencyNameEnOrgin, new TradeRecordDrawerLayoutHelper.OKClickListener() {
                    @Override
                    public void coinClick(OrderParam orderParam, TradeParam tradeParam) {
                        if (orderType == 3) {
                            TradeRecordManageActivity.this.tradeParam = tradeParam;
                        } else if (orderType == 1 || orderType == 2) {
                            TradeRecordManageActivity.this.orderParam = orderParam;
                        }
                        if (drawerManage.isDrawerOpen(Gravity.RIGHT)) {
                            drawerManage.closeDrawer(Gravity.RIGHT);
                        }
                        switchOrderType(orderType, false);
                    }
                });
                tradeRecordDrawerLayoutHelper.setChildViewVis(orderType);
            }
        });
    }

    @Override
    protected TradeRecordManagePresenter createPresenter(TradeRecordManagePresenter mPresenter) {
        return new TradeRecordManagePresenter();
    }

    @Override
    protected void onPresenterCreated(TradeRecordManagePresenter presenter) {
        super.onPresenterCreated(presenter);
        switchOrderType(orderType, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tradeRecordDrawerLayoutHelper != null) {
            tradeRecordDrawerLayoutHelper.clearActivity();
            tradeRecordDrawerLayoutHelper = null;
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (drawerManage.isDrawerOpen(Gravity.RIGHT)) {
                drawerManage.closeDrawer(Gravity.RIGHT);
                return true;
            }
            backEditStatus();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.img_select)
    void allSelectClick() {
        isAllSelectFlag = !isAllSelectFlag;
        isPageSelectFlag = false;
        imgPageSelect.setImageResource(R.drawable.icon_check_box_outline);
        if (isAllSelectFlag) {
            imgSelect.setImageResource(R.drawable.icon_check_box);
            allSelect();
        } else {
            imgSelect.setImageResource(R.drawable.icon_check_box_outline);
            allUnSelect();
        }
        setSelectNum();
    }

    @OnClick(R.id.img_page_select)
    void pageSelectClick() {
        isPageSelectFlag = !isPageSelectFlag;
        isAllSelectFlag = false;
        imgSelect.setImageResource(R.drawable.icon_check_box_outline);
        if (isPageSelectFlag) {
            imgPageSelect.setImageResource(R.drawable.icon_check_box);
            allSelect();
        } else {
            imgPageSelect.setImageResource(R.drawable.icon_check_box_outline);
            allUnSelect();
        }
        setSelectNum();
    }

    @OnClick(R.id.btn_revoke)
    void revokeList() {
        int num = 0;
        for (int i = 0; i < showOrderListModel.list.size(); i++) {
            if (showOrderListModel.list.get(i).isCheckOrder) {
                num++;
            }
        }
        if (num > 0) {
            cancelOrderListDialog(num);
        }
    }

    @Override
    public void showOrderList(ShowOrderListModel model) {
        if (showOrderListModel != null && showOrderListModel.list != null && showOrderListModel.list.size() > 0) {
            if (model != null && model.list != null && model.list.size() > 0) {
                showOrderListModel.total = model.total;
                if (isAllSelectFlag) {
                    for (int i = 0; i < model.list.size(); i++) {
                        model.list.get(i).isCheckOrder = true;
                    }
                }
                showOrderListModel.list.addAll(model.list);
            }
        } else {
            showOrderListModel = model;
        }

        if (showOrderListModel != null && showOrderListModel.list != null && showOrderListModel.list.size() > 0) {
            if (orderType == 1) {
                currentAdapter.setNewData(showOrderListModel.list);
            } else if (orderType == 2) {
                historyAdapter.setNewData(showOrderListModel.list);
            }
        } else {
            if (orderType == 1) {
                currentAdapter.setNewData(null);
                currentAdapter.setEmptyView(emptyView);
            } else if (orderType == 2) {
                historyAdapter.setNewData(null);
                historyAdapter.setEmptyView(emptyView);
            }
        }

        if (showOrderListModel == null || showOrderListModel.list == null || showOrderListModel.list.size() >= model.total) {
            refreshManage.setEnableLoadmore(false);
        } else {
            refreshManage.setEnableLoadmore(true);
        }
    }

    @Override
    public void showOrderListFail() {
        refreshManage.setEnableLoadmore(true);
        if (orderParam.start > 1) {
            orderParam.start--;
        }
    }

    @Override
    public void showTradeList(ShowTradeListModel model) {
        if (showTradeListModel != null && showTradeListModel.list != null && showTradeListModel.list.size() > 0) {
            showTradeListModel.total = model.total;
            showTradeListModel.list.addAll(model.list);
        } else {
            showTradeListModel = model;
        }

        if (showTradeListModel != null && showTradeListModel.list != null && showTradeListModel.list.size() > 0) {
            dealRecordAdapter.setNewData(showTradeListModel.list);
        } else {
            dealRecordAdapter.setNewData(null);
            dealRecordAdapter.setEmptyView(emptyView);
        }

        if (showTradeListModel == null || showTradeListModel.list == null || showTradeListModel.list.size() >= model.total) {
            refreshManage.setEnableLoadmore(false);
        } else {
            refreshManage.setEnableLoadmore(true);
        }
    }

    @Override
    public void showTradeListFail() {
        refreshManage.setEnableLoadmore(true);
        if (tradeParam.start > 1) {
            tradeParam.start--;
        }
    }

    @Override
    public void cancelOrder() {
        showToastSuccess(getResources().getString(R.string.order_success));
        switchOrderType(orderType, false);
    }

    @Override
    public void cancelOrderList() {
        showToastSuccess(getResources().getString(R.string.order_success));
        switchOrderType(orderType, false);
        imgSelect.setImageResource(R.drawable.icon_check_box_outline);
        imgPageSelect.setImageResource(R.drawable.icon_check_box_outline);
        btnRevoke.setText(getResources().getString(R.string.order_cancel) + "(0)");
    }

    private void initRecordPopUp() {
        if (mQmuiBottomSheet == null) {
            mQmuiBottomSheet = new QMUIBottomSheet.BottomListSheetBuilder(this)
                    .addItem(getResources().getString(R.string.open_orders))
                    .addItem(getResources().getString(R.string.history_record))
                    .addItem(getResources().getString(R.string.history_deal))
                    .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                        @Override
                        public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                            orderType = position + 1;
                            switchOrderType(orderType, false);
                            if (tradeRecordDrawerLayoutHelper != null) {
                                tradeRecordDrawerLayoutHelper.setChildViewVis(orderType);
                            }
                            tvTitle.setText(tag);
                            dialog.dismiss();
                        }
                    })
                    .build();
        }
        mQmuiBottomSheet.show();
    }

    /**
     * 切换当前的
     * 当前的类型 1：当前委托 2：历史委托 3：成交记录
     * isLoad : true=上拉加载 false=不是上拉加载
     */
    private void switchOrderType(int type, boolean isLoad) {
        if (type == 2) {
            tvTitle.setText(getResources().getString(R.string.history_record));
        } else if (type == 3) {
            tvTitle.setText(getResources().getString(R.string.history_deal));
        }
        switch (type) {
            case 1:
                if (currentAdapter == null) {
                    currentAdapter = new CurrentAdapter();
                }
                if (ryManage.getAdapter() != currentAdapter) {
                    ryManage.setAdapter(currentAdapter);
                    emptyView = View.inflate(this, R.layout.view_empty, null);
                    currentAdapter.setNewData(null);
                    currentAdapter.setEmptyView(emptyView);
                    orderParam = new OrderParam(currencyIdOrgin, baseCurrencyIdOrgin);
                }
                orderParam.status = "0,1";
                if (isLoad) {
                    orderParam.start++;
                } else {
                    orderParam.start = 1;
                    if (showOrderListModel != null && showOrderListModel.list != null && showOrderListModel.list.size() > 0) {
                        showOrderListModel.list.clear();
                    }
                }
                getOrderList(orderParam);
                break;
            case 2:
                if (historyAdapter == null) {
                    historyAdapter = new HistoryAdapter();
                }
                if (ryManage.getAdapter() != historyAdapter) {
                    ryManage.setAdapter(historyAdapter);
                    emptyView = View.inflate(this, R.layout.view_empty, null);
                    historyAdapter.setNewData(null);
                    historyAdapter.setEmptyView(emptyView);
                    orderParam = new OrderParam(currencyIdOrgin, baseCurrencyIdOrgin);
                }
                if (TextUtils.isEmpty(orderParam.status)) {
                    orderParam.status = "2,4,5";
                }
                if (isLoad) {
                    orderParam.start++;
                } else {
                    orderParam.start = 1;
                    if (showOrderListModel != null && showOrderListModel.list != null && showOrderListModel.list.size() > 0) {
                        showOrderListModel.list.clear();
                    }
                }
                getOrderList(orderParam);
                break;
            case 3:
                if (dealRecordAdapter == null) {
                    dealRecordAdapter = new DealRecordAdapter();
                }
                if (ryManage.getAdapter() != dealRecordAdapter) {
                    ryManage.setAdapter(dealRecordAdapter);
                    emptyView = View.inflate(this, R.layout.view_empty, null);
                    dealRecordAdapter.setNewData(null);
                    dealRecordAdapter.setEmptyView(emptyView);
                    tradeParam = new TradeParam(currencyIdOrgin, baseCurrencyIdOrgin);
                }
                if (isLoad) {
                    tradeParam.start++;
                } else {
                    tradeParam.start = 1;
                    if (showTradeListModel != null && showTradeListModel.list != null && showTradeListModel.list.size() > 0) {
                        showTradeListModel.list.clear();
                    }
                }
                getTradeList(tradeParam);
                break;
        }
    }

    /**
     * 设置编辑状态
     */
    private void setEditFlag() {
        isEditFlag = !isEditFlag;
        if (isEditFlag) {
            mTopBar.setTitle(getResources().getString(R.string.open_orders), 0);
            tvTitle.setEnabled(false);
            btnScreen.setVisibility(View.GONE);
            conlayoutBottom.setVisibility(View.VISIBLE);
            btnRevoke.setText(getResources().getString(R.string.order_cancel) + "(0)");
        } else {
            mTopBar.setTitle(getResources().getString(R.string.open_orders), R.drawable.icon_pulldown_grey, 18, 18, 3);
            tvTitle.setEnabled(true);
            btnScreen.setVisibility(View.VISIBLE);
            conlayoutBottom.setVisibility(View.GONE);
            isAllSelectFlag = false;
            isPageSelectFlag = false;
        }
        currentAdapter.notifyDataSetChanged();
    }

    /**
     * 处于编辑状态 点击返回 不退出而是取消编辑状态
     */
    private void backEditStatus() {
        if (isEditFlag) {
            setEditFlag();
        } else {
            finish();
        }
    }

    private void setSelectNum() {
        if (showOrderListModel != null && showOrderListModel.list != null) {
            if (isAllSelectFlag) {
                btnRevoke.setText(getResources().getString(R.string.order_cancel) + "(" + showOrderListModel.total + ")");
            } else {
                int num = 0;
                for (int i = 0; i < showOrderListModel.list.size(); i++) {
                    if (showOrderListModel.list.get(i).isCheckOrder) {
                        num++;
                    }
                }
                btnRevoke.setText(getResources().getString(R.string.order_cancel) + "(" + num + ")");
            }
        }

    }

    /**
     * 全选 包含 本页全选和全部全选
     */
    private void allSelect() {
        if (showOrderListModel != null && showOrderListModel.list != null && showOrderListModel.list.size() > 0) {
            for (int i = 0; i < showOrderListModel.list.size(); i++) {
                showOrderListModel.list.get(i).isCheckOrder = true;
            }
            currentAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 取消全选
     */
    private void allUnSelect() {
        if (showOrderListModel != null && showOrderListModel.list != null && showOrderListModel.list.size() > 0) {
            for (int i = 0; i < showOrderListModel.list.size(); i++) {
                showOrderListModel.list.get(i).isCheckOrder = false;
            }
            currentAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 当前委托／历史委托
     *
     * @param param
     */
    private void getOrderList(OrderParam param) {
        mPresenter.showOrderList(param.start, param.size, param.status, param.buyOrSell, param.currencyId, param.baseCurrencyId);
    }

    /**
     * 当前委托／历史委托
     *
     * @param param
     */
    private void getTradeList(TradeParam param) {
        mPresenter.showTradeList(param.start, param.size, param.buyOrSell, param.currencyId, param.baseCurrencyId);
    }

    /**
     * 当前委托
     */
    private class CurrentAdapter extends BaseQuickAdapter<ShowOrderListModel.ListBean, BaseViewHolder> {

        public CurrentAdapter() {
            super(R.layout.item_current_entrusted);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final ShowOrderListModel.ListBean item) {
            helper.setText(R.id.tv_buyorSell, item.getBuyOrSell());
            helper.setTextColor(R.id.tv_buyorSell, ContextCompat.getColor(TradeRecordManageActivity.this, item.getTextColor()));
            helper.setText(R.id.tv_coins_name, item.currencyNameEn + "/" + item.baseCurrencyNameEn);

            helper.setText(R.id.tv_price_title, getResources().getString(R.string.price) + "(" + item.baseCurrencyNameEn + ")");
            helper.setText(R.id.tv_number_title, getResources().getString(R.string.amount) + "(" + item.currencyNameEn + ")");
            helper.setText(R.id.tv_deal_title, getResources().getString(R.string.actual_transaction) + "(" + item.currencyNameEn + ")");

            helper.setText(R.id.tv_price, item.type == 1 ? FormatterUtils.getFormatValue(item.price) : item.price);
            helper.setText(R.id.tv_number, item.type == 1 ? FormatterUtils.getFormatValue(item.num) : item.num);
            if (!TextUtils.isEmpty(item.tradeNum)) {
                helper.setText(R.id.tv_deal, FormatterUtils.getFormatValue(item.tradeNum));
            } else {
                helper.setText(R.id.tv_deal, "0");
            }

            if (isAllSelectFlag || isPageSelectFlag || isEditFlag) {
                helper.setGone(R.id.img_select, true);
                if (item.isCheckOrder) {
                    helper.setImageResource(R.id.img_select, R.drawable.icon_check_box);
                } else {
                    helper.setImageResource(R.id.img_select, R.drawable.icon_check_box_outline);
                }
            } else {
                helper.setGone(R.id.img_select, false);
            }

            helper.setOnClickListener(R.id.img_select, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.isCheckOrder) {
                        showOrderListModel.list.get(helper.getLayoutPosition()).isCheckOrder = false;
                        item.isCheckOrder = false;
                    } else {
                        showOrderListModel.list.get(helper.getLayoutPosition()).isCheckOrder = true;
                        item.isCheckOrder = true;
                    }
                    if (isAllSelectFlag) {
                        isAllSelectFlag = false;
                    }
                    if (isPageSelectFlag) {
                        isPageSelectFlag = false;
                    }
                    notifyItemChanged(helper.getLayoutPosition());
                    setSelectNum();
                }
            });

            helper.setOnClickListener(R.id.btnCancel, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (spUtil.getPwdVisibility()) {
                        cancelOrderDialog(1, item.orderNo);
                    } else {
                        mPresenter.cancel(item.orderNo, "");
                    }
                }
            });
        }
    }

    /**
     * 历史委托
     */
    private class HistoryAdapter extends BaseQuickAdapter<ShowOrderListModel.ListBean, BaseViewHolder> {

        public HistoryAdapter() {
            super(R.layout.item_history_entrusted);
        }

        @Override
        protected void convert(BaseViewHolder helper, final ShowOrderListModel.ListBean item) {
            helper.setText(R.id.tv_buyorSell, item.getBuyOrSell());
            helper.setTextColor(R.id.tv_buyorSell, ContextCompat.getColor(TradeRecordManageActivity.this, item.getTextColor()));
            helper.setText(R.id.tv_coins_name, item.currencyNameEn + "/" + item.baseCurrencyNameEn);

            helper.setText(R.id.btnCancel, item.getStatus());
            if (item.status == 2 || item.status == 5) {
                Drawable drawable = ContextCompat.getDrawable(TradeRecordManageActivity.this, R.drawable.icon_arrow_right_black);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                ((QMUIAlphaButton) helper.getView(R.id.btnCancel)).setCompoundDrawables(null, null, drawable, null);
                helper.getView(R.id.btnCancel).setEnabled(true);
            } else {
                ((QMUIAlphaButton) helper.getView(R.id.btnCancel)).setCompoundDrawables(null, null, null, null);
                helper.getView(R.id.btnCancel).setEnabled(false);
            }

            helper.setText(R.id.tv_entrustment_price_title, getResources().getString(R.string.entrustment_price) + "(" + item.baseCurrencyNameEn + ")");
            helper.setText(R.id.tv_entrustment_number_title, getResources().getString(R.string.entrustment_number) + "(" + item.currencyNameEn + ")");
            helper.setText(R.id.tv_deal_all_title, getResources().getString(R.string.deal_all_amount) + "(" + item.baseCurrencyNameEn + ")");
            helper.setText(R.id.tv_deal_avg_title, getResources().getString(R.string.transaction_amount) + "(" + item.baseCurrencyNameEn + ")");
            helper.setText(R.id.tv_deal_number_title, getResources().getString(R.string.vol) + "(" + item.currencyNameEn + ")");

            helper.setText(R.id.tv_date, DateUtils.getStrToStr(item.orderTime, DateUtils.DEFAULT_PATTERN, DateUtils.FORMAT_DATE_YMDHM));
            helper.setText(R.id.tv_entrustment_price, FormatterUtils.getFormatValue(item.price));
            helper.setText(R.id.tv_entrustment_number, FormatterUtils.getFormatValue(item.num));
            helper.setText(R.id.tv_deal_all, FormatterUtils.getFormatValue(item.dealAmount));
            helper.setText(R.id.tv_deal_avg, FormatterUtils.getFormatValue(item.averagePrice));
            helper.setText(R.id.tv_deal_number, FormatterUtils.getFormatValue(item.tradeNum));

            helper.setOnClickListener(R.id.btnCancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.status == 2 || item.status == 5) {
                        jumpTo(DealDetailsActivity.createActivity(TradeRecordManageActivity.this, item));
                    }
                }
            });
        }
    }

    /**
     * 成交记录
     */
    private class DealRecordAdapter extends BaseQuickAdapter<ShowTradeListModel.ListBean, BaseViewHolder> {

        public DealRecordAdapter() {
            super(R.layout.item_deal_record);
        }

        @Override
        protected void convert(BaseViewHolder helper, ShowTradeListModel.ListBean item) {
            helper.setText(R.id.tv_buyorSell, item.getBuyOrSell());
            helper.setTextColor(R.id.tv_buyorSell, ContextCompat.getColor(TradeRecordManageActivity.this, item.getTextColor()));
            helper.setText(R.id.tv_coins_name, item.currencyNameEn + "/" + item.baseCurrencyNameEn);

            helper.setText(R.id.tv_deal_singleprice_title, getResources().getString(R.string.deal_single_price) + "(" + item.baseCurrencyNameEn + ")");
            helper.setText(R.id.tv_deal_number_title, getResources().getString(R.string.vol) + "(" + item.currencyNameEn + ")");
            helper.setText(R.id.tv_deal_price_title, getResources().getString(R.string.deal_price) + "(" + item.baseCurrencyNameEn + ")");
            if (item.buyOrSell == 1) {//买
                if (!TextUtils.isEmpty(item.buyFeeCurrencyName)) {
                    helper.setText(R.id.tv_fee_title, getResources().getString(R.string.fee) + "(" + item.buyFeeCurrencyName + ")");
                } else {
                    helper.setText(R.id.tv_fee_title, getResources().getString(R.string.fee) + "(" + item.currencyNameEn + ")");
                }
            } else if (item.buyOrSell == 2) {
                if (!TextUtils.isEmpty(item.sellFeeCurrencyName)) {
                    helper.setText(R.id.tv_fee_title, getResources().getString(R.string.fee) + "(" + item.sellFeeCurrencyName + ")");
                } else {
                    helper.setText(R.id.tv_fee_title, getResources().getString(R.string.fee) + "(" + item.baseCurrencyNameEn + ")");
                }
            }

            helper.setText(R.id.tv_date, DateUtils.getStrToStr(item.tradeTime, DateUtils.DEFAULT_PATTERN, DateUtils.FORMAT_DATE_YMDHM));
            helper.setText(R.id.tv_deal_singleprice, FormatterUtils.getFormatValue(item.tradePrice));
            helper.setText(R.id.tv_deal_number, FormatterUtils.getFormatValue(item.tradeNum));
            helper.setText(R.id.tv_deal_price, FormatterUtils.getFormatValue(item.tradeAmount));
            helper.setText(R.id.tv_fee, FormatterUtils.getFormatValue(item.fee));
        }
    }

    /**
     * 显示撤销输入交易密码
     * flag： 1=单个撤销 2=批量撤销
     */
    private void cancelOrderDialog(final int flag, final String orderNo) {
        // 将LoginActivity中的控件显示在对话框中
        final View view = View.inflate(this, R.layout.view_dialog_pwd, null);

        new DroidDialog.Builder(this)
                .title(getResources().getString(R.string.trade_pwd_confirm))
                .viewCustomLayout(view)
                .positiveButton(getResources().getString(R.string.sure), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        final EditText etPassword = view.findViewById(R.id.et_pwd);

                        //将页面输入框中获得的“用户名”，“密码”转为字符串
                        final String pwd = etPassword.getText().toString().trim();
                        if (TextUtils.isEmpty(pwd)) {
                            showToastError(getResources().getString(R.string.trade_password));
                            return;
                        }
                        if (flag == 1) {
                            mPresenter.cancel(orderNo, Md5Utils.encryptFdPwd(pwd, spUtil.getUserUid()).toLowerCase());
                        } else if (flag == 2) {
                            cancelList(pwd);
                        }
                    }
                })
                .negativeButton(getResources().getString(R.string.cancel), null)
                .cancelable(false, false)
                .show();
    }

    /**
     * 批量撤销提醒
     */
    private void cancelOrderListDialog(int num) {
        new DroidDialog.Builder(this)
                .title(getResources().getString(R.string.trade_pwd_confirm))
                .content(String.format(getResources().getString(R.string.confirm_orderlist_delete), String.valueOf(num)))
                .positiveButton(getResources().getString(R.string.sure), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        if (spUtil.getPwdVisibility()) {
                            cancelOrderDialog(2, null);
                        } else {
                            cancelList("");
                        }
                    }
                })
                .negativeButton(getResources().getString(R.string.cancel), null)
                .cancelable(false, false)
                .show();
    }

    private void cancelList(String pwd) {
        String deleteListSelectedParams = null;
        String orderListString = "";
        if (isAllSelectFlag) {
            deleteListSelectedParams = new Gson().toJson(orderParam);
        } else {
            if (showOrderListModel != null && showOrderListModel.list != null && showOrderListModel.list.size() > 0) {
                for (int i = 0; i < showOrderListModel.list.size(); i++) {
                    if (showOrderListModel.list.get(i).isCheckOrder) {
                        if (i == 0) {
                            orderListString += showOrderListModel.list.get(i).orderNo;
                        } else {
                            orderListString += "," + showOrderListModel.list.get(i).orderNo;
                        }
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(pwd)) {
            mPresenter.cancelList(orderListString, Md5Utils.encryptFdPwd(pwd, spUtil.getUserUid()).toLowerCase(), deleteListSelectedParams);
        } else {
            mPresenter.cancelList(orderListString, "", deleteListSelectedParams);
        }
    }
}
