package com.android.tacu.module.vip.view;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.common.TabIndicatorAdapter;
import com.android.tacu.module.vip.contract.RechargeDepositContract;
import com.android.tacu.module.vip.presenter.RechargeDepositPresenter;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.view.smartrefreshlayout.CustomTextHeaderView;
import com.android.tacu.widget.dialog.DroidDialog;
import com.android.tacu.widget.popupwindow.ListPopWindow;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class RechargeDepositActivity extends BaseActivity<RechargeDepositPresenter> implements RechargeDepositContract.IView, View.OnClickListener {

    @BindView(R.id.refreshlayout_home)
    SmartRefreshLayout refreshlayout;
    @BindView(R.id.rv)
    RecyclerView recyclerView;

    private TextView tv_accont_coin;
    private TextView tv_coin;
    private ScrollIndicatorView indicatorView;

    private ListPopWindow listPopup;
    private DroidDialog droidDialog;
    private OrderAdapter orderAdapter;
    private List<String> tabTitle = new ArrayList<>();

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
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
            }
        });

        orderAdapter = new OrderAdapter();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.content_bg_color_grey)));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(orderAdapter);

        initHeader();
    }

    @Override
    protected RechargeDepositPresenter createPresenter(RechargeDepositPresenter mPresenter) {
        return new RechargeDepositPresenter();
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
        }
    }

    private void initHeader() {
        View headerView = View.inflate(this, R.layout.header_recharge_deposit, null);
        tv_accont_coin = headerView.findViewById(R.id.tv_accont_coin);
        tv_coin = headerView.findViewById(R.id.tv_coin);
        indicatorView = headerView.findViewById(R.id.base_scrollIndicatorView);

        tabTitle.add(getResources().getString(R.string.home_recharge));
        tabTitle.add(getResources().getString(R.string.extract));
        indicatorView.setBackgroundColor(ContextCompat.getColor(this, R.color.tab_bg_color));
        indicatorView.setOnTransitionListener(new OnTransitionTextListener().setColor(ContextCompat.getColor(this, R.color.text_default), ContextCompat.getColor(this, R.color.tab_text_color)).setSize(14, 14));
        indicatorView.setScrollBar(new ColorBar(this, ContextCompat.getColor(this, R.color.text_default), 4));
        indicatorView.setSplitAuto(true);
        indicatorView.setAdapter(new TabIndicatorAdapter(this, tabTitle));
        indicatorView.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
            }
        });

        orderAdapter.addHeaderView(headerView);
    }

    public class OrderAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public OrderAdapter() {
            super(R.layout.item_recharge_deposit);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {

        }
    }

    private void showCoinType(final TextView tv) {
        if (listPopup != null && listPopup.isShowing()) {
            listPopup.dismiss();
            return;
        }
        if (listPopup == null) {
            final List<String> data = new ArrayList<>();
            data.add("ACU");
            ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, data);
            listPopup = new ListPopWindow(this, adapter);
            listPopup.create(UIUtils.dp2px(80), UIUtils.dp2px(40), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tv.setText(data.get(position));
                    listPopup.dismiss();
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            listPopup.setDropDownGravity(Gravity.END);
        }
        listPopup.setAnchorView(tv);
        listPopup.show();
    }

    private void showDialog() {
        View view = View.inflate(this, R.layout.view_dialog_trade_pwd, null);
        droidDialog = new DroidDialog.Builder(this)
                .title(getResources().getString(R.string.trade_password))
                .viewCustomLayout(view)
                .positiveButton(getResources().getString(R.string.confirm_new_pwd), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {

                    }
                })
                .cancelable(false, false)
                .show();
    }
}
