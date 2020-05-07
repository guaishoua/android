package com.android.tacu.module.otc.orderView;

import android.app.Dialog;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.OtcDetailNotifyEvent;
import com.android.tacu.R;
import com.android.tacu.module.ZoomImageViewActivity;
import com.android.tacu.module.otc.dialog.OtcTradeDialogUtils;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.presenter.OtcOrderDetailPresenter;
import com.android.tacu.module.otc.view.ArbitrationSubmitActivity;
import com.android.tacu.module.otc.view.OtcOrderDetailActivity;
import com.android.tacu.utils.DateUtils;
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

    private LinearLayout view_btn;
    private QMUIRoundButton btn_left;
    private QMUIRoundButton btn_right;

    private OtcTradeModel tradeModel;
    private Long currentTime;
    private CountDownTimer time;
    private String[] getCountDownTimes;
    private boolean isLock = false;

    private String imageUrlAritrotion;
    private String imageUrlBeAritrotion;

    private DroidDialog cancelDialog;
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

        view_btn = view.findViewById(R.id.view_btn);
        btn_left = view.findViewById(R.id.btn_left);
        btn_right = view.findViewById(R.id.btn_right);

        img_arbitration.setOnClickListener(this);
        img_bearbitration.setOnClickListener(this);
        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);
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
            case R.id.btn_left:
                if (tradeModel != null) {
                    if (tradeModel.buyuid == spUtil.getUserUid()) {
                        activity.startActivity(ArbitrationSubmitActivity.createActivity(activity, false, tradeModel.id, true));
                    } else if (tradeModel.selluid == spUtil.getUserUid()) {
                        activity.startActivity(ArbitrationSubmitActivity.createActivity(activity, false, tradeModel.id, false));
                    }
                }
                break;
            case R.id.btn_right:
                //如果是买方，这里显示取消仲裁按钮
                //如果是卖方，这里显示放币按钮
                if (tradeModel.buyuid == spUtil.getUserUid()) {
                    showCancel();
                } else if (tradeModel.selluid == spUtil.getUserUid()) {
                    if (!OtcTradeDialogUtils.isDialogShow(activity)) {
                        showSure();
                    }
                }
                break;
        }
    }

    public void selectTradeOne(OtcTradeModel model) {
        this.tradeModel = model;
        dealArbitration();
        if (tradeModel != null && tradeModel.status != null && tradeModel.status == 4) {
            dealTime();
        }
    }

    public void currentTime(Long currentTime) {
        this.currentTime = currentTime;
        if (tradeModel != null && tradeModel.status != null && tradeModel.status == 4) {
            dealTime();
        }
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
            setBaseValue(activity, tradeModel, spUtil);
            if (tradeModel.status != null) {
                switch (tradeModel.status) {
                    case 4:
                        rl_top.setVisibility(View.VISIBLE);
                        lin_top.setVisibility(View.GONE);

                        if (tradeModel.beArbitrateUid != null && tradeModel.beArbitrateUid != 0) {
                            view_btn.setVisibility(View.GONE);
                        } else {
                            view_btn.setVisibility(View.VISIBLE);
                            if (tradeModel.buyuid == spUtil.getUserUid()) {
                                btn_left.setText(activity.getResources().getString(R.string.shensu));
                                btn_right.setText(activity.getResources().getString(R.string.cancel));
                            } else if (tradeModel.selluid == spUtil.getUserUid()) {
                                btn_left.setText(activity.getResources().getString(R.string.shensu));
                                btn_right.setText(activity.getResources().getString(R.string.coined));
                            }
                        }
                        break;
                    case 12:
                    case 13:
                        rl_top.setVisibility(View.GONE);
                        lin_top.setVisibility(View.VISIBLE);
                        view_btn.setVisibility(View.GONE);
                        tv_explain.setText(tradeModel.arbitrateResults);
                        break;
                }
            }

            tv_arbitration.setText(tradeModel.arbitrateExp);
            tv_bearbitration.setText(tradeModel.beArbitrateExp);
            if (!TextUtils.isEmpty(tradeModel.arbitrateImg)) {
                img_arbitration.setVisibility(View.VISIBLE);
            } else {
                img_arbitration.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(tradeModel.beArbitrateImg)) {
                img_bearbitration.setVisibility(View.VISIBLE);
            } else {
                img_bearbitration.setVisibility(View.GONE);
            }
        }
    }

    private void showCancel() {
        cancelDialog = new DroidDialog.Builder(activity)
                .title(activity.getResources().getString(R.string.cancel_arbitration))
                .content(activity.getResources().getString(R.string.cancel_arbitration_tip))
                .positiveButton(activity.getResources().getString(R.string.sure), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        if (tradeModel != null) {
                            mPresenter.arbitrationOrderCancel(tradeModel.id);
                        }
                    }
                })
                .negativeButton(activity.getResources().getString(R.string.cancel), null)
                .cancelable(false, false)
                .show();
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
                .viewCustomLayout(view)
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

    private void dealTime() {
        if (currentTime != null && tradeModel != null && !TextUtils.isEmpty(tradeModel.arbitrationEndTime)) {
            if (tradeModel.beArbitrateUid != null && tradeModel.beArbitrateUid != 0) {
                tv_timeout.setVisibility(View.GONE);
                lin_countdown.setVisibility(View.GONE);
                view_btn.setVisibility(View.GONE);
            } else {
                if (tradeModel.status != null && tradeModel.status == 4) {
                    view_btn.setVisibility(View.VISIBLE);
                    long arbitrationEndTime = DateUtils.string2Millis(tradeModel.arbitrationEndTime, DateUtils.DEFAULT_PATTERN) - currentTime;
                    if (arbitrationEndTime > 0) {
                        tv_timeout.setVisibility(View.GONE);
                        lin_countdown.setVisibility(View.VISIBLE);

                        if (tradeModel.buyuid == spUtil.getUserUid()) {
                            btn_left.setVisibility(View.GONE);
                            btn_right.setVisibility(View.VISIBLE);
                        } else if (tradeModel.selluid == spUtil.getUserUid()) {
                            btn_left.setVisibility(View.VISIBLE);
                            btn_right.setVisibility(View.VISIBLE);
                        }
                        startCountDownTimer(arbitrationEndTime);
                    } else {
                        tv_timeout.setVisibility(View.VISIBLE);
                        lin_countdown.setVisibility(View.GONE);

                        if (tradeModel.buyuid == spUtil.getUserUid()) {
                            btn_left.setVisibility(View.VISIBLE);
                            btn_right.setVisibility(View.VISIBLE);
                        } else if (tradeModel.selluid == spUtil.getUserUid()) {
                            btn_left.setVisibility(View.GONE);
                            btn_right.setVisibility(View.GONE);
                        }
                    }
                } else {
                    tv_timeout.setVisibility(View.VISIBLE);
                    lin_countdown.setVisibility(View.GONE);
                    view_btn.setVisibility(View.GONE);
                }
            }
        }
    }

    private void startCountDownTimer(long valueTime) {
        if (time != null) {
            return;
        }
        isLock = false;
        time = new CountDownTimer(valueTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                try {
                    if (isLock) {
                        return;
                    }
                    if (millisUntilFinished >= 0) {
                        getCountDownTimes = DateUtils.getCountDownTime2(millisUntilFinished);
                        if (getCountDownTimes != null && getCountDownTimes.length == 3) {
                            tv_hour.setText(getCountDownTimes[0]);
                            tv_minute.setText(getCountDownTimes[1]);
                            tv_second.setText(getCountDownTimes[2]);
                        }
                    } else {
                        cancel();
                        isLock = true;
                        EventManage.sendEvent(new BaseEvent<>(EventConstant.OTCDetailCode, new OtcDetailNotifyEvent(true)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                cancel();
                isLock = true;
                EventManage.sendEvent(new BaseEvent<>(EventConstant.OTCDetailCode, new OtcDetailNotifyEvent(true)));
            }
        };
        time.start();
    }

    public void destory() {
        activity = null;
        mPresenter = null;
        if (cancelDialog != null && cancelDialog.isShowing()) {
            cancelDialog.dismiss();
        }
        if (droidDialog != null && droidDialog.isShowing()) {
            droidDialog.dismiss();
        }
        if (time != null) {
            time.cancel();
            time = null;
        }
    }
}
