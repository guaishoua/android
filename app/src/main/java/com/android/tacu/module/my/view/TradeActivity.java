package com.android.tacu.module.my.view;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.dingxiang.contract.ISwitchView;
import com.android.tacu.module.dingxiang.presenter.SwitchPresenter;
import com.android.tacu.module.my.contract.ChangePwdContact;
import com.android.tacu.module.my.presenter.ChangePwdPresenter;
import com.android.tacu.utils.CommonUtils;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xiaohong on 2018/8/28.
 * <p>
 * 设置交易密码
 */

public class TradeActivity extends BaseActivity<ChangePwdPresenter> implements ISwitchView, ChangePwdContact.ITradeView {
    //提示语
    @BindView(R.id.tv_bind_hint)
    TextView tv_bind_hint;
    //账号
    @BindView(R.id.et_phone)
    EditText et_phone;
    //短信或邮箱验证码
    @BindView(R.id.et_code)
    EditText et_code;
    //发送验证码
    @BindView(R.id.btn_code)
    QMUIAlphaButton btn_code;
    //提交
    @BindView(R.id.btn_submit)
    QMUIAlphaButton btn_submit;
    ///国家编号
    @BindView(R.id.tv_code)
    TextView tv_code;
    @BindView(R.id.tv_mode_email)
    TextView tv_mode_email;
    @BindView(R.id.tv_mode_phone)
    TextView tv_mode_phone;
    @BindView(R.id.view_email)
    View view_email;
    @BindView(R.id.view_phone)
    View view_phone;
    @BindView(R.id.view_getcode)
    View view_getcode;

    //type 4：郵箱   3：手機
    private int type = 4;
    //flags 1:修改登录密码  2：交易密码
    private int flags;
    private int countCode = CommonUtils.getContryCode();
    private SwitchPresenter switchPresenter;

    public static Intent createActivity(Context context, int flags) {
        Intent intent = new Intent(context, TradeActivity.class);
        intent.putExtra("flags", flags);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_bind_phone);
    }

    @Override
    protected void initView() {
        flags = getIntent().getIntExtra("flags", 0);
        if (flags == 1) {
            mTopBar.setTitle(getResources().getString(R.string.change_pwd));
        } else if (spUtil.getValidatePass()) {
            mTopBar.setTitle(getString(R.string.modify_trade_password));
        } else {
            mTopBar.setTitle(getString(R.string.trade_set_password));
        }

        et_phone.setEnabled(false);
        tv_code.setVisibility(View.GONE);
        tv_bind_hint.setVisibility(View.GONE);
        tv_code.setText("+" + countCode);
        switch (CommonUtils.isBindMode()) {
            case 1:
                type = 4;
                setTextView();
                view_getcode.setVisibility(View.VISIBLE);
                break;
            case 2:
                type = 4;
                et_phone.setText(spUtil.getEmail());
                view_getcode.setVisibility(View.GONE);
                et_code.setHint(getResources().getString(R.string.email_hint_code));
                break;
            case 3:
                type = 3;
                et_phone.setText(spUtil.getPhone());
                view_getcode.setVisibility(View.GONE);
                et_code.setHint(getResources().getString(R.string.phone_code));
                break;
        }
    }

    @Override
    protected ChangePwdPresenter createPresenter(ChangePwdPresenter mPresenter) {
        return new ChangePwdPresenter();
    }

    @Override
    protected void onPresenterCreated(ChangePwdPresenter presenter) {
        super.onPresenterCreated(presenter);
        switchPresenter = new SwitchPresenter(TradeActivity.this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (switchPresenter != null) {
            switchPresenter.destroy();
            switchPresenter = null;
        }
    }

    @OnClick(R.id.tv_mode_email)
    void modeEmail() {
        type = 4;
        setTextView();
    }

    @OnClick(R.id.tv_mode_phone)
    void modePhone() {
        type = 3;
        setTextView();
    }

    @OnClick(R.id.btn_submit)
    void setbtn_submit() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.anim_shake);
        String phone = et_phone.getText().toString().trim();
        String code = et_code.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            et_phone.startAnimation(shake);
            return;
        }
        if (TextUtils.isEmpty(code)) {
            et_code.startAnimation(shake);
            return;
        }

        mPresenter.validCode(type, code);
    }

    @OnClick(R.id.btn_code)
    void verifyPhoneSendMsg() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.anim_shake);
        if (countCode == 0) {
            et_code.startAnimation(shake);
            return;
        }
        if (TextUtils.isEmpty(et_phone.getText().toString().trim())) {
            et_phone.startAnimation(shake);
            return;
        }
        switchPresenter.switchView();
    }

    @Override
    public void switchSuccess(String token) {
        mPresenter.bindPhoneSendMsg(null, null, type, token);
    }

    // 第一个参数表示总时间，第二个参数表示间隔时间。意思就是每隔一秒会回调一次方法onTick，然后60秒之后会回调onFinish方法
    private CountDownTimer timer = new CountDownTimer(60000, 1000) {

        @Override
        public void onTick(long arg0) {
            // 定期定期回调
            btn_code.setText((arg0 / 1000) + "s");
            btn_code.setEnabled(false);
        }

        @Override
        public void onFinish() {
            btn_code.setEnabled(true);
            // 结束后回到
            btn_code.setText(getString(R.string.resend));
        }
    };

    private void setTextView() {
        if (type == 4) {
            view_email.setVisibility(View.VISIBLE);
            view_phone.setVisibility(View.GONE);
            et_phone.setText(spUtil.getEmail());
            et_code.setHint(getResources().getString(R.string.email_hint_code));
            tv_mode_email.setTextColor(ContextCompat.getColor(this, R.color.text_default));
            tv_mode_phone.setTextColor(ContextCompat.getColor(this, R.color.text_color));
        } else {
            view_email.setVisibility(View.GONE);
            view_phone.setVisibility(View.VISIBLE);
            et_phone.setText(spUtil.getPhone());
            et_code.setHint(getResources().getString(R.string.phone_code));
            tv_mode_email.setTextColor(ContextCompat.getColor(this, R.color.text_color));
            tv_mode_phone.setTextColor(ContextCompat.getColor(this, R.color.text_default));
        }
    }

    @Override
    public void bindSendMsg() {
        if (timer != null) {
            timer.start();
        }
        showToastSuccess(getResources().getString(R.string.send_successfully));
    }

    @Override
    public void validCode() {
        if (flags == 1) {
            jumpTo(ChangePwdActivity.class);
        } else {
            jumpTo(TradePwdActivity.class);
        }
    }
}
