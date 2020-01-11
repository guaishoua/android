package com.android.tacu.module.auctionplus.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.module.assets.model.AmountModel;
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

    public static DroidDialog dialogShow(Context mContext, AuctionPlusPayInfoModel model, String paymentOverdueTime, String balance, View.OnClickListener payClick,  View.OnClickListener rechargeClick) {
        View view = View.inflate(mContext, R.layout.view_dialog_auctionplus_pay, null);
        TextView tv_get = view.findViewById(R.id.tv_get);
        TextView tv_dealprice = view.findViewById(R.id.tv_dealprice);
        TextView tv_balance = view.findViewById(R.id.tv_balance);
        QMUIRoundButton btn_pay = view.findViewById(R.id.btn_pay);
        QMUIAlphaButton btn_recharge = view.findViewById(R.id.btn_recharge);
        TextView tv_tip = view.findViewById(R.id.tv_tip);

        tv_get.setText(FormatterUtils.getFormatValue(model.gain) + model.currency);
        tv_dealprice.setText(FormatterUtils.getFormatValue(model.pay));
        tv_balance.setText(FormatterUtils.getFormatRoundDown(2, balance));
        if (!TextUtils.isEmpty(paymentOverdueTime)) {
            tv_tip.setText(String.format(mContext.getResources().getString(R.string.successful_tip), paymentOverdueTime));
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

    public static DroidDialog dialogWinPlusShow(Context mContext, AmountModel model, AuctionPlusWinLisyModel.Bean plusBean, View.OnClickListener payClick, View.OnClickListener rechargeClick) {
        View view = View.inflate(mContext, R.layout.view_dialog_auctionplus_pay, null);
        TextView tv_get = view.findViewById(R.id.tv_get);
        TextView tv_dealprice = view.findViewById(R.id.tv_dealprice);
        TextView tv_balance = view.findViewById(R.id.tv_balance);
        QMUIRoundButton btn_pay = view.findViewById(R.id.btn_pay);
        QMUIAlphaButton btn_recharge = view.findViewById(R.id.btn_recharge);
        TextView tv_tip = view.findViewById(R.id.tv_tip);

        tv_get.setText(FormatterUtils.getFormatValue(plusBean.gain) + plusBean.gainName);
        tv_dealprice.setText(FormatterUtils.getFormatValue(plusBean.tradePrice));
        tv_balance.setText(FormatterUtils.getFormatRoundDown(2, model.attachment));
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
