package com.android.tacu.module.otc.orderView;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.tacu.EventBus.EventConstant;
import com.android.tacu.EventBus.EventManage;
import com.android.tacu.EventBus.model.BaseEvent;
import com.android.tacu.EventBus.model.OtcDetailNotifyEvent;
import com.android.tacu.R;
import com.android.tacu.module.ZoomImageViewActivity;
import com.android.tacu.module.payinfo.model.PayInfoModel;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.module.otc.presenter.OtcOrderDetailPresenter;
import com.android.tacu.module.otc.view.OtcOrderDetailActivity;
import com.android.tacu.utils.DateUtils;
import com.android.tacu.utils.GlideUtils;
import com.android.tacu.utils.ShowToast;
import com.android.tacu.utils.user.UserInfoUtils;
import com.android.tacu.widget.dialog.DroidDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundImageView;

//待付款
public class PayedView extends BaseOtcView implements View.OnClickListener {

    private OtcOrderDetailActivity activity;
    private OtcOrderDetailPresenter mPresenter;
    private UserInfoUtils spUtil;

    private TextView tv_hour;
    private TextView tv_minute;
    private TextView tv_second;

    private TextView tv_paytype;

    private LinearLayout lin_wx;
    private TextView tv_account_wx;
    private TextView tv_wx_name;
    private QMUIRoundImageView img_wx_shoukuan;

    private LinearLayout lin_zfb;
    private TextView tv_account_zfb;
    private TextView tv_zfb_name;
    private QMUIRoundImageView img_zfb_shoukuan;

    private LinearLayout lin_yhk;
    private TextView tv_yhk_name;
    private TextView tv_bank_name;
    private TextView tv_open_bank_name;
    private TextView tv_bank_id;

    private QMUIRoundButton btn_giveup;
    private QMUIRoundButton btn_pay;

    private OtcTradeModel tradeModel;
    private Long currentTime;
    private CountDownTimer time;
    private String[] getCountDownTimes;
    private boolean isLock = false;
    private String imageUrl;

    private DroidDialog droidDialog;

    public View create(OtcOrderDetailActivity activity, OtcOrderDetailPresenter mPresenter) {
        this.activity = activity;
        this.mPresenter = mPresenter;
        View statusView = View.inflate(activity, R.layout.view_otc_order_payed, null);
        initPayedView(statusView);
        return statusView;
    }

    /**
     * 待付款
     */
    private void initPayedView(View view) {
        setBaseView(view, activity);

        tv_hour = view.findViewById(R.id.tv_hour);
        tv_minute = view.findViewById(R.id.tv_minute);
        tv_second = view.findViewById(R.id.tv_second);

        tv_paytype = view.findViewById(R.id.tv_paytype);

        lin_wx = view.findViewById(R.id.lin_wx);
        tv_account_wx = view.findViewById(R.id.tv_account_wx);
        tv_wx_name = view.findViewById(R.id.tv_wx_name);
        img_wx_shoukuan = view.findViewById(R.id.img_wx_shoukuan);

        lin_zfb = view.findViewById(R.id.lin_zfb);
        tv_account_zfb = view.findViewById(R.id.tv_account_zfb);
        tv_zfb_name = view.findViewById(R.id.tv_zfb_name);
        img_zfb_shoukuan = view.findViewById(R.id.img_zfb_shoukuan);

        lin_yhk= view.findViewById(R.id.lin_yhk);
        tv_yhk_name = view.findViewById(R.id.tv_yhk_name);
        tv_bank_name = view.findViewById(R.id.tv_bank_name);
        tv_open_bank_name = view.findViewById(R.id.tv_open_bank_name);
        tv_bank_id = view.findViewById(R.id.tv_bank_id);

        btn_giveup = view.findViewById(R.id.btn_giveup);
        btn_pay = view.findViewById(R.id.btn_pay);

        tv_account_wx.setOnClickListener(this);
        tv_wx_name.setOnClickListener(this);
        tv_account_zfb.setOnClickListener(this);
        tv_zfb_name.setOnClickListener(this);
        tv_yhk_name.setOnClickListener(this);
        tv_bank_name.setOnClickListener(this);
        tv_open_bank_name.setOnClickListener(this);
        tv_bank_id.setOnClickListener(this);
        img_wx_shoukuan.setOnClickListener(this);
        img_zfb_shoukuan.setOnClickListener(this);
        btn_giveup.setOnClickListener(this);
        btn_pay.setOnClickListener(this);

        spUtil = UserInfoUtils.getInstance();
    }

