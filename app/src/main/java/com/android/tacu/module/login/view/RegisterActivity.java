package com.android.tacu.module.login.view;

import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.dingxiang.contract.ISwitchView;
import com.android.tacu.module.dingxiang.presenter.SwitchPresenter;
import com.android.tacu.module.login.contract.RegisterContract;
import com.android.tacu.module.login.presenter.RegisterPresenter;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.RegexUtils;
import com.android.tacu.utils.StatusBarUtils;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 邮箱注册
 * created by jiazhen
 */
public class RegisterActivity extends BaseActivity<RegisterPresenter> implements RegisterContract.IView, ISwitchView {

    @BindView(R.id.qmuiTopbar)
    QMUITopBar qmuiTopbar;
    //邮箱
    @BindView(R.id.tv_regiser_one)
    TextView tv_regiser_one;
    @BindView(R.id.tv_regiser_two)
    QMUIAlphaButton tv_regiser_two;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_pwd)
    QMUIRoundEditText etPwd;
    @BindView(R.id.et_confirm_pwd)
    QMUIRoundEditText etConfirmPwd;
    @BindView(R.id.et_email_code)
    EditText etEmailCode;
    @BindView(R.id.btnRegister)
    QMUIAlphaButton btnRegister;
    @BindView(R.id.btnCode)
    QMUIRoundButton btnCode;
    @BindView(R.id.ll_email)
    LinearLayout ll_email;
    @BindView(R.id.et_invited_code)
    EditText etInvitedCode;

    //手机哈
    @BindView(R.id.tv_phone_code)
    TextView tv_phone_code;
    @BindView(R.id.et_phone)
    EditText et_phone;
    @BindView(R.id.et_phone_pwd)
    QMUIRoundEditText et_phone_pwd;
    @BindView(R.id.et_phone_confirm_pwd)
    QMUIRoundEditText et_phone_confirm_pwd;
    @BindView(R.id.et_phone_code)
    EditText et_phone_code;
    @BindView(R.id.btn_code_phone)
    QMUIAlphaButton btn_code_phone;
    @BindView(R.id.ll_phone)
    LinearLayout ll_phone;
    @BindView(R.id.et_phone_invited_code)
    EditText et_phone_invited_code;

    @BindView(R.id.cb_xieyi)
    CheckBox cb_xieyi;
    @BindView(R.id.lin_introduce)
    LinearLayout lin_introduce;
    @BindView(R.id.cb_introduce)
    CheckBox cb_introduce;

    private Animation shake;
    //注册方式  true：邮箱注册  false：手机号注册
    private boolean registerType = true;
    //默认不会改变
    private int countryCode;
    private static final int REQUESTCODE = 1001;
    private SwitchPresenter switchPresenter;

    @Override
    protected void setView() {
        QMUIStatusBarHelper.translucent(this);
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void initView() {
        StatusBarUtils.moveViewStatusBarHeight(this, qmuiTopbar);

        qmuiTopbar.setBackgroundAlpha(0);
        qmuiTopbar.removeAllLeftViews();
        qmuiTopbar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        phoneCodeShow();
        setCountryCode();

        shake = AnimationUtils.loadAnimation(this, R.anim.anim_shake);

        etInvitedCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString())) {
                    lin_introduce.setVisibility(View.GONE);
                } else {
                    lin_introduce.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        et_phone_invited_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString())) {
                    lin_introduce.setVisibility(View.GONE);
                } else {
                    lin_introduce.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setCountryCode() {
        countryCode = CommonUtils.getContryCode();
        tv_phone_code.setText("+" + countryCode);
    }

    @Override
    protected RegisterPresenter createPresenter(RegisterPresenter mPresenter) {
        return new RegisterPresenter();
    }

    @Override
    protected void onPresenterCreated(RegisterPresenter presenter) {
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
        if (timer2 != null) {
            timer2.cancel();
            timer2 = null;
        }
        if (switchPresenter != null) {
            switchPresenter.destroy();
            switchPresenter = null;
        }
    }

    @OnClick(R.id.tv_regiser_two)
    void registerTwo() {
        if (TextUtils.equals(tv_regiser_two.getText().toString().trim(), getResources().getString(R.string.mobile_phone_register))) {
            registerType = false;
        } else {
            registerType = true;
        }
        phoneCodeShow();
    }

    @OnClick(R.id.tv_phone_code)
    void phoneCode() {
        jumpTo(CityListActivity.class, REQUESTCODE);
    }

    @OnClick(R.id.btnCode)
    void sendEmail() {
        if (isEmail()) {
            return;
        }

        switchPresenter.switchView();
    }

    @OnClick(R.id.tv_xieyi)
    void xieyiClick() {
        if (!TextUtils.isEmpty(Constant.REGISTER_XIEYI)) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(Constant.REGISTER_XIEYI);
            intent.setData(content_url);
            startActivity(intent);
        }
    }

    @OnClick(R.id.btn_code_phone)
    void sendPhone() {
        if (isEmail()) {
            return;
        }
        switchPresenter.switchView();
    }

    @OnClick(R.id.btnRegister)
    void register() {
        String email;
        String confirmPwd;
        String emailCode;
        if (registerType) {
            email = etEmail.getText().toString().trim();
            confirmPwd = etConfirmPwd.getText().toString().trim();
            emailCode = etEmailCode.getText().toString().trim();
        } else {
            email = et_phone.getText().toString().trim();
            confirmPwd = et_phone_confirm_pwd.getText().toString().trim();
            emailCode = et_phone_code.getText().toString().trim();
        }
        if (isEmail()) {
            return;
        }

        if (TextUtils.isEmpty(emailCode)) {
            etEmailCode.startAnimation(shake);
            return;
        }
        if (!cb_xieyi.isChecked()) {
            showToastError(getResources().getString(R.string.yonghu_xieyi));
            return;
        }

        if (registerType) {
            if (TextUtils.isEmpty(etInvitedCode.getText().toString()) && !cb_introduce.isChecked()) {
                showToastError(getResources().getString(R.string.choose_no_people_introduce));
                return;
            }
        } else {
            if (TextUtils.isEmpty(et_phone_invited_code.getText().toString()) && !cb_introduce.isChecked()) {
                showToastError(getResources().getString(R.string.choose_no_people_introduce));
                return;
            }
        }

        if (registerType) {
            mPresenter.register(null, email, confirmPwd, emailCode);
        } else {
            mPresenter.register(String.valueOf(countryCode), email, confirmPwd, emailCode);
        }
    }


    private boolean isEmail() {
        String email;
        String confirmPwd;
        String pwd;
        if (registerType) {
            email = etEmail.getText().toString().trim();
            confirmPwd = etConfirmPwd.getText().toString().trim();
            pwd = etPwd.getText().toString().trim();
        } else {
            email = et_phone.getText().toString().trim();
            confirmPwd = et_phone_confirm_pwd.getText().toString().trim();
            pwd = et_phone_pwd.getText().toString().trim();

            if (countryCode == 0) {
                tv_phone_code.startAnimation(shake);
                showToastError(getResources().getString(R.string.country));
                return true;
            }
        }

        if (!RegexUtils.checkAccount(registerType, this, email, countryCode)) {
            return true;
        }

        if (TextUtils.isEmpty(pwd)) {
            showToastError(getResources().getString(R.string.input_password));
            etPwd.startAnimation(shake);
            return true;
        } else if (!RegexUtils.checkPassword(pwd)) {
            showToastError(getResources().getString(R.string.regex_pwd));
            return true;
        }
        if (TextUtils.isEmpty(confirmPwd)) {
            etConfirmPwd.startAnimation(shake);
            return true;
        }
        if (!confirmPwd.equals(pwd)) {
            showToastError(getResources().getString(R.string.pwd_do_not));
            return true;
        }

        return false;
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

    private void phoneCodeShow() {
        if (registerType) {
            ll_email.setVisibility(View.VISIBLE);
            ll_phone.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(etInvitedCode.getText().toString())) {
                lin_introduce.setVisibility(View.GONE);
            } else {
                lin_introduce.setVisibility(View.VISIBLE);
            }

            tv_regiser_one.setText(getResources().getString(R.string.mobile_email_register));
            tv_regiser_two.setText(getResources().getString(R.string.mobile_phone_register));
        } else {
            ll_email.setVisibility(View.GONE);
            ll_phone.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(et_phone_invited_code.getText().toString())) {
                lin_introduce.setVisibility(View.GONE);
            } else {
                lin_introduce.setVisibility(View.VISIBLE);
            }

            tv_regiser_two.setText(getResources().getString(R.string.mobile_email_register));
            tv_regiser_one.setText(getResources().getString(R.string.mobile_phone_register));
        }
    }

    @Override
    public void showCodeMsg() {
        if (registerType && timer != null) {
            timer.start();
        }
        if (!registerType && timer2 != null) {
            timer2.start();
        }
        showToastSuccess(getResources().getString(R.string.send_successfully));
    }

    @Override
    public void success() {
        showToastSuccess(getResources().getString(R.string.register_success));
        jumpTo(LoginActivity.createActivity(this, false, true, true));
        finish();
    }

    @Override
    public void switchSuccess(String token) {
        if (registerType) {
            mPresenter.sendEmailForRegister(null, etEmail.getText().toString().trim(), token);
        } else {
            String phone = et_phone.getText().toString().trim();
            mPresenter.sendEmailForRegister(String.valueOf(countryCode), phone, token);
        }
    }

    private CountDownTimer timer = new CountDownTimer(60000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            // 定期定期回调
            btnCode.setText((millisUntilFinished / 1000) + "s");
            btnCode.setEnabled(false);
        }

        @Override
        public void onFinish() {
            btnCode.setText(getResources().getString(R.string.resend));
            btnCode.setEnabled(true);
        }
    };

    private CountDownTimer timer2 = new CountDownTimer(60000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            // 定期定期回调
            btn_code_phone.setText((millisUntilFinished / 1000) + "s");
            btn_code_phone.setEnabled(false);
        }

        @Override
        public void onFinish() {
            btn_code_phone.setText(getResources().getString(R.string.resend));
            btn_code_phone.setEnabled(true);
        }
    };
}
