package com.android.tacu.module.my.view;

import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.my.contract.ChangePwdContact;
import com.android.tacu.module.my.presenter.ChangePwdPresenter;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.RegexUtils;
import com.android.tacu.utils.ShowToast;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xiaohong on 2018/8/28.
 * <p>
 * 修改登录密码
 */

public class ChangePwdActivity extends BaseActivity<ChangePwdPresenter> implements ChangePwdContact.Iview {
    @BindView(R.id.et_old_pwd)
    QMUIRoundEditText et_old_pwd;
    @BindView(R.id.et_pwd)
    QMUIRoundEditText et_pwd;
    @BindView(R.id.et_confirm_pwd)
    QMUIRoundEditText et_confirm_pwd;
    @BindView(R.id.btn_submit)
    QMUIAlphaButton btn_submit;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_trade_pwd);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getString(R.string.change_pwd));
    }

    @Override
    protected ChangePwdPresenter createPresenter(ChangePwdPresenter mPresenter) {
        return new ChangePwdPresenter();
    }

    @Override
    protected void onPresenterCreated(ChangePwdPresenter presenter) {
        super.onPresenterCreated(presenter);
    }

    @OnClick(R.id.btn_submit)
    void setBtnSubmit() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.anim_shake);
        String newPwd = et_pwd.getText().toString().trim();
        String confirmPwd = et_confirm_pwd.getText().toString().trim();
        String oldPwd = et_old_pwd.getText().toString().trim();


        if (TextUtils.isEmpty(oldPwd)) {
            et_old_pwd.startAnimation(shake);
            return;
        }
        if (TextUtils.isEmpty(newPwd)) {
            et_pwd.startAnimation(shake);
            return;
        } else if (!RegexUtils.checkPassword(newPwd)) {
            ShowToast.error(getString(R.string.regex_pwd));
            return;
        }
        if (TextUtils.isEmpty(confirmPwd)) {
            et_confirm_pwd.startAnimation(shake);
            return;
        }
        if (!newPwd.equals(confirmPwd)) {
            ShowToast.error(getString(R.string.pwd_do_not));
            return;
        }
        mPresenter.resetPwdInUserCenter(newPwd, Md5Utils.encryptPwd(oldPwd).toLowerCase());
    }

    @Override
    public void bindStatus() {
        ShowToast.success(getString(R.string.change_success));
        tokenInvalid();
        activityManage.finishActivity(TradeActivity.class);
        finish();
    }
}
