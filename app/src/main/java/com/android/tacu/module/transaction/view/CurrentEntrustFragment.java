package com.android.tacu.module.transaction.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.module.transaction.contract.CurrentEntrustConstract;
import com.android.tacu.module.transaction.model.OrderParam;
import com.android.tacu.module.transaction.model.ShowOrderListModel;
import com.android.tacu.module.transaction.presenter.CurrentEntrustPresenter;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.widget.dialog.DroidDialog;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;

import butterknife.BindView;

public class CurrentEntrustFragment extends BaseFragment<CurrentEntrustPresenter> implements View.OnClickListener, CurrentEntrustConstract.IView {

    @BindView(R.id.refreshlayout_trade)
    SmartRefreshLayout refreshTrade;
    @BindView(R.id.ry)
    RecyclerView ryEntrust;
    @BindView(R.id.cbox)
    CheckBox cbox;
    @BindView(R.id.tv_history_entrust)
    TextView tv_history_entrust;

    private TextView tv_revoke_all;

    private int currencyId;
    private int baseCurrencyId;
    private String currencyNameEn;
    private String baseCurrencyNameEn;

    private Integer currencyIdValue;
    private Integer baseCurrencyIdValue;

    // true=仅仅显示当前市场
    private boolean isCheckbox = false;

    private CurrentAdapter currentAdapter;

    private View emptyView;
    private View footerView;

    private int start = 1;
    private int size = 20;

    private ShowOrderListModel showOrderListModel;

