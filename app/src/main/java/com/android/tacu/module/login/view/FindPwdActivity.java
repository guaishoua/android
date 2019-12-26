package com.android.tacu.module.login.view;

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
import com.android.tacu.module.login.contract.FindPwdContract;
import com.android.tacu.module.dingxiang.contract.ISwitchView;
import com.android.tacu.module.login.presenter.FindPwdPresenter;
import com.android.tacu.module.dingxiang.presenter.SwitchPresenter;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.RegexUtils;
import com.android.tacu.utils.StatusBarUtils;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * created by jiazhen
 */
public class FindPwdActivity extends BaseActivity<FindPwdPresenter> implements FindPwdContract.IView, ISwitchView {

    @BindView(R.id.tv_find_one)
    TextView tv_find_one;
    @BindView(R.id.tv_find_two)
    QMUIAlphaButton tv_find_two;
    @BindView(R.id.tv_phone_code)
    TextView tv_phone_code;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.tv_code)
    QMUIAlphaButton tv_code;
    @BindView(R.id.et_new_pwd)
    QMUIRoundEditText etNewPwd;
    @BindView(R.id.et_confirm_pwd)
    QMUIRoundEditText etConfirmPwd;
    @BindView(R.id.btnSubmit)
    QMUIAlphaButton btnSubmit;

    private boolean isEmail = true;
    private int countryCode = CommonUtils.getContryCode();
    private static final int REQUESTCODE = 1002;
    private SwitchPresenter switchPresenter;

    @Override
    protected void setView() {
        StatusBarUtils.setColorNoTranslucent(this, ContextCompat.getColor(this, R.color.color_login));
        setContentView(R.layout.activity_find_pwd);
    }

    @Override
    protected void initView() {
        mTopBar.setBackgroundAlpha(0);
        mTopBar.setBackgroundColor(ContextCompat.getColor(this, R.color.color_login));
        mTopBar.removeAllLeftViews();
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        isEmailShow();
        tv_phone_code.setText("+" + countryCode);
    }

    @Override
    protected FindPwdPresenter createPresenter(FindPwdPresenter mPresenter) {
        return new FindPwdPresenter();
    }

    @Override
    protected void onPresenterCreated(FindPwdPresenter presenter) {
        super.onPresenterCreated(presenter);
        switchPresenter = new SwitchPresenter(this, this);
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

    @OnClick(R.id.tv_find_two)
    void findTwo() {
        if (TextUtils.equals(tv_find_two.getText().toString().trim(), getResources().getString(R.string.reset_password_via_phone))) {
            isEmail = false;
        } else {
            isEmail = true;
        }
        isEmailShow();
    }

    @OnClick(R.id.tv_phone_code)
    void phoneCode() {
        jumpTo(CityListActivity.class, REQUESTCODE);
    }

    @OnClick(R.id.tv_find_login)
    void login() {
        jumpTo(LoginActivity.class);
    }

    @OnClick(R.id.tv_find_register)
    void register() {
        jumpTo(RegisterActivity.class);
        finish();
    }

    @OnClick(R.id.btnSubmit)
    void submit() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.anim_shake);
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            etEmail.startAnimation(shake);
            return;
        }
        if (!RegexUtils.checkAccount(isEmail, this, email, countryCode)) {
            return;
        }

        String code = etCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            etCode.startAnimation(shake);
            return;
        }
        String pwd = etNewPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            etNewPwd.startAnimation(shake);
            return;
        } else if (!RegexUtils.checkPassword(pwd)) {
            showToastError(getResources().getString(R.string.regex_pwd));
            return;
        }
        String confirmPwd = etConfirmPwd.getText().toString().trim();
        if (TextUtils.isEmpty(confirmPwd)) {
            etConfirmPwd.startAnimation(shake);
            return;
        }
        if (!pwd.equals(confirmPwd)) {
            showToastError(getResources().getString(R.string.pwd_do_not));
            return;
        }

        if (isEmail) {
            mPresenter.resetPwd(null, email, pwd, code);
        } else {
            mPresenter.resetPwd(String.valueOf(countryCode), email, pwd, code);
        }
    }

    @OnClick(R.id.tv_code)
    void getCode() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.anim_shake);
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            etEmail.startAnimation(shake);
            return;
        }
        if (!RegexUtils.checkAccount(isEmail, this, email, countryCode)) {
            return;
        }

        switchPresenter.switchView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == REQUESTCODE && resultCode == RESULT_OK) {
                countryCode = data.getIntExtra("code", 0);
                tv_phone_code.setText("+" + countryCode);
            }
        }
    }

    private void isEmailShow() {
        if (isEmail) {
            tv_phone_code.setVisibility(View.GONE);

            etEmail.setHint(getResources().getString(R.string.your_email));
            tv_find_one.setText(getResources().getString(R.string.reset_password_via_Email));
            tv_find_two.setText(getResources().getString(R.string.reset_password_via_phone));
        } else {
            tv_phone_code.setVisibility(View.VISIBLE);

            etEmail.setHint(getString(R.string.mobile_phone));
            tv_find_two.setText(getResources().getString(R.string.reset_password_via_Email));
            tv_find_one.setText(getResources().getString(R.string.reset_password_via_phone));
        }
    }

    @Override
    public void sendMailSuccess() {
        showToastSuccess(getResources().getString(R.string.send_successfully));
        if (timer != null) {
            timer.start();
        }
    }

    @Override
    public void resetSuccess() {
        showToastSuccess(getResources().getString(R.string.success));
        jumpTo(LoginActivity.createActivity(this, false, true));
        finish();
    }

    @Override
    public void switchSuccess(String token) {
        if (isEmail) {
            mPresenter.sendMail(null, etEmail.getText().toString().trim(), token);
        } else {
            String email = etEmail.getText().toString().trim();
            mPresenter.sendMail(String.valueOf(countryCode), email, token);
        }
    }

    //第一个参数表示总时间，第二个参数表示间隔时间。意思就是每隔一秒会回调一次方法onTick，然后60秒之后会回调onFinish方法
    private CountDownTimer timer = new CountDownTimer(60000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            //定期定期回调
            tv_code.setText((millisUntilFinished / 1000) + "s");
            tv_code.setEnabled(false);
        }

        @Override
        public void onFinish() {
            tv_code.setEnabled(true);
            // 结束后回到
            tv_code.setText(getResources().getString(R.string.resend));
        }
    };
}
