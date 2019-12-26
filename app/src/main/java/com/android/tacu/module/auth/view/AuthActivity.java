package com.android.tacu.module.auth.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.security.rp.RPSDK;
import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.interfaces.OnPermissionListener;
import com.android.tacu.module.auth.contract.AuthContract;
import com.android.tacu.module.auth.model.AliModel;
import com.android.tacu.module.auth.model.SelectAuthLevelModel;
import com.android.tacu.module.auth.presenter.AuthPresenter;
import com.android.tacu.utils.PackageUtils;
import com.android.tacu.utils.permission.PermissionUtils;
import com.android.tacu.widget.dialog.DroidDialog;
import com.yanzhenjie.permission.Permission;

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
    @BindView(R.id.tv_kyc3)
    TextView tvKyc3;
    @BindView(R.id.tv_error_des)
    TextView tv_error_des;

    private SelectAuthLevelModel model;
    private int kycFlag = 0;

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
        if (!TextUtils.isEmpty(spUtil.getActivityWord1())) {
            mTopBar.addRightImageButton(R.drawable.icon_tip_black, R.id.qmui_topbar_item_right, 12, 16).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showActivityWord();
                }
            });
        }

        tvKyc1.setTextColor(ContextCompat.getColor(this, R.color.text_grey_2));
        tvKyc1.setText(getResources().getString(R.string.verified));
    }

    @Override
    protected void initLazy() {
        super.initLazy();
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.equals(PackageUtils.getVersion(), spUtil.getVersionName())) {
                    hideLoadingView();

                    spUtil.setVersionName(PackageUtils.getVersion());
                    showActivityWord();
                }
            }
        }, 2000);
    }

    @Override
    protected AuthPresenter createPresenter(AuthPresenter mPresenter) {
        return new AuthPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (kycFlag != 3) {
            mPresenter.selectAuthLevel();
        }
    }

    @OnClick(R.id.layout_kyc2)
    void kyc2Click() {
        if (model == null) return;
        switch (model.isAuthSenior) {
            case -1:
            case 0:
                kycFlag = 2;
                jumpTo(RealNameActivity.class);
                break;
            case 1:
                showToastError(getResources().getString(R.string.authenticating));
                break;
            case 2:
                showToastSuccess(getResources().getString(R.string.authentication));
                break;
        }
    }

    @OnClick(R.id.layout_kyc3)
    void kyc3Click() {
        if (model == null) return;
        switch (model.isAuthSenior) {
            case -1:
            case 0:
            case 1:
                showToastError(getResources().getString(R.string.please_get_the_level_of_KYC));
                break;
            case 2:
                /**
                 * 视频认证 -1审核失败；0未审核；1审核中；2审核成功
                 */
                switch (model.isAuthVideo) {
                    case -1:
                    case 0:
                        PermissionUtils.requestPermissions(this, new OnPermissionListener() {
                            @Override
                            public void onPermissionSucceed() {
                                kycFlag = 3;
                                mPresenter.getVerifyToken();
                            }

                            @Override
                            public void onPermissionFailed() {
                            }
                        }, Permission.Group.CAMERA, new String[]{Permission.READ_PHONE_STATE}, Permission.Group.STORAGE);
                        break;
                    case 1:
                        showToastError((getResources().getString(R.string.authenticating)));
                        break;
                    case 2:
                        showToastSuccess((getResources().getString(R.string.authentication)));
                        break;
                }
                break;
            case 3:
                showToastSuccess((getResources().getString(R.string.authentication)));
                break;
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
                    tvKyc3.setText(getResources().getString(R.string.unverified));
                    tvKyc3.setTextColor(ContextCompat.getColor(this, R.color.color_default));
                    break;
                case 0:
                    tvKyc2.setText(getResources().getString(R.string.unverified));
                    tvKyc2.setTextColor(ContextCompat.getColor(this, R.color.color_default));
                    tv_error_des.setVisibility(View.GONE);

                    tvKyc3.setText(getResources().getString(R.string.unverified));
                    tvKyc3.setTextColor(ContextCompat.getColor(this, R.color.color_default));

                    break;
                case 1:
                    tvKyc2.setText(getResources().getString(R.string.authenticating));
                    tvKyc2.setTextColor(ContextCompat.getColor(this, R.color.text_grey_7));
                    tv_error_des.setVisibility(View.GONE);

                    tvKyc3.setText(getResources().getString(R.string.unverified));
                    tvKyc3.setTextColor(ContextCompat.getColor(this, R.color.color_default));
                    break;
                case 2:
                    tvKyc2.setText(getResources().getString(R.string.verified));
                    tvKyc2.setTextColor(ContextCompat.getColor(this, R.color.text_grey_7));
                    tv_error_des.setVisibility(View.GONE);
                    switch (model.isAuthVideo) {
                        case -1:
                            tvKyc3.setText(getResources().getString(R.string.authentication_failed));
                            tvKyc3.setTextColor(ContextCompat.getColor(this, R.color.text_red));
                            tv_error_des.setVisibility(View.VISIBLE);
                            break;
                        case 0:
                            tvKyc3.setText(getResources().getString(R.string.unverified));
                            tvKyc3.setTextColor(ContextCompat.getColor(this, R.color.color_default));
                            tv_error_des.setVisibility(View.GONE);
                            break;
                        case 1:
                            tvKyc3.setText(getResources().getString(R.string.authenticating));
                            tvKyc3.setTextColor(ContextCompat.getColor(this, R.color.text_grey_7));
                            tv_error_des.setVisibility(View.GONE);
                            break;
                        case 2:
                            tvKyc3.setText(getResources().getString(R.string.verified));
                            tvKyc3.setTextColor(ContextCompat.getColor(this, R.color.text_grey_7));
                            tv_error_des.setVisibility(View.GONE);
                            break;
                    }
                    break;
            }
        }
    }

    @Override
    public void getVerifyToken(AliModel model) {
        if (model != null && !TextUtils.isEmpty(model.token)) {
            RPSDK.start(model.token, this, new RPSDK.RPCompletedListener() {
                @Override
                public void onAuditResult(RPSDK.AUDIT audit, String code) {
                    if (audit == RPSDK.AUDIT.AUDIT_PASS) {
                        //认证通过。建议接入方调用实人认证服务端接口DescribeVerifyResult来获取最终的认证状态，并以此为准进行业务上的判断和处理
                        mPresenter.vedioAuth();
                    } else if (audit == RPSDK.AUDIT.AUDIT_FAIL) {
                        //认证不通过。建议接入方调用实人认证服务端接口DescribeVerifyResult来获取最终的认证状态，并以此为准进行业务上的判断和处理
                    } else if (audit == RPSDK.AUDIT.AUDIT_NOT) {
                        //未认证，具体原因可通过code来区分（code取值参见下方表格），通常是用户主动退出或者姓名身份证号实名校验不匹配等原因，导致未完成认证流程
                    }
                }
            });
        }
    }

    @Override
    public void vedioAuth() {
        mPresenter.selectAuthLevel();
    }

    private void showActivityWord() {
        if (!TextUtils.isEmpty(spUtil.getActivityWord1())) {
            new DroidDialog.Builder(this)
                    .content(spUtil.getActivityWord1())
                    .positiveButton(getResources().getString(R.string.node_know), new DroidDialog.onPositiveListener() {
                        @Override
                        public void onPositive(Dialog droidDialog) {
                            droidDialog.dismiss();
                        }
                    }).show();
        }
    }
}
