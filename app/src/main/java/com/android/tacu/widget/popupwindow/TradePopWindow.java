package com.android.tacu.widget.popupwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.module.transaction.view.TradeFragment;
import com.android.tacu.utils.SPUtils;

public class TradePopWindow extends PopupWindow implements View.OnClickListener {

    private Context mContext;

    private LinearLayout lin_currencyid_get;
    private LinearLayout lin_currencyid_take;
    private LinearLayout lin_basecurrencyid_get;
    private LinearLayout lin_basecurrencyid_take;
    private TextView tv_currencyid_get;
    private TextView tv_currencyid_take;
    private TextView tv_basecurrencyid_get;
    private TextView tv_basecurrencyid_take;

    private LinearLayout lin_hand;
    private LinearLayout lin_pwd;
    private LinearLayout lin_history_deal;
    private TextView tv_hand;

    private TabItemSelect tabItemSelect;

    public TradePopWindow(Context context) {
        super(context);
        this.mContext = context;
    }

    public void create(int width, final TabItemSelect tabItemSelect) {
        this.tabItemSelect = tabItemSelect;

        setWidth(width);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(mContext, R.color.content_bg_color)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(10F);
        }

        View view = View.inflate(mContext, R.layout.pop_trade, null);
        lin_currencyid_get = view.findViewById(R.id.lin_currencyid_get);
        lin_currencyid_take = view.findViewById(R.id.lin_currencyid_take);
        lin_basecurrencyid_get = view.findViewById(R.id.lin_basecurrencyid_get);
        lin_basecurrencyid_take = view.findViewById(R.id.lin_basecurrencyid_take);

        tv_currencyid_get = view.findViewById(R.id.tv_currencyid_get);
        tv_currencyid_take = view.findViewById(R.id.tv_currencyid_take);
        tv_basecurrencyid_get = view.findViewById(R.id.tv_basecurrencyid_get);
        tv_basecurrencyid_take = view.findViewById(R.id.tv_basecurrencyid_take);

        lin_hand = view.findViewById(R.id.lin_hand);
        lin_pwd = view.findViewById(R.id.lin_pwd);
        lin_history_deal = view.findViewById(R.id.lin_history_deal);
        tv_hand = view.findViewById(R.id.tv_hand);

        lin_currencyid_get.setOnClickListener(this);
        lin_currencyid_take.setOnClickListener(this);
        lin_basecurrencyid_get.setOnClickListener(this);
        lin_basecurrencyid_take.setOnClickListener(this);
        lin_hand.setOnClickListener(this);
        lin_pwd.setOnClickListener(this);
        lin_history_deal.setOnClickListener(this);

        setContentView(view);
    }

    public void notifyInfo(String currencyNameEn, String baseCurrencyNameEn) {
        tv_currencyid_get.setText(currencyNameEn + mContext.getResources().getString(R.string.recharge2));
        tv_currencyid_take.setText(currencyNameEn + mContext.getResources().getString(R.string.withdrawals));
        tv_basecurrencyid_get.setText(baseCurrencyNameEn + mContext.getResources().getString(R.string.recharge2));
        tv_basecurrencyid_take.setText(baseCurrencyNameEn + mContext.getResources().getString(R.string.withdrawals));

        String string = SPUtils.getInstance().getString(TradeFragment.VIEW_POSITION);
        if (TextUtils.equals(string,TradeFragment.POSITION_LEFT)){
            tv_hand.setText(mContext.getResources().getText(R.string.change_right_hand));
        }else{
            tv_hand.setText(mContext.getResources().getText(R.string.change_left_hand));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_currencyid_get:
                if (tabItemSelect != null) {
                    tabItemSelect.coinClick(0);
                }
                break;
            case R.id.lin_currencyid_take:
                if (tabItemSelect != null) {
                    tabItemSelect.coinClick(1);
                }
                break;
            case R.id.lin_basecurrencyid_get:
                if (tabItemSelect != null) {
                    tabItemSelect.coinClick(2);
                }
                break;
            case R.id.lin_basecurrencyid_take:
                if (tabItemSelect != null) {
                    tabItemSelect.coinClick(3);
                }
                break;
            case R.id.lin_hand:
                if (tabItemSelect != null) {
                    tabItemSelect.coinClick(4);
                }
                break;
            case R.id.lin_pwd:
                if (tabItemSelect != null) {
                    tabItemSelect.coinClick(5);
                }
                break;
            case R.id.lin_history_deal:
                if (tabItemSelect != null) {
                    tabItemSelect.coinClick(6);
                }
                break;
        }
    }

    public interface TabItemSelect {
        void coinClick(int position);
    }
}
