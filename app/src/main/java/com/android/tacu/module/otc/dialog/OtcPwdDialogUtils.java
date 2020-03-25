package com.android.tacu.module.otc.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.android.tacu.R;
import com.android.tacu.utils.ShowToast;
import com.android.tacu.widget.dialog.DroidDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundEditText;

public class OtcPwdDialogUtils {

    public static void showPwdDiaglog(final Context mContext, String title, final OnPassListener listener) {
        View view = View.inflate(mContext, R.layout.view_dialog_trade_pwd, null);
        final QMUIRoundEditText edit_trade_pwd = view.findViewById(R.id.edit_trade_pwd);

        new DroidDialog.Builder(mContext)
                .title(title)
                .viewCustomLayout(view)
                .positiveButton(mContext.getResources().getString(R.string.sure), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        String pwd = edit_trade_pwd.getText().toString().trim();
                        if (TextUtils.isEmpty(pwd)) {
                            ShowToast.error(mContext.getResources().getString(R.string.please_input_trade_password));
                            return;
                        }
                        if (listener != null) {
                            listener.onPass(pwd);
                        }
                    }
                })
                .negativeButton(mContext.getResources().getString(R.string.cancel), null)
                .cancelable(false, false)
                .show();
    }

    public interface OnPassListener {
        void onPass(String pwd);
    }
}
