package com.android.tacu.module.my.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.lock.GestureOffActivity;
import com.android.tacu.module.lock.GestureOnActivity;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.my.contract.SecurityCenterContract;
import com.android.tacu.module.my.presenter.SecurityCenterPresenter;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.Md5Utils;
import com.android.tacu.utils.lock.FingerprintUtils;
import com.android.tacu.utils.lock.LockUtils;
import com.android.tacu.utils.user.UserManageUtils;
import com.android.tacu.widget.dialog.DroidDialog;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;

import butterknife.BindView;

/**
 * 安全设置
 */
public class SecurityCenterActivity extends BaseActivity<SecurityCenterPresenter> implements SecurityCenterContract.IView {

    @BindView(R.id.groupListView)
    QMUIGroupListView groupListView;
    @BindView(R.id.listView)
    QMUIGroupListView listView;

    private QMUICommonListItemView itemWithGa;
    private QMUICommonListItemView itemTradePwd;
    private QMUICommonListItemView itemBindPhone;
    private QMUICommonListItemView itemBindEmail;
    private CheckBox checkTrade;
    private CheckBox checkGesture;
    private CheckBox checkFing;

    private String ga;
    private String loginPwd;
    private String tradePwd;
    private String bindPhone;
    private String bindEmail;
    private String tradePwdCheck;
    private String gestureCheck;
    private String fingCheck;

