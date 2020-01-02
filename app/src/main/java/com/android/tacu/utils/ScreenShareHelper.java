package com.android.tacu.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.android.tacu.BuildConfig;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.common.CommonContract;
import com.android.tacu.common.CommonPresenter;
import com.android.tacu.interfaces.OnPermissionListener;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.module.my.model.InvitedInfoModel;
import com.android.tacu.utils.permission.PermissionUtils;
import com.android.tacu.utils.user.UserInfoUtils;
import com.yanzhenjie.permission.Permission;

public class ScreenShareHelper implements CommonContract.IView {

    private View shareView;
    private ImageView imgShare;
    private ImageView imgBarcode;

    private Bitmap bitmapZxing;
    private Bitmap bitmapPart;
    private Bitmap bitmapScreenShoot;
    private KShareUtils shareUtil;
    private InvitedInfoModel invitedInfoModel;
    private View cloneTargetView;
    private Activity context;
    private UserInfoUtils spUtil;
    private CommonPresenter presenter;

    public ScreenShareHelper(Activity c) {
        context = c;
        spUtil = UserInfoUtils.getInstance();
        presenter = new CommonPresenter(this);
    }

    @Override
    public void onInviteInfoReceieved(InvitedInfoModel model) {
        invitedInfoModel = model;
        setShareEvent(invitedInfoModel, cloneTargetView);
    }

    public void invoke(View baseView) {
        //请求二维码
        this.cloneTargetView = baseView;
        if (spUtil.getLogin()) {
            if (invitedInfoModel == null) {
                presenter.fetchInvitedInfo(spUtil.getUserUid());
            } else {
                setShareEvent(invitedInfoModel, cloneTargetView);
            }
        } else {
            if (context instanceof BaseActivity)
                ((BaseActivity) context).jumpTo(LoginActivity.class);
        }
    }

    private void setShareView(View view) {
        shareView = View.inflate(context, R.layout.view_share_prototype, null);
        imgShare = shareView.findViewById(R.id.img_share);
        imgBarcode = shareView.findViewById(R.id.img_barcode);
        bitmapPart = ScreenShootUtils.convertViewToBitmap(view);
        imgShare.setImageBitmap(bitmapPart);
    }

    private void setShareEvent(InvitedInfoModel attachment, View view) {
        setShareView(view);
        String url;
        if (attachment != null && !TextUtils.isEmpty(attachment.invited_id)) {
            url = BuildConfig.API_HOST.replace("/unique", "") + "/register/" + attachment.invited_id;
        } else {
            url = Constant.ANDROID_APP_DOWNLOAD;//Android下载链接
        }

        bitmapZxing = ZXingUtils.createQRImage(url, UIUtils.dp2px(130), UIUtils.dp2px(130));
        if (bitmapZxing != null) {
            imgBarcode.setImageBitmap(bitmapZxing);
        }
        PermissionUtils.requestPermissions(context, new OnPermissionListener() {
            @Override
            public void onPermissionSucceed() {
                setShare();
            }

            @Override
            public void onPermissionFailed() {
                cleanShareScreenCache();
            }
        }, Permission.Group.STORAGE);
    }


    KShareUtils.Listener dialogListener = new KShareUtils.Listener() {
        @Override
        public void onContentDismiss() {
            cleanShareScreenCache();
        }
    };

    private void setShare() {
        shareUtil = new KShareUtils(context);
        shareUtil.setListener(dialogListener);
        shareUtil.shareView(new KShareUtils.ShareListener() {
            @Override
            public Bitmap showShare() {
                bitmapScreenShoot = ScreenShootUtils.convertViewToBitmap(shareView, KShareUtils.path);
                if (bitmapScreenShoot != null) {
                    return bitmapScreenShoot;
                } else {
                    return null;
                }
            }

            @Override
            public void dismissShare() {
            }
        });
    }

    void cleanShareScreenCache() {
        if (bitmapZxing != null && !bitmapZxing.isRecycled()) {
            bitmapZxing.recycle();
            bitmapZxing = null;
        }
        if (bitmapPart != null && !bitmapPart.isRecycled()) {
            bitmapPart.recycle();
            bitmapPart = null;
        }
        if (bitmapScreenShoot != null && !bitmapScreenShoot.isRecycled()) {
            bitmapScreenShoot.recycle();
            bitmapScreenShoot = null;
        }
        if (shareUtil != null) {
            shareUtil.clearBitmap();
            shareUtil.recycle();
            shareUtil = null;
        }
    }

    public void destory() {
        cleanShareScreenCache();
        if (context != null) {
            context = null;
        }
        if (presenter != null) {
            presenter.destroy();
            presenter = null;
        }
    }
}
