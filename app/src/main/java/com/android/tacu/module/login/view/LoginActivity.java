package com.android.tacu.module.login.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.base.BaseModel;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.main.view.MainActivity;
import com.android.tacu.module.dingxiang.contract.ISwitchView;
import com.android.tacu.module.login.contract.LoginContract;
import com.android.tacu.module.login.model.LoginModel;
import com.android.tacu.module.login.presenter.LoginPresenter;
import com.android.tacu.module.dingxiang.presenter.SwitchPresenter;
import com.android.tacu.module.market.model.SelfModel;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.utils.user.UserManageUtils;
import com.android.tacu.widget.dialog.DroidDialog;
import com.google.gson.Gson;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginContract.IView, ISwitchView {

    @BindView(R.id.et_email)
    QMUIRoundEditText etEmail;
    @BindView(R.id.et_pwd)
    QMUIRoundEditText etPwd;
    @BindView(R.id.btnLogin)
    QMUIAlphaButton btnLogin;

    private int interIndex;
    private int interAll;

    private Gson gson = new Gson();

    private final int GADIALOG = 1001;
    private boolean isGoMain = false;
    private SwitchPresenter switchPresenter;
    private DroidDialog dialog;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GADIALOG:
                    showPwdDialog();
                    break;
            }
        }
    };

    /**
     * @param context
     * @param isGoMain   为true的情况下  登录成功直接跳转MainActivity
     * @param isClearTop A-B-C-D 如果D-B 需要清空C 设置FLAG_ACTIVITY_CLEAR_TOP 和 FLAG_ACTIVITY_SINGLE_TOP(设置这个则B不需要重新创建)
     * @return
     */
    public static Intent createActivity(Context context, boolean isGoMain, boolean isClearTop) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("isGoMain", isGoMain);
        if (isClearTop) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void initView() {
        isGoMain = getIntent().getBooleanExtra("isGoMain", false);

        mTopBar.setBackgroundAlpha(0);
        mTopBar.removeAllLeftViews();
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (switchPresenter != null) {
            switchPresenter.destroy();
            switchPresenter = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
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
        String pwd = etPwd.getText().toString().trim();
        if (TextUtils.isEmpty(etEmail.getText().toString().trim())) {
            showToastError(getResources().getString(R.string.mailbox_cannot_be_empty));
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
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
            if (model.status != 200 && model.status != 5555) {
                showToastError(model.message);
            }
            if (model.status == 5555) {
                showToast(model.message);
                handler.sendEmptyMessageDelayed(GADIALOG, 2000);
            } else if (model.status == 200) {
                UserManageUtils.login(model);
                mustNeedInfo();
            }
        }
    }

    @Override
    public void showGaStatus(BaseModel<LoginModel> model) {
        if (model != null) {
            if (model.status != 200) {
                showToastError(model.message);
                return;
            }
            UserManageUtils.login(model);
            mustNeedInfo();
        }
    }

    @Override
    public void ownCenterSuccess(OwnCenterModel model) {
        if (model != null) {
            //是否开启谷歌认证
            spUtil.setGaStatus(model.isValidateGoogle);

            //是否每次都需要输入交易密码
            if (TextUtils.equals(model.fdPwdOrderEnabled, "1")) {
                spUtil.setPwdVisibility(true);
            } else if (TextUtils.equals(model.fdPwdOrderEnabled, "2")) {
                spUtil.setPwdVisibility(false);
            }

            //个人信息
            if (!TextUtils.isEmpty(model.email)) {
                spUtil.setEmailStatus(true);
                spUtil.setAccount(model.email);
            }
            if (!TextUtils.isEmpty(model.phone)) {
                spUtil.setPhoneStatus(true);
                spUtil.setAccount(model.phone);
            }

            spUtil.setPhone(model.phone);
            spUtil.setEmail(model.email);
            spUtil.setAuth(model.isAuth);
            spUtil.setIsAuthSenior(model.isAuthSenior);
            spUtil.setValidatePass(model.getIsValidatePass());

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

    /**
     * 滑动验证成功
     */
    @Override
    public void switchSuccess(String token) {
        String psd = etPwd.getText().toString().trim();
        mPresenter.login(etEmail.getText().toString().trim(), Md5Utils.encryptPwd(psd).toLowerCase(), token);
    }

    /**
     * 登录成功后 需要掉一些接口获取数据
     */
    private void mustNeedInfo() {
        interIndex = 0;
        interAll = 2;
        mPresenter.ownCenter();
        mPresenter.getSelfList();
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

    /**
     * 填写谷歌验证码
     */
    private void showPwdDialog() {
        final View view = View.inflate(this, R.layout.view_dialog_ga, null);
        EditText etPassword = view.findViewById(R.id.et_pwd);
        etPassword.setVisibility(View.GONE);
        dialog = new DroidDialog.Builder(this)
                .title(getResources().getString(R.string.Enter_the_Google_verification_code))
                .viewCustomLayout(view)
                .positiveButton(getResources().getString(R.string.sure), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        final EditText etGaPwd = view.findViewById(R.id.et_ga_pwd);
                        final String gaPwd = etGaPwd.getText().toString().trim();
                        if (TextUtils.isEmpty(gaPwd)) {
                            showToastError(getString(R.string.Enter_the_Google_verification_code));
                            return;
                        }
                        mPresenter.loginGASecond(gaPwd, etEmail.getText().toString().trim());
                    }
                })
                .negativeButton(getResources().getString(R.string.cancel), new DroidDialog.onNegativeListener() {
                    @Override
                    public void onNegative(Dialog droidDialog) {
                        dialog.dismiss();
                    }
                })
                .cancelable(false, false)
                .show();
    }
}