    @Override
    public void onClick(View v) {
        ClipboardManager cmb = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        switch (v.getId()) {
            case R.id.tv_account_wx:
                cmb.setText(tv_account_wx.getText().toString()); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
                ShowToast.success(activity.getResources().getString(R.string.copy_success));
                break;
            case R.id.tv_wx_name:
                cmb.setText(tv_wx_name.getText().toString()); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
                ShowToast.success(activity.getResources().getString(R.string.copy_success));
                break;
            case R.id.tv_account_zfb:
                cmb.setText(tv_account_zfb.getText().toString()); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
                ShowToast.success(activity.getResources().getString(R.string.copy_success));
                break;
            case R.id.tv_zfb_name:
                cmb.setText(tv_zfb_name.getText().toString()); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
                ShowToast.success(activity.getResources().getString(R.string.copy_success));
                break;
            case R.id.tv_yhk_name:
                cmb.setText(tv_yhk_name.getText().toString()); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
                ShowToast.success(activity.getResources().getString(R.string.copy_success));
                break;
            case R.id.tv_bank_name:
                cmb.setText(tv_bank_name.getText().toString()); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
                ShowToast.success(activity.getResources().getString(R.string.copy_success));
                break;
            case R.id.tv_open_bank_name:
                cmb.setText(tv_open_bank_name.getText().toString()); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
                ShowToast.success(activity.getResources().getString(R.string.copy_success));
                break;
            case R.id.tv_bank_id:
                cmb.setText(tv_bank_id.getText().toString()); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
                ShowToast.success(activity.getResources().getString(R.string.copy_success));
                break;
            case R.id.img_wx_shoukuan:
            case R.id.img_zfb_shoukuan:
                if (!TextUtils.isEmpty(imageUrl)) {
                    activity.jumpTo(ZoomImageViewActivity.createActivity(activity, imageUrl));
                }
                break;
            case R.id.btn_giveup:
                showCancel();
                break;
            case R.id.btn_pay:
                mPresenter.payOrder(tradeModel.id, null);
                break;
        }
    }

    public void selectTradeOne(OtcTradeModel model) {
        this.tradeModel = model;
        dealPayed();
        dealTime();
    }

    public void currentTime(Long currentTime) {
        this.currentTime = currentTime;
        dealTime();
    }

    public void selectPayInfoById(PayInfoModel model) {
        if (model != null && model.type != null) {
            switch (model.type) {
                case 1:
                    lin_yhk.setVisibility(View.VISIBLE);
                    tv_yhk_name.setText(model.name);
                    tv_paytype.setText(activity.getResources().getString(R.string.yinhanngka));
                    tv_bank_name.setText(model.bankName);
                    tv_open_bank_name.setText(model.openBankName);
                    tv_bank_id.setText(model.bankCard);
                    break;
                case 2:
                    lin_wx.setVisibility(View.VISIBLE);
                    tv_account_wx.setText(model.name);
                    tv_paytype.setText(activity.getResources().getString(R.string.weixin));
                    tv_wx_name.setText(model.weChatNo);
                    mPresenter.uselectUserInfo(model.weChatImg);
                    break;
                case 3:
                    lin_zfb.setVisibility(View.VISIBLE);
                    tv_account_zfb.setText(model.name);
                    tv_paytype.setText(activity.getResources().getString(R.string.zhifubao));
                    tv_zfb_name.setText(model.aliPayNo);
                    mPresenter.uselectUserInfo(model.aliPayImg);
                    break;
            }
        }
    }

    private void dealPayed() {
        if (tradeModel != null) {
            setBaseValue(activity, tradeModel, spUtil);
        }
    }

    public void uselectUserInfo(String imageUrl) {
        this.imageUrl = imageUrl;
        if (!TextUtils.isEmpty(imageUrl)) {
            if (tradeModel != null && tradeModel.payType != null) {
                switch (tradeModel.payType) {//支付类型 1 银行 2微信3支付宝
                    case 2:
                        GlideUtils.disPlay(activity, imageUrl, img_wx_shoukuan);
                        break;
                    case 3:
                        GlideUtils.disPlay(activity, imageUrl, img_zfb_shoukuan);
                        break;
                }
            }
        }
    }

    private void dealTime() {
        if (currentTime != null && tradeModel != null && !TextUtils.isEmpty(tradeModel.payEndTime)) {
            long payEndTime = DateUtils.string2Millis(tradeModel.payEndTime, DateUtils.DEFAULT_PATTERN) - currentTime;
            if (payEndTime > 0) {
                startCountDownTimer(payEndTime);
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

    private void showCancel() {
        droidDialog = new DroidDialog.Builder(activity)
                .title(activity.getResources().getString(R.string.otc_order_cancel))
                .content(activity.getResources().getString(R.string.cancel_order_tip))
                .positiveButton(activity.getResources().getString(R.string.sure), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        mPresenter.payCancelOrder(tradeModel.id);
                    }
                })
                .negativeButton(activity.getResources().getString(R.string.cancel), null)
                .cancelable(false, false)
                .show();
    }

    public void destory() {
        activity = null;
        mPresenter = null;
        if (time != null) {
            time.cancel();
            time = null;
        }
        if (droidDialog != null && droidDialog.isShowing()) {
            droidDialog.dismiss();
        }
    }
}
