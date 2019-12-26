package com.android.tacu.utils.permission;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;

import com.android.tacu.R;
import com.android.tacu.interfaces.OnPermissionListener;
import com.android.tacu.widget.dialog.DroidDialog;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Setting;

import java.util.List;

/**
 * 基于com.yanzhenjie:permission 框架的封装
 * <p>
 * Created by jiazhen on 2018/4/20.
 */

public class PermissionUtils {

    /**
     * @param listener
     * @param permissions
     */
    public static void requestPermissions(final Context context, final OnPermissionListener listener, String[]... permissions) {
        AndPermission.with(context)
                .runtime()
                .permission(permissions)
                //请求的回调
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        listener.onPermissionSucceed();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        listener.onPermissionFailed();

                        if (AndPermission.hasAlwaysDeniedPermission(context, data)) {
                            showSettingDialog(context, data);
                        }
                    }
                })
                //rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                //这样避免用户勾选不再提示，导致以后无法申请权限。
                //你也可以不设置。
                .rationale(new RuntimeRationale())
                .start();
    }

    /**
     * 有些手机申请回调会走失败的方法
     * @param context
     * @param permissions
     */
    private static void showSettingDialog(final Context context, final List<String> permissions) {
        List<String> permissionNames = Permission.transformText(context, permissions);
        String message = context.getString(R.string.permission_message_failed, TextUtils.join(" ", permissionNames));

        new DroidDialog.Builder(context)
                .content(message)
                .positiveButton(context.getResources().getString(R.string.permission_allow), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        setPermission(context);
                    }
                })
                .negativeButton(context.getResources().getString(R.string.permission_refuse), new DroidDialog.onNegativeListener() {
                    @Override
                    public void onNegative(Dialog droidDialog) {
                    }
                })
                .cancelable(false, false)
                .show();
    }

    /**
     * Set permissions.
     */
    private static void setPermission(Context context) {
        AndPermission.with(context)
                .runtime()
                .setting()
                .onComeback(new Setting.Action() {
                    @Override
                    public void onAction() {
                    }
                })
                .start();
    }

}