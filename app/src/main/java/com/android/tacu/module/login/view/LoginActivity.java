package com.android.tacu.module.login.view;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.base.BaseModel;
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.lock.FingerprintActivity;
import com.android.tacu.module.lock.GestureActivity;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.main.view.MainActivity;
import com.android.tacu.module.dingxiang.contract.ISwitchView;
import com.android.tacu.module.login.contract.LoginContract;
import com.android.tacu.module.login.model.LoginModel;
import com.android.tacu.module.login.presenter.LoginPresenter;
import com.android.tacu.module.dingxiang.presenter.SwitchPresenter;
import com.android.tacu.module.market.model.SelfModel;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.utils.StatusBarUtils;
import com.android.tacu.utils.lock.LockUtils;
import com.android.tacu.utils.user.UserManageUtils;
import com.google.gson.Gson;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginContract.IView, ISwitchView {

    //邮箱
    @BindView(R.id.tv_regiser_one)
    TextView tv_regiser_one;
    @BindView(R.id.tv_regiser_two)
    QMUIAlphaButton tv_regiser_two;

    @BindView(R.id.tv_phone_code)
    TextView tv_phone_code;

    @BindView(R.id.img_email)
    ImageView img_email;

    @BindView(R.id.qmuiTopbar)
    QMUITopBar qmuiTopbar;
    @BindView(R.id.et_email)
    QMUIRoundEditText etEmail;
    @BindView(R.id.et_pwd)
    QMUIRoundEditText etPwd;
    @BindView(R.id.btnLogin)
    QMUIAlphaButton btnLogin;

    private int interIndex;
    private int interAll;

    private Gson gson = new Gson();

    private boolean isGoMain = false;
    private boolean isClearTop = false;
    private boolean isLoginView = false;
    private SwitchPresenter switchPresenter;

    //登录方式  true：手机号  false：邮箱
    private boolean loginType = true;

    private int countryCode;
    private static final int REQUESTCODE = 1001;

    /**
     * @param context
     * @param isGoMain   为true的情况下  登录成功直接跳转MainActivity
     * @param isClearTop A-B-C-D 如果D-B 需要清空C 设置FLAG_ACTIVITY_CLEAR_TOP 和 FLAG_ACTIVITY_SINGLE_TOP(设置这个则B不需要重新创建)
     * @return
     */
    public static Intent createActivity(Context context, boolean isGoMain, boolean isClearTop) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("isGoMain", isGoMain);
        intent.putExtra("isClearTop", isClearTop);
        if (isClearTop) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        return intent;
    }

    /**
     * @param context
     * @param isGoMain    为true的情况下  登录成功直接跳转MainActivity
     * @param isClearTop  A-B-C-D 如果D-B 需要清空C 设置FLAG_ACTIVITY_CLEAR_TOP 和 FLAG_ACTIVITY_SINGLE_TOP(设置这个则B不需要重新创建)
     * @param isLoginView 为true表示直接进入LoginActivity，不再判断手势等
     * @return
     */
    public static Intent createActivity(Context context, boolean isGoMain, boolean isClearTop, boolean isLoginView) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("isGoMain", isGoMain);
        intent.putExtra("isClearTop", isClearTop);
        intent.putExtra("isLoginView", isLoginView);
        if (isClearTop) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        return intent;
    }

    @Override
    protected void setView() {
        QMUIStatusBarHelper.translucent(this);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void initView() {
        StatusBarUtils.moveViewStatusBarHeight(this, qmuiTopbar);

        isGoMain = getIntent().getBooleanExtra("isGoMain", false);
        isClearTop = getIntent().getBooleanExtra("isClearTop", false);
        isLoginView = getIntent().getBooleanExtra("isLoginView", false);
        goOtherLogin();

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
    }

    @Override
    protected LoginPresenter createPresenter(LoginPresenter mPresenter) {
        return new LoginPresenter();
    }

    @Override
    protected void onPresenterCreated(LoginPresenter presenter) {
        super.onPresenterCreated(presenter);
        switchPresenter = new SwitchPresenter(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (switchPresenter != null) {
            switchPresenter.destroy();
            switchPresenter = null;
        }
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

    @OnClick(R.id.tv_regiser_two)
    void registerTwo() {
        if (TextUtils.equals(tv_regiser_two.getText().toString().trim(), getResources().getString(R.string.email_login))) {
            loginType = false;
        } else {
            loginType = true;
        }
        phoneCodeShow();
    }

    @OnClick(R.id.tv_phone_code)
    void phoneCode() {
        jumpTo(CityListActivity.class, REQUESTCODE);
    }

    @OnClick(R.id.find_pwd)
    void findPwd() {
        jumpTo(FindPwdActivity.class);
    }

    @OnClick(R.id.register_now)
    void register() {
        jumpTo(RegisterActivity.class);
    }

    @OnClick(R.id.btnLogin)
    void login() {
        if (TextUtils.isEmpty(etEmail.getText().toString().trim())) {
            showToastError(getResources().getString(R.string.mailbox_cannot_be_empty));
            return;
        }

        if (TextUtils.isEmpty(etPwd.getText().toString().trim())) {
            showToastError(getResources().getString(R.string.pwd_cannot_be_empty));
            return;
        }
        switchPresenter.switchView();
    }

    /**
     * 登录的第一步
     */
    @Override
    public void showContent(BaseModel<LoginModel> model) {
        if (model != null) {
            // 5555谷歌认证，暂时关闭
            if (model.status != 200 && model.status != 5555) {
                showToastError(model.message);
            }
            if (model.status == 200) {
                UserManageUtils.login(model, etEmail.getText().toString().trim());
                String pwdS = etPwd.getText().toString().trim();
                LockUtils.addAccountAndPwd(etEmail.getText().toString().trim(), Md5Utils.encryptPwd(pwdS).toLowerCase());
                mustNeedInfo();
            }
        }
    }

    @Override
    public void ownCenterSuccess(OwnCenterModel model) {
        if (model != null) {
            UserManageUtils.setPersonInfo(model);
            // UserManageUtils.loginUnicorn(model);
            getMustNeed();
        }
    }

    @Override
    public void ownCenterError() {
        getMustNeed();
    }

    @Override
    public void getSelfSelectionValueSuccess(SelfModel selfModel) {
        if (selfModel == null) {
            selfModel = new SelfModel();
        }
        SPUtils.getInstance().put(Constant.SELFCOIN_LIST, gson.toJson(selfModel));
        getMustNeed();
    }

    @Override
    public void getSelfSelectionValueError() {
        getMustNeed();
    }

    @Override
    public void selectBankSuccess(List<PayInfoModel> list) {
        UserManageUtils.setPeoplePayInfo(list);
        getMustNeed();
    }

    @Override
    public void selectBankError() {
        getMustNeed();
    }

    /**
     * 滑动验证成功
     */
    @Override
    public void switchSuccess(String token) {
        String psd = etPwd.getText().toString().trim();
        if (loginType) {
            String value = "";
            if (countryCode != 86) {
                value = String.valueOf(countryCode);
            }
            mPresenter.login(value + etEmail.getText().toString().trim(), Md5Utils.encryptPwd(psd).toLowerCase(), token);
        } else {
            mPresenter.login(etEmail.getText().toString().trim(), Md5Utils.encryptPwd(psd).toLowerCase(), token);
        }
    }

    private void phoneCodeShow() {
        etEmail.setText("");
        etPwd.setText("");
        if (loginType) {
            etEmail.setHint(getResources().getString(R.string.mobile_phone));
            tv_phone_code.setVisibility(View.VISIBLE);
            img_email.setVisibility(View.GONE);

            tv_regiser_one.setText(getResources().getString(R.string.phone_login));
            tv_regiser_two.setText(getResources().getString(R.string.email_login));
        } else {
            etEmail.setHint(getResources().getString(R.string.email1));
            tv_phone_code.setVisibility(View.GONE);
            img_email.setVisibility(View.VISIBLE);

            tv_regiser_one.setText(getResources().getString(R.string.email_login));
            tv_regiser_two.setText(getResources().getString(R.string.phone_login));
        }
    }

    private void setCountryCode() {
        countryCode = CommonUtils.getContryCode();
        tv_phone_code.setText("+" + countryCode);
    }

    /**
     * 跳转手势和指纹登录页面的判断
     */
    private void goOtherLogin() {
        if (!isLoginView) {
            if (!TextUtils.isEmpty(LockUtils.getGesture())) {
                jumpTo(GestureActivity.createActivity(LoginActivity.this, isGoMain, isClearTop));
                finish();
                return;
            } else if (LockUtils.getIsFinger()) {
                jumpTo(FingerprintActivity.createActivity(LoginActivity.this, isGoMain, isClearTop));
                finish();
                return;
            }
        }
    }

    /**
     * 登录成功后 需要掉一些接口获取数据
     */
    private void mustNeedInfo() {
        interIndex = 0;
        interAll = 3;
        mPresenter.ownCenter();
        mPresenter.getSelfList();
        mPresenter.selectBank();
    }

    private void getMustNeed() {
        interIndex++;
        if (interIndex >= interAll) {
            showToastSuccess(getResources().getString(R.string.login_success));
            if (isGoMain) {
                jumpTo(MainActivity.class);
            }
            finish();
        }
    }
}
