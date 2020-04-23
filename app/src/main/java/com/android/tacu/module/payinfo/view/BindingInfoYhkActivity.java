package com.android.tacu.module.payinfo.view;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.interfaces.OnPermissionListener;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.payinfo.contract.PayInfoListContract;
import com.android.tacu.module.payinfo.model.PayInfoModel;
import com.android.tacu.module.otc.dialog.OtcPwdDialogUtils;
import com.android.tacu.module.otc.dialog.OtcTradeDialogUtils;
import com.android.tacu.module.payinfo.presenter.PayInfoPresenter;
import com.android.tacu.utils.ActivityStack;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.permission.PermissionUtils;
import com.android.tacu.widget.dialog.DroidDialog;
import com.yanzhenjie.permission.Permission;

import butterknife.BindView;
import butterknife.OnClick;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class BindingInfoYhkActivity extends BaseActivity<PayInfoPresenter> implements PayInfoListContract.IDetailView {

    @BindView(R.id.lin_edit)
    LinearLayout lin_edit;
    @BindView(R.id.tv_cardholder_name)
    TextView tv_cardholder_name;
    @BindView(R.id.edit_bank_name)
    EditText edit_bank_name;
    @BindView(R.id.edit_open_bank_name)
    EditText edit_open_bank_name;
    @BindView(R.id.edit_bank_id)
    EditText edit_bank_id;

    @BindView(R.id.lin_list)
    LinearLayout lin_list;
    @BindView(R.id.tv_cardholder_name1)
    TextView tv_cardholder_name1;
    @BindView(R.id.tv_bank_name)
    TextView tv_bank_name;
    @BindView(R.id.tv_open_bank_name)
    TextView tv_open_bank_name;
    @BindView(R.id.tv_bank_id)
    TextView tv_bank_id;

    private PayInfoModel payInfoModel;
    private final int MY_SCAN_REQUEST_CODE = 1001;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_yhk);
    }

    @Override
    protected void initView() {
        tv_cardholder_name.setText(spUtil.getKYCName());
        tv_cardholder_name1.setText(spUtil.getKYCName());
    }

    @Override
    protected PayInfoPresenter createPresenter(PayInfoPresenter mPresenter) {
        return new PayInfoPresenter();
    }

    @OnClick(R.id.img_saomiao)
    void saomiaoClick() {
        PermissionUtils.requestPermissions(this, new OnPermissionListener() {
            @Override
            public void onPermissionSucceed() {
                Intent scanIntent = new Intent(BindingInfoYhkActivity.this, CardIOActivity.class);
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, false)
                        .putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false)
                        .putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true)//去除水印
                        .putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true)//去除键盘
                        .putExtra(CardIOActivity.EXTRA_LANGUAGE_OR_LOCALE, "zh-Hans");//设置提示为中文
                startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
            }

            @Override
            public void onPermissionFailed() {
            }
        }, Permission.Group.CAMERA);
    }

    @OnClick(R.id.btn_bindinng)
    void bindingClick() {
        if (!OtcTradeDialogUtils.isDialogShow(this)) {
            final String bankName = edit_bank_name.getText().toString().trim();
            final String openBankName = edit_open_bank_name.getText().toString().trim();
            final String bankCard = edit_bank_id.getText().toString().trim();
            if (TextUtils.isEmpty(bankName)) {
                showToastError(getResources().getString(R.string.please_input_bank_name));
                return;
            }
            if (TextUtils.isEmpty(openBankName)) {
                showToastError(getResources().getString(R.string.please_input_open_bank_name));
                return;
            }
            if (TextUtils.isEmpty(bankCard)) {
                showToastError(getResources().getString(R.string.please_input_bank_id));
                return;
            }

            if (spUtil.getPwdVisibility()) {
                OtcPwdDialogUtils.showPwdDiaglog(this, getResources().getString(R.string.please_input_trade_password), new OtcPwdDialogUtils.OnPassListener() {
                    @Override
                    public void onPass(String pwd) {
                        mPresenter.insert(1, bankName, openBankName, bankCard, null, null, null, null, Md5Utils.encryptFdPwd(pwd, spUtil.getUserUid()).toLowerCase());
                    }
                });
                return;
            }
            mPresenter.insert(1, bankName, openBankName, bankCard, null, null, null, null, null);
        }
    }

    @OnClick(R.id.btn_delete)
    void deleteClick() {
        showDelete();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_SCAN_REQUEST_CODE) {
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
                edit_bank_id.setText(scanResult.cardNumber);
            }
        }
    }

    @Override
    public void insertSuccess() {
        ActivityStack.getInstance().finishActivity(PayInfoTypeActivity.class);
        finish();
    }

    @Override
    public void deleteSuccess() {
        ActivityStack.getInstance().finishActivity(PayInfoTypeActivity.class);
        finish();
    }

    @Override
    public void getOssSetting(AuthOssModel model, String fileLocalNameAddress) {

    }

    @Override
    public void uselectUserInfo(String imageUrl) {

    }

    private void showDelete() {
        new DroidDialog.Builder(this)
                .title(getResources().getString(R.string.tips))
                .content(getResources().getString(R.string.is_delete_account))
                .contentGravity(Gravity.CENTER)
                .positiveButton(getResources().getString(R.string.sure), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        mPresenter.delete(payInfoModel.id);
                    }
                })
                .negativeButton(getResources().getString(R.string.cancel), null)
                .cancelable(false, false)
                .show();
    }

    public void setValue(PayInfoModel model) {
        clearValue();
        this.payInfoModel = model;
        if (model != null) {
            lin_edit.setVisibility(View.GONE);
            lin_list.setVisibility(View.VISIBLE);

            tv_bank_name.setText(model.bankName);
            tv_open_bank_name.setText(model.openBankName);
            tv_bank_id.setText(model.bankCard);
        } else {
            lin_edit.setVisibility(View.VISIBLE);
            lin_list.setVisibility(View.GONE);
        }
    }

    private void clearValue() {
        edit_bank_name.setText("");
        edit_open_bank_name.setText("");
        edit_bank_id.setText("");
        tv_bank_name.setText("");
        tv_open_bank_name.setText("");
        tv_bank_id.setText("");
    }
}
