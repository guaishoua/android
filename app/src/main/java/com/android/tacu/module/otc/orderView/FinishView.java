package com.android.tacu.module.otc.orderView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.view.OtcOrderDetailActivity;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.user.UserInfoUtils;

//已完成
public class FinishView extends BaseOtcView {

    private OtcOrderDetailActivity activity;
    private UserInfoUtils spUtil;

    private ImageView img_pay;
    private TextView tv_pay;
    private TextView tv_pay_account;

    private OtcTradeModel tradeModel;
    private String imageUrl;

    //5 未确认超时取消 6 拒绝订单 7 付款超时取消 8放弃支付   10放币完成
    public View create(OtcOrderDetailActivity activity) {
        this.activity = activity;
        View statusView = View.inflate(activity, R.layout.view_otc_order_finished, null);
        initFinishedView(statusView);
        return statusView;
    }

    private void initFinishedView(View view) {
        setBaseView(view, activity);

        img_pay = view.findViewById(R.id.img_pay);
        tv_pay = view.findViewById(R.id.tv_pay);
        tv_pay_account = view.findViewById(R.id.tv_pay_account);

        spUtil = UserInfoUtils.getInstance();
    }

    public void selectTradeOne(OtcTradeModel model) {
        this.tradeModel = model;
        dealFinish();
    }

    public void selectPayInfoById(PayInfoModel model) {
        if (model != null && model.type != null) {
            switch (model.type) {
                case 1:
                    img_pay.setImageResource(R.mipmap.img_yhk);
                    tv_pay.setText(activity.getResources().getString(R.string.yinhanngka));
                    tv_pay_account.setText(CommonUtils.hideCardNo(model.bankCard));
                    break;
                case 2:
                    img_pay.setImageResource(R.mipmap.img_wx);
                    tv_pay.setText(activity.getResources().getString(R.string.weixin));
                    tv_pay_account.setText(CommonUtils.hidePhoneNo(model.weChatNo));
                    break;
                case 3:
                    img_pay.setImageResource(R.mipmap.img_zfb);
                    tv_pay.setText(activity.getResources().getString(R.string.zhifubao));
                    tv_pay_account.setText(CommonUtils.hidePhoneNo(model.aliPayNo));
                    break;
            }
        }
    }

    private void dealFinish() {
        if (tradeModel != null) {
            setBaseValue1(activity, tradeModel, spUtil);
        }
    }

    public void destory() {
        activity = null;
    }
}
