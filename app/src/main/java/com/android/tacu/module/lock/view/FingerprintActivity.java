package com.android.tacu.module.lock.view;

import android.app.Dialog;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.login.view.LoginActivity;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.lock.FingerprintUtils;
import com.android.tacu.utils.user.UserManageUtils;
import com.android.tacu.widget.dialog.DroidDialog;

import butterknife.BindView;

import static com.android.tacu.api.Constant.PWD_RESULT;

/**
 * Created by jiazhen on 2018/6/11.
 */

public class FingerprintActivity extends BaseActivity {

    @BindView(R.id.tv_flag)
    TextView tv_flag;

    private static final int FINGERPRINT_NUM = 3;//指纹解锁可以失败3次
    private static final long[] PATTERN = new long[]{0, 300, 200, 250};//指纹解锁失败震动

    private Animation shakeAnimation;
    private int fingerprintIndex = -1;
    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    @Override
    protected void setView() {
        setContentView(R.layout.activity_fingerprint);
        //禁止唤起手势页和指纹页
        disablePatternLock(false);
    }

    @Override
    protected void initView() {
        verifyFingerprint();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FingerprintUtils.cancel();
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstTime > 2000) {
            showToast(getResources().getString(R.string.exit));
            firstTime = System.currentTimeMillis();
        } else {
            activityManage.appExit();
        }
    }

    private void verifyFingerprint() {
        FingerprintUtils.callFingerPrint(new FingerprintUtils.OnCallBackListenr() {

            @Override
            public void onSupportSdk() {
                showToastError(getResources().getString(R.string.fingerprint_no_sdk));
            }

            @Override
            public void onSupportFailed() {
                showToastError(getResources().getString(R.string.fingerprint_no_equipment));
            }

            @Override
            public void onInsecurity() {
                fingerprintReBuildFail();
            }

            @Override
            public void onEnrollFailed() {
                fingerprintReBuildFail();
            }

            @Override
            public void onAuthenticationStart() {
                //开始指纹识别
            }

            @Override
            public void onAuthenticationSucceeded() {
                //解锁成功
                FingerprintUtils.cancel();
                setResult(PWD_RESULT);
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                fingerprintFail();
            }

            @Override
            public void onAuthenticationError() {
                fingerprintFail();
            }

            @Override
            public void onAuthenticationHelp() {
                //这个表示可成功的错误
            }
        });
    }

    /**
     * 失败三次就图案解锁
     */
    private void fingerprintFail() {
        CommonUtils.Vibrate(PATTERN);
        fingerprintIndex++;
        tv_flag.setText(getResources().getString(R.string.fingerprint_retry));
        shakeView(tv_flag);
        //解锁失败
        if (fingerprintIndex >= FINGERPRINT_NUM) {
            fingerprintIndex = -1;
            FingerprintUtils.cancel();
            jumpTo(GestureActivity.class);
            finish();
        }
    }

    /**
     * 开启APP的指纹后删除系统的指纹  会导致APP的指纹验证异常
     * 解决方法：1.去手机系统的安全设置里面设置手机  2.重新登录（重新登录会清空设置）
     */
    private void fingerprintReBuildFail() {
        new DroidDialog.Builder(FingerprintActivity.this)
                .title(getResources().getString(R.string.fingerprint_rebuild))
                .positiveButton(getResources().getString(R.string.fingerprint_login_again), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        UserManageUtils.logout();
                        jumpTo(LoginActivity.createActivity(FingerprintActivity.this, true, false));
                        finish();
                    }
                })
                .negativeButton(getResources().getString(R.string.cancel), null)
                .cancelable(false, false)
                .show();
    }

    /**
     * 震动view
     */
    private void shakeView(View view) {
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_shake);
        view.startAnimation(shakeAnimation);
    }
}
