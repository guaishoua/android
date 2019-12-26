package com.android.tacu.module.my.view;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.base.BaseModel;
import com.android.tacu.module.dingxiang.contract.ISwitchView;
import com.android.tacu.module.dingxiang.presenter.SwitchPresenter;
import com.android.tacu.module.my.contract.BindContact;
import com.android.tacu.module.my.presenter.BindPresenter;
import com.android.tacu.utils.ShowToast;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 绑定方式
 */
public class BindModeActivity extends BaseActivity<BindPresenter> implements BindContact.IBindView, ISwitchView {

    @BindView(R.id.tv_bind_hint)
    TextView tv_bind_hint;
    @BindView(R.id.tv_bind)
    TextView tv_bind;
    @BindView(R.id.et_code)
    EditText et_code;
    @BindView(R.id.btn_submit)
    QMUIAlphaButton btn_submit;
    @BindView(R.id.btn_code)
    QMUIAlphaButton btn_code;

    private int type;
    private SwitchPresenter switchPresenter;

    public static Intent createActivity(Context context, int type) {
        Intent intent = new Intent(context, BindModeActivity.class);
        intent.putExtra("type", type);
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_bind_mode);
    }

    @Override
    protected void initView() {
        type = getIntent().getIntExtra("type", 1);
        if (type == 4) {
            mTopBar.setTitle(getString(R.string.bind_email));

            et_code.setHint(getResources().getString(R.string.phone_code));
            tv_bind_hint.setText(getResources().getString(R.string.bind_phone_hint));
            tv_bind.setText(spUtil.getPhone());
        } else if (type == 3) {
            mTopBar.setTitle(getString(R.string.bind_phone));

            et_code.setHint(getResources().getString(R.string.email_hint_code));
            tv_bind_hint.setText(getResources().getString(R.string.bind_email_hint));
            tv_bind.setText(spUtil.getEmail());
        }
    }

    @Override
    protected BindPresenter createPresenter(BindPresenter mPresenter) {
        return new BindPresenter();
    }

    @Override
    protected void onPresenterCreated(BindPresenter presenter) {
        super.onPresenterCreated(presenter);
        switchPresenter = new SwitchPresenter(BindModeActivity.this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
     * 绑定邮箱或手机
     */
    @OnClick(R.id.btn_submit)
    void verifyBind() {
        String code = et_code.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            ShowToast.error(getResources().getString(R.string.code_cannot_be_empty));
            return;
        }

        if (type == 3) {
            mPresenter.validCode(4, code);
        } else {
            mPresenter.validCode(3, code);
        }

    }

    @OnClick(R.id.btn_code)
    void sendEmailCode() {
        switchPresenter.switchView();
    }

    @Override
    public void bindStatus() {
    }

    @Override
    public void showCodeStatus(BaseModel model) {
        ShowToast.success(getString(R.string.send_successfully));
        timer.start();
    }

    @Override
    public void verifySuccess() {
        if (type == 4) {
            jumpTo(BindEmailActivity.createActivity(this, 1));
        } else if (type == 3) {
            jumpTo(BindPhoneActivity.createActivity(this, 1));
        }
    }

    @Override
    public void switchSuccess(String token) {
        if (type == 3) {
            mPresenter.bindPhoneSendMsg(null, null, 4, token);
        } else if (type == 4) {
            mPresenter.bindPhoneSendMsg(null, null, 3, token);
        }
    }

    //第一个参数表示总时间，第二个参数表示间隔时间。意思就是每隔一秒会回调一次方法onTick，然后60秒之后会回调onFinish方法
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
