package com.android.tacu.module.my.view;

import com.android.tacu.module.dingxiang.contract.ISwitchView;
import com.android.tacu.module.dingxiang.presenter.SwitchPresenter;
import com.android.tacu.module.my.contract.GoogleAuthContact;
import com.android.tacu.module.my.presenter.GoogleAuthPresenter;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.utils.ActivityStack;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.ShowToast;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 谷歌绑定第三步
 */
public class GoogleAuthActivity extends BaseActivity<GoogleAuthPresenter> implements ISwitchView, GoogleAuthContact.IView {
    @BindView(R.id.view_email)
    View view_email;
    @BindView(R.id.view_phone)
    View view_phone;
    @BindView(R.id.tv_mode_email)
    TextView tv_mode_email;
    @BindView(R.id.tv_mode_phone)
    TextView tv_mode_phone;
    @BindView(R.id.tv_getcode)
    TextView tv_getcode;
    @BindView(R.id.et_ga_pwd)
    EditText etGaPwd;
    @BindView(R.id.et_phone_code)
    EditText et_phone_code;
    @BindView(R.id.ll_bind_mode)
    LinearLayout ll_bind_mode;
    @BindView(R.id.et_pwd)
    QMUIRoundEditText et_pwd;
    @BindView(R.id.btn_submit)
    QMUIAlphaButton btn_submit;

    private int type = 4;//3：手机  4:邮箱默认
    private boolean isBind;//true:绑定谷歌验证  false:解除谷歌验证
    private SwitchPresenter switchPresenter;

    public static Intent createActivty(Context context, boolean isBind) {
        Intent intent = new Intent(context, GoogleAuthActivity.class);
        intent.putExtra("isBind", isBind);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_google_auth);
    }

    @Override
    protected void initView() {
        isBind = getIntent().getBooleanExtra("isBind", true);
        if (isBind) {
            mTopBar.setTitle(getResources().getString(R.string.bind_ga));
            btn_submit.setText(getResources().getString(R.string.bind_ga));
        } else {
            mTopBar.setTitle(getResources().getString(R.string.clase_ga));
            btn_submit.setText(getResources().getString(R.string.clase_ga));
        }

        switch (CommonUtils.isBindMode()) {
            case 1:
                type = 4;
                ll_bind_mode.setVisibility(View.VISIBLE);
                setTextView();
                break;
            case 2:
                type = 4;
                ll_bind_mode.setVisibility(View.GONE);
                et_phone_code.setHint(getResources().getString(R.string.email_hint_code));
                break;
            case 3:
                type = 3;
                ll_bind_mode.setVisibility(View.GONE);
                et_phone_code.setHint(getResources().getString(R.string.phone_code));
                break;
        }
    }

    @Override
    protected GoogleAuthPresenter createPresenter(GoogleAuthPresenter mPresenter) {
        return new GoogleAuthPresenter();
    }

    @Override
    protected void onPresenterCreated(GoogleAuthPresenter presenter) {
        super.onPresenterCreated(presenter);
        switchPresenter = new SwitchPresenter(this, this);
    }

    /**
     * 邮箱验证
     */
    @OnClick(R.id.tv_mode_email)
    void modeEmail() {
        type = 4;
        setTextView();
    }

    /**
     * 手机验证
     */
    @OnClick(R.id.tv_mode_phone)
    void modePhone() {
        type = 3;
        setTextView();
    }

    /**
     * 获取验证码
     */
    @OnClick(R.id.tv_getcode)
    void getCode() {
        switchPresenter.switchView();
    }

    /**
     * 提交
     */
    @OnClick(R.id.btn_submit)
    void bindGa() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.anim_shake);
        String pwd = et_pwd.getText().toString().trim();
        String gaPwd = etGaPwd.getText().toString().trim();
        String code = et_phone_code.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            et_pwd.startAnimation(shake);
            return;
        }
        if (TextUtils.isEmpty(gaPwd)) {
            etGaPwd.startAnimation(shake);
            return;
        }
        if (TextUtils.isEmpty(code)) {
            et_phone_code.startAnimation(shake);
        }
        if (isBind) {
            mPresenter.bindGoogleAuth(gaPwd, Md5Utils.encryptPwd(pwd).toLowerCase(), code, type);
        } else {
            mPresenter.closeGoogleAuth(gaPwd, Md5Utils.encryptPwd(pwd).toLowerCase(), code, type);
        }

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

    private void setTextView() {
        if (type == 4) {
            view_email.setVisibility(View.VISIBLE);
            view_phone.setVisibility(View.GONE);
            et_phone_code.setHint(getResources().getString(R.string.email_hint_code));
            tv_mode_email.setTextColor(ContextCompat.getColor(this, R.color.text_default));
            tv_mode_phone.setTextColor(ContextCompat.getColor(this, R.color.text_color));
        } else {
            view_email.setVisibility(View.GONE);
            view_phone.setVisibility(View.VISIBLE);
            et_phone_code.setHint(getResources().getString(R.string.phone_code));
            tv_mode_email.setTextColor(ContextCompat.getColor(this, R.color.text_color));
            tv_mode_phone.setTextColor(ContextCompat.getColor(this, R.color.text_default));
        }
    }

    @Override
    public void switchSuccess(String token) {
        mPresenter.bindgaSendMsg(type, token);
    }

    /**
     * 关闭ga成功
     */
    @Override
    public void colseSuccess() {
        timer.cancel();
        spUtil.setGaStatus("2");
        showToastSuccess(getResources().getString(R.string.success));

        jumpTo(SecurityCenterActivity.createActivity(GoogleAuthActivity.this, true));
    }

    /**
     * 开启谷歌验证
     */
    @Override
    public void success() {
        timer.cancel();
        spUtil.setGaStatus("1");
        showToastSuccess(getResources().getString(R.string.success));
        ActivityStack.getInstance().finishActivity(GoogleHintActivity.class);
        finish();
    }

    @Override
    public void sendMsgSuccess() {
        ShowToast.success(getString(R.string.send_successfully));
        timer.start();
    }

    // 第一个参数表示总时间，第二个参数表示间隔时间。意思就是每隔一秒会回调一次方法onTick，然后60秒之后会回调onFinish方法
    private CountDownTimer timer = new CountDownTimer(60000, 1000) {

        @Override
        public void onTick(long arg0) {
            // 定期定期回调
            tv_getcode.setText((arg0 / 1000) + "s");
            tv_getcode.setEnabled(false);
        }

        @Override
        public void onFinish() {
            tv_getcode.setEnabled(true);
            // 结束后回到
            tv_getcode.setText(getString(R.string.resend));
        }
    };
}