    public static Intent createActivity(Context context, boolean isClearTop) {
        Intent intent = new Intent(context, SecurityCenterActivity.class);
        if (isClearTop) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        return intent;
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_security_center);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getString(R.string.security_setting));

        ga = getResources().getString(R.string.Google_Authenticator);
        itemWithGa = groupListView.createItemView(ga);
        itemWithGa.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        setItemViewLine(itemWithGa, false);

        bindPhone = getResources().getString(R.string.mobile_phone);
        itemBindPhone = groupListView.createItemView(bindPhone);
        itemBindPhone.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        setItemViewLine(itemBindPhone, true);

        bindEmail = getResources().getString(R.string.email_hint);
        itemBindEmail = groupListView.createItemView(bindEmail);
        itemBindEmail.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemBindEmail.setDetailText(getResources().getString(R.string.no_bind));
        setItemViewLine(itemBindEmail, true);

        loginPwd = getResources().getString(R.string.login_pwd);
        QMUICommonListItemView itemLoginPwd = groupListView.createItemView(loginPwd);
        itemLoginPwd.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemLoginPwd.setDetailText(getResources().getString(R.string.pwd_change));
        setItemViewLine(itemLoginPwd, true);

        tradePwd = getResources().getString(R.string.trade_password);
        itemTradePwd = groupListView.createItemView(tradePwd);
        itemTradePwd.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemTradePwd.setDetailText(getResources().getString(R.string.pwd_change));
        setItemViewLine(itemTradePwd, true);

        tradePwdCheck = getResources().getString(R.string.switch_of_trade_password);
        QMUICommonListItemView itemTradePwdCheck = listView.createItemView(tradePwdCheck);
        itemTradePwdCheck.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        checkTrade = getCheckBox();
        itemTradePwdCheck.addAccessoryCustomView(checkTrade);
        setItemViewLine(itemTradePwdCheck, false);

        gestureCheck = getResources().getString(R.string.pattern_lock);
        QMUICommonListItemView itemGestureCheck = listView.createItemView(gestureCheck);
        itemGestureCheck.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        checkGesture = getCheckBox();
        itemGestureCheck.addAccessoryCustomView(checkGesture);
        setItemViewLine(itemGestureCheck, true);

        fingCheck = getResources().getString(R.string.fingerprint_unlock);
        QMUICommonListItemView itemfingCheck = listView.createItemView(fingCheck);
        itemfingCheck.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        checkFing = getCheckBox();
        itemfingCheck.addAccessoryCustomView(checkFing);
        setItemViewLine(itemfingCheck, true);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof QMUICommonListItemView) {
                    String text = (String) ((QMUICommonListItemView) v).getText();
                    mOnClickListenter(text);
                }
            }
        };

        QMUIGroupListView.newSection(this)
                .setUseTitleViewForSectionSpace(false)
                .addItemView(itemWithGa, onClickListener)
                .addItemView(itemBindPhone, onClickListener)
                .addItemView(itemBindEmail, onClickListener)
                .addItemView(itemLoginPwd, onClickListener)
                .addItemView(itemTradePwd, onClickListener)
                .addTo(groupListView);

        QMUIGroupListView.newSection(this)
                .setUseTitleViewForSectionSpace(false)
                .addItemView(itemTradePwdCheck, onClickListener)
                .addItemView(itemGestureCheck, onClickListener)
                .addItemView(itemfingCheck, onClickListener)
                .addTo(listView);

        if (TextUtils.equals(spUtil.getGaStatus(), "0")) {
            itemWithGa.setDetailText(getResources().getString(R.string.ga_not));
        } else if (TextUtils.equals(spUtil.getGaStatus(), "1")) {
            itemWithGa.setDetailText(getResources().getString(R.string.ga_open));
        } else if (TextUtils.equals(spUtil.getGaStatus(), "2")) {
            itemWithGa.setDetailText(getResources().getString(R.string.ga_not));
        }
    }

    @Override
    protected SecurityCenterPresenter createPresenter(SecurityCenterPresenter mPresenter) {
        return new SecurityCenterPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.ownCenter();

        checkTrade.setChecked(spUtil.getPwdVisibility());

        if (!TextUtils.isEmpty(LockUtils.getGesture())) {
            checkGesture.setChecked(true);
        } else {
            checkGesture.setChecked(false);
        }
        if (LockUtils.getIsFinger()) {
            checkFing.setChecked(true);
        } else {
            checkFing.setChecked(false);
        }

        if (TextUtils.equals(spUtil.getGaStatus(), "0")) {
            itemWithGa.setDetailText(getResources().getString(R.string.ga_not));
        } else if (TextUtils.equals(spUtil.getGaStatus(), "1")) {
            itemWithGa.setDetailText(getResources().getString(R.string.ga_open));
        } else if (TextUtils.equals(spUtil.getGaStatus(), "2")) {
            itemWithGa.setDetailText(getResources().getString(R.string.ga_not));
        }
    }

    @Override
    public void ownCenter(OwnCenterModel model) {
        if (model != null) {
            UserManageUtils.setPersonInfo(model);

            if (TextUtils.isEmpty(spUtil.getPhone())) {
                itemBindPhone.setDetailText(getResources().getString(R.string.no_bind));
            } else {
                itemBindPhone.setDetailText(getResources().getString(R.string.pwd_change));
            }
            if (TextUtils.isEmpty(spUtil.getEmail())) {
                itemBindEmail.setDetailText(getResources().getString(R.string.no_bind));
            } else {
                itemBindEmail.setDetailText(getResources().getString(R.string.pwd_change));
            }
            if (spUtil.getValidatePass()) {
                itemTradePwd.setDetailText(getResources().getString(R.string.pwd_change));
            } else {
                itemTradePwd.setDetailText(getResources().getString(R.string.settings));
            }

            //是否每次都需要输入交易密码
            if (TextUtils.equals(model.fdPwdOrderEnabled, "1")) {
                checkTrade.setChecked(true);
            } else if (TextUtils.equals(model.fdPwdOrderEnabled, "2")) {
                checkTrade.setChecked(false);
            }
        }
    }

    @Override
    public void updateFdPwdSuccess() {
        if (spUtil.getPwdVisibility()) {
            spUtil.setPwdVisibility(false);
            showToastSuccess(getResources().getString(R.string.close_tradepwd_message));
        } else {
            spUtil.setPwdVisibility(true);
        }
    }

    @Override
    public void updateFdPwdError() {
        checkTrade.setChecked(spUtil.getPwdVisibility());
    }

    /**
     * 控件跳转
     */
    private void mOnClickListenter(String text) {
        if (TextUtils.equals(text, ga)) {
            if (TextUtils.equals(spUtil.getGaStatus(), "0")) {
                jumpTo(GoogleHintActivity.class);
            } else if (TextUtils.equals(spUtil.getGaStatus(), "1")) {
                jumpTo(GoogleAuthActivity.createActivty(SecurityCenterActivity.this, false));
            } else if (TextUtils.equals(spUtil.getGaStatus(), "2")) {
                jumpTo(GoogleHintActivity.class);
            }
        } else if (TextUtils.equals(text, loginPwd)) {
            jumpTo(TradeActivity.createActivity(SecurityCenterActivity.this, 1));
        } else if (TextUtils.equals(text, tradePwd)) {
            jumpTo(TradeActivity.createActivity(SecurityCenterActivity.this, 2));
        } else if (TextUtils.equals(text, bindPhone)) {
            if (TextUtils.isEmpty(spUtil.getPhone())) {
                jumpTo(BindModeActivity.createActivity(SecurityCenterActivity.this, 3));
            } else {
                jumpTo(BindPhoneActivity.createActivity(this, 0));
            }
        } else if (TextUtils.equals(text, bindEmail)) {
            if (TextUtils.isEmpty(spUtil.getEmail())) {
                jumpTo(BindModeActivity.createActivity(SecurityCenterActivity.this, 4));
            } else {
                jumpTo(BindEmailActivity.createActivity(this, 0));
            }
        } else if (TextUtils.equals(text, tradePwdCheck)) {
            checkTrade.toggle();
            if (spUtil.getValidatePass()) {
                showPwdDialog(checkTrade.isChecked());
            } else {
                showHint();
            }
        } else if (TextUtils.equals(text, gestureCheck)) {
            if (!TextUtils.isEmpty(spUtil.getAccountString())) {
                checkGesture.toggle();
                if (LockUtils.getIsFinger()) {
                    showToastError(getResources().getString(R.string.handpwd_and_gesture_only));
                    checkGesture.setChecked(false);
                    return;
                }
                if (TextUtils.isEmpty(LockUtils.getGesture())) {
                    jumpTo(GestureOnActivity.class);
                } else {
                    jumpTo(GestureOffActivity.class);
                }
            } else {
                tokenInvalid();
                finish();
            }
        } else if (TextUtils.equals(text, fingCheck)) {
            if (!TextUtils.isEmpty(spUtil.getAccountString())) {
                checkFing.toggle();
                if (!TextUtils.isEmpty(LockUtils.getGesture())) {
                    showToastError(getResources().getString(R.string.handpwd_and_gesture_only));
                    checkFing.setChecked(false);
                    return;
                }
                if (FingerprintUtils.isSupportFingerprint(this)) {
                    if (checkFing.isChecked()) {
                        LockUtils.addFinger(true);
                    } else {
                        LockUtils.addFinger(false);
                    }
                } else {
                    checkFing.setChecked(false);
                    LockUtils.addFinger(false);
                }
            } else {
                tokenInvalid();
                finish();
            }
        }
    }

    /**
     * 显示撤销输入交易密码
     *
     * @param isChecked
     */
    private void showPwdDialog(final boolean isChecked) {
        final View view = View.inflate(this, R.layout.view_pwd_dialog, null);
        CommonUtils.handleEditTextEyesIssueInBrightBackground(view.findViewById(R.id.et_pwd));
        new DroidDialog.Builder(SecurityCenterActivity.this)
                .title(getResources().getString(R.string.enter_trading_password))
                .titleGravity(Gravity.CENTER)
                .viewCustomLayout(view)
                .positiveButton(getResources().getString(R.string.sure), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        final QMUIRoundEditText etPassword = view.findViewById(R.id.et_pwd);

                        //将页面输入框中获得的“用户名”，“密码”转为字符串
                        final String pwd = etPassword.getText().toString().trim();
                        if (TextUtils.isEmpty(pwd)) {
                            showToastError(getResources().getString(R.string.enter_trading_password));
                            checkTrade.setChecked(spUtil.getPwdVisibility());
                            return;
                        }
                        mPresenter.updateFdPwdEnabled(isChecked ? 1 : 2, Md5Utils.encryptFdPwd(pwd, spUtil.getUserUid()).toLowerCase());
                    }
                })
                .negativeButton(getResources().getString(R.string.cancel), new DroidDialog.onNegativeListener() {
                    @Override
                    public void onNegative(Dialog droidDialog) {
                        checkTrade.setChecked(spUtil.getPwdVisibility());
                    }
                })
                .cancelable(false, false)
                .show();
    }

    private void showHint() {
        new DroidDialog.Builder(this)
                .content(getResources().getString(R.string.trade_hint))
                .contentGravity(Gravity.CENTER)
                .positiveButton(getResources().getString(R.string.trade_setting), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        jumpTo(TradeActivity.createActivity(SecurityCenterActivity.this, 2));
                    }
                })
                .negativeButton(getResources().getString(R.string.cancel), null)
                .show();
    }

    private void setItemViewLine(QMUICommonListItemView itemView, boolean isboolean) {
        if (isboolean) {
            itemView.setLineView(ContextCompat.getColor(this, R.color.content_bg_color_grey));
        } else {
            itemView.setLineView(ContextCompat.getColor(this, R.color.color_transparent));
        }
    }

    private CheckBox getCheckBox() {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setButtonDrawable(QMUIResHelper.getAttrDrawable(this, R.attr.qmui_common_list_item_switch));
        checkBox.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        checkBox.setClickable(false);
        checkBox.setEnabled(false);
        return checkBox;
    }
}
