package com.android.tacu.module.otc.orderView;

import android.app.Dialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.module.ZoomImageViewActivity;
import com.android.tacu.module.otc.dialog.OtcTradeDialogUtils;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.presenter.OtcOrderDetailPresenter;
import com.android.tacu.module.otc.view.ArbitrationSubmitActivity;
import com.android.tacu.module.otc.view.OtcOrderDetailActivity;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.ShowToast;
import com.android.tacu.utils.user.UserInfoUtils;
import com.android.tacu.widget.dialog.DroidDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundImageView;

public class ArbitrationView extends BaseOtcView implements View.OnClickListener {

    private OtcOrderDetailActivity activity;
    private OtcOrderDetailPresenter mPresenter;
    private UserInfoUtils spUtil;

    private RelativeLayout rl_top;
    private LinearLayout lin_top;
    private TextView tv_explain;

    private LinearLayout lin_countdown;
    private TextView tv_timeout;
    private TextView tv_hour;
    private TextView tv_minute;
    private TextView tv_second;

    private TextView tv_arbitration;
    private QMUIRoundImageView img_arbitration;
    private TextView tv_bearbitration;
    private QMUIRoundImageView img_bearbitration;

    private QMUIRoundButton btn_be;
    private QMUIRoundButton btn_coined;

    private OtcTradeModel tradeModel;
    private String imageUrlAritrotion;
    private String imageUrlBeAritrotion;

    private DroidDialog droidDialog;

    public View create(OtcOrderDetailActivity activity, OtcOrderDetailPresenter mPresenter) {
        this.activity = activity;
        this.mPresenter = mPresenter;
        View statusView = View.inflate(activity, R.layout.view_otc_order_arbitration, null);
        initArbitrationView(statusView);
        return statusView;
    }

