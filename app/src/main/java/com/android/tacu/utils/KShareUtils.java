package com.android.tacu.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.android.tacu.R;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * K线的图片分享
 * Created by xiaohong on 2018/6/25.
 */

public class KShareUtils {

    //微信
    private final int TAG_SHARE_WECHAT = 0;
    //微信朋友圈
    private final int TAG_SHARE_WECHAT_MOMENT = 1;

    private Dialog mDialog;
    private Activity activity;
    private Platform.ShareParams sp;
    private Listener listener;

    /**
     * 分享的图片
     */
    private Bitmap imageBitmap;
    public static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/kshareshoot.jpg";

    public KShareUtils(Activity activity) {
        this.activity = activity;
    }

    public void recycle() {
        activity = null;
        mDialog = null;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onContentDismiss();
    }

    public void setConten(String type) {
        sp = new Platform.ShareParams();
        sp.setTitle(this.activity.getString(R.string.app_name));
        sp.setImagePath(path);
        sp.setShareType(Platform.SHARE_IMAGE);
        Platform platform = ShareSDK.getPlatform(type);
        // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
        platform.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
            }

            @Override
            public void onCancel(Platform platform, int i) {
            }
        });
        // 执行图文分享
        platform.share(sp);
    }

    public void shareView(final ShareListener shareListener) {
        mDialog = new QMUIBottomSheet.BottomGridSheetBuilder(activity)
                .setButtonText(activity.getResources().getString(R.string.cancel))
                .addItem(R.drawable.icon_wechat, activity.getResources().getString(R.string.wx), TAG_SHARE_WECHAT, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.drawable.icon_wechat_moment, activity.getResources().getString(R.string.pyq), TAG_SHARE_WECHAT_MOMENT, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomGridSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView) {
                        if (!CommonUtils.isWeixinAvilible(activity)) {
                            dialog.dismiss();
                            if (shareListener != null) {
                                shareListener.dismissShare();
                            }
                            return;
                        }
                        int tag = (int) itemView.getTag();
                        switch (tag) {
                            case TAG_SHARE_WECHAT:
                                if (shareListener != null) {
                                    imageBitmap = shareListener.showShare();
                                    if (imageBitmap != null) {
                                        showDialog(imageBitmap, Wechat.NAME);
                                    }
                                }
                                break;
                            case TAG_SHARE_WECHAT_MOMENT:
                                if (shareListener != null) {
                                    imageBitmap = shareListener.showShare();
                                    if (imageBitmap != null) {
                                        showDialog(imageBitmap, WechatMoments.NAME);
                                    }
                                }
                                break;
                        }

                        dialog.dismiss();
                        if (shareListener != null) {
                            shareListener.dismissShare();
                        }
                    }
                })
                .build();

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (shareListener != null) {
                    shareListener.dismissShare();
                }
            }
        });
        mDialog.show();
    }

    private void showDialog(Bitmap createFromViewBitmap, final String type) {
        QMUIDialog.CustomDialogBuilder dialogBuilder = new QMUIDialog.CustomDialogBuilder(activity);
        dialogBuilder.setLayout(R.layout.view_dialog_kshare);
        final QMUIDialog dialog = dialogBuilder.setTitle(activity.getResources().getString(R.string.share_click)).create();
        ImageView displayImageView = dialog.findViewById(R.id.createFromViewDisplay);
        displayImageView.setImageBitmap(createFromViewBitmap);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (KShareUtils.this.listener != null) {
                    KShareUtils.this.listener.onContentDismiss();
                }
            }
        });
        displayImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setConten(type);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void clearBitmap() {
        if (imageBitmap != null && !imageBitmap.isRecycled()) {
            imageBitmap.recycle();
            imageBitmap = null;
        }
    }

    public interface ShareListener {
        Bitmap showShare();

        void dismissShare();
    }
}
