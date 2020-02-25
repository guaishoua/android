package com.android.tacu.module.otc.view;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.module.otc.contract.OtcOrderCreateContract;
import com.android.tacu.module.otc.model.OtcMarketOrderAllModel;
import com.android.tacu.module.otc.model.OtcMarketOrderModel;
import com.android.tacu.module.otc.presenter.OtcOrderCreatePresenter;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.widget.popupwindow.ListPopWindow;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 订单生成页
 */
public class OtcOrderCreateActivity extends BaseOtcOrderActvity<OtcOrderCreatePresenter> implements OtcOrderCreateContract.IView {

    @BindView(R.id.tv_pay_method)
    TextView tv_pay_method;
    @BindView(R.id.tv_xieyi)
    TextView tv_xieyi;
    @BindView(R.id.cb_xieyi)
    CheckBox cb_xieyi;

    private ListPopWindow listPopup;

    private boolean isBuy = true;
    private String fdPassword;
    private String num;
    private String amount;
    private OtcMarketOrderAllModel allModel;
    //支付类型 1银行卡 2微信 3支付宝
    private Integer payType;

    public static Intent createActivity(Context context, boolean isBuy, String fdPassword, String num, String amount, OtcMarketOrderAllModel allModel) {
        Intent intent = new Intent(context, OtcOrderCreateActivity.class);
        intent.putExtra("isBuy", isBuy);
        intent.putExtra("fdPassword", fdPassword);
        intent.putExtra("num", num);
        intent.putExtra("amount", amount);
        Bundle bundle = new Bundle();
        bundle.putSerializable("allModel", allModel);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_order_create);
    }

    @Override
    protected void initView() {
        isBuy = getIntent().getBooleanExtra("isBuy", true);
        fdPassword = getIntent().getStringExtra("fdPassword");
        num = getIntent().getStringExtra("num");
        amount = getIntent().getStringExtra("amount");
        allModel = (OtcMarketOrderAllModel) getIntent().getSerializableExtra("allModel");

        mTopBar.setTitle(getResources().getString(R.string.otc_order_create));

        tv_xieyi.setText(Html.fromHtml(getResources().getString(R.string.otc_xieyi) + "<font color=" + ContextCompat.getColor(this, R.color.color_otc_unhappy) + ">" + getResources().getString(R.string.otc_xieyi_name) + "</font>"));

        setValue(isBuy, allModel, num, amount);
    }

    @Override
    protected OtcOrderCreatePresenter createPresenter(OtcOrderCreatePresenter mPresenter) {
        return new OtcOrderCreatePresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listPopup != null) {
            listPopup.dismiss();
        }
    }

    @OnClick(R.id.tv_pay_method)
    void payMethodClick() {
        showAllMannerType();
    }

    @OnClick(R.id.btn_confirm)
    void confirmClick() {
        if (payType == null || payType == 0) {
            showToastError(getResources().getString(R.string.please_choose_get_pay));
            return;
        }
        if (!cb_xieyi.isChecked()) {
            showToastError(getResources().getString(R.string.please_check_xieyi));
            return;
        }
        mPresenter.otcTrade(allModel.orderModel.id, fdPassword, payType, num, amount);
    }

    @OnClick(R.id.btn_cancel)
    void cancelClick() {
        finish();
    }

    @Override
    public void otcTradeSuccess() {
        finish();
        activityManage.finishActivity(OtcBuyOrSellActivity.class);
        jumpTo(OtcOrderListActivity.class);
    }

    private void showAllMannerType() {
        if (listPopup != null && listPopup.isShowing()) {
            listPopup.dismiss();
            return;
        }
        final List<String> data = new ArrayList<>();
        if (allModel != null) {
            OtcMarketOrderModel orderModel = allModel.orderModel;
            if (orderModel != null) {
                if (orderModel.payByCard == 1) {
                    data.add(getResources().getString(R.string.yinhanngka));
                }
                if (orderModel.payWechat == 1) {
                    data.add(getResources().getString(R.string.weixin));
                }
                if (orderModel.payAlipay == 1) {
                    data.add(getResources().getString(R.string.zhifubao));
                }
            }
        }

        if (data != null && data.size() > 0) {
            ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, data);
            listPopup = new ListPopWindow(this, adapter);

            int height = 0;
            int itemHeight = UIUtils.dp2px(40);
            if (data.size() == 1) {
                height = itemHeight;
            } else {
                for (int i = 0; i < data.size(); i++) {
                    if (i == 0) {
                        height += itemHeight;
                    } else {
                        height += itemHeight + 1;
                    }
                }
            }

            listPopup.create(UIUtils.dp2px(120), height, new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tv_pay_method.setText(data.get(position));
                    // 1银行卡 2微信 3支付宝
                    if (TextUtils.equals(data.get(position), getResources().getString(R.string.yinhanngka))) {
                        payType = 1;
                    } else if (TextUtils.equals(data.get(position), getResources().getString(R.string.weixin))) {
                        payType = 2;
                    } else if (TextUtils.equals(data.get(position), getResources().getString(R.string.zhifubao))) {
                        payType = 3;
                    }
                    listPopup.dismiss();
                }
            });
        }
        if (data != null && data.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                listPopup.setDropDownGravity(Gravity.END);
            }
            listPopup.setAnchorView(tv_pay_method);
            listPopup.show();
        }
    }
}
