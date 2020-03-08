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
import com.android.tacu.module.login.view.CityListActivity;
import com.android.tacu.module.my.contract.BindContact;
import com.android.tacu.module.my.presenter.BindPresenter;
import com.android.tacu.utils.ActivityStack;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.ShowToast;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xiaohong on 2018/8/28.
 * <p>
 * 绑定或修改手机号
 */

public class BindPhoneActivity extends BaseActivity<BindPresenter> implements BindContact.IBindView, ISwitchView {
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
    @BindView(R.id.view_getcode)
    View view_getcode;

    private int flags;
    private int countCode = CommonUtils.getContryCode();
    private boolean verifySuccess = false;
    private SwitchPresenter switchPresenter;

    public static Intent createActivity(Context context, int flags) {
        //flags:1关闭当前页面时需要关闭其BindModeActivity页面
        Intent intent = new Intent(context, BindPhoneActivity.class);
        intent.putExtra("flags", flags);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_bind_phone);
    }

    @Override
    protected void initView() {
        mTopBar.removeAllLeftViews();
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGoBack();
            }
        });

        view_getcode.setVisibility(View.GONE);
        tv_bind_hint.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(spUtil.getPhone())) {
            mTopBar.setTitle(getString(R.string.bind_phone));

            btn_submit.setText(getString(R.string.sure));
            tv_bind_hint.setText(getResources().getString(R.string.new_phone));
            et_phone.setEnabled(true);
        } else {
            mTopBar.setTitle(getString(R.string.update_phone));

            btn_submit.setText(getString(R.string.next));
            tv_bind_hint.setText(getResources().getString(R.string.old_phone));

            tv_code.setVisibility(View.GONE);
            et_phone.setText(spUtil.getPhone());
        }

        flags = getIntent().getIntExtra("flags", 0);

        tv_code.setText("+" + countCode);
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
            timer.cancel();
            verifySuccess = false;

            et_phone.setEnabled(false);
            tv_code.setVisibility(View.GONE);
            et_phone.setText(spUtil.getPhone());
            et_code.setText(spUtil.getSaveCode());
            spUtil.saveOldCode("");
            spUtil.saveOldAccount("");
            btn_submit.setText(getString(R.string.next));
            btn_code.setText(getString(R.string.email_code));
            tv_bind_hint.setText(getResources().getString(R.string.old_phone));
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
        switchPresenter = new SwitchPresenter(BindPhoneActivity.this, this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            countCode = data.getIntExtra("code", 0);
            tv_code.setText("+" + String.valueOf(countCode));
        }
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

        if (TextUtils.isEmpty(spUtil.getPhone())) {
            mPresenter.bindPhone(String.valueOf(countCode), code, spUtil.getSaveCode(), phone, "");
        } else {
            if (verifySuccess) {
                mPresenter.bindPhone(String.valueOf(countCode), code, spUtil.getSaveCode(), phone, spUtil.getSaveAccount());
            } else {
                mPresenter.validCode(3, code);
            }
        }
    }

    @OnClick(R.id.tv_code)
    void selectCity() {
        jumpTo(CityListActivity.class, 1);
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

        if (countCode == 0) {
            ShowToast.error(getString(R.string.country));
            return;
        }
        switchPresenter.switchView();
    }

    @Override
    public void switchSuccess(String token) {
        String phone = et_phone.getText().toString().trim();
        if (TextUtils.isEmpty(spUtil.getPhone())) {
            mPresenter.bindPhoneSendMsg(String.valueOf(countCode), phone, 1, token);
        } else {
            if (verifySuccess) {
                mPresenter.bindPhoneSendMsg(String.valueOf(countCode), phone, 1, token);
            } else {
                mPresenter.bindPhoneSendMsg(null, null, 3, token);
            }
        }
    }

    @Override
    public void verifySuccess() {
        verifySuccess = true;
        timer.cancel();
        btn_code.setEnabled(true);
        btn_code.setText(getString(R.string.email_code));
        spUtil.saveOldCode(et_code.getText().toString().trim());
        spUtil.saveOldAccount(countCode == 86 ? et_phone.getText().toString().trim() : countCode + et_phone.getText().toString().trim());
        et_code.setText("");
        et_phone.setText("");
        et_phone.setEnabled(true);
        tv_code.setVisibility(View.VISIBLE);
        tv_bind_hint.setText(getResources().getString(R.string.new_phone));
    }

    @Override
    public void bindStatus() {
        ShowToast.success(getString(R.string.success));
        spUtil.setPhoneStatus(true);
        spUtil.saveOldCode("");
        spUtil.saveOldAccount("");
        if (flags == 1) {
            ActivityStack.getInstance().finishActivity(BindModeActivity.class);
        }
        finish();
    }

    @Override
    public void showCodeStatus(BaseModel model) {
        showToastSuccess(getResources().getString(R.string.send_successfully));
        timer.start();
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
