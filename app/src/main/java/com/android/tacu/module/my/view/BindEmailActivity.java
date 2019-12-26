package com.android.tacu.module.my.view;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.base.BaseModel;
import com.android.tacu.module.dingxiang.contract.ISwitchView;
import com.android.tacu.module.dingxiang.presenter.SwitchPresenter;
import com.android.tacu.module.my.contract.BindContact;
import com.android.tacu.module.my.presenter.BindPresenter;
import com.android.tacu.utils.RegexUtils;
import com.android.tacu.utils.ShowToast;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xiaohong on 2018/8/28.
 * <p>
 * 绑定和修改邮箱
 */

public class BindEmailActivity extends BaseActivity<BindPresenter> implements BindContact.IBindView, ISwitchView {

    @BindView(R.id.tv_bind_hint)
    TextView tv_bind_hint;
    @BindView(R.id.et_email)
    EditText et_email;
    @BindView(R.id.et_code)
    EditText et_code;
    @BindView(R.id.btn_code)
    QMUIAlphaButton btn_code;
    @BindView(R.id.btn_submit)
    QMUIAlphaButton btn_submit;

    private SwitchPresenter switchPresenter;

    //type: 4老邮箱 , 2新邮箱
    private int type;
    private int flags;
    private String code;
    private String email;
    private boolean verifySuccess = false;

    public static Intent createActivity(Context context, int flags) {
        //flags:1关闭当前页面时需要关闭其BindModeActivity页面
        Intent intent = new Intent(context, BindEmailActivity.class);
        intent.putExtra("flags", flags);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_bind_email);
    }

    @Override
    protected void initView() {
        if (TextUtils.isEmpty(spUtil.getEmail())) {
            type = 2;
            mTopBar.setTitle(getString(R.string.bind_email));
            btn_submit.setText(getString(R.string.sure));
            tv_bind_hint.setText(getResources().getString(R.string.new_email));
            et_email.setEnabled(true);
        } else {
            type = 4;
            mTopBar.setTitle(getString(R.string.revise_email));
            btn_submit.setText(getString(R.string.next));
            tv_bind_hint.setHint(getResources().getString(R.string.old_email));
            et_email.setEnabled(false);
            et_email.setText(spUtil.getEmail());
        }

        flags = getIntent().getIntExtra("flags", 0);

        mTopBar.removeAllLeftViews();
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGoBack();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onGoBack();
        }
        return true;
    }

    private void onGoBack() {
        if (verifySuccess) {
            type = 4;
            timer.cancel();
            verifySuccess = false;

            et_email.setEnabled(false);
            et_email.setText(spUtil.getEmail());
            et_code.setText(spUtil.getSaveCode());
            btn_submit.setText(getString(R.string.next));
            btn_code.setText(getString(R.string.email_code));
            tv_bind_hint.setHint(getResources().getString(R.string.old_email));
        } else {
            finish();
        }
    }

    @Override
    protected BindPresenter createPresenter(BindPresenter mPresenter) {
        return new BindPresenter();
    }

    @Override
    protected void onPresenterCreated(BindPresenter presenter) {
        super.onPresenterCreated(presenter);
        switchPresenter = new SwitchPresenter(BindEmailActivity.this, this);
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

    /**
     * 新验证码 type: 4老邮箱 , 2新邮箱
     */
    @OnClick(R.id.btn_code)
    void sendEmailCode() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.anim_shake);
        String email = et_email.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            et_email.startAnimation(shake);
            return;
        }

        if (type == 2 && !RegexUtils.checkAccount(true, this, email, 0)) {
            return;
        }

        switchPresenter.switchView();
    }

    /**
     * 未绑定邮箱用户
     * 绑定第一步验证手机号
     * 第二步 绑定邮箱
     * <p>
     * 修改邮箱
     * 验证老邮箱验证码通过后绑定新邮箱
     */
    @OnClick(R.id.btn_submit)
    void setbtn_submit() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.anim_shake);
        email = et_email.getText().toString().trim();
        code = et_code.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            et_email.startAnimation(shake);
            return;
        }
        if (type == 2 && !RegexUtils.checkAccount(true, this, email, 0)) {
            return;
        }
        if (TextUtils.isEmpty(code)) {
            et_code.startAnimation(shake);
            return;
        }

        if (TextUtils.isEmpty(spUtil.getEmail())) {
            mPresenter.bindPhone(null, code, spUtil.getSaveCode(), email, "");
        } else {
            if (verifySuccess) {
                mPresenter.bindPhone(null, code, spUtil.getSaveCode(), email, spUtil.getSaveAccount());
            } else {
                mPresenter.validCode(type, code);
            }
        }
    }

    @Override
    public void verifySuccess() {
        type = 2;
        verifySuccess = true;
        timer.cancel();
        btn_submit.setText(getString(R.string.sure));
        btn_code.setEnabled(true);
        btn_code.setText(getString(R.string.email_code));
        spUtil.saveOldCode(et_code.getText().toString().trim());
        spUtil.saveOldAccount(et_email.getText().toString().trim());
        et_code.setText("");
        et_email.setText("");
        et_email.setEnabled(true);
        tv_bind_hint.setHint(getString(R.string.new_email));
    }

    @Override
    public void bindStatus() {
        ShowToast.success(getString(R.string.success));
        spUtil.saveOldCode("");
        spUtil.saveOldAccount("");
        spUtil.setEmailStatus(true);
        if (flags == 1) {
            activityManage.finishActivity(BindModeActivity.class);
        }
        finish();
    }

    @Override
    public void showCodeStatus(BaseModel model) {
        ShowToast.success(getString(R.string.send_successfully));
        timer.start();
    }

    @Override
    public void switchSuccess(String token) {
        String email = et_email.getText().toString().trim();
        //是否绑定过邮箱
        mPresenter.bindPhoneSendMsg(null, email, type, token);
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
}
