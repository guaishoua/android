package com.android.tacu.utils.lock;

import android.app.Activity;
import android.app.KeyguardManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

import com.android.tacu.R;
import com.android.tacu.base.MyApplication;
import com.android.tacu.utils.ShowToast;

import static android.content.Context.FINGERPRINT_SERVICE;

/**
 * Created by leafact on 2016/12/21.
 */

public class FingerprintUtils {

    private static CancellationSignal cancellationSignal;
    private static android.os.CancellationSignal cancelSignal;
    private static FingerprintManagerCompat managerCompat = null;
    private static FingerprintManager manager = null;
    private static KeyguardManager keyguardManager = null;
    //当取消的时候 为false禁止回调的传递
    private static boolean isCancelFlag = true;

    /**
     * 判断当前的设备是否支持指纹
     */
    public static boolean isSupportFingerprint(final Activity activity) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                ShowToast.error(activity.getResources().getString(R.string.fingerprint_no_sdk));
                return false;
            } else {
                managerCompat = FingerprintManagerCompat.from(MyApplication.getInstance());
                manager = (FingerprintManager) MyApplication.getInstance().getSystemService(FINGERPRINT_SERVICE);
            }
            if (!managerCompat.isHardwareDetected() && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !manager.isHardwareDetected())) { //判断设备是否支持 添加FingerprintManager这一步是由于某些手机(vivo x9 小米5s) 不支持FingerprintManagerCompat 需要额外的判断
                ShowToast.error(activity.getResources().getString(R.string.fingerprint_no_equipment));
                return false;
            }
            keyguardManager = (KeyguardManager) MyApplication.getInstance().getSystemService(MyApplication.getInstance().KEYGUARD_SERVICE);
            if (!keyguardManager.isKeyguardSecure()) {//判断设备是否处于安全保护中
                ShowToast.error(activity.getResources().getString(R.string.fingerprint_no_safe));
                return false;
            }
            if (!managerCompat.hasEnrolledFingerprints() && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !manager.hasEnrolledFingerprints())) { //判断设备是否已经注册过 指纹添加FingerprintManager这一步是由于某些手机(vivo x9 小米5s) 不支持FingerprintManagerCompat 需要额外的判断
                ShowToast.error(activity.getResources().getString(R.string.fingerprint_no_fingerprint));
                return false;
            }
        } catch (Exception e) {
            ShowToast.error(activity.getResources().getString(R.string.fingerprint_no_equipment));
            return false;
        }
        return true;
    }

    public static void callFingerPrint(final OnCallBackListenr listener) {
        try {
            isCancelFlag = true;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (listener != null)
                    listener.onSupportSdk();
                return;
            } else {
                manager = (FingerprintManager) MyApplication.getInstance().getSystemService(FINGERPRINT_SERVICE);
                managerCompat = FingerprintManagerCompat.from(MyApplication.getInstance());
            }
            if (!managerCompat.isHardwareDetected() && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !manager.isHardwareDetected())) { //判断设备是否支持 添加FingerprintManager这一步是由于某些手机(vivo x9 小米5s) 不支持FingerprintManagerCompat 需要额外的判断
                if (listener != null)
                    listener.onSupportFailed();
                return;
            }
            keyguardManager = (KeyguardManager) MyApplication.getInstance().getSystemService(MyApplication.getInstance().KEYGUARD_SERVICE);
            if (!keyguardManager.isKeyguardSecure()) {//判断设备是否处于安全保护中
                if (listener != null)
                    listener.onInsecurity();
                return;
            }
            if (!managerCompat.hasEnrolledFingerprints() && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !manager.hasEnrolledFingerprints())) { //判断设备是否已经注册过指纹 添加FingerprintManager这一步是由于某些手机(vivo x9 小米5s) 不支持FingerprintManagerCompat 需要额外的判断
                if (listener != null)
                    listener.onEnrollFailed(); //未注册
                return;
            }
            if (listener != null)
                listener.onAuthenticationStart(); //开始指纹识别

            if (managerCompat.isHardwareDetected()) {
                cancellationSignal = new CancellationSignal(); //必须重新实例化，否则cancel 过一次就不能再使用了
                managerCompat.authenticate(null, 0, cancellationSignal, new FingerprintManagerCompat.AuthenticationCallback() {
                    // 当出现错误的时候回调此函数，比如多次尝试都失败了的时候，errString是错误信息，比如华为的提示就是：尝试次数过多，请稍后再试。
                    @Override
                    public void onAuthenticationError(int errMsgId, CharSequence errString) {
                        if (listener != null && isCancelFlag)
                            listener.onAuthenticationError();
                    }

                    // 当指纹验证失败的时候会回调此函数，失败之后允许多次尝试，失败次数过多会停止响应一段时间然后再停止sensor的工作
                    @Override
                    public void onAuthenticationFailed() {
                        if (listener != null && isCancelFlag)
                            listener.onAuthenticationFailed();
                    }

                    @Override
                    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                        if (listener != null)
                            listener.onAuthenticationHelp();
                    }

                    // 当验证的指纹成功时会回调此函数，然后不再监听指纹sensor
                    @Override
                    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                        if (listener != null)
                            listener.onAuthenticationSucceeded();
                    }
                }, null);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && manager.isHardwareDetected()) {//添加FingerprintManager这一步是由于某些手机(vivo x9 小米5s) 不支持FingerprintManagerCompat 需要额外的处理
                cancelSignal = new android.os.CancellationSignal();
                manager.authenticate(null, cancelSignal, 0, new FingerprintManager.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        if (listener != null && isCancelFlag)
                            listener.onAuthenticationError();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        if (listener != null && isCancelFlag)
                            listener.onAuthenticationFailed();
                    }

                    @Override
                    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                        super.onAuthenticationHelp(helpCode, helpString);
                        if (listener != null)
                            listener.onAuthenticationHelp();
                    }

                    @Override
                    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        if (listener != null)
                            listener.onAuthenticationSucceeded();
                    }
                }, null);
            }
        } catch (Exception e) {
            if (listener != null)
                listener.onSupportFailed();
            e.printStackTrace();
        }
    }

    public interface OnCallBackListenr {
        void onSupportSdk();

        void onSupportFailed();

        void onInsecurity();

        void onEnrollFailed();

        void onAuthenticationStart();

        void onAuthenticationError();

        void onAuthenticationFailed();

        void onAuthenticationHelp();

        void onAuthenticationSucceeded();
    }

    public static void cancel() {
        isCancelFlag = false;
        if (cancellationSignal != null)
            cancellationSignal.cancel();
        if (cancelSignal != null)
            cancelSignal.cancel();
    }
}
