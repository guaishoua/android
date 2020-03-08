package com.android.tacu.module.vip.view;

import android.app.Dialog;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.common.TabIndicatorAdapter;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.otc.dialog.OtcTradeDialogUtils;
import com.android.tacu.module.vip.contract.RechargeDepositContract;
import com.android.tacu.module.vip.model.BondRecordModel;
import com.android.tacu.module.vip.presenter.RechargeDepositPresenter;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.MathHelper;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.android.tacu.widget.dialog.DroidDialog;
import com.android.tacu.widget.popupwindow.ListPopWindow;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.TextWidthColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class RechargeDepositActivity extends BaseActivity<RechargeDepositPresenter> implements RechargeDepositContract.IView, View.OnClickListener {

    @BindView(R.id.refreshlayout_home)
    SmartRefreshLayout refreshlayout;
    @BindView(R.id.rv)
    RecyclerView recyclerView;

    private TextView tv_account_balance;
    private TextView tv_accont_coin;
    private TextView tv_margin_balance;
    private TextView tv_available_balance;
    private TextView tv_frozen_balance;
    private ScrollIndicatorView indicatorView;
    private TextView tv_coin;
    private EditText edit_amount;
    private QMUIRoundButton btn_ok;

    private ListPopWindow listPopup;
    private DroidDialog droidDialog;
    private OrderAdapter orderAdapter;
    private List<String> tabTitle = new ArrayList<>();

    //0=币币账户划转到保证金账户（充值） 1=保证金账户划转到币币账户（提取）
    private int type = 0;
    //上面的币币账户币种ID
    private int CC_CoinId = Constant.ACU_CURRENCY_ID;
    //上面的保证金账户币种ID
    private int Bond_CoinId = Constant.ACU_CURRENCY_ID;

    private View emptyView;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_recharge_deposit);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.recharge_deposit));

        CustomTextHeaderView header = new CustomTextHeaderView(this);
        header.setPrimaryColors(ContextCompat.getColor(this, R.color.content_bg_color), ContextCompat.getColor(this, R.color.text_color));
        refreshlayout.setRefreshHeader(header);
        refreshlayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale).setAnimatingColor(ContextCompat.getColor(this, R.color.color_default)));
        refreshlayout.setEnableLoadmore(false);
        refreshlayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                upload();
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
            }
        });

        orderAdapter = new OrderAdapter();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_recyclerview_divider));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(orderAdapter);

        emptyView = View.inflate(this, R.layout.view_empty, null);
        orderAdapter.setEmptyView(emptyView);

        initHeader();
    }

    @Override
    protected RechargeDepositPresenter createPresenter(RechargeDepositPresenter mPresenter) {
        return new RechargeDepositPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        upload();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listPopup != null) {
            listPopup.dismiss();
        }
        if (droidDialog != null) {
            droidDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_accont_coin:
                showCoinType(tv_accont_coin);
                break;
            case R.id.tv_coin:
                showCoinType(tv_coin);
                break;
            case R.id.btn_ok:
                if (!OtcTradeDialogUtils.isDialogShow(this)) {
                    String inputString = edit_amount.getText().toString().trim();
                    if (TextUtils.isEmpty(inputString)) {
                        showToastError(getResources().getString(R.string.please_input_recharge_amount));
                        return;
                    }
                    if (spUtil.getPwdVisibility()) {
                        showDialog();
                    } else {
                        bondOrCC(null);
                    }
                }
                break;
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
    public void customerCoinByOneCoin(Double value) {
        if (value != null) {
            tv_account_balance.setText(FormatterUtils.getFormatValue(value) + Constant.ACU_CURRENCY_NAME);
        }
    }

    @Override
    public void BondAccount(OtcAmountModel model) {
        if (model != null) {
            if (!TextUtils.isEmpty(model.amount)) {
                tv_margin_balance.setText(FormatterUtils.getFormatValue(model.amount) + Constant.ACU_CURRENCY_NAME);
            } else {
                tv_margin_balance.setText("0" + Constant.ACU_CURRENCY_NAME);
            }
            if (!TextUtils.isEmpty(model.cashAmount)) {
                tv_available_balance.setText(FormatterUtils.getFormatValue(model.cashAmount) + Constant.ACU_CURRENCY_NAME);
            } else {
                tv_available_balance.setText("0" + Constant.ACU_CURRENCY_NAME);
            }

            Double value1 = (!TextUtils.isEmpty(model.freezeAmount)) ? Double.valueOf(model.freezeAmount) : 0;
            Double value2 = (!TextUtils.isEmpty(model.bondFreezeAmount)) ? Double.valueOf(model.bondFreezeAmount) : 0;
            tv_frozen_balance.setText(FormatterUtils.getFormatValue(MathHelper.add(value1, value2)) + Constant.ACU_CURRENCY_NAME);
        } else {
            tv_margin_balance.setText("0" + Constant.ACU_CURRENCY_NAME);
            tv_available_balance.setText("0" + Constant.ACU_CURRENCY_NAME);
            tv_frozen_balance.setText("0" + Constant.ACU_CURRENCY_NAME);
        }
    }

    @Override
    public void CcToBondSuccess() {
        edit_amount.setText("");
        showToastSuccess(getResources().getString(R.string.success));
        upload();
    }

    @Override
    public void BondToCcSuccess() {
        edit_amount.setText("");
        showToastSuccess(getResources().getString(R.string.success));
        upload();
    }

    @Override
    public void selectBondRecord(List<BondRecordModel> list) {
        if (list != null && list.size() > 0) {
            orderAdapter.setNewData(list);
        } else {
            orderAdapter.setNewData(null);
            orderAdapter.setEmptyView(emptyView);
        }
    }

    private void initHeader() {
        View headerView = View.inflate(this, R.layout.header_recharge_deposit, null);
        tv_account_balance = headerView.findViewById(R.id.tv_account_balance);
        tv_accont_coin = headerView.findViewById(R.id.tv_accont_coin);
        tv_margin_balance = headerView.findViewById(R.id.tv_margin_balance);
        tv_available_balance = headerView.findViewById(R.id.tv_available_balance);
        tv_frozen_balance = headerView.findViewById(R.id.tv_frozen_balance);
        indicatorView = headerView.findViewById(R.id.base_scrollIndicatorView);
        tv_coin = headerView.findViewById(R.id.tv_coin);
        edit_amount = headerView.findViewById(R.id.edit_amount);
        btn_ok = headerView.findViewById(R.id.btn_ok);

        tv_accont_coin.setOnClickListener(this);
        tv_coin.setOnClickListener(this);
        btn_ok.setOnClickListener(this);

        tabTitle.add(getResources().getString(R.string.home_recharge));
        tabTitle.add(getResources().getString(R.string.extract));
        indicatorView.setBackgroundColor(ContextCompat.getColor(this, R.color.tab_bg_color));
        indicatorView.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(this, R.color.text_default), ContextCompat.getColor(this, R.color.tab_text_color)).setSize(14, 14));
        indicatorView.setScrollBar(new TextWidthColorBar(this, indicatorView, ContextCompat.getColor(this, R.color.text_default), 4));
        indicatorView.setSplitAuto(true);
        indicatorView.setAdapter(new TabIndicatorAdapter(this, tabTitle));
        indicatorView.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
                type = select;
            }
        });

        orderAdapter.setHeaderFooterEmpty(true, false);
        orderAdapter.addHeaderView(headerView);
    }

    private void upload() {
        mPresenter.customerCoinByOneCoin(CC_CoinId);
        mPresenter.BondAccount(CC_CoinId);
        mPresenter.selectBondRecord();
    }

    public void bondOrCC(String pwd) {
        if (type == 0) {
            mPresenter.CcToBond(edit_amount.getText().toString(), Bond_CoinId, pwd);
        } else if (type == 1) {
            mPresenter.BondToCc(edit_amount.getText().toString(), Bond_CoinId, pwd);
        }
    }

    public class OrderAdapter extends BaseQuickAdapter<BondRecordModel, BaseViewHolder> {

        public OrderAdapter() {
            super(R.layout.item_recharge_deposit);
        }

        @Override
        protected void convert(BaseViewHolder helper, BondRecordModel item) {
            helper.setText(R.id.tv_coin_name, item.currencyName);
            helper.setText(R.id.tv_type, item.getType());
            helper.setTextColor(R.id.tv_type, item.getTypeColor());
            helper.setText(R.id.tv_status, item.getStatus());
            helper.setTextColor(R.id.tv_status, item.getStatusColor());
            helper.setText(R.id.tv_time, item.createTime);
            helper.setText(R.id.tv_amount_title, getResources().getString(R.string.amount) + "(" + item.currencyName + ")");
            helper.setText(R.id.tv_amount, item.amount);
        }
    }

    private void showCoinType(final TextView tv) {
        if (listPopup != null && listPopup.isShowing()) {
            listPopup.dismiss();
            return;
        }
        final List<String> data = new ArrayList<>();
        final List<Integer> dataId = new ArrayList<>();
        data.add(Constant.ACU_CURRENCY_NAME);
        dataId.add(Constant.ACU_CURRENCY_ID);
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, data);
        listPopup = new ListPopWindow(this, adapter);
        listPopup.create(UIUtils.dp2px(80), UIUtils.dp2px(40), new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv.setText(data.get(position));
                if (tv == tv_accont_coin) {
                    CC_CoinId = dataId.get(position);
                } else if (tv == tv_coin) {
                    Bond_CoinId = dataId.get(position);
                }
                listPopup.dismiss();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            listPopup.setDropDownGravity(Gravity.END);
        }
        listPopup.setAnchorView(tv);
        listPopup.show();
    }

    private void showDialog() {
        View view = View.inflate(this, R.layout.view_dialog_trade_pwd, null);
        final QMUIRoundEditText edit_trade_pwd = view.findViewById(R.id.edit_trade_pwd);
        droidDialog = new DroidDialog.Builder(this)
                .title(getResources().getString(R.string.trade_password))
                .viewCustomLayout(view)
                .positiveButton(getResources().getString(R.string.confirm_new_pwd), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        String pwd = edit_trade_pwd.getText().toString().trim();
                        if (!TextUtils.isEmpty(pwd)) {
                            showToastError(getResources().getString(R.string.please_input_trade_password));
                            return;
                        }
                        bondOrCC(Md5Utils.encryptFdPwd(pwd, spUtil.getUserUid()).toLowerCase());
                    }
                })
                .cancelable(false, false)
                .show();
    }
}
