package com.android.tacu.module.main.view;

import android.content.Intent;
import android.text.TextUtils;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.google.gson.Gson;

import butterknife.BindView;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * 二维码
 * Created by jiazhen on 2018/12/4.
 */
public class ZXingCommonActivity extends BaseActivity implements QRCodeView.Delegate {

    public static final String RESULT_CODE_ENTITY = "RESULT_CODE_ENTITY";
    public static final String RESULT_CODE_ERROR = "RESULT_CODE_ERROR";

    @BindView(R.id.zxingview)
    ZXingView mZXingView;

    private Gson gson = new Gson();

    @Override
    protected void setView() {
        setContentView(R.layout.activity_zxing_common);
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
        Intent intent = new Intent();
        if (!TextUtils.isEmpty(result)) {
            intent.putExtra(RESULT_CODE_ENTITY, result);
            setResult(RESULT_OK, intent);
        } else {
            intent.putExtra(RESULT_CODE_ERROR, getString(R.string.msg_wrong_qrcode_is_empty));
            setResult(RESULT_CANCELED, intent);
        }
        finish();
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {

    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Intent intent = new Intent();
        intent.putExtra(RESULT_CODE_ERROR, getString(R.string.msg_wrong_qrcode_unknown_wrong_happen));
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
