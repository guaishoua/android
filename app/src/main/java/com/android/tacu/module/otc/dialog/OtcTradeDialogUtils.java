package com.android.tacu.module.otc.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.android.tacu.R;
import com.android.tacu.module.my.view.TradeActivity;
import com.android.tacu.utils.user.UserInfoUtils;
import com.android.tacu.widget.dialog.DroidDialog;
import com.qmuiteam.qmui.alpha.QMUIAlphaButton;

public class OtcTradeDialogUtils {

    private static DroidDialog droidDialog = null;

    /**
     * 为true则弹窗提示
     *
     * @param mContext
     * @return
     */
    public static boolean isDialogShow(final Context mContext) {
        final UserInfoUtils spUtil = UserInfoUtils.getInstance();

        boolean isTradePwd = false;

        if (!spUtil.getValidatePass()) {
            isTradePwd = true;
        }

        if (isTradePwd) {
            View view = View.inflate(mContext, R.layout.view_otc_tradepwd_judge, null);
            QMUIAlphaButton btn_go_setting = view.findViewById(R.id.btn_go_setting);

            btn_go_setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = TradeActivity.createActivity(mContext, 2);
                    mContext.startActivity(intent);
                    droidDialog.dismiss();
                }
            });

            droidDialog = new DroidDialog.Builder(mContext)
                    .title(mContext.getResources().getString(R.string.go_prefect))
                    .viewCustomLayout(view)
                    .positiveButton(mContext.getResources().getString(R.string.sure), new DroidDialog.onPositiveListener() {
                        @Override
                        public void onPositive(Dialog droidDialog) {
                            droidDialog.dismiss();
                        }
                    })
                    .cancelable(false, false)
                    .show();
        }
        return isTradePwd;
    }
}