    private void initArbitrationView(View view) {
        setBaseView(view, activity);

        rl_top = view.findViewById(R.id.rl_top);
        lin_top = view.findViewById(R.id.lin_top);
        tv_explain = view.findViewById(R.id.tv_explain);

        lin_countdown = view.findViewById(R.id.lin_countdown);
        tv_timeout = view.findViewById(R.id.tv_timeout);
        tv_hour = view.findViewById(R.id.tv_hour);
        tv_minute = view.findViewById(R.id.tv_minute);
        tv_second = view.findViewById(R.id.tv_second);

        tv_arbitration = view.findViewById(R.id.tv_arbitration);
        img_arbitration = view.findViewById(R.id.img_arbitration);
        tv_bearbitration = view.findViewById(R.id.tv_bearbitration);
        img_bearbitration = view.findViewById(R.id.img_bearbitration);

        btn_be = view.findViewById(R.id.btn_be);
        btn_coined = view.findViewById(R.id.btn_coined);

        img_arbitration.setOnClickListener(this);
        img_bearbitration.setOnClickListener(this);
        btn_be.setOnClickListener(this);
        btn_coined.setOnClickListener(this);
        spUtil = UserInfoUtils.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_arbitration:
                if (!TextUtils.isEmpty(imageUrlAritrotion)) {
                    activity.jumpTo(ZoomImageViewActivity.createActivity(activity, imageUrlAritrotion));
                }
                break;
            case R.id.img_bearbitration:
                if (!TextUtils.isEmpty(imageUrlBeAritrotion)) {
                    activity.jumpTo(ZoomImageViewActivity.createActivity(activity, imageUrlBeAritrotion));
                }
                break;
            case R.id.btn_coined:
                if (!OtcTradeDialogUtils.isDialogShow(activity)) {
                    showSure();
                }
                break;
            case R.id.btn_submit_arbitration:
                activity.startActivity(ArbitrationSubmitActivity.createActivity(activity, false, tradeModel.id));
                break;
        }
    }

    public void selectTradeOne(OtcTradeModel model) {
        this.tradeModel = model;
        dealArbitration();
    }

    public void uselectUserInfoArbitration(int type, String imageUrl) {
        switch (type) {
            case 1:
                this.imageUrlAritrotion = imageUrl;
                if (!TextUtils.isEmpty(imageUrl)) {
                    GlideUtils.disPlay(activity, imageUrl, img_arbitration);
                }
                break;
            case 2:
                this.imageUrlBeAritrotion = imageUrl;
                if (!TextUtils.isEmpty(imageUrl)) {
                    GlideUtils.disPlay(activity, imageUrl, img_bearbitration);
                }
                break;
        }
    }

    private void dealArbitration() {
        if (tradeModel != null) {
            if (tradeModel.status != null) {
                switch (tradeModel.status) {
                    case 4:
                        rl_top.setVisibility(View.VISIBLE);
                        lin_top.setVisibility(View.GONE);
                        if (tradeModel.buyuid == spUtil.getUserUid()) {
                            btn_be.setVisibility(View.GONE);
                            btn_coined.setVisibility(View.GONE);
                        } else if (tradeModel.selluid == spUtil.getUserUid()) {
                            btn_be.setVisibility(View.VISIBLE);
                            btn_coined.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 12:
                        rl_top.setVisibility(View.GONE);
                        lin_top.setVisibility(View.VISIBLE);
                        btn_be.setVisibility(View.GONE);
                        btn_coined.setVisibility(View.GONE);

                        tv_explain.setText(activity.getResources().getString(R.string.arbitration_explain1));
                        break;
                    case 13:
                        rl_top.setVisibility(View.GONE);
                        lin_top.setVisibility(View.VISIBLE);
                        btn_be.setVisibility(View.GONE);
                        btn_coined.setVisibility(View.GONE);

                        tv_explain.setText(activity.getResources().getString(R.string.arbitration_explain2));
                        break;
                }
            }

            tv_arbitration.setText(tradeModel.arbitrateExp);
            tv_bearbitration.setText(tradeModel.beArbitrateExp);
            if (!TextUtils.isEmpty(tradeModel.arbitrateImg)) {
                img_arbitration.setVisibility(View.VISIBLE);
                mPresenter.uselectUserInfoArbitration(1, tradeModel.arbitrateImg);
            } else {
                img_arbitration.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(tradeModel.beArbitrateImg)) {
                img_bearbitration.setVisibility(View.VISIBLE);
                mPresenter.uselectUserInfoArbitration(2, tradeModel.beArbitrateImg);
            } else {
                img_bearbitration.setVisibility(View.GONE);
            }
        }
    }

    private void showSure() {
        View view = View.inflate(activity, R.layout.dialog_coined_confirm, null);
        final CheckBox cb_xieyi = view.findViewById(R.id.cb_xieyi);
        final QMUIRoundEditText edit_trade_pwd = view.findViewById(R.id.edit_trade_pwd);

        if (spUtil.getPwdVisibility()) {
            edit_trade_pwd.setVisibility(View.VISIBLE);
        } else {
            edit_trade_pwd.setVisibility(View.GONE);
        }

        droidDialog = new DroidDialog.Builder(activity)
                .title(activity.getResources().getString(R.string.coined_confirm))
                .positiveButton(activity.getResources().getString(R.string.sure), false, new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        if (!cb_xieyi.isChecked()) {
                            ShowToast.error(activity.getResources().getString(R.string.please_check_xieyi));
                            return;
                        }
                        String pwdString = edit_trade_pwd.getText().toString();
                        if (spUtil.getPwdVisibility() && TextUtils.isEmpty(pwdString)) {
                            ShowToast.error(activity.getResources().getString(R.string.please_input_trade_password));
                            return;
                        }

                        if (tradeModel != null) {
                            droidDialog.dismiss();
                            mPresenter.finishOrder(tradeModel.id, spUtil.getPwdVisibility() ? Md5Utils.encryptFdPwd(pwdString, spUtil.getUserUid()).toLowerCase() : null);
                        }
                    }
                })
                .negativeButton(activity.getResources().getString(R.string.cancel), null)
                .cancelable(false, false)
                .show();
    }

    public void destory() {
        activity = null;
        mPresenter = null;
        if (droidDialog != null && droidDialog.isShowing()) {
            droidDialog.dismiss();
        }
    }
}
