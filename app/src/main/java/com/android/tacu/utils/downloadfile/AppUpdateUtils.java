package com.android.tacu.utils.downloadfile;

import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.module.main.model.UploadModel;
import com.android.tacu.widget.dialog.DroidDialog;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.service.DownloadService;
import com.vector.update_app.view.NumberProgressBar;

import java.io.File;

/**
 * app更新dialog
 * Created by jiazhen on 2018/8/30.
 */
public class AppUpdateUtils {

    private static final int APPSTART = 1001;
    private static final int APPPROGRESS = 1002;
    private static final int APPFINISH = 1003;
    private static final int APPERROR = 1004;

    private static Dialog dialog;
    private static DroidDialog droidDialog;
    private static DroidDialog.Builder builder;
    private static Button btnOk;
    private static NumberProgressBar progressBar;

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case APPSTART:
                    if (btnOk != null) {//简单的更新的时候用不到这个控件
                        btnOk.setVisibility(View.GONE);
                    }
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case APPPROGRESS:
                    progressBar.setProgress(msg.arg1);
                    break;
                case APPFINISH:
                    if (btnOk != null) {
                        btnOk.setVisibility(View.VISIBLE);
                    }
                    progressBar.setVisibility(View.GONE);
                    break;
                case APPERROR:
                    if (btnOk != null) {
                        btnOk.setVisibility(View.VISIBLE);
                    }
                    progressBar.setVisibility(View.GONE);
                    break;
            }
        }
    };

    public static void showSimpleUpdate(final Context mContext, final UploadModel model) {
        if (model == null)
            return;
        if (builder == null) {
            builder = new DroidDialog.Builder(mContext);

            View customView = View.inflate(mContext, R.layout.view_dialog_simple_update, null);
            TextView tvTitle = customView.findViewById(R.id.tv_title);
            TextView tvUpdateInfo = customView.findViewById(R.id.tv_update_info);
            progressBar = customView.findViewById(R.id.progressBar);

            tvTitle.setText(mContext.getResources().getString(R.string.app_update_title) + model.nowVersion);
            tvUpdateInfo.setText(model.msgContent);

            builder.title(mContext.getResources().getString(R.string.update))
                    .viewCustomLayout(customView)
                    .positiveButton(mContext.getResources().getString(R.string.sure), false, new DroidDialog.onPositiveListener() {
                        @Override
                        public void onPositive(Dialog droidDialog) {
                            appDownLoad(mContext, model.appUrl);
                        }
                    });
            //强更
            if (model.updateForce == 1) {
                builder.cancelable(false, false);
            } else {
                builder.negativeButton(mContext.getResources().getString(R.string.cancel), null)
                        .cancelable(true, false);
            }
        }
        if (droidDialog == null) {
            droidDialog = new DroidDialog(builder);
        }
        droidDialog.show();
    }

    public static void showUpdate(final Context mContext, final UploadModel model) {
        if (model == null)
            return;
        if (dialog == null) {
            dialog = new Dialog(mContext, R.style.UpdateAppDialog);
            dialog.setContentView(R.layout.view_dialog_update);

            LinearLayout linClose = dialog.findViewById(R.id.lin_close);
            TextView tvTitle = dialog.findViewById(R.id.tv_title);
            TextView tvUpdateInfo = dialog.findViewById(R.id.tv_update_info);
            ImageView imgClose = dialog.findViewById(R.id.img_close);
            btnOk = dialog.findViewById(R.id.btn_ok);
            progressBar = dialog.findViewById(R.id.progressBar);

            tvTitle.setText(mContext.getResources().getString(R.string.app_update_title) + model.nowVersion);
            tvUpdateInfo.setText(model.msgContent);

            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    appDownLoad(mContext, model.appUrl);
                }
            });

            //强更
            if (model.updateForce == 1) {
                linClose.setVisibility(View.GONE);
                dialog.setCancelable(false);
            }

            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.show();
    }

    public static void cancel() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    //下载app
    private static void appDownLoad(Context mContext, String mApkFileUrl) {
        String path = "";
        UpdateAppBean updateAppBean = new UpdateAppBean();
        //设置apk的下载地址
        updateAppBean.setApkFileUrl(mApkFileUrl);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
            try {
                path = mContext.getExternalCacheDir().getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(path)) {
                path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            }
        } else {
            path = mContext.getCacheDir().getAbsolutePath();
        }

        //设置apk 的保存路径
        updateAppBean.setTargetPath(path);
        //实现网络接口，只实现下载就可以
        updateAppBean.setHttpManager(new UpdateAppHttpUtil());

        UpdateAppManager.download(mContext, updateAppBean, new DownloadService.DownloadCallback() {
            @Override
            public void onStart() {
                handler.sendEmptyMessage(APPSTART);
            }

            @Override
            public void onProgress(float progress, long totalSize) {
                Message message = handler.obtainMessage(APPPROGRESS);
                message.arg1 = (int) progress;
                handler.sendMessage(message);
            }

            @Override
            public void setMax(long totalSize) {
            }

            @Override
            public boolean onFinish(File file) {
                handler.sendEmptyMessage(APPFINISH);
                return true;
            }

            @Override
            public void onError(String msg) {
                handler.sendEmptyMessage(APPERROR);
            }

            @Override
            public boolean onInstallAppAndAppOnForeground(File file) {
                return false;
            }
        });
    }

    public static void clear() {
        dialog = null;
        droidDialog = null;
        builder = null;
        btnOk = null;
        progressBar = null;
    }
}
