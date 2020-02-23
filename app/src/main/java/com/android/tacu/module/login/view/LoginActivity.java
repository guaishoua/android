package com.android.tacu.module.login.view;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

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
        mPresenter.login(etEmail.getText().toString().trim(), Md5Utils.encryptPwd(psd).toLowerCase(), token);
    }

    /**
     * 跳转手势和指纹登录页面的判断
     */
    private void goOtherLogin() {
        if (!isLoginView) {
            if (!TextUtils.isEmpty(LockUtils.getGesture())) {
                jumpTo(GestureActivity.createActivity(LoginActivity.this, isGoMain, isClearTop));
                finish();
            } else if (LockUtils.getIsFinger()) {
                jumpTo(FingerprintActivity.createActivity(LoginActivity.this, isGoMain, isClearTop));
                finish();
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
