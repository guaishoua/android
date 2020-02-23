package com.android.tacu.module.otc.dialog;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.tacu.R;
import com.android.tacu.module.auth.view.AuthActivity;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.module.my.view.BindModeActivity;
import com.android.tacu.module.my.view.EditPersonalDataActivity;
import com.android.tacu.module.my.view.TradeActivity;
import com.android.tacu.utils.user.UserInfoUtils;
import com.android.tacu.widget.dialog.DroidDialog;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;

public class OtcDialogUtils {

    private static DroidDialog droidDialog = null;

    /**
     * 为true则弹窗提示
     *
     * @param mContext
     * @return
     */
    public static boolean isDialogShow(final Context mContext) {
        final UserInfoUtils spUtil = UserInfoUtils.getInstance();
        if (!spUtil.getLogin()) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            mContext.startActivity(intent);
            return true;
        }

        boolean isFlag = false;
        boolean isAuth = false, isPhone = false, isTradePwd = false, isNickHead = false, isPayInfo = false;

        if (spUtil.getIsAuthSenior() < 2) {
            isFlag = true;
            isAuth = true;
        }
        if (!spUtil.getPhoneStatus()) {
            isFlag = true;
            isPhone = true;
        }
        if (!spUtil.getValidatePass()) {
            isFlag = true;
            isTradePwd = true;
        }
        if (TextUtils.isEmpty(spUtil.getNickName()) || TextUtils.isEmpty(spUtil.getHeadImg())) {
            isFlag = true;
            isNickHead = true;
        }
        if (!spUtil.getIsPayInfo()) {
            isFlag = true;
            isPayInfo = true;
        }

        if (isFlag) {
            View view = View.inflate(mContext, R.layout.view_otc_judge, null);
            RelativeLayout rl_auth = view.findViewById(R.id.rl_auth);
            RelativeLayout rl_tel = view.findViewById(R.id.rl_tel);
            RelativeLayout rl_trade_pwd = view.findViewById(R.id.rl_trade_pwd);
            RelativeLayout rl_nickname_head = view.findViewById(R.id.rl_nickname_head);
            RelativeLayout rl_binding_pay_info = view.findViewById(R.id.rl_binding_pay_info);

            QMUIAlphaButton btn_go_auth = view.findViewById(R.id.btn_go_auth);
            QMUIAlphaButton btn_go_binding = view.findViewById(R.id.btn_go_binding);
            QMUIAlphaButton btn_go_setting = view.findViewById(R.id.btn_go_setting);
            QMUIAlphaButton btn_go_prefect = view.findViewById(R.id.btn_go_prefect);
            QMUIAlphaButton btn_go_binding_pay = view.findViewById(R.id.btn_go_binding_pay);

            btn_go_auth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, AuthActivity.class);
                    mContext.startActivity(intent);
                    droidDialog.dismiss();
                }
            });
            btn_go_binding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = BindModeActivity.createActivity(mContext, 3);
                    mContext.startActivity(intent);
                    droidDialog.dismiss();
                }
            });
            btn_go_setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = TradeActivity.createActivity(mContext, 2);
                    mContext.startActivity(intent);
                    droidDialog.dismiss();
                }
            });
            btn_go_prefect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, EditPersonalDataActivity.class);
                    mContext.startActivity(intent);
                    droidDialog.dismiss();
                }
            });
            btn_go_binding_pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, EditPersonalDataActivity.class);
                    mContext.startActivity(intent);
                    droidDialog.dismiss();
                }
            });

            if (isAuth) {
                rl_auth.setVisibility(View.VISIBLE);
            } else {
                rl_auth.setVisibility(View.GONE);
            }
            if (isPhone) {
                rl_tel.setVisibility(View.VISIBLE);
            } else {
                rl_tel.setVisibility(View.GONE);
            }
            if (isTradePwd) {
                rl_trade_pwd.setVisibility(View.VISIBLE);
            } else {
                rl_trade_pwd.setVisibility(View.GONE);
            }
            if (isNickHead) {
                rl_nickname_head.setVisibility(View.VISIBLE);
            } else {
                rl_nickname_head.setVisibility(View.GONE);
            }
            if (isPayInfo) {
                rl_binding_pay_info.setVisibility(View.VISIBLE);
            } else {
                rl_binding_pay_info.setVisibility(View.GONE);
            }

            droidDialog = new DroidDialog.Builder(mContext)
                    .title(mContext.getResources().getString(R.string.go_prefect))
                    .viewCustomLayout(view)
                    .cancelable(false, false)
                    .show();
        }
        return isFlag;
    }
}
