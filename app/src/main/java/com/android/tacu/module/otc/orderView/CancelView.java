package com.android.tacu.module.otc.orderView;

import android.view.View;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.view.OtcOrderDetailActivity;
import com.android.tacu.utils.user.UserInfoUtils;

public class CancelView extends BaseOtcView {

    private OtcOrderDetailActivity activity;
    private UserInfoUtils spUtil;

    private TextView tv_status;

    private OtcTradeModel tradeModel;

    public View create(OtcOrderDetailActivity activity) {
        this.activity = activity;
        View statusView = View.inflate(activity, R.layout.view_otc_order_cancel, null);
        initCancelView(statusView);
        return statusView;
    }

    private void initCancelView(View view) {
        setBaseView(view, activity);

        tv_status = view.findViewById(R.id.tv_status);

        spUtil = UserInfoUtils.getInstance();
    }

    public void selectTradeOne(OtcTradeModel model) {
        this.tradeModel = model;
        dealCancel();
    }

    private void dealCancel() {
        if (tradeModel != null) {
            setBaseValue(activity, tradeModel, spUtil);
            if (tradeModel.status != null) {
                // 1待确认 2 已确认待付款 3已付款待放币 4 仲裁 5 未确认超时取消 6 拒绝订单 7 付款超时取消 8放弃支付 9 放币超时  10放币完成
                // 12 买家成功 13 卖家成功
                switch (tradeModel.status) {
                    case 5:
                        tv_status.setText(activity.getResources().getString(R.string.shop_noconfirm_timeout));
                        break;
                    case 6:
                        tv_status.setText(activity.getResources().getString(R.string.shop_refuse_order));
                        break;
                    case 7:
                        tv_status.setText(activity.getResources().getString(R.string.pay_timeout_cancel));
                        break;
                    case 8:
                        tv_status.setText(activity.getResources().getString(R.string.buy_giveup_pay));
                        break;
                }
            }
        }
    }

    public void destory() {
        activity = null;
    }
}
