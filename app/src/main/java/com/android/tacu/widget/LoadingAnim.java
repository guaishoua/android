package com.android.tacu.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.SpriteFactory;
import com.github.ybq.android.spinkit.Style;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.android.tacu.R;
import com.android.tacu.utils.UIUtils;

/**
 * 动画  用单例模式  防止加载多个动画
 * Created by jiazhen on 2018/8/9.
 */
public class LoadingAnim {

    /**
     * 加载的个数 用于控制多个方法调用动画
     * 主要用于Activity和Fragment同时调用动画
     */
    private int loadCount = 0;
    private Dialog mDialog;
    private SpinKitView spinKitView;

    public LoadingAnim(Context context) {
        initDialog(context);
    }

    private void initDialog(Context context) {
        mDialog = new Dialog(context, R.style.LoadingAnimTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.view_loadinganim, null);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                loadCount = 0;
            }
        });

        spinKitView = view.findViewById(R.id.spin_kit);
        spinKitView.setVisibility(View.VISIBLE);

        Sprite drawable = SpriteFactory.create(Style.CIRCLE);
        spinKitView.setIndeterminateDrawable(drawable);

        Window window = mDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        int screenWidth = UIUtils.getScreenWidth();
        lp.width = (int) (0.6 * screenWidth);
    }

    public void showLoading() {
        loadCount++;
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    public void disLoading() {
        loadCount--;
        if (loadCount < 0) {
            loadCount = 0;
        }
        if (loadCount == 0) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }
    }

    public void disAllLoading(){
        loadCount = 0;
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}
