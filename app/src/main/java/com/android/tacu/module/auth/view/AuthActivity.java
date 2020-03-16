package com.android.tacu.module.auth.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.auth.contract.AuthContract;
import com.android.tacu.module.auth.model.SelectAuthLevelModel;
import com.android.tacu.module.auth.presenter.AuthPresenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xiaohong on 2018/8/24.
 */

public class AuthActivity extends BaseActivity<AuthPresenter> implements AuthContract.IAuthView {

    @BindView(R.id.tv_kyc1)
    TextView tvKyc1;
    @BindView(R.id.tv_kyc2)
    TextView tvKyc2;
    @BindView(R.id.tv_error_des)
    TextView tv_error_des;

    private SelectAuthLevelModel model;
    private boolean isFirst = true;

    /**
     * @param context
     * @param isClearTop A-B-C-D 如果D-B 需要清空C 设置FLAG_ACTIVITY_CLEAR_TOP 和 FLAG_ACTIVITY_SINGLE_TOP(设置这个则B不需要重新创建)
     * @return
     */
    public static Intent createActivity(Context context, boolean isClearTop) {
        Intent intent = new Intent(context, AuthActivity.class);
        if (isClearTop) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_auth);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.identity_authentication));

        tvKyc1.setTextColor(ContextCompat.getColor(this, R.color.text_grey_2));
        tvKyc1.setText(getResources().getString(R.string.verified));
    }

    @Override
    protected AuthPresenter createPresenter(AuthPresenter mPresenter) {
        return new AuthPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        upload();
    }

    @OnClick(R.id.layout_kyc2)
    void kyc2Click() {
        if (model != null) {
            switch (model.isAuthSenior) {
                case -1:
                case 0:
                    jumpTo(RealNameActivity.class);
                    break;
                case 1:
                    showToastError(getResources().getString(R.string.authenticating));
                    break;
                case 2:
                    showToastSuccess(getResources().getString(R.string.authentication));
                    break;
            }
        } else {
            jumpTo(RealNameActivity.class);
        }
    }

    /**
     * 查询用户等级
     */
    @Override
    public void selectAuthLevel(SelectAuthLevelModel model) {
        this.model = model;

        /**
         * 高级认证 -1审核失败；0未审核；1审核中；2审核成功
         */
        if (model != null) {
            switch (model.isAuthSenior) {
                case -1:
                    tvKyc2.setText(getResources().getString(R.string.authentication_failed));
                    tvKyc2.setTextColor(ContextCompat.getColor(this, R.color.text_red));
                    tv_error_des.setVisibility(View.VISIBLE);
                    tv_error_des.setText(model.auth_fail_reason);
                    break;
                case 0:
                    tvKyc2.setText(getResources().getString(R.string.unverified));
                    tvKyc2.setTextColor(ContextCompat.getColor(this, R.color.color_default));
                    tv_error_des.setVisibility(View.GONE);
                    break;
                case 1:
                    tvKyc2.setText(getResources().getString(R.string.authenticating));
                    tvKyc2.setTextColor(ContextCompat.getColor(this, R.color.text_grey_3));
                    tv_error_des.setVisibility(View.GONE);
                    break;
                case 2:
                    tvKyc2.setText(getResources().getString(R.string.verified));
                    tvKyc2.setTextColor(ContextCompat.getColor(this, R.color.text_grey_3));
                    tv_error_des.setVisibility(View.GONE);
                    break;
            }
        } else {
            tvKyc2.setText(getResources().getString(R.string.unverified));
            tvKyc2.setTextColor(ContextCompat.getColor(this, R.color.color_default));
            tv_error_des.setVisibility(View.GONE);
        }
    }

    private void upload() {
        mPresenter.selectAuthLevel(isFirst);
        if (isFirst) {
            isFirst = false;
        }
    }
}
