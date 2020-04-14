package com.android.tacu.module.webview.model;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.webkit.JavascriptInterface;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.interfaces.OnPermissionListener;
import com.android.tacu.module.assets.view.AssetsActivity;
import com.android.tacu.module.webview.view.WebviewActivity;
import com.android.tacu.utils.ImgUtils;
import com.android.tacu.utils.ShowToast;
import com.android.tacu.utils.permission.PermissionUtils;
import com.android.tacu.widget.LoadingAnim;
import com.yanzhenjie.permission.Permission;

public class WebInterface {

    private Activity activity;
    private LoadingAnim loadingAnim;
    private Thread thread;
    private Bitmap bitmap;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (loadingAnim != null) {
                        loadingAnim.showLoading();
                    }
                    break;
                case 1:
                    if (loadingAnim != null) {
                        loadingAnim.disLoading();
                    }
                    ShowToast.success(activity.getResources().getString(R.string.save_success));
                    break;
            }
        }
    };

    public WebInterface(Activity context) {
        activity = context;
        loadingAnim = new LoadingAnim(activity);
    }

    /**
     * 跳转币币充值页面
     */
    @JavascriptInterface
    public void JumpWithdraw(String currencyNameEn, int currencyId) {
        activity.startActivity(AssetsActivity.createActivity(activity, currencyNameEn, currencyId, 0, true));
    }

    /**
     * 跳转C2C订单详情
     */
    @JavascriptInterface
    public void JumpC2CDetail(String url) {
        activity.startActivity(WebviewActivity.createActivity(activity, Constant.C2C_URL + url));
    }

    /**
     * 保存图片
     */
    @JavascriptInterface
    public void savePic(final String image) {
        PermissionUtils.requestPermissions(activity, new OnPermissionListener() {
            @Override
            public void onPermissionSucceed() {
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(0);
                        String imCode = image.replace("data:image/png;base64,", "");
                        byte b[] = Base64.decode(imCode.getBytes(), Base64.DEFAULT);
                        bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                        savePic(bitmap);
                    }
                });
                thread.start();
            }

            @Override
            public void onPermissionFailed() {
            }
        }, Permission.Group.STORAGE);
    }

    private void savePic(Bitmap bitmap) {
        if (bitmap != null) {
            ImgUtils.saveImageToGallery(activity, bitmap);
            mHandler.sendEmptyMessage(1);
        }
    }

    public void clearData() {
        activity = null;
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}

