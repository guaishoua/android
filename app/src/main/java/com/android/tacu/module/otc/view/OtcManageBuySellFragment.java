package com.android.tacu.module.otc.view;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.widget.popupwindow.ListPopWindow;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class OtcManageBuySellFragment extends BaseFragment {

    @BindView(R.id.tv_trade_type)
    TextView tv_trade_type;
    @BindView(R.id.tv_trade_money)
    TextView tv_trade_money;
    @BindView(R.id.tv_order_operation_time_limit)
    TextView tv_order_operation_time_limit;

    private ListPopWindow coinPopup;
    private ListPopWindow moneyPopup;
    private ListPopWindow timeLimitPopup;

    private boolean isBuy = true; //默认true=买

    public static OtcManageFragment newInstance(boolean isBuy) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isBuy", isBuy);
        OtcManageFragment fragment = new OtcManageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            isBuy = bundle.getBoolean("isBuy", true);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_otc_manage_buysell;
    }

    @Override
    protected void initData(View view) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (coinPopup != null && coinPopup.isShowing()) {
            coinPopup.dismiss();
            coinPopup = null;
        }
        if (moneyPopup != null && moneyPopup.isShowing()) {
            moneyPopup.dismiss();
            moneyPopup = null;
        }
        if (timeLimitPopup != null && timeLimitPopup.isShowing()) {
            timeLimitPopup.dismiss();
            timeLimitPopup = null;
        }
    }

    @OnClick(R.id.tv_trade_type)
    void tradeTypeClick() {
        showCoinType();
    }

    @OnClick(R.id.tv_trade_money)
    void tradeMoneyClick() {
        showMoneyType();
    }

    @OnClick(R.id.tv_order_operation_time_limit)
    void timeLimitClick() {
        showTimeType();
    }

    private void showCoinType() {
        if (coinPopup != null && coinPopup.isShowing()) {
            coinPopup.dismiss();
            return;
        }
        if (coinPopup == null) {
            final List<String> data = new ArrayList<>();
            data.add("ACU");
            data.add("USDT");
            data.add("BTC");
            ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.simple_list_item, data);
            coinPopup = new ListPopWindow(getContext(), adapter);
            coinPopup.create(UIUtils.dp2px(100), UIUtils.dp2px(122), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tv_trade_type.setText(data.get(position));
                    coinPopup.dismiss();
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            coinPopup.setDropDownGravity(Gravity.END);
        }
        coinPopup.setAnchorView(tv_trade_type);
        coinPopup.show();
    }

    private void showMoneyType() {
        if (moneyPopup != null && moneyPopup.isShowing()) {
            moneyPopup.dismiss();
            return;
        }
        if (moneyPopup == null) {
            final List<String> data = new ArrayList<>();
            data.add(getResources().getString(R.string.renminbi));
            ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.simple_list_item, data);
            moneyPopup = new ListPopWindow(getContext(), adapter);
            moneyPopup.create(UIUtils.dp2px(120), UIUtils.dp2px(40), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tv_trade_money.setText(data.get(position));
                    moneyPopup.dismiss();
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            moneyPopup.setDropDownGravity(Gravity.START);
        }
        moneyPopup.setAnchorView(tv_trade_money);
        moneyPopup.show();
    }

    private void showTimeType() {
        if (timeLimitPopup != null && timeLimitPopup.isShowing()) {
            timeLimitPopup.dismiss();
            return;
        }
        if (timeLimitPopup == null) {
            final List<String> data = new ArrayList<>();
            data.add(getResources().getString(R.string.min_15));
            data.add(getResources().getString(R.string.min_30));
            data.add(getResources().getString(R.string.hour_1));
            data.add(getResources().getString(R.string.hour_1_5));
            data.add(getResources().getString(R.string.hour_2));
            data.add(getResources().getString(R.string.hour_4));
            data.add(getResources().getString(R.string.hour_6));
            data.add(getResources().getString(R.string.hour_8));
            data.add(getResources().getString(R.string.hour_12));
            data.add(getResources().getString(R.string.hour_24));
            ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.simple_list_item, data);
            timeLimitPopup = new ListPopWindow(getContext(), adapter);
            timeLimitPopup.create(UIUtils.dp2px(120), UIUtils.dp2px(409), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tv_order_operation_time_limit.setText(data.get(position));
                    timeLimitPopup.dismiss();
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            timeLimitPopup.setDropDownGravity(Gravity.START);
        }
        timeLimitPopup.setAnchorView(tv_order_operation_time_limit);
        timeLimitPopup.show();
    }
}
