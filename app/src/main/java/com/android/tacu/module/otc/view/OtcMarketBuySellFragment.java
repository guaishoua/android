package com.android.tacu.module.otc.view;

import android.graphics.drawable.ColorDrawable;
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
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.module.otc.contract.OtcMarketBuySellContract;
import com.android.tacu.module.otc.presenter.OtcMarketBuySellPresenter;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.widget.popupwindow.ListPopWindow;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class OtcMarketBuySellFragment extends BaseFragment<OtcMarketBuySellPresenter> implements OtcMarketBuySellContract.IView, View.OnClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private TextView tv_price_sort;
    private TextView tv_surplus_amount_sort;
    private TextView tv_quota_sort;
    private TextView tv_all_manner_sort;
    private View view_flag;

    // 0 =默认不排序 1=代表向下，由高到低 2=代表向上，由低到高
    private int sort_price = 0;
    private int sort_surplus_amount = 0;
    private int sort_quota = 0;

    private int currencyId;
    private String currencyNameEn;
    private boolean isBuy = true; //默认true=买

    private ListPopWindow listPopup;
    private OtcMarketBuySellAdapter mAdapter;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            currencyId = bundle.getInt("currencyId");
            currencyNameEn = bundle.getString("currencyNameEn");
            isBuy = bundle.getBoolean("isBuy", true);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_otc_market_buy_sell;
    }

    @Override
    protected void initData(View view) {
        mAdapter = new OtcMarketBuySellAdapter();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.content_bg_color_grey)));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(mAdapter);

        initHeader();

        List<String> list = new ArrayList<>();
        list.add("1");
        mAdapter.setNewData(list);
    }

    @Override
    protected OtcMarketBuySellPresenter createPresenter(OtcMarketBuySellPresenter mPresenter) {
        return new OtcMarketBuySellPresenter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listPopup != null) {
            listPopup.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_price_sort:
                sort_price = (sort_price + 1) > 2 ? 0 : sort_price + 1;
                sort_surplus_amount = 0;
                sort_quota = 0;
                setSortView(tv_price_sort, sort_price);
                break;
            case R.id.tv_surplus_amount_sort:
                sort_surplus_amount = (sort_surplus_amount + 1) > 2 ? 0 : sort_surplus_amount + 1;
                sort_price = 0;
                sort_quota = 0;
                setSortView(tv_surplus_amount_sort, sort_surplus_amount);
                break;
            case R.id.tv_quota_sort:
                sort_quota = (sort_quota + 1) > 2 ? 0 : sort_quota + 1;
                sort_price = 0;
                sort_surplus_amount = 0;
                setSortView(tv_quota_sort, sort_quota);
                break;
            case R.id.tv_all_manner_sort:
                showAllMannerType(tv_all_manner_sort);
                break;
        }
    }

    private void initHeader() {
        View headerView = View.inflate(getContext(), R.layout.header_otc_market_buy_sell, null);
        tv_price_sort = headerView.findViewById(R.id.tv_price_sort);
        tv_surplus_amount_sort = headerView.findViewById(R.id.tv_surplus_amount_sort);
        tv_quota_sort = headerView.findViewById(R.id.tv_quota_sort);
        tv_all_manner_sort = headerView.findViewById(R.id.tv_all_manner_sort);
        view_flag = headerView.findViewById(R.id.view_flag);

        tv_price_sort.setOnClickListener(this);
        tv_surplus_amount_sort.setOnClickListener(this);
        tv_quota_sort.setOnClickListener(this);
        tv_all_manner_sort.setOnClickListener(this);

        mAdapter.setHeaderView(headerView);
    }

    public void setRefreshFragment(){

    }

    private void setSortView(TextView tv, int status) {
        clearSortView(tv);
        switch (status) {
            case 0:
                setDrawableR(tv, R.drawable.icon_sort_default);
                break;
            case 1:
                setDrawableR(tv, R.drawable.icon_sort_down);
                break;
            case 2:
                setDrawableR(tv, R.drawable.icon_sort_up);
                break;
        }
    }

    private void clearSortView(TextView tv) {
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
            data.add(getResources().getString(R.string.yinhanngka));
            data.add(getResources().getString(R.string.zhifubao));
            data.add(getResources().getString(R.string.weixin));
            ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.simple_list_item, data);
            listPopup = new ListPopWindow(getContext(), adapter);
            listPopup.create(UIUtils.dp2px(120), UIUtils.dp2px(122), new AdapterView.OnItemClickListener() {
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
        listPopup.setAnchorView(view_flag);
        listPopup.show();
    }

    public class OtcMarketBuySellAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public OtcMarketBuySellAdapter() {
            super(R.layout.item_otc_market_buy_sell);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            if (isBuy){
                helper.setTextColor(R.id.tv_single_price,ContextCompat.getColor(getContext(),R.color.color_riseup));
                ((QMUIRoundButtonDrawable) helper.getView(R.id.btn).getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_riseup));
            }else{
                helper.setTextColor(R.id.tv_single_price,ContextCompat.getColor(getContext(),R.color.color_risedown));
                ((QMUIRoundButtonDrawable) helper.getView(R.id.btn).getBackground()).setBgData(ContextCompat.getColorStateList(getContext(), R.color.color_risedown));
            }
        }
    }
}
