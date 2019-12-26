package com.android.tacu.utils.permission;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;

import com.android.tacu.R;
import com.android.tacu.widget.dialog.DroidDialog;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.util.List;

public final class RuntimeRationale implements Rationale<List<String>> {

    @Override
    public void showRationale(Context context, List<String> permissions, final RequestExecutor executor) {
        List<String> permissionNames = Permission.transformText(context, permissions);
        @SuppressLint({"StringFormatInvalid", "LocalSuppress"})
        String message = context.getResources().getString(R.string.permission_message_rationale, TextUtils.join(" ", permissionNames));

        new DroidDialog.Builder(context)
                .content(message)
                .positiveButton(context.getResources().getString(R.string.permission_allow), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        executor.execute();
                    }
                })
                .negativeButton(context.getResources().getString(R.string.permission_refuse), new DroidDialog.onNegativeListener() {
                    @Override
                    public void onNegative(Dialog droidDialog) {
                        executor.cancel();
                    }
                })
                .cancelable(false, false)
                .show();
    }
}