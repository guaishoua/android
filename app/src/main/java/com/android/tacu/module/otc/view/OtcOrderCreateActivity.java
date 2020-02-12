package com.android.tacu.module.otc.view;

import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.module.otc.contract.OtcOrderCreateContract;
import com.android.tacu.utils.UIUtils;
import com.android.tacu.widget.popupwindow.ListPopWindow;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 订单生成页
 */
public class OtcOrderCreateActivity extends BaseOtcOrderActvity implements OtcOrderCreateContract.IView {

    @BindView(R.id.tv_pay_method)
    TextView tv_pay_method;
    @BindView(R.id.tv_xieyi)
    TextView tv_xieyi;
    @BindView(R.id.tv_countdown)
    TextView tv_countdown;

    private ListPopWindow listPopup;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_otc_order_create);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.otc_order_create));

        tv_xieyi.setText(Html.fromHtml(getResources().getString(R.string.otc_xieyi) + "<font color=" + ContextCompat.getColor(this, R.color.color_otc_unhappy) + ">" + getResources().getString(R.string.otc_xieyi_name) + "</font>"));
        tv_countdown.setText(Html.fromHtml(getResources().getString(R.string.otc_order_create_countdown1) + "<font color=" + ContextCompat.getColor(OtcOrderCreateActivity.this, R.color.text_error) + "> " + "60 s " + "</font>" + getResources().getString(R.string.otc_order_create_countdown2)));
        timer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listPopup != null) {
            listPopup.dismiss();
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @OnClick(R.id.tv_pay_method)
    void payMethodClick() {
        showAllMannerType();
    }

    private void showAllMannerType() {
        if (listPopup != null && listPopup.isShowing()) {
            listPopup.dismiss();
            return;
        }
        if (listPopup == null) {
            List<String> data = new ArrayList<>();
            data.add(getResources().getString(R.string.yinhanngka));
            data.add(getResources().getString(R.string.zhifubao));
            data.add(getResources().getString(R.string.weixin));
            ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.simple_list_item, data);
            listPopup = new ListPopWindow(this, adapter);
            listPopup.create(UIUtils.dp2px(120), UIUtils.dp2px(122), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        tv_pay_method.setText(getResources().getString(R.string.yinhanngka));
                    } else if (position == 1) {
                        tv_pay_method.setText(getResources().getString(R.string.zhifubao));
                    } else if (position == 2) {
                        tv_pay_method.setText(getResources().getString(R.string.weixin));
                    }
                    listPopup.dismiss();
                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            listPopup.setDropDownGravity(Gravity.END);
        }
        listPopup.setAnchorView(tv_pay_method);
        listPopup.show();
    }

    //第一个参数表示总时间，第二个参数表示间隔时间。意思就是每隔一秒会回调一次方法onTick，然后60秒之后会回调onFinish方法
    private CountDownTimer timer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long arg0) {
            tv_countdown.setText(Html.fromHtml(getResources().getString(R.string.otc_order_create_countdown1) + "<font color=" + ContextCompat.getColor(OtcOrderCreateActivity.this, R.color.text_error) + "> " + (arg0 / 1000) + "s " + "</font>" + getResources().getString(R.string.otc_order_create_countdown2)));
        }

        @Override
        public void onFinish() {
            cancel();
        }
    };
}
