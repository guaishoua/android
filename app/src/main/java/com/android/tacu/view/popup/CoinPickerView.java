package com.android.tacu.view.popup;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.module.market.model.MarketNewModel;
import com.zyyoona7.wheel.WheelView;
import com.zyyoona7.wheel.WheelView.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class CoinPickerView extends PopupBaseView {

    WheelView wheelView;
    TextView tvTarget;
    Button confirm, cancel;
    List<MarketNewModel.TradeCoinsBean> list;
    String target;
    MarketNewModel.TradeCoinsBean selected = null;
    Listener listener;

    public interface Listener {
        void onItemSelected(MarketNewModel.TradeCoinsBean target);
        void onDismiss();
    }

    public CoinPickerView(Context context, String target, List<MarketNewModel.TradeCoinsBean> list, Listener l) {
        super(context);

        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.target = target;
        this.list = list;
        this.listener = l;
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.view_coin_picker;
    }

    @Override
    protected void initView(View root) {
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                listener.onDismiss();
            }
        });
        wheelView = root.findViewById(R.id.wheel_view_target);
        tvTarget = root.findViewById(R.id.tv_wheel_base_title);
        confirm = root.findViewById(R.id.btn_wheel_confirm);
        cancel = root.findViewById(R.id.btn_wheel_cancel);
        confirm.setOnClickListener(onClickListener);
        cancel.setOnClickListener(onClickListener);
        selected = list.get(0);
        tvTarget.setText(target);
        final List<String> options = new ArrayList<>();
        for (MarketNewModel.TradeCoinsBean bean : list) {
            options.add(!TextUtils.equals(
                    bean.baseCurrencyNameEn.toLowerCase(),
                    target.toLowerCase()) ?
                    bean.baseCurrencyNameEn : bean.currencyNameEn);
        }

        wheelView.setData(options);
        wheelView.setOnItemSelectedListener(new OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(com.zyyoona7.wheel.WheelView<String> wheelView, String data, int position) {
                selected = list.get(position);
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();

            switch (v.getId()) {
                case R.id.btn_wheel_confirm:
                    listener.onItemSelected(selected);
                    break;
                case R.id.btn_wheel_cancel:
                    break;
            }
        }
    };
}
