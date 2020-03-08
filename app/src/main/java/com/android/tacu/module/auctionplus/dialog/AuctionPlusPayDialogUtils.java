package com.android.tacu.module.auctionplus.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.module.auctionplus.modal.AuctionPlusModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusPayInfoModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusWinLisyModel;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.widget.dialog.DroidDialog;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

/**
 * Created by jiazhen on 2019/6/5.
 */
public class AuctionPlusPayDialogUtils {

    public static DroidDialog dialogShow(Context mContext, AuctionPlusPayInfoModel model, AuctionPlusModel auctionPlusModel, String balance, View.OnClickListener payClick, View.OnClickListener rechargeClick) {
        View view = View.inflate(mContext, R.layout.view_dialog_auctionplus_pay, null);
        TextView tv_get = view.findViewById(R.id.tv_get);
        TextView tv_dealprice_title = view.findViewById(R.id.tv_dealprice_title);
        TextView tv_dealprice = view.findViewById(R.id.tv_dealprice);
        TextView tv_balance_title = view.findViewById(R.id.tv_balance_title);
        TextView tv_balance = view.findViewById(R.id.tv_balance);
        QMUIRoundButton btn_pay = view.findViewById(R.id.btn_pay);
        QMUIAlphaButton btn_recharge = view.findViewById(R.id.btn_recharge);
        TextView tv_tip = view.findViewById(R.id.tv_tip);

        if (auctionPlusModel != null) {
            tv_dealprice_title.setText(mContext.getResources().getString(R.string.auctionplus_dealprice_all) + "(" + auctionPlusModel.payCurrencyName + ")");
            tv_balance_title.setText(mContext.getResources().getString(R.string.auction_balance) + "(" + auctionPlusModel.payCurrencyName + ")");
        } else {
            tv_dealprice_title.setText(mContext.getResources().getString(R.string.auctionplus_dealprice_all));
            tv_balance_title.setText(mContext.getResources().getString(R.string.auction_balance));
        }

        tv_get.setText(FormatterUtils.getFormatValue(model.gain) + model.currency);
        tv_dealprice.setText(FormatterUtils.getFormatValue(model.pay));
        tv_balance.setText(FormatterUtils.getFormatRoundDown(2, balance));

        if (auctionPlusModel != null && !TextUtils.isEmpty(auctionPlusModel.paymentOverdueTime)) {
            tv_tip.setText(String.format(mContext.getResources().getString(R.string.successful_tip), auctionPlusModel.paymentOverdueTime));
        } else {
            tv_tip.setText("");
        }

        btn_pay.setOnClickListener(payClick);
        btn_recharge.setOnClickListener(rechargeClick);

        return new DroidDialog.Builder(mContext)
                .title(mContext.getResources().getString(R.string.auction_pay_title))
                .viewCustomLayout(view)
                .cancelable(false, false)
                .show();
    }

    public static DroidDialog dialogWinPlusShow(Context mContext, Double value, AuctionPlusWinLisyModel.Bean plusBean, View.OnClickListener payClick, View.OnClickListener rechargeClick) {
        View view = View.inflate(mContext, R.layout.view_dialog_auctionplus_pay, null);
        TextView tv_get = view.findViewById(R.id.tv_get);
        TextView tv_dealprice_title = view.findViewById(R.id.tv_dealprice_title);
        TextView tv_dealprice = view.findViewById(R.id.tv_dealprice);
        TextView tv_balance_title = view.findViewById(R.id.tv_balance_title);
        TextView tv_balance = view.findViewById(R.id.tv_balance);
        QMUIRoundButton btn_pay = view.findViewById(R.id.btn_pay);
        QMUIAlphaButton btn_recharge = view.findViewById(R.id.btn_recharge);
        TextView tv_tip = view.findViewById(R.id.tv_tip);

        tv_dealprice_title.setText(mContext.getResources().getString(R.string.auctionplus_dealprice_all) + "(" + plusBean.payName + ")");
        tv_balance_title.setText(mContext.getResources().getString(R.string.auction_balance) + "(" + plusBean.payName + ")");

        tv_get.setText(FormatterUtils.getFormatValue(plusBean.gain) + plusBean.gainName);
        tv_dealprice.setText(FormatterUtils.getFormatValue(plusBean.tradePrice));
        if (value != null) {
            tv_balance.setText(FormatterUtils.getFormatRoundDown(2, value));
        }
        if (!TextUtils.isEmpty(plusBean.paymentOverdueTime)) {
            tv_tip.setText(String.format(mContext.getResources().getString(R.string.successful_tip), plusBean.paymentOverdueTime));
        } else {
            tv_tip.setText("");
        }

        btn_pay.setOnClickListener(payClick);
        btn_recharge.setOnClickListener(rechargeClick);

        return new DroidDialog.Builder(mContext)
                .title(mContext.getResources().getString(R.string.auction_pay_title))
                .viewCustomLayout(view)
                .cancelable(false, false)
                .show();
    }
}
