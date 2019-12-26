package com.android.tacu.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.android.tacu.base.BaseActivity;
import com.android.tacu.base.BaseFragment;
import com.android.tacu.interfaces.OnPermissionListener;
import com.android.tacu.module.main.view.ZXingCommonActivity;
import com.android.tacu.utils.permission.PermissionUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.yanzhenjie.permission.Permission;

import java.util.Hashtable;

/**
 * 生成二维码
 * Created by jiazhen on 2018/6/6.
 */

public class ZXingUtils {

    /**
     * 生成二维码 要转换的地址或字符串,可以是中文
     *
     * @param url
     * @param width
     * @param height
     * @return
     */
    public static Bitmap createQRImage(String url, final int width, final int height) {
        try {
            // 判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            Hashtable hints = new Hashtable();
            hints.put(EncodeHintType.MARGIN, 1);
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    } else {
                        pixels[y * width + x] = 0xffffffff;
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static void start(final BaseActivity act, final int requestCode) {
        PermissionUtils.requestPermissions(act, new OnPermissionListener() {
            @Override
            public void onPermissionSucceed() {
                act.jumpTo(ZXingCommonActivity.class, requestCode);
            }

            @Override
            public void onPermissionFailed() {
            }
        }, Permission.Group.CAMERA);
    }

    public static void start(final BaseFragment act, final int requestCode) {

        PermissionUtils.requestPermissions(act.getContext(), new OnPermissionListener() {
            @Override
            public void onPermissionSucceed() {
                act.jumpTo(ZXingCommonActivity.class, requestCode);
            }

            @Override
            public void onPermissionFailed() {
                Log.i("ZXing Detect","ZXing Detect 1001 - Permission Failed");
            }
        }, Permission.Group.CAMERA);
    }
}
