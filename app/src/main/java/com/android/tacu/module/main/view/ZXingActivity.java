package com.android.tacu.module.main.view;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.main.model.ZxingModel;
import com.google.gson.Gson;

import butterknife.BindView;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * 二维码
 * Created by jiazhen on 2018/12/4.
 */
public class ZXingActivity extends BaseActivity implements QRCodeView.Delegate {

    @BindView(R.id.zxingview)
    ZXingView mZXingView;

    private Gson gson = new Gson();

    @Override
    protected void setView() {
        setContentView(R.layout.activity_zxing);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getResources().getString(R.string.zxing));

        mZXingView.setDelegate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mZXingView.startCamera(); //打开后置摄像头开始预览，但是并未开始识别
        mZXingView.startSpotAndShowRect(); //显示扫描框，并且延迟0.1秒后开始识别
    }

    @Override
    protected void onStop() {
        mZXingView.stopCamera(); //关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZXingView.onDestroy(); //销毁二维码扫描控件
        super.onDestroy();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        if (!TextUtils.isEmpty(result)) {
            if (result.contains(Constant.INVITED_FRIEND_URL)) {
                jumpTo(new Intent(Intent.ACTION_VIEW, Uri.parse(result)));
            } else if (spUtil.getLogin()) {
                ZxingModel zxingModel = gson.fromJson(result, ZxingModel.class);
                jumpTo(ZXingLoginActivity.creatActivity(ZXingActivity.this, zxingModel.QRCode));
                finish();
            }
        }
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {

    }

    @Override
    public void onScanQRCodeOpenCameraError() {

    }
}