    public static CurrentEntrustFragment newInstance(int currencyId, int baseCurrencyId, String currencyNameEn, String baseCurrencyNameEn) {
        Bundle bundle = new Bundle();
        bundle.putInt("currencyId", currencyId);
        bundle.putInt("baseCurrencyId", baseCurrencyId);
        bundle.putString("currencyNameEn", currencyNameEn);
        bundle.putString("baseCurrencyNameEn", baseCurrencyNameEn);
        CurrentEntrustFragment fragment = new CurrentEntrustFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initLazy() {
        super.initLazy();

        getEntrustList();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            currencyId = bundle.getInt("currencyId");
            baseCurrencyId = bundle.getInt("baseCurrencyId");
            currencyNameEn = bundle.getString("currencyNameEn");
            baseCurrencyNameEn = bundle.getString("baseCurrencyNameEn");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_current_entrust;
    }

    @Override
    protected void initData() {
        refreshTrade.setEnableRefresh(false);
        refreshTrade.setEnableLoadmore(false);
        refreshTrade.setEnableOverScrollBounce(false);
        refreshTrade.setEnableOverScrollDrag(false);
        refreshTrade.setRefreshFooter(new BallPulseFooter(getContext()).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(getContext(), R.color.color_default)));
        refreshTrade.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                start++;
                getEntrustList();
            }
        });

        emptyView = View.inflate(getContext(), R.layout.view_trade_empty, null);
        footerView = View.inflate(getContext(), R.layout.view_current_entrust_footer, null);

        tv_revoke_all = footerView.findViewById(R.id.tv_revoke_all);

        cbox.setOnClickListener(this);
        tv_history_entrust.setOnClickListener(this);
        tv_revoke_all.setOnClickListener(this);

        currentAdapter = new CurrentAdapter();
        currentAdapter.setHeaderFooterEmpty(true, false);
        ryEntrust.setLayoutManager(new LinearLayoutManager(getActivity()));
        ryEntrust.setAdapter(currentAdapter);

        currentAdapter.setEmptyView(emptyView);
    }

    @Override
    protected CurrentEntrustPresenter createPresenter(CurrentEntrustPresenter mPresenter) {
        return new CurrentEntrustPresenter();
    }

    @Override
    public void onResume() {
        super.onResume();

        getEntrustList();
        if (!spUtil.getLogin()) {
            currentAdapter.setNewData(null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cbox:
                isCheckbox = !isCheckbox;
                cbox.setChecked(isCheckbox);
                getEntrustList();
                break;
            case R.id.tv_history_entrust:
                if (spUtil.getLogin()) {
                    jumpTo(TradeRecordManageActivity.createActivity(getContext(), currencyId, baseCurrencyId, currencyNameEn, baseCurrencyNameEn, 2));
                } else {
                    jumpTo(LoginActivity.class);
                }
                break;
            case R.id.tv_revoke_all:
                if (spUtil.getPwdVisibility()) {
                    cancelOrderDialog(2, null);
                } else {
                    cancelList("");
                }
                break;
        }
    }

    @Override
    public void showOrderList(ShowOrderListModel model) {
        if (showOrderListModel != null && showOrderListModel.list != null && showOrderListModel.list.size() > 0) {
            if (model == null || model.list == null || model.list.size() <= 0) {
                showOrderListModel.total = model.total;
                showOrderListModel.list.addAll(model.list);
            }
        } else {
            showOrderListModel = model;
        }

        if (showOrderListModel != null && showOrderListModel.list != null && showOrderListModel.list.size() > 0) {
            currentAdapter.setNewData(model.list);
            currentAdapter.setFooterView(footerView);
        } else {
            currentAdapter.setNewData(null);
            currentAdapter.setEmptyView(emptyView);
            currentAdapter.removeAllFooterView();
        }

        if (showOrderListModel == null || showOrderListModel.list == null || showOrderListModel.list.size() >= model.total) {
            refreshTrade.setEnableLoadmore(false);
        } else {
            refreshTrade.setEnableLoadmore(true);
        }
    }

    @Override
    public void showOrderListFail() {
        start--;
        if (start <= 1) {
            start = 1;
        }
        refreshTrade.setEnableLoadmore(true);
    }

    @Override
    public void cancelOrder() {
        showToastSuccess(getResources().getString(R.string.order_success));
        getEntrustList();
    }

    @Override
    public void cancelOrderList() {
        showToastSuccess(getResources().getString(R.string.order_success));
        getEntrustList();
    }

    public void setCoinInfo(int currencyId, int baseCurrencyId, String currencyNameEn, String baseCurrencyNameEn) {
        this.currencyId = currencyId;
        this.baseCurrencyId = baseCurrencyId;
        this.currencyNameEn = currencyNameEn;
        this.baseCurrencyNameEn = baseCurrencyNameEn;

        isCheckbox = false;
        cbox.setChecked(false);
        getEntrustList();
    }

    public void notiy() {
        getEntrustList();
    }

    private void getEntrustList() {
        if (spUtil != null && spUtil.getLogin()) {
            if (isCheckbox) {
                currencyIdValue = currencyId;
                baseCurrencyIdValue = baseCurrencyId;
            } else {
                currencyIdValue = null;
                baseCurrencyIdValue = null;
            }
            mPresenter.showOrderList(start, size, currencyIdValue, baseCurrencyIdValue);
        }
    }

    /**
     * 显示撤销输入交易密码
     * flag： 1=单个撤销 2=批量撤销
     */
    private void cancelOrderDialog(final int flag, final String orderNo) {
        // 将LoginActivity中的控件显示在对话框中
        final View view = View.inflate(getContext(), R.layout.view_dialog_pwd, null);

        new DroidDialog.Builder(getContext())
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

    private void cancelList(String pwd) {
        String deleteListSelectedParams = new Gson().toJson(new OrderParam(start, size, "0,1", 0, currencyIdValue, baseCurrencyIdValue));
        if (!TextUtils.isEmpty(pwd)) {
            mPresenter.cancelList("", Md5Utils.encryptFdPwd(pwd, spUtil.getUserUid()).toLowerCase(), deleteListSelectedParams);
        } else {
            mPresenter.cancelList("", "", deleteListSelectedParams);
        }
    }

    private class CurrentAdapter extends BaseQuickAdapter<ShowOrderListModel.ListBean, BaseViewHolder> {

        public CurrentAdapter() {
            super(R.layout.item_current_entrusted);
        }

        @Override
        protected void convert(BaseViewHolder helper, final ShowOrderListModel.ListBean item) {
            helper.setText(R.id.tv_buyorSell, item.getBuyOrSell());
            helper.setText(R.id.tv_coins_name, item.currencyNameEn + "/" + item.baseCurrencyNameEn);
            helper.setText(R.id.tv_time, item.orderTime);

            helper.setText(R.id.tv_number_title, getResources().getString(R.string.lodge_amount) + "(" + item.currencyNameEn + ")");
            helper.setText(R.id.tv_deal_title, getResources().getString(R.string.volume_amount) + "(" + item.currencyNameEn + ")");

            helper.setText(R.id.tv_price, item.type == 1 ? FormatterUtils.getFormatValue(item.price) : item.price);
            helper.setText(R.id.tv_price_rmb, item.type == 1 ? "≈" + getMcM(baseCurrencyId, Double.parseDouble(item.price)) : "");

            helper.setText(R.id.tv_number, item.type == 1 ? FormatterUtils.getFormatValue(item.num) : item.num);
            if (!TextUtils.isEmpty(item.tradeNum)) {
                helper.setText(R.id.tv_deal, FormatterUtils.getFormatValue(item.tradeNum));
            } else {
                helper.setText(R.id.tv_deal, "0");
            }

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
}
