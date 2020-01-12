package com.android.tacu.module.lock;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.base.BaseModel;
import com.android.tacu.db.model.LockNewModel;
import com.android.tacu.module.dingxiang.contract.ISwitchView;
import com.android.tacu.module.dingxiang.presenter.SwitchPresenter;
import com.android.tacu.module.login.contract.LoginContract;
import com.android.tacu.module.login.model.LoginModel;
import com.android.tacu.module.login.presenter.LoginPresenter;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.main.view.MainActivity;
import com.android.tacu.module.market.model.SelfModel;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.SPUtils;
import com.android.tacu.utils.lock.FingerprintUtils;
import com.android.tacu.utils.lock.LockUtils;
import com.android.tacu.utils.user.UserManageUtils;
import com.android.tacu.widget.dialog.DroidDialog;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jiazhen on 2018/6/11.
 */

public class FingerprintActivity extends BaseActivity<LoginPresenter> implements LoginContract.IView, ISwitchView {

    @BindView(R.id.tv_flag)
    TextView tv_flag;

    private static final int FINGERPRINT_NUM = 3;//指纹解锁可以失败3次
    private static final long[] PATTERN = new long[]{0, 300, 200, 250};//指纹解锁失败震动

    private Animation shakeAnimation;
    private int fingerprintIndex = -1;

    private Gson gson = new Gson();
    private SwitchPresenter switchPresenter;
    private int interIndex;
    private int interAll;
    private boolean isGoMain = false;
    private boolean isClearTop = false;

    /**
     * @param context
     * @param isGoMain   为true的情况下  登录成功直接跳转MainActivity
     * @param isClearTop A-B-C-D 如果D-B 需要清空C 设置FLAG_ACTIVITY_CLEAR_TOP 和 FLAG_ACTIVITY_SINGLE_TOP(设置这个则B不需要重新创建)
     * @return
     */
    public static Intent createActivity(Context context, boolean isGoMain, boolean isClearTop) {
        Intent intent = new Intent(context, FingerprintActivity.class);
        intent.putExtra("isGoMain", isGoMain);
        intent.putExtra("isClearTop", isClearTop);
        if (isClearTop) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_fingerprint);
    }

    @Override
    protected void initView() {
        isGoMain = getIntent().getBooleanExtra("isGoMain", false);
        isClearTop = getIntent().getBooleanExtra("isClearTop", false);

        verifyFingerprint();
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
        FingerprintUtils.cancel();
        if (switchPresenter != null) {
            switchPresenter.destroy();
            switchPresenter = null;
        }
    }

    @OnClick(R.id.tv_account_login)
    void accountLoginClick() {
        jumpTo(LoginActivity.createActivity(FingerprintActivity.this, isGoMain, isClearTop, true));
        finish();
    }

    @Override
    public void switchSuccess(String token) {
        LockNewModel lockNewModel = LockUtils.getLockNewModel();
        if (lockNewModel != null) {
            mPresenter.login(lockNewModel.getAccountString(), lockNewModel.getAccountPwd(), token);
        }
    }

    @Override
    public void showContent(BaseModel<LoginModel> model) {
        if (model != null) {
            // 5555谷歌认证，暂时关闭
            if (model.status != 200 && model.status != 5555) {
                showToastError(model.message);
            }
            if (model.status == 200) {
                UserManageUtils.login(model);
                mustNeedInfo();
            }
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

    private void verifyFingerprint() {
        FingerprintUtils.callFingerPrint(new FingerprintUtils.OnCallBackListenr() {

            @Override
            public void onSupportSdk() {
                showToastError(getResources().getString(R.string.fingerprint_no_sdk));
            }

            @Override
            public void onSupportFailed() {
                showToastError(getResources().getString(R.string.fingerprint_no_equipment));
            }

            @Override
            public void onInsecurity() {
                fingerprintReBuildFail();
            }

            @Override
            public void onEnrollFailed() {
                fingerprintReBuildFail();
            }

            @Override
            public void onAuthenticationStart() {
                //开始指纹识别
            }

            @Override
            public void onAuthenticationSucceeded() {
                //解锁成功
                switchPresenter.switchView();
            }

            @Override
            public void onAuthenticationFailed() {
                fingerprintFail();
            }

            @Override
            public void onAuthenticationError() {
                fingerprintFail();
            }

            @Override
            public void onAuthenticationHelp() {
                //这个表示可成功的错误
            }
        });
    }

    /**
     * 失败三次就图案解锁
     */
    private void fingerprintFail() {
        CommonUtils.Vibrate(PATTERN);
        fingerprintIndex++;
        tv_flag.setText(getResources().getString(R.string.fingerprint_retry));
        shakeView(tv_flag);
        //解锁失败
        if (fingerprintIndex >= FINGERPRINT_NUM) {
            fingerprintIndex = -1;
            FingerprintUtils.cancel();
            jumpTo(LoginActivity.createActivity(FingerprintActivity.this, isGoMain, isClearTop, true));
            finish();
        }
    }

    /**
     * 开启APP的指纹后删除系统的指纹  会导致APP的指纹验证异常
     * 解决方法：1.去手机系统的安全设置里面设置手机  2.重新登录（重新登录会清空设置）
     */
    private void fingerprintReBuildFail() {
        new DroidDialog.Builder(FingerprintActivity.this)
                .title(getResources().getString(R.string.fingerprint_rebuild))
                .positiveButton(getResources().getString(R.string.fingerprint_login_again), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        UserManageUtils.logout();
                        jumpTo(LoginActivity.createActivity(FingerprintActivity.this, true, false, true));
                        finish();
                    }
                })
                .negativeButton(getResources().getString(R.string.cancel), null)
                .cancelable(false, false)
                .show();
    }

    /**
     * 震动view
     */
    private void shakeView(View view) {
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_shake);
        view.startAnimation(shakeAnimation);
    }
}
